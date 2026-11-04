package com.koios.game.model.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.koios.GameService;
import com.koios.game.content.minigames.duel.DuelArenaHandler;
import com.koios.game.content.skill.summoning.SummoningData.PouchData;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.actions.CreatePouch;
import com.koios.game.content.skill.summoning.actions.CreateScroll;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.ContainerListener;
import com.koios.game.model.item.container.FamiliarInventory;
import com.koios.game.model.item.container.Stake;
import com.koios.game.model.item.container.Trade;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.bank.Bank.BankType;
import com.koios.network.out.impl.SendEnterXInterfacePacket;
import com.koios.network.out.impl.SendInterfacePacket;
import com.koios.network.out.impl.SendStringInputInterface;

/**
 * Manage the player's interfaces
 *
 * @author Gabriel | Wolfs Darker
 */
public class InterfaceState {

	private final Player player;

	private int currentInterface;

	private Consumer<String> nameInputConsumer = null;

	/**
	 * The active enter amount interface.
	 */
	private int valueInputInterface = -1;

	/**
	 * The active enter amount id.
	 */
	private int valueInputItemId;

	/**
	 * The active enter amount slot.
	 */
	private int valueInputItemSlot;

	/**
	 * A list of container listeners used on interfaces that have containers.
	 */
	private final List<ContainerListener> containerListeners = new ArrayList<>();

	public InterfaceState(Player player) {
		this.player = Objects.requireNonNull(player);
		currentInterface = -1;
	}

	/**
	 * Adds a listener to an interface that is closed when the inventory is closed.
	 *
	 * @param container The container.
	 * @param containerListener The listener.
	 */
	public void addListener(Container container, ContainerListener containerListener) {
		container.addListener(containerListener);
		containerListeners.add(containerListener);
	}

	/**
	 * Close all interfaces for the player
	 */
	public boolean closeInterfaces() {

		if (currentInterface <= 0) {
			return false;
		}

		currentInterface = -1;
		valueInputInterface = -1;
		valueInputItemSlot = -1;
		valueInputItemId = -1;

		for (ContainerListener listener : containerListeners) {
			player.getInventory().removeListener(listener);
			player.getEquipment().removeListener(listener);
			player.getBank().removeListener(listener);
		}

		player.dialogue().interrupt();
		player.dialogueAction = 0;
		player.nextChat = 0;

		player.awaitResponseDestroyItemId = 0;
		player.awaitResponseDestroyItemSlot = -1;

		player.storing = false;
		player.partyChestOpen = false;

		player.getBankField().setBanking(false);

		Trade.exitOutOfTrade(player, false);

		if (player.getDuel().isDueling()) {
			DuelArenaHandler.declineDuel(player, true);
		}

		if (player.currentShop > 0) {
			GameService.getShopManager().close(player);
		}

		currentInterface = -1;
		nameInputConsumer = null;
		return true;
	}

	/**
	 * Get the player's current interface
	 *
	 * @return the interface
	 */
	public int getCurrentInterface() {
		return currentInterface;
	}

	public boolean hasNameInputConsumer() {
		return nameInputConsumer != null;
	}

	public boolean hasValueInputConsumer() {
		return valueInputInterface > 0;
	}

	/**
	 * Receive the string input information
	 *
	 * @param name the information
	 */
	public void nameInputReceived(String name) {
		if (nameInputConsumer == null) {
			closeInterfaces();
			return;
		}

		nameInputConsumer.accept(name);
		nameInputConsumer = null;
	}

	/**
	 * Open a interface for the player
	 */
	public boolean openInterface(int id) {
		if (id <= 0) {
			return false;
		}

		if (player.isTrading()) {
			Trade.exitOutOfTrade(player, false);
		}

		if (player.getDuel().isDueling() && currentInterface == 6575 && id != 6412) {
			DuelArenaHandler.declineDuel(player, true);
		}

		if (currentInterface > 0 && currentInterface != id) {
			player.sendMessage("You are currently busy, please close your current interface.");
			return false;
		}

		currentInterface = id;
		player.getSession().send(new SendInterfacePacket(id));
		return true;
	}

