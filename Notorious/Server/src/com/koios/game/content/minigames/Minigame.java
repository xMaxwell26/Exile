package com.koios.game.content.minigames;

import java.util.Map;

import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Constructor of minigames variables.
 *
 * @author Gabriel | Wolfs Darker
 */
public abstract class Minigame extends Task {

	/**
	 * Constructor of the minigame class.
	 */
	public Minigame(int delay) {
		super(delay);
	}

	/**
	 * Gets the minigame name.
	 */
	public abstract String getMinigameName();

	/**
	 * Get the minigame player list.
	 */
	public abstract Map<Player, String> getPlayerList();

	/**
	 * Get the minigame mob list.
	 */
	public abstract Map<String, Mob> getMobList();

	/**
	 * Checks if player can teleport in middle of minigame.
	 */
	public abstract boolean canTeleport();

	/**
	 * Checks if the player can logout in the middle of minigame
	 */
	public abstract boolean canLogout();

	/**
	 * Checks if the entity can attack another entity.
	 */
	public abstract boolean canAttack();

	/**
	 * Checks if the entity can use prayer.
	 */
	public abstract boolean canUsePrayer();

	/**
	 * Checks if the minigame is over
	 */
	public abstract boolean isOver();

	/**
	 * Handles object clicking.
	 */
	public abstract boolean handleObjectClick(Player player, WorldObject object);

	/**
	 * Handle mob clicking.
	 */
	public abstract boolean handleMobClick(Player player, Mob mob);

	/**
	 * Handle button clicking.
	 */
	public abstract boolean handleButtonClick(Player player, int buttonId);

	/**
	 * Checks if minigame is full.
	 */
	public abstract boolean isFull();

	/**
	 * Gets the spawn locatiom.
	 */
	public abstract Location getSpawnLocation();

	/**
	 * Gets the respawn location.
	 */
	public abstract Location getRespawnLocation();

	/**
	 * Gets the exit location.
	 */
	public abstract Location getExitLocation();

	/**
	 * Get the minigame lobby.
	 */
	public abstract MinigameLobby getLobby();

	/**
	 * Handle the player logout in middle of minigame.
	 */
	public abstract void handleLogout(Player player);

	/**
	 * Handle the player login in minigame area.
	 */
	public abstract void handleLogin(Player player);

	/**
	 * Handle the player death inside the minigame area.
	 */
	public abstract void handleDeath(Player player);

	/**
	 * Starts the minigame.
	 */
	public abstract void startMinigame();

	/**
	 * Joins the player inside the minigame.
	 */
	public abstract void joinMinigame(Player player);

	/**
	 * Sets the minigame lobby.
	 */
	public abstract void setLobby(MinigameLobby lobby);

	/**
	 * Applies damage to a certain entity.
	 */
	public abstract void damageHook(EntityCharacter attacker, EntityCharacter target, int damage);

	/**
	 * Removes the player from the minigame.
	 */
	public abstract void removePlayer(Player player);

	/**
	 * Starts the next wave.
	 */
	public abstract void nextWave(Object[] objects);

}
