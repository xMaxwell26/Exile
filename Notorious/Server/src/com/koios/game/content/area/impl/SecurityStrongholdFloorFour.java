package com.koios.game.content.area.impl;

import com.koios.GameService;
import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.AreaHandler;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class SecurityStrongholdFloorFour extends AreaHandler {

	private static final int[][] MOVE_WEST = { { 2356, 5245 }, { 2356, 5246 }, { 2353, 5245 }, { 2353, 5246 }, { 2336, 5238 }, { 2336, 5237 }, { 2333, 5238 }, { 2333, 5237 },
			{ 2333, 5226 }, { 2333, 5227 }, { 2309, 5205 }, { 2309, 5204 }, { 2312, 5205 }, { 2312, 5204 }, { 2318, 5187 }, { 2318, 5186 }, { 2315, 5187 }, { 2315, 5186 },
			{ 2333, 5194 }, { 2333, 5193 }, { 2336, 5194 }, { 2336, 5193 }, { 2344, 5188 }, { 2344, 5187 }, { 2347, 5188 }, { 2347, 5187 }, { 2359, 5189 }, { 2359, 5188 },
			{ 2362, 5189 }, { 2362, 5188 }, { 2360, 5193 }, { 2360, 5194 }, { 2363, 5193 }, { 2363, 5194 }, };

	private static final int[][] MOVE_SOUTH = { { 2359, 5232 }, { 2360, 5232 }, { 2359, 5236 }, { 2360, 5236 }, { 2359, 5239 }, { 2360, 5235 }, { 2324, 5243 }, { 2323, 5243 },
			{ 2324, 5240 }, { 2323, 5240 }, { 2341, 5225 }, { 2340, 5225 }, { 2340, 5222 }, { 2341, 5222 }, { 2324, 5191 }, { 2323, 5191 }, { 2324, 5188 }, { 2323, 5188 },
			{ 2361, 5204 }, { 2362, 5204 }, { 2361, 5207 }, { 2362, 5207 }, };

	private static final int[][] MOVE_EAST = { { 2355, 5246 }, { 2355, 5245 }, { 2352, 5245 }, { 2352, 5246 }, { 2335, 5238 }, { 2335, 5237 }, { 2332, 5238 }, { 2332, 5237 }, };

	public static final AreaBoundary AREA = new AreaBoundary(2300, 5180, 2400, 5280);

	/**
	 * The ids for the boots
	 */
	private final int[] BOOT_IDS = { 9005, 9006 };

	@Override
	public AreaBoundary getArea() {
		return AREA;
	}

	@Override
	public void handleFirstClickObject(Player player, int id, Location location) {
		if (id == 16114) {
			player.setPosition(2026, 5217, 0);
		} else if (id == 16044 || id == 16043) {
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
		} else if (id == 16116) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2358, 5215, 0);
					stop();
				}
			});
		} else if (id == 16049) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2147, 5284, 0);
					stop();
				}
			});
		} else if (id == 16050) {
			if (player.getAttribute("stronghold_chest", 0) == 4 || player.getSkill().getCombatLevel(false) >= 80) {
				player.setPosition(2344, 5209, 0);
			} else {
				player.sendMessage("You are not strong enough to use this portal.");
			}
		} else if (id == 16048) {
			player.playAnimation(new Animation(828));
			GameService.getTaskScheduler().schedule(new Task(1) {

				@Override
				protected void execute() {
					player.setPosition(2358, 5215, 0);
					stop();
				}
			});
		} else if (id == 16047) {
			if (player.getInventory().freeSlots() < 1) {
				player.sendMessage("You do not have enough inventory space.");
				return;
			}
			if (player.getAttribute("stronghold_chest", 0) >= 3) {

				if (player.getAttribute("stronghold_chest", 0) == 3) {
					player.getInventory().add(new Item(995, 400_000));
					player.setAttribute("stronghold_chest", 4);
				}
				player.dialogue().start(new Dialogue(player) {

					@Override
					protected void start() {
						sendStatement("As your hand touches the cradle, you hear a voice in your head of", "a million dead adventurers...");
						player.setAttribute("ss_boots_id", -1);
						setPhase(0);
					}

					@Override
					public void next() {
						if (isPhase(0)) {
							if (inInventory(player)) {
								sendStatement("Would you like to swap your boots for the other style?");
								setPhase(8);
							} else if (containsBoots(player)) {
								sendStatement("Hold the other pair of boots in your inventory to change the style.");
								setPhase(7);
							} else {
								sendStatement("...welcome adventurer.... you have a choice...");
								setPhase(1);
							}
						} else if (isPhase(1)) {
							sendItemChat("", "", "You can choose between these 2 pair of boots.", "", "", BOOT_IDS[NumberUtil.inclusiveRandom(1)], 200);
							setPhase(2);
						} else if (isPhase(2)) {
							sendStatement("They will protect your feet exactly the same, however they look", "very different. You can always come back and get another pair if",
									"you lose them, or even swap them for the other style!");
							setPhase(3);
						} else if (isPhase(3)) {
							choice("Choose your style of boots", "I'll take the colourful ones!", "I'll take the fighting ones!");
						} else if (isPhase(4)) {
							player.getInventory().add(new Item(BOOT_IDS[0]));
							setPhase(player.getAttribute("stronghold_chest", 0) == 3 ? 6 : 7);
							next();
						} else if (isPhase(5)) {
							player.getInventory().add(new Item(BOOT_IDS[1]));
							setPhase(player.getAttribute("stronghold_chest", 0) == 3 ? 6 : 7);
							next();
						} else if (isPhase(6)) {
							sendStatement("Congratulations! You have successfully navigated the Stronghold of", "Security.");
							setPhase(7);
						} else if (isPhase(7)) {
							stop();
						} else if (isPhase(8)) {
							choice(null, "Yes", "No");
						}
					}

					@Override
					public void select(int index) {
						if (isPhase(3)) {
							if (index == 1) {
								chat(Expression.HAPPY_TALKING, "I'll take the colourful ones!");
								setPhase(4);
							} else if (index == 2) {
								chat(Expression.HAPPY_TALKING, "I'll take the fighting ones!");
								setPhase(5);
							}
						} else if (isPhase(8)) {
							int id = player.getAttribute("ss_boots_id", 0);
							player.getInventory().replace(id, id == BOOT_IDS[0] ? BOOT_IDS[1] : BOOT_IDS[0]);
							sendStatement("You have exchanged your boots for the other style!");
							setPhase(7);
						}
					}

				});
			} else {
				player.sendMessage("You have not yet earned this reward.");
			}
		}
	}

	/**
	 * Checks if the player has the boots in their inventory
	 *
	 * @param player The {@link Player} to check for boots
	 * @return If the player has boots in their inventory
	 */
	private boolean inInventory(Player player) {
		for (int i : BOOT_IDS) {
			if (player.getInventory().contains(i)) {
				player.setAttribute("ss_boots_id", i);
				return true;
			}
		}
		return false;
	}

	/**
	 * If the player does not have the boots in their inventory, check if they have them anywhere else
	 *
	 * @param player The {@link Player} To check for boots
	 * @return If the player has the boots anywhere else
	 */
	private boolean containsBoots(Player player) {
		for (int i : BOOT_IDS) {
			if (player.getEquipment().contains(i) || player.getBank().contains(i) || GameService.getGroundItemManager().playerHasItem(player, i)) {
				player.setAttribute("ss_boots_id", i);
				return true;
			}
		}
		return false;
	}

}
