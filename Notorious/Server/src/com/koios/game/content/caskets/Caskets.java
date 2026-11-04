package com.koios.game.content.caskets;

import java.util.ArrayList;
import java.util.List;

import com.koios.game.content.caskets.CasketConstants.CasketLoot;
import com.koios.game.content.caskets.CasketConstants.Rarity;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handle the rewards from caskets.
 *
 * @author Gabriel | Wolfs darker
 */
public class Caskets {

	/**
	 * Opens the casket.
	 */
	public static void openCasket(Player player, int itemId, ItemDefinition definition, int slot) {
		if (player.getInventory().get(slot) == null || player.getInventory().get(slot).getId() != itemId || !player.getInventory().contains(itemId) || definition == null) {
			return;
		}
		player.lastAction = System.currentTimeMillis();
		if (definition.getName().toLowerCase().equals("casket")) {
			List<CasketLoot> possible_loots = new ArrayList<>();
			int random = NumberUtil.inclusiveRandom(100);
			Rarity r = random > 90 ? Rarity.RARE : random >= 50 && random <= 90 ? Rarity.UNCOMMON : Rarity.COMMON;

			for (int i = 0; i < CasketConstants.LOOT_TABLE[0].length; i++) {
				if (CasketConstants.LOOT_TABLE[0][i].getRarity() == r) {
					possible_loots.add(CasketConstants.LOOT_TABLE[0][i]);
				}
			}

			CasketLoot loot = possible_loots.get(possible_loots.size() == 1 ? 0 : NumberUtil.random(possible_loots.size() - 1));

			if (loot == null || loot.getItem() == null) {
				return;
			}

			player.getInventory().remove(slot, new Item(itemId, 1));
			player.getInventory().add(loot.getItem().getId() == 995 ? new Item(995, NumberUtil.inclusiveRandom(8000 + r.ordinal() * 5000)) : loot.getItem());
		}

		if (player.getInventory().freeSlots() < 3) {
			player.sendMessage("You need at least 3 free spaces in your inventory to open this!");
			return;
		}

		if (definition.getName().toLowerCase().contains("easy") || definition.getName().toLowerCase().contains("medium") || definition.getName().toLowerCase().contains("hard")) {
			int count = NumberUtil.inclusiveRandom(4) + 1;
			boolean rare = false;
			List<Item> items = new ArrayList<>();
			ActionSender.sendInterfaceItems(player, 6963, new Item[6]);
			List<CasketLoot> possibleLoots = new ArrayList<>();
			Rarity rarity;

			for (int i = 0; i < count; i++) {
				possibleLoots.clear();
				rarity = Rarity.COMMON;

				if (!rare && NumberUtil.inclusiveRandom(350 / (i <= 0 ? 1 : i)) == 1) {
					rarity = Rarity.VERY_RARE;
					rare = true;
				} else if (!rare && NumberUtil.inclusiveRandom(150 / (i <= 0 ? 1 : i)) == 1) {
					rarity = Rarity.RARE;
					rare = true;
				} else if (NumberUtil.inclusiveRandom(30 / (i <= 0 ? 1 : i)) == 1) {
					rarity = Rarity.UNCOMMON;
				}

				for (int j = 0; j < CasketConstants.LOOT_TABLE[1].length; j++) {
					if (rarity == CasketConstants.LOOT_TABLE[1][j].getRarity()) {
						if (!possibleLoots.contains(CasketConstants.LOOT_TABLE[1][j]) && !items.contains(CasketConstants.LOOT_TABLE[1][j].getItem())) {
							possibleLoots.add(CasketConstants.LOOT_TABLE[1][j]);
						}
					}
				}

				CasketLoot newItem = possibleLoots.get(NumberUtil.inclusiveRandom(possibleLoots.size() - 1));

				if (newItem.getRandomAmount() > 0) {
					newItem.getItem().setCount(NumberUtil.inclusiveRandom(newItem.getRandomAmount()) + 1);
				}

				items.add(newItem.getItem());
			}

			ActionSender.sendInterfaceItems(player, 6963, items);
			player.getInterfaceState().openInterface(6960);
			player.getInventory().remove(new Item(itemId));

			for (Item reward : items) {
				if (!player.getInventory().add(reward)) {
					if (Bank.addToBank(player, reward)) {
						player.sendMessage("Any missing items have been deposited in your bank!");
					}
				}
			}
		}
	}

}
