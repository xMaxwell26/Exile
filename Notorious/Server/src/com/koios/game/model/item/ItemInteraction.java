package com.koios.game.model.item;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.obj.CrystalChestContents;
import com.koios.game.content.skill.Prayer;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.firemaking.FiremakingAction;
import com.koios.game.content.skill.herblore.potions.PotionMixing;
import com.koios.game.content.skill.hunter.Hunter.TrapType;
import com.koios.game.content.skill.hunter.impl.LayTrap;
import com.koios.game.def.ItemDefinition;
import com.koios.game.map.ObjectDefinition;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

/**
 * @author Ryan / Lmctruck30
 */
public class ItemInteraction {

	public static boolean isHilt(int id) {
		return id >= 11702 && id <= 11708;
	}

	public static void useItemWithItem(Player player, int itemUsed, int useWith) {
		if (FiremakingAction.canMakeFire(player, itemUsed, useWith)) {
			new FiremakingAction(player, itemUsed == 590 ? useWith : itemUsed).start();
		}

		if (itemUsed == 1540 && useWith == 11286 || itemUsed == 11286 && useWith == 1540) {
			if (player.getSkill().getLevel(Skill.SMITHING) >= 95) {
				player.getInventory().remove(player.getInventory().getSlotById(1540), new Item(1540, 1));
				player.getInventory().remove(player.getInventory().getSlotById(11286), new Item(11286, 1));
				player.getInventory().add(new Item(11283, 1));
				player.sendMessage("You combine the two materials to make a dragonfire shield.");
				player.getSkill().addExperience(Skill.SMITHING, 500 * Constants.SMITHING_EXPERIENCE);
			} else {
				player.sendMessage("You need a Smithing level of 95 to of a dragonfire shield.");
			}
		} else if (itemUsed == 13734 && useWith == 13754 || itemUsed == 13754 && useWith == 13734) {
			if (player.getSkill().getLevel(Skill.PRAYER) >= 85) {
				player.getInventory().remove(player.getInventory().getSlotById(useWith), new Item(useWith, 1));
				player.getInventory().remove(player.getInventory().getSlotById(itemUsed), new Item(itemUsed, 1));
				player.getInventory().add(new Item(13736, 1));
			} else {
				player.sendMessage("You need a Prayer level of 85 to bless a spirit shield.");
			}
		} else if (itemUsed == 985 && useWith == 987 || itemUsed == 987 && useWith == 985) {
			player.getInventory().remove(player.getInventory().getSlotById(useWith), new Item(useWith, 1));
			player.getInventory().remove(player.getInventory().getSlotById(itemUsed), new Item(itemUsed, 1));
			player.getInventory().add(new Item(989, 1));
		} else if (itemUsed == 13736 && useWith == 13752 || itemUsed == 13752 && useWith == 13736) {
			if (player.getSkill().getLevel(Skill.PRAYER) >= 90) {
				player.getInventory().remove(player.getInventory().getSlotById(useWith), new Item(useWith, 1));
				player.getInventory().remove(player.getInventory().getSlotById(itemUsed), new Item(itemUsed, 1));
				player.getInventory().add(new Item(13744, 1));
			} else {
				player.sendMessage("You need a Prayer level of 90 to make a spectral spirit shield.");
			}
		} else if (itemUsed == 13736 && useWith == 13746 || itemUsed == 13746 && useWith == 13736) {
			if (player.getSkill().getLevel(Skill.PRAYER) >= 90) {
				player.getInventory().remove(player.getInventory().getSlotById(useWith), new Item(useWith, 1));
				player.getInventory().remove(player.getInventory().getSlotById(itemUsed), new Item(itemUsed, 1));
				player.getInventory().add(new Item(13738, 1));
			} else {
				player.sendMessage("You need a Prayer level of 90 to make an arcane spirit shield.");
			}
		} else if (itemUsed == 13736 && useWith == 13748 || itemUsed == 13748 && useWith == 13736) {
			if (player.getSkill().getLevel(Skill.PRAYER) >= 90) {
				player.getInventory().remove(player.getInventory().getSlotById(useWith), new Item(useWith, 1));
				player.getInventory().remove(player.getInventory().getSlotById(itemUsed), new Item(itemUsed, 1));
				player.getInventory().add(new Item(13740, 1));
			} else {
				player.sendMessage("You need a Prayer level of 90 to make a divine spirit shield.");
			}
		} else if (itemUsed == 13736 && useWith == 13750 || itemUsed == 13750 && useWith == 13736) {
			if (player.getSkill().getLevel(Skill.PRAYER) >= 90) {
				player.getInventory().remove(player.getInventory().getSlotById(useWith), new Item(useWith, 1));
				player.getInventory().remove(player.getInventory().getSlotById(itemUsed), new Item(itemUsed, 1));
				player.getInventory().add(new Item(13742, 1));
			} else {
				player.sendMessage("You need a Prayer level of 90 to make an elysian spirit shield.");
			}
		} else if (itemUsed == 6585 && useWith == 19333 || itemUsed == 6585 && useWith == 19333) {
			player.getInventory().remove(new Item(6585, 1));
			player.getInventory().remove(new Item(19333, 1));
			player.getInventory().add(new Item(19335, 1));
			player.sendMessage("You use the ornament kit on the amulet of fury.");
		}

		else if (itemUsed == 4151 && useWith == 21369 || itemUsed == 21369 && useWith == 4151) {
			player.getInventory().remove(new Item(4151, 1));
			player.getInventory().remove(new Item(21369, 1));
			player.getInventory().add(new Item(21371, 1));
			player.sendMessage("You attach the whip vine to the abyssal whip and create an abyssal vine whip.");
		}

		// Full slayer helm
		else if (itemUsed == 15488 && useWith == 13263 || itemUsed == 13263 && useWith == 15488 || itemUsed == 15490 && useWith == 13263 || itemUsed == 13263 && useWith == 15490) {
			if (player.getInventory().contains(15488) && player.getInventory().contains(15490)) {
				player.getInventory().remove(new Item(15488, 1));
				player.getInventory().remove(new Item(15490, 1));
				player.getInventory().remove(new Item(13263, 1));
				player.getInventory().add(new Item(15492, 1));
				player.sendMessage("You use the hexcrest and focus sight to create a full slayer helmet.");
			} else {
				player.sendMessage("You need a focus sight and a hexcrest to make a full slayer helmet.");
			}
		}

		/**
		 * Ornamental kits creation Dragon ornamental kits and spiked
		 */

		// Legs or
		else if (itemUsed == 4087 && useWith == 19348 || itemUsed == 19348 && useWith == 4087) {
			player.getInventory().remove(new Item(4087, 1));
			player.getInventory().remove(new Item(19348, 1));
			player.getInventory().add(new Item(19338, 1));
			player.sendMessage("You use the ornament kit on the dragon platelegs.");
		}

		// Legs sp
		else if (itemUsed == 4087 && useWith == 19356 || itemUsed == 19356 && useWith == 4087) {
			player.getInventory().remove(new Item(4087, 1));
			player.getInventory().remove(new Item(19356, 1));
			player.getInventory().add(new Item(19343, 1));
			player.sendMessage("You use the spiked kit on the dragon platelegs.");
		}

		// Body or
		else if (itemUsed == 14479 && useWith == 19350 || itemUsed == 19350 && useWith == 14479) {
			player.getInventory().remove(new Item(19350, 1));
			player.getInventory().remove(new Item(14479, 1));
			player.getInventory().add(new Item(19337, 1));
			player.sendMessage("You use the ornament kit on the dragon platebody.");
		}

		// Body sp
		else if (itemUsed == 14479 && useWith == 19358 || itemUsed == 19358 && useWith == 14479) {
			player.getInventory().remove(new Item(19358, 1));
			player.getInventory().remove(new Item(14479, 1));
			player.getInventory().add(new Item(19342, 1));
			player.sendMessage("You use the spiked kit on the dragon platebody.");
		}

		// Helmet or
		else if (itemUsed == 11335 && useWith == 19346 || itemUsed == 19346 && useWith == 11335) {
			player.getInventory().remove(new Item(19346, 1));
			player.getInventory().remove(new Item(11335, 1));
			player.getInventory().add(new Item(19336, 1));
			player.sendMessage("You use the ornament kit on the dragon full helm.");
		}

		// Helmet sp
		else if (itemUsed == 11335 && useWith == 19354 || itemUsed == 19354 && useWith == 11335) {
			player.getInventory().remove(new Item(19354, 1));
			player.getInventory().remove(new Item(11335, 1));
			player.getInventory().add(new Item(19341, 1));
			player.sendMessage("You use the spiked kit on the dragon full helm.");
		}

		// Sq shield or
		else if (itemUsed == 1187 && useWith == 19352 || itemUsed == 19352 && useWith == 1187) {
			player.getInventory().remove(new Item(1187, 1));
			player.getInventory().remove(new Item(19352, 1));
			player.getInventory().add(new Item(19340, 1));
			player.sendMessage("You use the ornament kit on the dragon square shield.");
		}

		// Sq shield sp
		else if (itemUsed == 1187 && useWith == 19360 || itemUsed == 19360 && useWith == 1187) {
			player.getInventory().remove(new Item(19360, 1));
			player.getInventory().remove(new Item(1187, 1));
			player.getInventory().add(new Item(19345, 1));
			player.sendMessage("You use the spiked kit on the dragon sq shield.");
		}

		// End of dragon orn and sp

		String name = ItemDefinition.forId(itemUsed).getName();

		if (name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)")) {
			PotionMixing.redose(player, itemUsed, useWith);
		}

