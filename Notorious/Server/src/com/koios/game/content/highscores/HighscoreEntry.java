package com.koios.game.content.highscores;

public abstract class HighscoreEntry implements Comparable<HighscoreEntry> {

	// at least one primitive score is required
	protected final int score;

	public HighscoreEntry(int score) {
		this.score = score;
	}

	@Override
	public abstract String toString();

	@Override
	public int compareTo(HighscoreEntry o) {
		return Integer.compare(score, o.score);
	}

}