package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendObjectPacket implements OutboundPacket {

	private final int objectId, objectX, objectY, z, face, type;

	public SendObjectPacket(int objectId, int objectX, int objectY, int z, int face, int objectType) {
		this.objectId = objectId;
		this.objectX = objectX;
		this.objectY = objectY;
		this.z = z;
		this.face = face;
		type = objectType;
	}

	@Override
	public void execute(Session player) {
		if (player.getPlayer().getZ() != z) {
			return;
		}

		player.send(new SendLocalPosition(objectX, objectY, false));
		player.send(new SendRemoveObjectPacket(type, face));

		if (objectId != -1) {
			player.getPlayer().write(new GamePacketBuilder(151).writeByteS(0).writeLEShort(objectId).writeByteS(type << 2 | face & 3));
		}
	}

}
