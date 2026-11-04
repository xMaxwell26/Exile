package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.Constants;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class TutorialDialogue extends Dialogue {

	public static final int TUTORIAL_NPC = 945;

	public TutorialDialogue(Player player) {
		super(player);
	}

	@Override
	public void next() {
		switch (getPhase()) {
		case 1:
			tele(new Location(2322, 3803), "This is basic training.", "You can return to this and other training spots",
					"by casting the Monster Teleport in your Magic spellbook.");
			break;

		case 2:
			tele(new Location(3297, 9825), "These are the Slayer masters.", "You can get here in the Magic book", "by using the Trollheim teleport.");
			break;

		case 3:
			tele(new Location(3080, 3511), "This is the General store.", "You can sell almost anything here", "for quick cash.");
			break;

		case 4:
			tele(new Location(3044, 9779), "This is the Mining area.", "This is one of many Skill locations", "accessed within your Magic book!");
			break;
		case 5:
			tele(new Location(3080, 3482), "Have a great time playing " + Constants.SERVER_NAME + ",", "and remember to use ::rules to make", "sure you don't break any!");
			break;

		case 6:
			player.setAttribute("tutorial", false);
			stop();
			break;

		case 10:
			choice(null, "Yes, view the tutorial.", "No, skip the tutorial.");
			break;
		}
	}

	@Override
	public void select(int option) {
		if (isPhase(10)) {
			switch (option) {
			case 1:
				setPhase(0);
				tele(new Location(3094, 3485), "These are the shops.", "You can get items here for whatever you need!");
				break;
			case 2:
				player.setAttribute("tutorial", false);
				stop();
				break;
			}
		}
	}

	@Override
	protected void start() {
		if (player.getAttribute("tutorial", false)) {
			mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, "Welcome to " + Constants.SERVER_NAME + ", " + player.getName() + "!", "Would you like to view the tutorial?");
			setPhase(10);
		} else {
			mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, "Welcome back to " + Constants.SERVER_NAME + ", " + player.getName() + "!");
			setPhase(6);
		}
	}

	public void tele(Location location, String... message) {
		player.teleport(location);

		if (message.length == 1) {
			mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, message[0]);
		} else if (message.length == 2) {
			mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, message[0], message[1]);
		} else if (message.length == 3) {
			mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, message[0], message[1], message[2]);
		} else if (message.length == 4) {
			mobChat(TUTORIAL_NPC, Expression.HAPPY_TALKING, message[0], message[1], message[2], message[3]);
		}
		setPhase(getPhase() + 1);
	}

}