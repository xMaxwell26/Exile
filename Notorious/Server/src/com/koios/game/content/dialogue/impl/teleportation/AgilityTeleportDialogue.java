package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

/**
 * Agility course teleports
 *
 * @author Michael | Chex
 */
public class AgilityTeleportDialogue extends Dialogue {

	public AgilityTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	public void select(int index) {
		switch (index) {
		case 1:
			player.castTeleport(
					new Teleport(new Location(2480, 3437, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			player.sendMessage("Welcome to the Gnome agility course!");
			break;

		case 2:
			player.castTeleport(
					new Teleport(new Location(2552, 3558, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			player.sendMessage("Welcome to the Barbarian agility course!");
			break;

		case 3:
			player.castTeleport(
					new Teleport(new Location(2998, 3932, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			player.sendMessage("Welcome to the Wilderness agility course!");
			break;
		}

		stop();
	}

	@Override
	protected void start() {
		choice("Select a course", "Gnome course (level 1+)", "Barbarian course (level 30+)", "Wilderness course (level 52+, @red@PvP@bla@)");
	}

}
