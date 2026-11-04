package com.koios.game.content.dialogue.impl.donators;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class ZamboCakeDialogue extends Dialogue {

	public ZamboCakeDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		if (isPhase(0)) {
			mobChat(568, Expression.HAPPY_TALKING, "Hrmpfff...", "Are you bringing me stolen cakes again?");
		} else if (isPhase(1)) {
			chat(Expression.HAPPY_TALKING, "*Ahem*", "Nope... Good bye, Zambo!");
		} else {
			stop();
		}
		setPhase(getPhase() + 1);
	}

	@Override
	protected void start() {
		chat(Expression.HAPPY_TALKING, "Hi, Zambo!", "I have this brilliant cake -");
	}

}
