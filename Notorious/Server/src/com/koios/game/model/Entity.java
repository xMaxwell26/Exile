package com.koios.game.model;

import java.util.Objects;
import java.util.Random;

/**
 * A representation of an entity that roams within the game world.
 */
public abstract class Entity {

	/**
	 * A counter holding the next ident for the next entity to be made.
	 */
	// all entities are being created in a single thread (the "game update" thread).
	// Consider a change to AtomicInteger in the future should this behaviour change.
	private static int identCount = 1;

	/**
	 * The current location of this entity.
	 */
	private final Location location = new Location(0, 0, 0);

	/**
	 * This ident can be used to check if two entities are the exact same.
	 */
	private final int ident;

	/**
	 * An instance of {@link Random} relative to this instance of entity.
	 */
	protected final Random random = new Random();

	protected Entity() {
		int n = identCount;
		identCount = Math.incrementExact(n);
		ident = n;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj != null && obj.getClass() == getClass()) {
			Entity that = (Entity) obj;
			return that.ident == ident;
		}

		return false;
	}

	public abstract int getIndex();

	public abstract String getName();

	/**
	 * Gets the internal and unique {@link Random} instance for this {@code Entity}.
	 *
	 * @return An instance of {@link Random}.
	 */
	public Random getRandom() {
		return random;
	}

	public Location getLocation() {
		return location;
	}

	public int getX() {
		return location.getX();
	}

	public int getY() {
		return location.getY();
	}

	public int getZ() {
		return location.getZ();
	}

	@Override
	public int hashCode() {
		return ident;
	}

	public boolean isWithinDistance(Entity entity, int distance) {
		Objects.requireNonNull(entity, "entity");

		if (getZ() != entity.getZ()) {
			return false;
		}

		int dx = entity.getX() - getX();
		int dy = entity.getY() - getY();

		return dx <= distance && dx >= -distance && dy <= distance && dy >= -distance;
	}

	public void setX(int x) {
		location.setAs(new Location(x, location.getY(), location.getZ()));
	}

	public void setY(int y) {
		location.setAs(new Location(location.getX(), y, location.getZ()));
	}

	public void setZ(int z) {
		location.setAs(new Location(location.getX(), location.getY(), z));
	}

	/**
	 * Update internal logic on a per-cycle basis.
	 */
	public void update() {
	}

}
