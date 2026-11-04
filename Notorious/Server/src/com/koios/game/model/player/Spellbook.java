package com.koios.game.model.player;

import java.util.Objects;

public enum Spellbook {

	REGULAR(1151, "Regular spellbook"),

	ANCIENT_MAGICKS(12855, "Ancient magicks"),

	LUNAR(29999, "Lunar spellbook");

	/**
	 * The name of this spellbook.
	 */
	private final String name;

	/**
	 * The interface id for the spellbook sidebar.
	 */
	private final int sidebarId;

	private Spellbook(int sidebarId, String name) {
		this.sidebarId = sidebarId;
		this.name = Objects.requireNonNull(name);
	}

	public String getName() {
		return name;
	}

	public int getSidebarId() {
		return sidebarId;
	}

}
