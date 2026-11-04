package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendSetWidgetColor implements OutboundPacket {

	private final int rgb;

	private final int componentId;

	/**
	 * @param componentId The interface component id.
	 * @param rgb The RGB color value.
	 */
	public SendSetWidgetColor(int componentId, int rgb) {
		this.componentId = componentId;
		this.rgb = rgb;
	}

	@Override
	public void execute(Session client) {
		int r = rgb >> 16 & 0xFF;
		int g = rgb >> 8 & 0xFF;
		int b = rgb & 0xFF;
		int rgb16 = (r << 10 | g << 5 | b & 0x1F) & 0xFFFF;
		client.getPlayer().write(new GamePacketBuilder(122).writeLEShortA(componentId).writeLEShortA(rgb16));
	}

}
