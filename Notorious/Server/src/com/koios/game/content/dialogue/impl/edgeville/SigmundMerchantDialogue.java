package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class SigmundMerchantDialogue extends Dialogue {

	public SigmundMerchantDialogue(Player player) {
		super(player);
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			boolean hasDarkBow = player.getInventory().contains(ItemIdentifiers.DARK_BOW);
			boolean hasAbyssalWhip = player.getInventory().contains(ItemIdentifiers.ABYSSAL_WHIP);

			if (player.slayerPoints < 50) {
				setPhase(7);
			} else if (hasDarkBow) {
				if (hasAbyssalWhip) {
					// Got both
					setPhase(1);
				} else {
					// Got a dark bow
					setPhase(2);
				}
			} else if (hasAbyssalWhip) {
				// Got an abyssal whip
				setPhase(3);
			} else {
				// Don't have either
				setPhase(4);
			}

			mobChat(1282, Expression.HAPPY_TALKING, "Hello, " + player.getName() + ".", "Would you be interested in some recolors for your",
					"abyssal whip or dark bow? It will cost you 50", "Slayer Points.");
		} else if (isPhase(1)) {
			choice("Select an Option", "Abyssal whip", "Dark bow", "No thanks");
		} else if (isPhase(2)) {
			choice("Select an Option", "Dark bow", "No thanks");
		} else if (isPhase(3)) {
			choice("Select an Option", "Abyssal whip", "No thanks");
		} else if (isPhase(4)) {
			setPhase(0xBABE);
			chat(Expression.HAPPY_TALKING, "Sorry Sigmund, but I don't have any.");
		} else if (isPhase(7)) {
			setPhase(0xBABE);
			mobChat(1282, Expression.HAPPY_TALKING, "You do not have enough Slayer Points to trade.");
		} else {
			stop();
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(1)) {
			if (index == 1) {
				// Abyssal whip
				setPhase(5);
				choice("Select Color", "Yellow", "Blue", "White", "Green", "Forget it");
			} else if (index == 2) {
				// Dark bow
				setPhase(6);
				choice("Select Color", "Yellow", "Blue", "White", "Green", "Forget it");
			} else if (index == 3) {
				stop();
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				// Dark bow
				setPhase(6);
				choice("Select Color", "Yellow", "Blue", "White", "Green", "Forget it");
			} else if (index == 2) {
				stop();
			}
		} else if (isPhase(3)) {
			if (index == 1) {
				// Abyssal whip
				setPhase(5);
				choice("Select Color", "Yellow", "Blue", "White", "Green", "Forget it");
			} else if (index == 2) {
				stop();
			}
		} else if (isPhase(5)) {
			if (index >= 1 && index <= 4) {
				if (player.getInventory().contains(4151)) {
					player.getInventory().remove(new Item(4151));
					player.getInventory().add(new Item(15440 + index));
					AchievementManager.performed(player, Achievement.RECOLOR_WHIP);
				}
			}

			stop();
		} else if (isPhase(6)) {
			if (index >= 1 && index <= 4) {
				if (player.getInventory().contains(11235)) {
					player.getInventory().remove(new Item(11235));
					player.getInventory().add(new Item(15700 + index));

					AchievementManager.performed(player, Achievement.RECOLOR_DARK_BOW);
				}
			}

			stop();
		}
	}

	@Override
	protected void start() {
		chat(Expression.HAPPY_TALKING, "Hi!");
	}

}
