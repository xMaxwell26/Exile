package com.koios.game.content.skill.agility.course;

import com.koios.game.content.skill.agility.Course;
import com.koios.game.content.skill.agility.obstacle.Climbable;
import com.koios.game.content.skill.agility.obstacle.DoubleNet;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.content.skill.agility.obstacle.Walkable;
import com.koios.game.content.skill.agility.obstacle.Obstacle.ObstacleType;
import com.koios.game.model.Location;

/**
 * Represents the Gnome Agility Course for the Agility skill.
 *
 * @author Michael | Chex
 */
public class GnomeCourse extends Course {

	/** An instance of the Gnome agility course. */
	private static final GnomeCourse instace = new GnomeCourse();

	/**
	 * Constructs a new course for the Gnome Agility Course. This course cannot be instantiated, use the <code>getInstance</code> method instead.
	 */
	private GnomeCourse() {
		addObstacles(
				new Obstacle[] {

				/** Slippery log. */
				new Walkable(0, 2295, ObstacleType.LOG, 7.5f, 6, new Location(2474, 3436, 0), new Location(2474, 3429, 0)),

				/** First row of netting. */
				new Climbable(1, 2285, ObstacleType.NETTING, 7.5f, 2, new Location(2476, 3426, 0), new Location(2476, 3424, 1)),
				new Climbable(1, 2285, ObstacleType.NETTING, 7.5f, 2, new Location(2475, 3426, 0), new Location(2475, 3424, 1)),
				new Climbable(1, 2285, ObstacleType.NETTING, 7.5f, 2, new Location(2474, 3426, 0), new Location(2474, 3424, 1)),
				new Climbable(1, 2285, ObstacleType.NETTING, 7.5f, 2, new Location(2473, 3426, 0), new Location(2473, 3424, 1)),
				new Climbable(1, 2285, ObstacleType.NETTING, 7.5f, 2, new Location(2472, 3426, 0), new Location(2472, 3424, 1)),
				new Climbable(1, 2285, ObstacleType.NETTING, 7.5f, 2, new Location(2471, 3426, 0), new Location(2471, 3424, 1)),

				/** First tree branch. */
				new Climbable(2, 2313, ObstacleType.TREE_BRANCH_UP, 5.0f, 2, new Location(2472, 3422, 1), new Location(2473, 3420, 2)),
				new Climbable(2, 2313, ObstacleType.TREE_BRANCH_UP, 5.0f, 2, new Location(2473, 3423, 1), new Location(2473, 3420, 2)),
				new Climbable(2, 2313, ObstacleType.TREE_BRANCH_UP, 5.0f, 2, new Location(2474, 3422, 1), new Location(2473, 3420, 2)),

				/** Balance rope. */
				new Walkable(3, 2312, ObstacleType.TIGHT_ROPE, 7.5f, 6, new Location(2477, 3420, 2), new Location(2483, 3420, 2)),

				/** Second tree branch. */
				new Climbable(4, 2314, ObstacleType.TREE_BRANCH_DOWN, 5.0f, 2, new Location(2486, 3420, 2), new Location(2486, 3420, 0)),
				new Climbable(4, 2314, ObstacleType.TREE_BRANCH_DOWN, 5.0f, 2, new Location(2485, 3419, 2), new Location(2485, 3419, 0)),
				new Climbable(4, 2314, ObstacleType.TREE_BRANCH_DOWN, 5.0f, 2, new Location(2486, 3418, 2), new Location(2486, 3418, 0)),

				/** Second row of netting. */
				new DoubleNet(5, 2286, ObstacleType.NETTING, 7.5f, new Location(2483, 3425, 0), new Location(2483, 3427, 0)),
				new DoubleNet(5, 2286, ObstacleType.NETTING, 7.5f, new Location(2484, 3425, 0), new Location(2484, 3427, 0)),
				new DoubleNet(5, 2286, ObstacleType.NETTING, 7.5f, new Location(2485, 3425, 0), new Location(2485, 3427, 0)),
				new DoubleNet(5, 2286, ObstacleType.NETTING, 7.5f, new Location(2486, 3425, 0), new Location(2486, 3427, 0)),
				new DoubleNet(5, 2286, ObstacleType.NETTING, 7.5f, new Location(2487, 3425, 0), new Location(2487, 3427, 0)),
				new DoubleNet(5, 2286, ObstacleType.NETTING, 7.5f, new Location(2488, 3425, 0), new Location(2488, 3427, 0)),

				/** Tunnels. */
				new Walkable(6, 154, ObstacleType.PIPE, 7.5f, 6, new Location(2484, 3430, 0), new Location(2484, 3437, 0), true),
				new Walkable(6, 4058, ObstacleType.PIPE, 7.5f, 6, new Location(2487, 3430, 0), new Location(2487, 3437, 0), true), });
	}

	/**
	 * Gets the instance of the course.
	 *
	 * @return An instance of the Gnome agility course used for static reference.
	 */
	public static GnomeCourse getInstace() {
		return instace;
	}

	@Override
	protected int getLevelRequirement() {
		return 1;
	}

	@Override
	public float bonusExp() {
		return 39.0f;
	}
}