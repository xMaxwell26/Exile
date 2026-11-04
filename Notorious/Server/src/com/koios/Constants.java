package com.koios;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.koios.game.content.randoms.MysteryBoxRandomEvent;
import com.koios.game.map.MapRegion;
import com.koios.game.model.Location;
import com.koios.util.StringUtil;

public final class Constants {

	public static final List<Long> SUPER_ADMINISTRATORS = Arrays.asList("logan", "jedzio").stream().mapToLong(StringUtil::usernameToHash)
			.boxed().collect(Collectors.toList());

	public static final String DEFAULT_FRIENDSCHAT = "Logan";

	public static final String SERVER_NAME = "Exile"; 

	public static final String[] UPDATE_MESSAGES = {};

	public static final boolean DOUBLE_EXP = false;
	public static final boolean DOUBLE_PVP_POINTS = false;

	/**
	 * The announcements that are periodically broadcasted to every online player.
	 */
	public static final String[] ANNOUNCEMENTS = new String[] { "@blu@[Broadcast]@bla@ Don't forget to use the ::vote command every 12 hours!",
			"@blu@[Broadcast]@bla@ Need help or want to chat? Join the the '" + DEFAULT_FRIENDSCHAT + "' friends chat.",
			"@blu@[Broadcast]@bla@ Donations ('::donate') help the server grow & brings more updates!",
			"@blu@[Broadcast]@bla@ Keep active in-game & on the forums for a better chance at ranks!",
			"@blu@[Broadcast]@bla@ Remember to ::register on the forums to stay updated with everything!",
			"@blu@[Broadcast]@bla@ Please make good use of the 'Report Abuse' function!",
			"@blu@[Broadcast]@bla@ Please post any suggestions on the forums! @red@(::forums)",
			"@blu@[Broadcast]@bla@ Did you know you can mine runite ore near the king black dragon?",
			"@blu@[Broadcast]@bla@ Did you know you can zoom in by using ALT + the mouse wheel?",
			"@blu@[Broadcast]@bla@ Did you know you can create skill goals from the skill tab?" };

	/**
	 * Server Setup
	 */
	public static final int MAX_PLAYERS = 256;
	public static final int MAX_MOBS = 11000;
	public static final int CLIENT_VERSION = 2;
	public static final int MAX_PACKETS_PROCESSED = 10;
	public static final int CONNECTION_DELAY = 1000;
	public static final String MYSQL_HOST_ADDRESS = "srv.zarpor.org:3306";
	public static final boolean WORLD_LIST_FIX = false;
	public static final int START_LOCATION_X = 3080;
	public static final int START_LOCATION_Y = 3482;
	public static final int RESPAWN_X = 3088; // Respawn coordinates
	public static final int RESPAWN_Y = 3503;
	public static final int ITEM_LIMIT = 22500; // Item id limit
	public static final int MOB_RANDOM_WALK_DISTANCE = 10; // the square created
	public static final boolean COMBAT_LEVEL_DIFFERENCE = true;
	public static final int SKULL_TIMER = 1200; // how long does the skull last?
	public static final int KILLS_BEFORE_PKP_IP_GAIN = 1;
	public static final int NO_TELEPORT_WILD_LEVEL = 20; // level you can't tele
	public static final boolean MAGIC_LEVEL_REQUIRED = true; // need magic level
	public static final boolean RUNES_REQUIRED = true; // magic rune required?
	public static final int GOD_SPELL_CHARGE = 300000; // how long does god

