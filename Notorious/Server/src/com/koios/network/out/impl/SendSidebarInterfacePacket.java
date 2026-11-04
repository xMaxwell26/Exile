package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public final class SendSidebarInterfacePacket implements OutboundPacket {

	private final int index;
	private final int interfaceId;

	public SendSidebarInterfacePacket(int index, int interfaceId) {
		this.index = index;
		this.interfaceId = interfaceId;
	}

	@Override
	public void execute(Session session) {
		session.write(new GamePacketBuilder(71).writeShort(interfaceId).writeByteA(index));
	}

}