package com.koios.game.content.shop.currency;

import com.koios.game.content.shop.ItemCurrency;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemBonusDefinition;

public final class CoinsItemCurrency extends ItemCurrency {

	public CoinsItemCurrency() {
		super(995);
	}

	@Override
	protected int getSellPrice(Item item) {
		int currentCost = 0;
		ItemBonusDefinition def = GroundItemManager.definitions[item.getId()];
		
		if (def != null)
			currentCost = (int) (def.generalStoreValue);
		
		if (currentCost == 0) {
			ItemDefinition definition = ItemDefinition.forId(item.getId());
			currentCost = definition.getValue();
			
			if (currentCost == 0) {
				currentCost = definition.getHighAlchemy();
			}
		}
		return currentCost;
	}

	@Override
	protected int getBuyPrice(Item item) {
		int currentCost = 0;
		boolean lowerPrice = true;
		ItemBonusDefinition def = GroundItemManager.definitions[item.getId()];
		
		if (def != null)
		currentCost = (int) (def.generalStoreValue);
		
		if (currentCost == 0) {
			ItemDefinition definition = ItemDefinition.forId(item.getId());
			currentCost = definition.getValue();
			
			if (currentCost == 0) {
				currentCost = definition.getLowAlchemy();
				lowerPrice = false;
			}
		}
		
		if (lowerPrice) {
			currentCost = (int) (currentCost * 0.90);
		}
		return currentCost;
	}

}