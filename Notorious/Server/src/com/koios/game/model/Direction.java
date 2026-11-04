package com.koios.game.model;

/**
 * @author J
 */
public enum Direction {

	NORTH(1, 0, 1), NORTH_EAST(2, 1, 1), EAST(4, 1, 0), SOUTH_EAST(7, 1, -1), SOUTH(6, 0, -1), SOUTH_WEST(5, -1, -1), WEST(3, -1, 0), NORTH_WEST(0, -1, 1), NONE(-1, 0, 0);

	public static Direction direction(int diffX, int diffY) {
		if (diffX < 0) {
			if (diffY < 0) {
				return Direction.SOUTH_WEST;
			}

			if (diffY > 0) {
				return Direction.NORTH_WEST;
			}

			return Direction.WEST;
		}

		if (diffX > 0) {
			if (diffY < 0) {
				return Direction.SOUTH_EAST;
			}

			if (diffY > 0) {
				return Direction.NORTH_EAST;
			}

			return Direction.EAST;
		}

		if (diffY < 0) {
			return Direction.SOUTH;
		}

		if (diffY > 0) {
			return Direction.NORTH;
		}

		return Direction.NONE;
	}

	private final String name;
	private final int direction;
	private final int x;
	private final int y;

	private Direction(int direction, int x, int y) {
		name = name().toLowerCase().replace("_", "-");
		this.direction = direction;
		this.x = x;
		this.y = y;
	}

	public int getDirection() {
		return direction;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
