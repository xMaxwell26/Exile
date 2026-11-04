package com.koios.game.content.questtab.impl;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.koios.game.content.questtab.QuestPage;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.util.StringUtil;

/**
 * Writes the quick teleports quest tab page
 * 
 * @author Arithium
 *
 */
public class QuickTeleportsQuestPage extends QuestPage {

	/**
	 * Holds all of the data for the quick teleports
	 * 
	 * @author Arithium
	 *
	 */
	private static enum QuickTeleports {
		CITY_TELEPORTS(null), 
		EDGEVILLE(new Location(3087, 3495, 0)),
		LUMBRIDGE(new Location(3222, 3218, 0)),
		FALADOR(new Location(2964, 3378, 0)),
		CAMELOT(new Location(2757, 3477, 0)),
		ARDOUGNE(new Location(2662, 3305, 0)),
		DUEL_ARENA(new Location(3345, 3251, 0)),
		
		;

		/**
		 * An immutable set containing all of the quick teleports
		 */
		private static final Set<QuickTeleports> teleports = Sets.immutableEnumSet(EnumSet.allOf(QuickTeleports.class));

		/**
		 * The location the player will teleport too
		 */
		private final Location location;

		/**
		 * Constructs a new {@link QuickTeleports} instance
		 * 
		 * @param location
		 *            The {@link Location} the player will teleport too
		 */
		QuickTeleports(Location location) {
			this.location = location;
		}

		/**
		 * Gets the {@link Location} the player will teleport too
		 * 
		 * @return The {@link Location} the player will teleport too
		 */
		public Location getLocation() {
			return location;
		}

		/**
		 * Gets the {@link Set} of {@link QuickTeleports}
		 * 
		 * @return The {@Set} of {@link QuickTeleports}
		 */
		public static Set<QuickTeleports> getTeleports() {
			return teleports;
		}

		@Override
		public String toString() {
			return StringUtil.formatUsername(name().replaceAll("_", " "));
		}
	}

	@Override
	public void write(Player player) {
		int start = 1;
		for (QuickTeleports teleport : QuickTeleports.getTeleports()) {
			boolean title = teleport.getLocation() == null;
			writeLine(player, start++, (title ? "@gre@ - " : "@yel@") + teleport.toString() + (title ? " - " : " Teleport"));
		}
		writeLine(player, start + 1, "[Back]");
	}

	@Override
	public boolean buttonClick(Player player, int buttonId) {
		/*
		 * Gets our starting string id
		 */
		int start = 129002;
		
		/*
		 * Loop through all of the teleports to verify it has a location, if so and the button was pressed, teleport.
		 */
		for (QuickTeleports teleport : QuickTeleports.getTeleports()) {
			if (buttonId == start++) {
				if (teleport.getLocation() != null) {
					player.castTeleport(teleport.getLocation());
					return true;
				}
			}
		}
		
		/*
		 * Our back button
		 */
		if (buttonId == 129002 + QuickTeleports.getTeleports().size() + 1) {
			QuestTabHandler.write(player, QuestPages.HOME);
			return true;
		}
		return false;
	}

	@Override
	public String title() {
		return "Quick Teleports";
	}

}
