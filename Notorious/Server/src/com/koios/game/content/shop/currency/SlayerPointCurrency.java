package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public final class SlayerPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Slayer points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.slayerPoints;
	}

	@Override
	protected void delete(Player player, int amount) {
		player.slayerPoints -= amount;
	}

	@Override
	protected void add(Player player, int amount) {
		player.slayerPoints += amount;
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {
		case 4166:
		case 4164:
		case 4551:
		case 4168:
			return 5; // slayer helm ingredients
		case 6199:
			return 60;
		case 11967: // cannon
			return 500;
		case 6:
		case 8:
		case 10:
		case 12:
			return 125;
		case 8901:
			return 50;
		case 7582:
			return 150;
		case 15488:
		case 15490:
			return 35;
		case 18337:
			return 100;

		case 6914: // master wand
			return 85;
		case 6889: // Mages book
			return 100;
		case 15403: // balmung
			return 85;
		case 20072: // dragon defender
			return 125;
		case 14499:
		case 14497:
		case 14501:
			return 25;
		case 10548: // fighter hat
			return 75;
		case 10551: // fighter torso
			return 125;
		case 4170: // slayer staff
		case 7509: // rock cake
			return 20;
		case 13263: // slayer helm
			return 150;
		case 14494: // elite helm
		case 14492: // elite platebody
		case 14490: // elite platelegs
			return 20;
		case 2572: // ring of wealth
			return 100;
		case 15492: // full slayer helm
			return 200;
		case 19748: // ardy cloak 4
			return 75;
		case 14641:
		case 14642:
			return 75;
		case 15486:
			return 100;
		case 2581:
		case 2577: // robin & ranger
			return 200;
		case 10499: // ava's
			return 10;
		}
		return Integer.MAX_VALUE; // default
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}