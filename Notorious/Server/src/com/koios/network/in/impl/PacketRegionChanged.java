package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.music.MusicData;
import com.koios.game.content.skill.farming.Farming;
import com.koios.game.model.item.GlobalDropsHandler;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Change Regions
 */
public class PacketRegionChanged implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {

		GameService.getObjectHandler().updateObjects(player);
		GameService.getGroundItemManager().reloadItems(player);
		GameService.getObjectManager().loadObjects(player);
		GlobalDropsHandler.load(player);
		Farming.updateFarmingConfigs(player);
		MusicData.playMusic(player);
		player.allowSaveRequests = true;com.koios.game.arpg.MapRuns.fixtures(player);

		if (player.skullTimer > 0) {
			player.isSkulled = true;
			player.skullIcon = 0;
			player.requestUpdates();
		}
	}

}
