package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.ItemCurrency;
import com.koios.game.model.item.Item;

public final class StardustItemCurrency extends ItemCurrency {

	public StardustItemCurrency() {
		super(13727);
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {
		case 21000:
			return 15;// herb box
		case 21002:
			return 5000;// occult necklace
		case 21391:
			return 5000;// gilded boots
		case 21422:// 3rd age
		case 21437:// 3rd age
		case 21426:// 3rd age
			return 10000;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}