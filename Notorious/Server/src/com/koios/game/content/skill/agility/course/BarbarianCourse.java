package com.koios.game.content.skill.agility.course;

import com.koios.game.content.skill.agility.Course;
import com.koios.game.content.skill.agility.obstacle.Climbable;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.content.skill.agility.obstacle.RopeSwing;
import com.koios.game.content.skill.agility.obstacle.Walkable;
import com.koios.game.content.skill.agility.obstacle.Obstacle.ObstacleType;
import com.koios.game.model.Location;

/**
 * Represents the Barbarian Outpost Agility Course for the Agility skill.
 *
 * @author Michael | Chex
 */
public class BarbarianCourse extends Course {

	/** An instance of the Barbarian agility course used in static methods. */
	private static final BarbarianCourse instace = new BarbarianCourse();

	/**
	 * Constructs a new course for the Barbarian Agility Course. This course cannot be instantiated, use the <code>getInstance</code> method instead.
	 */
	private BarbarianCourse() {
		addObstacles(new Obstacle[] {
				/** Rope swing. */
				new RopeSwing(0, 2282, ObstacleType.ROPE_SWING, 22, new Location(2551, 3554, 0), new Location(2551, 3549, 0)),

				/** Slippery log. */
				new Walkable(1, 2294, ObstacleType.LOG, 13.5f, 10, new Location(2551, 3546, 0), new Location(2541, 3546, 0)),

				/** Net. */
				new Climbable(2, 2284, ObstacleType.NETTING, 8, 2, new Location(2539, 3546, 0), new Location(2537, 3546, 1)),
				new Climbable(2, 2284, ObstacleType.NETTING, 8, 2, new Location(2539, 3545, 0), new Location(2537, 3545, 1)),

				/** Ledge. */
				new Walkable(3, 2302, ObstacleType.LEDGE, 22, 5, new Location(2536, 3547, 1), new Location(2532, 3547, 1)),

				/** Ladder. */
				new Climbable(4, 3205, ObstacleType.LADDER, 0, 2, new Location(2532, 3546, 1), new Location(2532, 3546, 0)),

				/** Crumbling walls. */
				new Walkable(5, 1948, ObstacleType.CRUMBLING_WALL, 13.5f, 2, new Location(2535, 3553, 0), new Location(2537, 3553, 0)),
				new Walkable(6, 1948, ObstacleType.CRUMBLING_WALL, 13.5f, 2, new Location(2538, 3553, 0), new Location(2540, 3553, 0)),
				new Walkable(7, 1948, ObstacleType.CRUMBLING_WALL, 13.5f, 2, new Location(2541, 3553, 0), new Location(2543, 3553, 0), true), });
	}

	/**
	 * Gets the instance of the course.
	 *
	 * @return An instance of the Barbarian agility course used in static methods.
	 */
	public static BarbarianCourse getInstace() {
		return instace;
	}

	@Override
	protected int getLevelRequirement() {
		return 30;
	}

	@Override
	public float bonusExp() {
		return 46.5f;
	}
}