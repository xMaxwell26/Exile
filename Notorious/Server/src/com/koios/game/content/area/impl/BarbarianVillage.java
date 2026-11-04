package com.koios.game.content.area.impl;

import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class BarbarianVillage extends AreaHandler {

	private static final AreaBoundary AREA = new AreaBoundary(3070, 3403, 3099, 3462);

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 16154) {
			player.setPosition(1860, 5244, 0);
			player.dialogue().start(new Dialogue(player) {

				@Override
				protected void start() {
					sendStatement("You squeeze through the hole and find a ladder a few feet down", "leading into the Stronghold of Security.");
				}

			});
		}
	}

	@Override
	public AreaBoundary getArea() {
		return AREA;
	}

}
