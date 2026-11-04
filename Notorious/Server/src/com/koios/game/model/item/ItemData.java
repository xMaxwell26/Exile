package com.koios.game.model.item;

import com.koios.game.def.ItemDefinition;

public final class ItemData {

	private static final String[] NO_AVA_EFFECT = { "chestplate", "platebody", "ahrim's robetop" };

	public static boolean doesNegateAvasDeviceEffect(int id) {
		if (id == -1) {
			return false;
		}

		String itemName = ItemDefinition.forId(id).getName();

		for (String s : NO_AVA_EFFECT) {
			if (itemName.toLowerCase().contains(s.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

}
