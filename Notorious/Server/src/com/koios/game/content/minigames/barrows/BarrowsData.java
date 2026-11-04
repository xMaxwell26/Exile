package com.koios.game.content.minigames.barrows;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class BarrowsData {

	/**
	 * Grabs the coffin by the dig area.
	 */
	public static int digSpaceIndex(Player player) {
		if (isAtAhrimsMound(player.getLocation())) {
			return 0;
		} else if (isAtDharoksMound(player.getLocation())) {
			return 1;
		} else if (isAtGuthansMound(player.getLocation())) {
			return 2;
		} else if (isAtKarilsMound(player.getLocation())) {
			return 3;
		} else if (isAtToragsMound(player.getLocation())) {
			return 4;
		} else if (isAtVeracsMound(player.getLocation())) {
			return 5;
		}

		return -1;
	}

	public static Item getRandomLoot(int[] array, int type) {
		int item = array[NumberUtil.random(array.length - 1)];
		int amount = 1000 / (type <= 0 ? 1 : 500 * type);
		return new Item(item, amount <= 0 ? NumberUtil.random(1, 5) : amount);
	}

	public static boolean inBarrows(Location pos) {
		return pos.getX() > 3520 && pos.getX() < 3598 && pos.getY() > 9653 && pos.getY() < 9750 || pos.getX() > 3532 && pos.getX() < 3582 && pos.getY() > 9676 && pos.getY() < 9721;
	}

	public static boolean isAtAhrimsMound(Location pos) {
		return pos.getX() >= 3561 && pos.getX() <= 3567 && pos.getY() >= 3286 && pos.getY() <= 3292;
	}

	public static boolean isAtDharoksMound(Location pos) {
		return pos.getX() >= 3570 && pos.getX() <= 3576 && pos.getY() >= 3293 && pos.getY() <= 3299;
	}

	public static boolean isAtGuthansMound(Location pos) {
		return pos.getX() >= 3574 && pos.getX() <= 3579 && pos.getY() >= 3279 && pos.getY() <= 3284;
	}

	public static boolean isAtKarilsMound(Location pos) {
		return pos.getX() >= 3563 && pos.getX() <= 3568 && pos.getY() >= 3273 && pos.getY() <= 3279;
	}

	public static boolean isAtToragsMound(Location pos) {
		return pos.getX() >= 3550 && pos.getX() <= 3555 && pos.getY() >= 3280 && pos.getY() <= 3285;
	}

	public static boolean isAtVeracsMound(Location pos) {
		return pos.getX() >= 3554 && pos.getX() <= 3559 && pos.getY() >= 3294 && pos.getY() <= 3300;
	}

	public static boolean isBarrowsObject(int object) {
		for (int element : COFFIN_IDS) {
			if (object == element) {
				return true;
			}
		}

		return false;
	}

	public static boolean isInBarrowsTomb(Location pos) {
		return pos.getX() >= 3532 && pos.getX() <= 3579 && pos.getY() >= 9676 && pos.getY() <= 9721;
	}

	public static void moveThroughDoor(final Player p, Location objLoc, boolean n) {
		int x = 0;
		int y = 0;

		if (n) {
			if (p.getLocation().getX() > objLoc.getX() || p.getLocation().getX() == objLoc.getX()) {
				x = -1;
			} else {
				x = 1;
			}

			y = 0;
		} else {
			if (p.getLocation().getY() > objLoc.getY() || p.getLocation().getY() == objLoc.getY()) {
				y = -1;
			} else {
				y = 1;
			}

			x = 0;
		}

		p.setPosition(Location.create(p.getLocation().getX() + x, p.getLocation().getY() + y, p.getLocation().getZ()));
	}

	public static final int[] BARROW_LOOT = { 4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753, 4755,
			4757, 4749, };

	public static final int[] BROTHER_IDS = { 2025, 2026, 2027, 2028, 2029, 2030 };

	public static final String[] BROTHER_NAME = { "Ahrim", "Dharok", "Guthan", "Karil", "Torag", "Verac" };

	public static final Achievement[] BROTHER_ACHIEVEMENTS = { Achievement.KILL_AHRIMS, Achievement.KILL_DHAROK, Achievement.KILL_GUTHANS, Achievement.KILL_KARILS,
			Achievement.KILL_TORAGS, Achievement.KILL_VERACS };

	public static final int[] COFFIN_IDS = { 6821, 6771, 6773, 6822, 6772, 6823 };

	public static final Location[] DIG_POSITION = { Location.create(3557, 9703, 3), Location.create(3556, 9718, 3), Location.create(3534, 9704, 3), Location.create(3546, 9684, 3),
			Location.create(3568, 9683, 3), Location.create(3578, 9706, 3) };

	public static final int[] DOOR_IDS = { 6726, 6745, 6736, 6717, 6750, 6731, 6748, 6729, 6737, 6718, 6738, 6719, 6735, 6716, 6740, 6721, 6739, 6720, 6723, 6742, 6722, 6741, 6744,
			6725, 6747, 6728, 6749, 6730, 6746, 6727, 6743, 6724, };

	public static final boolean[] DOORS_TYPE = { false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true,
			false, false, true, true, false, false, true, true, false, false, true, true, };

	public static final int[] MISC_LOOT = { 560, 565, 562, 4740, 995 };

	public static final int[] NOTED_LOOT = { 1080, 1114, 1148, 1164, 1202, 1214, 1276, 1304, 1320, 1334, 1402, 1404, };

	public static final Location[] SPAWNL_LOCATIONS = { new Location(3557, 9700, 3), new Location(3553, 9716, 3), new Location(3540, 9705, 3), new Location(3549, 9681, 3),
			new Location(3568, 9685, 3), new Location(3575, 9708, 3) };

}
