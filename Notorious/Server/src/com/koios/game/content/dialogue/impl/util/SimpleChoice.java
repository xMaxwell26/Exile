package com.koios.game.content.dialogue.impl.util;

import java.util.Objects;
import java.util.function.Consumer;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.player.Player;

/**
 * A simple choice dialogue that accepts two options supplied in the constructor. When/if the player makes his/her choice, the action consumer for that choice is called.
 */
public class SimpleChoice extends Dialogue {

	private final String textOption1;
	private final String textOption2;
	private final Consumer<Player> actionOption1;
	private final Consumer<Player> actionOption2;

	public SimpleChoice(Player player, String textOption1, Consumer<Player> actionOption1, String textOption2, Consumer<Player> actionOption2) {
		super(player);
		this.textOption1 = Objects.requireNonNull(textOption1);
		this.textOption2 = Objects.requireNonNull(textOption2);
		this.actionOption1 = Objects.requireNonNull(actionOption1);
		this.actionOption2 = Objects.requireNonNull(actionOption2);
	}

	@Override
	protected void select(int index) {
		// Note: stop before consuming action in case a new dialogue is started
		stop();

		if (index == 1) {
			actionOption1.accept(player);
		} else if (index == 2) {
			actionOption2.accept(player);
		}
	}

	@Override
	protected void start() {
		choice("Select an Option", textOption1, textOption2);
	}

}
