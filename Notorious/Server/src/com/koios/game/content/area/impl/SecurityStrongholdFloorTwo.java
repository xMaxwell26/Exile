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
public class SecurityStrongholdFloorTwo extends AreaHandler {

	private static final int[][] DOORS_LEFT = { { 2040, 5244 }, { 2040, 5245 }, { 2037, 5245 }, { 2037, 5244 }, { 2019, 5227 }, { 2019, 5228 }, { 2034, 5186 }, { 2034, 5185 },
			{ 2043, 5223 }, { 2043, 5222 }, { 2037, 5186 }, { 2037, 5185 }, { 2009, 5215 }, { 2009, 5216 }, { 2006, 5216 }, { 2006, 5215 }, { 2000, 5216 }, { 2000, 5215 },
			{ 1997, 5216 }, { 1997, 5215 }, { 2040, 5223 }, { 2040, 5222 }, { 2043, 5223 }, { 2043, 5222 }, { 2016, 5228 }, { 2016, 5227 }, };

	private static final int[][] DOORS_SOUTH = { { 2026, 5239 }, { 2027, 5239 }, { 2026, 5242 }, { 2027, 5242 }, { 2020, 5240 }, { 2019, 5240 }, { 2014, 5240 }, { 2013, 5240 },
			{ 2005, 5235 }, { 2006, 5236 }, { 2034, 5208 }, { 2023, 5208 }, { 2032, 5196 }, { 2031, 5196 }, { 2045, 5195 }, { 2046, 5195 }, { 2045, 5237 }, { 2044, 5237 },
			{ 2037, 5201 }, { 2036, 5201 }, { 2020, 5200 }, { 2021, 5200 }, { 1994, 5194 }, { 1995, 5194 }, { 2004, 5192 }, { 2005, 5192 }, { 2006, 5235 }, { 2005, 5235 },
			{ 2032, 5225 }, { 2031, 5225 }, };

	private static final int[][] DOORS_EAST = { { 2018, 5227 }, { 2018, 5228 }, { 2036, 5186 }, { 2036, 5185 }, { 2042, 5223 }, { 2042, 5222 }, { 2039, 5244 }, { 2039, 5245 }, };

	/**
	 * The area bounds of the floor
	 */
	public static final AreaBoundary AREA = new AreaBoundary(1980, 5180, 2050, 5250);

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 16080) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(new Location(1902, 5221));
					stop();
				}

			});
		} else if (id == 16078) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2042, 5245, 0);
					stop();
				}

			});
		} else if (id == 16066 || id == 16065) {

			for (int[] i : DOORS_LEFT) {
				if (player.getX() == i[0] && player.getY() == i[1]) {
					player.setPosition(player.getX() - 1, player.getY(), player.getZ());
					return;
				}
			}

			for (int[] i : DOORS_EAST) {
				if (player.getX() == i[0] && player.getY() == i[1]) {
					player.setPosition(player.getX() + 1, player.getY(), player.getZ());
					return;
				}
			}

			for (int[] i : DOORS_SOUTH) {
				if (player.getX() == i[0] && player.getY() == i[1]) {
					player.setPosition(player.getX(), player.getY() - 1, player.getZ());
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
				y = -1;
			} else if (player.getY() < location.getY() && player.getX() == location.getX()) {
				y = 1;
			}
			player.setPosition(player.getX() + x, player.getY() + y, player.getZ());
		} else if (id == 16082) {
			if (player.getSkill().getCombatLevel(false) >= 40 || player.getAttribute("stronghold_chest", 0) >= 2) {
				player.setPosition(2017, 5216, 0);
			} else {
				player.sendMessage("You are not yet strong enough to use this portal.");
			}
		} else if (id == 16077) {
			if (player.getAttribute("stronghold_chest", 0) == 0) {
				player.sendMessage("You have not yet unlocked this reward.");
			} else if (player.getAttribute("stronghold_chest", 0) >= 2) {
				player.sendMessage("You have already claimed this reward.");
			} else {
				player.dialogue().start(new Dialogue(player) {

					@Override
					protected void start() {
						sendStatement("The grain shifts in the sack, sighing audible words....");
					}

					@Override
					public void next() {
						if (player.getAttribute("stronghold_chest", 0) == 1) {
							sendStatement("....congratulations adventurer, you have been deemed worth of this", "reward.");
							player.getInventory().add(new Item(995, 200_000));
							player.setAttribute("stronghold_chest", 2);
						} else {
							stop();
						}
					}

				});
			}
		} else if (id == 16081) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2122, 5251, 0);
					stop();
				}
			});
		} else if (id == 16048) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2122, 5251, 0);
					stop();
				}
			});
		}
	}

	@Override
	public AreaBoundary getArea() {
		return AREA;
	}

}
