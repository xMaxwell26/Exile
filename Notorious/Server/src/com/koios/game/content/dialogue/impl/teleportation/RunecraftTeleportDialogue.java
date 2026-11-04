package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

public class RunecraftTeleportDialogue extends Dialogue {

	public RunecraftTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				// Air Altar
				player.castTeleport(
						new Teleport(new Location(2845, 4832, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 2) {
				// Mind Altar
				player.castTeleport(
						new Teleport(new Location(2786, 4839, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 3) {
				// Water Altar
				player.castTeleport(
						new Teleport(new Location(2713, 4836, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 4) {
				// Earth Altar
				player.castTeleport(
						new Teleport(new Location(2660, 4839, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 5) {
				setPhase(1);
				choice("Select an Option", "Fire Altar", "Body Altar", "Cosmic Altar", "Previous Page", "Next Page");
				return; // avoid stop
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Fire Altar
				player.castTeleport(
						new Teleport(new Location(2583, 4838, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 2) {
				// Body Altar
				player.castTeleport(
						new Teleport(new Location(2527, 4833, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 3) {
				// Cosmic Altar
				player.castTeleport(
						new Teleport(new Location(2162, 4833, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 4) {
				setPhase(0);
				choice("Select an Option", "Air Altar", "Mind Altar", "Water Altar", "Earth Altar", "Next Page");
				return; // avoid stop
			} else if (index == 5) {
				setPhase(2);
				choice("Select an Option", "Nature Altar", "Law Altar", "Death Altar", "Blood Altar", "Previous Page");
				return; // avoid stop
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				// Nature Altar
				player.castTeleport(
						new Teleport(new Location(2398, 4841, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 2) {
				// Law Altar
				player.castTeleport(
						new Teleport(new Location(2464, 4834, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 3) {
				// Death Altar
				player.castTeleport(
						new Teleport(new Location(2207, 4836, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 4) {
				// Blood Altar
				player.castTeleport(
						new Teleport(new Location(2460, 4894, 1), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else if (index == 5) {
				setPhase(1);
				choice("Select an Option", "Fire Altar", "Body Altar", "Cosmic Altar", "Previous Page", "Next Page");
				return; // avoid stop
			}
		}

		stop();
	}

	@Override
	protected void start() {
		choice("Select an Option", "Air Altar", "Mind Altar", "Water Altar", "Earth Altar", "Next Page");
	}

}
