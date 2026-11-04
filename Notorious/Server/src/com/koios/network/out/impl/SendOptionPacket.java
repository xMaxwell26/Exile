package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.OutboundPacket;

public class SendOptionPacket implements OutboundPacket {

	private final int index;

	private final String action;

	private final boolean priority;

	public SendOptionPacket(int index, String action, boolean priority) {
		this.index = index;
		this.action = action;
		this.priority = priority;
	}

	@Override
	public void execute(Session player) {
		String previousAction = player.getPlayer().atPlayerActions[index - 1];

		if (action == null && previousAction == null) {
			// Old and new action both null.
			return;
		}

		if (action != null && action.equals(previousAction)) {
			// Old and new action are the same.
			return;
		}

		player.getPlayer().write(new GamePacketBuilder(104, PacketType.BYTE).writeByteC(index).writeByteA(priority ? 1 : 0).writeString(action == null ? "null" : action).toPacket());
		player.getPlayer().atPlayerActions[index - 1] = action;
	}

}
