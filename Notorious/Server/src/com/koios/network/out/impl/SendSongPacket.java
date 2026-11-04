package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendSongPacket implements OutboundPacket {

	private final int songId;

	public SendSongPacket(int songId) {
		this.songId = songId;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(74).writeLEShort(songId));
	}

}
