package com.koios.game.content.obj;

import java.util.Objects;
import java.util.Random;

import com.koios.GameService;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.Utils;

public final class MysteryBox {

	/**
	 * Static factory providing a new instance of {@code MysteryBox}.
	 *
	 * @param player The player to loot the mystery box. Not null.
	 * @return A new instance that will generate loot for the argument player.
	 */
	public static MysteryBox newInstance(Player player) {
		return new MysteryBox(player);
	}

	private final Player player;
	private final Random random;

	public MysteryBox(Player player) {
		this.player = Objects.requireNonNull(player);
		random = player.getRandom();
	}

	public void loot() {
		int num = random.nextInt(24);

		if (num < 1) {
			// Godwars hilts, minor weapons and dragon claws
			num = random.nextInt(20);
			if (num < 1) {
				add(ItemIdentifiers.DRAGON_CLAWS, 1);
			} else if (num < 2) {
				add(ItemIdentifiers.ARMADYL_HILT, 1);
			} else if (num < 4) {
				add(ItemIdentifiers.SARADOMIN_HILT, 1);
			} else if (num < 7) {
				add(ItemIdentifiers.ZAMORAK_HILT, 1);
			} else if (num < 10) {
				add(ItemIdentifiers.BANDOS_HILT, 1);
			} else if (num < 15) {
				add(ItemIdentifiers.ZAMORAKIAN_SPEAR, 1);
			} else if (num < 20) {
				add(ItemIdentifiers.SARADOMIN_SWORD, 1);
			}
		} else if (num < 3) {
			// Barrows equipment (and chance at spiritual sigils)
			num = random.nextInt(25);

			if (num < 1) {
				add(ItemIdentifiers.AHRIMS_HOOD, 1);
			} else if (num < 2) {
				add(ItemIdentifiers.AHRIMS_ROBETOP, 1);
			} else if (num < 3) {
				add(ItemIdentifiers.AHRIMS_ROBESKIRT, 1);
			} else if (num < 4) {
				add(ItemIdentifiers.AHRIMS_STAFF, 1);
			} else if (num < 5) {
				add(ItemIdentifiers.DHAROKS_HELM, 1);
			} else if (num < 6) {
				add(ItemIdentifiers.DHAROKS_PLATEBODY, 1);
			} else if (num < 7) {
				add(ItemIdentifiers.DHAROKS_PLATELEGS, 1);
			} else if (num < 8) {
				add(ItemIdentifiers.DHAROKS_GREATAXE, 1);
			} else if (num < 9) {
				add(ItemIdentifiers.TORAGS_HELM, 1);
			} else if (num < 10) {
				add(ItemIdentifiers.TORAGS_PLATEBODY, 1);
			} else if (num < 11) {
				add(ItemIdentifiers.TORAGS_PLATELEGS, 1);
			} else if (num < 12) {
				add(ItemIdentifiers.TORAGS_HAMMERS, 1);
			} else if (num < 13) {
				add(ItemIdentifiers.GUTHANS_HELM, 1);
			} else if (num < 14) {
				add(ItemIdentifiers.GUTHANS_PLATEBODY, 1);
			} else if (num < 15) {
				add(ItemIdentifiers.GUTHANS_CHAINSKIRT, 1);
			} else if (num < 16) {
				add(ItemIdentifiers.GUTHANS_WARSPEAR, 1);
			} else if (num < 17) {
				add(ItemIdentifiers.VERACS_HELM, 1);
			} else if (num < 18) {
				add(ItemIdentifiers.VERACS_BRASSARD, 1);
			} else if (num < 19) {
				add(ItemIdentifiers.VERACS_PLATESKIRT, 1);
			} else if (num < 20) {
				add(ItemIdentifiers.VERACS_FLAIL, 1);
			} else if (num < 21) {
				add(ItemIdentifiers.KARILS_COIF, 1);
			} else if (num < 22) {
				add(ItemIdentifiers.KARILS_LEATHERTOP, 1);
			} else if (num < 23) {
				add(ItemIdentifiers.KARILS_LEATHERSKIRT, 1);
			} else if (num < 24) {
				add(ItemIdentifiers.KARILS_CROSSBOW, 1);
			} else if (num < 25) {
				// Exceedingly rare spiritual sigils
				String name = player.getName();
				num = random.nextInt(8);

				if (num < 1) {
					add(ItemIdentifiers.DIVINE_SIGIL, 1);
					GameService.getWorld().broadcast("<img=9> @blu@News: " + name + " found a divine sigil in a mystery box!", Player::isNewsfeedEnabled);
				} else if (num < 2) {
					add(ItemIdentifiers.ELYSIAN_SIGIL, 1);
					GameService.getWorld().broadcast("<img=9> @blu@News: " + name + " found an elysian sigil in a mystery box!", Player::isNewsfeedEnabled);
				} else if (num < 5) {
					add(ItemIdentifiers.SPECTRAL_SIGIL, 1);
					GameService.getWorld().broadcast("<img=9> @blu@News: " + name + " found a spectral sigil in a mystery box!", Player::isNewsfeedEnabled);
				} else if (num < 8) {
					add(ItemIdentifiers.ARCANE_SIGIL, 1);
					GameService.getWorld().broadcast("<img=9> @blu@News: " + name + " found an arcane sigil in a mystery box!", Player::isNewsfeedEnabled);
				}
			}
		} else if (num < 7) {
			// Prayer resources
			num = random.nextInt(6);
			if (num < 1) {
				add(ItemIdentifiers.DRAGON_BONES_NOTE, 100);
			} else if (num < 3) {
				add(ItemIdentifiers.BIG_BONES_NOTE, 250);
			} else if (num < 6) {
				add(ItemIdentifiers.BONES_NOTE, 500);
			}
		} else if (num < 11) {
			// Cooking resources
			num = random.nextInt(6);
			if (num < 1) {
				add(ItemIdentifiers.RAW_ROCKTAIL_NOTE, 100);
			} else if (num < 3) {
				add(ItemIdentifiers.RAW_SHARK_NOTE, 250);
			} else if (num < 6) {
				add(ItemIdentifiers.RAW_LOBSTER_NOTE, 500);
			}
		} else if (num < 15) {
			// Fletching resources
			num = random.nextInt(9);
			if (num < 1) {
				add(ItemIdentifiers.MAGIC_LOGS_NOTE, 100);
			} else if (num < 3) {
				add(ItemIdentifiers.YEW_LOGS_NOTE, 250);
			} else if (num < 6) {
				add(ItemIdentifiers.MAPLE_LOGS_NOTE, 500);
			} else if (num < 9) {
				add(ItemIdentifiers.BOW_STRING_NOTE, 250);
			}
		} else if (num < 16) {
			// Runecrafting resources
			num = random.nextInt(5);
			if (num < 2) {
				add(ItemIdentifiers.PURE_ESSENCE_NOTE, 10_000);
			} else if (num < 5) {
				add(ItemIdentifiers.RUNE_ESSENCE_NOTE, 10_000);
			}
		} else if (num < 19) {
			// Magic runes
			num = random.nextInt(26);
			if (num < 1) {
				add(ItemIdentifiers.BLOOD_RUNE, 5_000);
			} else if (num < 2) {
				add(ItemIdentifiers.DEATH_RUNE, 5_000);
			} else if (num < 3) {
				add(ItemIdentifiers.NATURE_RUNE, 2_500);
			} else if (num < 4) {
				add(ItemIdentifiers.ASTRAL_RUNE, 2_500);
			} else if (num < 6) {
				add(ItemIdentifiers.CHAOS_RUNE, 5_000);
			} else if (num < 11) {
				add(ItemIdentifiers.AIR_RUNE, 10_000);
			} else if (num < 16) {
				add(ItemIdentifiers.WATER_RUNE, 10_000);
			} else if (num < 21) {
				add(ItemIdentifiers.EARTH_RUNE, 10_000);
			} else if (num < 26) {
				add(ItemIdentifiers.FIRE_RUNE, 10_000);
			}
		} else if (num < 23) {
			// Rune armour
			num = random.nextInt(11);
			if (num < 1) {
				add(ItemIdentifiers.DRAGON_MED_HELM, 1);
			} else if (num < 3) {
				add(ItemIdentifiers.RUNE_FULL_HELM, 1);
			} else if (num < 5) {
				add(ItemIdentifiers.RUNE_PLATEBODY, 1);
			} else if (num < 7) {
				add(ItemIdentifiers.RUNE_PLATELEGS, 1);
			} else if (num < 9) {
				add(ItemIdentifiers.RUNE_KITESHIELD, 1);
			} else if (num < 11) {
				num = random.nextInt(4);
				if (num < 1) {
					add(ItemIdentifiers.DRAGON_BOOTS, 1);
				} else if (num < 4) {
					add(ItemIdentifiers.RUNE_BOOTS, 1);
				}
			}
		} else if (num < 24) {
			player.getInventory().add(new Item(ItemIdentifiers.SPINACH_ROLL));
			player.sendMessage("You find a spinach roll wrapped inside the mystery box. Better luck next time!");
		}

		if (!GameService.isOnlineMode()) {
			int count = player.increaseVariable("mb_count", 1);
			player.sendMessage("Mystery boxes opened: <col=800000>" + count);
		}
	}

	/*
	 * Add item to inventory and format an informative server message telling the player what they find inside the mystery box.
	 */
	private void add(int id, int amount) {
		String msg = "You find " + Utils.formatCountedItem(id, amount) + " wrapped inside the mystery box.";

		player.getInventory().add(new Item(id, amount));
		player.sendMessage(msg);
	}

}
