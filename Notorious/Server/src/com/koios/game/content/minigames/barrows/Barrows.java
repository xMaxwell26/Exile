package com.koios.game.content.minigames.barrows;

import com.koios.GameService;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.impl.misc.BarrowsDialogue;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.DefaultMinigame;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameMob;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendEntityHintIconPacket;
import com.koios.network.out.impl.SendShakeScreen;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class Barrows extends BarrowsData {

	private static Minigame minigame = new DefaultMinigame();

	/**
	 * Handles the spade digging into the tombs.
	 */
	public static void handleDigging(Player player) {

		player.playAnimation(new Animation(831));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				if (BarrowsData.digSpaceIndex(player) >= 0) {
					updateInterface(player);
					player.setPosition(BarrowsData.DIG_POSITION[BarrowsData.digSpaceIndex(player)]);
				}

				player.playAnimation(new Animation(65535));
				stop();
			}

		});
	}

	/**
	 * Handle the object interactions.
	 */
	public static void handleObjectClick(Player player, int objectId, Location objLoc) {
		if (spawnCoffin(player, objectId)) {
			return;
		}

		switch (objectId) {
		case 6702:
			player.setPosition(Location.create(3564, 3289));
			break;

		case 6703:
			player.setPosition(Location.create(3574, 3298));
			break;

		case 6704:
			player.setPosition(Location.create(3576, 3281));
			break;

		case 6705:
			player.setPosition(Location.create(3565, 3276));
			break;

		case 6706:
			player.setPosition(Location.create(3553, 3283));
			break;

		case 6707:
			player.setPosition(Location.create(3556, 3297));
			break;

		case 10284:
			int kc = getKillCount(player);

			if (kc == 5) {
				spawnChestBrother(player);
			} else if (kc >= 6) {
				openChest(player);
				updateInterface(player);
			} else {
				player.sendMessage("You didn't kill all the brothers.");
				return;
			}

			break;
		}
	}

	/**
	 * Open the chest and reward the player.
	 */
	public static void openChest(Player player) {
		player.resetBarrowsKillCount();

		for (int i = 0; i < player.getKilledBrothers().length; i++) {
			player.getKilledBrothers()[i] = false;
			player.getSummonedBrothers()[i] = false;
		}

		player.setHiddenBrother(-1);
		int chance = NumberUtil.random(10);
		int[] array1 = chance == 0 ? MISC_LOOT : chance == 2 ? BARROW_LOOT : chance == 4 ? NOTED_LOOT : MISC_LOOT;
		int[] array2 = chance == 0 ? BARROW_LOOT : chance == 2 ? BARROW_LOOT : chance == 4 ? BARROW_LOOT : NOTED_LOOT;
		Item item1 = getRandomLoot(array1, chance == 4 ? 1 : chance < 0 ? 0 : chance);
		Item item2 = getRandomLoot(array2, chance == 0 ? 2 : chance == 2 ? 2 : chance == 4 ? 2 : 1);

		if (!player.getInventory().add(item1)) {
			GroundItemManager.spawnNew(player, item1.getId(), player.getX(), player.getY(), item1.getCount());
		}

		if (!player.getInventory().add(item2)) {
			GroundItemManager.spawnNew(player, item2.getId(), player.getX(), player.getY(), item2.getCount());
		}

		player.getSession().send(new SendShakeScreen(2, 3, 4, 5));
	}

	/**
	 * Spawn a brother near the chest.
	 */
	public static void spawnChestBrother(Player player) {
		getRandomBrother(player);

		if (player.getSummonedBrothers()[player.getHiddenBrother()] && GameService.isOnlineMode()) {
			return;
		}

		int brotherId = BarrowsData.BROTHER_IDS[player.getHiddenBrother()];

		if (brotherId <= 0) {
			return;
		}

		player.getSummonedBrothers()[player.getHiddenBrother()] = true;

		Mob mob = MinigameMob.spawnMob(brotherId, player.getLocation().transform(1, 0, 0), new int[] { 400, 400, 120, 25 }, minigame);

		if (mob == null) {
			player.getSummonedBrothers()[player.getHiddenBrother()] = false;
			return;
		}

		player.getSession().send(new SendEntityHintIconPacket(mob));
		mob.summoner = player;
		mob.speak("You dare to steal from us!");
		mob.getCombat().setTarget(player);
		mob.getFollowing().setFollow(player, FollowType.COMBAT);
	}

	/**
	 * Attempt to spawn a barrows brother next to its coffin.
	 */
	public static boolean spawnCoffin(Player player, int objectId) {
		int index = -1;

		for (int i = 0; i < BarrowsData.COFFIN_IDS.length; i++) {
			if (objectId == BarrowsData.COFFIN_IDS[i]) {
				index = i;
			}
		}

		if (index < 0) {
			return false;
		}

		if (player.getHiddenBrother() < 0) {
			getRandomBrother(player);
		}

		if (player.getHiddenBrother() == index) {
			player.dialogue().start(new BarrowsDialogue(player));
			return false;
		}

		if (player.getKilledBrothers()[index] || player.getSummonedBrothers()[index]) {
			SimpleDialogues.sendStatement(player, "You have already defeated " + BarrowsData.BROTHER_NAME[index] + ".");
			return false;
		}

		player.getSummonedBrothers()[index] = true;

		Mob mob = MinigameMob.spawnMob(BarrowsData.BROTHER_IDS[index], player.getLocation().transform(1, 0, 0), new int[] { 400, 400, 120, 25 }, minigame);

		if (mob == null) {
			player.getSummonedBrothers()[player.getHiddenBrother()] = false;
			return false;
		}

		player.getSession().send(new SendEntityHintIconPacket(mob));
		mob.summoner = player;
		mob.speak("You dare to steal from us!");
		mob.getCombat().setTarget(player);
		mob.getFollowing().setFollow(player, FollowType.COMBAT);
		return true;
	}

	/**
	 * Updates the walkable interface inside tombs and tunnels.
	 */
	public static void updateInterface(Player p) {
		int kc = getKillCount(p);
		p.getSession().send(new SendStringPacket(kc + "", 16119));

		for (int i = 0; i < p.getKilledBrothers().length; i++) {
			p.getSession().send(new SendStringPacket(p.getKilledBrothers()[i] ? BarrowsData.BROTHER_NAME[i] : "", 16112 + i));
		}
	}

	/**
	 * Handle the barrow's brother death.
	 */
	public static void handleBrotherDeath(Player p, Mob brother) {
		for (int i = 0; i < BarrowsData.BROTHER_IDS.length; i++) {
			if (brother.getId() == BarrowsData.BROTHER_IDS[i]) {
				p.getKilledBrothers()[i] = true;
				AchievementManager.performed(p, BarrowsData.BROTHER_ACHIEVEMENTS[i]);
				updateInterface(p);
				break;
			}
		}
	}

	/**
	 * Grabs the player's killcount.
	 */
	public static int getKillCount(Player p) {
		int kc = 0;

		for (int i = 0; i < p.getKilledBrothers().length; i++) {
			if (p.getKilledBrothers()[i]) {
				kc++;
			}
		}

		return kc;
	}

	/**
	 * Grabs the random brother.
	 */
	public static void getRandomBrother(Player player) {
		for (int i = 0; i < player.getKilledBrothers().length; i++) {
			if (player.getKilledBrothers()[i] && player.getHiddenBrother() == i) {
				player.setHiddenBrother(-1);
			}
		}

		while (player.getHiddenBrother() < 0) {
			int randomBrother = NumberUtil.inclusiveRandom(BarrowsData.BROTHER_IDS.length - 1);

			if (!player.getKilledBrothers()[randomBrother]) {
				player.setHiddenBrother(randomBrother);
			}
		}
	}

}
