package com.koios.network.in.impl;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

public class PacketIdleIndication implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
	}

}
