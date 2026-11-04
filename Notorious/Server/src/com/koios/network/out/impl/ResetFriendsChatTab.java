package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class ResetFriendsChatTab implements OutboundPacket {

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(190));
	}

}
