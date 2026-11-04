package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendPlayerInitializationPacket implements OutboundPacket {

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(249).writeByteA(1).writeLEShortA(player.getPlayer().index()));
	}

}
