package com.koios.game.content.titles;

import java.util.Objects;

/**
 * Player titles (Loyalty titles)
 *
 * @author " + Constants.SERVER_NAME + " development team
 */
public final class Title {

	public static Title of(String name, int color, boolean prefix) {
		Objects.requireNonNull(name);
		return new Title(name, color, prefix);
	}

	private final String name;
	private final int color;
	private final boolean prefix;

	private Title(String name, int color, boolean prefix) {
		this.name = Objects.requireNonNull(name);
		this.color = color & 0xFFFFFF;
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}

	public boolean isPrefix() {
		return prefix;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, color, prefix);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Title) {
			Title that = (Title) obj;
			return prefix == that.prefix && color == that.color && name.equals(that.name);
		}

		return false;
	}

}
