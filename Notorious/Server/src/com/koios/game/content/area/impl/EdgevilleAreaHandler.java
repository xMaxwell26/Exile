package com.koios.game.content.area.impl;

import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.content.minigames.rfd.GlovesDialogue;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class EdgevilleAreaHandler extends AreaHandler {

	private final AreaBoundary edgevilleArea = new AreaBoundary(3069, 3453, 3128, 3522, 0, 4);

	@Override
	public AreaBoundary getArea() {
		return edgevilleArea;
	}

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 11356) {
			player.castTeleport(new Location(2837, 10209, 0));
		} else if (id == 12356) {
			if (!player.dialogue().isActive())
				player.dialogue().start(new GlovesDialogue(player, player.rfdGloveCount == 0 ? 0 : 3));
		} else if (id == 5947) {
			player.setPosition(new Location(3297, 9824, 0));
			player.sendMessage("You climb down the hole...");
		}

	}

}
