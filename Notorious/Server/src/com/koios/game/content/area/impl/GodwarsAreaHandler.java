package com.koios.game.content.area.impl;

import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * Handles the godwars area
 *
 * @author Timothy Howard <Arithium>
 */
public class GodwarsAreaHandler extends AreaHandler {

	/**
	 * The amount required to enter a godwars room
	 */
	public static final int KILLCOUNT_REQUIRED = 20;

	public static final int BANDOS = 0;

	public static final int ARMADYL = 3;

	public static final int ZAMORAK = 1;

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 3205) {
			if (player.distanceToPoint(2914, 5300) < 5)
				player.teleport(new Location(2914, 5300, 1));
			else
				player.teleport(new Location(2919, 5274));
		} else if (id == 2282) {
			if (player.getY() <= location.getY())
				player.teleport(new Location(2885, 5344, 2));
			else
				player.teleport(new Location(location.getX(), location.getY(), 2));
		}

	}

	/**
	 * The area for godwars
	 */
	private final AreaBoundary godwarsArea = new AreaBoundary(2800, 5200, 2950, 5400, 0, 3);

	@Override
	public AreaBoundary getArea() {
		return godwarsArea;
	}

}
