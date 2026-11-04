package com.koios.network.out.impl;

import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendClientScriptPacket implements OutboundPacket {

	public SendClientScriptPacket(int scriptId, String params) {
	}

	@Override
	public void execute(Session client) {
		// client.getPlayer().write(new GamePacketBuilder(247,
		// PacketType.BYTE).writeString(params).writeByte(scriptId));
	}

}
