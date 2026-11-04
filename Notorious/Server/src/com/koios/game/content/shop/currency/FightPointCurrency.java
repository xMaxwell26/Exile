package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class FightPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Fighter Points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.getFightPitPoints();
	}

	@Override
	protected void delete(Player player, int amount) {
		player.setFightPitPoints(player.getFightPitPoints() - amount);
	}

	@Override
	protected void add(Player player, int amount) {
		player.setFightPitPoints(player.getFightPitPoints() + amount);
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {

		default:
			return 500;
		}
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}
