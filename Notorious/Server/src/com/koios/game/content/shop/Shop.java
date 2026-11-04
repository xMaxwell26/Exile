package com.koios.game.content.shop;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.ContainerListener;
import com.koios.game.model.item.container.InterfaceContainerListener;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendInventoryOverlayPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

public class Shop extends Container {

	private final int id;
	private final String name;
	private final ShopType type;
	private final Currency currency;
	private final Item[] stock;
	private final boolean unlimitedStock;

	private final Set<Player> viewers = new HashSet<>();

	public Shop(int id, String name, ShopType type, Currency currency, Item[] stock, boolean unlimitedStock) {
		super(Type.ALWAYS_STACK, 40);
		this.id = id;
		this.name = name;
		this.type = type;
		this.currency = currency;
		this.stock = stock;
		this.unlimitedStock = unlimitedStock;

		for (Item item : stock) {
			add(item);
		}

		addListener(new ShopListener());
	}

	public void addViewer(Player player) {
		viewers.add(player);
	}

	public void removeViewer(Player player) {
		viewers.remove(player);
	}

	public boolean buyItem(Player player, int id, int amount, int slot) {
		if (player.getInterfaceState().getCurrentInterface() != 3824 /*
																		 * && player.getInterfaceState(). getSecondaryInterfaceId () != 3822
																		 */) {
			return false;
		}

		if (!viewers.contains(player)) {
			return false;
		}

		Container inventory = player.getInventory();

		Item item = inventory.get(slot);
		if (item == null || item.getId() != id) {
			return false;
		}

		if (amount > item.getCount() && (item.getDefinition().isNoted() || item.getDefinition().isStackable())) {
			amount = item.getCount();
		} else if (amount > inventory.getCount(id) && !item.getDefinition().isNoted() && !item.getDefinition().isStackable()) {
			amount = inventory.getCount(id);
		}

		if (type == ShopType.SELL_ONLY || type == ShopType.BUY_AND_SELL) {
			if (!shopContains(item)) {
				player.sendMessage("You cannot sell that item to this shop.");
				return false;
			}
		}

		int price = currency.getBuyPrice(item) * amount;

		if (!currency.canAdd(player, price)) {
			return false;
		}

		inventory.remove(item.getId(), amount);
		add(new Item(item.getId(), amount));
		fireItemsChanged();

		currency.add(player, price);
		return true;
	}

	public boolean sellItem(Player player, int id, int amount, int slot) {
		if (player.getInterfaceState().getCurrentInterface() != 3824 /*
																		 * && player.getInterfaceState(). getSecondaryInterfaceId () != 3822
																		 */) {
			return false;
		}

		if (!viewers.contains(player)) {
			return false;
		}

		if (type == ShopType.BUY_ONLY) {
			player.sendMessage("You may only sell items to this shop.");
			return false;
		}

		Item item = toArray()[slot];

		if (item == null || item.getId() != id) {
			return false;
		}

		if (item.getCount() == 0) {
			player.sendMessage("That item is currently out of stock.");
			return false;
		}

		Container inventory = player.getInventory();

		if (inventory.freeSlots() <= 0 && !(inventory.contains(item.getId()) && item.getDefinition().isStackable())) {
			player.sendMessage("You do not have enough space in your inventory.");
			return false;
		}

		if (!item.getDefinition().isStackable() && amount > inventory.freeSlots()) {
			amount = inventory.freeSlots();
		} else if (inventory.contains(id) && item.getDefinition().isStackable() && (long) amount + inventory.getCount(id) > Integer.MAX_VALUE) {
			amount = Integer.MAX_VALUE - inventory.getCount(id);
		}

		if (amount == 0) {
			player.sendMessage("You do not have enough space in your inventory.");
			return false;
		}

		if (amount > item.getCount()) {
			amount = item.getCount();
		}

		int playerCurrencyAmount = currency.getAmount(player);

		// Check to ensure they can buy at least 1
		if (playerCurrencyAmount < currency.getSellPrice(item)) {
			player.sendMessage("You do not have enough " + currency.getName() + " to purchase that!");
			return false;
		}

		long value = (long) amount * currency.getSellPrice(item);
		if (value > Integer.MAX_VALUE) {
			value = Integer.MAX_VALUE;
		}

		if (playerCurrencyAmount < value) {
			player.sendMessage("You do not have enough " + currency.getName() + " to purchase that amount.");
			return false;
		}

		currency.delete(player, (int) value);

		if (!unlimitedStock) {
			// Manage the original stock, add 0 of the item if its bought out, otherwise remove like
			// normal.
			if (stock.length > 0 && slot >= 0 && slot < stock.length && stock[slot] != null && stock[slot].getId() == item.getId() && item.getCount() - amount == 0) {
				set(slot, new Item(item.getId(), 0));
				fireItemsChanged();
			} else {
				remove(slot, new Item(item.getId(), amount));
			}
		}

		inventory.add(new Item(item.getId(), amount));
		return true;
	}

	public void checkBuyPrice(Player player, int id, int slot) {
		Item item = player.getInventory().get(slot);
		if (item == null || item.getId() != id) {
			return;
		}

		if (type == ShopType.BUY_ONLY || type == ShopType.BUY_AND_SELL) {
			if (!shopContains(item)) {
				player.sendMessage("You cannot sell that item to this shop.");
				return;
			}
		}

		int price = currency.getBuyPrice(item);
		player.sendMessage(item.getDefinition().getName() + ": shop will buy for " + price + formatAmount(price));
	}

	public void checkSellPrice(Player player, int id, int slot) {
		Item item = toArray()[slot];
		if (item == null || item.getId() != id) {
			return;
		}

		if (type == ShopType.BUY_ONLY) {
			player.sendMessage("You may only sell items to this shop.");
			return;
		}

		int price = currency.getSellPrice(item);
		player.sendMessage(item.getDefinition().getName() + ": currently costs " + price + formatAmount(price));
	}

	private String formatAmount(int amount) {
		StringBuilder builder = new StringBuilder();

		if (amount >= 1000 && amount < 1000000) {
			builder.append(" (").append(amount / 1000).append("K)");
		} else if (amount >= 1000000) {
			builder.append(" (").append(amount / 1000000).append(" million)");
		}

		builder.append(" ").append(currency.getName()).append(".");

		return builder.toString();
	}

	public void open(Player player) {
		if (player.currentShop != -1) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		player.currentShop = id;

		addViewer(player);
		refresh(player);

		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 3823));
		player.getSession().send(new SendInventoryOverlayPacket(3824, 3822));
		player.getSession().send(new SendStringPacket(name, 3901));
	}

	public boolean shopContains(Item item) {
		return Stream.of(stock).anyMatch(shopItem -> shopItem != null && item.getId() == shopItem.getId());
	}

	public boolean isRegulated() {
		return true;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ShopType getType() {
		return type;
	}

	public Item[] getStock() {
		return stock;
	}

	private final class ShopListener implements ContainerListener {

		@Override
		public void onSingleItemChange(Container container, int slot) {
			onFullItemChange(container);
		}

		@Override
		public void onItemGroupChange(Container container, int[] slots) {
			onFullItemChange(container);
		}

		@Override
		public void onFullItemChange(Container container) {
			for (Player player : viewers) {
				refresh(player);
			}
		}

	}

	public void refresh(Player player) {
		ActionSender.sendInterfaceItems(player, 3900, toArray());
		ActionSender.sendInterfaceItems(player, 3823, player.getInventory().toArray());
	}

	public Set<Player> getViewers() {
		return viewers;
	}

}