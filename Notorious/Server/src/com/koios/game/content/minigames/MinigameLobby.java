package com.koios.game.content.minigames;

import java.util.Map;

import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Abstract class that handle minigames lobby.
 *
 * @author Gabriel | Wolfs Darker
 */
public abstract class MinigameLobby extends Task {

	public MinigameLobby(int delay) {
		super(delay);
	}

	/**
	 * @return If player can teleport.
	 */
	public abstract boolean canTeleport();

	/**
	 * @return If player can use summoning.
	 */
	public abstract boolean canLogout();

	/**
	 * @return If player can move.
	 */
	public abstract boolean canMove();

	/**
	 * @return If player can trade.
	 */
	public abstract boolean canTrade();

	/**
	 * @return If player can use summoning.
	 */
	public abstract boolean canUseSummoning();

	/**
	 * Get the players inside the lobby
	 *
	 * @return the players
	 */
	public abstract Map<String, Player> getPlayers();

	/**
	 * Get the players inside the lobby
	 *
	 * @return the players
	 */
	public abstract Map<Player, String> getPlayersList();

	/**
	 * Handle player logout in lobby.
	 */
	public abstract void handleLogout(Player player);

	/**
	 * Handle player login in lobby.
	 */
	public abstract void handleLogin(Player player);

	/**
	 * Handle player teleport in lobby.
	 */
	public abstract void handleTeleport(Player player);

	/**
	 * Join the player in lobby.
	 */
	public abstract void joinLobby(Player player, Object[] objects);

	/**
	 * Checks if the lobby is full.
	 */
	public abstract boolean isFull();

	/**
	 * Get add lobby location.
	 */
	public abstract Location getJoinLocation();

	/**
	 * Get the remove lobby location.
	 */
	public abstract Location getLeaveLocation();

	/**
	 * Remove the player from the lobby.
	 */
	public abstract void leaveLobby(Player player);

	/**
	 * Starts the minigame.
	 */
	public abstract void startMinigame();

}
