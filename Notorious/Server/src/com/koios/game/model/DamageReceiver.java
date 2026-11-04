package com.koios.game.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Receives the damage and returns the killer
 *
 * @author Gabriel | Wolfs Darker
 */
public class DamageReceiver {

	/**
	 * Handle the damage list
	 */
	private final Map<String, Integer> damageTakenMap = new HashMap<>();

	/**
	 * Add a new damage to the map
	 */
	public void addDamage(String name, int damage) {
		if (damage <= 0) {
			return;
		}

		Integer value = damageTakenMap.get(name);
		damageTakenMap.put(name, damage + (value == null ? 0 : value));
	}

	/**
	 * Get the entity that did most damage
	 *
	 * @return the entity
	 */
	public String getKillerUsername() {
		String targetUsername = "";
		int highestDamageDealt = -1;

		for (Map.Entry<String, Integer> entry : damageTakenMap.entrySet()) {
			if (entry.getValue() > highestDamageDealt) {
				targetUsername = entry.getKey();
				highestDamageDealt = entry.getValue();
			}
		}

		return targetUsername;
	}

	/**
	 * Resets the list
	 */
	public void reset() {
		damageTakenMap.clear();
	}

}
