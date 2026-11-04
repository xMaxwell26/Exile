package com.koios.game.model.movement;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.util.DirectionUtil;

public abstract class MovementHandler {

	protected final EntityCharacter entity;
	protected boolean flag = false;
	private boolean forced = false;
	protected boolean forceMove = false;
	protected boolean stopMovement;
	protected Location lastLocation = new Location(0, 0);
	protected Deque<Point> waypoints = new ConcurrentLinkedDeque<Point>();

	public MovementHandler(EntityCharacter entity) {
		this.entity = entity;
	}

	private void addStep(int x, int y) {
		if (waypoints.size() >= 100 || stopMovement) {
			return;
		}

		if (waypoints.size() == 0) {
			reset();
		}

		Point last = waypoints.peekLast();
		int deltaX = x - last.getX();
		int deltaY = y - last.getY();
		int direction = DirectionUtil.direction(deltaX, deltaY);

		if (direction > -1) {
			waypoints.add(new Point(x, y, direction));
		}
	}

	public void addToPath(Location location) {
		if (waypoints.size() == 0) {
			reset();
		}

		Point last = waypoints.peekLast();
		int deltaX = location.getX() - last.getX();
		int deltaY = location.getY() - last.getY();
		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));

		for (int i = 0; i < max; i++) {
			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}

			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}

			addStep(location.getX() - deltaX, location.getY() - deltaY);
		}
	}

	public abstract boolean canMoveTo(int paramInt);

	public abstract boolean canMoveTo(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

	public void finish() {
		waypoints.removeFirst();
	}

	public void flag() {
		flag = true;
		forced = false;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public boolean hasDirection() {
		return entity.getDirection1() != -1;
	}

	public boolean isFlagged() {
		return flag;
	}

	public boolean isForced() {
		return forced;
	}

	public boolean isForceMove() {
		return forceMove;
	}

	public boolean moving() {
		return waypoints.size() > 0 && entity.getEF().canWalk(entity, false);
	}

	public abstract void process();

	public void reset() {
		waypoints.clear();
		Location p = entity.getLocation();
		waypoints.add(new Point(p.getX(), p.getY(), -1));
	}

	public void resetMoveDirections() {
		entity.setDirection1(-1);
		entity.setDirection2(-1);
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public void setForceMove(boolean forceMove) {
		this.forceMove = forceMove;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public void setPath(Deque<Point> path) {
		waypoints = path;
	}

	public void walkTo(int x, int y) {
		Location location = entity.getLocation();
		int newX = location.getX() + x;
		int newY = location.getY() + y;
		reset();
		addToPath(new Location(newX, newY));
		finish();
	}

	/**
	 * Sets the movement to stop
	 *
	 * @param stop
	 */
	public void setStopMovement(boolean stop) {
		stopMovement = stop;
	}

	public boolean isStopMovement() {
		return stopMovement;
	}

}