		if (itemUsed == 1601 && useWith == 1755 || itemUsed == 1755 && useWith == 1601) {
			if (player.getSkill().getLevel(Skill.FLETCHING) >= 63) {
				player.getInventory().remove(new Item(1601, 1));
				player.getInventory().add(new Item(9192, 15));
				player.getSkill().addExperience(Skill.FLETCHING, 8 * Constants.FLETCHING_EXPERIENCE);
			} else {
				player.sendMessage("You need a Fletching level of 63 to fletch this item.");
			}
		}

		if (itemUsed == 1607 && useWith == 1755 || itemUsed == 1755 && useWith == 1607) {
			if (player.getSkill().getLevel(Skill.FLETCHING) >= 65) {
				player.getInventory().remove(new Item(1607, 1));
				player.getInventory().add(new Item(9189, 15));
				player.getSkill().addExperience(Skill.FLETCHING, 8 * Constants.FLETCHING_EXPERIENCE);
			} else {
				player.sendMessage("You need a Fletching level of 65 to fletch this item.");
			}
		}

		if (itemUsed == 1605 && useWith == 1755 || itemUsed == 1755 && useWith == 1605) {
			if (player.getSkill().getLevel(Skill.FLETCHING) >= 71) {
				player.getInventory().remove(new Item(1605, 1));
				player.getInventory().add(new Item(9190, 15));
				player.getSkill().addExperience(Skill.FLETCHING, 8 * Constants.FLETCHING_EXPERIENCE);
			} else {
				player.sendMessage("You need a Fletching level of 71 to fletch this item.");
			}
		}

