package com.koios.game.map;

import java.util.Objects;

public class Tile {

	public final int x;
	public final int y;
	public final int z;

	public Tile(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Tile) {
			Tile other = (Tile) o;
			return other.x == x && other.y == y && other.z == z;
		}

		return false;
	}

}
