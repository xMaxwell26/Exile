package com.koios.game.content.skill.herblore.potions;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemAssistant;
import com.koios.game.model.player.Player;

/**
 * @author Sanity
 */
public final class PotionMixing {

	/**
	 * Decant all potions in inventory
	 *
	 * @param player player to decant for
	 */
	public static int decantAll(Player player) {
		for (int i = 0; i < player.getInventory().getCapacity(); i++) {

			if (player.getInventory().get(i) == null) {
				continue;
			}

			int item1 = player.getInventory().get(i).getId();

			if (item1 <= 0) {
				continue;
			}

			String name1 = ItemDefinition.forId(item1).getName();

			if (!name1.contains("(4)") && !name1.contains("(3)") && !name1.contains("(2)") && !name1.contains("(1)")) {
				continue;
			}

			for (int k = 0; k < player.getInventory().getCapacity(); k++) {
				if (player.getInventory().get(k) == null) {
					continue;
				}

				int item2 = player.getInventory().get(k).getId();

				if (item2 <= 0) {
					continue;
				}

				String name2 = ItemDefinition.forId(item1).getName();

				if (!name2.contains("(4)") && !name2.contains("(3)") && !name2.contains("(2)") && !name2.contains("(1)")) {
					continue;
				}

				PotionMixing.redose(player, item1, item2);

			}
		}

		return 1;
	}

	public static void redose(Player player, int id1, int id2) {
		String firstName = ItemDefinition.forId(id1).getName();
		String secondName = ItemDefinition.forId(id2).getName();

		if (!firstName.contains("(")) {
			return;
		}

		if (!secondName.contains("(")) {
			return;
		}

		if (ItemDefinition.forId(id1).isNoted() || ItemDefinition.forId(id2).isNoted()) {
			/* Potion mixing cannot be performed with noted potions. */
			return;
		}

		if (firstName.substring(0, firstName.indexOf("(")).equalsIgnoreCase(secondName.substring(0, secondName.indexOf("(")))) {
			int amount1 = Integer.parseInt(firstName.substring(firstName.indexOf("(") + 1, firstName.indexOf("(") + 2));
			int amount2 = Integer.parseInt(secondName.substring(secondName.indexOf("(") + 1, secondName.indexOf("(") + 2));
			int totalAmount = amount1 + amount2;

			if (totalAmount > 4) {
				amount1 = 4;
				amount2 = totalAmount - 4;
				String item1 = firstName.substring(0, firstName.indexOf("(") + 1) + amount1 + ")";
				String item2 = firstName.substring(0, firstName.indexOf("(") + 1) + amount2 + ")";
				player.getInventory().remove(new Item(id1, 1));
				player.getInventory().remove(new Item(id2, 1));
				player.getInventory().add(new Item(ItemAssistant.getItemID(item1), 1));
				player.getInventory().add(new Item(ItemAssistant.getItemID(item2), 1));
			} else {
				amount1 = totalAmount;
				String item1 = firstName.substring(0, firstName.indexOf("(") + 1) + amount1 + ")";
				player.getInventory().remove(new Item(id1, 1));
				player.getInventory().remove(new Item(id2, 1));
				player.getInventory().add(new Item(ItemAssistant.getItemID(item1), 1));
				player.getInventory().add(new Item(229, 1));
			}
		}
	}

}
