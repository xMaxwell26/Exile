package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

public class SendMessagePacket implements OutboundPacket {

	private final String message;

	public SendMessagePacket(String message) {
		this.message = message;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(253, PacketType.BYTE).writeString(message));
	}

}
