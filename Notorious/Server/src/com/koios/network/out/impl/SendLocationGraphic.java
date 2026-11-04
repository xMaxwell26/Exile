package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendLocationGraphic implements OutboundPacket {

	private final int id, x, y, height, time;

	public SendLocationGraphic(int id, int x, int y, int height, int time) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.height = height;
		this.time = time;
	}

	@Override
	public void execute(Session client) {
		client.send(new SendLocalPosition(x, y, false));
		client.getPlayer().write(new GamePacketBuilder(4).writeByte(0).writeShort(id).writeByte(height).writeShort(time));
	}

}
