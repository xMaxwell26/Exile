package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

/**
 * Changes the color of a string on an interface
 *
 * @author Arithium
 */
public class SendStringColorPacket implements OutboundPacket {

	private final int color;

	private final int stringId;

	/**
	 * Changes a strings color
	 *
	 * @param stringId
	 * @param color
	 */
	public SendStringColorPacket(int stringId, int color) {
		this.stringId = stringId;
		this.color = color;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(122).writeLEShortA(stringId).writeLEShortA(color));
	}

}
