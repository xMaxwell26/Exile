package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

/**
 * Sends a private message to another player
 * 
 * @author Timothy Howard <Arithium>
 */
public class SendPrivateMessage implements OutboundPacket {

	/**
	 * The username hash of the player
	 */
	private final long usernameHash;

	/**
	 * The players rights
	 */
	private final int rights;

	/**
	 * The text being sent
	 */
	private final byte[] text;

	public SendPrivateMessage(long usernameHash, int rights, byte[] text) {
		this.usernameHash = usernameHash;
		this.rights = rights;
		this.text = text;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer()
				.write(new GamePacketBuilder(196, PacketType.BYTE).writeLong(usernameHash).writeLEInt(client.getPlayer().lastChatId++).writeByte(rights).writeByteArray(text));
	}

}
