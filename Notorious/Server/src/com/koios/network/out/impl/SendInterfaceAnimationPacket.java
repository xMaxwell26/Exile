package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendInterfaceAnimationPacket implements OutboundPacket {

	private final int animationId;
	private final int interfaceId;

	public SendInterfaceAnimationPacket(int interfaceId, int animationId) {
		this.interfaceId = interfaceId;
		this.animationId = animationId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(200).writeShort(interfaceId).writeShort(animationId));
	}

}
