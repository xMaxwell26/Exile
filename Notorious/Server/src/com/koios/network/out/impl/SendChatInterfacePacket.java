package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendChatInterfacePacket implements OutboundPacket {

	private final int id;

	public SendChatInterfacePacket(int id) {
		this.id = id;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().getInterfaceState().setCurrentInterface(id);
		client.getPlayer().write(new GamePacketBuilder(164).writeLEShort(id));
	}

}
