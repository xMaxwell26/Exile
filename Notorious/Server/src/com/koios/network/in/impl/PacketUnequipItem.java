package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.minigames.duel.DuelArenaConstants;
import com.koios.game.content.partyroom.PartyRoomManager;
import com.koios.game.content.skill.smithing.SmithingAction;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.FamiliarInventory;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.Stake;
import com.koios.game.model.item.container.Trade;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Remove Item
 */
public class PacketUnequipItem implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		int interfaceId = packet.readUnsignedShortA();
		int removeSlot = packet.readShortA();
		int removeId = packet.readShortA();

		if (!player.hasFocus()) {
			return;
		}

		switch (interfaceId) {
		case 1688:
			if (removeSlot >= 0 && removeSlot < Equipment.SIZE) {
				if (!Container.transfer(player.getEquipment(), player.getInventory(), removeSlot, removeId)) {
					player.sendMessage("You do not have enough inventory space.");
				}
			}
			break;

		case 2700:
			if (removeSlot >= 0 && removeSlot < player.getSummoning().getFamiliarInventory().getCapacity()) {
				FamiliarInventory.withdraw(player, removeSlot, removeId, 1);
			}
			break;
		case 2274:
			if (player.partyChestOpen) {
				PartyRoomManager.unqueueItem(player, new Item(removeId));
				return;
			}
			break;
		case 5064:
			if (!player.getInventory().contains(removeId) || removeSlot < 0 && removeSlot > Inventory.SIZE) {
				return;
			}
			if (player.partyChestOpen) {
				PartyRoomManager.queueItem(player, new Item(removeId));
				return;
			}

			if (player.getInterfaceState().getCurrentInterface() == 2700) {
				FamiliarInventory.deposit(player, removeSlot, removeId, 1);
			} else if (player.getInterfaceState().getCurrentInterface() == 5292) {
				Bank.deposit(player, removeSlot, removeId, 1, true);
			}
			break;

		case 3322:
			if (player.isTrading()) {
				if (removeSlot >= 0 && removeSlot < Inventory.SIZE) {
					Trade.addItemToOffer(player, removeId, removeSlot, 1);
				}
			} else if (player.getDuel().isDueling()) {
				if (removeSlot >= 0 && removeSlot < Inventory.SIZE && player.getInventory().get(removeSlot) != null) {
					Stake.stakeItem(player, player.getInventory().get(removeSlot).getId(), 1, removeSlot);
				}
			}
			break;

		case 38000:
			if (player.getDuel().getStakeInventory().get(removeSlot) == null) {
				return;
			}

			if (removeSlot >= 0 && removeSlot < DuelArenaConstants.STAKE_SIZE) {
				Stake.removeItem(player, player.getDuel().getStakeInventory().get(removeSlot).getId(), 1);
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
			Bank.withdrawSelected(player,removeId,1,removeSlot,interfaceId);
			break;

		case 3900:
			GameService.getShopManager().checkSellPrice(player, removeId, removeSlot);
			break;
		case 3823:
			GameService.getShopManager().checkBuyPrice(player, removeId, removeSlot);
			break;

		case Trade.TRADE_INTERFACE:
			if (removeSlot >= 0 && removeSlot < Inventory.SIZE) {
				Trade.removeItemFromOffer(player, removeId, removeSlot, 1);
			}
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			SmithingAction.readInput(player, removeId, 1);
			break;
		}
	}

}
