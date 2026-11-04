package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendMinimapStatePacket implements OutboundPacket {

	private final int state;

	public SendMinimapStatePacket(int state) {
		this.state = state;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(99).writeByte(state));
	}

}
