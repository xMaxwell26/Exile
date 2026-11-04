package com.koios.game.content.area;

/**
 * The type of interaction the player is doing
 *
 * @author Timothy Howard <Mobster>
 */
public enum InteractionType {

	/**
	 * Handles first clicking an object in an area
	 */
	FIRST_CLICK_OBJECT,

	/**
	 * Handles second clicking an object in an area
	 */
	SECOND_CLICK_OBJECT,

	/**
	 * Handles third clicking an object in an area
	 */
	THIRD_CLICK_OBJECT,

	/**
	 * Handles first clicking an npc in an area
	 */
	FIRST_CLICK_NPC,

	/**
	 * Handles second clicking an npc in an area
	 */
	SECOND_CLICK_NPC,

	/**
	 * Handles third clicking an npc in an area
	 */
	THIRD_CLICK_NPC

}
