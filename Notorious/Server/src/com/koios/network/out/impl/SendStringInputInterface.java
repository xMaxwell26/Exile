package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;
import com.koios.util.Logger;

/**
 * Sends the string input interface
 *
 * @author Gabriel
 */
public class SendStringInputInterface implements OutboundPacket {

	@Override
	public void execute(Session client) {
		if (client.getPlayer().getInterfaceState().hasNameInputConsumer()) {
			client.getPlayer().write(new GamePacketBuilder(187));
		} else {
			Logger.writeLogs(client.getPlayer(), "[ Packet 187 - Out] The player " + client.getPlayer().getName() + " attempt to loot name input field without setting consumer.",
					"dupelogs");
		}
	}

}
