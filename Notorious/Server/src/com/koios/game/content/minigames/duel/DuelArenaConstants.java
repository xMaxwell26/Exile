package com.koios.game.content.minigames.duel;

import com.koios.game.model.Location;
import com.koios.util.StringUtil;

public class DuelArenaConstants {

	public final static String OPPONENT_NO_SPACE = "Your opponent does not have have enough free inventory space to set this rule.";

	public final static String PLAYER_NO_SPACE = "You don't have enough free inventory space to set this rule.";

	public final static Location OBSTACLES_LOCATION = new Location(3366, 3246, 0);

	public final static Location NON_OBSTACLES_LOCATION = new Location(3335, 3246, 0);

	public final static int STAKE_SIZE = 15;

	static final int[] DUEL_RULE_ID = { 1, 2, 16, 32, 64, 128, 256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 2097152, 8388608, 16777216, 67108864,
			134217728 };

	public static enum DuelingRules {

		NO_FORFEIT(784, 148006, -1), NO_MOVEMENT(788, 148010, -1), NO_RANGED(780, 148002, -1), NO_MELEE(781, 148003, -1), NO_MAGIC(782, 148004, -1), NO_POTIONS(785, 148007,
				-1), NO_FOOD(786, 148008, -1), NO_PRAYER(787, 148009, -1), OBSTACLES(789, 148011, -1), FUN_WEPAONS_ONLY(783, 148005, -1), NO_SPECIAL_ATTACKS(790, 148012, -1),

		NO_HELM(890, 148038, 0), NO_CAPE(895, 148032, 1), NO_AMULET(891, 148028, 2), NO_WEAPON(897, 148034, 3), NO_BODY(892, 148029, 4), NO_SHIELD(898, 148035, 5), NO_LEGS(893,
				148030, 7), NO_GLOVES(899, 148036, 9), NO_BOOTS(894, 148031, 10), NO_RINGS(900, 148037, 12), NO_ARROWS(896, 148033, 13);

		private final int configId;
		private final int buttonId;
		private final int equipmentSlot;

		private DuelingRules(int configId, int buttonId, int equipmentSlot) {
			this.configId = configId;
			this.buttonId = buttonId;
			this.equipmentSlot = equipmentSlot;
		}

		public int getConfigId() {
			return configId;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getEquipmentSlot() {
			return equipmentSlot;
		}

		public static DuelingRules forId(int i) {
			for (DuelingRules r : DuelingRules.values()) {
				if (r.ordinal() == i) {
					return r;
				}
			}

			return null;
		}

		public static DuelingRules forButtonId(int buttonId) {
			for (DuelingRules r : DuelingRules.values()) {
				if (r.getButtonId() == buttonId) {
					return r;
				}
			}

			return null;
		}

		public static int forEquipmentSlot(int slot) {
			for (DuelingRules r : DuelingRules.values()) {
				if (r.getEquipmentSlot() == slot) {
					return r.ordinal();
				}
			}

			return -1;
		}

		@Override
		public String toString() {
			return StringUtil.capitalize(name().replace("_", " ").toLowerCase());
		}

	}

	public static enum DuelingStatus {

		NONE(-1), WAITING_FIRST_ACCEPT(37888), ACCEPTED_FIRST_INTERFACE(37888), WAITING_SECOND_ACCEPT(6412), ACCEPTED_SECOND_INTERFACE(6412), DUELING(-1), WON_DUEL(-1);

		private final int interfaceRequired;

		private DuelingStatus(int interfaceRequired) {
			this.interfaceRequired = interfaceRequired;
		}

		public int getInterfaceRequired() {
			return interfaceRequired;
		}

	}

	/**
	 * Checks if the location is in duel region.
	 *
	 * @param location
	 * @return
	 */
	public static boolean inDuelArena(Location location) {
		return location.getX() >= 3324 && location.getX() <= 3390 && location.getY() >= 3203 && location.getY() <= 3379
				|| location.getX() >= 3322 && location.getX() <= 3323 && location.getY() >= 3223 && location.getY() <= 3250
				|| location.getX() >= 3312 && location.getX() <= 3321 && location.getY() >= 3223 && location.getY() <= 3247;
	}

	/**
	 * Checks if the location is inside the duel arena.
	 *
	 * @param location
	 * @return
	 */
	public static boolean insideDuelArena(Location location) {
		return location.getX() > 3330 && location.getX() < 3391 && location.getY() > 3204 && location.getY() < 3260;
	}

}
