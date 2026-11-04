package com.koios.network.out;

import com.koios.network.Session;

/**
 * An interface that represents an outbound (i.e. server -> client) packet.
 */
public interface OutboundPacket {

	public void execute(Session session);

}
