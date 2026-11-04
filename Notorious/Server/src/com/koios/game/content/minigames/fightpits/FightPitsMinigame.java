package com.koios.game.content.minigames.fightpits;

import java.util.Map;

import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;

public class FightPitsMinigame extends Minigame {

	public FightPitsMinigame() {
		super(1);
	}

	@Override
	public String getMinigameName() {
		return null;
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
		return false;
	}

	@Override
	public boolean canAttack() {
		return false;
	}

	@Override
	public boolean canUsePrayer() {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, WorldObject object) {
		return false;
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
		return null;
	}

	@Override
	public Location getRespawnLocation() {
		return null;
	}

	@Override
	public Location getExitLocation() {
		return null;
	}

	@Override
	public MinigameLobby getLobby() {
		return null;
	}

	@Override
	public void handleLogout(Player player) {
	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleDeath(Player player) {
	}

	@Override
	public void startMinigame() {
	}

	@Override
	public void joinMinigame(Player player) {
	}

	@Override
	public void setLobby(MinigameLobby lobby) {
	}

	@Override
	public void damageHook(EntityCharacter attacker, EntityCharacter target, int damage) {
	}

	@Override
	public void removePlayer(Player player) {
	}

	@Override
	public void nextWave(Object[] objects) {
	}

	@Override
	protected void execute() {
	}

	@Override
	public boolean isOver() {
		return false;
	}

}
