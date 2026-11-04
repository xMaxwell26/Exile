package com.koios.game.model;

/**
 * Represents a single location in the game world.
 *
 * @author Graham Edgecombe
 */
public class Location {

	/**
	 * Creates a location.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y) {
		return new Location(x, y, 0);
	}

	/**
	 * Creates a location.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}

	/**
	 * The x coordinate.
	 */
	private int x;

	/**
	 * The y coordinate.
	 */
	private int y;

	/**
	 * The z coordinate.
	 */
	private int z;

	/**
	 * Blank constructor for easy creation.
	 */
	public Location() {
	}

	/**
	 * Creates a location.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * Creates a location.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Calculate the distance between a player and a point.
	 *
	 * @return The square distance.
	 */
	public double getDistance(Location other) {
		int xdiff = getX() - other.getX();
		int ydiff = getY() - other.getY();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	/**
	 * Gets the distance from a point.
	 *
	 * @param other The target.
	 * @return blah distance.
	 */
	public int getDistanceFromLocation(Location other) {
		int absX = x;
		int absY = y;
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
	}

	/**
	 * Gets the local x coordinate relative to this region.
	 *
	 * @return The local x coordinate relative to this region.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 *
	 * @param location The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Location location) {
		return x - 8 * location.getRegionX();
	}

	/**
	 * Gets the local y coordinate relative to this region.
	 *
	 * @return The local y coordinate relative to this region.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 *
	 * @param location The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Location location) {
		return y - 8 * location.getRegionY();
	}

	public int getBaseX() {
		return x << 3;
	}

	public int getBaseY() {
		return y << 3;
	}

	public Location getLocation() {
		return new Location(x, y, z);
	}

	/**
	 * Gets the region x coordinate.
	 *
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the region y coordinate.
	 *
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}

	/**
	 * Gets the absolute x coordinate.
	 *
	 * @return The absolute x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the absolute y coordinate.
	 *
	 * @return The absolute y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the z coordinate, or height.
	 *
	 * @return The z coordinate.
	 */
	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Location)) {
			return false;
		}
		Location other = (Location) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if this location is within range of another.
	 *
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location other) {
		if (z != other.z) {
			return false;
		}

		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	/**
	 * Moves the location.
	 *
	 * @param amountX the amount of X coordinates
	 * @param amountY the amount of Y coordinates
	 */
	public void move(int amountX, int amountY) {
		x += amountX;
		y += amountY;
	}

	/**
	 * Sets this location as the other location. <b>Please use this method instead of player.setLocation(other)</b> because of reference conflicts (if the other location gets
	 * modified, so will the players).
	 *
	 * @param other the other location
	 */
	public void setAs(Location other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}

	@Override
	public String toString() {
		return "Location[" + x + "," + y + "," + z + "]";
	}

	/**
	 * Creates a new location based on this location.
	 *
	 * @param diffX X difference.
	 * @param diffY Y difference.
	 * @param diffZ Z difference.
	 * @return The new location.
	 */
	public Location transform(int diffX, int diffY, int diffZ) {
		return Location.create(x + diffX, y + diffY, z + diffZ);
	}

	public Location transform(int diffX, int diffY) {
		return transform(diffX, diffY, 0);
	}

	public boolean withinBlock(Location other, int size) {
		return x - other.getX() < size && x - other.getX() > -1 && y - other.getY() < size && y - other.getY() > -1;
	}

	/**
	 * Checks if a specific location is within a specific radius.
	 *
	 * @param rad The radius.
	 * @return True if we're within distance/range, false if not.
	 */
	public boolean withinRange(Location p, int rad) {
		if (p == null) {
			return false;
		}

		int dX = Math.abs(x - p.x);
		int dY = Math.abs(y - p.y);
		return dX <= rad && dY <= rad;
	}

	public static Location delta(Location a, Location b) {
		return new Location(b.getX() - a.getX(), b.getY() - a.getY());
	}

	public boolean isViewableFrom(Location other) {
		Location p = delta(this, other);
		return other.z == z && p.x <= 14 && p.x >= -15 && p.y <= 14 && p.y >= -15;
	}

	public static int getDistance(Location a, Location b) {
		int deltaX = b.getX() - a.getX();
		int deltaY = b.getY() - a.getY();
		return (int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}

	public static int getManhattanDistance(int x, int y, int x2, int y2) {
		return Math.abs(x - x2) + Math.abs(y - y2);
	}

	public static int getManhattanDistance(Location a, Location b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	public int distanceTo(Location position) {
		int distX = Math.abs(x - position.getX());
		int distY = Math.abs(y - position.getY());

		if (distX == distY) {
			return distX + 1;
		}

		return distX > distY ? distX : distY;
	}

	public Location copy() {
		// TODO Auto-generated method stub
		return new Location(x, y, z);
	}

}
