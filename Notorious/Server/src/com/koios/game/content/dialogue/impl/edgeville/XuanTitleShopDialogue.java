package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.Constants;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class XuanTitleShopDialogue extends Dialogue {

	public XuanTitleShopDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		if (isPhase(0)) {
			setPhase(1);
			mobChat(13727, Expression.HAPPY_TALKING, "The names Xuan, I am the owner of the player titles", "store! I offer many different titles in various categories...");
		} else if (isPhase(1)) {
			setPhase(2);
			chat(Expression.HAPPY_TALKING, "Sounds great! I will be sure to check out your store.");
		} else {
			stop();
		}
	}

	@Override
	protected void start() {
		chat(Expression.HAPPY_TALKING, "Hello there Sir, what do you do here", "in the land of " + Constants.SERVER_NAME + "?");
	}

}
