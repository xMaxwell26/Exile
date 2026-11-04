package com.koios.game.model;

import java.util.Arrays;

/**
 * The class that represents an area located anywhere in the world.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public abstract class Area {

	/**
	 * Determines if the specified position is in this location.
	 *
	 * @param location the position to determine if in this location.
	 * @return {@code true} if the position is in this location, {@code false} otherwise.
	 */
	public abstract boolean inArea(Location location);

	/**
	 * Generates a pseudo-random position from within this location.
	 *
	 * @return the random position that was generated.
	 * @throws UnsupportedOperationException by default, if this method is not overridden.
	 */
	public Location random() {
		throw new UnsupportedOperationException("No algorithm to generate a pseudo-random position from this location!");
	}

	/**
	 * Determines if the specified position is in <b>all</b> of the specified locations.
	 *
	 * @param location the position to determine if in the locations.
	 * @param areas the locations to determine if in this position.
	 * @return {@code true} if the position is in all of these locations, {@code false} otherwise.
	 */
	public static boolean inAllLocation(Location location, Area... areas) {
		return Arrays.stream(areas).allMatch(l -> l.inArea(location));
	}

	/**
	 * Determines if the specified position is in <b>any</b> of the specified locations.
	 *
	 * @param location the position to determine if in the locations.
	 * @param areas the locations to determine if in this position.
	 * @return {@code true} if the position is in any of these locations, {@code false} otherwise.
	 */
	public static boolean inAnyLocation(Location location, Area... areas) {
		return Arrays.stream(areas).anyMatch(l -> l.inArea(location));
	}

}
