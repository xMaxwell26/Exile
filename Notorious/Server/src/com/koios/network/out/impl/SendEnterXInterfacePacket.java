package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;
import com.koios.util.Logger;

public class SendEnterXInterfacePacket implements OutboundPacket {

	@Override
	public void execute(Session client) {
		if (client.getPlayer().getInterfaceState().hasValueInputConsumer()) {
			client.getPlayer().write(new GamePacketBuilder(27));
		} else {
			Logger.writeLogs(client.getPlayer(), "[Packet 27 - Out] The player " + client.getPlayer().getName() + " attempt to loot integer input field without setting consumer.",
					"dupelogs");
		}
	}

}
