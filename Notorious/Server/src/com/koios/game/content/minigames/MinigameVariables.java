package com.koios.game.content.minigames;

import com.koios.game.model.player.Player;

/**
 * Handle player's minigame variables.
 *
 * @author Gabriel | Wolfs Darker
 */
public class MinigameVariables {

	/**
	 * Player instance handler.
	 */
	private final Player player;

	/**
	 * Player's current lobby.
	 */
	private MinigameLobby currentLobby;

	/**
	 * Player's current minigame.
	 */
	private Minigame currentMinigame;

	/**
	 * Pest control commendations count;
	 */
	private int commendations;

	/**
	 * Tzhaar Fight cave variables.
	 */
	private int tzhaarWave = 0;

	/**
	 * Kiln fight cave variables
	 */
	private boolean inKiln, kilnFee;

	/**
	 * Player's current challenge wave.
	 */
	private int challengeWave = 0;

	/**
	 * Constructor of the variables for players.
	 */
	public MinigameVariables(Player player) {
		this.player = player;
	}

	/**
	 * Boosts the player's commendations count.
	 */
	public void boostCommendations(int boost) {
		commendations += boost;
	}

	/**
	 * Put the player inside a lobby.
	 */
	public void enterLobby(MinigameLobby lobby) {
		enterLobby(lobby, null);
	}

	/**
	 * Put the player inside a lobby.
	 */
	public void enterLobby(MinigameLobby lobby, Object[] objects) {
		if (lobby.isFull()) {
			player.sendMessage("This lobby is full!");
			return;
		}

		player.setPosition(lobby.getJoinLocation());
		lobby.joinLobby(player, objects);
		currentLobby = lobby;
	}

	/**
	 * Put the player inside a minigame.
	 */
	public void enterMinigame(Minigame minigame) {
		if (currentMinigame != null) {
			return;
		}

		currentMinigame = minigame;
		minigame.joinMinigame(player);
	}

	/**
	 * Gets the pest control commendations.
	 *
	 * @return commendations count
	 */
	public int getCommendations() {
		return commendations;
	}

	/**
	 * Gets the current jad wave.
	 */
	public int getTzhaarWave() {
		return tzhaarWave;
	}

	/**
	 * Returns the current challenge wave index
	 *
	 * @return the wave
	 */
	public int getChallengeWave() {
		return challengeWave;
	}

	/**
	 * Checks if the player is in the tzhaar or kiln minigame
	 */
	public boolean inKiln() {
		return inKiln;
	}

	/**
	 * Checks if the player has the fee paid.
	 */
	public boolean isKilnFeePaid() {
		return kilnFee;
	}

	/**
	 * Gets the player current lobby.
	 *
	 * @return the lobby
	 */
	public MinigameLobby getCurrentLobby() {
		return currentLobby;
	}

	/**
	 * Gets the player current minigame.
	 *
	 * @return the minigame
	 */
	public Minigame getCurrentMinigame() {
		return currentMinigame;
	}

	/**
	 * Handle the player logout inside a minigame or lobby.
	 */
	public void handleLogout() {
		if (currentLobby != null) {
			currentLobby.handleLogout(player);
		}

		if (currentMinigame != null) {
			currentMinigame.handleLogout(player);
		}
	}

	/**
	 * Checks if the player is current in a minigame.
	 */
	public boolean inMinigame() {
		return currentMinigame != null;
	}

	/**
	 * Checks if the player is current in a minigame lobby.
	 */
	public boolean inMinigameLobby() {
		return currentLobby != null;
	}

	/**
	 * Remove the player from the lobby.
	 */
	public void leaveLobby() {
		if (currentLobby == null) {
			return;
		}

		currentLobby.leaveLobby(player);
		player.setPosition(currentLobby.getLeaveLocation());
		currentLobby = null;
	}

	/**
	 * Resets the player's minigame.
	 */
	public void resetMinigame() {
		currentMinigame = null;
	}

	/**
	 * Set the player's commendations count.
	 */
	public void setCommendations(int commendations) {
		this.commendations = commendations;
	}

	/**
	 * Sets the current wave.
	 */
	public void setTzhaarWave(int i) {
		tzhaarWave = i;
	}

	/**
	 * Increases the jad wave
	 */
	public void increaseTzhaarWave(int i) {
		tzhaarWave += i;
	}

	/**
	 * Sets the current wave.
	 */
	public void setChallengeWave(int i) {
		challengeWave = i;
	}

	/**
	 * Increases the jad wave
	 */
	public void increaseChallengeWave(int i) {
		challengeWave += i;
	}

	/**
	 * Resets the tzhaar variables.
	 */
	public void resetTzhaarVariables() {
		tzhaarWave = 0;
		inKiln = false;
	}

	/**
	 * Sets if the player is in kiln
	 */
	public void setInKiln(boolean b) {
		inKiln = b;
	}

	/**
	 * Sets the kiln fee
	 */
	public void setKilnFee(boolean b) {
		kilnFee = b;
	}

}
