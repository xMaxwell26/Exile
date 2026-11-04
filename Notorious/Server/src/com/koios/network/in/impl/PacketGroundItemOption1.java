package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.combat.Combat;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.GlobalDropsHandler;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.task.Task;
import com.koios.util.Logger;

/**
 * Select first item entity option (usually "Take")
 */
public class PacketGroundItemOption1 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int itemY = packet.readLEShort();
		final int itemId = packet.readShort();
		final int itemX = packet.readLEShort();
		player.walkingToItem = false;

		if (!player.hasFocus()) {
			return;
		}

		if (!GameService.getGroundItemManager().exists(player, itemId, itemX, itemY)) {
			// Note: invalidate bad requests to use a ground item that does not exist.
			return;
		}

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}
		}

		if (Math.abs(player.getX() - itemX) > 25 || Math.abs(player.getY() - itemY) > 25) {
			player.getMovementHandler().reset();
			return;
		}

		GlobalDropsHandler.take(player, itemId, itemX, itemY);
		Combat.resetCombat(player);

		if (player.getX() == itemX && player.getY() == itemY) {
			Logger.writeLogs(player,
					player.getName() + " picked the item '" + ItemDefinition.getName(itemId) + "' (ID: " + itemId + "), X:" + itemX + ", Y:" + itemY + ", Z:" + player.getZ(),
					"pickitem");
			GameService.getGroundItemManager().removeGroundItem(player, itemId, itemX, itemY, player.getZ(), true);

		} else {
			player.walkingToItem = true;

			GameService.getTaskScheduler().schedule(new Task() {

				@Override
				public void execute() {
					if (player.isDisconnected() || !player.walkingToItem) {
						stop();
						return;
					}

					if (player.getX() == itemX && player.getY() == itemY) {
						GameService.getGroundItemManager().removeGroundItem(player, itemId, itemX, itemY, player.getZ(), true);
						stop();
					}
				}

				@Override
				public void onStop() {
					player.walkingToItem = false;
				}

			});
		}
	}

}
