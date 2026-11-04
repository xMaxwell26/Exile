package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

/**
 * Enables a sprite in a client sided interface.
 */
public class ToggleWidgetLayer implements OutboundPacket {

	/**
	 * The id of the sprite to toggle
	 */
	private int sprite;

	/**
	 * The sprite is enabled/disabled
	 */
	private boolean enabled;

	public ToggleWidgetLayer(boolean enabled, int sprite) {
		this.enabled = enabled;
		this.sprite = sprite;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(171).writeByte(enabled ? 0 : 1).writeShort(sprite));
	}

}
