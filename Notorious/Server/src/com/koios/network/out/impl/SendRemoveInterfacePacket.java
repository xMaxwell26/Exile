package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendRemoveInterfacePacket implements OutboundPacket {

	@Override
	public void execute(Session player) {
		player.getPlayer().getInterfaceState().closeInterfaces();
		player.getPlayer().write(new GamePacketBuilder(219));
	}

}
