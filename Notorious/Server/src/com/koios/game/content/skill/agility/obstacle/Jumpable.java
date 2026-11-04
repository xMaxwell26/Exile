package com.koios.game.content.skill.agility.obstacle;

import com.koios.GameService;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * A jump-able object is a type of {@linkplain Obstacle} used in a course. It can be called when the player needs to handle jumping to a destination such as stepping stones.
 *
 * @author Michael
 */
public class Jumpable extends Obstacle {

	/**
	 * Constructs a new {@linkplain Jumpable} obstacle.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id of the log.
	 * @param exp The experience reward for walking on the log.
	 * @param delay The amount of ticks it takes to walk across the log.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public Jumpable(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition, boolean finalObstacle) {
		super(ordinal, object, obstacleType, exp, delay, startPosition, endPosition, finalObstacle);
	}

	/**
	 * Constructs a new {@linkplain Jumpable} obstacle.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id of the log.
	 * @param exp The experience reward for walking on the log.
	 * @param delay The amount of ticks it takes to walk across the log.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public Jumpable(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition) {
		super(ordinal, object, obstacleType, exp, delay, startPosition, endPosition);
	}

	@Override
	public void performPreInteraction(Player player) {
		player.getRunEnergy().setRunning(false);
		player.walkAnim = getObstacleType().getAnimation();
		player.requestUpdates();

		final int initX = player.getLocation().getX();
		final int initY = player.getLocation().getY();
		final int stepsX = getEndPosition().getX() - initX;
		final int stepsY = getEndPosition().getY() - initY;

		GameService.getTaskScheduler().schedule(new Task(2, true) {

			int currentStepX = 0;
			int currentStepY = 0;

			@Override
			protected void execute() {
				if (stepsX != 0) {
					currentStepX += stepsX > 0 ? 1 : -1;
				}
				if (stepsY != 0) {
					currentStepY += stepsY > 0 ? 1 : -1;
				}
				player.playAnimation(new Animation(3067));

				if (currentStepX == stepsX && currentStepY == stepsY) {
					stop();
				}
			}
		});

		GameService.getTaskScheduler().schedule(new Task(2, false) {

			int currentStepX = 0;
			int currentStepY = 0;

			@Override
			protected void execute() {
				if (stepsX != 0) {
					currentStepX += stepsX > 0 ? 1 : -1;
				}
				if (stepsY != 0) {
					currentStepY += stepsY > 0 ? 1 : -1;
				}
				player.setPosition(initX + currentStepX, initY + currentStepY, player.getLocation().getZ());

				if (currentStepX == stepsX && currentStepY == stepsY) {
					stop();
				}
			}
		});

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
		return new Jumpable(getOrdinal(), getObject(), getObstacleType(), getExp(), getDelay(), getStartPosition(), getEndPosition());
	}
}
