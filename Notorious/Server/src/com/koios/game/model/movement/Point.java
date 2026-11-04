package com.koios.game.model.movement;

import com.koios.game.model.Location;

public class Point extends Location {

	private int direction;

	public Point(int x, int y, int direction) {
		super(x, y);
		setDirection(direction);
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

}
