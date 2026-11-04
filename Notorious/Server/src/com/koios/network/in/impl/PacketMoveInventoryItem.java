package com.koios.network.in.impl;

import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 *
 */
public class PacketMoveInventoryItem implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int interfaceId = packet.readLEShortA() & 0xFFFF;
		final int fromTab = (packet.readLEShortA() & 0xFFFF) - Bank.BANK_INVENTORY_INTERFACE;
		final int currentSlot = packet.readLEShortA() & 0xFFFF;
		final int destinationSlot = packet.readLEShort() & 0xFFFF;

		if (!player.hasFocus()) {
			return;
		}

		if (!player.getBankField().isBanking()) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		if (interfaceId >= Bank.BANK_INVENTORY_INTERFACE && interfaceId <= Bank.BANK_INVENTORY_INTERFACE + 8) {
			if (player.getBankField().isSearching()) {
				return;
			}

			if (fromTab > 8 || fromTab < 0) {
				return;
			}

			int tab = interfaceId - Bank.BANK_INVENTORY_INTERFACE;
			int currentOffset = player.getBankField().getOffset(fromTab);
			int destinationOffset = player.getBankField().getOffset(tab);

			// Check if we are dragging items between tabs on main tab screen.
			if (tab != fromTab) {
				// Check if the item is being dragged to the end of another tab.
				if (destinationSlot >= player.getBankField().getTabAmounts()[tab]) {
					Bank.moveToTab(player, currentSlot, fromTab, tab);
					return;
				}

				// If not the statement above, must be a swap/insert operation.
				if (player.getBankField().isInserting()) {
					player.sendMessage("<col=369>Inserting function to be implemented...");
				} else {
					Bank.swapTabs(player, currentSlot + currentOffset, destinationSlot + destinationOffset);
				}
				return;
			}

			if (currentSlot == destinationSlot) {
				return;
			}

			if (destinationSlot >= player.getBankField().getTabAmounts()[tab] || currentSlot >= player.getBankField().getTabAmounts()[tab]) {
				return;
			}

			boolean bankFiringEvents = player.getBank().isFiringEvents();
			player.getBank().setFiringEvents(false);

			if (player.getBankField().isInserting()) {
				Bank.insert(player, currentSlot + currentOffset, destinationSlot + destinationOffset);
			} else {
				Bank.swap(player, currentSlot + currentOffset, destinationSlot + destinationOffset);
			}

			player.getBank().setFiringEvents(bankFiringEvents);
			player.getBank().shift();
			return;
		}

		if (interfaceId >= 50051 && interfaceId <= 50059) {

			int toTab = interfaceId - 50051;

			if (player.getBankField().isSearching()) {
				return;
			}

			Bank.moveToTab(player, currentSlot, fromTab, toTab);
			return;
		}

		if (interfaceId == Inventory.INTERFACE || interfaceId == 5064) {
			if (currentSlot < 28 && destinationSlot < 28) {
				Item dest = player.getInventory().get(destinationSlot);
				player.getInventory().set(destinationSlot, player.getInventory().get(currentSlot));
				player.getInventory().set(currentSlot, dest);
			}
		}
	}

}
