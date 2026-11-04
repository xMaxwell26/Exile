package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendInventoryOverlayPacket implements OutboundPacket {

	private final int interfaceId;
	private final int overlayId;

	public SendInventoryOverlayPacket(int interfaceId, int overlayId) {
		this.interfaceId = interfaceId;
		this.overlayId = overlayId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().getInterfaceState().setCurrentInterface(interfaceId);
		client.getPlayer().write(new GamePacketBuilder(248).writeShortA(interfaceId).writeShort(overlayId));
	}

}
