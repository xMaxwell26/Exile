package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendItemOnInterfacePacket implements OutboundPacket {

	private final int interfaceId;
	private final int itemId;
	private final int zoom;

	/**
	 * Sends an item on the interface
	 *
	 * @param interfaceId
	 * @param zoom
	 * @param itemId
	 */
	public SendItemOnInterfacePacket(int interfaceId, int zoom, int itemId) {
		this.interfaceId = interfaceId;
		this.zoom = zoom;
		this.itemId = itemId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(246).writeLEShort(interfaceId).writeShort(zoom).writeShort(itemId));
	}

}
