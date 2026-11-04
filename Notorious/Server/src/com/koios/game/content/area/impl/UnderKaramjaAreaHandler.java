package com.koios.game.content.area.impl;

import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class UnderKaramjaAreaHandler extends AreaHandler {

	private static final AreaBoundary area = new AreaBoundary(2824, 9545, 2877, 9662);

	@Override
	public AreaBoundary getArea() {
		return area;
	}

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 2606) {
			int offset = player.getY() >= location.getY() ? -1 : 1;
			if (offset == 1 && !player.isDonator()) {
				player.dialogue().start(new Dialogue(player) {

					@Override
					protected void start() {
						sendStatement("It costs 500k coins to pass. Would you like to pay to", "fight nomad?", "Did you know? Donators get through free.");
					}

					@Override
					public void next() {
						if (isPhase(0)) {
							choice(null, "Pay 500k To Pass", "Nevermind");
						} else if (isPhase(1)) {
							stop();
						}
					}

					@Override
					public void select(int index) {
						if (isPhase(0)) {
							switch (index) {
							case 1:
								if (player.getInventory().contains(new Item(995, 500_000))) {
									player.getInventory().remove(new Item(995, 500_000));
									player.setPosition(2836, player.getY() + 1, player.getZ());
								} else {
									sendStatement("You do not have enough gold to pass.");
									setPhase(1);
									return;
								}
								break;
							case 2:
								stop();
								break;
							}
						}
					}

				});
			} else {
				player.setPosition(2836, player.getY() + offset, player.getZ());
			}
		}
	}

}
