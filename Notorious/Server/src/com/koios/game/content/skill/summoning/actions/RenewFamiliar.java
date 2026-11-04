package com.koios.game.content.skill.summoning.actions;

import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData.SummonPouch;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Renews the familiar time and hitpoints.
 *
 * @author Gabriel | Wolfs Darker
 */
public class RenewFamiliar {

	public static void execute(Player player) {
		SummonPouch spouch = player.getSummoning().getFamiliar().getPouchDefinitions();
		if (player.getSkill().getLevel(Skill.SUMMONING) < spouch.getPointsReq()) {
			player.sendMessage("You don't have enough summoning points to rewew your familiar.");
			return;
		}
		if (!player.getInventory().contains(spouch.getPouchId())) {
			player.sendMessage("You need a " + new Item(spouch.getPouchId()).getDefinition().getName() + " to renew your familiar.");
			return;
		}
		if (player.getSummoning().getRemainingTicks() > 5) {
			player.sendMessage("You need wait untill there is only 2 minutes and half left to renew your familiar.");
			return;
		}
		player.getInventory().remove(new Item(spouch.getPouchId(), 1));
		player.getSummoning().setRemainingTicks(spouch.getDuration());
		player.getSummoning().getFamiliar().setHitpoints(player.getSummoning().getFamiliar().getMaxHitpoints());
		player.sendMessage("You renew your familiar.");
		player.getSummoning().sendTab(false);
	}
}
