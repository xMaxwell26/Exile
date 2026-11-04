package com.koios.game.content.dialogue.impl.misc;

import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class PikkupstixDialogue extends Dialogue {

	public PikkupstixDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		setPhase(0);
		mobChat(6970, Expression.HAPPY_TALKING, "Hello " + player.getName() + "! My name is Pikkupstix, and", "I sell summoning supplies.", "What can I do for you?");
	}

	@Override
	protected void next() {
		if (getPhase() < 0) {
			stop();
			return;
		}

		if (isPhase(0)) {
			choice("Select an Option", "Open Shop (Level 1-55 Summoning)", "Open Shop (Level 55-99 Summoning)", "Never mind, I have to go.");
		}
	}

	@Override
	protected void select(int index) {
		if (index == 1) {
			GameService.getShopManager().open(player, 8);
		} else if (index == 2) {
			GameService.getShopManager().open(player, 9);
		} else {
			stop();
		}
	}

}
