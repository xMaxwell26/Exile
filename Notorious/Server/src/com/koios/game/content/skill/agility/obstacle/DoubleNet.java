package com.koios.game.content.skill.agility.obstacle;

import com.koios.GameService;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * A double net object is a type of {@linkplain Obstacle} used in a course. It can be called when the player needs to climb and decline on a net.
 *
 * @author Michael
 */
public class DoubleNet extends Obstacle {

	/**
	 * Constructs a new {@linkplain DoubleNet} obstacle.
	 *
	 * @param object The object id of the log.
	 * @param obstacleType The type of obstacle this is.
	 * @param exp The experience reward for walking on the log.
	 * @param objectDescription The description of the action when walking on the log.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public DoubleNet(int ordinal, int object, ObstacleType obstacleType, float exp, Location startPosition, Location endPosition) {
		super(ordinal, object, obstacleType, exp, 5, startPosition, endPosition);
	}

	@Override
	public void performPreInteraction(Player player) {
		player.playAnimation(new Animation(getObstacleType().getAnimation()));

		GameService.getTaskScheduler().schedule(new Task() {

			@Override
			protected void execute() {
				int faceX = player.getLocation().getX() + getStartPosition().getX() - getEndPosition().getX();
				int faceY = player.getLocation().getY() + getStartPosition().getY() - getEndPosition().getY();
				player.setFacePosition(faceX, faceY);
				player.setPosition(getEndPosition().getX(), getEndPosition().getY(), getEndPosition().getZ());
				stop();
			}
		});

		player.sendMessage(getObstacleType().getPreInteraction());
	}

	@Override
	public void performInteraction(Player player) {
		// This class has a special case where the actual interaction is handled
		// during the pre-interaction phase.
	}

	@Override
	public Obstacle createNewInstance() {
		return new DoubleNet(getOrdinal(), getObject(), getObstacleType(), getExp(), getStartPosition(), getEndPosition());
	}
}
