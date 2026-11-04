package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

public class CityTeleportDialogue extends Dialogue {

	public CityTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		choice("Select an Option", "Ardougne", "Camelot", "Canifis", "Entrana", "More Options");
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				// Ardougne
				teleport(2662, 3305, 0);
			} else if (index == 2) {
				// Camelot
				teleport(2757, 3477, 0);
			} else if (index == 3) {
				// Canifis
				teleport(3499, 3492, 0);
			} else if (index == 4) {
				// Entrana
				teleport(2834, 3335, 0);
			} else if (index == 5) {
				// More Options
				setPhase(1);
				choice("Select an Option", "Falador", "Lumbridge", "Varrock", "Previous Options", "More Options");
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Falador
				teleport(2964, 3379, 0);
			} else if (index == 2) {
				// Lumbridge
				teleport(3222, 3218, 0);
			} else if (index == 3) {
				// Varrock
				teleport(3210, 3424, 0);
			} else if (index == 4) {
				// Previous Options
				setPhase(0);
				choice("Select an Option", "Ardougne", "Camelot", "Canifis", "Entrana", "More Options");
			} else if (index == 5) {
				// More Options
				setPhase(2);
				choice("Select an Option", "Previous Options", "Yanille", "Karamja", "Taverley");
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				// Previous Options
				setPhase(1);
				choice("Select an Option", "Falador", "Lumbridge", "Varrock", "Previous Options", "More Options");
			} else if (index == 2) {
				// Yanille
				teleport(2606, 3093, 0);
			} else if (index == 3) {
				// Karamja
				teleport(2923, 3148, 0);
			} else if (index == 4) {
				// Taverley
				teleport(2918, 3440, 0);
			}
		}
	}

	private void teleport(int x, int y, int z) {
		stop();
		player.castTeleport(new Teleport(new Location(x, y, z), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
	}

}
