package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

/**
 * Sets the players private message state
 * 
 * @author Timothy Howard <Arithium>
 */
public class SetPrivateMessageState implements OutboundPacket {

	/**
	 * The state to set, 0 = offline, 1 = friends only, 2 = online
	 */
	private final int state;

	/**
	 * Constructs a new {@link OutboundPacket} to set the private message state
	 * 
	 * @param state The state to set
	 */
	public SetPrivateMessageState(int state) {
		this.state = state;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(221).writeByte(state));
	}

}
