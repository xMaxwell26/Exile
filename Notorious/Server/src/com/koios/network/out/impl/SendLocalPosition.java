package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendLocalPosition implements OutboundPacket {

	private final int x;

	private final int y;

	private final boolean offset;

	public SendLocalPosition(int x, int y, boolean offset) {
		this.x = x;
		this.y = y;
		this.offset = offset;
	}

	@Override
	public void execute(Session client) {
		int localY = (y - (8 * client.getPlayer().getCurrentRegion().getRegionY())) - (offset ? 2 : 0);
		int localX = (x - (8 * client.getPlayer().getCurrentRegion().getRegionX())) - (offset ? 3 : 0);
		client.getPlayer().write(new GamePacketBuilder(85).writeByteC(localY).writeByteC(localX));
	}

}
