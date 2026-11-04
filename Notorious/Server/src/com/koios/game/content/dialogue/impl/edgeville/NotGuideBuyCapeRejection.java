package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.Constants;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;
import com.koios.util.TimeUtil;

public class NotGuideBuyCapeRejection extends Dialogue {

	public NotGuideBuyCapeRejection(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		mobChat(945, Expression.HAPPY_TALKING, "This shop is for " + Constants.SERVER_NAME + " Veterans.", "You need at least 5 days play time.",
				"Current: " + TimeUtil.toString(player.cachedPlaytime));
	}

}
