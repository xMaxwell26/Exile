package com.koios.game.model.object;

import java.util.Objects;

import com.koios.game.model.Direction;
import com.koios.game.model.Location;

public class SceneryObject {

	private final int id;
	private final Location position;
	private final int direction;

	public SceneryObject(int id, Location position, Direction direction) {
		this.id = id;
		this.position = Objects.requireNonNull(position);

		switch (direction) {
		case NORTH:
			this.direction = 0;
			break;

		case SOUTH:
			this.direction = 2;
			break;

		case WEST:
			this.direction = 3;
			break;

		case EAST:
			this.direction = 1;
			break;

		default:
			throw new IllegalArgumentException("illegal direction: " + direction);
		}
	}

	public int getDirection() {
		return direction;
	}

	public int getId() {
		return id;
	}

	public Location getPosition() {
		return position;
	}

}
