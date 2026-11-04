package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class PvPTeleportDialogue extends Dialogue {

	public PvPTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		choice(null, "Chaos Temple (Multi)", "27 Portals", "East Dragons", "Mage Bank", "Cancel");
	}

	@Override
	public void next() {

	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			switch (index) {
			case 1:
				player.castTeleport(new Location(3240, 3611, 0));
				break;
			case 2:
				player.castTeleport(new Location(3022, 3734, 0));
				break;
			case 3:
				player.castTeleport(new Location(3330, 3669, 0));
				break;
			case 4:
				player.castTeleport(new Location(2539, 4716, 0));
				break;
			case 5:
				stop();
				break;
			}
		}
	}

}
