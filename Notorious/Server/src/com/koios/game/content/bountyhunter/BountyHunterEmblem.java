package com.koios.game.content.bountyhunter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Handles the bounty hunter's emblems
 *
 * @author Gabriel | Wolfs Darker && Jason
 */
public enum BountyHunterEmblem {

	TIER_1(14876, 50_000, 0), TIER_2(14877, 100_000, 1), TIER_3(14878, 200_000, 2), TIER_4(14879, 400_000, 3), TIER_5(14880, 750_000, 4), TIER_6(14881, 1_250_000, 5), TIER_7(14882,
			1_750_000, 6), TIER_8(14883, 2_500_000, 7), TIER_9(14884, 3_500_000, 8), TIER_10(14885, 5_000_000, 9);

	private final int itemId;
	private final int bounties;
	private final int index;

	private BountyHunterEmblem(int itemId, int bounties, int index) {
		this.itemId = itemId;
		this.bounties = bounties;
		this.index = index;
	}

	public static final Set<BountyHunterEmblem> EMBLEMS = Collections.unmodifiableSet(EnumSet.allOf(BountyHunterEmblem.class));

	public static BountyHunterEmblem valueOf(int index) {
		if (index >= EMBLEMS.size()) {
			return BountyHunterEmblem.TIER_10;
		}

		for (BountyHunterEmblem emblem : EMBLEMS) {
			if (emblem.getIndex() == index) {
				return emblem;
			}
		}

		return BountyHunterEmblem.TIER_1;
	}

	public static BountyHunterEmblem getNext(BountyHunterEmblem emblem) {
		return valueOf(emblem.getIndex() + 1);
	}

	public static BountyHunterEmblem getPrevious(BountyHunterEmblem emblem) {
		return valueOf(emblem.getIndex() - 1);
	}

	public int getItemId() {
		return itemId;
	}

	public int getBounties() {
		return bounties;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * Returns the best emblem for the player next reward.
	 *
	 * @param player
	 * @return the best emblem.
	 */
	public static BountyHunterEmblem getBest(Player player, boolean deletePrevious) {
		BountyHunterEmblem tier = BountyHunterEmblem.TIER_1;
		int toDelete = -1;

		for (BountyHunterEmblem emblem : EMBLEMS) {
			if (player.getInventory().contains(emblem.getItemId())) {
				tier = getNext(emblem);
				toDelete = emblem.getItemId();
			}
		}

		if (toDelete > 0 && deletePrevious) {
			player.getInventory().remove(new Item(toDelete));
		}

		return tier;
	}

	/**
	 * Returns the value of bounties the player carries with his emblems.
	 *
	 * @param player
	 * @return the bounties count
	 */
	public static int getBountiesCarried(Player player, boolean deleteItems) {
		int currentAmount = 0;
		boolean mustDelete = false;

		for (int i = 0; i < player.getInventory().size(); i++) {
			Item item = player.getInventory().get(i);

			if (item == null) {
				continue;
			}

			for (BountyHunterEmblem emblem : EMBLEMS) {
				if (item.getId() == emblem.getItemId()) {
					currentAmount += emblem.getBounties();
					mustDelete = true;
				}
			}

			if (deleteItems && mustDelete) {
				mustDelete = false;
				player.getInventory().set(i, null);
			}
		}

		return currentAmount;
	}

}
