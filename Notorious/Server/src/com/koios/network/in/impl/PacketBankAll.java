package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.minigames.duel.DuelArenaConstants;
import com.koios.game.content.partyroom.PartyRoomManager;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.FamiliarInventory;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.Stake;
import com.koios.game.model.item.container.Trade;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.bank.Bank.BankType;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Bank All Items
 */
public class PacketBankAll implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int slot = packet.readUnsignedShortA();
		final int interfaceId = packet.readUnsignedShort();
		final int id = packet.readUnsignedShortA();

		if (!player.hasFocus()) {
			return;
		}

		switch (interfaceId) {
		case 3900:
			GameService.getShopManager().sellItem(player, id, 10, slot);
			break;

		case 3823:
			if (ItemDefinition.forId(id).isStackable() || ItemDefinition.forId(id).isNoted()) {
				GameService.getShopManager().buyItem(player, id, 10, slot);
			} else {
				boolean inv = player.getInventory().isFiringEvents();
				player.getInventory().setFiringEvents(false);
				Item[] items = player.getInventory().toArray();

				for (int index = 0, value = 10; index < items.length && value > 0; index++) {
					Item item = items[index];

					if (item == null) {
						continue;
					}

					if (item.getId() == id) {
						GameService.getShopManager().buyItem(player, id, 1, index);
						value--;
					}
				}
				player.getInventory().setFiringEvents(inv);
				player.getInventory().fireItemsChanged();
			}
			break;
		case 2274:
			if (player.partyChestOpen) {
				PartyRoomManager.unqueueItem(player, new Item(id, player.chestDeposit.getCount(id)));
				return;
			}
			break;
		case 5064:
			if (player.partyChestOpen) {
				PartyRoomManager.queueItem(player, new Item(id, player.getInventory().getCount(id)));
				return;
			}
			if (!player.getBankField().isBanking() || player.isTrading()) {
				return;
			}

			if (player.getInterfaceState().getCurrentInterface() == 2700) {
				FamiliarInventory.deposit(player, slot, id, player.getInventory().getCount(player.getInventory().get(slot)));
			} else if (player.getInterfaceState().getCurrentInterface() == 5292) {
				if (ItemDefinition.forId(id).isStackable() || ItemDefinition.forId(id).isNoted()) {
					Bank.deposit(player, player.getInventory().getSlotById(id), id, player.getInventory().getCount(id), BankType.INVENTORY, false);
				} else {
					Item[] items = player.getInventory().toArray();

					for (int index = 0, value = player.getInventory().getCount(id); index < items.length && value > 0; index++) {
						Item item = items[index];

						if (item == null) {
							continue;
						}

						if (item.getId() == id) {
							Bank.deposit(player, index, id, 1, BankType.INVENTORY, false);
							value--;
						}
					}
				}

				player.getBank().fireItemChanged(player.getBank().getSlotById(id));
				player.getInventory().fireItemsChanged();
			}
			break;

		case 2700:
			if (slot >= 0 && slot < player.getSummoning().getFamiliarInventory().getCapacity()) {
				FamiliarInventory.withdraw(player, slot, id, player.getSummoning().getFamiliarInventory().getCount(player.getSummoning().getFamiliarInventory().get(slot)));
			}
			break;

		case 7423:
			if (player.storing) {

				return;
			}

			Bank.deposit(player, slot, player.getInventory().get(slot).getId(), player.getInventory().get(slot).getCount(), true);
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
			Bank.withdrawSelected(player,id,Integer.MAX_VALUE,slot,interfaceId);
			break;

		case 3322:
			if (player.isTrading()) {
				if (slot >= 0 && slot < Inventory.SIZE) {
					Trade.addItemToOffer(player, id, slot, player.getInventory().getCount(player.getInventory().get(slot)));
				}
			} else if (player.getDuel().isDueling()) {
				if (slot >= 0 && slot < Inventory.SIZE) {
					Stake.stakeItem(player, player.getInventory().get(slot).getId(), player.getInventory().get(slot).getCount(), slot);
				}
			}
			break;

		case Trade.TRADE_INTERFACE:
			if (player.isTrading()) {
				if (slot >= 0 && slot < Inventory.SIZE) {
					Trade.removeItemFromOffer(player, id, slot, player.getOther().getCount(player.getOther().get(slot)));
				}
			}

			break;

		case 7295:
			Bank.deposit(player, slot, player.getInventory().get(slot).getId(), player.getInventory().get(slot).getCount(), true);
			break;

		case 6669:
			if (slot >= 0 && slot < DuelArenaConstants.STAKE_SIZE) {
				Stake.removeItem(player, player.getDuel().getStakeInventory().get(slot).getId(), player.getDuel().getStakeInventory().get(slot).getCount());
			}
		}
	}

}
