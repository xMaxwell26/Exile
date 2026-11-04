package com.koios.game.content.teleport;

/**
 * The different types of teleports
 * 
 * @author Timothy Howard <Arithium>
 */
public enum TeleportType {

	/**
	 * A regualr teleport which will provide normal requirement checks before teleporting
	 */
	REGULAR,

	/**
	 * A totally unchecked teleport type which will not check any requirements
	 */
	UNCHECKED,

	/**
	 * A regular teleport which will NOT perform a wilderness level check, but will perform other requirement checks
	 */
	NO_WILD_CHECK;

}
