package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

/**
 * Hunter teleports
 *
 * @author J
 */
public class HunterTeleportDialogue extends Dialogue {

	public HunterTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	public void select(int index) {
		switch (index) {
		case 1: // Crimson Swifts & Red Chinchompas
			player.castTeleport(
					new Teleport(new Location(2711, 2780, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 2: // Curulean twitches
			player.castTeleport(
					new Teleport(new Location(2849, 3487, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 3: // Copper longtails & Chinchompas
			player.castTeleport(
					new Teleport(new Location(2412, 3043, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 4: // Raccoons
			player.castTeleport(
					new Teleport(new Location(3096, 3222, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 5: // Greenwalls
			player.castTeleport(
					new Teleport(new Location(3136, 3273, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
		}

		stop();
	}

	@Override
	protected void start() {
		choice("Select a teleport", "Crimson Swifts & Red Chinchompas", "Curulean twitches", "Copper longtails & Chinchompas", "Raccoons", "Greenwalls");
	}

}
