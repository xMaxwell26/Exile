package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

public class SendFriendRank implements OutboundPacket {

	private final String name;

	private final int rank;

	public SendFriendRank(String name, int rank) {
		this.name = name;
		this.rank = rank;
	}

	@Override
	public void execute(Session session) {
		session.getPlayer().write(new GamePacketBuilder(181, PacketType.BYTE).writeString(name).writeByte(rank));
	}

}
