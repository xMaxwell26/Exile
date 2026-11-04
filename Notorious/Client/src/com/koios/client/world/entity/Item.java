package com.koios.client.world.entity;

import com.koios.client.definition.ItemDefinition;
import com.koios.client.world.Model;

public final class Item extends Animable {

	public final Model getRotatedModel()
	{
		ItemDefinition itemDef = ItemDefinition.forID(ID);
			return itemDef.method201(anInt1559);
	}

	public Item()
	{
	}

	public int ID;
	public int x;
	public int y;
	public int anInt1559;
}
