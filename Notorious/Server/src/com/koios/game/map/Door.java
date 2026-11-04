package com.koios.game.map;

public class Door {

	private byte append = 0;
	private byte currentFace;
	private int currentId;
	private byte face;
	private byte face2;
	private int id;
	private int id2;
	private long time_left;
	private byte type;
	private short x;
	private byte xMod = 0;
	private short y;
	private byte yMod = 0;
	private byte z;

	public Door(int id, int x, int y, int z, int type, int face) {
		currentId = id;
		this.id = currentId;
		this.x = (short) x;
		this.y = (short) y;
		this.z = (byte) z;
		currentFace = (byte) face;
		this.type = (byte) type;
		this.face = (byte) face;
		boolean flag = !MapConstants.isOpen(id);
		id2 = ObjectAlternates.getAlternate(id);

		if (id2 <= 0) {
			id2 = id;
		}

		if (type == 9) {
			// diagonal doors
			if (!flag) {
				// open
				switch (face) {
				case 0:
					face2 = 1;
					xMod = 1;
					break;

				case 1:
					face2 = 0;
					yMod = -1;
					break;

				case 2:
					face2 = 1;
					xMod = -1;
					break;
				}
			} else {
				// closed
				switch (face) {
				case 0:
					face2 = 3;
					xMod = 1;
					break;

				case 1:
					face2 = 0;
					yMod = -1;
					break;

				case 2:
					face2 = 3;
					yMod = -1;
					break;

				case 3:
					face2 = 0;
					xMod = -1;
					break;
				}
			}

			return;
		}

		if (!flag) {
			// open
			switch (face) {
			case 0:
				face2 = 3;
				yMod = 1;
				break;

			case 1:
				face2 = 0;
				xMod = 1;
				break;

			case 2:
				face2 = 1;
				yMod = -1;
				break;

			case 3:
				face2 = 2;
				xMod = -1;
				break;
			}
		} else {
			// closed
			switch (face) {
			case 0:
				face2 = 1;
				xMod = -1;
				break;

			case 1:
				face2 = 2;
				yMod = 1;
				break;

			case 2:
				face2 = -1;
				xMod = 1;
				break;

			case 3:
				face2 = 0;
				yMod = -1;
				break;
			}
		}
	}

	public void append() {
		if (currentId == id) {
			currentId = id2;
		} else if (currentId == id2) {
			currentId = id;
		}

		if (currentFace == face) {
			currentFace = face2;
			x += xMod;
			y += yMod;
			append = (byte) 1;
		} else if (currentFace == face2) {
			currentFace = face;
			x -= xMod;
			y -= yMod;
			append = (byte) 0;
		}
	}

	public boolean equals(Door door) {
		return door.id == id && door.x == x && door.y == y && door.z == z;
	}

	public int getCurrentFace() {
		return currentFace;
	}

	public int getCurrentId() {
		return currentId;
	}

	public int getFace() {
		return face;
	}

	public int getFace2() {
		return face2;
	}

	public int getId() {
		return id;
	}

	public int getId2() {
		return id2;
	}

	public long getTimeLeft() {
		return time_left;
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

	public boolean original() {
		return append == (byte) 0;
	}

	public void setCurrentFace(int currentFace) {
		this.currentFace = (byte) currentFace;
	}

	public void setTimeLeft(long l) {
		time_left = l;
	}

}