	/**
	 * Send the string input interface
	 *
	 * @param consumer The input consumer.
	 */
	public void openNameInput(Consumer<String> consumer) {
		if (nameInputConsumer != null) {
			closeInterfaces();
			return;
		}

		setNameInputConsumer(consumer);
		player.getSession().send(new SendStringInputInterface());
	}

	/**
	 * Called to loot the enter amount interface ( Actions without containers )
	 */
	public void openValueInput() {
		valueInputInterface = 1;
		player.getSession().send(new SendEnterXInterfacePacket());
	}

	/**
	 * Called to loot the enter amount interface.
	 *
	 * @param interfaceId The interface id.
	 * @param slot The slot.
	 * @param id The id.
	 */
	public void openValueInput(int interfaceId, int slot, int id) {
		valueInputInterface = interfaceId;
		valueInputItemSlot = slot;
		valueInputItemId = id;
		player.getSession().send(new SendEnterXInterfacePacket());
	}

	/**
	 * Sets the current interface ID
	 */
	public void setCurrentInterface(int i) {
		currentInterface = i;
	}

	public void setNameInputConsumer(Consumer<String> nameInputConsumer) {
		this.nameInputConsumer = Objects.requireNonNull(nameInputConsumer);
	}

	/**
	 * Receive the interger input information
	 *
	 * @param value the information
	 */
	public void valueInputReceived(int value) {
		if (valueInputInterface == 1 && player.getCurrentSkill() != null) {
			player.getCurrentSkill().setAmount(value);
			player.getCurrentSkill().start();
			closeInterfaces();
			return;
		}

		if (valueInputInterface <= 0) {
			closeInterfaces();
			return;
		}

		switch (valueInputInterface) {
		case Trade.PLAYER_INVENTORY_INTERFACE:
			if (player.isTrading()) {
				Trade.addItemToOffer(player, valueInputItemId, valueInputItemSlot, value);
			} else {
				Stake.stakeItem(player, valueInputItemId, value, valueInputItemSlot);
			}
			break;

		case Trade.TRADE_INTERFACE:
			Trade.removeItemFromOffer(player, valueInputItemId, valueInputItemSlot, value);
			break;

		case 52700:
			CreatePouch.hasRequirements(player, valueInputItemSlot, true, value, PouchData.get(valueInputItemId));
			break;

		case 51700:
			CreateScroll.hasRequirements(player, valueInputItemSlot, true, value, ScrollsData.get(valueInputItemId));
			break;

		case 5064:
			if (currentInterface == 2700) {
				FamiliarInventory.deposit(player, valueInputItemSlot, valueInputItemId, value);
			} else if (currentInterface == 5292) {
				if (ItemDefinition.forId(valueInputItemId) != null && (ItemDefinition.forId(valueInputItemId).isStackable() || ItemDefinition.forId(valueInputItemId).isNoted())) {
					Bank.deposit(player, valueInputItemSlot, valueInputItemId, value, true);
				} else {
					Item[] items = player.getInventory().toArray();

					for (int index = 0; index < items.length && value > 0; index++) {
						Item item = items[index];

						if (item == null) {
							continue;
						}

						if (item.getId() == valueInputItemId) {
							Bank.deposit(player, index, valueInputItemId, 1, BankType.INVENTORY, false);
							value--;
						}
					}

					player.getBank().fireItemChanged(player.getBank().getSlotById(valueInputItemId));
					player.getInventory().fireItemsChanged();
				}
			}
			break;

		case Bank.BANK_INVENTORY_INTERFACE:
		case Bank.BANK_INVENTORY_INTERFACE + 1:
		case Bank.BANK_INVENTORY_INTERFACE + 2:
		case Bank.BANK_INVENTORY_INTERFACE + 3:
		case Bank.BANK_INVENTORY_INTERFACE + 4:
		case Bank.BANK_INVENTORY_INTERFACE + 5:
		case Bank.BANK_INVENTORY_INTERFACE + 6:
		case Bank.BANK_INVENTORY_INTERFACE + 7:
		case Bank.BANK_INVENTORY_INTERFACE + 8:
			Bank.withdraw(player, valueInputItemId, value);
			break;

		case 2700:
			FamiliarInventory.withdraw(player, valueInputItemId, valueInputItemSlot, value);
			break;

		case 6669:
			Stake.removeItem(player, valueInputItemId, value);
			break;
		}
	}

}
