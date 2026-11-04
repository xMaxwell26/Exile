package com.koios.network.out.impl;

import com.koios.game.model.EntityCharacter;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendEntityHintIconPacket implements OutboundPacket {

	private final int index;
	private final int type;

	public SendEntityHintIconPacket(int type, int index) {
		this.type = type;
		this.index = index;
	}

	public SendEntityHintIconPacket(EntityCharacter entity) {
		int id = entity == null ? 1 : entity.isMob() ? 1 : 10;
		int index = entity == null ? -1 : entity.getIndex();
		this.type = id;
		this.index = index;
	}

	@Override
	public void execute(Session player) {
		player.getPlayer().write(new GamePacketBuilder(254).writeByte(type).writeShort(index).writeTripleInt(0));
	}

}
