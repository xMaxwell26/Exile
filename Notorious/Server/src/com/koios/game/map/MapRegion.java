package com.koios.game.map;

import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.util.DirectionUtil;

public class MapRegion {

	private static MapRegion[] regions;

	public static int getClipping(int x, int y, int height) {
		try {
			if (height > 3) {
				height = height % 4;
			}

			int regionX = x >> 3;
			int regionY = y >> 3;
			int regionId = (regionX / 8 << 8) + regionY / 8;
			return MapLoading.regionIdTable[regionId].getClip(x, y, height);
		} catch (Exception e) {
		}

		return 0;
	}

	public static Door getDoor(int x, int y, int z) {
		MapRegion region = getRegion(x, y);

		if (z > 3) {
			z = z % 4;
		}

		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		if (region.doors[z] == null) {
			return null;
		}

		return region.doors[z][x - regionAbsX][y - regionAbsY];
	}

	public static DoubleDoor getDoubleDoor(int x, int y, int z) {
		MapRegion region = getRegion(x, y);

		if (z > 3) {
			z = z % 4;
		}

		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		return region.doubleDoors[z][x - regionAbsX][y - regionAbsY];
	}

	public static DoubleDoor[][][] getDoubleDoors(int x, int y) {
		MapRegion r = getRegion(x, y);

		return r.doubleDoors;
	}

	/**
	 * @param e the entity object to ignore
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param ignorePlayers ignore the players, only return the non-players
	 * @return an array containing the surrounding entities index ids
	 */
	public static int[] getEntitiesSurroundingTile(EntityCharacter e, int x, int y, int z, int cap, boolean ignorePlayers) {
		int[] entities = new int[cap];

		if (z > 3) {
			z = z % 4;
		}

		int thisIndex = e.getClientIndex();
		MapRegion region = getRegion(x, y);
		int rx = x - (region.id >> 8 << 6);
		int ry = y - ((region.id & 0xFF) << 6);
		final int[][] dir = DirectionUtil.DIR;

		for (int i = 0; i < region.entities[z][rx][ry].length; i++) {
			int onTile = region.entities[z][rx][ry][i];

			if (onTile == 0 || ignorePlayers && onTile > EntityCharacter.PLAYER_INDEX_MOD || onTile == thisIndex) {
				continue;
			} else {
				for (int k = 0; k < entities.length; k++) {
					if (entities[k] != 0) {
						if (k == entities.length - 1) {
							return entities;
						}

						continue;
					} else {
						entities[k] = onTile;
						break;
					}
				}
			}
		}

		for (int[] element : dir) {
			int x2 = x + element[0];
			int y2 = y + element[1];
			region = getRegion(x2, y2);
			rx = x2 - (region.id >> 8 << 6);
			ry = y2 - ((region.id & 0xFF) << 6);

			if (region.entities[z] == null) {
				continue;
			}

			for (int k = 0; k < region.entities[z][rx][ry].length; k++) {
				int onTile = region.entities[z][rx][ry][k];

				if (onTile == 0 || ignorePlayers && onTile > EntityCharacter.PLAYER_INDEX_MOD || onTile == thisIndex) {
					continue;
				} else {
					for (int j = 0; j < entities.length; j++) {
						if (entities[j] != 0) {
							if (j == entities.length - 1) {
								return entities;
							}

							continue;
						} else {
							entities[j] = onTile;
							break;
						}
					}
				}
			}
		}

		return entities;
	}

	/**
	 * @param e the entity object to ignore
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param ignorePlayers ignore the players, only return the non-players
	 * @return an array containing the surrounding entities index ids
	 */
	public static int[] getEntitiesSurroundingTile(EntityCharacter e, Location p, int cap, boolean pCombat) {
		return getEntitiesSurroundingTile(e, p.getX(), p.getY(), p.getZ(), cap, pCombat);
	}

