package com.koios.game.content.skill.agility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.agility.course.BarbarianCourse;
import com.koios.game.content.skill.agility.course.GnomeCourse;
import com.koios.game.content.skill.agility.course.WildernessCourse;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * @author Michael | Chex
 */
public abstract class Course {

	/** The course's intractable objects. */
	private final Map<Location, Obstacle> obstacles = new HashMap<>();

	/**
	 * Sets the obstacle array which contains all the obstacles in the course.
	 *
	 * @param obstacles The obstacles in the course.
	 */
	protected void addObstacles(Obstacle[] obstacles) {
		Arrays.stream(obstacles).forEach(obs -> this.obstacles.put(obs.getStartPosition(), obs));
	}

	/**
	 * The course's bonus experience given after the entire course is complete.
	 */
	public abstract float bonusExp();

	/**
	 * Checks if the object id is part of the course.
	 *
	 * @param object The object id to check if it is a valid course obstacle.
	 * @return <code>true</code> if the object is a valid course obstacle.
	 */
	public Obstacle forData(int object, Location position) {
		Obstacle obstacle = obstacles.get(position);

		if (obstacle != null && obstacle.getObject() == object) {
			return obstacle;
		}

		return null;
	}

	/** The level requirement to complete this course. */
	protected abstract int getLevelRequirement();

	/**
	 * Handles the interaction with an object. If the object is an {@linkplain Obstacle}, the player's progress in the course will progress.
	 *
	 * @param player The player interacting with objects.
	 * @param object The object id being interacted with.
	 * @return <code>true</code> if the interaction was successful.
	 */
	public void interact(Player player, Obstacle obstacle) {

		if (obstacle == null) {
			return;
		} else {
			obstacle = obstacle.createNewInstance();
		}

		if (player.isSkilling[Skill.AGILITY]) {
			return;
		}

		if (player.getSkill().getLevel(Skill.AGILITY) < getLevelRequirement()) {
			player.sendMessage("You need an agility level of " + getLevelRequirement() + " to train here.");
			return;
		}

		final Obstacle finalObstacle = obstacle;
		final Course finalCourse = this;

		player.isSkilling[Skill.AGILITY] = true;
		player.setStopPacket(true);
		player.getMovementHandler().setForceMove(true);
		finalObstacle.setRunState(player.getRunEnergy().isRunning());
		finalObstacle.setWalkIndex(player.getWalkAnim(player.getEquipment().get(3)));
		finalObstacle.setRunIndex(player.getRunAnim(player.getEquipment().get(3)));

		if (!finalObstacle.getStartPosition().equals(player.getLocation())) {
			player.teleport(new Location(finalObstacle.getStartPosition().getX(), finalObstacle.getStartPosition().getY(), finalObstacle.getStartPosition().getZ()));
		}

		finalObstacle.performPreInteraction(player);

		GameService.getTaskScheduler().schedule(new Task(finalObstacle.getDelay()) {

			@Override
			protected void execute() {
				player.getRunEnergy().setRunning(finalObstacle.initialRunState());
				player.walkAnim = -1;
				player.runAnim = -1;
				player.requestUpdates();
				player.getMovementHandler().setForceMove(false);

				finalObstacle.performInteraction(player);

				player.agilityFlags |= 1 << finalObstacle.getOrdinal();

				float exp = finalObstacle.getExp();

				if (finalObstacle.isFinalObstacle()) {
					int requiredFlags = 0;
					String endMessage = "";
					if (finalCourse instanceof GnomeCourse) {
						requiredFlags = 0b0111_1111;
						endMessage = "Gnome Agility Course and receive 5 tickets";
						if (player.agilityFlags == requiredFlags) {
							player.getInventory().add(new Item(2996, 5));
							AchievementManager.performed(player, Achievement.COMPLETE_GNOME_COURSE);
						}
					} else if (finalCourse instanceof BarbarianCourse) {
						requiredFlags = 0b1111_1111;
						endMessage = "Barbarian Agility Course";
					} else if (finalCourse instanceof WildernessCourse) {
						requiredFlags = 0b0001_1111;
						endMessage = "Wilderness Agility Course";
					}

					if ((player.agilityFlags & requiredFlags) == 1 << finalObstacle.getOrdinal()) {
						player.sendMessage("You have not completed any of the other obstacles and receive no tickets.");
					} else if ((player.agilityFlags & requiredFlags) != requiredFlags) {
						player.sendMessage("You have not completed all of the obstacles and receive no tickets.");
					} else {
						exp += bonusExp();
						player.sendMessage("You have completed the " + endMessage + ".");
					}
					player.agilityFlags = 0;
				}

				player.getSkill().addExperience(Skill.AGILITY, Constants.AGILITY_EXPERIENCE * exp);

				player.setPosition(finalObstacle.getEndPosition().getX(), finalObstacle.getEndPosition().getY(), finalObstacle.getEndPosition().getZ());
				player.isSkilling[Skill.AGILITY] = false;
				player.setStopPacket(false);
				stop();
			}
		});
	}

}
