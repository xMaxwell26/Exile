package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendPlanePacket implements OutboundPacket {

	@Override
	public void execute(Session session) {
		session.write(new GamePacketBuilder(77).writeInt(session.getPlayer().getLocation().getZ()).toPacket());
	}

}
