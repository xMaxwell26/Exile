package com.koios.game.content.skill.agility.obstacle;

import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * Represents an obstacle the player can interact with through the {@linkplain Agility} skill.
 *
 * @author Michael | Chex
 */
public abstract class Obstacle {

	public static enum ObstacleType {
		LOG(762, "You walk carefully across the slippery log...", "...You make it safely to the other side."), TIGHT_ROPE(762, "You carefully cross the tightrope...",
				"...You make it safely to the other side."), ROCKS(839, "You climb over the rocks...", "...You make it safely to the other side."), STEPPING_STONE(3067,
						"You jump across the stepping stones...",
						"...You make it safely to the other side."), LEDGE(756, "You walk carefully across the ledge...", "...You make it safely to the other side."), ROPE_SWING(
								751, "You swing across the rope...", "...You make it safely to the other side."), CRUMBLING_WALL(839, "You climb over the crumbling wall.",
										null), NETTING(3063, "You climb the netting.", null), TREE_BRANCH_UP(828, "You climb the tree to the platform above.",
												null), TREE_BRANCH_DOWN(828, "You climb down the tree and land on the ground.", null), PIPE(844, "You squeeze into the pipe...",
														null), LADDER(828, "You climb down the ladder.", null), DITCH(6132, "You jump across the ditch...", null);

		private int animation;
		private String preInteraction;
		private String postInteraction;

		private ObstacleType(int animation, String preInteraction, String postInteraction) {
			this.animation = animation;
			this.preInteraction = preInteraction;
			this.postInteraction = postInteraction;
		}

		public int getAnimation() {
			return animation;
		}

		public String getPreInteraction() {
			return preInteraction;
		}

		public String getPostInteraction() {
			return postInteraction;
		}
	}

	/**
	 * The position where the player is supposed to end up after completing the obstacle.
	 */
	private final Location endPosition;

	/**
	 * The position where the player begins interaction with the obstacle.
	 */
	private final Location startPosition;

	/**
	 * The index in the completed course boolean array.
	 */
	private final int ordinal;

	/**
	 * Whether or not the player was running before interacting with an obstacle.
	 */
	private boolean runState = true;

	/** The initial walking index. */
	private int walkIndex;

	/** The initial running index. */
	private int runIndex;

	/** The amount of ticks (600ms per tick) required to complete the obstacle. */
	private final int delay;

	/** The object id for this obstacle. */
	private final int object;

	/** The experience reward for this obstacle. */
	private final float exp;

	/**
	 * <code>true</code> will enable the obstacle to reward the player with the course's bonus experience.
	 */
	private boolean finalObstacle;

	private ObstacleType obstacleType;

	/**
	 * Constructs a new {@linkplain Obstacle}.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id.
	 * @param obstacleType The type of obstacle this is.
	 * @param exp The experience reward.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The position where the player is supposed to end up after completing the obstacle.
	 * @param delay The amount of ticks (600ms per tick) required to complete the obstacle.
	 * @param finalObstacle <code>true</code> will enable the obstacle will reward the player with the course's bonus experience.
	 */
	public Obstacle(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition, boolean finalObstacle) {
		this(ordinal, object, obstacleType, exp, delay, startPosition, endPosition);
		this.finalObstacle = finalObstacle;
		this.obstacleType = obstacleType;
	}

	/**
	 * Constructs a new {@linkplain Obstacle}.
	 *
	 * @param ordinal The index in the completed course boolean array.
	 * @param object The object id.
	 * @param obstacleType The type of obstacle this is.
	 * @param exp The experience reward.
	 * @param startPosition The position where the player begins interaction with the obstacle.
	 * @param endPosition The position where the player is supposed to end up after completing the obstacle.
	 * @param delay The amount of ticks (600ms per tick) required to complete the obstacle.
	 */
	public Obstacle(int ordinal, int object, ObstacleType obstacleType, float exp, int delay, Location startPosition, Location endPosition) {
		this.ordinal = ordinal;
		this.object = object;
		this.exp = exp;
		this.delay = delay;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.obstacleType = obstacleType;
	}

	/**
	 * Checks if this obstacle is the final obstacle of an agility course.
	 *
	 * @return <code>true</code> if this obstacle is the final obstacle of an agility course.
	 */
	public boolean isFinalObstacle() {
		return finalObstacle;
	}

	/**
	 * Gets the obstacle type.
	 *
	 * @return The obstacle type.
	 */
	public ObstacleType getObstacleType() {
		return obstacleType;
	}

	/**
	 * Gets the object id for this obstacle.
	 *
	 * @return The object id.
	 */
	public int getObject() {
		return object;
	}

	/**
	 * Gets the experience reward for this obstacle.
	 *
	 * @return The experience reward.
	 */
	public float getExp() {
		return exp;
	}

	/**
	 * Gets the initial running state of the player before interacting with the obstacle.
	 */
	public boolean initialRunState() {
		return runState;
	}

	/**
	 * Gets the initial walking index of the player before interacting with the obstacle.
	 */
	public int initialWalkIndex() {
		return walkIndex;
	}

	/**
	 * Gets the initial running index of the player before interacting with the obstacle.
	 */
	public int initialRunIndex() {
		return runIndex;
	}

	/**
	 * Gets the amount of ticks (600ms per tick) required to complete the obstacle.
	 *
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the position where the player begins interaction with the obstacle.
	 *
	 * @return The start position.
	 */
	public Location getStartPosition() {
		return startPosition;
	}

	/**
	 * Gets the position where the player is supposed to end up after completing the obstacle.
	 *
	 * @return The end position.
	 */
	public Location getEndPosition() {
		return endPosition;
	}

	/**
	 * Gets the ordinal The index in the completed course boolean array.
	 *
	 * @return The ordinal.
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * Sets what running state the player was in before interacting with the obstacle.
	 *
	 * @param runTemp The initial running state.
	 */
	public void setRunState(boolean runTemp) {
		runState = runTemp;
	}

	public void setWalkIndex(int walkIndex) {
		this.walkIndex = walkIndex;
	}

	public void setRunIndex(int runIndex) {
		this.runIndex = runIndex;
	}

	/**
	 * Performs the pre-interaction details.
	 */
	public abstract void performPreInteraction(Player player);

	/**
	 * Performs the interaction details.
	 */
	public abstract void performInteraction(Player player);

	/**
	 * Creates a new instance of an obstacle already initialized.
	 *
	 * @return A new instance of this obstacle.
	 */
	public abstract Obstacle createNewInstance();
}