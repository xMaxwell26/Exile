package com.koios.game.content.minigames.misc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.content.minigames.MinigameMob;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.ConfigKeys;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;
import com.koios.util.ItemIdentifiers;

public class ChallengeScroll extends Minigame {

	private final Player player;
	private final List<Integer> mob_list = new CopyOnWriteArrayList<Integer>();
	private boolean login = false;
	private boolean isOver;

	public ChallengeScroll(Player player, boolean login) {
		super(3);
		this.player = player;
		this.login = login;
	}

	@Override
	public String getMinigameName() {
		return ChallengeScrollConstants.MINIGAME_NAME;
	}

	@Override
	public Map<Player, String> getPlayerList() {
		return null;
	}

	@Override
	public Map<String, Mob> getMobList() {
		return null;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canLogout() {
		return true;
	}

	@Override
	public boolean canAttack() {
		return true;
	}

	@Override
	public boolean canUsePrayer() {
		return false;
	}

	@Override
	public boolean isOver() {
		return isOver;
	}

	@Override
	public boolean handleObjectClick(Player player, WorldObject object) {
		if (object.getID() == 10553) {
			isOver = true;
			removeNpcs();
			player.getMinigame().setChallengeWave(0);
			player.setPosition(getExitLocation());
			player.getMinigame().resetMinigame();
			stop();
		}

		return true;
	}

	@Override
	public boolean handleMobClick(Player player, Mob mob) {
		return false;
	}

	@Override
	public boolean handleButtonClick(Player player, int buttonId) {
		return false;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public Location getSpawnLocation() {
		return ChallengeScrollConstants.SPAWN_LOCATION;
	}

	@Override
	public Location getRespawnLocation() {
		return ChallengeScrollConstants.RESPAWN_LOCATION;
	}

	@Override
	public Location getExitLocation() {
		return ChallengeScrollConstants.RESPAWN_LOCATION;
	}

	@Override
	public MinigameLobby getLobby() {
		return null;
	}

	@Override
	public void handleLogout(Player player) {
		removeNpcs();
		isOver = true;
		stop();
	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleDeath(Player player) {
		isOver = true;
		removeNpcs();
		player.getMinigame().setChallengeWave(0);
		player.setPosition(getExitLocation());
		player.getMinigame().resetMinigame();
		stop();
	}

	@Override
	public void startMinigame() {
		if (!login) {
			player.getMinigame().setChallengeWave(0);
		}

		player.teleport(getSpawnLocation().transform(0, 0, player.getIndex() * 4));
		player.sendMessage("[ <col=a90909>Challenge</col> ] This is a SAFE minigame, you will not lose any items upon death!");
		player.sendMessage("[ <col=a90909>Challenge</col> ] Get ready! The next wave will start shortly.");

		GameService.getTaskScheduler().schedule(new Task(9) {

			@Override
			public void execute() {
				if (!player.getMinigame().inMinigame() || player.isDisconnected()) {
					stop();
					return;
				}

				mob_list.clear();
				player.getMinigame().increaseChallengeWave(-1);
				nextWave(null);
				stop();
			}

		});

		GameService.getTaskScheduler().schedule(this);
	}

	@Override
	public void joinMinigame(Player player) {
		startMinigame();
	}

	@Override
	public void setLobby(MinigameLobby lobby) {
	}

	@Override
	public void damageHook(EntityCharacter attacker, EntityCharacter target, int damage) {
	}

	@Override
	public void removePlayer(Player player) {

		GameService.getWorld().broadcast("<img=9> @blu@News: " + player.getName() + " beat a challenge scroll fight.", p -> !p.disabledGlobalChat);

		player.slayerPoints += 25;
		player.pvpPoints += 15;
		player.getSession().send(new SendConfigPacket(ConfigKeys.SLAYER_POINTS, player.slayerPoints));
		player.getSession().send(new SendConfigPacket(ConfigKeys.PVP_POINTS, player.pvpPoints));
		player.getSession().send(new SendStringPacket("Well done, you defeated all the Champions!", 15835));
		player.getSession().send(new SendStringPacket("25 Slayer & 15 PvP Points", 15839));
		player.getSession().send(new SendStringPacket("A Mystery box", 15840));
		player.getInterfaceState().openInterface(15831);
		player.teleport(getExitLocation());

		if (!player.getInventory().add(new Item(ItemIdentifiers.MYSTERY_BOX, 1))) {
			GroundItemManager.spawnNew(player, ItemIdentifiers.MYSTERY_BOX, 3185, 9758, 1);
			player.sendMessage("[ <col=a90909>Challenge</col> ] Your inventory is full, so the mystery box is placed on the ground.");
		}

		if (!player.wonChallenge) {
			GameService.getTaskScheduler().schedule(new Task() {

				@Override
				public void execute() {
					player.wonChallenge = true;
					stop();
				}

			});
		}
		
		player.getMinigame().resetMinigame();

		stop();
	}

	@Override
	public void nextWave(Object[] objects) {
		if (player == null || player.isDisconnected() || !player.getMinigame().inMinigame() || player.isDead() || player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			return;
		}

		if (mob_list.size() > 0) {
			return;
		}

		player.getMinigame().increaseChallengeWave(1);

		if (player.getMinigame().getChallengeWave() > 0 && !login) {
			player.slayerPoints += player.getMinigame().getChallengeWave() + 2;
			player.sendMessage("[ <col=a90909>Challenge</col> ] You received " + (player.getMinigame().getChallengeWave() + 2) + " slayer points for completing this wave.");
		}

		if (login) {
			login = false;
		}

		GameService.getTaskScheduler().schedule(new Task(9) {

			@Override
			public void execute() {
				if (!player.getMinigame().inMinigame() || player.isDisconnected()) {
					stop();
					return;
				}

				player.sendMessage("[ <col=a90909>Challenge</col> ] Wave <col=a90909>" + (player.getMinigame().getChallengeWave() + 1) + "</col> is spawning..");
				spawnNpc(player.getMinigame().getChallengeWave());
				stop();
			}

		});
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
			if (player.getMinigame().getChallengeWave() >= ChallengeScrollConstants.MINIGAME_WAVES.length - 1) {
				removePlayer(player);
			} else {
				nextWave(null);
			}
		}
	}

	private void spawnNpc(int wave) {
		Location loc = ChallengeScrollConstants.MOB_SPAWN_LOCATION.transform(0, 0, player.getZ());

		if (loc.getZ() != player.getZ()) {
			loc.setAs(Location.create(loc.getX(), loc.getY(), player.getLocation().getZ()));
		}

		Mob mob = MinigameMob.spawnMob(ChallengeScrollConstants.MINIGAME_WAVES[wave], loc, new int[] { ChallengeScrollConstants.MOB_STATS[wave][0],
				ChallengeScrollConstants.MOB_STATS[wave][1], ChallengeScrollConstants.MOB_STATS[wave][2], ChallengeScrollConstants.MOB_STATS[wave][3] }, this);

		if (mob == null) {
			removeNpcs();
			player.setPosition(getExitLocation());
			player.getMinigame().setChallengeWave(0);
			player.getMinigame().resetMinigame();
			player.sendMessage("[ ERROR ] Post this on forums! Message: NO MOB SLOTS AVALIABLE [  Challenge scroll ].");
			return;
		}

		mob.summoner = player;
		mob_list.add(mob.getIndex());
		mob.getCombat().setTarget(player);
		mob.getFollowing().setFollow(player, FollowType.COMBAT, true);
	}

	public void removeNpcs() {
		for (int index : mob_list) {
			Mob mob = MobManager.mobs[index];

			if (mob != null && mob.getLocation().getZ() == player.getIndex() * 4) {
				mob.remove();
			}
		}

		mob_list.clear();
	}

}
