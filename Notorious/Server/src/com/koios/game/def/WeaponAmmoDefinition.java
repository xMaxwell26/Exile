package com.koios.game.def;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koios.util.XStreamLibrary;

/**
 * Ranged weapon loader.
 *
 * @author Gabbe
 */
public class WeaponAmmoDefinition {

	public static WeaponAmmoDefinition getWeapon(int id) {
		return ammo.get(id);
	}

	@SuppressWarnings("unchecked")
	public static void init() throws FileNotFoundException {
		ammo = new HashMap<>();
		List<WeaponAmmoDefinition> loaded = (ArrayList<WeaponAmmoDefinition>) XStreamLibrary.load("./data/definitions/weapon_ammo_definitions.xml");

		for (WeaponAmmoDefinition rangedAmmo : loaded) {
			for (int weaponId : rangedAmmo.getWeaponId()) {
				ammo.put(weaponId, rangedAmmo);
			}
		}
	}

	private int[] weaponId;

	private int[] arrowsId;

	private static Map<Integer, WeaponAmmoDefinition> ammo;

	public int[] getArrows() {
		return arrowsId;
	}

	public int[] getWeaponId() {
		return weaponId;
	}

}
