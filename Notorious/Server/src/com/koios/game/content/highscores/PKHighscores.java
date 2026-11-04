package com.koios.game.content.highscores;

import java.text.NumberFormat;
import java.util.Locale;

import com.koios.GameService;
import com.koios.game.model.player.Player;

public final class PKHighscores extends Highscores {

	private static final class Entry extends HighscoreEntry {

		private static final NumberFormat FORMATTER = NumberFormat.getInstance(Locale.US);

		private final String name;

		private final double kdr;

		public Entry(int score, String name, double kdr) {
			super(score);
			this.name = name;
			this.kdr = kdr;
		}

		@Override
		public String toString() {
			return name + " @or1@- Kills: @gre@" + score + "@or1@ - KDR: @gre@" + FORMATTER.format(kdr);
		}

		static {
			// set the maximum amount of digits to format for the kdr
			// used so we don't get values like 1.0222222223, rather 1.02
			FORMATTER.setMaximumFractionDigits(3);
		}

	}

	private static final int MAXIMUM_ENTRIES = 30;

	@Override
	protected void populate() {
		for (Player other : GameService.getWorld().getPlayers()) {
			if (other == null) {
				continue;
			}

			if (other.isSuperAdministrator()) {
				continue;
			}

			if (other.killCount == 0) {
				continue;
			}

			highscores.add(new Entry(other.killCount, other.getName(), other.getKDR()));
		}
	}

	@Override
	protected String getTitle() {
		return "Top Online PK Highscores";
	}

	@Override
	protected int getMaximumEntries() {
		return MAXIMUM_ENTRIES;
	}

}