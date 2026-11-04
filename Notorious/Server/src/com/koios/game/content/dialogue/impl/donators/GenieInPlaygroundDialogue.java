package com.koios.game.content.dialogue.impl.donators;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.ConfigKeys;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;

public class GenieInPlaygroundDialogue extends Dialogue {

	public GenieInPlaygroundDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		if (isPhase(0)) {
			choice("Select an Option", "Please tell me more!", "Yes! Bought XP is the best XP!", "Sorry, but I'm not interested.");
		} else if (isPhase(1)) {
			setPhase(2);
			mobChat(3351, Expression.HAPPY_TALKING, "Certainly!", "Firstly, it costs 100 Donation Points and is consumed", "on use. It is worth to mention that it cannot be",
					"traded to other players or sold in NPC shops.");
		} else if (isPhase(2)) {
			setPhase(0); // return to phase 0 when continue is clicked
			mobChat(3351, Expression.HAPPY_TALKING, "Once consumed it will remove you with 100,000 XP", "in whatever skill you choose; assuming your skill level",
					"in that particular skill is at least 60.");
		} else if (isPhase(3)) {
			setPhase(4);
			mobChat(3351, Expression.HAPPY_TALKING, "Are you sure?", "It will cost you 100 Donation Points.");
		} else if (isPhase(4)) {
			choice("Buy lamp?", "Use 100 Donator Points on an XP lamp", "I'd rather trade my points with Zambo.");
		} else {
			stop();
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				// Please tell me more!
				setPhase(1);
				chat(Expression.HAPPY_TALKING, "Could you elaborate a little its cost, use and", "any limitations it might have?");
			} else if (index == 2) {
				// Yes! Bought XP is the best XP!
				setPhase(3);
				chat(Expression.HAPPY_TALKING, "Yes a million times!");
			} else if (index == 3) {
				// Sorry, but I'm not interested.
				stop();
			}
		} else if (isPhase(4)) {
			if (index == 1) {
				// Use 100 Donator Points on an XP lamp.
				if (player.donatorPoints >= 100) {
					if (player.getInventory().freeSlots() > 0) {
						player.donatorPoints -= 100;
						player.getInventory().add(new Item(2528));
						player.getSession().send(new SendConfigPacket(ConfigKeys.DONATOR_POINTS, player.donatorPoints));
						stop();
					} else {
						setPhase(0xBABE); // stop
						sendStatement("You don't have enough inventory space to do this.");
					}
				} else {
					setPhase(0xBABE); // stop
					chat(Expression.HAPPY_TALKING, "Perhaps I should come back when I actually have 100", "Donator Points to spend.");
				}
			} else if (index == 2) {
				// I'd rather trade my points with Zambo.
				stop();
			}
		} else {
			stop();
		}
	}

	@Override
	protected void start() {
		mobChat(3351, Expression.HAPPY_TALKING, "Hello, " + player.getName() + ".", "Are you interested in buying an XP lamp?");
	}

}
