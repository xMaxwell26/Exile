package com.koios.network.out.impl;

import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendMapRegionPacket implements OutboundPacket {

	private final Location location;

	public SendMapRegionPacket(Player player) {
		location = player.getLocation();
	}

	@Override
	public void execute(Session client) {
        if(com.koios.game.arpg.MapRuns.sendRegion(client.getPlayer()))return;
		client.getPlayer().getCurrentRegion().setAs(client.getPlayer().getLocation());
		client.getPlayer().setPlacementNeeded(true);
		client.getPlayer().write(new GamePacketBuilder(73).writeShortA(location.getRegionX() + 6).writeShort(location.getRegionY() + 6));
	}

}
