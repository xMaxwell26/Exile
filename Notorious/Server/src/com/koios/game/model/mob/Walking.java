package com.koios.game.model.mob;

import com.koios.Constants;
import com.koios.game.map.MapRegion;
import com.koios.game.map.path.SimplePathWalker;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.util.NumberUtil;

public class Walking {

	private static MapRegion region = null;

	public static MapRegion getRegion(int x, int y) {return MapRegion.getRegion(x,y);
	}

	public static void randomWalk(Mob mob) {
		if (!mob.getEF().canWalk(mob, false)) {
			return;
		}

		if (NumberUtil.random(15) == 0) {
			int dir = getRandomDirection(mob);

			if (dir != -1) {
				SimplePathWalker.walkToNextTile(mob, new Location(mob.getX() + Constants.DIR[dir][0], mob.getY() + Constants.DIR[dir][1]));
			}
		}
	}

	public static void walk(Mob mob, int dir) {
		// TODO: check what is placement
		if (dir == -1 || /* (mob.isPlacement()) || */!mob.getEF().canWalk(mob, false) || mob.getDirection1() != -1) {
			return;
		}

		if (mob.isDead()) {
			return;
		}

		if (mob.getMovementHandler().isStopMovement()) {
			return;
		}

		setMobOnTile(mob, false);
		mob.setDirection1(dir);
		mob.getMovementHandler().getLastLocation().setAs(mob.getLocation());
		mob.getLocation().move(Constants.DIR[dir][0], Constants.DIR[dir][1]);
		setMobOnTile(mob, true);
		mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		mob.setDirection1(dir);
	}

	public static void setMobOnTile(Mob mob, boolean set) {
		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();
		int size = mob.getSize();

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < Constants.SIZES[i].length; k++) {
				int x2 = x + Constants.SIZES[i][k][0];
				int y2 = y + Constants.SIZES[i][k][1];
				MapRegion mapRegion = getRegion(x2, y2);

				if (mapRegion == null) {
					return;
				}

				mapRegion.setMobOnTile(set, x2, y2, z);
			}
		}
	}

	public static boolean canMoveTo(Mob mob, int direction) {
		if (direction == -1) {
			return false;
		}

		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();
		int x5 = mob.getLocation().getX() + Constants.DIR[direction][0];
		int y5 = mob.getLocation().getY() + Constants.DIR[direction][1];
		int size = mob.getSize();

		boolean familiar = mob.isFamiliar();

		if (size < 1) {
			size = 1;
		}

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < Constants.SIZES[i].length; k++) {
				int x3 = x + Constants.SIZES[i][k][0];
				int y3 = y + Constants.SIZES[i][k][1];
				int x2 = x5 + Constants.SIZES[i][k][0];
				int y2 = y5 + Constants.SIZES[i][k][1];
				if (Constants.withinBlock(x, y, size, x2, y2)) {
					continue;
				}

				if (familiar && mob.summoner != null && mob.summoner.getX() == x2 && mob.summoner.getY() == y2) {
					return false;
				}

				MapRegion region1 = getRegion(x3, y3);
				MapRegion region2 = getRegion(x2, y2);

				if (region1 == null || region2 == null) {
					return false;
				}

				if (!region1.canMove(x3, y3, z, direction)) {
					return false;
				}

				if (region2.isNpcOnTile(x2, y2, z)) {
					return false;
				}

				for (int j = 0; j < 8; j++) {
					if (Constants.withinBlock(x5, y5, size, x2 + Constants.DIR[j][0], x2 + Constants.DIR[j][1]) && !MapRegion.getRegion(x2, x2).canMove(x2, x2, z, j)) {
						return false;
					}
				}
			}
		}

		if (Constants.DIR[direction][0] != 0 && Constants.DIR[direction][1] != 0) {
			return canMoveTo(mob, Constants.getDirection(Constants.DIR[direction][0], 0)) && canMoveTo(mob, Constants.getDirection(0, Constants.DIR[direction][1]));
		}

		return true;
	}

	public static boolean canMoveTo(Mob mob, int x, int y, int direction, int size) {
		if (direction == -1) {
			return false;
		}

		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();
		int x5 = mob.getLocation().getX() + Constants.DIR[direction][0];
		int y5 = mob.getLocation().getY() + Constants.DIR[direction][1];
		int x4 = 0;
		int y4 = 0;

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < Constants.SIZES[i].length; k++) {
				int x3 = x + Constants.SIZES[i][k][0];
				int y3 = y + Constants.SIZES[i][k][1];
				int x2 = x5 + Constants.SIZES[i][k][0];
				int y2 = y5 + Constants.SIZES[i][k][1];

				if (!Constants.withinBlock(x, y, size, x2, y2)) {
					if (!getRegion(x3, y3).canMove(x3, y3, z, direction)) {
						return false;
					}

					if (x2 == x4 && y2 == y4) {
						return false;
					}

					for (int j = 0; j < 8; j++) {
						int x6 = x3 + Constants.DIR[j][0];
						int y6 = y3 + Constants.DIR[j][1];

						if (Constants.withinBlock(x5, y5, size, x6, y6) && !getRegion(x3, y3).canMove(x3, y3, z, j)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public static int getRandomDirection(Mob mob) {
		int count = 0;

		byte[] walkable = new byte[8];

		for (byte i = 0; i < 8; i = (byte) (i + 1)) {
			if (withinMaxDistance(mob, i) && mob.getMovementHandler().canMoveTo(i)) {
				walkable[count] = i;
				count++;
			}
		}

		if (count > 0) {
			return walkable[NumberUtil.random(count)];
		}

		return -1;
	}

	public static boolean withinMaxDistance(Mob mob, int direction) {
		int xMax = mob.homeLocation.getX() + Constants.MOB_RANDOM_WALK_DISTANCE;
		int yMax = mob.homeLocation.getY() + Constants.MOB_RANDOM_WALK_DISTANCE;
		int xMin = mob.homeLocation.getX() - Constants.MOB_RANDOM_WALK_DISTANCE;
		int yMin = mob.homeLocation.getY() - Constants.MOB_RANDOM_WALK_DISTANCE;
		int newX = mob.getLocation().getX() + Constants.DIR[direction][0];
		int newY = mob.getLocation().getY() + Constants.DIR[direction][1];
		return newX <= xMax && newY <= yMax && newX >= xMin && newY >= yMin;
	}

	public static boolean withinMaxDistance(Mob mob) {
		int xMax = mob.homeLocation.getX() + Constants.MOB_RANDOM_WALK_DISTANCE;
		int yMax = mob.homeLocation.getY() + Constants.MOB_RANDOM_WALK_DISTANCE;
		int xMin = mob.homeLocation.getX() - Constants.MOB_RANDOM_WALK_DISTANCE;
		int yMin = mob.homeLocation.getY() - Constants.MOB_RANDOM_WALK_DISTANCE;
		int newX = mob.getLocation().getX();
		int newY = mob.getLocation().getY();
		return newX <= xMax && newY <= yMax && newX >= xMin && newY >= yMin;
	}

}
