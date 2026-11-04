package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class BarrowsDialogue extends Dialogue {

	public BarrowsDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		sendStatement("You've found a hidden tunnel, do you want to enter?");
	}

	@Override
	protected void next() {
		choice("Select an Option", "Yes I'm fearless!", "No way, that look's scary!");
	}

	@Override
	protected void select(int index) {
		stop();

		if (index == 1) {
			player.setPosition(Location.create(3551, 9691));
		}
	}

}