	public static final int MAGIC_EXP_RATE = 600;
	public static final int MELEE_EXP_RATE = 650; // damage * exp rate
	public static final int RANGE_EXP_RATE = 600;
	public static final int PRAYER_EXPERIENCE = 210;
	public static final int COOKING_EXPERIENCE = 25 * (DOUBLE_EXP ? 2 : 1);
	public static final int FISHING_EXPERIENCE = 25 * (DOUBLE_EXP ? 2 : 1);
	public static final int WOODCUTTING_EXPERIENCE = 25 * (DOUBLE_EXP ? 2 : 1);
	public static final int MINING_EXPERIENCE = 55 * (DOUBLE_EXP ? 2 : 1);
	public static final int RUNECRAFTING_EXPERIENCE = 60 * (DOUBLE_EXP ? 2 : 1);
	public static final int THIEVING_EXPERIENCE = 40 * (DOUBLE_EXP ? 2 : 1);
	public static final int HERBLORE_EXPERIENCE = 35 * (DOUBLE_EXP ? 2 : 1);
	public static final int FIREMAKING_EXPERIENCE = 22 * (DOUBLE_EXP ? 2 : 1);
	public static final int HUNTER_EXPERIENCE = 35 * (DOUBLE_EXP ? 2 : 1);
	public static final int SMITHING_EXPERIENCE = 45 * (DOUBLE_EXP ? 2 : 1);
	public static final int SLAYER_EXPERIENCE = 350 * (DOUBLE_EXP ? 2 : 1);
	public static final int AGILITY_EXPERIENCE = 50 * (DOUBLE_EXP ? 2 : 1);
	public static final int FLETCHING_EXPERIENCE = 35 * (DOUBLE_EXP ? 2 : 1);
	public static final int FARMING_EXPERIENCE = 50 * (DOUBLE_EXP ? 2 : 1);
	public static final int SUMMONING_EXPERIENCE = 15 * (DOUBLE_EXP ? 2 : 1);
	public static final int CRAFTING_EXPERIENCE = 15 * (DOUBLE_EXP ? 2 : 1);
	
	// config magic words
	public static final String MYSQL_TABLE = "sqlTable";
	public static final String MYSQL_ADDRESS = "sqlAddress";
	public static final String MYSQL_USERNAME = "sqlUsername";
	public static final String MYSQL_PASSWORD = "sqlPassword";
	public static final String MYSQL_DATABASE = "sqlDatabase";

	public static final int[] DAGANNOTHS = { 1338, 1339, 1340, 1341, 1342, 1343, 1347, 1356, 2881, 2882, 2883 };
	public static final int[] UNDEAD_MOB_ID = { 90, 91, 92, 93, 94, 103, 104, 73, 74, 75, 76, 77, }; // Undead
	// NPCs
	public static final int EDGEVILLE_X = 3087;
	public static final int EDGEVILLE_Y = 3500;
	public static final int[] FUN_WEAPONS = { 2460, 2461, 2462, 2463, 2464, 2465, 2466, 2467, 2468, 2469, 2470, 2471, 2471, 2473, 2474, 2475, 2476, 2477, };

	public static final int[] UNDROPPABLE_ITEMS = { 1637, 15084, 15085, 15086, 15087, 15088, 15089, 15090, 15091, 15092, 15093, 15094, 15095, 15096, 15097, 15098, 15099, 15100,
			15101, 15102, 15103, 15104, MysteryBoxRandomEvent.RANDOM_MYSTERY_BOX };

	/**
	 * The maximum amount of items in a stack.
	 */
	public static final int MAX_ITEMS = 20694;
	public static final int MAX_COUNT = Integer.MAX_VALUE;
	// public static boolean LIVE_MODE = false;
	public static double SERVER_EXP_BONUS = 1; // server xp bonus?

	/**
	 * Suppress the default constructor to prevent instantiation.
	 */
	private Constants() {
		throw new InstantiationError();
	}

	public static final int[][][] SIZES = { { { 0, 0 } }, { { 0, 0 } }, // 1
			{ { 0, 1 }, { 1, 0 }, { 1, 1 } }, // 2
			{ { 2, 0 }, { 2, 1 }, { 2, 2 }, { 1, 2 }, { 0, 2 } }, // 3
			{ { 3, 0 }, { 3, 1 }, { 3, 2 }, { 3, 3 }, { 2, 3 }, { 1, 3 }, { 0, 3 } }, // 4
			{ { 4, 0 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 4, 4 }, { 3, 4 }, { 2, 4 }, { 1, 4 }, { 0, 4 } }, // 5
			{ { 5, 0 }, { 5, 1 }, { 5, 2 }, { 5, 3 }, { 5, 4 }, { 5, 5 }, { 4, 5 }, { 3, 5 }, { 2, 5 }, { 1, 5 }, { 0, 5 } }, // 6
	};

