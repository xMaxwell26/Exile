package com.koios.game.model.item;

public class ItemBonusDefinition {

	public int[] bonuses;
	public double generalStoreValue;
	final int id;
	public int[] requirements;

	ItemBonusDefinition(int id) {
		this.id = id;
	}

}
