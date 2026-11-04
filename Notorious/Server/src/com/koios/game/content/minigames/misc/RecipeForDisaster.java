package com.koios.game.content.minigames.misc;

import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.content.teleport.Teleport.TeleportPlaces;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class RecipeForDisaster extends Minigame {

	public static final int[] NPC_IDS = new int[] { 3493, 3494, 3495, 3496, 3491 };

	public static int getNextWave(int npc) {
		for (int i = 0; i < NPC_IDS.length; i++) {
			if (npc == NPC_IDS[i]) {
				return NPC_IDS[i + 1];
			}
		}

		return -1;
	}

	public static boolean isRecipeMonster(int npc) {
		for (int npcId : NPC_IDS) {
			if (npc == npcId) {
				return true;
			}
		}

		return false;
	}

	private final Player player;

	public RecipeForDisaster(Player player) {
		super(1);
		this.player = player;
	}

	@Override
	public boolean canAttack() {
		return true;
	}

	@Override
	public boolean canLogout() {
		return false;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canUsePrayer() {
		return false;
	}

	@Override
	public void damageHook(EntityCharacter attacker, EntityCharacter target, int damage) {
	}

	@Override
	protected void execute() {
	}

	@Override
	public Location getExitLocation() {
		return new Location(Constants.EDGEVILLE_X, Constants.EDGEVILLE_Y, 0);
	}

	@Override
	public MinigameLobby getLobby() {
		return null;
	}

	@Override
	public String getMinigameName() {
		return "RECIPE_FOR_DISASTER";
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
		return new Location(Constants.EDGEVILLE_X, Constants.EDGEVILLE_Y, 0);
	}

	@Override
	public Location getSpawnLocation() {
		return TeleportPlaces.RFD.getLocation();
	}

	@Override
	public boolean handleButtonClick(Player player, int buttonId) {
		return false;
	}

	@Override
	public void handleDeath(Player player) {
		player.getMinigame().resetMinigame();
		player.setPosition(getExitLocation());
	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleLogout(Player player) {
		player.getMinigame().resetMinigame();
		player.setPosition(getExitLocation());
	}

	@Override
	public boolean handleMobClick(Player player, Mob mob) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, WorldObject object) {
		return false;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean isOver() {
		return false;
	}

	@Override
	public void joinMinigame(Player player) {
		startMinigame();
	}

	@Override
	public void nextWave(Object[] objects) {
		Mob dead_mob = (Mob) objects[0];

		if (dead_mob.getId() == 3491) {
			removePlayer(player);
		} else {
			MobManager.spawnMob(player, getNextWave(dead_mob.getId()), Location.create(1899, 5355 + NumberUtil.inclusiveRandom(5)), 250, 25, 400, 400, true, true, null);
		}
	}

	@Override
	public void removePlayer(Player player) {
		player.getInventory().add(new Item(995, 1_000_000 * NumberUtil.random(1, 4)));
		player.sendMessage("You completeed the Recipe for Disaster minigame!");
		player.setPosition(getExitLocation());
		player.getMinigame().resetMinigame();
	}

	@Override
	public void setLobby(MinigameLobby lobby) {
	}

	@Override
	public void startMinigame() {
		player.castTeleport(new Teleport(player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL,
				getSpawnLocation().transform(0, 0, player.getIndex() * 4)));
		player.sendMessage("Prepare yourself for the battle.");

		GameService.getTaskScheduler().schedule(new Task(15) {

			@Override
			public void execute() {
				MobManager.spawnMob(player, 3493, Location.create(1899, 5355 + NumberUtil.inclusiveRandom(5)), 250, 25, 400, 400, true, true, null);
				stop();
			}

		});
	}

}
