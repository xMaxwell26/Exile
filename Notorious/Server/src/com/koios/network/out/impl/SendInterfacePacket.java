package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendInterfacePacket implements OutboundPacket {

	private final int interfaceId;

	public SendInterfacePacket(int interfaceId) {
		this.interfaceId = interfaceId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(97).writeShort(interfaceId));
	}

}
