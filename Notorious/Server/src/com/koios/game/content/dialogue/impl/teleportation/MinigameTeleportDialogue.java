package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

public class MinigameTeleportDialogue extends Dialogue {

	public MinigameTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		choice(null, "Duel Arena", "Barrows", "Pest Control", "Fight Caves/Kiln", "More...");
	}

	@Override
	public void select(int option) {
		if (isPhase(0)) {
			switch (option) {
			case 1:
				player.castTeleport(
						new Teleport(new Location(3366, 3266, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			case 2:
				player.castTeleport(
						new Teleport(new Location(3565, 3314, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			case 3:
				player.castTeleport(
						new Teleport(new Location(2662, 2649, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			case 4:
				player.castTeleport(
						new Teleport(new Location(2441, 5171, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			case 5:
				choice(null, "Fight Pits", "Champion Scroll", "Back");
				setPhase(1);
				break;
			}
		} else if (isPhase(1)) {
			switch (option) {
			case 1:
				player.castTeleport(
						new Teleport(new Location(2399, 5178, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			case 2:
				player.castTeleport(
						new Teleport(new Location(3189, 9758, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			case 3:
				choice(null, "Duel Arena", "Barrows", "Pest Control", "Fight Caves/Kiln", "More...");
				setPhase(0);
				break;
			}
		}
	}

}
