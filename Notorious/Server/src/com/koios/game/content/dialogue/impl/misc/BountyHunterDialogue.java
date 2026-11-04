package com.koios.game.content.dialogue.impl.misc;

import com.koios.GameService;
import com.koios.game.content.bountyhunter.BountyHunterEmblem;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public class BountyHunterDialogue extends Dialogue {

	public BountyHunterDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		mobChat(975, Expression.HAPPY_TALKING, "Hello, wanderer.");
		setPhase(0);
	}

	@Override
	protected void next() {
		switch (getPhase()) {
		case 0:
			choice("Select an Option", "Open Shop", "Trade Bounties");
			break;

		case 1:
			mobChat(975, Expression.HAPPY_TALKING, "I see you have something valuable on your person", "Certain.... ancient embles, you see.");
			setPhase(2);
			break;

		case 2:
			mobChat(975, Expression.HAPPY_TALKING, "I'll happily take those of your hands for a handsome", "fee.");
			setPhase(3);
			break;

		case 3:
			int currentPoints = BountyHunterEmblem.getBountiesCarried(player, false);
			sendItemChat("", "", "All your emblems are a total worth of " + currentPoints + " Bounty points.", "", "", 14876, 150);
			setPhase(4);
			break;

		case 4:
			choice("Sell your emblems to the trader?", "Yes", "No");
			setPhase(5);
			break;

		case 8:
			break;

		default:
			stop();
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				stop();
				GameService.getShopManager().open(player, 82);
			} else {
				mobChat(975, Expression.HAPPY_TALKING, "I see you have something valuable on your person", "Certain.... ancient emblems, you see.");
				setPhase(2);
			}
		} else if (isPhase(5)) {
			if (index == 1) {
				mobChat(975, Expression.HAPPY_TALKING, "Thank you. My master in the north will be very", "pleased.");
				player.setBounties(player.getBounties() + BountyHunterEmblem.getBountiesCarried(player, true));
				player.sendMessage("You now have " + player.getBounties() + " bounty points.");
				setPhase(200);
			}
		} else {
			stop();
		}
	}

}
