package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendOpenSideTabPacket implements OutboundPacket {

	private final int tab;

	public SendOpenSideTabPacket(int tabId) {
		tab = tabId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(106).writeByteC(tab));
	}

}
