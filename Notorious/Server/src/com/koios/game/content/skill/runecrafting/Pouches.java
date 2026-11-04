package com.koios.game.content.skill.runecrafting;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public final class Pouches {

	private static final int BROKEN_GIANT_POUCH = 5515;
	private static final int BROKEN_LARGE_POUCH = 5513;
	private static final int BROKEN_MEDIUM_POUCH = 5511;
	private static final int GIANT_POUCH = 5514;
	private static final int LARGE_POUCH = 5512;
	private static final int MEDIUM_POUCH = 5510;
	private static final int PURE_ESSENCE_ID = 7936;
	private static final int RUNE_ESSENCE_ID = 1436;

	public static void addGiantPouch(Player player) {
		int giantPouchCapacity;

		if (player.giantPouchDecay < 0) {
			giantPouchCapacity = 9;
		} else {
			giantPouchCapacity = 12;
		}

		if (player.giantPouchPureEssence + player.giantPouchRuneEssence >= giantPouchCapacity) {
			return;
		}

		if (player.giantPouchDecay > 0) {
			player.giantPouchDecay--;
		}

		if (player.giantPouchDecay == 0) {
			player.getInventory().remove(Item.of(GIANT_POUCH));
			player.getInventory().add(Item.of(BROKEN_GIANT_POUCH));
			giantPouchCapacity = 9;
			player.giantPouchDecay = -1;
		}

		if (player.giantPouchPureEssence + player.giantPouchRuneEssence >= giantPouchCapacity) {
			return;
		}

		int pureEssenceCount = player.getInventory().getCount(PURE_ESSENCE_ID);
		int runeEssenceCount = player.getInventory().getCount(RUNE_ESSENCE_ID);

		if (pureEssenceCount > giantPouchCapacity) {
			pureEssenceCount = giantPouchCapacity;
		}

		if (runeEssenceCount > giantPouchCapacity) {
			runeEssenceCount = giantPouchCapacity;
		}

		for (int i = 0; i < pureEssenceCount; i++) {
			player.giantPouchPureEssence++;
			player.getInventory().remove(Item.of(PURE_ESSENCE_ID));

			if (player.giantPouchPureEssence + player.giantPouchRuneEssence >= giantPouchCapacity) {
				return;
			}
		}

		for (int i = 0; i < runeEssenceCount; i++) {
			player.giantPouchRuneEssence++;
			player.getInventory().remove(Item.of(RUNE_ESSENCE_ID));

			if (player.giantPouchPureEssence + player.giantPouchRuneEssence >= giantPouchCapacity) {
				return;
			}
		}
	}

	public static void addLargePouch(Player player) {
		int largePouchCapacity;

		if (player.largePouchDecay < 0) {
			largePouchCapacity = 6;
		} else {
			largePouchCapacity = 9;
		}

		if (player.largePouchP + player.largePouchE >= largePouchCapacity) {
			return;
		}

		if (player.largePouchDecay > 0) {
			player.largePouchDecay--;
		}

		if (player.largePouchDecay == 0) {
			player.getInventory().remove(Item.of(LARGE_POUCH));
			player.getInventory().add(Item.of(BROKEN_LARGE_POUCH));
			largePouchCapacity = 6;
			player.largePouchDecay = -1;
		}

		if (player.largePouchP + player.largePouchE >= largePouchCapacity) {
			return;
		}

		int pureEssenceCount = player.getInventory().getCount(PURE_ESSENCE_ID);
		int runeEssenceCount = player.getInventory().getCount(RUNE_ESSENCE_ID);

		if (pureEssenceCount > largePouchCapacity) {
			pureEssenceCount = largePouchCapacity;
		}

		if (runeEssenceCount > largePouchCapacity) {
			runeEssenceCount = largePouchCapacity;
		}

		for (int i = 0; i < pureEssenceCount; i++) {
			player.largePouchP++;
			player.getInventory().remove(Item.of(PURE_ESSENCE_ID));

			if (player.largePouchP + player.largePouchE >= largePouchCapacity) {
				return;
			}
		}

		for (int j = 0; j < runeEssenceCount; j++) {
			player.largePouchE++;
			player.getInventory().remove(Item.of(RUNE_ESSENCE_ID));

			if (player.largePouchP + player.largePouchE >= largePouchCapacity) {
				return;
			}
		}
	}

	public static void addMediumPouch(Player player) {
		int mediumPouchCapacity;

		if (player.mediumPouchDecay < 0) {
			mediumPouchCapacity = 3;
		} else {
			mediumPouchCapacity = 6;
		}

		if (player.mediumPouchP + player.mediumPouchE >= mediumPouchCapacity) {
			return;
		}

		if (player.mediumPouchDecay > 0) {
			player.mediumPouchDecay--;
		}

		if (player.mediumPouchDecay == 0) {
			player.getInventory().remove(Item.of(MEDIUM_POUCH));
			player.getInventory().add(Item.of(BROKEN_MEDIUM_POUCH));
			mediumPouchCapacity = 3;
			player.mediumPouchDecay = -1;
		}

		if (player.mediumPouchP + player.mediumPouchE >= mediumPouchCapacity) {
			return;
		}

		int pureEssenceCount = player.getInventory().getCount(PURE_ESSENCE_ID);
		int runeEssenceCount = player.getInventory().getCount(RUNE_ESSENCE_ID);

		if (pureEssenceCount > mediumPouchCapacity) {
			pureEssenceCount = mediumPouchCapacity;
		}

		if (runeEssenceCount > mediumPouchCapacity) {
			runeEssenceCount = mediumPouchCapacity;
		}

		for (int i = 0; i < pureEssenceCount; i++) {
			player.mediumPouchP++;
			player.getInventory().remove(Item.of(PURE_ESSENCE_ID));

			if (player.mediumPouchP + player.mediumPouchE >= mediumPouchCapacity) {
				return;
			}
		}

		for (int i = 0; i < runeEssenceCount; i++) {
			player.mediumPouchE++;
			player.getInventory().remove(Item.of(RUNE_ESSENCE_ID));

			if (player.mediumPouchP + player.mediumPouchE >= mediumPouchCapacity) {
				return;
			}
		}
	}

	public static void addSmallPouch(Player player) {
		if (player.smallPouchPureEssence + player.smallPouchRuneEssence >= 3) {
			return;
		}

		int pureEssenceCount = player.getInventory().getCount(PURE_ESSENCE_ID);
		int runeEssenceCount = player.getInventory().getCount(RUNE_ESSENCE_ID);

		if (pureEssenceCount > 3) {
			pureEssenceCount = 3;
		}

		if (runeEssenceCount > 3) {
			runeEssenceCount = 3;
		}

		for (int i = 0; i < pureEssenceCount; i++) {
			player.smallPouchPureEssence++;
			player.getInventory().remove(Item.of(PURE_ESSENCE_ID));

			if (player.smallPouchPureEssence + player.smallPouchRuneEssence >= 3) {
				return;
			}
		}

		for (int i = 0; i < runeEssenceCount; i++) {
			player.smallPouchRuneEssence++;
			player.getInventory().remove(Item.of(RUNE_ESSENCE_ID));

			if (player.smallPouchPureEssence + player.smallPouchRuneEssence >= 3) {
				return;
			}
		}
	}

	public static void removeGiantPouch(Player player) {
		if (player.giantPouchRuneEssence == 0 && player.giantPouchPureEssence == 0) {
			return;
		}

		int pureEssenceCount = player.giantPouchPureEssence;
		int runeEssenceCount = player.giantPouchRuneEssence;

		for (int i = 0; i < player.giantPouchPureEssence; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(PURE_ESSENCE_ID, 1));
				pureEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.giantPouchPureEssence = pureEssenceCount;
				return;
			}
		}

		player.giantPouchPureEssence = pureEssenceCount;

		for (int i = 0; i < player.giantPouchRuneEssence; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(RUNE_ESSENCE_ID, 1));
				runeEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.giantPouchRuneEssence = runeEssenceCount;
				return;
			}
		}

		player.giantPouchRuneEssence = runeEssenceCount;
	}

	public static void removeLargePouch(Player player) {
		if (player.largePouchE == 0 && player.largePouchP == 0) {
			return;
		}

		int pureEssenceCount = player.largePouchP;
		int runeEssenceCount = player.largePouchE;

		for (int i = 0; i < player.largePouchP; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(PURE_ESSENCE_ID, 1));
				pureEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.largePouchP = pureEssenceCount;
				return;
			}
		}

		player.largePouchP = pureEssenceCount;

		for (int i = 0; i < player.largePouchE; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(RUNE_ESSENCE_ID, 1));
				runeEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.largePouchE = runeEssenceCount;
				return;
			}
		}

		player.largePouchE = runeEssenceCount;
	}

	public static void removeMediumPouch(Player player) {
		if (player.mediumPouchE == 0 && player.mediumPouchP == 0) {
			return;
		}

		int pureEssenceCount = player.mediumPouchP;
		int runeEssenceCount = player.mediumPouchE;

		for (int i = 0; i < player.mediumPouchP; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(PURE_ESSENCE_ID, 1));
				pureEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.mediumPouchP = pureEssenceCount;
				return;
			}
		}

		player.mediumPouchP = pureEssenceCount;

		for (int i = 0; i < player.mediumPouchE; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(RUNE_ESSENCE_ID, 1));
				runeEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.mediumPouchE = runeEssenceCount;
				return;
			}
		}

		player.mediumPouchE = runeEssenceCount;
	}

	public static void removeSmallPouch(Player player) {
		if (player.smallPouchRuneEssence == 0 && player.smallPouchPureEssence == 0) {
			return;
		}

		int pureEssenceCount = player.smallPouchPureEssence;
		int runeEssenceCount = player.smallPouchRuneEssence;

		for (int i = 0; i < player.smallPouchPureEssence; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(PURE_ESSENCE_ID, 1));
				pureEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.smallPouchPureEssence = pureEssenceCount;
				return;
			}
		}

		player.smallPouchPureEssence = pureEssenceCount;

		for (int i = 0; i < player.smallPouchRuneEssence; i++) {
			int invSlots = player.getInventory().freeSlots();

			if (invSlots > 0) {
				player.getInventory().add(Item.of(RUNE_ESSENCE_ID, 1));
				runeEssenceCount--;
			} else {
				player.sendMessage("You have run out of free inventory slots.");
				player.smallPouchRuneEssence = runeEssenceCount;
				return;
			}
		}

		player.smallPouchRuneEssence = runeEssenceCount;
	}

}