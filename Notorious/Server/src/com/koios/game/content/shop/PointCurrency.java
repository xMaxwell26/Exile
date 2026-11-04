package com.koios.game.content.shop;

import com.koios.game.model.player.Player;

public abstract class PointCurrency extends Currency {

	@Override
	protected boolean canAdd(Player player, int amount) {
		long total = (long) getAmount(player) + amount;
		if (total > Integer.MAX_VALUE) {
			player.sendMessage("You cannot receive any more " + getName() + ".");
			return false;
		}

		return true;
	}

}