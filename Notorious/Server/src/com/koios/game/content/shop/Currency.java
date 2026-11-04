package com.koios.game.content.shop;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public abstract class Currency {

	protected abstract String getName();

	protected abstract int getAmount(Player player);

	protected abstract void delete(Player player, int amount);

	protected abstract void add(Player player, int amount);

	protected abstract int getSellPrice(Item item);

	protected abstract int getBuyPrice(Item item);

	protected abstract boolean canAdd(Player player, int amount);

}