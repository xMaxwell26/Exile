package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

public class SendStringPacket implements OutboundPacket {

	private final int widgetId;
	private final String message;

	public SendStringPacket(String message, int widgetId) {
		this.message = message;
		this.widgetId = widgetId;
	}

	public SendStringPacket(int widgetId, String message) {
		this.message = message;
		this.widgetId = widgetId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(126, PacketType.SHORT).writeString(message == null ? "" : message).writeShortA(widgetId));
	}

}
