package com.koios.game.content;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Displays the items kept on death
 *
 * @author Gabriel | Wolfs Darker
 */
public class KeepOnDeath {

	/**
	 * Grabs a list with the most valuable items.
	 */
	public static List<Item> getMostValuedItems(Player player) {
		int keptAmount = (player.isSkulled ? 0 : 3) + (player.getPrayers().isActive(NormalPrayer.PROTECT_ITEM, CursedPrayer.PROTECT_ITEM) ? 1 : 0);
		CopyOnWriteArrayList<Item> items = new CopyOnWriteArrayList<>();

		items.addAll(player.getInventory().getValidItems());
		items.addAll(player.getEquipment().getValidItems());

		CopyOnWriteArrayList<Item> keptItems = new CopyOnWriteArrayList<>();

		int last_price = 0;
		Item kept = null;

		for (int i = 0; i < keptAmount; i++) {
			for (Item item : items) {
				if (item.getDefinition().getValue() > last_price && !keptItems.contains(item)) {
					last_price = item.getDefinition().getValue();
					kept = item;
				}
			}

			keptItems.add(kept);
			items.remove(kept);
			kept = null;
			last_price = 0;
		}

		return keptItems;
	}

	/**
	 * Send the items to the interface
	 */
	public static void sendItems(Player player) {
		if (player.getInterfaceState().getCurrentInterface() != 17100) {
			return;
		}

		List<Item> protItems = getMostValuedItems(player);
		CopyOnWriteArrayList<Item> otherItems = new CopyOnWriteArrayList<>();

		for (int i = 0; i < 4; i++) {
			ActionSender.sendUpdateItem34(player, 10494, i, new Item(-1, 1));
		}

		for (int i = 0; i < 40; i++) {
			ActionSender.sendUpdateItem34(player, 10600, i, new Item(-1, 1));
		}

		otherItems.addAll(player.getInventory().getValidItems());
		otherItems.addAll(player.getEquipment().getValidItems());

		for (int i = 0; i < protItems.size(); i++) {
			if (protItems.get(i) != null && protItems.get(i).getId() > 0) {
				otherItems.remove(protItems.get(i));
				ActionSender.sendUpdateItem34(player, 10494, i, protItems.get(i));
			}
		}

		for (int i = 0; i < otherItems.size(); i++) {
			if (otherItems.get(i) != null && otherItems.get(i).getId() > 0) {
				ActionSender.sendUpdateItem34(player, 10600, i, otherItems.get(i));
			}
		}

		player.getSession().send(new SendStringPacket("~ " + protItems.size() + " ~", 11708));
	}

}
