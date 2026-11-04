package com.koios.game.content.minigames.misc;

import com.koios.game.model.Location;

public class TzhaarFightCaveConstants {

	/**
	 * The minigame name.
	 */
	public static final String MINIGAME_NAME = "Tzhaar_Fight_Caves";

	/**
	 * Mob Ids.
	 */
	public static final int TZ_KIH = 2627, TZ_KEK_SPAWN = 2738, TZ_KEK = 2630, TOK_XIL = 2631, YT_MEJKOT = 2741, KET_ZEK = 2743, TZTOK_JAD = 2745;

	/**
	 * Array of mob ids.
	 */
	public final static int[] TZHAAR_MONSTERS = { TZ_KIH, TZ_KEK_SPAWN, TZ_KEK, TOK_XIL, YT_MEJKOT, KET_ZEK, TZTOK_JAD };

	/**
	 * Array of spawn locations.
	 */
	public final static Location SPAWN_LOCATION = new Location(2398, 5087, 0);

	/**
	 * Checks if the location is inside the fight caves.
	 *
	 * @param pos
	 * @return if is inside
	 */
	public static boolean inFightCaves(Location pos) {
		return pos.getX() > 2368 && pos.getX() < 2430 && pos.getY() > 5056 && pos.getY() < 5118;
	}

	/**
	 * Array with wave spawn configs.
	 */
	public final static int[][] WAVES = { { TZ_KIH }, { TZ_KIH, TZ_KIH }, { TZ_KEK }, { TZ_KEK, TZ_KIH }, { TZ_KEK, TZ_KIH, TZ_KIH }, { TZ_KEK, TZ_KEK }, { TOK_XIL },
			{ TOK_XIL, TZ_KIH }, { TOK_XIL, TZ_KIH, TZ_KIH }, { TOK_XIL, TZ_KEK }, { TOK_XIL, TOK_XIL }, { YT_MEJKOT, TOK_XIL }, { YT_MEJKOT, TOK_XIL, TOK_XIL },
			{ YT_MEJKOT, YT_MEJKOT }, { KET_ZEK, KET_ZEK }, { TZTOK_JAD } };

	public static int getAttack(final int mobId) {
		switch (mobId) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 100;

		case TZ_KEK:
			return 200;

		case TOK_XIL:
			return 350;

		case YT_MEJKOT:
			return 450;

		case KET_ZEK:
			return 650;

		case TZTOK_JAD:
			return 700;
		}

		return 100;
	}

	public static int getDefence(final int mobId) {
		switch (mobId) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 50;

		case TZ_KEK:
			return 100;

		case TOK_XIL:
			return 150;

		case YT_MEJKOT:
			return 175;

		case KET_ZEK:
			return 200;

		case TZTOK_JAD:
			return 225;
		}

		return 100;
	}

	public static int getHitpoints(final int mobId) {
		switch (mobId) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 10;

		case TZ_KEK:
			return 20;

		case TOK_XIL:
			return 40;

		case YT_MEJKOT:
			return 80;

		case KET_ZEK:
			return 150;

		case TZTOK_JAD:
			return 255;
		}

		return 100;
	}

	public static int getMaxHit(final int mobId) {
		switch (mobId) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 4;

		case TZ_KEK:
			return 7;

		case TOK_XIL:
			return 17;

		case YT_MEJKOT:
			return 28;

		case KET_ZEK:
			return 44;

		case TZTOK_JAD:
			return 97;
		}

		return 5;
	}

	public static final int[][] KILN_WAVES = { { TOK_XIL }, // 1
			{ TOK_XIL, TOK_XIL }, // 2
			{ TOK_XIL, TOK_XIL, TOK_XIL }, // 3
			{ TOK_XIL, TOK_XIL, TZTOK_JAD }, // 4
			{ TOK_XIL, YT_MEJKOT }, // 5
			{ TOK_XIL, YT_MEJKOT, YT_MEJKOT }, // 6
			{ TOK_XIL, YT_MEJKOT, TZTOK_JAD }, // 7
			{ TOK_XIL, TOK_XIL, YT_MEJKOT, TZTOK_JAD }, // 8
			{ YT_MEJKOT, YT_MEJKOT, TZTOK_JAD }, // 9
			{ KET_ZEK, TZTOK_JAD } }; // 10

	public static boolean isTzhaarNpc(int npcId) {
		for (int npc : TZHAAR_MONSTERS) {
			if (npc == npcId) {
				return true;
			}
		}

		return false;
	}

}
