package com.koios.util;

import com.koios.game.def.ItemDefinition;

public class Utils {

	public static String formatCountedItem(int id, int amount) {
		ItemDefinition definition = ItemDefinition.forId(id);
		String name = definition.getName().toLowerCase();
		StringBuilder bldr = new StringBuilder();

		if (amount == 1) {
			if (name.endsWith("s")) {
				bldr.append("some");
			} else {
				bldr.append(StringUtil.getArticle(name));
			}
		} else if (amount > 1) {
			bldr.append("some");
		} else {
			throw new AssertionError("negative item amount: " + amount);
		}

		bldr.append(' ').append(name);
		if (amount > 1 && !name.endsWith("s")) {
			bldr.append('s');
		}

		return bldr.toString();
	}

	public static int hexToInt(byte[] data, int off, int len) {
		int tmp = 0;
		int val = 1000;

		for (int i = 0; i < len; i++) {
			tmp += (data[off + i] & 0xFF) * val;

			if (val > 1) {
				val = val / 1000;
			}
		}

		return tmp;
	}

}
