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
 * Handles the interactions for the third floor of the Stronghold of Security
 *
 * @author Timothy Howard <Arithium>
 */
public class SecurityStrongholdFloorThree extends AreaHandler {

	/**
	 * Individual coords for the doors that move you west
	 */
	private static final int[][] MOVE_WEST = { { 2127, 5288 }, { 2127, 5287 }, { 2124, 5288 }, { 2124, 5287 }, { 2138, 5295 }, { 2138, 5294 }, { 2141, 5295 }, { 2141, 5294 },
			{ 2171, 5272 }, { 2171, 5271 }, { 2168, 5271 }, { 2168, 5272 }, { 2157, 5263 }, { 2157, 5264 }, { 2154, 5264 }, { 2154, 5263 }, { 2141, 5262 }, { 2141, 5263 },
			{ 2138, 5263 }, { 2138, 5262 }, { 2153, 5291 }, { 2153, 5292 }, { 2149, 5292 }, { 2149, 5291 }, };

	/**
	 * Individual coords for the doors that move you south
	 */
	private static final int[][] MOVE_SOUTH = { { 2132, 5260 }, { 2133, 5260 }, { 2132, 5257 }, { 2133, 5257 }, { 2130, 5293 }, { 2131, 5293 }, { 2130, 5296 }, { 2131, 5296 },
			{ 2149, 5299 }, { 2148, 5299 }, { 2149, 5302 }, { 2148, 5302 }, { 2167, 5297 }, { 2168, 5297 }, { 2167, 5294 }, { 2168, 5294 }, { 2167, 5262 }, { 2166, 5262 },
			{ 2163, 5275 }, { 2164, 5275 }, { 2163, 5278 }, { 2164, 5278 }, { 2156, 5286 }, { 2155, 5286 }, { 2156, 5289 }, { 2155, 5289 }, };

	/**
	 * Individual coords for the doors that move you east
	 */
	private static final int[][] MOVE_EAST = { { 2137, 5263 }, { 2137, 5262 }, { 2170, 5272 }, { 2170, 5271 }, { 2167, 5272 }, { 2167, 5271 }, { 2156, 5264 }, { 2156, 5263 },
			{ 2153, 5264 }, { 2153, 5263 }, { 2140, 5263 }, { 2140, 5262 }, { 2152, 5292 }, { 2152, 5291 }, { 2148, 5292 }, { 2148, 5291 }, { 1999, 5216 }, { 1999, 5215 },
			{ 2008, 5216 }, { 2008, 5215 }, };

	/**
	 * The {@link AreaBoundary} for this location
	 */
	public static final AreaBoundary AREA = new AreaBoundary(2110, 5245, 2210, 5345);

	@Override
	public AreaBoundary getArea() {
		return AREA;
	}

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 16114) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2026, 5217, 0);
					stop();
				}
			});
		} else if (id == 16089 || id == 16090) {
			for (int[] i : MOVE_WEST) {
				if (player.getX() == i[0] && player.getY() == i[1]) {
					player.setPosition(player.getX() - 1, player.getY(), player.getZ());
					return;
				}
			}

			for (int[] i : MOVE_EAST) {
				if (player.getX() == i[0] && player.getY() == i[1]) {
					player.setPosition(player.getX() + 1, player.getY(), player.getZ());
					return;
				}
			}

			for (int[] i : MOVE_SOUTH) {
				if (player.getX() == i[0] && player.getY() == i[1]) {
					int distance = 1;
					if (!player.getLocation().equals(location)) {
						distance = 2;
					}
					player.setPosition(player.getX(), player.getY() - distance, player.getZ());
					return;
				}
			}

			int x = 0;
			int y = 0;
			if (player.getX() == location.getX() && player.getY() == location.getY()) {
				y = 1;
			} else if (player.getY() == location.getY() && player.getX() < location.getX()) {
				x = 1;
			} else if (player.getY() > location.getY() && player.getX() == location.getX()) {
				y = -2;
			} else if (player.getY() < location.getY() && player.getX() == location.getX()) {
				y = 1;
			}
			player.setPosition(player.getX() + x, player.getY() + y, player.getZ());
		} else if (id == 16116) {
			if (player.getSkill().getCombatLevel(false) >= 60 || player.getAttribute("stronghold_chest", 0) >= 3) {
				player.setPosition(2142, 5284, 0);
			} else {
				player.sendMessage("You are not yet strong enough to use this portal.");
			}
		} else if (id == 16112) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2122, 5251, 0);
					stop();
				}
			});
		} else if (id == 16118) {
			if (player.getAttribute("stronghold_chest", 0) >= 3) {
				player.sendMessage("You have already claimed this reward.");
			} else if (player.getAttribute("stronghold_chest", 0) < 2) {
				player.sendMessage("You have not earned this reward yet.");
			} else {
				player.dialogue().start(new Dialogue(player) {

					@Override
					protected void start() {
						sendStatement("The box hinges creak and appear to form audible words...");
					}

					@Override
					public void next() {
						if (player.getAttribute("stronghold_chest", 0) == 2) {
							sendStatement("....congratulations adventurer, you have been deemed worth of this", "reward.");
							player.getInventory().add(new Item(995, 300_000));
							player.setAttribute("stronghold_chest", 3);
						} else {
							stop();
						}
					}

				});
			}
		} else if (id == 16115) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2358, 5215, 0);
					stop();
				}
			});
		}
	}

}
