package com.koios.game.content;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class Food {

	public static enum FoodDefinition {

		ANCHOVIES(319, 1, "Anchovies", 0, "Food", false),
		ANCHOVY_PIZZA(2297, 9, "Anchovy Pizza", 2299, "Food", false),
		APPLE_PIE(2323, 7, "Apple Pie", 2335, "Food", false),
		BAKED_POTATO(6701, 4, "Baked Potato", 0, "Food", false),
		BANANA(1963, 2, "Banana", 0, "Food", false),
		BASS(365, 13, "Bass", 0, "Food", false),
		BREAD(2309, 5, "Bread", 0, "Food", false),
		CABBAGE(1965, 1, "Cabbage", 0, "Food", false),
		CAKE(1891, 4, "Cake", 0, "Food", false),
		SPINACH_ROLL(1969, 2, "Spinach roll", 0, "Food", false),
		CHILLI_POTATO(7054, 14, "Chilli Potato", 0, "Food", false),
		CHOCOLATE_CAKE(1897, 5, "Chocolate Cake", 1899, "Food", false),
		CHOCOLATE_SLICE(1901, 5, "Chocolate Slice", 0, "Food", false),
		COD(339, 7, "Cod", 0, "Food", false),
		KEBAB(1971, 8, "Kebab", 0, "Food", false),
		Easter_Egg(1961, 12, "Easter Egg", 0, "Food", false),
		EASTER_EGG(7928, 1, "Easter Egg", 0, "Food", false),
		EASTER_EGG2(7929, 1, "Easter Egg", 0, "Food", false),
		EASTER_EGG3(7930, 1, "Easter Egg", 0, "Food", false),
		EASTER_EGG4(7931, 1, "Easter Egg", 0, "Food", false),
		EASTER_EGG5(7932, 1, "Easter Egg", 0, "Food", false),
		EASTER_EGG6(7933, 1, "Easter Egg", 0, "Food", false),
		EGG_POTATO(7056, 16, "Egg Potato", 0, "Food", false),
		HALF_ANCHOVY_PIZZA(2299, 9, "1/2 Anchovy Pizza", 0, "Food", false),
		HALF_APPLE_PIE(2335, 7, "Half Apple Pie", 2313, "Food", false),
		HALF_CHOCOLATE_CAKE(1899, 5, "2/3 Chocolate Cake", 1901, "Food", false),
		HALF_MEAT_PIE(2331, 6, "Half Meat Pie", 2313, "Food", false),
		HALF_MEAT_PIZZA(2295, 8, "1/2 Meat Pizza", 0, "Food", false),
		HALF_PINEAPPLE_PIZZA(2303, 11, "1/2 Pineapple Pizza", 0, "Food", false),
		HALF_PLAIN_PIZZA(2291, 7, "1/2 Plain pizza", 0, "Food", false),
		HALF_REDBERRY_PIE(2333, 5, "Half Redberry Pie", 2313, "Food", false),
		HALF_SUMMER_PIE(7220, 11, "Half Summer Pie", 2313, "Food", false),
		HERRING(347, 5, "Herring", 0, "Food", false),
		LOBSTER(379, 12, "Lobster", 0, "Food", false),
		MANTA(391, 22, "Manta Ray", 0, "Food", false),
		MEAT_PIE(2327, 6, "Meat Pie", 2331, "Food", false),
		MEAT_PIZZA(2293, 8, "Meat Pizza", 2295, "Food", false),
		MONKFISH(7946, 16, "Monkfish", 0, "Food", false),
		MUSHROOM_POTATO(7058, 20, "Mushroom Potato", 0, "Food", false),
		ORANGE(2108, 2, "Orange", 0, "Food", false),
		PEACH(6883, 8, "Peach", 0, "Food", false),
		PIKE(351, 8, "Pike", 0, "Food", false),
		PINEAPPLE_CHUNKS(2116, 2, "Pineapple Chunks", 0, "Food", false),
		PINEAPPLE_PIZZA(2301, 11, "Pineapple Pizza", 2303, "Food", false),
		PINEAPPLE_RINGS(2118, 2, "Pineapple Rings", 0, "Food", false),
		PLAIN_PIZZA(2289, 7, "Plain Pizza", 2291, "Food", false),
		POTATO(1942, 1, "Potato", 0, "Food", false),
		POTATO_WITH_BUTTER(6703, 14, "Potato with Butter", 0, "Food", false),
		POTATO_WITH_CHEESE(6705, 16, "Potato with Cheese", 0, "Food", false),
		Pumpkin(1959, 14, "Pumpkin", 0, "Food", false),
		PURPLE_SWEETS(10476, 9, "Purple Sweets", 0, "Food", false),
		REDBERRY_PIE(2325, 5, "Redberry Pie", 2333, "Food", false),
		ROCKTAIL(15272, 23, "Rocktail", 0, "Food", true),
		SALMON(329, 9, "Salmon", 0, "Food", false),
		SARDINE(325, 4, "Sardine", 0, "Food", false),
		SEA_TURTLE(397, 21, "Sea Turtle", 0, "Food", false),
		SHARK(385, 20, "Shark", 0, "Food", false),
		KARAMBWAN(3144, 18, "karambwan", 0, "Drink", false),
		SHRIMPS(315, 3, "Shrimps", 0, "Food", false),
		SUMMER_PIE(7218, 11, "Summer Pie", 7220, "Food", false),
		SWORDFISH(373, 14, "Swordfish", 0, "Food", false),
		TROUT(333, 7, "Trout", 0, "Food", false),
		TUNA(361, 10, "Tuna", 0, "Food", false),
		TUNA_POTATO(7060, 22, "Tuna Potato", 0, "Food", false),
		Ugthanki_kebab(1883, 2, "Ugthanki kebab", 0, "Food", false);

		private static final Map<Integer, FoodDefinition> values = new HashMap<>();

		static {
			for (FoodDefinition definition : FoodDefinition.values()) {
				values.put(definition.getId(), definition);
			}
		}

		private final boolean foodEffect;
		private final int heal;
		private final int id;
		private final String name;
		private final int replace;
		private final String type;

		private FoodDefinition(int id, int heal, String name, int replaceWith, String type, boolean foodEffect) {
			this.id = id;
			this.heal = heal;
			this.name = name;
			replace = replaceWith;
			this.type = type;
			this.foodEffect = foodEffect;
		}

		public static FoodDefinition get(int id) {
			return values.get(id);
		}

		/**
		 * Any values that has no eat delay
		 */
		public int getHeal() {
			return heal;
		}

		private int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		private String getType() {
			return type;
		}

		private boolean hasEffect() {
			return foodEffect;
		}

		public int replaceWith() {
			return replace;
		}

	}

	public static void eat(Player player, int id, int slot) {
		boolean isKarambwan = id == FoodDefinition.KARAMBWAN.getId();

		if (player.isDead() || player.getHitpoints() <= 0) {
			return;
		}

		if (player.getDuel().isRuleActive(DuelingRules.NO_FOOD)) {
			player.sendMessage("You may not eat in this duel.");
			return;
		}

		int delay = isKarambwan ? 300 : 1600;

		if (System.currentTimeMillis() - player.foodDelay >= delay && player.getHitpoints() > 0) {
			Combat.resetCombat(player);
			player.getCombat().setAttackTimer(player.getCombat().getAttackTime() + 2);
			player.playAnimation(new Animation(829));
			player.getInventory().remove(slot, new Item(id));
			FoodDefinition definition = FoodDefinition.values.get(id);

			if (definition == null) {
				return;
			}

			if (!player.getController().canEat(player)) {
				return;
			}

			if (definition.hasEffect()) {
				foodEffect(player, id);
			}

			if (definition.replaceWith() > 0) {
				player.getInventory().add(new Item(definition.replaceWith()));
			}

			if (player.getHitpoints() <= player.getMaxHitpoints() && !definition.hasEffect()) {
				player.getSkill().incrementLevel(Skill.HITPOINTS, definition.getHeal());

				if (player.getHitpoints() >= player.getMaxHitpoints() && !definition.hasEffect()) {
					player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints());
				}
			}

			if (definition.hasEffect()) {
				if (player.getHitpoints() < player.getMaxHitpoints() + 10) {
					player.getSkill().incrementLevel(Skill.HITPOINTS, definition.getHeal());
				}

				if (player.getHitpoints() >= player.getMaxHitpoints() + 10) {
					player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints() + 10);
				}
			}

			if (definition.getType().equals("Food")) {
				player.sendMessage("You eat the " + definition.getName().toLowerCase() + ".");
			} else if (definition.getType().equals("Drink") && definition == FoodDefinition.KARAMBWAN) {
				player.sendMessage("You eat the karambwan.");
			} else if (definition.getType().equals("Drink")) {
				player.sendMessage("You drink the " + definition.getName().toLowerCase() + ".");
			}

			AchievementManager.performed(player, Achievement.EAT_FOOD, definition.getHeal());
			AchievementManager.performed(player, Achievement.EAT_FOOD_ELITE, definition.getHeal());
			player.foodDelay = System.currentTimeMillis();
		}
	}

	private static void foodEffect(Player player, int id) {
		switch (id) {
		case 1917:
			double beerEffectStrength = player.getSkill().getLevelForExperience(Skill.STRENGTH) * .04 + player.getSkill().getLevelForExperience(Skill.STRENGTH);
			double beerEffectAttack = player.getSkill().getLevelForExperience(Skill.ATTACK) * .07;

			if (player.getSkill().getLevel(Skill.STRENGTH) < beerEffectStrength) {
				player.getSkill().setLevel(Skill.STRENGTH, (int) beerEffectStrength);
			}

			if (player.getSkill().getLevel(Skill.ATTACK) > 0) {
				player.getSkill().setLevel(Skill.ATTACK, (int) beerEffectAttack);
			}

			if (player.getSkill().getLevel(Skill.ATTACK) < 0) {
				player.getSkill().setLevel(Skill.ATTACK, 1);
			}

			break;

		case 15272:
			break;
		}
	}

	public static boolean isItemFood(int id) {
		return FoodDefinition.values.containsKey(id);
	}

}