	public static final int[][] DIR = { { -1, 1 }, { 0, 1 }, { 1, 1 }, { -1, 0 }, { 1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 } };

	public static int getDirection(int x, int y, int x2, int y2) {
		int xDiff = x2 - x;
		int yDiff = y2 - y;

		for (int i = 0; i < DIR.length; i++) {
			if (xDiff == DIR[i][0] && yDiff == DIR[i][1]) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Gets if the coordinates are within a block
	 *
	 * @param blockX
	 * @param blockY
	 * @param size
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean withinBlock(int blockX, int blockY, int size, int x, int y) {
		return x - blockX < size && x - blockX > -1 && y - blockY < size && y - blockY > -1;
	}

	public static int getDirection(int x, int y) {
		for (int i = 0; i < 8; i++) {
			if (DIR[i][0] == x && DIR[i][1] == y) {
				return i;
			}
		}

		return -1;
	}

	public static Location getClearAdjacentLocation(Location location, int size) {
		int x = location.getX();
		int y = location.getY();
		int z = location.getZ();

		int lowDist = 99999;
		int lowX = 0;
		int lowY = 0;

		main: for (int dir = 0; dir < DIR.length; dir++) {
			int nextX = x + DIR[dir][0];
			int nextY = y + DIR[dir][1];

			for (int width = 0; width < size; width++) {
				int y3 = nextY + width;

				for (int length = 0; length < size; length++) {
					int x3 = nextX + length;

					MapRegion r = MapRegion.getRegion(x3, y3);

					if (r != null) {
						for (int i = 1; i < 5; i += (i == 1 || i == 4) ? 2 : 1) {
							if (!r.canMove(x3, y3, z, i)) {
								continue main;
							}
						}
					}
				}
			}

			int dist = Location.getManhattanDistance(nextX, nextX, x, y);

			if (dist < lowDist) {
				lowX = nextX;
				lowY = nextY;
				lowDist = dist;
			}
		}

		return lowX != 0 ? new Location(lowX, lowY, z) : null;
	}

	public static Location[] getBorder(Location loc, int size) {
		if (size <= 0) {
			size = 1;
		}

		int x = loc.getX();
		int y = loc.getY();

		if (size == 1) {
			return new Location[] { loc };
		}

		Location[] border = new Location[4 * (size - 1)];
		int j = 0;
		border[0] = new Location(x, y);

		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < (i < 3 ? size - 1 : size - 2); k++) {
				if (i == 0) {
					x++;
				} else if (i == 1) {
					y++;
				} else if (i == 2) {
					x--;
				} else if (i == 3) {
					y--;
				}

				border[++j] = new Location(x, y);
			}
		}

		return border;
	}

	public static Location[] getEdges(int x, int y, int size) {
		if (size == 1) {
			return new Location[] { new Location(x, y) };
		}

		return new Location[] { new Location(x, y), new Location(x + size, y), new Location(x + size, y + size), new Location(x, y + size) };
	}

	public static Location[] getBorder(int x, int y, int size) {
		if (size == 1) {
			return new Location[] { new Location(x, y) };
		}

		Location[] border = new Location[4 * (size - 1)];
		int j = 0;

		border[0] = new Location(x, y);

		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < (i < 3 ? size - 1 : size - 2); k++) {
				if (i == 0) {
					x++;
				} else if (i == 1) {
					y++;
				} else if (i == 2) {
					x--;
				} else if (i == 3) {
					y--;
				}

				border[++j] = new Location(x, y);
			}
		}

		return border;
	}

	public static boolean withinBlock(int blockX, int blockY, int blockSize, int x, int y, int checkSize) {
		for (int i = 1; i < checkSize + 1; i++) {
			for (int k = 0; k < SIZES[i].length; k++) {
				int x2 = x + SIZES[i][k][0];
				int y2 = y + SIZES[i][k][1];

				if (x2 - blockX < blockSize && x2 - blockX > -1 && y2 - blockY < blockSize && y2 - blockY > -1) {
					return true;
				}
			}
		}

		return false;
	}

}
