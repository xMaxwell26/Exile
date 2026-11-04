package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class SirAmikVarzeDialogue extends Dialogue {

	public SirAmikVarzeDialogue(Player player) {
		super(player);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			setPhase(1);
			mobChat(608, Expression.HAPPY_TALKING, "Certainly!", "Here in the realm of " + Constants.SERVER_NAME + " there are many tasks",
					"in four main categories: easy, medium, hard and elite.");
		} else if (isPhase(1)) {
			setPhase(2);
			mobChat(608, Expression.HAPPY_TALKING, "Every individual task yields achievement points on", "completion, and I'm here to offer equipment in trade",
					"for your achievement points.");
		} else if (isPhase(2)) {
			setPhase(3);
			mobChat(608, Expression.HAPPY_TALKING, "You can swap to your Achievement Diary by clicking the", "green icon in your Quest Journals tab.");
		} else if (isPhase(3)) {
			setPhase(4);
			choice("Select an Option", "What do you have to offer?", "Thank you!");
		} else if (isPhase(5)) {
			setPhase(0xBABE); // exit
			mobChat(608, Expression.HAPPY_TALKING, "Good bye!");
		} else {
			stop();
		}
	}

	@Override
	public void select(final int index) {
		if (isPhase(0)) {
			if (index == 1) {
				chat(Expression.HAPPY_TALKING, "Hi Amik Varze!", "Could you please tell me more about achievements?");
			} else if (index == 2) {
				stop();
				GameService.getShopManager().open(player, 78);
			} else if (index == 3) {
				stop();
			}
		} else if (isPhase(4)) {
			if (index == 1) {
				stop();
				GameService.getShopManager().open(player, 78);
			} else if (index == 2) {
				setPhase(5);
				chat(Expression.HAPPY_TALKING, "Thank you!");
			}
		}
	}

	@Override
	protected void start() {
		choice("Select an Option", "Talk about Achievements", "What do you have to offer?", "Never mind");
	}

}
