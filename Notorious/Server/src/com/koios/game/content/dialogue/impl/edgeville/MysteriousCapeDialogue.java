package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class MysteriousCapeDialogue extends Dialogue {

	public static boolean canWearCompletionistCape(Player player, boolean verbose) {
		final boolean killsRequirement = player.killCount >= 100;
		boolean skillRequirement = true;

		for (int i = 0; i < 23; ++i) {
			if (i == Skill.CONSTRUCTION) {
				continue;
			}

			final int level = player.getSkill().getLevelForExperience(i);

			if (level < 99) {
				if (skillRequirement) {
					if (verbose) {
						skillRequirement = false;
						player.sendMessage("To wear this you need level 99 in the following:");
					} else {
						return false;
					}
				}

				player.sendMessage(Skill.SKILL_NAME[i]);
			}
		}

		if (verbose && !killsRequirement) {
			player.sendMessage("You need to kill " + (100 - player.killCount) + " more players in PvP situations.");
		}

		return killsRequirement && skillRequirement;
	}

	public static boolean canWearMaxCape(Player player, boolean verbose) {
		boolean skillRequirement = true;

		for (int i = 0; i < 23; ++i) {
			if (i == Skill.CONSTRUCTION) {
				continue;
			}

			final int level = player.getSkill().getLevelForExperience(i);

			if (level < 99) {
				if (skillRequirement) {
					if (verbose) {
						skillRequirement = false;
						player.sendMessage("To wear this you need level 99 in the following:");
					} else {
						return false;
					}
				}

				player.sendMessage(Skill.SKILL_NAME[i]);
			}
		}

		return skillRequirement;
	}

	public MysteriousCapeDialogue(Player player) {
		super(player);
	}

	private boolean confirmPurchase(int capeId, int hoodId, int cost) {
		final int coins = player.getInventory().getCount(995);
		final int slot = player.getInventory().getSlotById(995);

		if (cost > coins) {
			return false;
		}

		if (coins == cost) {
			player.getInventory().get(slot).transform(0);
			player.getInventory().get(slot).setCount(0);
		} else if (player.getInventory().freeSlots() <= 1) {
			return false;
		} else {
			player.getInventory().get(slot).setCount(player.getInventory().get(slot).getCount() - cost);
		}

		player.getInventory().add(new Item(hoodId));
		player.getInventory().add(new Item(capeId));
		return true;
	}

	@Override
	protected void next() {
		if (isPhase(1) || isPhase(2)) {
			choice("Select an Option", "Yes!", "No.");
		} else {
			stop();
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				if (canWearMaxCape(player, true)) {
					setPhase(1);
					sendStatement("Are you sure you want to spend 20,000,000 coins on this?");
				} else {
					stop();
				}
			} else if (index == 2) {
				if (canWearCompletionistCape(player, true)) {
					setPhase(2);
					sendStatement("Are you sure you want to spend 50,000,000 coins on this?");
				} else {
					stop();
				}
			} else if (index == 3) {
				stop();
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				if (confirmPurchase(20767, 20768, 20000000)) {
					player.sendMessage("You've purchased a max cape for 20,000,000 coins.");
					AchievementManager.performed(player, Achievement.PURCHASE_MAX_CAPE);
					stop();
				} else if (player.getInventory().freeSlots() <= 1) {
					setPhase(3);
					sendStatement("You do not have enough inventory space to do this.");
				} else {
					setPhase(3);
					sendStatement("Maybe I should come back when I have that kind of money with me.");
				}
			} else if (index == 2) {
				stop();
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				if (confirmPurchase(20748, 20770, 50000000)) {
					player.sendMessage("You've purchased a completionist cape for 50,000,000 coins.");
					AchievementManager.performed(player, Achievement.OBTAIN_COMPLETIONIST_CAPE);
					stop();
				} else if (player.getInventory().freeSlots() <= 1) {
					setPhase(3);
					sendStatement("You do not have enough inventory space to do this.");
				} else {
					setPhase(3);
					sendStatement("Maybe I should come back when I have that kind of money with me.");
				}
			} else if (index == 2) {
				stop();
			}
		}
	}

	@Override
	protected void start() {
		choice("Select an Option", "Purchase max cape", "Purchase completionist cape", "Nevermind");
	}

}
