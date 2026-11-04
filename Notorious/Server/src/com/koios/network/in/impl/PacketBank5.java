package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.dialogue.impl.teleportation.GloryTeleportDialogue;
import com.koios.game.content.minigames.duel.DuelArenaConstants;
import com.koios.game.content.partyroom.PartyRoomManager;
import com.koios.game.content.skill.smithing.SmithingAction;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.FamiliarInventory;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.Stake;
import com.koios.game.model.item.container.Trade;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.bank.Bank.BankType;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Bank 5 Items
 */
public class PacketBank5 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int interfaceId = packet.readLEShortA() & 0xFFFF;
		final int id = packet.readLEShortA() & 0xFFFF;
		final int slot = packet.readLEShort() & 0xFFFF;

		if (!player.hasFocus()) {
			return;
		}

		switch (interfaceId) {
		case 1688:
			switch (id) {
			case 10396:
				player.playAnimation(new Animation(5312));
				break;

			case 10398:
				player.playAnimation(new Animation(5313));
				break;

			case 10392:
				player.playAnimation(new Animation(5315));
				break;

			case 10394:
				player.playAnimation(new Animation(5316));
				break;

			case 19308:
				player.playAnimation(new Animation(9633));
				player.playGraphics(new Graphic(931));
				player.speak("You shall not pass!");
				break;

			case 11283:
			case 11284:
				if (player.getCombat().getTarget() != null) {
					CombatExtras.handleDragonFireShield(player);
				}
				break;

			case 1706:
			case 1708:
			case 1710:
			case 1712:
				player.dialogue().start(new GloryTeleportDialogue(player, id, Equipment.SLOT_AMULET, false));
				break;

			default:
				player.sendMessage("Nothing interesting happens.");
			}
			break;

		case 3900:
			GameService.getShopManager().sellItem(player, id, 1, slot);
			break;

		case 3823:
			GameService.getShopManager().buyItem(player, id, 1, slot);
			break;

		case 2274:
			if (player.partyChestOpen) {
				PartyRoomManager.unqueueItem(player, new Item(id, 5));
				return;
			}
			break;

		case 5064:
			if (player.partyChestOpen) {
				PartyRoomManager.queueItem(player, new Item(id, 5));
				return;
			}

			if (player.getInterfaceState().getCurrentInterface() == 2700) {
				FamiliarInventory.deposit(player, slot, id, 5);
			} else if (player.getInterfaceState().getCurrentInterface() == 5292) {
				if (ItemDefinition.forId(id).isStackable() || ItemDefinition.forId(id).isNoted()) {
					int am = player.getInventory().get(slot).getCount();
					am = am > 5 ? 5 : am;
					Bank.deposit(player, player.getInventory().getSlotById(id), id, am, BankType.INVENTORY, true);
				} else {
					Item[] items = player.getInventory().toArray();

					for (int index = 0, value = 5; index < items.length && value > 0; index++) {
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
				FamiliarInventory.withdraw(player, slot, id, 5);
			}
			break;

		case 7423:
			if (player.storing) {
				return;
			}

			Bank.deposit(player, slot, id, 5, true);
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
			Bank.withdrawSelected(player,id,5,slot,interfaceId);
			break;

		case 3322:
			if (player.isTrading()) {
				if (slot >= 0 && slot < Inventory.SIZE) {
					Trade.addItemToOffer(player, id, slot, 5);
				}
			} else if (player.getDuel().isDueling()) {
				if (slot >= 0 && slot < Inventory.SIZE) {
					Stake.stakeItem(player, player.getInventory().get(slot).getId(), 5, slot);
				}
			}
			break;

		case Trade.TRADE_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Trade.removeItemFromOffer(player, id, slot, 5);
			}
			break;

		case 6669:
			if (slot >= 0 && slot < DuelArenaConstants.STAKE_SIZE) {
				Stake.removeItem(player, player.getDuel().getStakeInventory().get(slot).getId(), 5);
			}
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			SmithingAction.readInput(player, id, 5);
			break;
		}
	}

}
