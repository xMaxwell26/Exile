package com.koios.game.model.mob.impl;

import com.koios.GameService;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public final class StaticDropBroadcastStrategy {

	public static void broadcast(Mob mob, Player target, int distance, int id, int amount, String mobName) {
		/*
		 * Due to the nature of the mob API there is a chance this code is hit despite the mob having no valid killer, in which case we skip the local area notification.
		 */

		if (target != null) {
			String playerName = target.getName();
			String itemName = ItemDefinition.forId(id).getName();
			String result;

			// No need to express the amount of an item if there's only one.
			if (amount == 1) {
				result = "<col=006600>%s received: %s (%s)";
				result = String.format(result, playerName, itemName, mobName);
			} else {
				String amountFormatted = NumberUtil.format(amount);
				result = "<col=006600>%s received: %s x %s (%s)";
				result = String.format(result, playerName, amountFormatted, itemName, mobName);
			}

			GameService.getWorld().broadcast(result, p -> p.isWithinDistance(mob, distance));
		}
	}

	private StaticDropBroadcastStrategy() {
	}

}
