package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.content.minigames.misc.TzhaarFightCave;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class KilnDialogue extends Dialogue {

	public KilnDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		if (player.getMinigame().isKilnFeePaid()) {
			player.getMinigame().enterMinigame(new TzhaarFightCave(player, true, false));
			return;
		}

		if (!player.getInventory().contains(6570)) {
			mobChat(2611, Expression.HAPPY_TALKING, "You cannot enter without a Firecape to sacrifice.");
			setPhase(-1);
			return;
		}

		mobChat(2611, Expression.HAPPY_TALKING, "Would you like to sacrifice your Firecape and enter the kiln?");
		setPhase(0);
	}

	@Override
	protected void next() {
		if (getPhase() < 0) {
			stop();
			return;
		}

		if (isPhase(0)) {
			choice("Would you?", "Yes", "No");
		}
	}

	@Override
	protected void select(int index) {
		if (index == 1) {
			if (!player.getInventory().contains(6570)) {
				mobChat(2611, Expression.HAPPY_TALKING, "You cannot enter without a Firecape to sacrifice.");
				stop();
				return;
			}

			player.getInventory().remove(new Item(6570));
			player.getMinigame().setKilnFee(true);
			player.getMinigame().enterMinigame(new TzhaarFightCave(player, true, false));
			stop();
		} else {
			stop();
		}
	}

}
