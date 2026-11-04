package com.koios.game.content.area;

import java.util.Arrays;

import com.koios.game.model.Location;

/**
 * Represents a single Area
 *
 * @author Timothy Howard <Arithium>
 */
public class AreaBoundary {

	/**
	 * The southwest corner x
	 */
	private final int x1;

	/**
	 * The northeast corner x
	 */
	private final int x2;

	/**
	 * The southwest corner y
	 */
	private final int y1;

	/**
	 * The northeast corner y
	 */
	private final int y2;

	/**
	 * The z location of the southwest coordinate
	 */
	private final int z1;

	/**
	 * the z locatino of the northeast coordinate
	 */
	private final int z2;

	/**
	 * Constructs a new {@link AreaBoundary} with 2 locations
	 *
	 * @param location1 The southwest corner location
	 * @param location2 The northwest corner location
	 */
	public AreaBoundary(Location location1, Location location2) {
		x1 = location1.getX();
		x2 = location2.getX();
		y1 = location1.getY();
		y2 = location2.getY();
		z1 = location1.getZ();
		z2 = location2.getZ();
	}

	/**
	 * Constucts a new {@link AreaBoundary} with 2 locations
	 *
	 * @param x1 The southwest x coordinate
	 * @param y1 The southwest y coordinate
	 * @param x2 The northeast x coordinate
	 * @param y2 The northeast y coordinate
	 * @param z1 The z coordinate of the southwest corner
	 * @param z2 The z coordinate of the northeast corner
	 */
	public AreaBoundary(int x1, int y1, int x2, int y2, int z1, int z2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
	}

	/**
	 * Constucts a new {@link AreaBoundary} with 2 locations
	 *
	 * @param x1 The southwest x coordinate
	 * @param y1 The southwest y coordinate
	 * @param x2 The northeast x coordinate
	 * @param y2 The northeast y coordinate
	 */
	public AreaBoundary(int x1, int y1, int x2, int y2) {
		this(x1, y1, x2, y2, -1, -1);
	}

	/**
	 * Gets the southwest x coordinate
	 *
	 * @return
	 */
	public int getX1() {
		return x1;
	}

	/**
	 * Gets the northeast x coordinate
	 *
	 * @return
	 */
	public int getX2() {
		return x2;
	}

	/**
	 * Gets the southwest y coordinate
	 *
	 * @return
	 */
	public int getY1() {
		return y1;
	}

	/**
	 * Gets the northeast y coordinate
	 *
	 * @return
	 */
	public int getY2() {
		return y2;
	}

	/**
	 * gets the z coordinate for the southwest corner
	 *
	 * @return
	 */
	public int getZ1() {
		return z1;
	}

	/**
	 * gets the z coordinate for the northeast corner
	 *
	 * @return
	 */
	public int getZ2() {
		return z2;
	}

	/**
	 * Checks if a location is in an area
	 *
	 * @param location The {@link Location} of the area to check
	 * @param area The {@link AreaBoundary} to check if the {@link Location} is in
	 * @return If the {@link Location} is in the {@link AreaBoundary}
	 */
	public static boolean inArea(Location location, AreaBoundary area) {

		if (area.getZ1() != -1 && area.getZ2() != -1) {
			if (location.getZ() < area.getZ1() || location.getZ() > area.getZ2()) {
				return false;
			}
		}
		return (location.getX() >= area.getX1()) && (location.getX() <= area.getX2()) && (location.getY() >= area.getY1()) && (location.getY() <= area.getY2());
	}

	public static boolean inAnyLocation(Location location, AreaBoundary... areas) {
		return Arrays.stream(areas).anyMatch(l -> AreaBoundary.inArea(location, l));
	}
}
