package com.koios.game.content.achievement;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendSetWidgetColor;

/**
 * A handler for the achievement diaries for the player.
 *
 * @author Mack/Hoodlum
 */
public class AchievementManager {

	/**
	 * Displaying the information of said achievement on the interface
	 *
	 * @param player the player
	 * @param buttonId the actionbutton id
	 */
	public static void onButtonClick(Player player, int buttonId) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

		for (Achievement achievement : Achievement.values()) {
			if (buttonId == achievement.getActionId()) {
				int index = achievement.getUniqueId();
				int progress = player.getAchievementStatus(index);
				int total = achievement.getRequiredAmount();
				String actionType = achievement.getActionType();
				String object = achievement.getObject();

				player.sendMessage(String.format("I have %s %s out of %s %s.", actionType, numberFormat.format(progress), numberFormat.format(total), object));
				break;
			}
		}
	}

	/**
	 * Increases the achievement count for said achievement.
	 *
	 * @param player the player doing the achievement
	 * @param achievement the achievement being done
	 */
	public static void performed(Player player, Achievement achievement) {
		performed(player, achievement, 1);
	}

	/**
	 * Increases the achievement count for said achievement.
	 *
	 * @param player the player doing the achievement
	 * @param achievement the achievement being done
	 * @param increaseAmount the amount in which to performed the achievement by
	 */
	public static void performed(Player player, Achievement achievement, int increaseAmount) {
		Objects.requireNonNull(player, "null player");
		Objects.requireNonNull(achievement, "null achievement");
		int currentState = player.getAchievementStatus(achievement.getUniqueId());
		int maxState = achievement.getRequiredAmount();
		int nextState = currentState + increaseAmount;

		// Test if the achievement has already been completed.
		if (currentState >= maxState) {
			return;
		}

		// Overflow test
		if (nextState > maxState || nextState < 0) {
			nextState = maxState;
		}

		// Test if we've just completed this achievement.
		if (nextState == maxState) {
			int pointReward = achievement.getPoints();
			player.addAchievementPoints(pointReward);
			player.sendMessage("You have just completed the achievement " + achievement.getDescription() + "!");

			if (pointReward > 1) {
				player.sendMessage("You have obtained " + pointReward + " Achievement Points!");
			} else {
				player.sendMessage("You have obtained an Achievement Point!");
			}
		}

		player.setAchievementStatus(achievement.getUniqueId(), nextState);

		// Refresh state (if required)
		int index = achievement.getUniqueId();
		int progress = player.getAchievementStatus(index);
		int total = achievement.getRequiredAmount();
		int id = achievement.getWidgetId();
		boolean started = progress > 0;
		boolean completed = progress >= total;
		int color = completed ? 0xFF00 : started ? 0xFFFF00 : 0xFF0000;
		player.getSession().send(new SendSetWidgetColor(id, color));
	}

	/**
	 * Displays the progress on the achievement interface.
	 */
	public static void setup(Player player) {
		for (Achievement achievement : Achievement.values()) {
			int index = achievement.getUniqueId();
			int progress = player.getAchievementStatus(index);
			int total = achievement.getRequiredAmount();
			int widgetId = achievement.getWidgetId();
			boolean started = progress > 0;
			boolean completed = progress >= total;
			int color = completed ? 0xFF00 : started ? 0xFFFF00 : 0xFF0000;
			player.getSession().send(new SendSetWidgetColor(widgetId, color));
		}
	}

}
