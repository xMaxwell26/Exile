package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendMoveWidgetComponentPacket implements OutboundPacket {

	private final int widgetId;
	private final int x;
	private final int y;

	/**
	 * Moves an interface component.
	 *
	 * @param widgetId The component id.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	public SendMoveWidgetComponentPacket(int widgetId, int x, int y) {
		this.x = x;
		this.y = y;
		this.widgetId = widgetId;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(70).writeShort(x).writeLEShort(y).writeLEShort(widgetId));
	}

}
