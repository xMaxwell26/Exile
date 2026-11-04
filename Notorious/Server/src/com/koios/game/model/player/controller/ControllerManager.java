package com.koios.game.model.player.controller;

import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.minigames.barrows.BarrowsData;
import com.koios.game.content.minigames.fightpits.FightPitsLobby;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.impl.BarrowsController;
import com.koios.game.model.player.controller.impl.DefaultController;
import com.koios.game.model.player.controller.impl.DuelArenaController;
import com.koios.game.model.player.controller.impl.FightPitsController;
import com.koios.game.model.player.controller.impl.FightPitsLobbyController;
import com.koios.game.model.player.controller.impl.GodWarsController;
import com.koios.game.model.player.controller.impl.PestControlBoatController;
import com.koios.game.model.player.controller.impl.PestControlGameController;
import com.koios.game.model.player.controller.impl.RecipeForDisasterController;
import com.koios.game.model.player.controller.impl.WildernessController;
import com.koios.network.out.impl.SendMultiIconPacket;
import com.koios.network.out.impl.SendOptionPacket;

/**
 * Handles controllers for specific areas to moderate a players gameplay
 * 
 * @author Mobster
 * @author Mikey`
 */
public class ControllerManager {

	/**
	 * The default controller which is handled when the player has no other active controller
	 */
	public static final DefaultController DEFAULT_CONTROLLER = new DefaultController();

	/**
	 * The barrows minigame controller
	 */
	private static final BarrowsController BARROWS_CONTROLLER = new BarrowsController();

	/**
	 * The controller for the wilderness
	 */
	private static final WildernessController WILDERNESS_CONTROLLER = new WildernessController();

	/**
	 * The controller for the duel arena
	 */
	private static final DuelArenaController DUEL_ARENA_CONTROLLER = new DuelArenaController();

	/**
	 * The controller for the fight pits minigame
	 */
	private static final FightPitsController FIGHT_PITS_CONTROLLER = new FightPitsController();

	/**
	 * The controller for the fight pits lobby
	 */
	private static final FightPitsLobbyController FIGHT_PITS_LOBBY_CONTROLLER = new FightPitsLobbyController();

	/**
	 * The controller for the pest control boat
	 */
	private static final PestControlBoatController PEST_CONTROL_BOAT_CONTROLLER = new PestControlBoatController();

	/**
	 * The controller for the pest control game
	 */
	private static final PestControlGameController PEST_CONTROL_GAME_CONTROLLER = new PestControlGameController();

	/**
	 * The controller for the god wars area
	 */
	private static final GodWarsController GOD_WARS_CONTROLLER = new GodWarsController();
	
	/**
	 * The recipe for disaster controller
	 */
	private static final RecipeForDisasterController RFD_CONTROLLER = new RecipeForDisasterController();

	/**
	 * Sets the controller when a player takes a step or teleports
	 * 
	 * @param player The {@link Player} to set the controller for
	 */
	public static void setControllerOnWalk(Player player) {

		if ((player.getController() != null) && (!player.getController().transitionOnWalk(player))) {
			return;
		}

		/*
		 * Default our controller before deciding the proper controller
		 */
		Controller c = DEFAULT_CONTROLLER;

		/*
		 * Check if we should have a different controller for our current location
		 */
		if (BarrowsData.inBarrows(player.getLocation()) || BarrowsData.isInBarrowsTomb(player.getLocation()))
			c = BARROWS_CONTROLLER;
		else if (player.inWild())
			c = WILDERNESS_CONTROLLER;
		else if (player.inDuelArena() && !player.inBarbDef())
			c = DUEL_ARENA_CONTROLLER;
		else if (FightPits.getInstance().isParticipant(player))
			c = FIGHT_PITS_CONTROLLER;
		else if (player.inPestControlBoat()) {
			c = PEST_CONTROL_BOAT_CONTROLLER;
		} else if (player.inPestControlGame()) {
			c = PEST_CONTROL_GAME_CONTROLLER;
		} else if (FightPitsLobby.inside(player)) {
			c = FIGHT_PITS_LOBBY_CONTROLLER;
		} else if (player.inGodwars()) {
			c = GOD_WARS_CONTROLLER;
		} else if (player.inBounds(1888, 1950, 5340, 5400)) {
			c = RFD_CONTROLLER;
		}

		/*
		 * Set our controller after deciding the right controller
		 */
		player.setController(c);

		/*
		 * Check if we're in multi so we dont have to keep checking
		 */
		boolean inMulti = player.inMulti();

		/*
		 * Send a multi sign if we're in a multi area
		 */
		if (!player.hasMultiSign && inMulti) {
			player.hasMultiSign = true;
			player.getSession().send(new SendMultiIconPacket(1));
		} else if (player.hasMultiSign && !inMulti) {
			player.hasMultiSign = false;
			player.getSession().send(new SendMultiIconPacket(-1));
		}

		if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().getMinigameName().equals("PROP_HUNT")) {
			player.getSession().send(new SendOptionPacket(3, "Shoot", true));
		}

	}

	/**
	 * Updates the controller when the player is walking
	 * 
	 * @param player
	 */
	public static void updateControllerOnWalk(Player player) {
		if (player.getController() == null) {
			return;
		}

		player.getController().onWalk(player);
	}

	public static void onForceLogout(Player player) {

	}

}
