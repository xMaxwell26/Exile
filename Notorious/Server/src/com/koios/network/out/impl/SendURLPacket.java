package com.koios.network.out.impl;

import com.google.common.base.Preconditions;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

/**
 * Sends an {@link OutboundPacket} to open a URL on the players web browser
 * 
 * @author Arithium
 *
 */
public class SendURLPacket implements OutboundPacket {

	/**
	 * The URL to open with the web browser
	 */
	private final String url;

	/**
	 * The amount of times to open the URL
	 */
	private final int count;

	/**
	 * Sends a URL for the client to open
	 * 
	 * @param url
	 *            The URL to open
	 * @param count
	 *            The amount of times to open the URL
	 */
	public SendURLPacket(String url, int count) {
		Preconditions.checkArgument(url != null, "URL must not be null");
		Preconditions.checkArgument(count > 0, "The count must be greater then 0.");
		this.url = url;
		this.count = count;
	}

	@Override
	public void execute(Session session) {
		session.write(new GamePacketBuilder(129, PacketType.SHORT).writeString(url).writeInt(count).toPacket());
	}

}
