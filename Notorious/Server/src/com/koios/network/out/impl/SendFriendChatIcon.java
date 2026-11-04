package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendFriendChatIcon implements OutboundPacket {

	private final int icon;

	private final int componentId;

	public SendFriendChatIcon(int icon, int componentId) {
		this.icon = icon;
		this.componentId = componentId;
	}

	@Override
	public void execute(Session session) {
		session.getPlayer().write(new GamePacketBuilder(180).writeShort(icon).writeShort(componentId));
	}

}
