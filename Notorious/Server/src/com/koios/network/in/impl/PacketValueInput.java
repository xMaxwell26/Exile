package com.koios.network.in.impl;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Bank X Items
 */
public class PacketValueInput implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int value = packet.readInt();

		if (!player.hasFocus()) {
			return;
		}

		if (value < 0) {
			// Note: negative values should only be accepted when truly
			// anticipated. In general it is NOT what we want.
			// Also, take note that the client does not allow the - character in
			// the amount dialogue anyway.
			return;
		}

		if (player.getInterfaceState().hasValueInputConsumer()) {
			player.getInterfaceState().valueInputReceived(value);
			return;
		}

		if (player.dialogue().input(value)) {
			return;
		}

	}

}