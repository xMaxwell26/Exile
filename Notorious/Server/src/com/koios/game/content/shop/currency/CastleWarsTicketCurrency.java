package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.ItemCurrency;
import com.koios.game.model.item.Item;

public final class CastleWarsTicketCurrency extends ItemCurrency {

	public CastleWarsTicketCurrency() {
		super(4067);
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {
		case 2572:
			return 150;
		case 18746:
		case 18745:
		case 18744:
			return 100;
		case 18747:
			return 200;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}