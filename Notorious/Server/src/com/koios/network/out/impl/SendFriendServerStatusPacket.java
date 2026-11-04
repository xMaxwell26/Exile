package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendFriendServerStatusPacket implements OutboundPacket {

	private final int status;

	public SendFriendServerStatusPacket(int status) {
		this.status = status;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(221).writeByte(status));
	}

}