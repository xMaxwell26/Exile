package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Bank X Items
 */
public class PacketBankX1 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		if (!player.hasFocus()) {
			return;
		}

		if (packet.getOpcode() == 135) {
			int slot = packet.readLEShort();
			int interfaceId = packet.readUnsignedShortA();
			int itemId = packet.readLEShort();

			if (interfaceId == 3900) {
				GameService.getShopManager().sellItem(player, itemId, 100, slot);
				return;
			}

			if (interfaceId == 3823) {
				GameService.getShopManager().buyItem(player, itemId, 50, slot);
				return;
			}

			player.getInterfaceState().openValueInput(interfaceId, slot, itemId);
		}
	}

}
