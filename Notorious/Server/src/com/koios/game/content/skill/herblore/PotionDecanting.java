package com.koios.game.content.skill.herblore;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * A class to handle decanting of potions.
 *
 * @author Desmin
 */
public class PotionDecanting {

	/**
	 * A config enum containing the different potions and their dose id's.
	 *
	 * @author Desmin
	 */
	public enum Decanting {

		STRENGTH(113, 115, 117, 119), ATTACK(2428, 121, 123, 125), RESTORE(2430, 127, 129, 131), DEFENCE(2432, 133, 135, 137), PRAYER(2434, 139, 141, 143), FISHING(2438, 151, 153,
				155), RANGING(2444, 169, 171, 173), ANTIFIRE(2452, 2454, 2456, 2458), ENERGY(3008, 3010, 3012, 3014), AGILITY(3032, 3034, 3036, 3038), MAGIC(3040, 3042, 3044,
						3046), COMBAT(9739, 9741, 9743, 9745), SUMMONING(12140, 12142, 12144, 12146), SUPER_ATTACK(2436, 145, 147, 149), SUPER_STRENGTH(2440, 157, 159,
								161), SUPER_DEFENCE(2442, 163, 165, 167), SUPER_ENERGY(3016, 3018, 3020, 3022), SUPER_RESTORE(3024, 3026, 3028, 3030), ZAMORAK_BREW(2450, 189, 191,
										193), SARADOMIN_BREW(6685, 6687, 6689, 6691), WATERING_CAN(5333, 5334, 5335, 5336, 5337, 5338, 5339, 5340);

		/**
		 * Grabs the {@link Decanting} constant associated with the given id.
		 *
		 * @param id The given item id.
		 * @return A <code>null</code> if the item isn't a potion, otherwise the constant associated with the item.
		 */
		public static Decanting forPotionId(int id) {
			for (Decanting d : values()) {
				for (int i : d.doseIds) {
					if (i == id) {
						return d;
					}
				}
			}

			return null;
		}

		/**
		 * The item id's for potion doses.
		 */
		public final int[] doseIds;

		/**
		 * Creates a {@link Decanting} constant.
		 *
		 * @param doseIds The item ids.
		 */
		Decanting(int... doseIds) {
			this.doseIds = doseIds;
		}

		/**
		 * Grabs the id of a full potion.
		 *
		 * @return The id of a full potion.
		 */
		public int getFourDoseId() {
			return doseIds[0];
		}

		/**
		 * Grabs the id of a 1/4 full potion.
		 *
		 * @return The id of a 1/4 full potion.
		 */
		public int getOneDoseId() {
			return doseIds[3];
		}

		/**
		 * Grabs the id of a 3/4 full potion.
		 *
		 * @return The id of a 3/4 full potion.
		 */
		public int getThreeDoseId() {
			return doseIds[1];
		}

		/**
		 * Grabs the id of a 1/2 full potion.
		 *
		 * @return The id of a 1/2 full potion.
		 */
		public int getTwoDoseId() {
			return doseIds[2];
		}

		/**
		 * Tells us if the constant is a watering can.
		 *
		 * @return If the constant is a watering can.
		 */
		public boolean isAWateringCan() {
			return this == WATERING_CAN;
		}

		/**
		 * Performs an equality check, ensuring the given type is equal to the constant type.
		 *
		 * @param typeTwo The given Decanting type.
		 * @return True if the two are equal, false otherwise.
		 */
		public boolean isOfTheSameType(Decanting typeTwo) {
			if (typeTwo == null) {
				return false;
			}

			return equals(typeTwo);
		}

	}

	private static int getDoseAmount(int pot) {
		Item potion = new Item(pot);

		if (potion.getDefinition() == null || potion.getDefinition().getName() == null) {
			return 4;
		}

		String name = potion.getDefinition().getName();

		/**
		 * We trim the string down to whatever number is between the "(" and ")" in the item's name, giving us the dose.
		 */
		return Integer.valueOf(name.substring(name.indexOf("(") + 1, name.length() - 1));
	}

	/**
	 * Initiates and executes the decanting of two potions.
	 *
	 * @param player The {@link Player} decanting the potions.
	 * @param givenOne The first given item.
	 * @param givenTwo The second given item.
	 * @return True if the potions were decanted, false otherwise.
	 */
	public static boolean handlePotionDecanting(Player player, int givenOne, int givenTwo) {
		Decanting potion = Decanting.forPotionId(givenOne);

		/* If the potion is a null, we assume the given item is not a potion and return false. */
		if (potion == null) {
			return false;
		}

		/*
		 * We check that both items are (1) potions and (2) the same type of potion, as we cannot mix two different potion types. We return if they are not of the same type.
		 */
		if (!potion.isOfTheSameType(Decanting.forPotionId(givenTwo))) {
			return false;
		}

		int doseOfOne = getDoseAmount(givenOne), doseOfTwo = getDoseAmount(givenTwo);
		int fullDoseAmount = potion.isAWateringCan() ? 8 : 4;

		if (doseOfOne == fullDoseAmount || doseOfTwo == fullDoseAmount) {
			return true;
		}

		int totalDose = doseOfOne + doseOfTwo;
		replacePotions(player, totalDose, potion, givenOne, givenTwo);
		return true;
	}

	/**
	 * Replaces the required potions, based on the information given.
	 *
	 * @param player The {@link Player} decanting the potions.
	 * @param dose The total dose.
	 * @param potion The {@link Decanting} potion type.
	 * @param givenOne The first given item.
	 * @param givenTwo The second given item.
	 */
	private static void replacePotions(Player player, int dose, Decanting potion, int givenOne, int givenTwo) {
		if (potion.isAWateringCan()) {
			if (dose >= 8) {
				/*
				 * If the dose is greater than 8, we can create a full watering can and remove 8 from the dose.
				 */
				player.getInventory().remove(new Item(givenOne));
				player.getInventory().add(new Item(potion.doseIds[7]));
				dose -= 8;
			} else {
				/* If the dose is not greater than 8, then one of the cans will become empty. */
				player.getInventory().remove(new Item(givenOne));
				player.getInventory().add(new Item(5331));
			}

			/* Then we take the remaining dose left and add that item to the player's inventory. */
			int index = dose - 1;
			player.getInventory().remove(new Item(givenTwo));
			player.getInventory().add(new Item(index == -1 ? 5331 : potion.doseIds[index]));
		} else {
			if (dose >= 4) {
				/* If the dose is greater than 4, we add a full potion and remove 4 from the dose. */
				player.getInventory().remove(new Item(givenOne));
				player.getInventory().add(new Item(potion.getFourDoseId()));
				dose -= 4;
			} else {
				/* If the dose is not greater than 4, one potion will become an empty vial. */
				player.getInventory().remove(new Item(givenOne));
				player.getInventory().add(new Item(229));
			}

			/* Then we use the remaining dose to add the correct potion. */
			player.getInventory().remove(new Item(givenTwo));
			player.getInventory().add(new Item(dose == 3 ? potion.getThreeDoseId() : dose == 2 ? potion.getTwoDoseId() : dose == 1 ? potion.getOneDoseId() : 229));
		}
	}

}
