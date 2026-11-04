package com.koios.game.content.combat.magic;

import java.util.Arrays;
import java.util.List;

import com.koios.Constants;
import com.koios.game.content.combat.Combat;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.util.ItemIdentifiers;

public class MagicRequirements extends MagicData {

	private static final List<Integer> ANCIENT_SPELLS = Arrays.asList(32, 33, 34, 35, 52, 37, 38, 39, 40, 42, 43, 44, 45, 46, 47, 55);

	private static final List<Integer> REGULAR_SPELLS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
			30, 31, 48, 51);

	private static boolean hasItemRequirement(Player player, int index) {
		if (MagicData.MAGIC_SPELLS[index][8] != 0) {
			if (!wearingStaff(player, MagicData.MAGIC_SPELLS[index][8])
					&& !player.getInventory().hasItem(new Item(MagicData.MAGIC_SPELLS[index][8], MagicData.MAGIC_SPELLS[index][9]))) {
				return false;
			}
		}

		if (MagicData.MAGIC_SPELLS[index][10] != 0) {
			if (!wearingStaff(player, MagicData.MAGIC_SPELLS[index][10])
					&& !player.getInventory().hasItem(new Item(MagicData.MAGIC_SPELLS[index][10], MagicData.MAGIC_SPELLS[index][11]))) {
				return false;
			}
		}

		if (MagicData.MAGIC_SPELLS[index][12] != 0) {
			if (!wearingStaff(player, MagicData.MAGIC_SPELLS[index][12])
					&& !player.getInventory().hasItem(new Item(MagicData.MAGIC_SPELLS[index][12], MagicData.MAGIC_SPELLS[index][13]))) {
				return false;
			}
		}

		if (MagicData.MAGIC_SPELLS[index][14] != 0) {
			if (!wearingStaff(player, MagicData.MAGIC_SPELLS[index][14])
					&& !player.getInventory().hasItem(new Item(MagicData.MAGIC_SPELLS[index][14], MagicData.MAGIC_SPELLS[index][15]))) {
				return false;
			}
		}

		return true;
	}

	public static boolean testSpellRequirements(Player player, int spellId) {
		if (!player.spellCasting) {
			return true;
		}

		if (REGULAR_SPELLS.contains(spellId)) {
			if (player.getSpellbook() != Spellbook.REGULAR) {
				if (player.isSuperAdministrator()) {
					player.sendMessage("Invalid spellbook to cast spell: " + spellId);
				}

				return false;
			}
		} else if (ANCIENT_SPELLS.contains(spellId)) {
			if (player.getSpellbook() != Spellbook.ANCIENT_MAGICKS) {
				if (player.isSuperAdministrator()) {
					player.sendMessage("Invalid spellbook to cast spell: " + spellId);
				}

				return false;
			}
		} else {
			if (player.isSuperAdministrator()) {
				player.sendMessage("Unrecognized spell id: " + spellId);
			}

			return false;
		}

		if (player.spellCasting && Constants.RUNES_REQUIRED) {
			if (!hasItemRequirement(player, spellId)) {
				player.sendMessage("You don't have the required runes to cast this spell.");
				return false;
			}
		}

		if (player.spellCasting && player.getCombat().getTarget() != null) {
			EntityCharacter target = player.getCombat().getTarget();
			if (target.isPlayer()) {
				Player other = target.toPlayer();

				for (int r = 0; r < MagicData.REDUCE_SPELLS.length; r++) {
					if (MagicData.REDUCE_SPELLS[r] == MagicData.MAGIC_SPELLS[spellId][0]) {
						player.reduceSpellId = r;

						if (System.currentTimeMillis() - other.reduceSpellDelay[player.reduceSpellId] > MagicData.REDUCE_SPELL_TIME[player.reduceSpellId]) {
							other.canUseReducingSpell[player.reduceSpellId] = true;
						} else {
							other.canUseReducingSpell[player.reduceSpellId] = false;
						}

						break;
					}
				}

				if (!other.canUseReducingSpell[player.reduceSpellId]) {
					player.sendMessage("That player is currently immune to this spell.");
					player.spellCasting = false;
					player.getMovementHandler().reset();
					Combat.resetCombat(player);
					return false;
				}
			}
		}

		int staffRequired = getStaffNeeded(player);

		if (MAGIC_SPELLS[player.spellId][0] >= 21744 && MAGIC_SPELLS[player.spellId][0] <= 21746 || MAGIC_SPELLS[player.spellId][0] == 22168) {
			if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != ItemIdentifiers.ZURIELS_STAFF
					&& player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != ItemIdentifiers.ZURIELS_STAFF_DEG
					&& player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 13941 && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 13943) {
				player.sendMessage("You require a zuriel's staff to cast this spell.");
				return false;
			}
		}

		if (player.spellCasting && staffRequired > 0 && Constants.RUNES_REQUIRED) {
			// staff required
			if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != staffRequired) {
				player.sendMessage("You need a " + ItemDefinition.forId(staffRequired).getName().toLowerCase() + " to cast this spell.");
				return false;
			}
		}

		if (player.spellCasting && Constants.MAGIC_LEVEL_REQUIRED) {
			// check magic level
			if (player.getSkill().getLevel(6) < MagicData.MAGIC_SPELLS[spellId][1]) {
				player.sendMessage("You need to have a magic level of " + MagicData.MAGIC_SPELLS[spellId][1] + " to cast this spell.");
				return false;
			}
		}

		if (player.spellCasting && Constants.RUNES_REQUIRED) {
			if (MagicData.MAGIC_SPELLS[spellId][8] > 0) {
				// deleting runes
				if (!wearingStaff(player, MagicData.MAGIC_SPELLS[spellId][8])) {
					player.getInventory().remove(player.getInventory().getSlotById(MagicData.MAGIC_SPELLS[spellId][8]),
							new Item(MagicData.MAGIC_SPELLS[spellId][8], MagicData.MAGIC_SPELLS[spellId][9]));
				}
			}

			if (MagicData.MAGIC_SPELLS[spellId][10] > 0) {
				if (!wearingStaff(player, MagicData.MAGIC_SPELLS[spellId][10])) {
					player.getInventory().remove(player.getInventory().getSlotById(MagicData.MAGIC_SPELLS[spellId][10]),
							new Item(MagicData.MAGIC_SPELLS[spellId][10], MagicData.MAGIC_SPELLS[spellId][11]));
				}
			}

			if (MagicData.MAGIC_SPELLS[spellId][12] > 0) {
				if (!wearingStaff(player, MagicData.MAGIC_SPELLS[spellId][12])) {
					player.getInventory().remove(player.getInventory().getSlotById(MagicData.MAGIC_SPELLS[spellId][12]), new Item(MagicData.MAGIC_SPELLS[spellId][12],

					MagicData.MAGIC_SPELLS[spellId][13]));
				}
			}

			if (MagicData.MAGIC_SPELLS[spellId][14] > 0) {
				if (!wearingStaff(player, MagicData.MAGIC_SPELLS[spellId][14])) {
					player.getInventory().remove(player.getInventory().getSlotById(MagicData.MAGIC_SPELLS[spellId][14]), new Item(MagicData.MAGIC_SPELLS[spellId][14],

					MagicData.MAGIC_SPELLS[spellId][15]));
				}
			}
		}

		return true;
	}

	private static boolean wearingStaff(Player player, int runeId) {
		if (player.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
			return false;
		}

		int weaponId = player.getEquipment().get(Equipment.SLOT_WEAPON).getId();
		int shieldId = player.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD) ? player.getEquipment().get(Equipment.SLOT_SHIELD).getId() : -1;

		switch (runeId) {
		case 554:
			if (weaponId == 1387) {
				return true;
			}
			break;

		case 555:
			if (weaponId == 1383 || weaponId == 6562) {
				return true;
			} else if (shieldId == 18346) {
				return true;
			}
			break;

		case 556:
			if (weaponId == 1381) {
				return true;
			}
			break;

		case 557:
			if (weaponId == 1385 || weaponId == 6562) {
				return true;
			}
		}

		return false;
	}

}
