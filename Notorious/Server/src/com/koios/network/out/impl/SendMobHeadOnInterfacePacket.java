package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendMobHeadOnInterfacePacket implements OutboundPacket {

	private final int interfaceId;
	private final int npcId;

	public SendMobHeadOnInterfacePacket(int npcId, int interfaceId) {
		this.npcId = npcId;
		this.interfaceId = interfaceId;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(75).writeLEShortA(npcId).writeLEShortA(interfaceId));
	}

}
