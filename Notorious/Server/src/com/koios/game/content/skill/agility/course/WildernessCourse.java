package com.koios.game.content.skill.agility.course;

import com.koios.game.content.skill.agility.Course;
import com.koios.game.content.skill.agility.obstacle.Jumpable;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.content.skill.agility.obstacle.RopeSwing;
import com.koios.game.content.skill.agility.obstacle.Walkable;
import com.koios.game.content.skill.agility.obstacle.Obstacle.ObstacleType;
import com.koios.game.model.Location;

/**
 * Represents the Wilderness Agility Course for the Agility skill.
 *
 * @author Michael | Chex
 */
public class WildernessCourse extends Course {

	/** An instance of the Wilderness agility course used in static methods. */
	private static final WildernessCourse instace = new WildernessCourse();

	/**
	 * Constructs a new course for the Wilderness Agility Course. This course cannot be instantiated, use the <code>getInstance</code> method instead.
	 */
	private WildernessCourse() {
		addObstacles(new Obstacle[] {
				/** Pipe. */
				new Walkable(0, 2288, ObstacleType.PIPE, 12.5f, 12, new Location(3004, 3937, 0), new Location(3004, 3950, 0)),

				/** Rope. */
				new RopeSwing(1, 2283, ObstacleType.ROPE_SWING, 20, new Location(3005, 3953, 0), new Location(3005, 3958, 0)),

				/** Stepping stones. */
				new Jumpable(2, 2311, ObstacleType.STEPPING_STONE, 20, 13, new Location(3002, 3960, 0), new Location(2996, 3960, 0)),

				/** Slippery log. */
				new Walkable(3, 2297, ObstacleType.LOG, 20, 8, new Location(3002, 3945, 0), new Location(2994, 3945, 0)),

				/** Rocks. */
				new Walkable(4, 2328, ObstacleType.ROCKS, 0, 8, new Location(2993, 3937, 0), new Location(2993, 3933, 0), true),
				new Walkable(4, 2328, ObstacleType.ROCKS, 0, 8, new Location(2994, 3937, 0), new Location(2994, 3933, 0), true),
				new Walkable(4, 2328, ObstacleType.ROCKS, 0, 8, new Location(2995, 3937, 0), new Location(2995, 3933, 0), true), });
	}

	/**
	 * Gets the instance of the course.
	 *
	 * @return An instance of the Barbarian agility course used in static methods.
	 */
	public static WildernessCourse getInstace() {
		return instace;
	}

	@Override
	protected int getLevelRequirement() {
		return 52;
	}

	@Override
	public float bonusExp() {
		return 498.9f;
	}
}