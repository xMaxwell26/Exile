package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class AchievementPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Achievement Points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.achievementPoints;
	}

	@Override
	protected void delete(Player player, int amount) {
		player.achievementPoints -= amount;

	}

	@Override
	protected void add(Player player, int amount) {
		player.achievementPoints += amount;
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {
		
		case 11756: //varrock armor 1
		case 11757: // varrock armor 2
		case 11758: // varrock armor 3
			return 12;
		case 15345: // ardougne cloak 1
			return 6;
			
			default: // default at 100 so nothing is free
				return 100;
		}
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}