		if (itemUsed == 1603 && useWith == 1755 || itemUsed == 1755 && useWith == 1603) {
			if (player.getSkill().getLevel(Skill.FLETCHING) >= 73) {
				player.getInventory().remove(new Item(1603, 1));
				player.getInventory().add(new Item(9191, 15));
				player.getSkill().addExperience(Skill.FLETCHING, 8 * Constants.FLETCHING_EXPERIENCE);
			} else {
				player.sendMessage("You need a Fletching level of 73 to fletch this item.");
			}
		}

		if (itemUsed == 1615 && useWith == 1755 || itemUsed == 1755 && useWith == 1615) {
			if (player.getSkill().getLevel(Skill.FLETCHING) >= 73) {
				player.getInventory().remove(new Item(1615, 1));
				player.getInventory().add(new Item(9193, 15));
				player.getSkill().addExperience(Skill.FLETCHING, 8 * Constants.FLETCHING_EXPERIENCE);
			} else {
				player.sendMessage("You need a Fletching level of 73 to fletch this item.");
			}
		}

		if (itemUsed >= 11710 && itemUsed <= 11714 && useWith >= 11710 && useWith <= 11714) {
			if (player.getInventory().contains(11710) && player.getInventory().contains(11712) && player.getInventory().contains(11714)) {
				player.getItems().makeBlade();
			}
		}

