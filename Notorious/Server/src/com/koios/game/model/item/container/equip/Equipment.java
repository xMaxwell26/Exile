package com.koios.game.model.item.container.equip;

import java.util.Arrays;

import com.koios.game.model.player.Player;

/**
 * Contains equipment utility methods.
 *
 * @author Graham Edgecombe
 * @author Lothy
 */
public class Equipment {

	/**
	 * Equipment type enum.
	 *
	 * @author Lothy
	 * @author Miss Silabsoft
	 */
	public enum EquipmentType {

		AMULET("Amulet", Equipment.SLOT_AMULET), 
		ARROWS("Arrows", Equipment.SLOT_ARROWS), 
		BODY("Body", Equipment.SLOT_CHEST), 
		BOOTS("Boots", Equipment.SLOT_BOOTS), 
		CAPE("Cape", Equipment.SLOT_CAPE), 
		FULL_HELMET("Full helm", Equipment.SLOT_HEAD), 
		FULL_MASK("Full mask", Equipment.SLOT_HEAD), 
		GLOVES("Gloves", Equipment.SLOT_GLOVES), 
		HAT("Hat", Equipment.SLOT_HEAD), 
		LEGS("Legs", Equipment.SLOT_LEGS), 
		PLATEBODY("Plate body", Equipment.SLOT_CHEST), 
		RING("Ring", Equipment.SLOT_RING), 
		SHIELD("Shield", Equipment.SLOT_SHIELD), 
		WEAPON("Weapon", Equipment.SLOT_WEAPON);

		/**
		 * Gets the Type
		 */
		public static EquipmentType getType(int i) {
			for (EquipmentType t : EquipmentType.values()) {
				if (t.ordinal() == i) {
					return t;
				}
			}

			return WEAPON;
		}

		/**
		 * The description.
		 */
		private final String description;

		/**
		 * The slot.
		 */
		private final int slot;

		/**
		 * Creates the equipment type.
		 *
		 * @param description The description.
		 * @param slot The slot.
		 */
		private EquipmentType(String description, int slot) {
			this.description = description;
			this.slot = slot;
		}

		/**
		 * Gets the description.
		 *
		 * @return The description.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Gets the slot.
		 *
		 * @return The slot.
		 */
		public int getSlot() {
			return slot;
		}

	}

	public static boolean hasFullGuthans(Player player) {
		if (player.getEquipment().get(Equipment.SLOT_HEAD) == null || player.getEquipment().get(Equipment.SLOT_LEGS) == null
				|| player.getEquipment().get(Equipment.SLOT_CHEST) == null || player.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
			return false;
		}

		return player.getEquipment().get(Equipment.SLOT_HEAD).getId() == 4724 && player.getEquipment().get(Equipment.SLOT_CHEST).getId() == 4728
				&& player.getEquipment().get(Equipment.SLOT_LEGS).getId() == 4730 && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4726;
	}

	public static boolean hasFullVeracs(Player player) {
		if (player.getEquipment().get(Equipment.SLOT_HEAD) == null || player.getEquipment().get(Equipment.SLOT_LEGS) == null
				|| player.getEquipment().get(Equipment.SLOT_CHEST) == null || player.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
			return false;
		}

		return player.getEquipment().get(Equipment.SLOT_HEAD).getId() == 4753 && player.getEquipment().get(Equipment.SLOT_CHEST).getId() == 4757
				&& player.getEquipment().get(Equipment.SLOT_LEGS).getId() == 4759 && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4755;
	}

	/**
	 * An array of all the skillcape ids
	 */
	private static final int[] SKILLCAPE_IDS = { 9747, 9748, 9750, 9751, 9753, 9754, 9756, 9757, 9759, 9760, 9762, 9763, 9765, 9766, 9768, 9769, 9771, 9772, 9774, 9775, 9777, 9778,
			9780, 9781, 9783, 9784, 9786, 9787, 9789, 9790, 9792, 9793, 9795, 9796, 9798, 9799, 9801, 9802, 9804, 9805, 9807, 9808, 9810, 9811, 9948, 9949, 10662 };

	/**
	 * Checks if the id is a skillcape
	 * 
	 * @param id The id of the cape
	 * @return If the id is a skillcape
	 */
	public static boolean isCapeEquipped(int id) {
		return Arrays.stream(SKILLCAPE_IDS).filter(i -> i == id).findAny().isPresent();
	}

	public static boolean isWieldingBalmung(Player player) {
		return player.getEquipment().get(Equipment.SLOT_WEAPON) != null && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15403;
	}

	/**
	 * Equipment interface id.
	 */
	public static final int INTERFACE = 1688;

	/**
	 * The size of the equipment container.
	 */
	public static final int SIZE = 14;

	/**
	 * The amulet slot.
	 */
	public static final int SLOT_AMULET = 2;

	/**
	 * The arrows slot.
	 */
	public static final int SLOT_ARROWS = 13;

	/**
	 * The boots slot.
	 */
	public static final int SLOT_BOOTS = 10;

	/**
	 * The cape slot.
	 */
	public static final int SLOT_CAPE = 1;

	/**
	 * The chest slot.
	 */
	public static final int SLOT_CHEST = 4;

	/**
	 * The gloves slot.
	 */
	public static final int SLOT_GLOVES = 9;

	/**
	 * The helmet slot.
	 */
	public static final int SLOT_HEAD = 0;

	/**
	 * The bottoms slot.
	 */
	public static final int SLOT_LEGS = 7;

	/**
	 * The rings slot.
	 */
	public static final int SLOT_RING = 12;

	/**
	 * The shield slot.
	 */
	public static final int SLOT_SHIELD = 5;

	/**
	 * The weapon slot.
	 */
	public static final int SLOT_WEAPON = 3;

}
