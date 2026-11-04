package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendHeadToInterfacePacket implements OutboundPacket {

	private final int interfaceId;

	public SendHeadToInterfacePacket(int interfaceId) {
		this.interfaceId = interfaceId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(185).writeLEShortA(interfaceId));
	}

}
