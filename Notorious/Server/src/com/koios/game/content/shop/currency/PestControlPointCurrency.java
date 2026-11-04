package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public final class PestControlPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Pest control points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.pcPoints;
	}

	@Override
	protected void delete(Player player, int amount) {
		player.pcPoints -= amount;
	}

	@Override
	protected void add(Player player, int amount) {
		player.pcPoints += amount;
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {
		case 11663: // void mage helm
			return 200;
		case 11664: // void ranger helm
			return 200;
		case 11665: // void melee helm
			return 200;
		case 8842: // void gloves
			return 150;
		case 8839: // void top
			return 250;
		case 8840: // void bottom
			return 250;
		case 8841:
			return 100;
		case 19785: // void elite top
		case 19787: // void elite top
		case 19789: // void elite top
			return 500;
		case 19786: // void elite bottom
		case 19788: // void elite bottom
		case 19790: // void elite bottom
			return 500;
		case 19748: // cloak
			return 250;
		}
		return Integer.MAX_VALUE; // default
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}