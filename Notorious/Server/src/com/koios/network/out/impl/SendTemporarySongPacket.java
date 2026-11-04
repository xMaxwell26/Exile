package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendTemporarySongPacket implements OutboundPacket {

	private final int songDelay;

	private final int songId;

	public SendTemporarySongPacket(int songId, int songDelay) {
		this.songId = songId;
		this.songDelay = songDelay;
	}

	@Override
	public void execute(Session client) {
		if (!client.getPlayer().getAttribute("music_enabled", false)) {
			return;
		}
		client.getPlayer().write(new GamePacketBuilder(121).writeLEShort(songId).writeLEShort(songDelay));
	}

}
