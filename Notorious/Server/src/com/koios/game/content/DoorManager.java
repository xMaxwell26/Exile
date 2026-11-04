package com.koios.game.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.impl.SecurityStrongholdFloorOne;
import com.koios.game.content.area.impl.SecurityStrongholdFloorTwo;
import com.koios.game.map.Door;
import com.koios.game.map.DoubleDoor;
import com.koios.game.map.MapLoading;
import com.koios.game.map.MapRegion;
import com.koios.game.model.Location;
import com.koios.game.model.object.WorldObject;
import com.koios.task.Task;

public class DoorManager extends Task {

	private static List<Door> doors_list = new CopyOnWriteArrayList<Door>();

	private static List<DoubleDoor> double_doors_list = new CopyOnWriteArrayList<DoubleDoor>();

	public static final int[][] JAMMED_DOORS = { { 1852, 5212 }, { 1832, 5224 }, { 1832, 5225 }, { 2854, 3546 }, { 2855, 3546 } };

	public static boolean isDoorJammed(int x, int y, int door) {
		for (int[] i : JAMMED_DOORS) {
			if (x == i[0] && y == i[1]) {
				return true;
			}
		}

		return false;
	}

	public static boolean openDoor(int x, int y, int z) {

		if (AreaBoundary.inAnyLocation(new Location(x, y, z), SecurityStrongholdFloorOne.AREA, SecurityStrongholdFloorTwo.AREA)) {
			return false;
		}
		if (MapRegion.isDoor(x, y, z)) {
			if (isDoorJammed(x, y, z)) {
				return false;
			}
			Door door = MapRegion.getDoor(x, y, z);
			if (door == null) {
				return false;
			}

			if (z > 3) {
				z %= 4;
			}

			GameService.getObjectHandler().addObject(new WorldObject(-1, door.getX(), door.getY(), z, door.getCurrentFace(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(-1, door.getX(), door.getY(), z, door.getFace(), door.getType(), 0));
			MapRegion.getRegion(x, y).appendDoor(door.getCurrentId(), x, y, z);
			GameService.getObjectHandler().addObject(new WorldObject(door.getCurrentId(), door.getX(), door.getY(), z, door.getCurrentFace(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(door.getCurrentId(), door.getX(), door.getY(), z, door.getFace(), door.getType(), 0));
			MapLoading.addObject(false, door.getCurrentId(), door.getX(), door.getY(), z, door.getType(), door.getCurrentFace());

			return true;
		}

		if (MapRegion.isDoubleDoor(x, y, z)) {
			if (isDoorJammed(x, y, z)) {
				return false;
			}
			DoubleDoor door = MapRegion.getDoubleDoor(x, y, z);

			if (door == null) {
				return false;
			}

			if (z > 3) {
				z %= 4;
			}

			GameService.getObjectHandler().addObject(new WorldObject(-1, door.getX1(), door.getY1(), z, door.getCurrentFace1(), door.getType(), 0));
			GameService.getObjectHandler().addObject(new WorldObject(-1, door.getX2(), door.getY2(), z, door.getCurrentFace2(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(-1, door.getX1(), door.getY1(), z, door.getCurrentFace1(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(-1, door.getX2(), door.getY2(), z, door.getCurrentFace2(), door.getType(), 0));
			MapRegion.getRegion(x, y).appendDoubleDoor(door.getCurrentId1(), door.getX1(), door.getY1(), z);
			GameService.getObjectHandler().addObject(new WorldObject(door.getCurrentId1(), door.getX1(), door.getY1(), z, door.getCurrentFace1(), door.getType(), 0));
			GameService.getObjectHandler().addObject(new WorldObject(door.getCurrentId2(), door.getX2(), door.getY2(), z, door.getCurrentFace2(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(door.getCurrentId1(), door.getX1(), door.getY1(), z, door.getCurrentFace1(), door.getType(), 0));
			GameService.getObjectHandler().placeObject(new WorldObject(door.getCurrentId2(), door.getX2(), door.getY2(), z, door.getCurrentFace2(), door.getType(), 0));
			return true;
		}

		return false;
	}

	public static void addDoor() {
	}

	@Override
	protected void execute() {
		if (doors_list.isEmpty() || double_doors_list.isEmpty()) {
			return;
		}

		int index = 0;

		for (Iterator<Door> it = new LinkedList<>(doors_list).iterator(); it.hasNext();) {
			Door door = it.next();

			if (door == null) {
				it.remove();
				doors_list.remove(index);
				return;
			}

			if (System.currentTimeMillis() - door.getTimeLeft() >= 15000) {

			}

			index++;
		}

		index = 0;

		for (Iterator<DoubleDoor> it = new LinkedList<>(double_doors_list).iterator(); it.hasNext();) {
			DoubleDoor door = it.next();

			if (door == null) {
				it.remove();
				double_doors_list.remove(index);
				return;
			}

			if (System.currentTimeMillis() - door.getTimeLeft() >= 15000) {
				GameService.getObjectHandler().addObject(new WorldObject(-1, door.getX1(), door.getY1(), door.getZ(), door.getCurrentFace1(), door.getType(), 0));
				GameService.getObjectHandler().addObject(new WorldObject(-1, door.getX2(), door.getY2(), door.getZ(), door.getCurrentFace2(), door.getType(), 0));
				GameService.getObjectHandler().placeObject(new WorldObject(-1, door.getX1(), door.getY1(), door.getZ(), door.getCurrentFace1(), door.getType(), 0));
				GameService.getObjectHandler().placeObject(new WorldObject(-1, door.getX2(), door.getY2(), door.getZ(), door.getCurrentFace2(), door.getType(), 0));

				MapRegion.getRegion(door.getX1(), door.getY1()).appendDoubleDoor(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ());

				GameService.getObjectHandler().addObject(new WorldObject(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ(), door.getCurrentFace1(), door.getType(), 0));
				GameService.getObjectHandler().addObject(new WorldObject(door.getCurrentId2(), door.getX2(), door.getY2(), door.getZ(), door.getCurrentFace2(), door.getType(), 0));
				GameService.getObjectHandler()
						.placeObject(new WorldObject(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ(), door.getCurrentFace1(), door.getType(), 0));
				GameService.getObjectHandler()
						.placeObject(new WorldObject(door.getCurrentId2(), door.getX2(), door.getY2(), door.getZ(), door.getCurrentFace2(), door.getType(), 0));
			}

			index++;
		}
	}

}
