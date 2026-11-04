package com.koios.game.content.skill.agility;

import java.util.Arrays;
import java.util.HashMap;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.content.skill.agility.obstacle.Walkable;
import com.koios.game.content.skill.agility.obstacle.Obstacle.ObstacleType;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class Shortcut {

	public static enum ShortcutType {

		BRIMHAVEN_DUNGEON_PIPE_1(70, new Walkable(0, 9293, ObstacleType.PIPE, 0.25f, 6, new Location(2886, 9799, 0), new Location(2892, 9799, 0), false)), BRIMHAVEN_DUNGEON_PIPE_2(
				70, new Walkable(0, 9293, ObstacleType.PIPE, 0.25f, 6, new Location(2892, 9799, 0), new Location(2886, 9799, 0), false)),

		WILDERNESS_DITCH_1(0, new Walkable(0, 23271, ObstacleType.DITCH, 0, 2, new Location(-1, 3520, 0), new Location(-1, 3523, 0), false)), WILDERNESS_DITCH_2(0,
				new Walkable(0, 23271, ObstacleType.DITCH, 0, 2, new Location(-1, 3523, 0), new Location(-1, 3520, 0), false)),;

		/**
		 * The agility level required.
		 */
		private final int levelRequired;
		private final Obstacle obstacle;

		private ShortcutType(int levelRequired, Obstacle obstacle) {
			this.levelRequired = levelRequired;
			this.obstacle = obstacle;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Obstacle getObstacle() {
			return obstacle;
		}
	}

	public static Shortcut getInstace() {
		return instance;
	}

	public static HashMap<Location, ShortcutType> getShortcuts() {
		return shortcuts;
	}

	private static final Shortcut instance = new Shortcut();

	/** The course's intractable objects. */
	private static HashMap<Location, ShortcutType> shortcuts = new HashMap<>();

	static {
		Arrays.stream(ShortcutType.values()).forEach(type -> {
			shortcuts.put(type.getObstacle().getStartPosition(), type);
		});
	}

	/**
	 * Checks if the object id is a shortcut.
	 *
	 * @param object The object id to check if it is a valid shortcut obstacle.
	 * @return The object if the object is a valid shortcut obstacle.
	 */
	public ShortcutType forData(int object, Location position) {
		ShortcutType shortcutType = shortcuts.get(position);

		if (shortcutType != null && shortcutType.getObstacle().getObject() == object) {
			return shortcutType;
		}

		return null;
	}

	/**
	 * Handles the interaction with an object. If the object is an {@linkplain Obstacle}, the player's progress in the course will progress.
	 *
	 * @param player The player interacting with objects.
	 * @param object The object id being interacted with.
	 * @return <code>true</code> if the interaction was successful.
	 */
	public void interact(Player player, ShortcutType shortcutType) {
		Obstacle obstacle = shortcutType.getObstacle();

		if (obstacle == null) {
			return;
		} else {
			obstacle = obstacle.createNewInstance();
		}

		if (player.isSkilling[Skill.AGILITY]) {
			return;
		}

		if (player.getSkill().getLevel(Skill.AGILITY) < shortcutType.getLevelRequired()) {
			player.sendMessage("You need an agility level of " + shortcutType.getLevelRequired() + " to use this shortcut.");
			return;
		}

		final Obstacle finalObstacle = obstacle;

		player.setStopPacket(true);
		finalObstacle.setRunState(player.getRunEnergy().isRunning());
		finalObstacle.setWalkIndex(player.getWalkAnim(player.getEquipment().get(3)));
		finalObstacle.setRunIndex(player.getRunAnim(player.getEquipment().get(3)));

		int startX = finalObstacle.getStartPosition().getX() == -1 ? player.getLocation().getX() : finalObstacle.getStartPosition().getX();
		int startY = finalObstacle.getStartPosition().getY() == -1 ? player.getLocation().getY() : finalObstacle.getStartPosition().getY();
		int endX = finalObstacle.getEndPosition().getX() == -1 ? player.getLocation().getX() : finalObstacle.getEndPosition().getX();
		int endY = finalObstacle.getEndPosition().getY() == -1 ? player.getLocation().getY() : finalObstacle.getEndPosition().getY();
		player.getMovementHandler().setForceMove(true);
		if (!Location.create(startX, startY, finalObstacle.getStartPosition().getZ()).equals(player.getLocation())) {
			player.teleport(new Location(startX, startY, finalObstacle.getStartPosition().getZ()));
		}

		finalObstacle.performPreInteraction(player);

		GameService.getTaskScheduler().schedule(new Task(finalObstacle.getDelay()) {

			@Override
			protected void execute() {
				player.getRunEnergy().setRunning(finalObstacle.initialRunState());
				player.walkAnim = -1;
				player.runAnim = -1;
				player.requestUpdates();
				finalObstacle.performInteraction(player);

				float exp = finalObstacle.getExp();

				player.getSkill().addExperience(Skill.AGILITY, Constants.AGILITY_EXPERIENCE * exp);
				player.setPosition(endX, endY, finalObstacle.getEndPosition().getZ());
				player.isSkilling[Skill.AGILITY] = false;
				player.setStopPacket(false);
				player.getMovementHandler().setForceMove(false);
				stop();
			}

		});
	}

}
