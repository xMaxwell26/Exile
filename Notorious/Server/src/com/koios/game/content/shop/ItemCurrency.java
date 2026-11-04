package com.koios.game.content.shop;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.player.Player;

public abstract class ItemCurrency extends Currency {

	private final int id;

	public ItemCurrency(int id) {
		this.id = id;
	}

	@Override
	protected String getName() {
		return ItemDefinition.forId(id).getName();
	}

	@Override
	protected int getAmount(Player player) {
		return player.getInventory().getCount(id);
	}

	@Override
	protected void delete(Player player, int amount) {
		if (amount > 0) {
			player.getInventory().remove(id, amount);
		}
	}

	@Override
	protected void add(Player player, int amount) {
		if (amount > 0) {
			player.getInventory().add(new Item(id, amount));
		}
	}

	@Override
	protected boolean canAdd(Player player, int amount) {
		Container inventory = player.getInventory();
		ItemDefinition definition = ItemDefinition.forId(id);

		boolean full = false;
		if (definition.isStackable() && !inventory.contains(id) && inventory.freeSlots() < 1) {
			full = true;
		} else if (!definition.isStackable() && inventory.freeSlots() < amount) {
			full = true;
		}

		if (full) {
			player.sendMessage("You do not have enough space in your inventory.");
			return false;
		}

		return true;
	}

}