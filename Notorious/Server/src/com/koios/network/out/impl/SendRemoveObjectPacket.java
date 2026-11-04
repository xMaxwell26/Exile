package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendRemoveObjectPacket implements OutboundPacket {

	/**
	 * The type of the object
	 */
	private final int type;

	/**
	 * The direction the object is facing
	 */
	private final int direction;

	public SendRemoveObjectPacket(final int type, final int direction) {
		this.type = type;
		this.direction = direction;
	}

	@Override
	public void execute(Session session) {
		session.getPlayer().write(new GamePacketBuilder(101).writeByteC(type << 2 | direction & 3).writeByte(0));
	}

}
