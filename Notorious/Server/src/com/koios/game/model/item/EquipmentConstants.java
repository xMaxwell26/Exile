package com.koios.game.model.item;

import com.koios.util.ItemIdentifiers;

public final class EquipmentConstants {

	public static final int[] ARROWS = { 15243, 882, 884, 886, 888, 890, 892, 4740, 11212, 9140, 9141, 4142, 9143, 9144, 9240, 9241, 9242, 9243, 9244, 9245 };

	public static final int[] BOWS = { 16887, 17295, 15241, 18357, 9185, 839, 845, 847, 851, 855, 859, 841, 843, 849, 853, 857, 861, 4212, 4214, 4215, 11235, 15701, 15702, 15703,
			15704, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 6724, 4734, 4934, 4935, 4936, 4937 };

	public static final int[] NO_ARROW_DROP = { 15241, 4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 4734, 4934, 4935, 4936, 4937 };

	public static final int[] OTHER_RANGE_WEAPONS = { 863, 864, 865, 866, 867, 868, 869, 806, 807, 808, 809, 810, 811, 825, 826, 827, 828, 829, 830, 800, 801, 802, 803, 804, 805,
			6522, ItemIdentifiers.CHINCHOMPA, ItemIdentifiers.RED_CHINCHOMPA };

	/**
	 * Private default constructor to suppress default constructor.
	 */
	private EquipmentConstants() {
		throw new InstantiationError();
	}

}
