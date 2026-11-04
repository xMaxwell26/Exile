package com.koios.game.content.skill.agility.obstacle;

import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * A walkable object is a type of {@linkplain Obstacle} used in a course. It can be called when the player needs to handle walking over objects such as logs or ropes.
 *
 * @author Michael
 */
public class Walkable extends Obstacle {

	/**
	 * Constructs a new {@linkplain Walkable} obstacle.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id of the log.
	 * @param exp The experience reward for walking on the log.
	 * @param delay The amount of ticks it takes to walk across the log.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public Walkable(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition, boolean finalObstacle) {
		super(ordinal, object, obstacleType, exp, delay, startPosition, endPosition, finalObstacle);
	}

	/**
	 * Constructs a new {@linkplain Walkable} obstacle.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id of the log.
	 * @param exp The experience reward for walking on the log.
	 * @param delay The amount of ticks it takes to walk across the log.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public Walkable(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition) {
		super(ordinal, object, obstacleType, exp, delay, startPosition, endPosition);
	}

	@Override
	public void performPreInteraction(Player player) {
		player.getRunEnergy().setRunning(false);
		player.walkAnim = getObstacleType().getAnimation();

		player.requestUpdates();

		int deltaX = (getEndPosition().getX() == -1 ? player.getLocation().getX() : getEndPosition().getX()) - player.getLocation().getX();
		int deltaY = (getEndPosition().getY() == -1 ? player.getLocation().getY() : getEndPosition().getY()) - player.getLocation().getY();

		player.getMovementHandler().walkTo(deltaX, deltaY);

		player.sendMessage(getObstacleType().getPreInteraction());
	}

	@Override
	public void performInteraction(Player player) {
		if (getObstacleType().getPostInteraction() != null) {
			player.sendMessage(getObstacleType().getPostInteraction());
		}
	}

	@Override
	public Obstacle createNewInstance() {
		return new Walkable(getOrdinal(), getObject(), getObstacleType(), getExp(), getDelay(), getStartPosition(), getEndPosition(), isFinalObstacle());
	}

}