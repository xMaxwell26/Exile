package com.koios.game.content.highscores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.primitives.Ints;
import com.koios.Constants;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public abstract class Highscores {

	public static final Highscores PK_HIGHSCORES = new PKHighscores();

	protected static final int[] CHILD_IDS = { 6402, 6403, 6404, 6405, 6406, 6407, 6408, 6409, 6410, 6411, 8578, 8579, 8580, 8581, 8582, 8583, 8584, 8585, 8586, 8587, 8588, 8589,
			8590, 8591, 8592, 8593, 8594, 8595, 8596, 8597, 8598, 8599, 8600, 8601, 8602, 8603, 8604, 8605, 8606, 8607, 8608, 8609, 8610, 8611, 8612, 8613, 8614, 8615, 8616,
			8617 };

	protected final List<HighscoreEntry> highscores = new ArrayList<>(getMaximumEntries());

	public static void dispatch(Player player, Highscores highscores) {
		highscores.dispatch(player);
	}

	public void dispatch(Player player) {
		highscores.clear();

		populate();

		Collections.sort(highscores);

		player.getSession().send(new SendStringPacket("" + Constants.SERVER_NAME + " High Scores", 6400)); // TODO:
		// Make
		// this
		// customizable?
		player.getSession().send(new SendStringPacket(getTitle() + ":", 6399));

		int slot = 0;
		int size = Ints.min(CHILD_IDS.length, highscores.size(), getMaximumEntries());

		for (; slot < size; slot++) {
			player.getSession().send(new SendStringPacket("@or1@Rank " + (slot + 1) + ": @gre@" + highscores.get(slot).toString(), CHILD_IDS[slot]));
		}

		for (; slot < CHILD_IDS.length; slot++) {
			player.getSession().send(new SendStringPacket(null, CHILD_IDS[slot]));
		}

		player.getInterfaceState().openInterface(6308);
	}

	protected abstract void populate();

	protected abstract String getTitle();

	protected abstract int getMaximumEntries();

}