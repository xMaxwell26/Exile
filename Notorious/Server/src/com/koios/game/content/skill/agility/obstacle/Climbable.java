package com.koios.game.content.skill.agility.obstacle;

import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * A climb-able object is a type of {@linkplain Obstacle} used in a course. It can be called when the player needs to handle climbing to a different height.
 *
 * @author Michael | Chex
 */
public class Climbable extends Obstacle {

	/**
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id of the log.
	 * @param exp The experience reward for walking on the log.
	 * @param delay The amount of ticks it takes to walk across the log.
	 * @param objectDescription The description of the action when climbing.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public Climbable(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition) {
		super(ordinal, object, obstacleType, exp, delay, startPosition, endPosition);
	}

	@Override
	public void performPreInteraction(Player player) {
		player.playAnimation(new Animation(getObstacleType().getAnimation()));
		player.sendMessage(getObstacleType().getPreInteraction());
	}

	@Override
	public void performInteraction(Player player) {
		player.setPosition(getEndPosition().getX() + (getObject() == 20211 ? -2 : 0), player.getLocation().getY(), getEndPosition().getZ());
		player.playAnimation(new Animation(65535));

		if (getObstacleType().getPostInteraction() != null) {
			player.sendMessage(getObstacleType().getPostInteraction());
		}
	}

	@Override
	public Obstacle createNewInstance() {
		return new Climbable(getOrdinal(), getObject(), getObstacleType(), getExp(), getDelay(), getStartPosition(), getEndPosition());
	}
}