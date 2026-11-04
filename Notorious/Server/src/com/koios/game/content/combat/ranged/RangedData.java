package com.koios.game.content.combat.ranged;

import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.def.AmmoDefinition;
import com.koios.game.def.WeaponAmmoDefinition;
import com.koios.game.def.WeaponDefinitions;
import com.koios.game.def.WeaponDefinitions.RangeTypes;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class RangedData {

	/**
	 * Checks if the player's current weapon can be throwed ( ex: knives )
	 *
	 * @param p
	 * @return if it can be throwed
	 */
	public static boolean canBeThrow(Player p) {
		if (p.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
			return false;
		}

		String weapon_name = p.getEquipment().get(Equipment.SLOT_WEAPON).getDefinition().getName().toLowerCase();
		return weapon_name.contains("knife") || weapon_name.contains("javelin") || weapon_name.contains("dart") || weapon_name.contains("chimchompa");
	}

	/**
	 * Checks if the player has correct ammo for his weapon.
	 *
	 * @param p
	 * @return if he has correct ammo
	 */
	public static boolean correcRangedAmmo(Player p) {
		if (p.getEquipment().get(3) != null && (WeaponDefinitions.getWeapon(p.getEquipment().get(3).getId()).getRangeType() == RangeTypes.NEED_WEAPON || WeaponDefinitions.getWeapon(p.getEquipment().get(3).getId()).getRangeType() == RangeTypes.NO_AMMO)) {
			return true;
		}

		if (p.getEquipment().get(13) == null || p.getEquipment().get(3) == null) {
			return false;
		}

		WeaponAmmoDefinition ammo = WeaponAmmoDefinition.getWeapon(p.getEquipment().get(3).getId());
		int arrowId = p.getEquipment().get(13).getId();

		if (ammo == null) {
			return false;
		}

		for (int i = 0; i < ammo.getArrows().length; i++) {
			if (arrowId == ammo.getArrows()[i]) {
				return true;
			}
		}

		return false;
	}

	public static void fireProjectile(Player player) {
		if (player.getCombat().getPreviousTarget() != null) {
			player.projectileStage = 2;
			int pX = player.getX();
			int pY = player.getY();
			int nX = player.getCombat().getPreviousTarget().getX();
			int nY = player.getCombat().getPreviousTarget().getY();
			int offX = (pY - nY) * -1;
			int offY = (pX - nX) * -1;

			if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15241) {
				if (!player.specialGraphics) {
					player.playGraphics(new Graphic(2138));
				}
				CombatExtras.createPlayersProjectile(pX, pY, player.getLocation().getZ(), offX, offY, 50, 55, 2143, 22, 22,
						player.getCombat().getPreviousTarget().getProjetileIndex(), 30, 16);
				player.specialGraphics = false;
			} else {
				CombatExtras.createPlayersProjectile(pX, pY, player.getLocation().getZ(), offX, offY, 50, getProjectileSpeed(player), getRangeProjectileGFX(player), 43, 31,
						player.getCombat().getPreviousTarget().getProjetileIndex(), getProjectileShowDelay(player), 16);
			}

			if (usingDbow(player)) {
				CombatExtras.createPlayersProjectile(pX, pY, player.getLocation().getZ(), offX, offY, 50, getProjectileSpeed(player), getRangeProjectileGFX(player), 60, 31,
						player.getCombat().getPreviousTarget().getProjetileIndex(), MagicData.getStartDelay(player), 35);
			}
		}
	}

	private static int getProjectileShowDelay(Player c) {
		int[] data = { 806, 806, 808, 809, 810, 811, 10033, 10034, 11230, };
		int str = 53;

		if (c.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
			for (int element : data) {
				if (c.getEquipment().get(Equipment.SLOT_WEAPON).getId() == element) {
					str = 32;
				}
			}
		}

		return str;
	}

	/**
	 * Returns the player's ammo projectile speed.
	 *
	 * @param player
	 * @return the speed
	 */
	private static int getProjectileSpeed(Player player) {
		if (player.dbowSpec) {
			return 100;
		}

		int id = player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1;

		switch (id) {
		case 10033:
		case 10034:
			return 40;

		case 15243:
			return 30;
		}

		return 70;
	}

	/**
	 * Returns the player's ranging strength from his ammo or weapon.
	 *
	 * @param p
	 * @return the ranged strength
	 */
	public static int getRangedStrength(Player p) {

		if (p.getCombat().getWeaponUsed() == null && p.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
			return 0;
		}

		if (p.getCombat().getWeaponUsed() != null && p.getCombat().getWeaponUsed().getRangeType() == RangeTypes.NEED_AMMO && !p.getEquipment().isSlotUsed(Equipment.SLOT_ARROWS)) {
			return 0;
		}

		if (p.getCombat().getWeaponUsed() == null && p.getEquipment().get(Equipment.SLOT_ARROWS) == null) {
			return 0;
		}

		int str = 0;
		int weap = -1;

		if (p.getCombat().getWeaponUsed() != null && (p.getCombat().getWeaponUsed().getRangeType() == RangeTypes.NO_AMMO || p.getCombat().getWeaponUsed().getRangeType() == RangeTypes.NEED_WEAPON)) {
			weap = p.getCombat().getLastWeapon() > 0 ? p.getCombat().getLastWeapon()
					: p.getEquipment().get(Equipment.SLOT_WEAPON) != null ? p.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1;
		} else {
			weap = p.getEquipment().get(Equipment.SLOT_ARROWS) == null ? -1 : p.getEquipment().get(Equipment.SLOT_ARROWS).getId();
		}

		if (weap <= 0) {
			return 0;
		}

		AmmoDefinition def = AmmoDefinition.getArrow(weap);

		if (def != null) {
			str += def.getRangedStr();
		} else {
			switch (weap) {
			case 4212:
			case 4214:
			case 4215:
			case 4216:
			case 4217:
			case 4218:
			case 4219:
			case 4220:
			case 4221:
			case 4222:
			case 4223:
				return 70;
			case 20171:
				return 115;
			}
		}
		return str;
	}

	private static final int[][] str_data = { { 877, 10 }, { 9140, 46 }, { 9145, 36 }, { 9141, 64 }, { 9142, 82 }, { 9143, 100 }, { 9144, 115 }, { 9236, 14 }, { 9237, 30 },
			{ 9238, 48 }, { 9239, 66 }, { 9240, 83 }, { 9241, 85 }, { 9242, 103 }, { 9243, 105 }, { 9244, 117 }, { 9245, 120 }, { 882, 7 }, { 884, 10 }, { 886, 16 }, { 888, 22 },
			{ 890, 31 }, { 892, 49 }, { 4740, 55 }, { 11212, 65 }, { 806, 1 }, { 807, 3 }, { 808, 4 }, { 809, 7 }, { 810, 10 }, { 811, 14 }, { 11230, 20 }, { 864, 3 }, { 863, 4 },
			{ 865, 7 }, { 866, 10 }, { 867, 14 }, { 868, 24 }, { 825, 6 }, { 826, 10 }, { 827, 12 }, { 828, 18 }, { 829, 28 }, { 830, 42 }, { 800, 5 }, { 801, 7 }, { 802, 11 },
			{ 803, 16 }, { 804, 23 }, { 805, 36 }, { 9976, 0 }, { 9977, 15 }, { 4212, 70 }, { 4214, 70 }, { 4215, 70 }, { 4216, 70 }, { 4217, 70 }, { 4218, 70 }, { 4219, 70 },
			{ 4220, 70 }, { 4221, 70 }, { 4222, 70 }, { 4223, 70 }, { 6522, 49 }, { 10034, 15 }, { 15243, 150 }, { ItemIdentifiers.CHINCHOMPA, 20 },
			{ ItemIdentifiers.RED_CHINCHOMPA, 45 }, };

	public static int getRangedStrength(final int id) {

		for (int[] element : str_data) {
			if (id == element[0]) {
				return element[1];
			}
		}

		return 0;
	}

	public static int getRangeEndGFX(Player player) {
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);

		if (weapon == null) {
			return -1;
		}

		switch (weapon.getId()) {
		case 10033:
		case 10034:
			player.rangeEndGFXHeight = true;
			return 157;
		}

		return -1;
	}

	private static final int[][] data = {
			// KNIFES
			{ 863, 213 }, { 864, 212 }, { 865, 214 }, { 866, 216 }, { 867, 217 }, { 868, 218 }, { 869, 215 },

			// DARTS
			{ 806, 226 }, { 807, 227 }, { 808, 228 }, { 809, 229 }, { 810, 230 }, { 811, 231 },

			// JAVELINS
			{ 825, 200 }, { 826, 201 }, { 827, 202 }, { 828, 203 }, { 829, 204 }, { 830, 205 },

			// AXES
			{ 800, 36 }, { 801, 35 }, { 802, 37 }, { 803, 38 }, { 804, 39 }, { 805, 40 },

			// ARROWS
			{ 882, 10 }, { 884, 9 }, { 886, 11 }, { 888, 12 }, { 890, 13 }, { 892, 15 }, { 19152, 2167 }, { 19157, 2119 }, { 19162, 2183 }, { 11212, 1120 },

			// CHINCHOMPA
			{ 10033, 908 }, { 10034, 909 },

			// CANNON
			{ 15243, 2143 },

			// DFS
			{ 11283, 1166 }, { 11284, 1166 },

			// OTHERS
			{ 6522, 442 }, { 4740, 27 }, { 4212, 249 }, { 4214, 249 }, { 4215, 249 }, { 4216, 249 }, { 4217, 249 }, { 4218, 249 }, { 4219, 249 }, { 4220, 249 }, { 4221, 249 },
			{ 4222, 249 }, { 4223, 249 }, { 20171, 2183 } };

	private static int getRangeProjectileGFX(Player player) {
		if (player.dbowSpec) {
			return player.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1099 : 1101;
		}

		if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 20171) {
			return 2183;
		}

		if (player.bowSpecShot > 0) {
			switch (player.rangeItemUsed) {
			case 19152:
				return 2167;
			case 19157:
				return 2119;
			case 19162:
				return 2183;
			default:
				return 249;
			}
		}

		boolean castingMagic = player.spellCasting || player.autocasting || player.spellId > 0;

		if (castingMagic) {
			return -1;
		}

		if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null
				&& (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 9185 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 18357)) {
			return 27;
		}

		int str = -1;

		for (int[] element : data) {
			if (player.rangeItemUsed == element[0]) {
				str = element[1];
			}
		}

		return str;
	}

	private static final int[][] gfx_data = {
			// KNIFES
			{ 863, 220 }, { 864, 219 }, { 865, 221 }, { 866, 223 }, { 867, 224 }, { 868, 225 }, { 869, 222 },

			// DARTS
			{ 806, 1234 }, { 807, 1235 }, { 808, 1236 }, { 809, 1238 }, { 810, 1239 }, { 811, 1240 }, { 11230, 1242 },

			// JAVELIN
			{ 825, 206 }, { 826, 207 }, { 827, 208 }, { 828, 209 }, { 829, 210 }, { 830, 211 },

			// AXES
			{ 800, 42 }, { 801, 43 }, { 802, 44 }, { 803, 45 }, { 804, 46 }, { 805, 48 },

			// ARROWS
			{ 882, 19 }, { 884, 18 }, { 886, 20 }, { 888, 21 }, { 890, 22 }, { 892, 24 }, { 19152, 24 }, { 19157, 24 }, { 19162, 24 },

			// CRYSTAL_BOW
			{ 4212, 250 }, { 4214, 250 }, { 4215, 250 }, { 4216, 250 }, { 4217, 250 }, { 4218, 250 }, { 4219, 250 }, { 4220, 250 }, { 4221, 250 }, { 4222, 250 }, { 4223, 250 },
			{ 20171, 1111 }, };

	public static int getRangeStartGFX(Player player) {
		int str = -1;

		for (int[] element : gfx_data) {
			if (player.rangeItemUsed == element[0]) {
				str = element[1];
			}
		}

		if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
			if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11235 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15701
					|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15702 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15703
					|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15704) {
				int[][] moreD = { { 882, 1104 }, { 884, 1105 }, { 886, 1106 }, { 888, 1107 }, { 890, 1108 }, { 892, 1109 }, { 11212, 1111 }, };

				for (int[] element : moreD) {
					if (player.getEquipment().get(Equipment.SLOT_ARROWS).getId() == element[0]) {
						str = element[1];
					}
				}
			}
		}

		return str;
	}

	public static boolean isBolt(int id) {
		return id >= 9140 && id <= 9145 || id >= 9236 && id <= 9245;
	}

	public static boolean properBolts(Player player) {
		return player.getEquipment().get(Equipment.SLOT_WEAPON) == null || player.getEquipment().get(Equipment.SLOT_ARROWS) == null ? false
				: isBolt(player.getEquipment().get(Equipment.SLOT_ARROWS).getId());
	}

	public static boolean usingCrystalBow(Player player) {
		return player.getEquipment().get(Equipment.SLOT_WEAPON).getId() >= 4212 && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() <= 4223;
	}

	private static boolean usingDbow(Player player) {
		if (player.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
			return false;
		}

		return player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11235 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15701
				|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15702 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15703
				|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15704;
	}

}