	public static RSObject getObject(int x, int y, int z) {
		MapRegion region = getRegion(x, y);

		if (region == null) {
			return null;
		}

		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (region.objects[z] == null) {
			return null;
		}

		return region.objects[z][x - regionAbsX][y - regionAbsY];
	}

	public static int[] getObjectLength(int x, int y, int z) {
		MapRegion region = getRegion(x, y);

		if (z > 3) {
			z = z % 4;
		}

		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		if (region == null || region.objects[z] == null || region.objects[z][x - regionAbsX][y - regionAbsY] == null) {
			return new int[] { 1, 1 };
		}

		RSObject r = region.objects[z][x - regionAbsX][y - regionAbsY];
		ObjectDefinition d = ObjectDefinition.getById(r.getId());

		if (d == null) {
			return new int[] { 1, 1 };
		}

		int[] length = new int[2];

		if (r.getFace() != 1 && r.getFace() != 3) {
			length[0] = d.xLength();
			length[1] = d.yLength();
		} else {
			length[0] = d.yLength();
			length[1] = d.xLength();
		}

		return length;
	}

	/**
	 * Fetches a region after they are sorted by id.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @return the region.
	 */
	public static MapRegion getRegion(int x, int y) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = (regionX / 8 << 8) + regionY / 8;

		if (regionId > regions.length - 1) {
			return null;
		}

		if (regionId < 0) {
			System.out.println("FATAL CLIPPING ERROR: regionId < 0");
			System.exit(0);
		}

