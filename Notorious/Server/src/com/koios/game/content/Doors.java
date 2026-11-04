package com.koios.game.content;

import com.koios.GameService;
import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.impl.SecurityStrongholdFloorOne;
import com.koios.game.content.area.impl.SecurityStrongholdFloorTwo;
import com.koios.game.map.Door;
import com.koios.game.map.DoubleDoor;
import com.koios.game.map.MapRegion;
import com.koios.game.model.Location;
import com.koios.game.model.object.WorldObject;

public class Doors {

	public static final int[][] JAMMED_DOORS = { { 1852, 5212 }, { 1832, 5224 }, { 1832, 5225 }, { 2854, 3546 }, { 2855, 3546 } };

	public static boolean isDoorJammed(int x, int y, int z) {
		for (int[] i : JAMMED_DOORS) {
			if (x == i[0] && y == i[1]) {
				return true;
			}
		}

		return false;
	}

	public static boolean clickDoor(int x, int y, int z) {

		if (AreaBoundary.inAnyLocation(new Location(x, y, z), SecurityStrongholdFloorOne.AREA, SecurityStrongholdFloorTwo.AREA)) {
			return false;
		}

		if (MapRegion.isDoor(x, y, z)) {

			Door door = MapRegion.getDoor(x, y, z);

			if (door == null) {
				return false;
			}

			if (z > 3) {
				z %= 4;
			}

			GameService.getObjectHandler().placeObject(new WorldObject(2376, door.getX(), door.getY(), z, door.getCurrentFace(), door.getType(), 0));
			MapRegion.getRegion(x, y).appendDoor(door.getCurrentId(), x, y, z);
			GameService.getObjectHandler().placeObject(new WorldObject(door.getCurrentId(), door.getX(), door.getY(), z, door.getCurrentFace(), door.getType(), 0));
			return true;
		}

		if (MapRegion.isDoubleDoor(x, y, z)) {
			DoubleDoor door = MapRegion.getDoubleDoor(x, y, z);

			if (door == null) {
				return false;
			}

			if (z > 3) {
				z %= 4;
			}

			GameService.getObjectHandler().placeObject(new WorldObject(2376, door.getX1(), door.getY1(), z, door.getCurrentFace1(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(2376, door.getX2(), door.getY2(), z, door.getCurrentFace2(), door.getType(), 0));
			MapRegion.getRegion(x, y).appendDoubleDoor(door.getCurrentId1(), door.getX1(), door.getY1(), z);

			int face1 = door.getCurrentId1() > 30_000 ? door.getCurrentFace2() : door.getCurrentFace1();
			int face2 = door.getCurrentId2() > 30_000 ? door.getCurrentFace1() : door.getCurrentFace2();
			GameService.getObjectHandler().placeObject(new WorldObject(door.getCurrentId1(), door.getX1(), door.getY1(), z, face1, door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(door.getCurrentId2(), door.getX2(), door.getY2(), z, face2, door.getType(), 0));
			return true;
		}

		return false;
	}

}
