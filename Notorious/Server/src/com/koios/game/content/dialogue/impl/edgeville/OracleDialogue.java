package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class OracleDialogue extends Dialogue {

	public OracleDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		if (isPhase(0)) {
			setPhase(1);
			mobChat(746, Expression.HAPPY_TALKING, "If a tree falls in the forest and no one is around, then", "nobody gets Woodcutting xp.");
		} else {
			stop();
		}
	}

	@Override
	protected void start() {
		chat(Expression.HAPPY_TALKING, "Can you impart your wise knowledge to me,", "O Oracle?");
	}

}
