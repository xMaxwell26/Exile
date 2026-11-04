package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class SkillTeleportDialogue extends Dialogue {

	public SkillTeleportDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		choice(null, "Falador mine", "Agility courses", "Seers' village", "Catherby", "More...");
	}

	@Override
	public void next() {
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				player.castTeleport(new Location(3044, 9779, 0));
			} else if (index == 2) {
				player.dialogue().start(new AgilityTeleportDialogue(player));
			} else if (index == 3) {
				player.castTeleport(new Location(2722, 3485, 0));
			} else if (index == 4) {
				player.castTeleport(new Location(2804, 3433, 0));
			} else {
				choice(null, "Fishing Guild", "Hunting Grounds", "Port Phasmatys Farming Patch", "Al Kharid mining site", "Back");
				setPhase(1);
			}
		} else if (isPhase(1)) {
			switch (index) {
			case 1:
				player.castTeleport(new Location(2595, 3415, 0));
				break;
			case 2:
				player.dialogue().start(new HunterTeleportDialogue(player));
				break;
			case 3:
				player.castTeleport(new Location(3599, 3522, 0));
				break;
			case 4:
				player.castTeleport(new Location(3300, 3313, 0));
				break;
			case 5:
				choice(null, "Falador mine", "Agility courses", "Seers' village", "Catherby", "More...");
				setPhase(0);
				break;
			}
		}
	}

}
