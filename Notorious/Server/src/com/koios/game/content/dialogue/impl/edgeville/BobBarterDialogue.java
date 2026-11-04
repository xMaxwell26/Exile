package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.content.skill.herblore.potions.PotionMixing;
import com.koios.game.model.player.Player;

/**
 * Bob Barter, decants herbs
 *
 * @author Mikey`
 */
public class BobBarterDialogue extends Dialogue {

	/**
	 * Bob barter NPC id
	 */
	private static final int BOB_BARTER = 6524;

	public BobBarterDialogue(Player player) {
		super(player);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			choice("Select an Option", "Who are you?", "Can you decant my potions?");
		} else if (isPhase(-1)) {
			stop();
		}
	}

	@Override
	public void select(final int index) {
		if (isPhase(0)) {
			if (index == 1) {
				mobChat(BOB_BARTER, Expression.HAPPY_TALKING, "I'm Bob Barter, I can decant your potions!");
				setPhase(-1);
			} else if (index == 2) {
				int decanted = PotionMixing.decantAll(player);
				mobChat(BOB_BARTER, Expression.HAPPY_TALKING, decanted == 0 ? "You do not have any potions to decant." : "I've decanted your potions, come back any time!");
				setPhase(-1);
			}
		}
	}

	@Override
	protected void start() {
		mobChat(BOB_BARTER, Expression.HAPPY_TALKING, "How can I help you, " + player.getName() + "?");
	}

}
