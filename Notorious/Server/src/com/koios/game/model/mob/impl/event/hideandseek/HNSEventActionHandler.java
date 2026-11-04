package com.koios.game.model.mob.impl.event.hideandseek;

import com.koios.game.content.dialogue.impl.misc.HNSHelperDialogue;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

public final class HNSEventActionHandler {

	public static void click(Player player, Mob mob) {
		if (mob.getClass() != MobHideAndSeekHelper.class) {
			player.sendMessage("Nothing interesting happens.");
			return;
		}

		MobHideAndSeekHelper helper = (MobHideAndSeekHelper) mob;

		if (helper.isOwner(player)) {
			player.dialogue().start(new HNSHelperDialogue(player, helper));
		} else {
			helper.claim(player);
		}
	}

	public static void addCoffers(Player player, Mob mob, int slot) {
		if (mob.getClass() != MobHideAndSeekHelper.class) {
			player.sendMessage("Nothing interesting happens.");
			return;
		}

		MobHideAndSeekHelper helper = (MobHideAndSeekHelper) mob;

		if (helper.isOwner(player) || player.isSuperAdministrator()) {
			Item item = player.getInventory().get(slot);

			if (item == null) {
				player.sendMessage("Error adding item.");
			} else if (helper.addItem(item)) {
				player.sendMessage("Added to coffer: " + item.getDefinition().getName());
				player.getInventory().set(slot, null);
			} else {
				player.sendMessage("This helper has been claimed. Please spawn a new helper.");
			}
		} else {
			player.sendMessage("Only the owner can add items to the coffer.");
		}
	}

}
