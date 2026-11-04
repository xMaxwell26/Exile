package com.koios.game.model.item.container;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendInventoryOverlayPacket;

public class FamiliarInventory {

	/**
	 * Deposits an item.
	 *
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to deposit.
	 */
	public static void deposit(Player player, int slot, int id, int amount) {
        if(player.getInventory().get(slot)!=null && player.getInventory().get(slot).hasRoll()){player.sendMessage("Rolled gear cannot use this legacy transfer yet.");return;}
		try {
			Item item = player.getInventory().get(slot);

			if (item == null || item.getId() != id) {
				return;
			}

			int itemCount = item.getDefinition().isStackable() ? player.getInventory().getCount(item.getId(), slot) : player.getInventory().getCount(item.getId());

			if (itemCount < amount) {
				amount = itemCount;
			}

			if (amount == 0) {
				return;
			}

			if (item.getDefinition().isStackable() || item.getDefinition().isNoted()) {
				if (player.getSummoning().getFamiliarInventory().freeSlots() <= 0 && player.getSummoning().getFamiliarInventory().getById(item.getId()) == null) {
					player.sendMessage("Your don't have enough space to store this item.");
					return;
				}

				int newInventoryAmount = item.getCount() - amount;
				Item newItem;

				if (newInventoryAmount <= 0) {
					newItem = null;
				} else {
					newItem = new Item(item.getId(), newInventoryAmount);
				}

				if (!player.getSummoning().getFamiliarInventory().add(new Item(item.getId(), amount))) {
					player.sendMessage("Your don't have enough space to store this item.");
					return;
				} else {
					player.getInventory().set(slot, newItem);
					player.getInventory().fireItemsChanged();
					player.getSummoning().getFamiliarInventory().fireItemsChanged();
				}
			} else {
				if (player.getSummoning().getFamiliarInventory().freeSlots() < amount) {
					player.sendMessage("Your don't have enough space to store this item.");
					return;
				}

				item.setCount(amount);

				if (!player.getSummoning().getFamiliarInventory().add(item)) {
					player.sendMessage("Your don't have enough space to store this item.");
				} else {
					for (int i = 0; i < amount; i++) {
						player.getInventory().set(player.getInventory().getSlotById(item), null);
					}

					player.getInventory().fireItemsChanged();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void openBoBInventory(Player player) {
		if (player.getSummoning().getFamiliarInventory() == null) {
			player.sendMessage("Your familiar can't store any items.");
			return;
		}

		player.getSession().send(new SendInventoryOverlayPacket(2700, 5063));
		player.getInterfaceState().addListener(player.getSummoning().getFamiliarInventory(), new InterfaceContainerListener(player, 2702));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 5064));
	}

	/**
	 * Withdraws an item.
	 *
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to deposit.
	 */
	public static void withdraw(Player player, int slot, int id, int amount) {
		Item item = player.getSummoning().getFamiliarInventory().get(slot);

		if (item == null || item.getId() != id) {
			return;
		}

		Container familiarInv = player.getSummoning().getFamiliarInventory();
		int withdrawAmount = item.getDefinition().isStackable() ? familiarInv.getCount(item.getId(), slot) : familiarInv.getCount(item.getId());

		if (withdrawAmount > amount) {
			withdrawAmount = amount;
		}

		if (item.getDefinition().isNoted() || item.getDefinition().isStackable()) {
			if (player.getInventory().freeSlots() <= 0 && player.getInventory().getById(item.getId()) == null) {
				player.sendMessage("Your don't have enough space to store this item.");
				return;
			}

			int addSlot = player.getInventory().getSlotById(item.getId());
			Item stack = player.getInventory().get(addSlot);
			int newAmount = stack != null ? withdrawAmount + stack.getCount() : withdrawAmount;

			if (addSlot < 0) {
				addSlot = player.getInventory().getNextFreeSlot();
			}

			player.getSummoning().getFamiliarInventory().remove(new Item(item.getId(), withdrawAmount));
			player.getInventory().set(addSlot, new Item(item.getId(), newAmount));
		} else {
			if (player.getInventory().freeSlots() < 1) {
				player.sendMessage("Your don't have enough space to store this item.");
				return;
			}

			if (withdrawAmount > player.getInventory().freeSlots()) {
				player.sendMessage("You don't have enough inventory space to withdraw that many.");
				withdrawAmount = player.getInventory().freeSlots();
			}

			player.getSummoning().getFamiliarInventory().remove(new Item(item.getId(), withdrawAmount));
			player.getInventory().add(new Item(item.getId(), withdrawAmount));
		}
	}

	public static void withdrawAll(Player player) {
		if (player.getSummoning().getFamiliarInventory() == null) {
			return;
		}

		for (Item item : player.getSummoning().getFamiliarInventory().toArray()) {
			if (item == null) {
				continue;
			}

			if (item.getDefinition().isNoted() || item.getDefinition().isStackable()) {
				if (player.getInventory().freeSlots() <= 0 && player.getInventory().getById(item.getId()) == null) {
					player.sendMessage("You don't have enough space to withdraw that many.");
					continue;
				}

				int addSlot = player.getInventory().getSlotById(item.getId());
				int newAmount = addSlot > 0 ? item.getCount() + player.getInventory().get(addSlot).getCount() : item.getCount();

				if (addSlot <= 0) {
					addSlot = player.getInventory().getNextFreeSlot();
				}

				player.getSummoning().getFamiliarInventory().remove(new Item(item.getId(), newAmount));
				player.getInventory().set(addSlot, new Item(item.getId(), newAmount));
			} else {
				int withdrawAmount = item.getCount();

				if (player.getInventory().freeSlots() <= 0) {
					player.sendMessage("You don't have enough inventory space to withdraw that many.");
					return;
				}

				if (withdrawAmount > player.getInventory().freeSlots()) {
					player.sendMessage("You don't have enough inventory space to withdraw that many.");
					withdrawAmount = player.getInventory().freeSlots();
				}

				player.getSummoning().getFamiliarInventory().remove(new Item(item.getId(), withdrawAmount));
				player.getInventory().add(new Item(item.getId(), withdrawAmount));
			}
		}
	}

	public static final int INTERFACE_ID = 2700;

}
