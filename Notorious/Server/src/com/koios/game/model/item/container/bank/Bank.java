package com.koios.game.model.item.container.bank;

import java.util.Arrays;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.InterfaceContainerListener;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendInventoryOverlayPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.ToggleWidgetLayer;

/**
 * Banking utility class.
 *
 * @author Graham Edgecombe (basic withdraw and deposit)
 * @author Michael | Chex (bank tab operations)
 */
public class Bank {

	/**
	 * The bank inventory interface.
	 */
	public static final int BANK_INVENTORY_INTERFACE = 50088;

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 5064;

	/**
	 * Describes the type of banking your doing
	 *
	 * @author Timothy Howard <Mobster>
	 */
	public enum BankType {
		/**
		 * Depositing items from the players inventory
		 */
		INVENTORY,

		/**
		 * Depositing items from the players equipment
		 */
		EQUIPMENT,

		/**
		 * Depositing items from the players beast of burden
		 */
		BOB
	}

	/**
	 * The bank size.
	 */
	public static final int SIZE = 350;

	public static boolean addToBank(Player player, Item item) {
		if (item == null) {
			return false;
		}

		if (item.getId() == 6183) {
			player.sendMessage("You can't bank this item.");
			return false;
		}

		boolean contains = !item.hasRoll() && player.getBank().getSlotById(item) != -1;
		int here = item instanceof BankItem ? ((BankItem) item).getTabIndex() : 0;
		int toTab = contains ? ((BankItem) player.getBank().get(player.getBank().getSlotById(item))).getTabIndex() : here;

		if (!player.getBank().add(new BankItem(toTab, item))) {
			return false;
		}

		if (!contains) {
			player.getBankField().getTabAmounts()[toTab]++;
		}

		return true;
	}

	public static boolean bankButton(Player player, int buttonId) {
		if (buttonId >= 195114 && buttonId <= 195130) {
			int tab = (buttonId - 195114) / 2;
			viewTab(player, tab);
			return true;
		}

		if (buttonId >= 196060 && buttonId <= 196074) {
			int tab = (buttonId - 196060) / 2 + 1;
			collapse(player, tab, 0);
			player.getBank().fireItemsChanged();
			viewTab(player, 0);
			return true;
		}

		switch (buttonId) {
		case 195087:
			player.getBankField().setSearching(!player.getBankField().isSearching());
			return true;

		case 195178: // Toggle bank.
			player.getSession().send(new SendRemoveInterfacePacket());
			open(player);
			player.getSession().send(new ToggleWidgetLayer(false, 50097));
			return true;

		case 195110: // Toggle equipment (from bank).
			player.getSession().send(new ToggleWidgetLayer(true, 50097));
			player.getSession().send(new SendRemoveInterfacePacket());
			player.getInterfaceState().openInterface(15106);
			return true;

		case 59097: // Open equipment.
			player.getSession().send(new ToggleWidgetLayer(false, 50097));
			player.getSession().send(new SendRemoveInterfacePacket());
			player.getInterfaceState().openInterface(15106);
			return true;

		case 195093: // Toggle withdraw as note.
			player.getBankField().setWithdrawAsNote(!player.getBankField().isWithdrawAsNote());
			player.getSession().send(new SendConfigPacket(1011, player.getBankField().isWithdrawAsNote() ? 0 : 1));
			return true;

		case 195084: // Toggle inserting or swapping.
			player.getBankField().setInserting(!player.getBankField().isInserting());
			player.getSession().send(new SendConfigPacket(1009, player.getBankField().isInserting() ? 0 : 1));
			return true;

		case 195101: // Deposit inventory.
			depositInventory(player);
			return true;

		case 195104: // Deposit worn equipment.
			depositEquipment(player);
			return true;

		case 195107: // Deposit BoB.
			depositBoB(player);
			// player.sendMessage("<col=369>Deposit BoB items to be
			// implemented...");
			return true;
		}

		return false;
	}

	private static void depositBoB(Player player) {

		if (player.getSummoning().getFamiliar() == null) {
			return;
		}

		if (player.getSummoning().getFamiliarInventory() == null) {
			return;
		}

		Item[] items = player.getSummoning().getFamiliarInventory().toArray();

		for (int slot = 0; slot < items.length; slot++) {
			Item item = player.getSummoning().getFamiliarInventory().get(slot);
			if (item == null) {
				continue;
			}

			deposit(player, slot, item.getId(), item.getCount(), BankType.BOB, false);
		}

		player.getBank().fireItemsChanged();
		player.getSummoning().getFamiliarInventory().fireItemsChanged();
	}

