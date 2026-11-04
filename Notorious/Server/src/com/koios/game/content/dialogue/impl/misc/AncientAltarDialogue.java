package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Animation;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.network.out.impl.SendSidebarInterfacePacket;

public class AncientAltarDialogue extends Dialogue {

	public AncientAltarDialogue(Player player) {
		super(player);
	}

	@Override
	protected void select(int index) {
		if (index == 1) {
			switch (player.getSpellbook()) {
			case REGULAR:
				setSpellbook(Spellbook.ANCIENT_MAGICKS);
				break;

			case ANCIENT_MAGICKS:
				setSpellbook(Spellbook.REGULAR);
				break;

			case LUNAR:
				setSpellbook(Spellbook.ANCIENT_MAGICKS);
			}
		} else if (index == 2) {
			switch (player.getSpellbook()) {
			case REGULAR:
				setSpellbook(Spellbook.LUNAR);
				break;

			case ANCIENT_MAGICKS:
				setSpellbook(Spellbook.LUNAR);
				break;

			case LUNAR:
				setSpellbook(Spellbook.REGULAR);
			}
		} else {
			stop();
		}
	}

	private void setSpellbook(Spellbook spellbook) {
		player.autocastId = -1;
		player.resetAutocast();
		player.getSession().send(new SendSidebarInterfacePacket(6, spellbook.getSidebarId()));
		player.playAnimation(new Animation(645));
		player.setSpellbook(spellbook);
		sendStatement("Spellbook activated: " + spellbook.getName());
	}

	@Override
	protected void start() {
		switch (player.getSpellbook()) {
		case REGULAR:
			choice("Choose Spellbook", "Ancient Magicks", "Lunar Spellbook", "Never mind");
			break;

		case ANCIENT_MAGICKS:
			choice("Choose Spellbook", "Regular Spellbook", "Lunar Spellbook", "Never mind");
			break;

		case LUNAR:
			choice("Choose Spellbook", "Ancient Magicks", "Regular Spellbook", "Never mind");
			break;

		default:
			sendStatement("Invalid spellbook.");
		}
	}

}
