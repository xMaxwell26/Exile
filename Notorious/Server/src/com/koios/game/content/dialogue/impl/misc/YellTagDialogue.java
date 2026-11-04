package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.util.StringUtil;

public class YellTagDialogue extends Dialogue {

	private boolean tag;

	public YellTagDialogue(Player player) {
		super(player);
		tag = false;
	}

	@Override
	protected void start() {
		if (player.getPrivilege() == RegularPrivilege.NONE) {
			mobChat(5782, Expression.HAPPY_TALKING, "Hello " + player.getName() + "! You currently don't have a donator upgrade!", "To access this feature, donate in our forums!");
			setPhase(-1);
		}

		setPhase(0);
		mobChat(5782, Expression.HAPPY_TALKING, "Hello " + player.getName() + "! My name is Mernik, and", "I manage the yell tags!", "What can I do for you?");
	}

	@Override
	protected void next() {
		if (getPhase() < 0) {
			stop();
			return;
		}

		if (isPhase(0)) {
			choice("Select an Option", "Set custom title", "Set title color", "Never mind, I have to go.");
			setPhase(1);
		} else if (isPhase(1)) {
			mobChat(5782, Expression.HAPPY_TALKING, "It currently costs 25 million coins to change your yell tag.");
			setPhase(2);
		} else if (isPhase(2)) {
			choice(null, "Pay 25 million coins", "No thanks");
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				tag = true;
				takeNameInput();
			} else if (index == 2) {
				tag = false;
				takeNameInput();
			} else {
				stop();
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				next();
			} else if (index == 2) {
				tag = false;
				takeNameInput();
			} else {
				stop();
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				if (player.getInventory().contains(new Item(995, 25_000_000))) {
					tag = true;
					takeNameInput();
				} else {
					mobChat(5782, Expression.HAPPY_TALKING, "You do not have enough gold.");
					setPhase(0);
				}
			} else {
				setPhase(0);
				next();
			}
		} 
	}

	@Override
	protected void input(String value) {
		if (tag) {
			if (player.getYT().setTag(StringUtil.formatUsername(value))) {
				if (player.getInventory().contains(new Item(995, 25_000_000))) {
					mobChat(5782, Expression.HAPPY_TALKING, "Your new yell tag is '" + StringUtil.formatUsername(value) + "'!");
					player.getInventory().remove(new Item(995, 25_000_000));
					setPhase(-1);
				} else {
					mobChat(5782, Expression.CALM_TALK, "You do not have enough gold.");
				}
			} else {
				stop();
			}
		} else {

			try {
				int rgb = Integer.decode("#" + value);
				player.sendMessage("<col=" + Integer.toHexString(rgb) + ">@@@ This is your new yell-tag color @@@");
			} catch (NumberFormatException ex) {
				player.sendMessage("Invalid RGB hexadecimal format: <col=800000>" + value + "</col>.");
				stop();
				return;
			}

			player.getYT().setColour(value);
			mobChat(5782, Expression.HAPPY_TALKING, "Your new yell tag colour is '" + value + "'!");
			setPhase(-1);
		}
	}

}
