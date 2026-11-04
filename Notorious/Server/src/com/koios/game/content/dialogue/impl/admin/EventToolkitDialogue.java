package com.koios.game.content.dialogue.impl.admin;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.mob.impl.event.hideandseek.MobHideAndSeekHelper;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.util.ItemIdentifiers;

public final class EventToolkitDialogue extends Dialogue {

	public EventToolkitDialogue(Player player) {
		super(player);
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				setPhase(1);
				choice("Hide and Seek", "Spawn helper", "Destroy existing helpers");
			} else if (index == 3) {
				setPhase(0xBABE);
				sendStatement("Let the development team know if you have any ideas!");
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				MobHideAndSeekHelper helper = new MobHideAndSeekHelper(player.getLocation().copy(), player);
				MobManager.spawn(helper);
				player.sendMessage("You've summoned a hide and seek helper!");
				helper.speak("You've summoned me, " + player.getName() + "!");
				stop();
			} else if (index == 2) {
				setPhase(2);
				choice("Destroy Helpers?", "Yes", "No");
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				for (int i = 1; i < MobManager.mobs.length; i++) {
					if (MobManager.mobs[i] == null || MobManager.mobs[i].getClass() != MobHideAndSeekHelper.class) {
						continue;
					}

					MobHideAndSeekHelper helper = (MobHideAndSeekHelper) MobManager.mobs[i];

					if (helper.isOwner(player)) {
						helper.setDead(true);
					}
				}
			}

			stop();
		}
	}

	@Override
	protected void next() {
		stop();
	}

	@Override
	protected void start() {
		if (player.getPrivilege().inferiorTo(RegularPrivilege.ADMINISTRATOR)) {
			player.getInventory().remove(Item.of(ItemIdentifiers.EVENT_TOOLKIT));
			player.sendMessage("Too late - it's gone!");
			stop();
			return;
		}

		choice("Select an Option", "Hide and Seek", "", "Placeholder for future expansions.");
	}

}
