package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendChatOptionsPacket implements OutboundPacket {

	private final int publicChat;

	private final int privateChat;

	private final int tradeChat;

	public SendChatOptionsPacket(int publicChat, int privateChat, int tradeChat) {
		this.publicChat = publicChat;
		this.privateChat = privateChat;
		this.tradeChat = tradeChat;
	}

	@Override
	public void execute(Session session) {
		session.getPlayer().write(new GamePacketBuilder(206).writeByte(publicChat).writeByte(privateChat).writeByte(tradeChat));
	}

}
