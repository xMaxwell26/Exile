package com.koios.game.content.minigames;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.combat.mob.Godwars;
import com.koios.game.content.minigames.barrows.Barrows;
import com.koios.game.content.minigames.barrows.BarrowsData;
import com.koios.game.content.minigames.misc.ChallengeScroll;
import com.koios.game.content.minigames.misc.ChallengeScrollConstants;
import com.koios.game.content.minigames.misc.TzhaarFightCave;
import com.koios.game.model.player.Player;

/**
 * Manages the minigames.
 *
 * @author Gabriel
 */
public class MinigamesManager {

	/**
	 * Handles the minigame lobbys list.
	 */
	private static Map<String, MinigameLobby> lobbysList = new HashMap<>();

	/**
	 * Handles the minigames list.
	 */
	private static Map<String, Minigame> minigamesList = new HashMap<>();

	/**
	 * Creates a new minigame lobby.
	 */
	public static boolean addLobby(MinigameLobby lobby, String name) {
		if (name == null || lobby == null || lobbysList.containsKey(name)) {
			return false;
		}

		lobbysList.put(name, lobby);
		return true;
	}

	/**
	 * Creates a new minigame.
	 */
	public static boolean addMinigame(Minigame minigame, String name) {
		if (name == null || minigame == null || lobbysList.containsKey(name)) {
			return false;
		}

		minigamesList.put(name, minigame);
		return true;
	}

	/**
	 * Get a lobby by its name.
	 */
	public static MinigameLobby getLobby(String lobbyName) {
		return lobbysList.get(lobbyName);
	}

	public static void handleLogin(Player player) {
		if (player.inFightCaves()) {
			player.getMinigame().enterMinigame(new TzhaarFightCave(player, player.getMinigame().inKiln(), true));
		}

		if (BarrowsData.isInBarrowsTomb(player.getLocation()) || BarrowsData.inBarrows(player.getLocation())) {
			Barrows.updateInterface(player);
		}

		if (player.inGodwars()) {
			Godwars.updateInterface(player);
		}

		if (ChallengeScrollConstants.inChallengeArea(player.getLocation())) {
			player.getMinigame().enterMinigame(new ChallengeScroll(player, true));
		}
	}

	/**
	 * Checks if there is a existing lobby for a expecific minigame.
	 */
	public static boolean lobbyExist(String lobbyName) {
		return lobbysList.containsKey(lobbyName);
	}

	/**
	 * Checks if the minigame is already running.
	 */
	public static boolean minigameExist(String minigameName) {
		return minigamesList.containsKey(minigameName);
	}

	/**
	 * Removes a lobby from the list.
	 */
	public static boolean removeLobby(String name) {
		if (lobbysList.get(name) == null) {
			return false;
		}

		lobbysList.remove(name);
		return true;
	}

	/**
	 * Removes a minigame from the list.
	 */
	public static boolean removeMinigame(String name) {
		if (minigamesList.get(name) == null) {
			return false;
		}

		minigamesList.remove(name);
		return true;
	}

}
