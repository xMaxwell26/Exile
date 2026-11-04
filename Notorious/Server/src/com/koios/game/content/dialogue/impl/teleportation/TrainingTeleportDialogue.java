package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class TrainingTeleportDialogue extends Dialogue {

	public TrainingTeleportDialogue(Player player) {
		super(player);
	}

	private static final String[] OPTION_1 = { "Neitiznot (Yaks)", "Rellekka (Rock Crabs)", "Taverly Dungeon", "Brimhaven Dungeon", "More..." };

	private static final String[] OPTION_2 = { "Bandits", "Varrock sewers", "Experiments", "Cave Horrors", "More..." };

	private static final String[] OPTION_3 = { "Stronghold of Security", "Fremennik Slayer Dungeon", "Slayer Tower", "Dagganoths", "More..." };

	private static final String[] OPTION_4 = { "Frost Dragons", "Karamja Dungeon", "Waterfall", "Port Sarim Dungeon", "More..." };

	private static final String[] OPTION_5 = { "Heroes Guild Dungeon", "Beginning..." };
	@Override
	protected void start() {
		choice("Select an Option", OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3], OPTION_1[4]);
	}

	@Override
	protected void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				// neitiznot
				player.castTeleport(Location.create(2322, 3803, 0));
			} else if (index == 2) {
				// rock crabs
				player.castTeleport(Location.create(2707, 3711));
			} else if (index == 3) {
				// taverly dungeon
				player.castTeleport(Location.create(2884, 9798));
			} else if (index == 4) {
				// brimhaven dungeon
				player.castTeleport(Location.create(2708, 9467));
			} else if (index == 5) {
				setPhase(1);
				choice("Select an Option", OPTION_2[0], OPTION_2[1], OPTION_2[2], OPTION_2[3], OPTION_2[4]);
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Desert Bandits
				player.castTeleport(Location.create(3170, 2994, 0));
			} else if (index == 2) {
				// Varrock sewers
				player.castTeleport(Location.create(3236, 9858));
			} else if (index == 3) {
				player.castTeleport(Location.create(3575, 9929, 4));
			} else if (index == 4) {
				player.castTeleport(Location.create(3575, 9929));
			} else {
				choice("Select an Option", OPTION_3[0], OPTION_3[1], OPTION_3[2], OPTION_3[3], OPTION_3[4]);
				setPhase(2);
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				player.castTeleport(new Location(3080, 3424));
			} else if (index == 2) {
				player.castTeleport(new Location(2807, 10001));
			} else if (index == 3) {
				player.castTeleport(new Location(3428, 3538));
			} else if (index == 4) {
				player.castTeleport(new Location(2527, 3739));
			} else {
				choice("Select an Option", OPTION_4[0], OPTION_4[1], OPTION_4[2], OPTION_4[3], OPTION_4[4]);
				setPhase(3);
			}
		} else if (isPhase(3)) {
			if (index == 1) {
				player.castTeleport(new Location(1747 + NumberUtil.inclusiveRandom(1), 5324 + NumberUtil.inclusiveRandom(2)));
			} else if (index == 2) {
				player.castTeleport(new Location(2856 + NumberUtil.inclusiveRandom(3), 9571 + NumberUtil.inclusiveRandom(2)));
			} else if (index == 3) {
				player.castTeleport(new Location(2574 + NumberUtil.inclusiveRandom(2), 9873 + NumberUtil.inclusiveRandom(3)));
			} else if (index == 4) {
				player.castTeleport(new Location(3008 + NumberUtil.inclusiveRandom(2), 9550 + NumberUtil.inclusiveRandom(3)));
			} else {
				choice("Select an Option", OPTION_5[0], OPTION_5[1]);
				setPhase(4);
			}
		} else if (isPhase(4)) {
			if (index == 1) {
				player.castTeleport(new Location(2893, 9907));
			} else {
				choice("Select an Option", OPTION_1[0], OPTION_1[1], OPTION_1[2], OPTION_1[3], OPTION_1[4]);
				setPhase(0);
			}
		}
	}

}
