package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.util.NumberUtil;

public class BossesTeleportDialogue extends Dialogue {

	private Location location = null;

	public BossesTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		choice("Select an Option", "Godwars Dungeon", "Corporeal Beast", "Tormented Demons", "King Black Dragon (PvP)", "More Options");
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				// Godwars Dungeon
				confirmTeleport("the Godwars Dungeon", false, Location.create(2881, 5310, 2));
			} else if (index == 2) {
				// Corporeal Beast
				confirmTeleport("the Corporeal Beast", false, Location.create(2721 + NumberUtil.random(2), 5102 + NumberUtil.random(2), 0));
				// confirmTeleport("the Corporeal Beast", false, Location.create(2966, 4383, 2));
			} else if (index == 3) {
				// Tormented Demonsplayer.castTeleport(new Location(2823, 9514));
				confirmTeleport("Tormented Demons", false, new Location(2828, 9527));
			} else if (index == 4) {
				// King Black Dragon
				confirmTeleport("the King Black Dragon", true, Location.create(3006, 3849));
			} else if (index == 5) {
				setPhase(1);
				choice("Select an Option", "Barrelchest", "Kalphite Queen", "Dagannoth Kings", "Chaos Elemental (PvP)", "More Options");
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Barrelchest
				confirmTeleport("the Barrelchest", false, Location.create(2974, 9516, 1));
			} else if (index == 2) {
				// Kalphite Queen
				confirmTeleport("the Kalphite Queen", false, Location.create(3502, 9492));
			} else if (index == 3) {
				// Dagannoth Kings
				confirmTeleport("the Dagannoth Kings", false, Location.create(1909, 4366));
			} else if (index == 4) {
				// Chaos Elemental (PvP)
				confirmTeleport("Chaos Elemental (PvP)", true, Location.create(3273, 3922));
			} else if (index == 5) {
				setPhase(2);
				choice("Select an Option", "Bork (PvP)", "Bal'lak the Pummeller", "Nomad", "Glod", "Beginning");
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				// Bork (PvP)
				confirmTeleport("Bork (PvP)", true, Location.create(3226, 3944));
			} else if (index == 2) {
				// Bal'lak the Pummeller
				confirmTeleport("Bal'lak the Pummeller", false, Location.create(2837, 3805));
			} else if (index == 3) {
				confirmTeleport("Nomad", false, Location.create(2836, 9598));
			} else if (index == 4) {
				confirmTeleport("Glod", false, Location.create(2774, 9209));
			} else if (index == 5) {
				setPhase(0);
				choice("Select an Option", "Godwars Dungeon", "Corporeal Beast", "Tormented Demons", "King Black Dragon (PvP)", "More Options");
			}
		} else if (isPhase(4)) {
			if (index == 1) {
				if (null == location) {
					setPhase(0);
					sendStatement("Invalid teleport.");
				} else {
					player.castTeleport(new Teleport(location, player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				}
			} else {
				stop();
			}
		}
	}

	@Override
	protected void next() {
		if (isPhase(3)) {
			setPhase(4);
			choice("Select an Option", "Yes", "No");
		} else {
			stop();
		}
	}

	private void confirmTeleport(String name, boolean pvp, Location location) {
		setPhase(3);
		this.location = location;

		if (pvp) {
			sendStatement("Are you sure you want to teleport to " + name + "?", "@dre@This spell will take you to a PvP area.");
		} else {
			sendStatement("Are you sure you want to teleport to " + name + "?");
		}
	}

}