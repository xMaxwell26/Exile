package com.koios.game.content.degrading;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the degradable armour.
 *
 * @author Ben Stulpin
 */
public enum Armour {

	/**
	 * Vesta equipment
	 */
	VESTA_CHAINBODY(13887, 13889, 4, 1000), VESTA_PLATESKIRT(13893, 13895, 7, 1000), VESTA_LONGSWORD(13899, 13901, 3, 1000), VESTA_SPEAR(13905, 13907, 3,
			1000), CORRUPT_VESTA_CHAINBODY(13911, 13913, 4, 500), CORRUPT_VESTA_PLATESKIRT(13917, 13919, 7, 500), CORRUPT_VESTA_LONGSWORD(13923, 13925, 3,
					500), CORRUPT_VESTA_SPEAR(13929, 13931, 3, 500),

	/**
	 * Zuriel equipment.
	 */
	ZURIEL_ROBE_TOP(13858, 13860, 4, 1000), ZURIEL_ROBE_BOTTOM(13861, 13863, 7, 1000), ZURIEL_HOOD(13864, 13866, 0, 1000), ZURIEL_STAFF(13867, 13869, 3,
			1000), CORRUPT_ZURIEL_ROBE_TOP(13932, 13934, 4, 500), CORRUPT_ZURIEL_ROBE_BOTTOM(13935, 13937, 7, 500), CORRUPT_ZURIEL_HOOD(13938, 13940, 0,
					500), CORRUPT_ZURIEL_STAFF(13941, 13943, 3, 500),

	/**
	 * Morrigan equipment.
	 */
	MORRIGAN_LEATHER_BODY(13870, 13872, 4, 1000), MORRIGAN_LEATHER_CHAPS(13873, 13875, 7, 1000), MORRIGAN_COIF(13876, 13878, 0, 1000), CORRUPT_MORRIGAN_LEATHER_BODY(13944, 13946,
			4, 500), CORRUPT_MORRIGAN_LEATHER_CHAPS(13947, 13949, 7, 500), CORRUPT_MORRIGAN_COIF(13950, 13952, 0, 500),

	/**
	 * Statius equipment.
	 */
	STATIUS_PLATEBODY(13884, 13886, 4, 1000), STATIUS_PLATELEG(13890, 13892, 7, 1000), STATIUS_FULL_HELM(13896, 13898, 0, 1000), STATIUS_WARHAMMER(13902, 13904, 3,
			1000), CORRUPT_STATIUS_PLATEBODY(13908, 13910, 4, 500), CORRUPT_STATIUS_PLATELEG(13914, 13916, 7, 500), CORRUPT_STATIUS_FULL_HELM(13920, 13922, 0,
					500), CORRUPT_STATIUS_WARHAMMER(13926, 13904, 3, 500);

	/**
	 * We get the armour using the id.
	 *
	 * @param id the id.
	 * @return the armour.
	 */
	public static Armour get(int id) {
		return values.get(id);
	}

	/**
	 * The degradeable armours.
	 */
	private static final Map<Integer, Armour> values;

	/**
	 * The id.
	 */
	private final int id;

	/**
	 * The degraded id.
	 */
	private final int degradedId;

	/**
	 * The slot.
	 */
	private final int slot;

	/**
	 * The health.
	 */
	private final int health;

	static {
		values = new HashMap<>(Armour.values().length << 1);

		for (Armour armour : Armour.values()) {
			values.put(armour.getId(), armour);
			values.put(armour.getDegradedId(), armour);
		}
	}

	/**
	 * Creates a new degradeable armour.
	 *
	 * @param id the id.
	 * @param degradedId the degraded id.
	 * @param slot the slot.
	 * @param health the health.
	 */
	private Armour(int id, int degradedId, int slot, int health) {
		this.id = id;
		this.degradedId = degradedId;
		this.slot = slot;
		this.health = health;
	}

	/**
	 * Gets the degraded id.
	 *
	 * @return the degraded id.
	 */
	public int getDegradedId() {
		return degradedId;
	}

	/**
	 * Gets the health.
	 *
	 * @return the health.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the slot.
	 *
	 * @return the slot.
	 */
	public int getSlot() {
		return slot;
	}

}
