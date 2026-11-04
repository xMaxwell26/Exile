package com.koios.game.content.combat.util;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handle's combat data.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class CombatData {

	public enum EquipmentSets {

		VERACS(new Item[] { new Item(4753), new Item(4755), new Item(4757), new Item(4759) },
				new int[] { Equipment.SLOT_HEAD, Equipment.SLOT_WEAPON, Equipment.SLOT_CHEST, Equipment.SLOT_LEGS },
				3), GUTHANS(new Item[] { new Item(4724), new Item(4726), new Item(4728), new Item(4730) },
						new int[] { Equipment.SLOT_HEAD, Equipment.SLOT_WEAPON, Equipment.SLOT_CHEST, Equipment.SLOT_LEGS }, 4);

		private final Item[] items;
		private final int[] slots;
		private final int misc;

		private EquipmentSets(Item[] items, int[] slots, int misc) {
			this.items = items;
			this.slots = slots;
			this.misc = misc;
		}

		public Item[] getItems() {
			return items;
		}

		public int[] getSlots() {
			return slots;
		}

		public int getMisc() {
			return misc;
		}

	}

	/**
	 * Obisidian Weapons.
	 */
	public static final int[] OBSIDIAN_WEAPON_IDS = { 746, 747, 6523, 6525, 6526, 6527, 6528 };

	/**
	 * Calculates the dharok damage.
	 */
	public static double getDharoksMultiplier(Player p) {
		return (1 - (float) p.getSkill().getLevel(3) / (float) p.getSkill().getLevelForExperience(3)) * 0.95 + 1;
	}

	/**
	 * Checks if the player has obsidian equipment to apply the effect.
	 *
	 * @param p
	 * @return
	 */
	public static boolean hasObsidianEffect(Player p) {
		if (!p.getEquipment().contains(11128)) {
			return false;
		}

		for (int weapon : OBSIDIAN_WEAPON_IDS) {
			if (p.getEquipment().contains(weapon)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the player is wearing full dharok armour.
	 *
	 * @param player
	 * @return
	 */
	public static boolean wearingFullDharoks(Player player) {
		// TODO test 100, 75, 50 and 25 counterparts.

		return player.getEquipment().contains(4718) && player.getEquipment().contains(4716) && player.getEquipment().contains(4720) && player.getEquipment().contains(4722);
	}

	/**
	 * Checks if the player is wearing full void.
	 *
	 * @param player
	 * @return
	 */
	public static boolean wearingFullVoid(Player player, AttackType type) {
		int complete = 0;

		int helmet = type == AttackType.MELEE ? 11665 : type == AttackType.MAGIC ? 11663 : 11664;
		boolean hasDeflector = player.getEquipment().contains(19712);
		boolean hasLegs = player.getEquipment().contains(8840) || player.getEquipment().contains(19790) || player.getEquipment().contains(19786)
				|| player.getEquipment().contains(19788);
		boolean hasPlate = player.getEquipment().contains(8839) || player.getEquipment().contains(19789) || player.getEquipment().contains(19785)
				|| player.getEquipment().contains(19787);

		if (player.getEquipment().contains(helmet)) {
			complete++;
		}

		if (player.getEquipment().contains(8842)) {
			complete++;
		}

		if (hasDeflector && (hasPlate && !hasLegs || !hasPlate && hasLegs) || hasPlate && hasLegs) {
			complete++;
		}

		return complete == 3;
	}

	/**
	 * Checks if the player has a full set of armour.
	 *
	 * @param player
	 * @param set
	 * @return
	 */
	public static boolean hasEquipmentEffect(Player player, EquipmentSets set) {
		if (set == null) {
			return false;
		}
		for (int i = 0; i < set.getSlots().length; i++) {
			if (player.getEquipment().get(i) == null || player.getEquipment().get(i).getId() != set.getItems()[i].getId()) {
				return false;
			}
		}
		return NumberUtil.inclusiveRandom(set.getMisc()) == 1;
	}

	private static Map<Integer, PoisonType> types = new HashMap<Integer, PoisonType>();

	static {
		types.put(817, PoisonType.POISON);
		types.put(816, PoisonType.POISON);
		types.put(818, PoisonType.POISON);
		types.put(831, PoisonType.POISON);
		types.put(812, PoisonType.POISON);
		types.put(813, PoisonType.POISON);
		types.put(814, PoisonType.POISON);
		types.put(815, PoisonType.POISON);
		types.put(883, PoisonType.POISON);
		types.put(885, PoisonType.POISON);
		types.put(887, PoisonType.POISON);
		types.put(889, PoisonType.POISON);
		types.put(891, PoisonType.POISON);
		types.put(893, PoisonType.POISON);
		types.put(870, PoisonType.POISON);
		types.put(871, PoisonType.POISON);
		types.put(872, PoisonType.POISON);
		types.put(873, PoisonType.POISON);
		types.put(874, PoisonType.POISON);
		types.put(875, PoisonType.POISON);
		types.put(876, PoisonType.POISON);
		types.put(834, PoisonType.POISON);
		types.put(835, PoisonType.POISON);
		types.put(832, PoisonType.POISON);
		types.put(833, PoisonType.POISON);
		types.put(836, PoisonType.POISON);
		types.put(1221, PoisonType.POISON);
		types.put(1223, PoisonType.POISON);
		types.put(1219, PoisonType.POISON);
		types.put(1229, PoisonType.POISON);
		types.put(1231, PoisonType.POISON);
		types.put(1225, PoisonType.POISON);
		types.put(1227, PoisonType.POISON);
		types.put(1233, PoisonType.POISON);
		types.put(1253, PoisonType.POISON);
		types.put(1251, PoisonType.POISON);
		types.put(1263, PoisonType.POISON);
		types.put(1261, PoisonType.POISON);
		types.put(1259, PoisonType.POISON);
		types.put(1257, PoisonType.POISON);
		types.put(3094, PoisonType.POISON);

		types.put(5621, PoisonType.EXTRA_POISON);
		types.put(5620, PoisonType.EXTRA_POISON);
		types.put(5617, PoisonType.EXTRA_POISON);
		types.put(5616, PoisonType.EXTRA_POISON);
		types.put(5619, PoisonType.EXTRA_POISON);
		types.put(5618, PoisonType.EXTRA_POISON);
		types.put(5629, PoisonType.EXTRA_POISON);
		types.put(5628, PoisonType.EXTRA_POISON);
		types.put(5631, PoisonType.EXTRA_POISON);
		types.put(5630, PoisonType.EXTRA_POISON);
		types.put(5645, PoisonType.EXTRA_POISON);
		types.put(5644, PoisonType.EXTRA_POISON);
		types.put(5647, PoisonType.EXTRA_POISON);
		types.put(5646, PoisonType.EXTRA_POISON);
		types.put(5643, PoisonType.EXTRA_POISON);
		types.put(5642, PoisonType.EXTRA_POISON);
		types.put(5633, PoisonType.EXTRA_POISON);
		types.put(5632, PoisonType.EXTRA_POISON);
		types.put(5634, PoisonType.EXTRA_POISON);
		types.put(5660, PoisonType.EXTRA_POISON);
		types.put(5656, PoisonType.EXTRA_POISON);
		types.put(5657, PoisonType.EXTRA_POISON);
		types.put(5658, PoisonType.EXTRA_POISON);
		types.put(5659, PoisonType.EXTRA_POISON);
		types.put(5654, PoisonType.EXTRA_POISON);
		types.put(5655, PoisonType.EXTRA_POISON);
		types.put(5680, PoisonType.EXTRA_POISON);

		types.put(5623, PoisonType.SUPER_POISON);
		types.put(5622, PoisonType.SUPER_POISON);
		types.put(5625, PoisonType.SUPER_POISON);
		types.put(5624, PoisonType.SUPER_POISON);
		types.put(5627, PoisonType.SUPER_POISON);
		types.put(5626, PoisonType.SUPER_POISON);
		types.put(5698, PoisonType.SUPER_POISON);
		types.put(5730, PoisonType.SUPER_POISON);
		types.put(5641, PoisonType.SUPER_POISON);
		types.put(5640, PoisonType.SUPER_POISON);
		types.put(5637, PoisonType.SUPER_POISON);
		types.put(5636, PoisonType.SUPER_POISON);
		types.put(5639, PoisonType.SUPER_POISON);
		types.put(5638, PoisonType.SUPER_POISON);
		types.put(5635, PoisonType.SUPER_POISON);
		types.put(5661, PoisonType.SUPER_POISON);
		types.put(5662, PoisonType.SUPER_POISON);
		types.put(5663, PoisonType.SUPER_POISON);
		types.put(5652, PoisonType.SUPER_POISON);
		types.put(5653, PoisonType.SUPER_POISON);
		types.put(5648, PoisonType.SUPER_POISON);
		types.put(5649, PoisonType.SUPER_POISON);
		types.put(5650, PoisonType.SUPER_POISON);
		types.put(5651, PoisonType.SUPER_POISON);
		types.put(5667, PoisonType.SUPER_POISON);
		types.put(5666, PoisonType.SUPER_POISON);
		types.put(5665, PoisonType.SUPER_POISON);
		types.put(5664, PoisonType.SUPER_POISON);
	}

	/**
	 * Gets the items poison type
	 *
	 * @return The items poison type
	 */
	public static PoisonType getPoisonType(Item item) {
		PoisonType poisonType = types.get(item.getId());
		if (poisonType == null) {
			return PoisonType.NONE;
		}
		return poisonType;
	}

	/**
	 * Gets the types of poison used by an item and the damage dealt by it.
	 *
	 * @author Advocatus
	 */
	public static enum PoisonType {

		NONE, POISON, EXTRA_POISON, SUPER_POISON;

		/**
		 * Gets the damage amount
		 *
		 * @return the damage amount
		 */
		public int getMeleeDamage() {
			return ordinal() + 3;
		}

		/**
		 * Gets the damage amount
		 *
		 * @return the damage amount
		 */
		public int getRangeDamage() {
			return ordinal() + 1;
		}
	}

}