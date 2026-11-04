package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.content.skill.summoning.SummoningData.SummonPouch;
import com.koios.game.model.player.Player;

/**
 * Handle the familiar interaction dialogue.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class FamiliarInteraction extends Dialogue {

	public FamiliarInteraction(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		if (player.getSummoning().getFamiliar() == null) {
			return;
		}

		SummonPouch pouch = player.getSummoning().getFamiliar().getPouchDefinitions();

		if (pouch.getInteraction() == null || pouch.getInteraction().length == 0) {
			return;
		}

		int length = pouch.getInteraction().length;

		if (length == 1) {
			mobChat(pouch.getFamiliarId(), Expression.HAPPY_TALKING, pouch.getInteraction()[0]);
		} else if (length == 2) {
			mobChat(pouch.getFamiliarId(), Expression.HAPPY_TALKING, pouch.getInteraction()[0], pouch.getInteraction()[1]);
		} else if (length == 3) {
			mobChat(pouch.getFamiliarId(), Expression.HAPPY_TALKING, pouch.getInteraction()[0], pouch.getInteraction()[1], pouch.getInteraction()[2]);
		}
	}

	@Override
	protected void next() {
		stop();
	}

}
