package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class WiseOldManDialogue extends Dialogue {

	public WiseOldManDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			choice("Select an option.", "Yes.", "No.");
			setPhase(1);
			break;

		default:
			stop();
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(1) && index == 1) {
			GameService.getShopManager().open(player, 81);
		} else {
			stop();
		}
	}

	@Override
	protected void start() {
		if (player.getSkill().hasA99()) {
			mobChat(2566, Expression.HAPPY_TALKING, "Oh, hello! Look at you, a bona fide master!", "Would you like to look at my capes?");
		} else {
			mobChat(2566, Expression.HAPPY_TALKING, "Come back when you've mastered a skill.");
			setPhase(-1);
		}
	}

}
