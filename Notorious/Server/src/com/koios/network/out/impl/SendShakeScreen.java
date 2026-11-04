package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendShakeScreen implements OutboundPacket {

	private final int verticalAmount, verticalSpeed, horizontalAmount, horizontalSpeed;

	public SendShakeScreen(int verticalAmount, int verticalSpeed, int horizontalAmount, int horizontalSpeed) {
		this.verticalAmount = verticalAmount;
		this.verticalSpeed = verticalSpeed;
		this.horizontalAmount = horizontalAmount;
		this.horizontalSpeed = horizontalSpeed;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(35).writeByte(verticalAmount).writeByte(verticalSpeed).writeByte(horizontalAmount).writeByte(horizontalSpeed));
	}

}
