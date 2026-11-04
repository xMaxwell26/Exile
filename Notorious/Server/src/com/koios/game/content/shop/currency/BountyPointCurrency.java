package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class BountyPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Bounty Points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.getBounties();
	}

	@Override
	protected void delete(Player player, int amount) {
		player.setBounties(player.getBounties() - amount);
	}

	@Override
	protected void add(Player player, int amount) {
		player.setBounties(player.getBounties() + amount);
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
