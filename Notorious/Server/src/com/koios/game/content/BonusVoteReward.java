package com.koios.game.content;

import java.util.Random;

import com.koios.GameService;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.Utils;

/**
 * @author Tringan
 * @author Raw Envy
 * @author Erik Eide (rework on 2014-10-14)
 */
public final class BonusVoteReward {

	public static BonusVoteReward newInstance(Player player) {
		return new BonusVoteReward(player);
	}

	private final Player player;
	private final Random random;

	/**
	 * @throws NullPointerException if {@code player == null}.
	 */
	private BonusVoteReward(Player player) {
		this.player = player;
		random = player.getRandom();
	}

	public void loot() {
		int num = random.nextInt(14);

		if (num < 1) {
			// Inferno adze
			num = random.nextInt(3);
			if (num < 1) {
				dropWithBroadcast(ItemIdentifiers.INFERNO_ADZE, 1);
			} else if (num < 3) {
				// ... and animal masks!
				num = random.nextInt(6);
				if (num < 1) {
					dropWithBroadcast(ItemIdentifiers.WOLF_MASK, 1);
				} else if (num < 2) {
					dropWithBroadcast(ItemIdentifiers.SHEEP_MASK, 1);
				} else if (num < 3) {
					dropWithBroadcast(ItemIdentifiers.CAT_MASK, 1);
				} else if (num < 4) {
					dropWithBroadcast(ItemIdentifiers.BAT_MASK, 1);
				} else if (num < 5) {
					dropWithBroadcast(ItemIdentifiers.FOX_MASK, 1);
				} else if (num < 6) {
					dropWithBroadcast(ItemIdentifiers.PENGUIN_MASK, 1);
				}
			}
		} else if (num < 3) {
			// Royal outfit
			num = random.nextInt(5);
			if (num < 1) {
				dropWithBroadcast(ItemIdentifiers.ROYAL_CROWN, 1);
			} else if (num < 2) {
				dropWithBroadcast(ItemIdentifiers.ROYAL_SHIRT, 1);
			} else if (num < 3) {
				dropWithBroadcast(ItemIdentifiers.ROYAL_LEGGINGS, 1);
			} else if (num < 4) {
				dropWithBroadcast(ItemIdentifiers.ROYAL_AMULET, 1);
			} else if (num < 5) {
				dropWithBroadcast(ItemIdentifiers.ROYAL_SCEPTRE, 1);
			}
		} else if (num < 7) {
			// God-aligned runite equipment
			num = random.nextInt(12);
			// Guthix runite armour
			if (num < 1) {
				drop(ItemIdentifiers.GUTHIX_FULL_HELM, 1);
			} else if (num < 2) {
				dropPrivate(ItemIdentifiers.GUTHIX_PLATEBODY, 1);
			} else if (num < 3) {
				if (player.getAppearance().isMale()) {
					dropPrivate(ItemIdentifiers.GUTHIX_PLATELEGS, 1);
				} else {
					dropPrivate(ItemIdentifiers.GUTHIX_PLATESKIRT, 1);
				}
			} else if (num < 4) {
				dropPrivate(ItemIdentifiers.GUTHIX_KITESHIELD, 1);
			} else if (num < 5) {
				dropPrivate(ItemIdentifiers.SARADOMIN_FULL_HELM, 1);
			} else if (num < 6) {
				dropPrivate(ItemIdentifiers.SARADOMIN_PLATEBODY, 1);
			} else if (num < 7) {
				if (player.getAppearance().isMale()) {
					dropPrivate(ItemIdentifiers.SARADOMIN_PLATELEGS, 1);
				} else {
					dropPrivate(ItemIdentifiers.SARADOMIN_PLATESKIRT, 1);
				}
			} else if (num < 8) {
				dropPrivate(ItemIdentifiers.SARADOMIN_KITESHIELD, 1);
			} else if (num < 9) {
				dropPrivate(ItemIdentifiers.ZAMORAK_FULL_HELM, 1);
			} else if (num < 10) {
				dropPrivate(ItemIdentifiers.ZAMORAK_PLATEBODY, 1);
			} else if (num < 11) {
				if (player.getAppearance().isMale()) {
					dropPrivate(ItemIdentifiers.ZAMORAK_PLATELEGS, 1);
				} else {
					dropPrivate(ItemIdentifiers.ZAMORAK_PLATESKIRT, 1);
				}
			} else if (num < 12) {
				dropPrivate(ItemIdentifiers.ZAMORAK_KITESHIELD, 1);
			}
		} else if (num < 14) {
			// Pet kittens
			num = random.nextInt(7);
			if (num < 1) {
				dropPrivate(1555, 1);
			} else if (num < 2) {
				dropPrivate(1556, 1);
			} else if (num < 3) {
				dropPrivate(1557, 1);
			} else if (num < 4) {
				dropPrivate(1558, 1);
			} else if (num < 5) {
				dropPrivate(1559, 1);
			} else if (num < 6) {
				dropPrivate(1560, 1);
			} else if (num < 7) {
				// ... and corrupt dragon equipment
				num = random.nextInt(11);
				if (num < 1) {
					drop(ItemIdentifiers.CORRUPT_DRAGON_MED_HELM, 1);
				} else if (num < 2) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_CHAINBODY, 1);
				} else if (num < 3) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_SQ_SHIELD, 1);
				} else if (num < 4) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_PLATESKIRT, 1);
				} else if (num < 5) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_PLATELEGS, 1);
				} else if (num < 6) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_BATTLEAXE, 1);
				} else if (num < 7) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_DAGGER, 1);
				} else if (num < 8) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_SCIMITAR, 1);
				} else if (num < 9) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_LONGSWORD, 1);
				} else if (num < 10) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_MACE, 1);
				} else if (num < 11) {
					dropPrivate(ItemIdentifiers.CORRUPT_DRAGON_SPEAR, 1);
				}
			}
		}
	}

	private void drop(int id, int amount) {
		Item item = new Item(id, amount);

		if (player.getInventory().hasRoomFor(item)) {
			player.getInventory().add(item);
		} else if (player.getBank().hasRoomFor(item)) {
			Bank.addToBank(player, item);
		} else {
			GroundItemManager.spawnNew(player, item);
		}
	}

	private void dropPrivate(int id, int amount) {
		drop(id, amount);
		player.sendMessage("You receive " + Utils.formatCountedItem(id, amount) + " as an added benefit for voting!");
	}

	private void dropWithBroadcast(int id, int amount) {
		if (player.isNewsfeedEnabled()) {
			drop(id, amount);
			GameService.getWorld().broadcast("<col=800000>News: " + player.getName() + " received vote bonus: " + Utils.formatCountedItem(id, amount) + "!",
					Player::isNewsfeedEnabled);
		} else {
			dropPrivate(id, amount);
		}
	}

}
