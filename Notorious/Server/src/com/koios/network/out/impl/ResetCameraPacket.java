package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

/**
 * Resets the players camera
 * 
 * @author Timothy Howard <Arithium>
 */
public class ResetCameraPacket implements OutboundPacket {

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(107));
	}

}
