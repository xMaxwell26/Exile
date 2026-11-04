package com.koios.game.content.area.impl;

import com.koios.GameService;
import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handles object clicking for the first floor of the Stronghold of Security
 *
 * @author Timothy Howard <Arithium>
 */
public class SecurityStrongholdFloorOne extends AreaHandler {

	/**
	 * The area bounds of the floor
	 */
	public static final AreaBoundary AREA = new AreaBoundary(1855, 5180, 1920, 5250);

	private final int a2DimArray[][] = { { 1865, 5227 }, { 1865, 5226 }, { 1868, 5226 }, { 1868, 5227 }, { 1867, 5217 }, { 1867, 5218 }, { 1870, 5217 }, { 1870, 5218 },
			{ 1894, 5213 }, { 1894, 5212 }, { 1897, 5213 }, { 1897, 5212 }, { 1904, 5203 }, { 1904, 5204 }, { 1907, 5203 }, { 1907, 5204 }, { 1882, 5188 }, { 1882, 5189 },
			{ 1879, 5189 }, { 1879, 5188 }, { 1879, 5240 }, { 1879, 5239 }, { 1876, 5240 }, { 1876, 5239 }, { 1884, 5244 }, { 1884, 5243 }, { 1887, 5244 }, { 1887, 5243 },
			{ 1889, 5235 }, { 1889, 5236 }, { 1886, 5235 }, { 1886, 5236 }, { 1904, 5242 }, { 1904, 5243 }, { 1908, 5242 }, { 1908, 5243 }

	};

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 16148) {
			if (location.getX() == 1859) {
				player.setPosition(new Location(3081, 3421, 0));
			} else {
				player.playAnimation(new Animation(828));
				GameService.getTaskScheduler().schedule(new Task(1) {

					@Override
					protected void execute() {
						player.setPosition(new Location(1860, 5244));
						stop();
					}

				});
			}
		} else if (id == 16150) {
			if (player.getSkill().getCombatLevel(false) >= 20 || player.getAttribute("stronghold_chest", 0) > 0) {
				player.setPosition(new Location(1905, 5217));
			} else {
				player.sendMessage("You are not yet strong enough to enter the portal.");
			}
		} else if (id == 16135) {
			if (player.getAttribute("stronghold_chest", 0) == 0) {
				player.dialogue().start(new Dialogue(player) {

					@Override
					protected void start() {
						sendStatement("The box hinges creak and appear to be forming audible words....");
					}

					@Override
					public void next() {
						if (isPhase(0) && player.getAttribute("stronghold_chest", 0) == 0) {
							if (player.getInventory().freeSlots() < 1) {
								player.sendMessage("You do not have enough inventory space.");
							} else {
								sendStatement("...congratulations adventurer, you have been deemed worth of this", "reward.");
								player.getInventory().add(new Item(995, 100_000));
								player.setAttribute("stronghold_chest", 1);
							}
						} else {
							stop();
						}
					}

				});
			} else {
				player.sendMessage("You have already claimed this reward.");
			}
		} else if (id == 16149) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(new Location(2042, 5245));
					stop();
				}

			});
		} else if (id == 16124 || id == 16123) {
			for (int[] element : a2DimArray) {
				if (player.getX() == element[0] && player.getY() == element[1]) {
					player.setPosition(player.getX() - 1, player.getY(), player.getZ());
					return;
				}
			}
			if (player.getX() == 1890 && player.getY() == 5208 || player.getX() == 1889 && player.getY() == 5208 || player.getX() == 1876 && player.getY() == 5195
					|| player.getX() == 1877 && player.getY() == 5195 || player.getX() == 1876 && player.getY() == 5192 || player.getX() == 1877 && player.getY() == 5192) {
				player.setPosition(player.getX(), player.getY() - 1, player.getZ());
				return;
			}
			int x = 0;
			int y = 0;
			if (player.getX() == location.getX() && player.getY() == location.getY()) {
				y = 1;
			} else if (player.getY() == location.getY() && player.getX() < location.getX()) {
				x = 1;
			} else if (player.getY() > location.getY() && player.getX() == location.getX()) {
				y = -1;
			} else if (player.getY() < location.getY() && player.getX() == location.getX()) {
				y = 1;
			}
			player.setPosition(player.getX() + x, player.getY() + y, player.getZ());

		} else if (id == 16152) {
			player.dialogue().start(new Dialogue(player) {

				@Override
				protected void start() {
					sendStatement("Yep, hes dead...");
				}

			});
		}
	}

	@Override
	public AreaBoundary getArea() {
		return AREA;
	}

}
