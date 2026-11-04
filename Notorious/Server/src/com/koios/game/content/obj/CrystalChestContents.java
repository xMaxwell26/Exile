package com.koios.game.content.obj;

import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.Utils;

public final class CrystalChestContents {

	public static CrystalChestContents newInstance(Player player) {
		return new CrystalChestContents(player);
	}

	private final Player player;
	private final Random random;

	public CrystalChestContents(Player player) {
		this.player = Objects.requireNonNull(player, "player cannot be null");
		random = player.getRandom();
	}

	public void loot() {
		// Loot count statistics
		GameService.getConfiguration().increment("ckey_count_unlock");
		AchievementManager.performed(player, Achievement.OPEN_CRYSTAL_CHEST);
		int lootCount = player.increaseVariable("count_crystal_chest", 1);
		player.sendMessage("Your crystal chest loot count: <col=800000>" + lootCount + "</col>.");

		// Reward with an extraordinary uncut gem (dragonstone; onyx)
		if (random.nextInt(16) < 1) {
			drop(ItemIdentifiers.UNCUT_ONYX, 1);
		} else {
			drop(ItemIdentifiers.UNCUT_DRAGONSTONE, 1);
		}

		// Generate two random rewards from the contents table.
		IntStream.range(0, 2).forEach(i -> chooseAndDropReward());
	}

