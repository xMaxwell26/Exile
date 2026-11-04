package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendWalkableWidget implements OutboundPacket {

	private final int interfaceId;

	public SendWalkableWidget(int id) {
		this.interfaceId = id;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(208).writeLEShort(interfaceId).toPacket());
	}

}
