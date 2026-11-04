package com.koios.game.content.skill.agility.obstacle;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * A rope is a type of {@linkplain Obstacle} used in a course. It can be called when the player needs to handle swinging over objects with a rope.
 *
 * @author Michael | Chex
 */
public class RopeSwing extends Obstacle {

	/**
	 * Constructs a new {@linkplain RopeSwing} obstacle.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id of the log.
	 * @param exp The experience reward for walking on the log.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The final position where the player ends up.
	 */
	public RopeSwing(int ordinal, int object, ObstacleType obstacleType, float exp, Location startPosition, Location endPosition) {
		super(ordinal, object, obstacleType, exp, 3, startPosition, endPosition);
	}

	@Override
	public void performPreInteraction(Player player) {
		player.getRunEnergy().setRunning(true);
		player.walkAnim = getObstacleType().getAnimation();
		player.runAnim = getObstacleType().getAnimation();
		player.requestUpdates();
		player.sendMessage(getObstacleType().getPreInteraction());
		GameService.getObjectManager().sendObjectAnimation(2551, 3550, 497, 10, 0);
		player.getMovementHandler().walkTo(getEndPosition().getX() - player.getLocation().getX(), getEndPosition().getY() - player.getLocation().getY());

		GameService.getTaskScheduler().schedule(new Task() {

			@Override
			protected void execute() {
				player.sendMessage("Jump for your life!");
				stop();
			}

		});
	}

	@Override
	public void performInteraction(Player player) {
		if (getObstacleType().getPostInteraction() != null) {
			player.sendMessage(getObstacleType().getPostInteraction());
		}
		player.walkAnim = -1;
		player.runAnim = -1;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	public Obstacle createNewInstance() {
		return new RopeSwing(getOrdinal(), getObject(), getObstacleType(), getExp(), getStartPosition(), getEndPosition());
	}
}
