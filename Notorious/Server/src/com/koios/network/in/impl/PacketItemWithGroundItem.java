package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

public class PacketItemWithGroundItem implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		int interfaceId = packet.readLEShort();
		int invItemId = packet.readShortA();
		int groundItemId = packet.readShort();
		int groundItemY = packet.readShortA();
		int invSlot = packet.readLEShortA();
		int groundItemX = packet.readShort();

		if (interfaceId != Inventory.INTERFACE) {
			// Note: current support only covers the inventory.
			return;
		}

		if (invSlot < 0 || invSlot >= player.getInventory().getCapacity() || player.getInventory().get(invSlot) == null || player.getInventory().get(invSlot).getId() != invItemId
				|| player.getInventory().get(invSlot).getCount() < 1) {
			return;
		}

		if (!GameService.getGroundItemManager().exists(player, groundItemId, groundItemX, groundItemY)) {
			// Note: invalidate bad requests to use a ground item that does not exist.
			return;
		}

		switch (invItemId) {
		default:
			if (player.getPrivilege() == RegularPrivilege.DEVELOPER) {
				System.out.println("ItemUsed " + invItemId + " on Ground Item " + groundItemId);
			}
		}
	}

}
