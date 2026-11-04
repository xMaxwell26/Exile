package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.skill.summoning.actions.DismissFamiliar;
import com.koios.game.model.player.Player;

public class SummoningDismiss extends Dialogue {

	public SummoningDismiss(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		choice("Dismiss familiar?", "Yes", "No");
		setPhase(0);
	}

	@Override
	protected void select(int index) {
		if (index == 1) {
			DismissFamiliar.execute(player);
		}

		stop();
	}

}
