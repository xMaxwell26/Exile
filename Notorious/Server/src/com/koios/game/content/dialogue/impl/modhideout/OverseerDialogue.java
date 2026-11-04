package com.koios.game.content.dialogue.impl.modhideout;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.util.ItemIdentifiers;

public final class OverseerDialogue extends Dialogue {

	public OverseerDialogue(Player player) {
		super(player);
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				if (player.isSuperAdministrator()) {
					player.getInventory().add(Item.of(ItemIdentifiers.ROTTEN_POTATO));
				} else {
					player.sendMessage("Access denied.");
				}
			} else if (index == 2) {
				if (player.isSuperAdministrator()) {
					player.getInventory().add(Item.of(ItemIdentifiers.SHABBY_BOOK));
				} else {
					player.sendMessage("Access denied.");
				}
			} else if (index == 3) {
				if (player.getPrivilege().inferiorTo(RegularPrivilege.ADMINISTRATOR)) {
					player.sendMessage("Access denied.");
				} else {
					player.getInventory().add(Item.of(ItemIdentifiers.EVENT_TOOLKIT));
				}
			}
		}

		stop();
	}

	@Override
	protected void start() {
		choice("Grab Item", "Rotten potato!", "Shabby book!", "Event toolkit!");
	}

}
