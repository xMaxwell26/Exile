package com.koios.game.content.area;

import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

/**
 * Represents a single 'Area' with actions that has interactable objects
 *
 * @author Timothy Howard <Mobster>
 */
public abstract class AreaHandler {

	/**
	 * Handles first clicking object in a specific area
	 *
	 * @param player The {@link Player} clicking on the object
	 * @param id The id of the {link Object}
	 * @param location The {@link Location} of the object
	 */
	public void handleFirstClickObject(Player player, int id, Location location) {
	}

	/**
	 * Handles second clicking an object in a specific area
	 *
	 * @param player The {@link Player} clicking on the object
	 * @param id The id of the {link Object}
	 * @param location The {@link Location} of the object
	 */
	public void handleSecondClickObject(Player player, int id, Location location) {
	}

	/**
	 * Handles third clicking an object in a specific area
	 *
	 * @param player The {@link Player} clicking on the object
	 * @param id The id of the {link Object}
	 * @param location The {@link Location} of the object
	 */
	public void handleThirdClickObject(Player player, int id, Location location) {
	}

	/**
	 * Handles first clicking an npc in a specific area
	 *
	 * @param player The {@link Player} clicking on the npc
	 * @param npc The {@link NPC} being clicked
	 * @return
	 */
	public void handleFirstClickNpc(Player player, Mob npc, Location location) {
	}

	/**
	 * Handles first clicking an npc in a specific area
	 *
	 * @param player The {@link Player} clicking on the npc
	 * @param npc The {@link NPC} being clicked
	 * @return
	 */
	public void handleSecondClickNpc(Player player, Mob npc, Location location) {
	}

	/**
	 * Handles first clicking an npc in a specific area
	 *
	 * @param player The {@link Player} clicking on the npc
	 * @param npc The {@link NPC} being clicked
	 * @return
	 */
	public void handleThirdClickNpc(Player player, Mob npc, Location location) {
	}

	/**
	 * The area this location is in
	 *
	 * @return
	 */
	public abstract AreaBoundary getArea();
}
