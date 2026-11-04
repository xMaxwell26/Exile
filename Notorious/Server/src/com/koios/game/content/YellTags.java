package com.koios.game.content;

import java.util.Objects;

import com.koios.game.model.player.Player;

/**
 * Player yell tag information and manipulation handler
 *
 * @author Gabriel | Wolfs Darker
 */
public class YellTags {

	private final Player player;
	private String tag = "None";
	private String colour = "None";

	public YellTags(Player player) {
		this.player = Objects.requireNonNull(player);
	}

	/**
	 * Set the player's new tag
	 *
	 * @param s new tag
	 */
	public boolean setTag(String s) {
		if (s.length() <= 0 || s.length() > 15) {
			player.sendMessage("Invalid tag, please try again.");
			return false;
		}

		tag = s;
		return true;
	}

	/**
	 * Set the tag's new color
	 *
	 * @param s new color
	 */
	public void setColour(String s) {
		colour = s;
	}

	/**
	 * Get the player's tag
	 *
	 * @return the tag. Not null.
	 */
	public String getTag() {
		return tag == null || tag.equals("None") || tag.isEmpty() ? "Global Chat" : tag;
	}

	/**
	 * Get the tag's color
	 *
	 * @return the color
	 */
	public String getColour() {
		return colour == null || colour.equals("None") || colour.isEmpty() ? "255" : colour;
	}

}
