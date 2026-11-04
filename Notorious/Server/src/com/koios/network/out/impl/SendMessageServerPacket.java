package com.koios.network.out.impl;

import com.koios.Constants;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendMessageServerPacket implements OutboundPacket {

	private final long username;

	private int world;

	public SendMessageServerPacket(long username, int world) {
		this.username = username;
		this.world = world;
	}

	@Override
	public void execute(Session player) {
		if (world != 0) {
			world += 9;
		} else if (!Constants.WORLD_LIST_FIX) {
			world += 1;
		}
		player.getPlayer().write(new GamePacketBuilder(50).writeLong(username).writeByte(world));
	}

}
