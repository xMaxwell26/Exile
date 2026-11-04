package com.koios.game.content.minigames.misc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.impl.misc.TzhaarDialogue;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.content.minigames.MinigameMob;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Tzhaar fight cave minigame controller.
 *
 * @author Gabriel | Wolfs Darker
 */
public class TzhaarFightCave extends Minigame {

	private boolean isOver;
	private boolean kiln = false, login = false;
	private final List<Integer> mob_list = new CopyOnWriteArrayList<Integer>();
	private final Player player;
	private final int[][] waves;

	public TzhaarFightCave(Player player, boolean kiln, boolean login) {
		super(3);
		this.player = player;
		this.kiln = kiln;
		this.login = login;
		waves = kiln ? TzhaarFightCaveConstants.KILN_WAVES : TzhaarFightCaveConstants.WAVES;
		isOver = false;
	}

	@Override
	public boolean canAttack() {
		return true;
	}

	@Override
	public boolean canLogout() {
		return true;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canUsePrayer() {
		return true;
	}

	@Override
	public void damageHook(EntityCharacter attacker, EntityCharacter target, int damage) {
	}

	@Override
	protected void execute() {
		if (player == null || player.isDisconnected() || !player.getMinigame().inMinigame() || player.isDead() || player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			stop();
			removeNpcs();
			return;
		}

		if (mob_list.size() <= 0) {
			return;
		}

		for (int i = 0; i < mob_list.size(); i++) {
			Mob mob = MobManager.mobs[mob_list.get(i)];

			if (mob == null || mob.getLocation().getZ() != player.getLocation().getZ()) {
				mob_list.remove(i);
				continue;
			}

			if (mob.isDead() || mob.getHitpoints() <= 0) {
				mob_list.remove(i);
			}
		}

		if (mob_list.size() <= 0) {
			if (player.getMinigame().getTzhaarWave() >= waves.length - 1) {
				removePlayer(player);
			} else {
				nextWave(null);
			}
		}

	}

	@Override
	public Location getExitLocation() {
		return new Location(2438, 5168, 0);
	}

	@Override
	public MinigameLobby getLobby() {
		return null;
	}

	@Override
	public String getMinigameName() {
		return TzhaarFightCaveConstants.MINIGAME_NAME;
	}

	@Override
	public Map<String, Mob> getMobList() {
		return null;
	}

	@Override
	public Map<Player, String> getPlayerList() {
		return null;
	}

	@Override
	public Location getRespawnLocation() {
		return new Location(2438, 5168, 0);
	}

	@Override
	public Location getSpawnLocation() {
		return new Location(2413, 5117, player.getIndex() * 4);
	}

	@Override
	public boolean handleButtonClick(Player player, int buttonId) {
		return false;
	}

	@Override
	public void handleDeath(Player player) {
		isOver = true;
		removeNpcs();
		player.getMinigame().setInKiln(false);
		player.getMinigame().resetTzhaarVariables();
		player.getMinigame().resetMinigame();
		player.setPosition(new Location(2438, 5168, 0));
		stop();
	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleLogout(Player player) {
		isOver = true;
		player.getMinigame().setInKiln(kiln);
		removeNpcs();
		stop();
	}

	@Override
	public boolean handleMobClick(Player player, Mob mob) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, WorldObject object) {
		if (object.getID() == 9357) {
			removeNpcs();
			isOver = true;
			player.setPosition(getExitLocation());
			player.getMinigame().resetTzhaarVariables();
			player.getMinigame().resetMinigame();
		}

		return true;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean isOver() {
		return isOver;
	}

	@Override
	public void joinMinigame(Player player) {
		startMinigame();
	}

	@Override
	public void nextWave(Object[] objects) {
		if (player == null || player.isDisconnected() || !player.getMinigame().inMinigame() || player.isDead() || player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			return;
		}

		if (mob_list.size() > 0) {
			return;
		}

		player.getMinigame().increaseTzhaarWave(1);

		if (!GameService.isOnlineMode()) {
			// player.getMinigame().setTzhaarWave(waves.length - 1);
		}

		player.sendMessage("[ <col=a90909>Tzhaar</col> ] Starting wave <col=a90909>" + player.getMinigame().getTzhaarWave() + "</col>...");

		GameService.getTaskScheduler().schedule(new Task(9) {

			@Override
			public void execute() {

				for (int npc : waves[player.getMinigame().getTzhaarWave()]) {
					spawnNpc(npc);
				}

				stop();
			}

		});
	}

	public void removeNpcs() {
		for (int index : mob_list) {
			Mob mob = MobManager.mobs[index];

			if (mob != null && TzhaarFightCaveConstants.isTzhaarNpc(mob.getId()) && mob.getLocation().getZ() == player.getIndex() * 4) {
				mob.remove();
			}
		}

		mob_list.clear();
	}

	@Override
	public void removePlayer(Player player) {

		if (player == null || player.isDisconnected() || !player.getMinigame().inMinigame()) {
			return;
		}

		removeNpcs();
		isOver = true;
		player.getInventory().add(new Item(6529, NumberUtil.random(3000) + 3000));
		player.sendMessage("You completeed the " + (kiln ? "Kiln fight" : "TzHaar fight") + " caves!");

		if (player.getInventory().add(new Item(kiln ? 19111 : 6570))) {
			if (player.getInventory().add(new Item(995, 1_000_000 * NumberUtil.random(1, 4)))) {
			} else {
				Bank.addToBank(player, new Item(995, 1_000_000 * NumberUtil.random(1, 4)));
				player.sendMessage("You have no space in your inventory, check your bank for reward.");
			}
		} else {
			Bank.addToBank(player, new Item(995, 1_000_000 * NumberUtil.random(1, 4)));
			Bank.addToBank(player, new Item(kiln ? 19111 : 6570));
			player.sendMessage("You have no space in your inventory, check your bank for reward.");
		}

		AchievementManager.performed(player, Achievement.DEFEAT_JAD);
		player.setPosition(getExitLocation());
		player.getMinigame().resetMinigame();
		player.getMinigame().resetTzhaarVariables();
		stop();
	}

	@Override
	public void setLobby(MinigameLobby lobby) {
	}

	/**
	 * Spawns a new npc in the cave.
	 *
	 * @param id
	 * @param z
	 */
	private void spawnNpc(int id) {

		Location loc = TzhaarFightCaveConstants.SPAWN_LOCATION.transform(NumberUtil.random(4), NumberUtil.random(4), player.getZ());

		if (loc.getZ() != player.getZ()) {
			loc.setAs(Location.create(loc.getX(), loc.getY(), player.getLocation().getZ()));
		}

		Mob mob = MinigameMob.spawnMob(id, loc, new int[] { TzhaarFightCaveConstants.getAttack(id), TzhaarFightCaveConstants.getDefence(id),
				TzhaarFightCaveConstants.getHitpoints(id), TzhaarFightCaveConstants.getMaxHit(id) }, this);

		if (mob == null) {
			removeNpcs();
			player.setPosition(getExitLocation());
			player.getMinigame().resetTzhaarVariables();
			player.getMinigame().resetMinigame();
			player.sendMessage("[ ERROR ] Post this on forums! Message: NO MOB SLOTS AVALIABLE [  Tzhaar Fight Caves ].");
			return;
		}

		mob.summoner = player;
		mob_list.add(mob.getIndex());
		mob.getCombat().setTarget(player);
		mob.getFollowing().setFollow(player, FollowType.COMBAT, true);
	}

	@Override
	public void startMinigame() {
		if (!login) {
			player.getMinigame().resetTzhaarVariables();
			player.dialogue().start(new TzhaarDialogue(player));
			player.sendMessage(kiln ? "@dre@Good luck completing the fight kiln! There are 10 waves if you defeat all waves"
					: "@dre@Good luck completing the fight caves, there are 15 waves if you defeat all waves");
			player.sendMessage(kiln ? "@dre@you will be rewarded the TokHaar-Kal!" : "@dre@you will be rewarded a fire cape!");
		}

		player.teleport(new Location(2413, 5117, player.getIndex() * 4));
		player.sendMessage("Get ready! The next wave will start shortly.");

		GameService.getTaskScheduler().schedule(new Task(9) {

			@Override
			public void execute() {
				if (!player.getMinigame().inMinigame() || player.isDisconnected()) {
					stop();
					return;
				}

				mob_list.clear();
				player.getMinigame().increaseTzhaarWave(-1);
				nextWave(null);
				stop();
			}

		});

		GameService.getTaskScheduler().schedule(this);
	}

}
