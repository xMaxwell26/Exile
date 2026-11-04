package com.koios.game.content.cluescroll;

import java.util.function.Predicate;

import com.koios.GameService;
import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Represents a clue scroll type.
 *
 * @author Michael | Chex
 */
public abstract class ClueScroll {

	/**
	 * The item id of the clue scroll.
	 */
	private final int item;

	/**
	 * The location to complete the clue scroll.
	 */
	private final Predicate<Location> endLocation;

	/**
	 * The clue scroll difficulty.
	 */
	private final Difficulty difficulty;

	/**
	 * Constructs a new Clue Scroll object.
	 *
	 * @param item - The clue scroll item id.
	 */
	public ClueScroll(int item, Predicate<Location> emoteLocation, Difficulty difficulty) {
		this.item = item;
		endLocation = emoteLocation;
		this.difficulty = difficulty;
	}

	/**
	 * Gets the clue scroll difficulty.
	 *
	 * @return The difficulty.
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * Gets the item id of the clue scroll.
	 *
	 * @return The item id.
	 */
	public final int getItem() {
		return item;
	}

	/**
	 * Gets a random reward from the clue scroll difficulty's loot table.
	 *
	 * @return A random item from the difficulty's loot table.
	 */
	public Item getRandomReward() {
		return getDifficulty().getRewards().getReward();
	}

	/**
	 * Opens a clue scroll.
	 *
	 * @param player - The player opening the clue scroll.
	 */
	public abstract void open(Player player);

	/**
	 * Attempts to dig in a location for a casket for the next clue or their reward.
	 *
	 * @param player - The player digging.
	 */
	public void dig(Player player) {
		if (player.getAttribute("KILL_AGENT", false)) {
			return;
		}

		player.playAnimation(new Animation(831));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(65535));
				attemptCompletion(player);
				stop();
			}

		});
	}

	/**
	 * Checks if a player is in the correct location for this clue scroll.
	 *
	 * @param player - The player to check their coordinates.
	 * @return <code>True</code> if the player is in the correct location.
	 */
	public boolean inEndLocation(Player player) {
		return getEndLocation().test(player.getLocation());
	}

	/**
	 * Gets the location where the player completes the clue scroll.
	 *
	 * @return The dig location.
	 */
	private Predicate<Location> getEndLocation() {
		return endLocation;
	}

	/**
	 * Handles the completion action, if the player is in the correct location.
	 *
	 * @param player - The player digging.
	 */
	public abstract void attemptCompletion(Player player);

	/**
	 * Rewards the player with either the next clue scroll in the treasure trails or rewards with the final item; a casket.
	 * <p>
	 *
	 * <pre>
	 * Easy scrolls have a maximum of 5 clues per trail.
	 * Medium scrolls have a maximum of 6 clues per trail.
	 * Hard scrolls have a maximum of 8 clues per trail.
	 * </pre>
	 *
	 * @param player - The player to receive the reward.
	 * @param acquirement - The means of which the player has acquired the reward.
	 */
	public void reward(Player player, String acquirement) {
		Item item;
		boolean nextClue = false;

		switch (getDifficulty()) {
		case EASY:
			nextClue = player.getEasyCluesCompleted() < 5 && Math.random() > 0.5;

			if (nextClue) {
				player.setEasyCluesCompleted(player.getEasyCluesCompleted() + 1);
			} else {
				player.setEasyCluesCompleted(0);
			}
			break;

		case MEDIUM:
			nextClue = player.getMediumCluesCompleted() < 6 && Math.random() > 0.5;

			if (nextClue) {
				player.setMediumCluesCompleted(player.getMediumCluesCompleted() + 1);
			} else {
				player.setMediumCluesCompleted(0);
			}
			break;

		case HARD:
			nextClue = player.getHardCluesCompleted() < 8 && Math.random() > 0.5;

			if (nextClue) {
				player.setHardCluesCompleted(player.getHardCluesCompleted() + 1);
			} else {
				player.setHardCluesCompleted(0);
			}
			break;
		}

		if (!nextClue) {
			TreasureTrails.showChatItem(player, ClueScrollConstants.CASKET_HARD, acquirement + " a casket!");
			player.sendMessage("Well done, you've completed the Treasure Trail!");
			item = new Item(getDifficulty().equals(Difficulty.EASY) ? ClueScrollConstants.CASKET_EASY
					: getDifficulty().equals(Difficulty.MEDIUM) ? ClueScrollConstants.CASKET_MEDIUM : ClueScrollConstants.CASKET_HARD);
		} else {
			item = ClueScrollConstants.getRandomClue(player, getDifficulty());

			if (item == null) {
				TreasureTrails.showChatItem(player, ClueScrollConstants.CASKET_HARD, acquirement + " a casket!");
				player.sendMessage("Well done, you've completed the Treasure Trail!");
				item = new Item(getDifficulty().equals(Difficulty.EASY) ? ClueScrollConstants.CASKET_EASY
						: getDifficulty().equals(Difficulty.MEDIUM) ? ClueScrollConstants.CASKET_MEDIUM : ClueScrollConstants.CASKET_HARD);
			} else {
				TreasureTrails.showChatItem(player, item.getId(), acquirement + " another clue!");
			}
		}

		player.getInventory().remove(Item.of(getItem()));
		player.getInventory().add(item);
	}

}
