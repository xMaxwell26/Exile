package com.koios.network.in.impl;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Clicking in game
 */
public class PacketClickingInGame implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
	}

}
