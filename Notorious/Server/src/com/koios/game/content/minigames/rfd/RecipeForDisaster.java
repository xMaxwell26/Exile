package com.koios.game.content.minigames.rfd;

import java.util.Map;

import com.koios.Constants;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;

public final class RecipeForDisaster extends Minigame {

	private boolean finished;

	public RecipeForDisaster() {
		super(600);
	}

	@Override
	public String getMinigameName() {
		return "Recipe for Disaster";
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
		return true;
	}

	@Override
	public boolean canUsePrayer() {
		return false;
	}

	@Override
	public boolean isOver() {
		return finished;
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
		return new Location(Constants.RESPAWN_X, Constants.RESPAWN_Y);
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

}