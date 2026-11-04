package com.koios.network.in.impl;

import com.koios.game.content.obj.Fillables;
import com.koios.game.content.skill.cooking.Cooking;
import com.koios.game.content.skill.farming.Farming;
import com.koios.game.content.skill.smithing.SmithingInterface;
import com.koios.game.map.ObjectDefinition;
import com.koios.game.model.item.ItemInteraction;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.task.EntityTask;

public class PacketItemWithObject implements IncomingPacketDecoder {

	private int getInteractionDistance(int id) {
		switch (id) {
		case 6:
			return 2;
		}

		return 1;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void handle(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort();
		final int objectId = packet.readLEShort();
		final int objectY = packet.readLEShortA();
		final int itemSlot = packet.readLEShort();
		final int objectX = packet.readLEShortA();
		final int itemId = packet.readShort();

		if (interfaceId != Inventory.INTERFACE) {
			// Note: currently we support only inventory items being used with objects.
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (player.isPacketReadDisabled()) {
			return;
		}

		if (itemSlot < 0 || itemSlot >= player.getInventory().getCapacity() || player.getInventory().get(itemSlot) == null || player.getInventory().get(itemSlot).getId() != itemId
				|| player.getInventory().get(itemSlot).getCount() < 1) {
			return;
		}

		if (player.dialogue().isActive()) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		player.objectX = objectX;
		player.objectY = objectY;

		if (player.goodDistance(objectX, objectY, player.getX(), player.getY(), getInteractionDistance(objectId))) {
			handleItemOnObject(player, objectId, objectX, objectY, itemId);
		} else {
			player.schedule(new EntityTask(1, player, false) {

				@Override
				public void execute() {
					if (player.goodDistance(objectX, objectY, player.getX(), player.getY(), getInteractionDistance(objectId))) {
						handleItemOnObject(player, objectId, objectX, objectY, itemId);
						stop();
					}
				}

			});
		}
	}

	private void handleItemOnObject(Player player, int id, int x, int y, int itemId) {
		player.setFacePosition(x, y);
		ObjectDefinition def = ObjectDefinition.getById(id);
		Fillables.fillTheItem(player, itemId, id);

		if (Farming.prepareCrop(player, itemId, id, x, y)) {
			return;
		}

		if (def != null) {
			if (def.name != null) {
				if (def.name.toLowerCase().contains("compost")) {
					Farming.prepareCrop(player, itemId, id, x, y);
					return;
				}
			}
		}

		ItemInteraction.useItemWithObject(player, id, x, y, itemId);

		if (player.isPacketReadDisabled()) {
			return;
		}

		if (Cooking.handleItemOnObject(player, itemId, id)) {
			return;
		}

		switch (id) {
		case 6:
			if (player.oldCannon != null && player.oldCannon.x == x && player.oldCannon.y == y) {
				player.getDwarfMultiCannon().loadCannon();
			} else {
				player.sendMessage("This is not your cannon.");
			}
			break;

		case 2783:
			SmithingInterface.showSmithInterface(player, itemId);
		}
	}

}
