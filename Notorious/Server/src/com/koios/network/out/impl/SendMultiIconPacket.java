package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendMultiIconPacket implements OutboundPacket {

	private final int icon;

	public SendMultiIconPacket(int icon) {
		this.icon = icon;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(61).writeByte(icon));
	}

}
