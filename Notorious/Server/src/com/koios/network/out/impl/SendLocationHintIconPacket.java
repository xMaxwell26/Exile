package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendLocationHintIconPacket implements OutboundPacket {

	private final int height;
	private final int position;
	private final int x;
	private final int y;

	/**
	 * Creates an arrow at a specific location
	 */
	public SendLocationHintIconPacket(int position, int x, int y, int height) {
		this.position = position;
		this.x = x;
		this.y = y;
		this.height = height;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(254).writeByte(position).writeShort(x).writeShort(y).writeByte(height));
	}

}
