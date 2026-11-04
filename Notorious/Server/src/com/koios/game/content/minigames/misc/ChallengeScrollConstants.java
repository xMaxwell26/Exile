package com.koios.game.content.minigames.misc;

import com.koios.game.model.Location;

public class ChallengeScrollConstants {

	public final static String MINIGAME_NAME = "CHALLENGE_SCROLL";

	public final static int[] MINIGAME_WAVES = { 3062, 3065, 3066, 3058, 8986, 3059, 7078, 8987, 3057, 8989, 3064, 3067 };

	public final static Location SPAWN_LOCATION = Location.create(3181, 9758, 0);

	public final static Location RESPAWN_LOCATION = Location.create(3185, 9758, 0);

	public final static Location MOB_SPAWN_LOCATION = Location.create(3169, 9758, 0);

	public final static int[][] MOB_STATS = { { 65, 65, 120, 12 }, { 80, 80, 140, 15 }, { 95, 95, 160, 17 }, { 110, 110, 180, 19 }, { 140, 140, 200, 21 }, { 180, 180, 260, 23 },
			{ 210, 210, 340, 27 }, { 240, 240, 400, 31 }, { 300, 300, 420, 32 }, { 360, 360, 450, 34 }, { 420, 420, 470, 37 }, { 510, 510, 520, 40 }, };

	/**
	 * Checks if the location is inside the challenge area.
	 *
	 * @param location
	 * @return
	 */
	public static boolean inChallengeArea(Location location) {
		return location.getX() <= 3181 && location.getX() >= 3157 && location.getY() <= 9765 && location.getY() >= 9751;
	}

}
