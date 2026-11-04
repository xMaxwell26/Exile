package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public final class DragonShieldCreatorDialogue extends Dialogue {

	private final byte MOB_ID = 1;

	protected DragonShieldCreatorDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		if (player.getInventory().contains(new Item(4151), new Item(1050))) {
			mobChat(MOB_ID, Expression.HAPPY_TALKING, "How are you, " + player.getName() + " I see you don't have a dragon visage or shield");
		}

		mobChat(MOB_ID, Expression.HAPPY_TALKING, "Come back to me when you do I can attatch them for a small fee!");
	}

}