	/**
	 * Collapses a tab and disables the tab button sprite.
	 *
	 * @param tab The tab to collapse.
	 * @param toTab The tab to transfer the collapsed items to.
	 */
	public static void collapse(Player player, int tab, int toTab) {
		if (tab <= 0 || toTab == tab || tab >= 9) {
			return;
		}

		int itemSlot = player.getBankField().getOffset(tab);
		int initialTabAmount = player.getBankField().getTabAmounts()[tab];

		for (int fromSlot = 0; fromSlot < initialTabAmount; fromSlot++) {
			if (player.getBank().get(itemSlot) != null) {
				toTab(player, tab, toTab, 0);
			}
		}

		if (tab != player.getBankField().getUsedTabs()) {
			collapse(player, tab + 1, tab);
		}
	}

	/**
	 * Deposits an item.
	 *
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to deposit.
	 */
	public static void deposit(Player player, int slot, int id, int amount, boolean inventory) {
		deposit(player, slot, id, amount, BankType.INVENTORY, true);
	}

	/**
	 * Deposits an item.
	 *
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to deposit.
	 */
	public static void deposit(Player player, int slot, int id, int amount, BankType type, boolean refresh) {
		if (!player.getBankField().isBanking()) {
			return;
		}
		if (player.getBankField().isSearching()) {
			viewTab(player, 0);
		}

		if (type == BankType.BOB && (player.getSummoning().getFamiliar() == null || player.getSummoning().getFamiliarInventory() == null)) {
			return;
		}

		boolean inventoryFiringEvents = type == BankType.INVENTORY ? player.getInventory().isFiringEvents()
				: type == BankType.EQUIPMENT ? player.getEquipment().isFiringEvents() : player.getSummoning().getFamiliarInventory().isFiringEvents();
		boolean bankFiringEvents = player.getBank().isFiringEvents();

		if (type == BankType.INVENTORY) {
			player.getInventory().setFiringEvents(false);
		} else if (type == BankType.EQUIPMENT) {
			player.getEquipment().setFiringEvents(false);
		} else if (type == BankType.BOB) {
			player.getSummoning().getFamiliarInventory().setFiringEvents(false);
		}

		player.getBank().setFiringEvents(refresh);

		try {
			Item item = type == BankType.INVENTORY ? player.getInventory().get(slot)
					: type == BankType.EQUIPMENT ? player.getEquipment().get(slot) : type == BankType.BOB ? player.getSummoning().getFamiliarInventory().get(slot) : null;

			if (item == null) {
				return;
			}

			if (item.getId() != id) {
				return; // invalid packet, or client out of sync
			}

			if (item.getDefinition() == null) {
				player.sendMessage("Some items were unable to deposit.");
				return;
			}

			int transferAmount = item.getDefinition().isStackable()
					? type == BankType.INVENTORY ? player.getInventory().getCount(item, slot)
							: type == BankType.EQUIPMENT ? player.getEquipment().getCount(item, slot) : player.getSummoning().getFamiliarInventory().getCount(item, slot)
					: type == BankType.INVENTORY ? player.getInventory().getCount(item.getId())
							: type == BankType.EQUIPMENT ? player.getEquipment().getCount(item.getId()) : player.getSummoning().getFamiliarInventory().getCount(item.getId());

			if (transferAmount > amount) {
				transferAmount = amount;
			}

			if (transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if (!item.hasRoll() && (item.getDefinition().isStackable() || item.getDefinition().isNoted())) {
				int bankedId = item.getDefinition().isNoted() ? item.getDefinition().getNoteId() : item.getId();

				if (player.getBank().freeSlots() < 1 && player.getBank().getById(bankedId) == null) {
					player.sendMessage("You don't have enough space in your bank account.");
				}

				// we only need to remove from one stack
				int newInventoryAmount = item.getCount() - transferAmount;
				Item newItem;

				if (newInventoryAmount <= 0) {
					newItem = null;
				} else {
					newItem = new Item(item.getId(), newInventoryAmount);
				}

				boolean contains = player.getBank().getSlotById(new Item(bankedId)) != -1;
				int toTab = contains ? ((BankItem) player.getBank().get(player.getBank().getSlotById(new Item(bankedId)))).getTabIndex() : player.getBankField().getTabIndex();
				BankItem toAdd = new BankItem(toTab, bankedId, transferAmount);

				player.getBank().setFiringEvents(false);

				if (!player.getBank().add(toAdd)) {
					player.sendMessage("You don't have enough space in your bank account.");
				} else {
					if (type == BankType.INVENTORY) {
						player.getInventory().set(slot, newItem);

						if (refresh) {
							player.getInventory().fireItemsChanged();
						}
					} else if (type == BankType.EQUIPMENT) {
						player.getEquipment().set(slot, newItem);

						if (refresh) {
							player.getEquipment().fireItemsChanged();
						}
					} else if (type == BankType.BOB) {
						player.getSummoning().getFamiliarInventory().set(slot, newItem);
						if (refresh) {
							player.getSummoning().getFamiliarInventory().fireItemsChanged();
						}
					}

					if (!contains) {
						if (player.getBankField().getUsedTabs() > 1) {
							int to = player.getBankField().getOffset(toTab) + player.getBankField().getTabAmounts()[toTab];
							int from = player.getBank().size() - 1;
							((BankItem) player.getBank().get(from)).setTabSlot(toTab);
							insert(player, from, to);
						}

						player.getBankField().getTabAmounts()[toTab]++;
					}

					player.getBank().fireItemChanged(player.getBank().getSlotById(bankedId));
				}

				player.getBank().setFiringEvents(refresh);
			} else {
				if (player.getBank().freeSlots() < transferAmount) {
					player.sendMessage("You don't have enough space in your bank account.");
				}

				if (item.hasRoll()) transferAmount=1;
                item.setCount(transferAmount);

				boolean contains = !item.hasRoll() && player.getBank().getSlotById(item) != -1;
				int itemTab = contains ? ((BankItem) player.getBank().get(player.getBank().getSlotById(item))).getTabIndex() : player.getBankField().getTabIndex();
				BankItem toAdd = new BankItem(itemTab, item);
				boolean bankRefresh = player.getBank().isFiringEvents();
				player.getBank().setFiringEvents(false);

				if (!player.getBank().add(toAdd)) {
					player.sendMessage("You don't have enough space in your bank account.");
				} else {
					for (int i = 0; i < transferAmount; i++) {
						if (type == BankType.INVENTORY) {
							player.getInventory().set(slot, null);

							if (refresh) {
								player.getInventory().fireItemsChanged();
							}
						} else if (type == BankType.EQUIPMENT) {
							player.getEquipment().set(slot, null);

							if (refresh) {
								player.getEquipment().fireItemsChanged();
							}
						} else if (type == BankType.BOB) {
							player.getSummoning().getFamiliarInventory().set(slot, null);
							if (refresh) {
								player.getSummoning().getFamiliarInventory().fireItemsChanged();
							}
						}
					}

					int toTab = player.getBankField().getTabIndex();
					int to = player.getBankField().getOffset(toTab) + player.getBankField().getTabAmounts()[toTab];

					if (!contains) {
						if (player.getBankField().getUsedTabs() > 1) {
							int from = player.getBank().size() - 1;
							((BankItem) player.getBank().get(from)).setTabSlot(toTab);
							insert(player, from, to);
						}

						player.getBankField().getTabAmounts()[toTab]++;
					} else {
						to = player.getBank().getSlotById(item.getId());
					}

					if (refresh) {
						player.getBank().fireItemChanged(to);
					}
				}

				player.getBank().setFiringEvents(bankRefresh);
			}
		} finally {
			if (type == BankType.INVENTORY) {
				player.getInventory().setFiringEvents(inventoryFiringEvents);
			} else if (type == BankType.EQUIPMENT) {
				player.getEquipment().setFiringEvents(inventoryFiringEvents);
			} else if (type == BankType.BOB) {
				player.getSummoning().getFamiliarInventory().setFiringEvents(inventoryFiringEvents);
			}

			player.getBank().setFiringEvents(bankFiringEvents);
		}
	}

	public static void depositEquipment(Player player) {
		Item[] items = player.getEquipment().toArray();

		for (int slot = 0; slot < items.length; slot++) {
			Item item = player.getEquipment().get(slot);

			if (item == null) {
				continue;
			}

			deposit(player, slot, item.getId(), item.getCount(), BankType.EQUIPMENT, false);
		}

		player.getBank().fireItemsChanged();
		player.getEquipment().fireItemsChanged();
	}

	public static void depositInventory(Player player) {
		Item[] items = player.getInventory().toArray();

		for (int slot = 0; slot < items.length; slot++) {
			Item item = player.getInventory().get(slot);

			if (item == null) {
				continue;
			}

			deposit(player, slot, item.getId(), item.getCount(), BankType.INVENTORY, false);
		}

		player.getBank().fireItemsChanged();
		player.getInventory().fireItemsChanged();
	}

	/**
	 * Initializes bank sprite configuration. Mainly for enabling or disabling tab buttons, and setting main tab to the viewed tab.
	 *
	 * @param player The player initializing the bank configurations.
	 */
	public static void initializeBankConfigs(Player player) {
		player.getBankField().setTabIndex(0);

		if (player.getBankField().isLoadError()) {
			Bank.sortItems(player);
		}

		// Disable noting, inserting, and searching.
		player.getSession().send(new SendConfigPacket(1011, 1));
		player.getSession().send(new SendConfigPacket(1009, 1));
		player.getSession().send(new SendConfigPacket(1010, 0));
		player.getBank().fireItemsChanged();
	}

	public static void insert(Player player, int currentSlot, int destinationSlot) {
		int index = currentSlot;

		if (currentSlot > destinationSlot) {
			while (index != destinationSlot) {
				swap(player, index, --index);
			}
		} else if (currentSlot < destinationSlot) {
			while (index != destinationSlot) {
				swap(player, index, ++index);
			}
		}
	}

	public static void moveToTab(Player player, int slot, int fromTab, int toTab) {
		boolean bankFiringEvents = player.getBank().isFiringEvents();
		player.getBank().setFiringEvents(false);

		if (toTab(player, fromTab, toTab, slot)) {
			if (player.getBankField().getTabAmounts()[fromTab] <= 0) {
				collapse(player, fromTab + 1, fromTab);
				viewTab(player, 0);
			}

			player.getBank().shift();
			player.getBank().fireItemsChanged();
		}

		player.getBank().setFiringEvents(bankFiringEvents);
	}

	/**
	 * Opens the bank for the specified player.
	 *
	 * @param player The player to loot the bank for.
	 */
	public static void open(Player player) {
		player.getSession().send(new SendInventoryOverlayPacket(5292, PLAYER_INVENTORY_INTERFACE - 1));
		player.getInterfaceState().addListener(player.getBank(), new BankContainerListener(player));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, PLAYER_INVENTORY_INTERFACE));
		player.getBank().shift();
		player.getBankField().setBanking(true);
	}

	public static void sortItems(Player player) {
		Item[] bankList = player.getBank().toArray();
		BankItem[] bankItems = new BankItem[player.getBank().size()];

		for (int i = 0, c = 0; i < bankList.length; i++) {
			if (bankList[i] != null) {
				bankItems[c++] = (BankItem) bankList[i];
			}
		}

		Arrays.sort(bankItems, (first, second) -> first.getTabIndex() - second.getTabIndex());
		player.getBank().setItems(Arrays.copyOf(bankItems, SIZE));
	}

	public static void swap(Player player, int currentSlot, int destinationSlot) {
		BankItem current = (BankItem) player.getBank().get(currentSlot);
		BankItem destination = (BankItem) player.getBank().get(destinationSlot);
		player.getBank().toArray()[destinationSlot] = current;
		player.getBank().toArray()[currentSlot] = destination;
	}

	public static void swapTabs(Player player, int currentSlot, int destinationSlot) {
		BankItem current = ((BankItem) player.getBank().get(currentSlot)).copy();
		BankItem destination = ((BankItem) player.getBank().get(destinationSlot)).copy();
		int temp = destination.getTabIndex();
		destination.setTabSlot(current.getTabIndex());
		current.setTabSlot(temp);
		player.getBank().toArray()[destinationSlot] = current;
		player.getBank().toArray()[currentSlot] = destination;
	}

	/**
	 * Moves an item from it's current bank slot to the end of the tab desired.
	 *
	 * @param player The player to retrieve their bank.
	 * @param fromTab The original tab.
	 * @param toTab The desired tab.
	 * @param slot The item's slot in the bank (no offset for tabs).
	 */
	public static boolean toTab(Player player, int fromTab, int toTab, int slot) {
		if (toTab == fromTab) {
			return false;
		}

		if (toTab > player.getBankField().getUsedTabs()) {
			return false;
		}

		int from = slot + player.getBankField().getOffset(fromTab);
		int to = player.getBankField().getOffset(toTab) + player.getBankField().getTabAmounts()[toTab] - (fromTab > toTab ? 0 : 1);

		if (player.getBank().get(from) == null) {
			return false;
		}

		((BankItem) player.getBank().get(from)).setTabSlot(toTab);
		insert(player, from, to);
		player.getBankField().getTabAmounts()[toTab]++;
		player.getBankField().getTabAmounts()[fromTab]--;
		return true;
	}

	/**
	 * View a bank tab.
	 *
	 * @param player The player viewing the tab.
	 * @param tab The tab index.
	 */
	public static void viewTab(Player player, int tab) {
		if (player.getBankField().isSearching()) {
			player.getBankField().setSearching(false);
		}

		if (tab == player.getBankField().getTabIndex()) {
			return;
		}

		if (player.getBankField().getTabAmounts()[tab] <= 0 && tab > 0) {
			player.sendMessage("Drag an item here to create a new tab.");
			return;
		}

		player.getBankField().setTabIndex(tab);
	}

	/**
	 * Withdraws an item.
	 *
	 * @param player The player.
	 * @param id The item id.
	 * @param amount The amount of the item to withdraw.
	 */
	public static void withdraw(Player player, int id, int amount) {
  int slot=player.getBank().getSlotById(id);
  for(Item i:player.getBank().toArray())if(i!=null && i.getId()==id && i.hasRoll()){
   player.sendMessage("Use Withdraw-1, 5, 10 or All on the selected rolled item.");return;
  }
  withdrawSlot(player,id,amount,slot);
 }
 public static void withdrawSelected(Player player,int id,int amount,int uiSlot,int interfaceId){
  int slot=-1;
  if(player.getBankField().isSearching()){
   String keyword=player.getBankField().getSearchText();int found=0;
   for(int i=0;i<player.getBank().getCapacity();i++){
    Item item=player.getBank().get(i);
    if(item!=null && item.getDefinition().getName().replace('_',' ').toLowerCase().contains(keyword==null?"":keyword.trim().toLowerCase())){
     if(found++==uiSlot){slot=i;break;}
    }
   }
  }else{
   int tab=interfaceId-BANK_INVENTORY_INTERFACE;
   if(tab<0 || tab>=9 || uiSlot<0 || uiSlot>=player.getBankField().getTabAmounts()[tab])return;
   slot=player.getBankField().getOffset(tab)+uiSlot;
  }
  withdrawSlot(player,id,amount,slot);
 }
 private static void withdrawSlot(Player player,int id,int amount,int slot) {
		if (!player.getBankField().isBanking()) {
			return;
		}

		if(amount<1)return;
		BankItem bankItem = (BankItem) player.getBank().get(slot);

		if (bankItem == null || bankItem.getId() != id) {
			return; // invalid packet, or client out of sync
		}

		int tab = bankItem.getTabIndex();
		int transferAmount = bankItem.getCount();

		if (transferAmount >= amount) {
			transferAmount = amount;
		} else if (transferAmount == 0) {
			return; // invalid packet, or client out of sync
		}

		int newId = bankItem.getId();

		if (!bankItem.hasRoll() && player.getBankField().isWithdrawAsNote()) {
			int noteId = bankItem.getDefinition().getNoteId();

			if (noteId != -1) {
				newId = noteId;
			} else {
				player.sendMessage("This item cannot be witdrawn as a note.");
			}
		}

		ItemDefinition def = ItemDefinition.forId(newId);

		if (def.isStackable()) {
			if (player.getInventory().freeSlots() <= 0 && player.getInventory().getById(newId) == null) {
				player.sendMessage("You don't have enough inventory space to withdraw that many.");
			}
		} else {
			int free = player.getInventory().freeSlots();

			if (transferAmount > free) {
				player.sendMessage("You don't have enough inventory space to withdraw that many.");
				transferAmount = free;
			}
		}

		// now add it to inv
		Item withdrawn=new Item(newId,transferAmount);
        withdrawn.setRoll(bankItem.getRoll());
        if (player.getInventory().add(withdrawn)) {
			// all items in the bank are stacked, makes it very easy!
			int newAmount = bankItem.getCount() - transferAmount;

			if (newAmount <= 0) {
				player.getBankField().getTabAmounts()[tab]--;
				player.getBank().set(slot, null);
				player.getBank().shift();

				if (player.getBankField().getTabAmounts()[tab] <= 0) {
					Bank.collapse(player, tab + 1, tab);
					player.getBank().fireItemsChanged();
					Bank.viewTab(player, 0);
				}
			} else {
				bankItem.setCount(newAmount);
				player.getBank().set(slot, bankItem);
				player.getBank().fireItemChanged(slot);
			}
		} else {
			player.sendMessage("You don't have enough inventory space to withdraw that many.");
		}
	}

}
