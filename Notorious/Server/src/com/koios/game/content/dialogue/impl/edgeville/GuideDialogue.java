package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class GuideDialogue extends Dialogue {

	public static final int TUTORIAL_NPC = 945;

	public GuideDialogue(Player player) {
		super(player);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			choice("Select an Option", "Sure!", "No thanks.");
			setPhase(1);
		} else {
			stop();
		}
	}

	@Override
	public void select(int index) {
		if (index == 1) {
			player.dialogue().start(new TutorialDialogue(player));
		} else if (index == 2) {
			stop();
		}
	}

	@Override
	protected void start() {
		mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, "Hello, " + player.getName() + "!", "Would you like to watch the tutorial?");
	}

}
