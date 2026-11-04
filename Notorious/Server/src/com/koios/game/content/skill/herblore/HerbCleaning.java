package com.koios.game.content.skill.herblore;

import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Handles herb cleaning
 *
 * @author Poesy700
 */
public class HerbCleaning {

	public enum GrimyData {

		GUAM(1, 199, 249, 3), TARROMIN(5, 203, 253, 4), MARRENTILL(9, 201, 251, 5), HARRALANDER(20, 205, 255, 6), RANARR(25, 207, 257, 7), TOADFLAX(30, 3049, 2998, 8), SPIRITWEED(
				35, 12174, 12172, 8), IRIT(40, 209, 259, 9), WERGALI(41, 14836, 14854, 10), AVANTOE(48, 211, 261, 10), KWUARM(54, 213, 263, 11), SNAPDRAGON(59, 3051, 3000,
						12), CADANTINE(65, 215, 265, 13), LANTADYME(67, 2485, 2481, 13), DWARFWEED(70, 217, 267, 14), TORSTOL(75, 219, 269, 15), FELLSTALK(91, 21626, 21624, 17);

		public static GrimyData forHerb(int grimyId) {
			return values.get(grimyId);
		}

		private final int levelRequired, grimyId, cleanedId, xpAmount;

		private static final Map<Integer, GrimyData> values = new HashMap<>();

		static {
			for (GrimyData a : values()) {
				values.put(a.getGrimyId(), a);
			}
		}

		private GrimyData(int levelReq, int grimyId, int cleanedId, int xpAmount) {
			levelRequired = levelReq;
			this.grimyId = grimyId;
			this.cleanedId = cleanedId;
			this.xpAmount = xpAmount;
		}

		public int getCleanedId() {
			return cleanedId;
		}

		public int getGrimyId() {
			return grimyId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getXpAmount() {
			return xpAmount;
		}

	}

	public static void cleanHerb(Player player, int itemId, int itemSlot) {
		final GrimyData definition = GrimyData.forHerb(itemId);

		if (!itemIsGrimy(itemId)) {
			return;
		}

		if (player.getSkill().getLevel(Skill.HERBLORE) < definition.getLevelRequired()) {
			player.sendMessage("You need a herblore level of " + definition.getLevelRequired() + " to clean this herb.");
			return;
		}

		player.sendMessage("You clean the " + new Item(itemId).getDefinition().getName().toLowerCase() + ".");
		player.getInventory().remove(new Item(itemId, 1));
		player.getInventory().add(new Item(definition.getCleanedId(), 1));
		player.getSkill().addExperience(Skill.HERBLORE, definition.getXpAmount() * Constants.HERBLORE_EXPERIENCE);
		AchievementManager.performed(player, Achievement.CLEAN_HERB);
	}

	public static boolean itemIsGrimy(int itemId) {
		for (GrimyData grimy : GrimyData.values()) {
			if (itemId == grimy.getGrimyId()) {
				return true;
			}
		}

		return false;
	}

}
