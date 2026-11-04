package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class MorrisDialogue extends Dialogue {

	public MorrisDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		int countUnlock = (int) GameService.getConfiguration().getInteger("ckey_count_unlock");
		int countThirdage = (int) GameService.getConfiguration().getInteger("ckey_count_thirdage");

		mobChat(227, Expression.HAPPY_TALKING, "That chest has been looted @blu@" + NumberUtil.format(countUnlock) + "@bla@ times.",
				"Third-age equipment has been found @blu@" + NumberUtil.format(countThirdage) + "@bla@ times.");
	}

}
