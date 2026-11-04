package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

/**
 * Represents a single outbound sound packet
 *
 * @author Arithium
 */
public class SendSoundPacket implements OutboundPacket {

	/**
	 * The id of the sound
	 */
	private final int id;

	/**
	 * The type of the sound
	 */
	private final int type;

	/**
	 * The delay of the sound
	 */
	private final int delay;

	/**
	 * Constructs a new SendSoundPacket
	 *
	 * @param id The id of the sound
	 * @param type The type of the sound
	 * @param delay The delay of the sound
	 */
	public SendSoundPacket(int id, int type, int delay) {
		this.id = id;
		this.delay = delay;
		if (type == 0) {
			this.type = 10;
		} else {
			this.type = type;
		}
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(174).writeShort(id).writeByte(type).writeShort(delay));
	}

}
