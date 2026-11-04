package com.koios.network.in.impl;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.util.StringUtil;

/**
 * This packet contains an encoded username - and is intended used for friends chat and such.
 */
public class PacketNameInput implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		long name = packet.readLong();
		if (!StringUtil.checkValid(name)) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}
		
		player.getInterfaceState().nameInputReceived(StringUtil.hashToUsername(name));
	}

}