		return regions[regionId];
	}

	/**
	 * Fetches a region after they are sorted by id.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @return the region.
	 */
	public static MapRegion getRegion(Location location) {
		int regionX = location.getX() >> 3;
		int regionY = location.getY() >> 3;
		int regionId = (regionX / 8 << 8) + regionY / 8;

		if (regionId > regions.length - 1) {
			return null;
		}

		if (regionId < 0) {
			System.out.println("FATAL CLIPPING ERROR: regionId < 0");
			System.exit(0);
		}

		return regions[regionId];
	}

	public static MapRegion getRegionById(int id) {
		for (MapRegion region : regions) {
			if (region == null) {
				continue;
			}

			if (region.id() == id) {
				return region;
			}
		}

		return null;
	}

	public static MapRegion[] getRegions() {
		return regions;
	}

	public static int getStaticClip(int x, int y, int z) {
		MapRegion region = getRegion(x, y);
		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (x - regionAbsX < 0 || y - regionAbsY < 0 || x - regionAbsX > 63 || y - regionAbsY > 63) {
			return getRegion(x, y).getClip(x, y, z);
		}

		try {
			if (region.clips[z] == null) {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

		return region.clips[z][x - regionAbsX][y - regionAbsY];
	}

	public static int getStaticClip(Location p) {
		return getStaticClip(p.getX(), p.getY(), p.getZ());
	}

	/**
	 * Used to fetch a region before they are sorted by id.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @return the region.
	 */
	public static MapRegion getUnsortedRegion(int x, int y) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = (regionX / 8 << 8) + regionY / 8;

		for (MapRegion region : regions) {
			if (region == null) {
				continue;
			}

			if (region.id() == regionId) {
				return region;
			}
		}

		return null;
	}

	public static boolean isDoor(int x, int y, int z) {
		MapRegion region = getRegion(x, y);
		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (region.doors[z] == null) {
			return false;
		}

		return region.doors[z][x - regionAbsX][y - regionAbsY] != null;
	}

	public static boolean isDoubleDoor(int x, int y, int z) {
		MapRegion region = getRegion(x, y);
		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (region.doubleDoors[z] == null) {
			return false;
		}

		return region.doubleDoors[z][x - regionAbsX][y - regionAbsY] != null;
	}

	public static void removeDoor(int x, int y, int z) {
		MapRegion region = getRegion(x, y);

		if (z > 3) {
			z = z % 4;
		}

		int regionAbsX = region.id >> 8 << 6;
		int regionAbsY = (region.id & 0xFF) << 6;
		region.doors[z][x - regionAbsX][y - regionAbsY] = null;
	}

	/**
	 * Removes the entity from the tile they are currently on
	 *
	 * @param e the entity object
	 */
	public static void removeEntityOnTile(EntityCharacter e) {
		try {
			final int x = e.getLocation().getX();
			final int y = e.getLocation().getY();
			int z = e.getLocation().getZ();
			final MapRegion region = getRegion(x, y);

			if (region == null) {
				return;
			}

			final int rx = x - (region.id >> 8 << 6);
			final int ry = y - ((region.id & 0xFF) << 6);

			if (z > 3) {
				z = z % 4;
			}

			if (region.entities[z] == null) {
				return;
			}

			int index = e.getIndex();

			if (!e.isMob()) {
				index += EntityCharacter.PLAYER_INDEX_MOD;
			}

			for (int i = 0; i < region.entities[z][rx][ry].length; i++) {
				if (region.entities[z][rx][ry][i] == index) {
					region.entities[z][rx][ry][i] = 0;
					return;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void setRegions(MapRegion[] set) {
		regions = set;
	}

	public static void sort() {
		MapRegion[] sorted = new MapRegion[70000];

		for (int i = 0; i < regions.length; i++) {
			if (regions[i] == null) {
				continue;
			}

			sorted[regions[i].id()] = regions[i]; // bring me to the spot your maps are loaded
		}

		regions = sorted;
		System.out.println("Finished loading map regions!");
	}

	private final int[][][] clips;
	private final Door[][][] doors;
	private final DoubleDoor[][][] doubleDoors;
	private final short[][][][] entities;
	private final int id;
	private final byte[][][] npcs;
	private final RSObject[][][] objects;
	private final int[][][] shootable;

	public MapRegion(int id) {
		this.id = id;
		clips = new int[4][][];
		entities = new short[4][][][];
		npcs = new byte[4][][];
		shootable = new int[4][][];
		doors = new Door[4][][];
		doubleDoors = new DoubleDoor[4][][];
		objects = new RSObject[4][][];
	}

	public void addClip(int x, int y, int height, int shift) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xff) << 6;

		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}

		clips[height][x - regionAbsX][y - regionAbsY] += shift;
	}

	public void setClip(int x, int y, int height, int flag) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xff) << 6;

		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}

		clips[height][x - regionAbsX][y - regionAbsY] = flag;
	}

	public void addDoor(Door door) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (doors[door.getZ()] == null) {
			doors[door.getZ()] = new Door[64][64];
		}

		doors[door.getZ()][door.getX() - regionAbsX][door.getY() - regionAbsY] = door;
	}

	public void addDoor(int oId, int x, int y, int z, int type, int face) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (doors[z] == null) {
			doors[z] = new Door[64][64];
		}

		doors[z][x - regionAbsX][y - regionAbsY] = new Door(oId, x, y, z, type, face);
	}

	public void addDoubleDoor(DoubleDoor door) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (doubleDoors[door.getZ()] == null) {
			doubleDoors[door.getZ()] = new DoubleDoor[64][64];
		}

		doubleDoors[door.getZ()][door.getX1() - regionAbsX][door.getY1() - regionAbsY] = door;
		MapRegion region = getRegion(door.getX2(), door.getY2());
		regionAbsX = region.id >> 8 << 6;
		regionAbsY = (region.id & 0xff) << 6;

		if (region.doubleDoors[door.getZ()] == null) {
			region.doubleDoors[door.getZ()] = new DoubleDoor[64][64];
		}

		region.doubleDoors[door.getZ()][door.getX2() - regionAbsX][door.getY2() - regionAbsY] = door;
	}

	/**
	 * Adds an object to this region.
	 *
	 * @param object the object.
	 */
	public void addObject(RSObject object) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;
		int z = object.getZ();

		if (z > 3) {
			z = z % 4;
		}

		if (objects[z] == null) {
			objects[z] = new RSObject[64][64];
		}

		objects[z][object.getX() - regionAbsX][object.getY() - regionAbsY] = object;
	}

	public void addShootable(int x, int y, int z, int flag) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (shootable[z] == null) {
			shootable[z] = new int[64][64];
		}

		shootable[z][x - regionAbsX][y - regionAbsY] |= flag;
	}

	public boolean appendDoor(int oId, int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		Door door = doors[z][x - regionAbsX][y - regionAbsY];

		if (door == null) {
			return false;
		}

		MapLoading.removeObject(oId, x, y, z, door.getType(), door.getCurrentFace());
		doors[z][x - regionAbsX][y - regionAbsY] = null;
		door.append();
		MapLoading.addObject(false, door.getCurrentId(), door.getX(), door.getY(), z, door.getType(), door.getCurrentFace());
		getRegion(door.getX(), door.getY()).addDoor(door);
		return true;
	}

	public void appendDoubleDoor(int oId, int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		DoubleDoor door = doubleDoors[z][x - regionAbsX][y - regionAbsY];

		if (door == null) {
			return;
		}

		MapLoading.removeObject(door.getCurrentId1(), door.getX1(), door.getY1(), z, door.getType(), door.getCurrentFace1());
		MapLoading.removeObject(door.getCurrentId2(), door.getX2(), door.getY2(), z, door.getType(), door.getCurrentFace2());
		doubleDoors[z][door.getX1() - regionAbsX][door.getY1() - regionAbsY] = null;
		getDoubleDoors(door.getX2(), door.getY2())[z][door.getX2() - regionAbsX][door.getY2() - regionAbsY] = null;
		door.append();
		MapLoading.addObject(false, door.getCurrentId1(), door.getX1(), door.getY1(), z, door.getType(), door.getCurrentFace1());
		MapLoading.addObject(false, door.getCurrentId2(), door.getX2(), door.getY2(), z, door.getType(), door.getCurrentFace2());

		getRegion(door.getX1(), door.getY1()).addDoubleDoor(door);
		getRegion(door.getX2(), door.getY2()).addDoubleDoor(door);
	}

	public boolean blockedEast(int x, int y, int z) {
		return (getClip(x + 1, y, z) & 0x1280180) != 0;
	}

	public boolean blockedNorth(int x, int y, int z) {
		return (getClip(x, y + 1, z) & 0x1280120) != 0;
	}

	public boolean blockedNorthEast(int x, int y, int z) {
		return (getClip(x + 1, y + 1, z) & 0x12801e0) != 0;
	}

	public boolean blockedNorthWest(int x, int y, int z) {
		return (getClip(x - 1, y + 1, z) & 0x1280138) != 0;
	}

	public boolean blockedSouth(int x, int y, int z) {
		return (getClip(x, y - 1, z) & 0x1280102) != 0;
	}

	public boolean blockedSouthEast(int x, int y, int z) {
		return (getClip(x + 1, y - 1, z) & 0x1280183) != 0;
	}

	public boolean blockedSouthWest(int x, int y, int z) {
		return (getClip(x - 1, y - 1, z) & 0x128010e) != 0;
	}

	public boolean blockedWest(int x, int y, int z) {
		return (getClip(x - 1, y, z) & 0x1280108) != 0;
	}

	/**
	 * Tells you if this direction is walkable.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param direction the direction.
	 * @return if the direction is walkable.
	 */
	public boolean canMove(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !blockedNorthWest(x, y, z) && !blockedNorth(x, y, z) && !blockedWest(x, y, z);
		} else if (direction == 1) {
			return !blockedNorth(x, y, z);
		} else if (direction == 2) {
			return !blockedNorthEast(x, y, z) && !blockedNorth(x, y, z) && !blockedEast(x, y, z);
		} else if (direction == 3) {
			return !blockedWest(x, y, z);
		} else if (direction == 4) {
			return !blockedEast(x, y, z);
		} else if (direction == 5) {
			return !blockedSouthWest(x, y, z) && !blockedSouth(x, y, z) && !blockedWest(x, y, z);
		} else if (direction == 6) {
			return !blockedSouth(x, y, z);
		} else if (direction == 7) {
			return !blockedSouthEast(x, y, z) && !blockedSouth(x, y, z) && !blockedEast(x, y, z);
		}

		return false;
	}

	/**
	 * Tells you if this direction is shootable.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param direction the direction.
	 * @return if the direction is walkable.
	 */
	public boolean canShoot(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !shotBlockedWest(x, y, z) && !shotBlockedNorth(x, y, z) && !shotBlockedWest(x, y, z);
		} else if (direction == 1) {
			return !shotBlockedNorth(x, y, z);
		} else if (direction == 2) {
			return !shotBlockedNorthEast(x, y, z) && !shotBlockedNorth(x, y, z) && !shotBlockedEast(x, y, z);
		} else if (direction == 3) {
			return !shotBlockedWest(x, y, z);
		} else if (direction == 4) {
			return !shotBlockedEast(x, y, z);
		} else if (direction == 5) {
			return !shotBlockedSouthWest(x, y, z) && !shotBlockedSouth(x, y, z) && !shotBlockedWest(x, y, z);
		} else if (direction == 6) {
			return !shotBlockedSouth(x, y, z);
		} else if (direction == 7) {
			return !shotBlockedSouthEast(x, y, z) && !shotBlockedSouth(x, y, z) && !shotBlockedEast(x, y, z);
		}

		return false;
	}

	public int getClip(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (x - regionAbsX < 0 || y - regionAbsY < 0 || x - regionAbsX > 63 || y - regionAbsY > 63) {
			return 0;// getRegion(x, y) == null ? 0 : getRegion(x, y).getClip(x, y, z);
		}

		if (z >= clips.length || z < 0) {
			z = 0;
		}

		if (clips[z] == null) {
			return 0;
		}
		return clips[z][x - regionAbsX][y - regionAbsY];
	}

	public int getClip(Location location) {
		int x = location.getX();
		int y = location.getY();
		int z = location.getZ();
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (x - regionAbsX < 0 || y - regionAbsY < 0 || x - regionAbsX > 63 || y - regionAbsY > 63) {
			return getRegion(x, y).getClip(x, y, z);
		}

		if (z >= clips.length || z < 0) {
			z = 0;
		}

		if (clips[z] == null) {
			return 0;
		}

		return clips[z][x - regionAbsX][y - regionAbsY];
	}

	public RSObject[][][] getObjects() {
		return objects;
	}

	public int[][][] getShootable() {
		return shootable;
	}

	public int getShootable(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (x - regionAbsX < 0 || y - regionAbsY < 0 || x - regionAbsX > 63 || y - regionAbsY > 63) {
			return getRegion(x, y).getClip(x, y, z);
		}

		if (shootable[z] == null) {
			return 0;
		}

		return shootable[z][x - regionAbsX][y - regionAbsY];
	}

	/**
	 * @return the region id.
	 */
	public int id() {
		return id;
	}

	public boolean isNpcOnTile(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (npcs[z] == null) {
			return false;
		}

		if (z >= npcs.length || x - regionAbsX >= npcs[z].length || y - regionAbsY >= npcs[z][x - regionAbsX].length) {
			return false;
		}

		return npcs[z][x - regionAbsX][y - regionAbsY] == 1;
	}

	/**
	 * Tells you whether an object exists at this point in time.
	 *
	 * @param id the object id.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @return whether the object exists
	 */
	public boolean objectExists(int objectId, int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (objects[z] == null) {
			return false;
		}

		if (z > objects.length || x - regionAbsX > objects[z].length || y - regionAbsY > objects[z][x - regionAbsX].length) {
			return false;
		}

		return objects[z][x - regionAbsX][y - regionAbsY] != null && objects[z][x - regionAbsX][y - regionAbsY].getId() == objectId;
	}

	/**
	 * Tells you whether an object exists at this point in time.
	 *
	 * @param id the object id.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @return whether the object exists
	 */
	public boolean objectExists(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (objects == null || objects[z] == null) {
			return false;
		}

		if (z > objects.length || x - regionAbsX > objects[z].length || y - regionAbsY > objects[z][x - regionAbsX].length) {
			return false;
		}
		return objects[z][x - regionAbsX][y - regionAbsY] != null && objects[z][x - regionAbsX][y - regionAbsY].getId() > 0;
	}

	/**
	 * Reinstates an unclipped object, after it has been removed.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 */
	public void reinstateUnclippedObject(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (objects[z] == null) {
			return;
		}

		if (objects[z][x - regionAbsX][y - regionAbsY] == null) {
			return;
		}

		objects[z][x - regionAbsX][y - regionAbsY].setId(objects[z][x - regionAbsX][y - regionAbsY].getMasterId());
	}

	public void removeObject(RSObject object) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;
		int z = object.getZ();

		if (z > 3) {
			z = z % 4;
		}

		if (objects[z] == null) {
			return;
		}

		objects[z][object.getX() - regionAbsX][object.getY() - regionAbsY] = null;
	}

	public void removeObject(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xff) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (objects[z] == null) {
			return;
		}
		objects[z][x - regionAbsX][y - regionAbsY] = null;
	}

	/**
	 * Removes an unclipped object, so it will not be shown as existing.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 */
	public void removeUnclippedObject(int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (objects[z][x - regionAbsX][y - regionAbsY] == null) {
			return;
		}

		objects[z][x - regionAbsX][y - regionAbsY].setId(-1);
	}

	/**
	 * Replaces an object's id with another.
	 *
	 * @param id the object id.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 */
	public void replaceObject(int objectId, int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (z >= objects.length || z < 0 || objects[z] == null) {
			return;
		}

		if (objects[z][x - regionAbsX][y - regionAbsY] == null) {
			return;
		}

		objects[z][x - regionAbsX][y - regionAbsY].setId(objectId);
	}

	public void setMobOnTile(boolean onTile, int x, int y, int z) {
		int regionAbsX = id >> 8 << 6;
		int regionAbsY = (id & 0xFF) << 6;

		if (z > 3) {
			z = z % 4;
		}

		if (z >= npcs.length || z < 0 || npcs[z] == null) {
			npcs[z] = new byte[64][64];
		}

		if (onTile) {
			npcs[z][x - regionAbsX][y - regionAbsY] = 1;
		} else {
			npcs[z][x - regionAbsX][y - regionAbsY] = 0;
		}
	}

	public boolean shotBlockedEast(int x, int y, int z) {
		return (getShootable(x + 1, y, z) & 0x1280180) != 0;
	}

	public boolean shotBlockedNorth(int x, int y, int z) {
		return (getShootable(x, y + 1, z) & 0x1280120) != 0;
	}

	public boolean shotBlockedNorthEast(int x, int y, int z) {
		return (getShootable(x + 1, y + 1, z) & 0x12801e0) != 0;
	}

	public boolean shotBlockedNorthWest(int x, int y, int z) {
		return (getShootable(x - 1, y + 1, z) & 0x1280138) != 0;
	}

	public boolean shotBlockedSouth(int x, int y, int z) {
		return (getShootable(x, y - 1, z) & 0x1280102) != 0;
	}

	public boolean shotBlockedSouthEast(int x, int y, int z) {
		return (getShootable(x + 1, y - 1, z) & 0x1280183) != 0;
	}

	public boolean shotBlockedSouthWest(int x, int y, int z) {
		return (getShootable(x - 1, y - 1, z) & 0x128010e) != 0;
	}

	public boolean shotBlockedWest(int x, int y, int z) {
		return (getShootable(x - 1, y, z) & 0x1280108) != 0;
	}

	public boolean withinRegion(int x, int y) {
		int regionAbsX = x - (id >> 8 << 6);
		int regionAbsY = y - ((id & 0xFF) << 6);
		return !(regionAbsX < 0 || regionAbsY < 0 || regionAbsX > 63 || regionAbsY > 63);
	}

}
