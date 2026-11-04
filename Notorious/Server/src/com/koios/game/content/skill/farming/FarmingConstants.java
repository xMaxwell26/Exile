package com.koios.game.content.skill.farming;

import com.koios.game.model.Location;

public class FarmingConstants {

	public static boolean inRangeArea(Location position1, Location position2, Location positionChecked) {
		int x = position1.getX();
		int y = position1.getY();
		int x1 = position2.getX();
		int y1 = position2.getY();
		return positionChecked.getX() >= x && positionChecked.getY() >= y && positionChecked.getX() <= x1 && positionChecked.getY() <= y1;
	}

	public static final int WATERING_CAN_ANIM = 2293;
	public static final int RAKING_ANIM = 2273;
	public static final int SPADE_ANIM = 830;
	public static final int SEED_DIBBING = 2291;
	public static final int PICKING_VEGETABLE_ANIM = 2282;
	public static final int PICKING_HERB_ANIM = 2279;
	public static final int PUTTING_COMPOST = 2283;
	public static final int CURING_ANIM = 2288;
	public static final int PLANTING_POT_ANIM = 2272;
	public static final int PRUNING_ANIM = 2275;
	public static final int RAKE = 5341;
	public static final int SEED_DIBBER = 5343;
	public static final int SPADE = 952;
	public static final int TROWEL = 5325;
	public static final int SECATEURS = 5329;
	public static final int MAGIC_SECATEURS = 7409;

	private FarmingConstants() {
		throw new InstantiationError();
	}

}
