package com.koios.game.map;

import com.koios.Constants;

public class MapConstants {

	public static final RSObject getDoubleDoor(final int id, final int x, final int y, final int z, final int face) {
		final String name = ObjectDefinition.getById(id).name;

		for (int i = 0; i < 8; i++) {
			int x2 = x + Constants.DIR[i][0];
			int y2 = y + Constants.DIR[i][1];

			RSObject o = MapRegion.getObject(x2, y2, z);
			ObjectDefinition d = null;

			if (o != null && (d = ObjectDefinition.getById(o.getId())) != null && d.name != null && d.name.equals(name) && o.getFace() == face) {
				return o;
			}
		}

		return null;
	}

	public static final int getOptionCount(ObjectDefinition def) {
		int k = 0;

		for (String i : def.actions) {
			if (i != null) {
				k++;
			}
		}

		return k;
	}

	public static final boolean isOpen(int id) {
		ObjectDefinition def = ObjectDefinition.getById(id);

		if (def.actions != null) {
			for (String i : def.actions) {
				if (i == null) {
					continue;
				}

				if (i.equals("Close")) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isReverseOrientation(int id) {
		return id == 14234 || id == 14233 || id == 31841;
	}

}
