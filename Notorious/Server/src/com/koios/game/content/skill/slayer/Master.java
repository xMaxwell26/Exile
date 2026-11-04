package com.koios.game.content.skill.slayer;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.skill.slayer.assignments.AssignmentConstants;

/**
 * An enum containing masters.
 *
 * @author Ben Stulpin
 */
public enum Master {

	MAZCHNA(8274, 1, 40, 8, AssignmentConstants.MAZCHNA_ASSIGNMENTS), VANNAKA(1597, 40, 45, 12, AssignmentConstants.VANNAKA_ASSIGNMENTS), CHAELDAR(1598, 70, 50, 15,
			AssignmentConstants.CHAELDAR_ASSIGNMENTS), DURADEL(8275, 100, 65, 20, AssignmentConstants.DURADEL_ASSIGNMENTS), KURADEL(9085, 110, 75, 25,
					AssignmentConstants.DURADEL_ASSIGNMENTS);

	/**
	 * Gets the master from the id.
	 *
	 * @return the master.
	 */
	public static Master forId(int id) {
		return masters.get(id);
	}

	/**
	 * The id.
	 */
	private final int id;

	/**
	 * The level.
	 */
	private final int level;

	/**
	 * The maximum amount.
	 */
	private final int maximumAmount;

	/**
	 * The points to reward.
	 */
	private final int points;

	/**
	 * The tasks.
	 */
	private final int[] tasks;

	/**
	 * A map containing all of the masters.
	 */
	private static final Map<Integer, Master> masters = new HashMap<>();

	static {
		for (Master master : Master.values()) {
			masters.put(master.getId(), master);
		}
	}

	private Master(int id, int level, int maximumAmount, int points, int[] tasks) {
		this.id = id;
		this.level = level;
		this.maximumAmount = maximumAmount;
		this.points = points;
		this.tasks = tasks;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Gets the maximum amount.
	 *
	 * @return the maximum amount.
	 */
	public int getMaximumAmount() {
		return maximumAmount;
	}

	/**
	 * Gets the points to reward.
	 *
	 * @return the points.
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Gets the tasks.
	 *
	 * @return the tasks.
	 */
	public int[] getTasks() {
		return tasks;
	}

}