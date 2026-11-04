package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class TzhaarDialogue extends Dialogue {

	public TzhaarDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		mobChat(2611, Expression.HAPPY_TALKING, "Be carefull with TzTok-Jad...", "And good luck, you will need it.");
		setPhase(0);
	}

}
