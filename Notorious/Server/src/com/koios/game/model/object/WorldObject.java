package com.koios.game.model.object;

import com.koios.game.model.Location;

public class WorldObject {

	private final int face;
	public int z;
	public int id;
	public int ticks;
	private final int type;
	public int x;
	public int y;
	public long owner;
	public int removeTimer;

	/**
	 * The time it takes to remove a world object
	 */
	private final int REMOVE_TIMER = 100;

	public WorldObject(int id, int x, int y, int z, int face, int type, int ticks) {
		this(id, x, y, z, face, type, ticks, false);
	}

	public WorldObject(int id, int x, int y, int z, int face, int type, int ticks, boolean remove) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.type = type;
		this.ticks = ticks;
		removeTimer = remove ? REMOVE_TIMER : -1;
	}

	public int getFace() {
		return face;
	}

	public int getID() {
		return id;
	}

	public Location getPosition() {
		return new Location(x, y, z);
	}

	public int getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public WorldObject setOwner(long owner) {
		this.owner = owner;
		return this;
	}

}
