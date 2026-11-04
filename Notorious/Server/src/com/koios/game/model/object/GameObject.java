package com.koios.game.model.object;

import com.koios.GameService;
import com.koios.game.model.Location;

public class GameObject {

	public byte face;
	public int height;
	public int newId;
	public int id;
	public short x;
	public short y;
	private final boolean remove;
	public short ticks;
	public int type;

	/**
	 * Creates a new game object
	 *
	 * @param id
	 * @param x
	 * @param y
	 * @param height
	 * @param face
	 * @param type
	 * @param newId
	 * @param ticks
	 * @param remove
	 */
	public GameObject(int id, int x, int y, int height, int face, int type, int newId, int ticks, boolean remove) {
		this.id = id;
		this.x = (short) x;
		this.y = (short) y;
		this.height = height;
		this.face = (byte) face;
		this.type = type;
		this.newId = newId;
		this.ticks = (short) ticks;
		this.remove = remove;
		GameService.getObjectManager().addObject(this);
	}

	public int getFace() {
		return face;
	}

	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}

	public Location getPosition() {
		return new Location(x, y, height);
	}

	public boolean getRemove() {
		return remove;
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

}
