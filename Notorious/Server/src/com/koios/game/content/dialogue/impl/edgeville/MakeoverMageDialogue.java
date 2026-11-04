package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.Constants;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendInterfacePacket;

public class MakeoverMageDialogue extends Dialogue {

	public MakeoverMageDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		if (isPhase(0)) {
			setPhase(1);
			mobChat(599, Expression.HAPPY_TALKING, "I the Make-over Mage of " + Constants.SERVER_NAME + "... I have the power", "to change your appearance to whatever you wish!");
		} else if (isPhase(1)) {
			setPhase(2);
			choice("Change Appearance", "Change your appearance", "Nevermind");
		} else {
			stop();
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(2)) {
			if (index == 1) {
				player.canChangeAppearance = true;
				player.getSession().send(new SendInterfacePacket(3559));
			} else {
				chat(Expression.HAPPY_TALKING, "Awesome! I will let you know if I require your skills.");
			}
		}
	}

	@Override
	protected void start() {
		chat(Expression.HAPPY_TALKING, "Hello strange wizard, what are you doing around here", "in the land of " + Constants.SERVER_NAME + "?");
	}

}
