package com.koios.game.map;

public class RSObject {

	private final int direction;
	private int id;
	private final int masterId;
	private final int type;
	private final int x;
	private final int y;
	private final int z;

	public RSObject(int x, int y, int z, int id, int type, int direction) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		masterId = id;
		this.type = type;
		this.direction = direction;
	}

	public int getFace() {
		return direction;
	}

	public int getId() {
		return id;
	}

	public int getMasterId() {
		return masterId;
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

	public void setId(int id) {
		this.id = id;
	}

}
