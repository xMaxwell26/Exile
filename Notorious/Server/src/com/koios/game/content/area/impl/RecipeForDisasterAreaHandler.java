package com.koios.game.content.area.impl;

import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class RecipeForDisasterAreaHandler extends AreaHandler {

	public static final AreaBoundary AREA = new AreaBoundary(1885, 5336, 1925, 5370, -1, -1);

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 12356) {
			player.teleport(new Location(3097, 3486));
			player.setAttribute("culi", 0);
		}
	}

	@Override
	public AreaBoundary getArea() {
		return AREA;
	}

}
