package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

public class SendFriendschatMessagePacket implements OutboundPacket {

	private final String name;
	private final String message;
	private final String clan;
	private final int rights;

	public SendFriendschatMessagePacket(String name, String message, String clan, int rights) {
		this.name = name;
		this.message = message;
		this.clan = clan;
		this.rights = rights;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(217, PacketType.SHORT).writeString(name).writeString(message).writeString(clan).writeShort(rights));
	}

}
