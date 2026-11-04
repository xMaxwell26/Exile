package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

public class SendPrivateMessagePacket implements OutboundPacket {

	private final byte[] message;
	private final int rights;
	private final long usernameToLong;

	public SendPrivateMessagePacket(long username, int rights, byte[] message, int size) {
		usernameToLong = username;
		this.rights = rights;
		this.message = message;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer()
				.write(new GamePacketBuilder(196, PacketType.BYTE).writeLong(usernameToLong).writeLEInt(player.getPlayer().lastChatId++).writeByte(rights).writeByteArray(message));
	}

}