	private void chooseAndDropReward() {
		int rn = random.nextInt(32);

		if (rn < 1) {
			rn = random.nextInt(5);

			if (rn < 1) {
				rn = random.nextInt(13);

				// Third-age druidic equipment (special table)
				if (rn < 1) {
					rn = random.nextInt(5);
					if (rn < 1) {
						dropExtraordinary(ItemIdentifiers.THIRD_AGE_DRUIDIC_WREATH, 1, true);
					} else if (rn < 2) {
						dropExtraordinary(ItemIdentifiers.THIRD_AGE_DRUIDIC_ROBE_TOP, 1, true);
					} else if (rn < 3) {
						dropExtraordinary(ItemIdentifiers.THIRD_AGE_DRUIDIC_ROBE, 1, true);
					} else if (rn < 4) {
						dropExtraordinary(ItemIdentifiers.THIRD_AGE_DRUIDIC_CLOAK, 1, true);
					} else if (rn < 5) {
						dropExtraordinary(ItemIdentifiers.THIRD_AGE_DRUIDIC_STAFF, 1, true);
					}

					GameService.getConfiguration().increment("ckey_count_thirdage_druidic");
				}
				// Third-age melee equipment
				else if (rn < 2) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_FULL_HELMET, 1, true);
				} else if (rn < 3) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_PLATEBODY, 1, true);
				} else if (rn < 4) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_PLATELEGS, 1, true);
				} else if (rn < 5) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_KITESHIELD, 1, true);
				} else if (rn < 6) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_MAGE_HAT, 1, true);
				} else if (rn < 7) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_ROBE_TOP, 1, true);
				} else if (rn < 8) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_ROBE, 1, true);
				} else if (rn < 9) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_AMULET, 1, true);
				} else if (rn < 10) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_RANGE_COIF, 1, true);
				} else if (rn < 11) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_RANGE_TOP, 1, true);
				} else if (rn < 12) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_RANGE_LEGS, 1, true);
				} else if (rn < 13) {
					dropExtraordinary(ItemIdentifiers.THIRD_AGE_VAMBRACES, 1, true);
				}

				// Update global statistics tracking
				GameService.getConfiguration().increment("ckey_count_thirdage");
			}
			// Gilded equipment
			else if (rn < 2) {
				if (player.getAppearance().isMale()) {
					drop(ItemIdentifiers.GILDED_PLATELEGS, 1);
				} else {
					drop(ItemIdentifiers.GILDED_PLATESKIRT, 1);
				}
			} else if (rn < 3) {
				drop(ItemIdentifiers.GILDED_FULL_HELM, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.GILDED_PLATEBODY, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.GILDED_KITESHIELD, 1);
			}
		} else if (rn < 3) {
			rn = random.nextInt(24);

			// Ancient rune equipment
			if (rn < 1) {
				drop(ItemIdentifiers.ANCIENT_FULL_HELM, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.ANCIENT_PLATEBODY, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.ANCIENT_PLATELEGS, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.ANCIENT_KITESHIELD, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.ARMADYL_FULL_HELM, 1);
			} else if (rn < 6) {
				drop(ItemIdentifiers.ARMADYL_PLATEBODY, 1);
			} else if (rn < 7) {
				drop(ItemIdentifiers.ARMADYL_PLATELEGS, 1);
			} else if (rn < 8) {
				drop(ItemIdentifiers.ARMADYL_KITESHIELD, 1);
			} else if (rn < 9) {
				drop(ItemIdentifiers.BANDOS_FULL_HELM, 1);
			} else if (rn < 10) {
				drop(ItemIdentifiers.BANDOS_PLATEBODY, 1);
			} else if (rn < 11) {
				drop(ItemIdentifiers.BANDOS_PLATELEGS, 1);
			} else if (rn < 12) {
				drop(ItemIdentifiers.BANDOS_KITESHIELD, 1);
			} else if (rn < 13) {
				drop(ItemIdentifiers.SARADOMIN_FULL_HELM, 1);
			} else if (rn < 14) {
				drop(ItemIdentifiers.SARADOMIN_PLATEBODY, 1);
			} else if (rn < 15) {
				drop(ItemIdentifiers.SARADOMIN_PLATELEGS, 1);
			} else if (rn < 16) {
				drop(ItemIdentifiers.SARADOMIN_KITESHIELD, 1);
			} else if (rn < 17) {
				drop(ItemIdentifiers.GUTHIX_FULL_HELM, 1);
			} else if (rn < 18) {
				drop(ItemIdentifiers.GUTHIX_PLATEBODY, 1);
			} else if (rn < 19) {
				drop(ItemIdentifiers.GUTHIX_PLATELEGS, 1);
			} else if (rn < 20) {
				drop(ItemIdentifiers.GUTHIX_KITESHIELD, 1);
			} else if (rn < 21) {
				drop(ItemIdentifiers.ZAMORAK_FULL_HELM, 1);
			} else if (rn < 22) {
				drop(ItemIdentifiers.ZAMORAK_PLATEBODY, 1);
			} else if (rn < 23) {
				drop(ItemIdentifiers.ZAMORAK_PLATELEGS, 1);
			} else if (rn < 24) {
				drop(ItemIdentifiers.ZAMORAK_KITESHIELD, 1);
			}
		} else if (rn < 4) {
			boolean anyDrop = dropIfNoSuchItem(ItemIdentifiers.BOOTS_OF_LIGHTNESS) || dropIfNoSuchItem(ItemIdentifiers.PENANCE_GLOVES)
					|| dropIfNoSuchItem(ItemIdentifiers.AGILE_LEGS) || dropIfNoSuchItem(ItemIdentifiers.AGILE_TOP);

			if (!anyDrop) {
				rn = random.nextInt(5);
				if (rn < 1) {
					drop(ItemIdentifiers.ROGUE_TOP, 1);
				} else if (rn < 2) {
					drop(ItemIdentifiers.ROGUE_MASK, 1);
				} else if (rn < 3) {
					drop(ItemIdentifiers.ROGUE_TROUSERS, 1);
				} else if (rn < 4) {
					drop(ItemIdentifiers.ROGUE_GLOVES, 1);
				} else if (rn < 5) {
					drop(ItemIdentifiers.ROGUE_BOOTS, 1);
				}
			}
		} else if (rn < 5) {
			rn = random.nextInt(2);
			if (rn < 1) {
				drop(ItemIdentifiers.TOOTH_HALF_KEY, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.LOOP_HALF_KEY, 1);
			}
		} else if (rn < 6) {
			drop(ItemIdentifiers.RUNITE_BAR_NOTE, 75);
		} else if (rn < 7) {
			drop(ItemIdentifiers.BLOOD_RUNE, 500);
		} else if (rn < 8) {
			drop(ItemIdentifiers.DEATH_RUNE, 500);
		} else if (rn < 9) {
			drop(ItemIdentifiers.CHAOS_RUNE, 1000);
		} else if (rn < 10) {
			drop(ItemIdentifiers.ASTRAL_RUNE, 750);
		} else if (rn < 12) {
			drop(ItemIdentifiers.RAW_ROCKTAIL_NOTE, 100);
		} else if (rn < 13) {
			drop(ItemIdentifiers.RAW_SHARK_NOTE, 250);
		} else if (rn < 14) {
			drop(ItemIdentifiers.COAL_NOTE, 275);
		} else if (rn < 16) {
			drop(ItemIdentifiers.FROST_DRAGON_BONES_NOTE, 50);
		} else if (rn < 18) {
			drop(ItemIdentifiers.DRAGON_BONES_NOTE, 75);
		} else if (rn < 20) {
			drop(ItemIdentifiers.GRIMY_SNAPDRAGON_NOTE, 75);
		} else if (rn < 21) {
			drop(ItemIdentifiers.GRIMY_TORSTOL_NOTE, 50);
		} else if (rn < 22) {
			// Cloak table
			rn = random.nextInt(6);
			if (rn < 1) {
				drop(ItemIdentifiers.ARMADYL_CLOAK, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.ANCIENT_CLOAK, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.BANDOS_CLOAK, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.SARADOMIN_CLOAK, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.GUTHIX_CLOAK, 1);
			} else if (rn < 6) {
				drop(ItemIdentifiers.ZAMORAK_CLOAK, 1);
			}
		} else if (rn < 23) {
			// Flag table
			rn = random.nextInt(6);
			if (rn < 1) {
				drop(ItemIdentifiers.CUTTHROAT_FLAG, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.GUILDED_SMILE_FLAG, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.BRONZE_FIST_FLAG, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.LUCKY_SHOT_FLAG, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.TREASURE_FLAG, 1);
			} else if (rn < 6) {
				drop(ItemIdentifiers.PHASMATYS_FLAG, 1);
			}
		} else if (rn < 24) {
			// Highwayman mask, cavalier and beret table
			rn = random.nextInt(9);
			if (rn < 1) {
				drop(ItemIdentifiers.HIGHWAYMAN_MASK, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.TAN_CAVALIER, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.DARK_CAVALIER, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.BLACK_CAVALIER, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.GUTHIX_CLOAK, 1);
			} else if (rn < 6) {
				drop(ItemIdentifiers.ZAMORAK_CLOAK, 1);
			} else if (rn < 7) {
				drop(ItemIdentifiers.BLUE_BERET, 1);
			} else if (rn < 8) {
				drop(ItemIdentifiers.WHITE_BERET, 1);
			} else if (rn < 9) {
				drop(ItemIdentifiers.BLACK_BERET, 1);
			}
		} else if (rn < 25) {
			// Elegant outfit table
			rn = random.nextInt(18);
			if (rn < 1) {
				drop(ItemIdentifiers.BLACK_ELE_SHIRT, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.BLACK_ELE_LEGS, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.RED_ELE_SHIRT, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.RED_ELE_LEGS, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.RED_ELE_BLOUSE, 1);
			} else if (rn < 6) {
				drop(ItemIdentifiers.RED_ELE_SKIRT, 1);
			} else if (rn < 7) {
				drop(ItemIdentifiers.BLUE_ELE_SHIRT, 1);
			} else if (rn < 8) {
				drop(ItemIdentifiers.BLUE_ELE_LEGS, 1);
			} else if (rn < 9) {
				drop(ItemIdentifiers.BLUE_ELE_BLOUSE, 1);
			} else if (rn < 10) {
				drop(ItemIdentifiers.BLUE_ELE_SKIRT, 1);
			} else if (rn < 11) {
				drop(ItemIdentifiers.GREEN_ELE_SHIRT, 1);
			} else if (rn < 12) {
				drop(ItemIdentifiers.GREEN_ELE_LEGS, 1);
			} else if (rn < 13) {
				drop(ItemIdentifiers.GREEN_ELE_BLOUSE, 1);
			} else if (rn < 14) {
				drop(ItemIdentifiers.GREEN_ELE_SKIRT, 1);
			} else if (rn < 15) {
				drop(ItemIdentifiers.PURPLE_ELE_SHIRT, 1);
			} else if (rn < 16) {
				drop(ItemIdentifiers.PURPLE_ELE_LEGS, 1);
			} else if (rn < 17) {
				drop(ItemIdentifiers.PURPLE_ELE_BLOUSE, 1);
			} else if (rn < 18) {
				drop(ItemIdentifiers.PURPLE_ELE_SKIRT, 1);
			}
		} else if (rn < 26) {
			// Gnomish accessories and fury (or) kit
			rn = random.nextInt(5);
			if (rn < 1) {
				dropExtraordinary(ItemIdentifiers.FURY_ORNAMENT_KIT, 1, false);
			} else if (rn < 3) {
				rn = random.nextInt(8);
				if (rn < 1) {
					dropExtraordinary(ItemIdentifiers.DRAGON_FULL_HELM_ORNAMENT_KIT_OR, 1, false);
				} else if (rn < 2) {
					dropExtraordinary(ItemIdentifiers.DRAGON_FULL_HELM_ORNAMENT_KIT_SP, 1, false);
				} else if (rn < 3) {
					dropExtraordinary(ItemIdentifiers.DRAGON_PLATEBODY_ORNAMENT_KIT_OR, 1, false);
				} else if (rn < 4) {
					dropExtraordinary(ItemIdentifiers.DRAGON_PLATEBODY_ORNAMENT_KIT_SP, 1, false);
				} else if (rn < 5) {
					dropExtraordinary(ItemIdentifiers.DRAGON_PLATELEGS_SKIRT_ORNAMENT_KIT_OR, 1, false);
				} else if (rn < 6) {
					dropExtraordinary(ItemIdentifiers.DRAGON_PLATELEGS_SKIRT_ORNAMENT_KIT_SP, 1, false);
				} else if (rn < 7) {
					dropExtraordinary(ItemIdentifiers.DRAGON_SQ_SHIELD_ORNAMENT_KIT_OR, 1, false);
				} else if (rn < 8) {
					dropExtraordinary(ItemIdentifiers.DRAGON_SQ_SHIELD_ORNAMENT_KIT_SP, 1, false);
				}
			} else if (rn < 4) {
				drop(ItemIdentifiers.GNOME_SCARF, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.GNOME_GOGGLES, 1);
			}
		} else if (rn < 27) {
			// Family Crest gauntlets
			rn = random.nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.COOKING_GAUNTLETS, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.CHAOS_GAUNTLETS, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.GOLDSMITH_GAUNTLET, 1);
			}
		} else if (rn < 28) {
			drop(ItemIdentifiers.AMULET_OF_GLORY_T, 1);
		} else if (rn < 30) {
			drop(ItemIdentifiers.STRENGTH_AMULET_T, 1);
		} else if (rn < 31) {
			// Gold/trimmed black equipment
			rn = random.nextInt(8);
			if (rn < 1) {
				drop(ItemIdentifiers.BLACK_FULL_HELM_T, 1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.BLACK_PLATEBODY_T, 1);
			} else if (rn < 3) {
				drop(ItemIdentifiers.BLACK_PLATELEGS_T, 1);
			} else if (rn < 4) {
				drop(ItemIdentifiers.BLACK_KITESHIELD_T, 1);
			} else if (rn < 5) {
				drop(ItemIdentifiers.BLACK_FULL_HELM_G, 1);
			} else if (rn < 6) {
				drop(ItemIdentifiers.BLACK_PLATEBODY_G, 1);
			} else if (rn < 7) {
				drop(ItemIdentifiers.BLACK_PLATELEGS_G, 1);
			} else if (rn < 8) {
				drop(ItemIdentifiers.BLACK_KITESHIELD_G, 1);
			}
		} else if (rn < 32) {
			drop(ItemIdentifiers.CAPE_OF_LEGENDS, 1);
		}
	}

	private boolean dropIfNoSuchItem(int id) {
		Container bank = player.getBank();
		Container equipment = player.getEquipment();
		Container inventory = player.getInventory();

		if (bank.contains(id) || equipment.contains(id) || inventory.contains(id)) {
			return false;
		}

		drop(id, 1);
		return true;
	}

	private void drop(int id, int amount) {
		player.getInventory().add(new Item(id, amount));
	}

	private void dropExtraordinary(int id, int amount, boolean log) {
		drop(id, amount);

		// Broadcast to super administrators and print to stdout.

		if (log) {
			System.out.println(player.getName() + " unlocked the crystal chest and found: " + amount + " x " + ItemDefinition.forId(id).getName());
		}

		if (player.isNewsfeedEnabled()) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " unlocked the crystal chest and found " + Utils.formatCountedItem(id, amount) + ".",
					Player::isNewsfeedEnabled);
		} else {
			player.sendMessage("<col=800000>You've found " + Utils.formatCountedItem(id, amount) + "!");
		}
	}

}
