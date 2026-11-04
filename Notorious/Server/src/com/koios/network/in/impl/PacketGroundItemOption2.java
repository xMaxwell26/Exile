package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

public class PacketGroundItemOption2 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int itemX = packet.readLEShort();
		final int itemY = packet.readLEShortA();
		final int itemId = packet.readShortA();
		player.walkingToItem = false;

		if (!player.hasFocus()) {
			return;
		}

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}
		}

		if (!GameService.getGroundItemManager().exists(player, itemId, itemX, itemY)) {
			// Note: invalidate bad requests to use a ground item that does not exist.
			return;
		}

		if (Math.abs(player.getX() - itemX) > 25 || Math.abs(player.getY() - itemY) > 25) {
			player.getMovementHandler().reset();
			return;
		}
	}

}
