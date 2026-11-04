package com.koios.game.content.effects;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.effects.impl.Overload;
import com.koios.game.content.effects.impl.PrayerRenewal;

/**
 * Handle a list of all effects in game
 *
 * @author Gabriel | Wolfs Darker
 */
public class EffectList {

	private static final Map<String, Effect> list = new HashMap<>();

	/**
	 * Effects loader
	 */
	static {
		list.put(EffectConstants.PRAYER_RENEWAL_NAME, new PrayerRenewal());
		list.put(EffectConstants.OVERLOAD_NAME, new Overload());
	}

	/**
	 * Get a effect by it's name
	 */
	public static Effect getEffectByName(String name) {
		return list.get(name);
	}

}
