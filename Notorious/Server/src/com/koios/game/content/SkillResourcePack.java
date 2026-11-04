package com.koios.game.content;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public final class SkillResourcePack {

	public static void unpack(Player player, int slot, int id, int productId, int amount) {
		/*
		 * Each unit yields one hundred products, so we deny requests to unpack more than 20 million units to avoid unpredictable results caused by multiplication that overflows
		 * several times.
		 */
		if (amount > 20_000_000) {
			player.sendMessage("Requested action aborted. Please try again with at most 20,000,000 units.");
			return;
		}

		int packAmount = player.getInventory().get(slot).getCount();
		int freeSlots = player.getInventory().freeSlots();
		int productAmount = player.getInventory().getCount(productId);
		int unpackAmount = amount * 100;
		int totalAmount = productAmount + unpackAmount;

		// In a scenario where we've hit an overflow we make an effort to unpack what we can!
		if (totalAmount < 0 && amount > 1) {
			int overflow = Math.abs(Integer.MAX_VALUE - totalAmount) / 100;
			amount -= overflow;
			unpackAmount = amount * 100;
		}

		if (unpackAmount == 0 || unpackAmount < packAmount && freeSlots == 0 && productAmount == 0) {
			String packName = ItemDefinition.forId(id).getName().toLowerCase();
			player.sendMessage("You do not have enough inventory space to loot this " + packName + ".");
		} else {
			String base;

			switch (productId) {
			case 222:
				base = "You unpack %s eyes of newt.";
				break;

			case 228:
				base = "You unpack %s vials of water.";
				break;

			case 230:
				base = "You unpack %s vials.";
				break;

			default:
				base = "You unpack %s from this " + ItemDefinition.forId(id).getName() + ".";
			}

			player.getInventory().remove(slot, new Item(id, amount));
			player.getInventory().add(new Item(productId, unpackAmount));
			player.sendMessage(String.format(base, NumberUtil.format(unpackAmount)));
		}
	}

	private SkillResourcePack() {
		throw new InstantiationError("You cannot instantiate com.com.world.content.SkillResourcePack!");
	}

}
