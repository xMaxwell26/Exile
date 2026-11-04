package com.koios.game.content.skill.thieving;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import com.koios.Constants;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class Thieving {

	private final Player player;

	public Thieving(Player player) {
		this.player = Objects.requireNonNull(player);
	}

	private static final int[] Runes2 = { 555, 557, 560, 565, 9075, 892 };
	private static final int[] THIEVING_STALL_LOOT_TABLE = { 1755, 1891, 1623, 1621 };

	public static int getRandomRuneType() {
		return Runes2[NumberUtil.inclusiveRandom(5)];
	}

	public static int getRandomThievingStallLoot() {
		return THIEVING_STALL_LOOT_TABLE[NumberUtil.inclusiveRandom(3)];
	}

	public void stealFromStall(int id, int amount, int xp, int requiredLevel) {
		if (System.currentTimeMillis() - player.lastThieve < 2500) {
			return;
		}

		if (player.getSkill().getLevel(Skill.THIEVING) >= requiredLevel) {
			int freeSlots = player.getInventory().freeSlots();

			if (freeSlots >= 2 || freeSlots == 1 && player.getInventory().contains(ItemIdentifiers.COINS)) {
				int item = NumberUtil.inclusiveRandom(3);

				if (item == 0) {
					player.getInventory().add(new Item(getRandomThievingStallLoot()));
				} else if (item == 1) {
					player.getInventory().add(new Item(getRandomRuneType(), NumberUtil.inclusiveRandom(99) + 1));
				}

				if (player.getInventory().add(new Item(id, amount))) {
					player.getSkill().addExperience(Skill.THIEVING, xp * Constants.THIEVING_EXPERIENCE);
					player.lastThieve = System.currentTimeMillis();
					player.playAnimation(new Animation(832));
					player.sendMessage("You steal " + NumberFormat.getInstance(Locale.US).format(amount) + " coins.");
					AchievementManager.performed(player, Achievement.THIEVE_STALL);
				}
			} else {
				player.sendMessage("You do not have enough inventory space to steal from this stall.");
			}
		} else {
			player.sendMessage("You need a Thieving level of " + requiredLevel + " to steal from this stall.");
		}
	}

}
