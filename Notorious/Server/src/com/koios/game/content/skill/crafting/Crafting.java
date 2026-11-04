package com.koios.game.content.skill.crafting;

import com.koios.game.model.player.Player;

public class Crafting {

	public static boolean handleItems(Player player, int itemUsed, int useWith) {
		if (GlassBlowing.handleItems(player, itemUsed, useWith)) {
			return true;
		}

		if (CutGem.handleItems(player, itemUsed, useWith)) {
			return true;
		}

		if (CraftLeather.handleItems(player, itemUsed, useWith)) {
			return true;
		}

		return false;
	}

}
