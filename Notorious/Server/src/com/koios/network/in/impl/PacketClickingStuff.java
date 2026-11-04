package com.koios.network.in.impl;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * Clicking stuff (interfaces)
 */
public class PacketClickingStuff implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		player.getSession().send(new SendRemoveInterfacePacket());
	}

}
