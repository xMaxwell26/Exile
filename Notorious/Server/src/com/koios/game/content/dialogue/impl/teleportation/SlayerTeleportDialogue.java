package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

public class SlayerTeleportDialogue extends Dialogue {

	public SlayerTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		setPhase(0);
		choice("Select an Option", "Slayer Masters", "Slayer Locations");
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				player.castTeleport(
						new Teleport(new Location(3297, 9825, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				stop();
			} else if (index == 2) {
				setPhase(1);
				choice("My Destination:", "Morytania Slayer Tower", "Taverly Dungeon", "Brimhaven Dungeon", "Fremennik Slayer Dungeon", "Next Page");
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Morytania Slayer Tower (first floor)
				player.castTeleport(
						new Teleport(new Location(3429, 3538, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));

			} else if (index == 2) {
				// Taverly Dungeon
				player.castTeleport(
						new Teleport(new Location(2884, 9798, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 3) {
				// Brimhaven Dungeon
				player.castTeleport(
						new Teleport(new Location(2710, 9466, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 4) {
				// Fremennik Slayer Dungeon
				player.castTeleport(
						new Teleport(new Location(2795, 3615, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 5) {
				setPhase(2);
				choice("My Destination:", "Cave-o-Horror", "Frost Dragon Lair", "Waterbirth Island (Dagannoth Lair)", "", "Previous Page");
				return; // avoid stop()
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				// Cave-o-Horror
				player.castTeleport(
						new Teleport(new Location(3577, 9927, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 2) {
				// Frost Dragon Lair
				player.castTeleport(
						new Teleport(new Location(2828, 9518, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 3) {
				// Waterbirth Island (Dagannoth Lair)
				player.castTeleport(
						new Teleport(new Location(2527, 3739, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				player.sendMessage("Enter the cave entrance to get to the Dagannoth lair..");
			} else if (index == 5) {
				setPhase(1);
				choice("My Destination:", "Morytania Slayer Tower", "Taverly Dungeon", "Brimhaven Dungeon", "Fremennik Slayer Dungeon", "Next Page");
				return; // avoid stop()
			}
		}
	}

}
