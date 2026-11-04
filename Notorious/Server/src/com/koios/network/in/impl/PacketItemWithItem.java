package com.koios.network.in.impl;

import com.koios.game.content.randoms.RandomEvent;
import com.koios.game.content.skill.crafting.Crafting;
import com.koios.game.content.skill.fletching.CraftShortBowAction;
import com.koios.game.content.skill.fletching.CreateArrowAction;
import com.koios.game.content.skill.fletching.CreateBoltAction;
import com.koios.game.content.skill.fletching.CreateBoltTipAction;
import com.koios.game.content.skill.fletching.StringWeaponAction;
import com.koios.game.content.skill.herblore.CreateTarAction;
import com.koios.game.content.skill.herblore.FinishPotionAction;
import com.koios.game.content.skill.herblore.PotionDecanting;
import com.koios.game.content.skill.herblore.UnfPotionAction;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemInteraction;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.ItemIdentifiers;

/**
 * @author Ryan / Lmctruck30
 */
public class PacketItemWithItem implements IncomingPacketDecoder {

	private boolean containsType(int id, int itemId1, int itemId2) {
		return itemId1 == id || itemId2 == id;
	}

	@Override
	public void handle(Player player, GamePacket packet) {
		final int usedWithSlot = packet.readShort();
		final int itemUsedSlot = packet.readShortA();
		final int item1ID = packet.readLEShortA();
		final int interfaceId1 = packet.readShort();
		final int item2ID = packet.readLEShort();
		final int interfaceId2 = packet.readShort();

		if (player.isPacketReadDisabled()) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (interfaceId1 != Inventory.INTERFACE || interfaceId2 != Inventory.INTERFACE) {
			// invalidate requests not related to the inventory
			return;
		}

		if (usedWithSlot < 0 || usedWithSlot >= player.getInventory().getCapacity() || itemUsedSlot < 0 || itemUsedSlot >= player.getInventory().getCapacity()) {
			return; // Invalid item slot
		}

		if (player.getInventory().get(itemUsedSlot) == null || player.getInventory().get(usedWithSlot) == null || item1ID != player.getInventory().get(usedWithSlot).getId()
				|| item2ID != player.getInventory().get(itemUsedSlot).getId()) {
			return; // Items mismatched.
		}

		if(com.koios.game.arpg.GemCrafting.use(player,itemUsedSlot,usedWithSlot))return;
		if(com.koios.game.arpg.CraftingCurrency.use(player,itemUsedSlot,usedWithSlot))return;
        if(player.getInventory().get(itemUsedSlot).hasRoll()||player.getInventory().get(usedWithSlot).hasRoll()){player.sendMessage("Use a crafting material on this item.");return;}
		RandomEvent.tryRandom(player);
		player.getSession().send(new SendRemoveInterfacePacket());

		if (containsType(492, item1ID, item2ID)) {
			final int axeHeadID = item1ID != 492 ? item1ID : item2ID;
			int product = -1;

			if (axeHeadID == 508) {
				product = 1351;
			} else if (axeHeadID == 510) {
				product = 1349;
			} else if (axeHeadID == 512) {
				product = 1353;
			} else if (axeHeadID == 514) {
				product = 1361;
			} else if (axeHeadID == 516) {
				product = 1355;
			} else if (axeHeadID == 518) {
				product = 1357;
			} else if (axeHeadID == 520) {
				product = 1359;
			}

			if (product != -1) {
				player.getInventory().set(itemUsedSlot, new Item(-1));
				player.getInventory().set(usedWithSlot, new Item(-1));
				player.getInventory().add(new Item(product, 1));
				player.sendMessage("You reassemble the " + ItemDefinition.forId(product).getName() + ".");
				return;
			}
		} else if (item1ID == 6743 && item2ID == 7952 || item1ID == 7952 && item2ID == 6743) {
			player.getInventory().set(itemUsedSlot, new Item(-1));
			player.getInventory().set(usedWithSlot, new Item(-1));
			player.getInventory().add(new Item(6739, 1));
			player.sendMessage("You reassemble the " + ItemDefinition.forId(6739).getName() + ".");
			return;
		}

		if (item1ID == ItemIdentifiers.ROTTEN_POTATO) {
			ItemDefinition def = ItemDefinition.forId(item2ID);
			player.sendMessage(def.getName() + ": " + def.getId() + (def.isUntradeable() ? "" : " (tradeable)"));
			return;
		} else if (item2ID == ItemIdentifiers.ROTTEN_POTATO) {
			ItemDefinition def = ItemDefinition.forId(item1ID);
			player.sendMessage(def.getName() + ": " + def.getId());
			return;
		}

		if (CraftShortBowAction.handleItems(player, item1ID, item2ID)) {
			return;
		}

		if (StringWeaponAction.canFinishWeapon(player, item1ID, item2ID)) {
			return;
		}

		if (CreateBoltAction.canCreateBolts(player, item1ID, item2ID)) {
			return;
		}

		if (CreateArrowAction.canCreateBolts(player, item1ID, item2ID)) {
			return;
		}

		if (CreateBoltTipAction.canMakeTip(player, item1ID, item2ID)) {
			return;
		}

		if (UnfPotionAction.canCreatePotion(player, item1ID, item2ID)) {
			return;
		}

		if (FinishPotionAction.canCreatePotion(player, item1ID, item2ID)) {
			return;
		}

		if (CreateTarAction.canCreateTar(player, item1ID, item2ID)) {
			return;
		}

		if (Crafting.handleItems(player, item1ID, item2ID)) {
			return;
		}

		if (PotionDecanting.handlePotionDecanting(player, item1ID, item2ID)) {
			return;
		}

		ItemInteraction.useItemWithItem(player, item1ID, item2ID);
	}

}
