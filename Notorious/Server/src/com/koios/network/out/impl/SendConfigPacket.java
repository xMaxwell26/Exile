package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;

public class SendConfigPacket implements com.koios.network.out.OutboundPacket {

	private final int configId;
	private final int state;

	public SendConfigPacket(int configId, int state) {
		this.configId = configId;
		this.state = state;
	}

	public SendConfigPacket(int configId, boolean flag) {
		this.configId = configId;
		this.state = flag ? 1 : 0;
	}

	@Override
	public void execute(Session client) {
		if (state >= 0 && state <= 255) {
			client.getPlayer().write(new GamePacketBuilder(36).writeLEShort(configId).writeByte(state));
		} else {
			client.getPlayer().write(new GamePacketBuilder(87).writeLEShort(configId).writeSingleInt(state));
		}
	}

}