		if (itemUsed == 2368 && useWith == 2366 || itemUsed == 2366 && useWith == 2368) {
			player.getInventory().remove(new Item(2368, 1));
			player.getInventory().remove(new Item(2366, 1));
			player.getInventory().add(new Item(1187, 1));
		}

		if (itemUsed >= 11702 && itemUsed <= 11708 && itemUsed % 2 == 0 || useWith >= 11702 && useWith <= 11708 && useWith % 2 == 0) {
			int hilt = isHilt(itemUsed) ? itemUsed : useWith;
			int blade = isHilt(itemUsed) ? useWith : itemUsed;

			if (blade == 11690) {
				player.getItems().makeGodsword(hilt);
			}
		}
	}

	public static void onItemWithMob(Player player, int itemId, int mobId, int slot) {
		player.clickMobType = 0;
		player.mobClickIndex = 0;
		player.setFaceIndex(mobId, false);

		switch (mobId) {
		case 3050:
			if (itemId == 2842) {
				player.getDH().sendDialogues(7, mobId);
			}
			break;

		default:
			player.sendMessage("Nothing interesting happens.");
		}
	}

	public static void useItemWithObject(Player player, int objectId, int x, int y, int itemId) {
		player.objectX = x;
		player.objectY = y;

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}
		}

		if (itemId == ItemIdentifiers.ROPE || itemId == ItemIdentifiers.SMALL_FISHING_NET) {
			ObjectDefinition def = ObjectDefinition.getById(objectId);

			if (def != null && def.containsAction("Set-Trap")) {
				GameService.getTaskScheduler().schedule(new LayTrap(player, player.getInventory().getSlotById(itemId), TrapType.NET));
				return;
			}
		}

		switch (objectId) {
		case 172:
			if (itemId == ItemIdentifiers.CRYSTAL_KEY) {
				if (!player.getInventory().contains(ItemIdentifiers.CRYSTAL_KEY)) {
					int unlockCount = player.getVariable("count_crystal_chest", 0);

					if (unlockCount > 0) {
						player.sendMessage("You need a crystal key to unlock this chest. You've unlocked it <col=800000>" + NumberUtil.format(unlockCount) + "</col> times.");
					} else {
						player.sendMessage("You need a crystal key to unlock this chest.");
					}
				}
				// test for 2 items; the key itself is "replaced" by the gemstone (3rd item)
				else if (player.getInventory().freeSlots() < 2) {
					player.sendMessage("You do not have enough inventory space to unlock this chest.");
				} else {
					player.getInventory().remove(ItemIdentifiers.CRYSTAL_KEY, 1);
					CrystalChestContents.newInstance(player).loot();
				}
			}
			break;

		case 3830:
			if (itemId == 954) {
				GameService.getObjectHandler().createAnObject(player, 3831, 3509, 9497);
				player.getInventory().remove(new Item(954, 1));
			}
			break;

		case 409:
			Prayer.sendPrayerInterface(player, itemId, x, y);
			break;

		default:
			if (player.getPrivilege() == RegularPrivilege.DEVELOPER) {
				player.sendMessage("Item-on-object: " + objectId + ", " + itemId + ", " + x + ", " + y);
			}
		}
	}

}
