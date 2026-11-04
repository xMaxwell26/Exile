package com.koios.game.model.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendObjectPacket;

/**
 * @author Sanity
 */
public class ObjectHandler {

	private static final int VIEW_DISTANCE = 64;

	public final List<WorldObject> globalObjects = new ArrayList<>();

	/**
	 * Adds object to list
	 */
	public void addObject(WorldObject object) {
		globalObjects.add(object);
	}

	public WorldObject createAnObject(final int id, final int x, final int y, final int face) {
		WorldObject object = new WorldObject(id, x, y, 0, face, 10, 0);

		if (id == -1) {
			removeObject(object);
		} else {
			addObject(object);
		}

		placeObject(object);
		return object;
	}

	public void createAnObject(Player player, int id, int x, int y) {
		WorldObject object = new WorldObject(id, x, y, player.getZ(), -1, 10, 0);

		if (id == -1) {
			removeObject(object);
		} else {
			addObject(object);
		}

		// GameService.getObjectHandler().placeObject(object);
		GameService.getObjectHandler().placeObject(object);
	}

	/**
	 * Does object exist
	 */
	public WorldObject get(int x, int y, int z) {
		for (WorldObject object : globalObjects) {
			if (object.getX() == x && object.getY() == y && object.getZ() == z) {
				return object;
			}
		}

		return null;
	}

	public boolean doesWorldObjectExist(Location position) {
		for (WorldObject obj : globalObjects) {
			if (obj.getPosition().equals(position)) {
				return true;
			}
		}

		return GameService.getObjectManager().doesObjectExist(position.getX(), position.getY());
	}

	/**
	 * Creates the object for anyone who is within VIEW_DISTANCE squares of the object
	 */
	public WorldObject placeObject(WorldObject object) {
		removeAllObjects(object);

		if (object.getID() != -1) {
			globalObjects.add(object);
		}

		GameService.getWorld().execute(p -> true, p -> {
			if (p.getZ() == object.getZ() && object.ticks == 0 && p.distanceToPoint(object.getX(), object.getY()) <= VIEW_DISTANCE) {
				p.getSession().send(new SendObjectPacket(object.getID(), object.getX(), object.getY(), object.getZ(), object.getFace(), object.getType()));
			}
		});
		return object;
	}

	private void removeAllObjects(WorldObject object) {
		for (WorldObject obj : globalObjects) {
			if (obj.getX() == object.getX() && obj.getY() == object.getY() && obj.getZ() == object.getZ()) {
				globalObjects.remove(obj);
				break;
			}
		}
	}

	/**
	 * Removes object from list
	 */
	public void removeObject(WorldObject object) {
		WorldObject toRemove = null;

		for (WorldObject obj : globalObjects) {
			if (object.getPosition().equals(obj.getPosition())) {
				toRemove = obj;
				break;
			}
		}

		if (toRemove != null) {
			globalObjects.remove(toRemove);
		}
	}

	/**
	 * Update objects when entering a new region or logging in
	 */
	public void updateObjects(Player player) {
		Objects.requireNonNull(player);

		for (WorldObject object : globalObjects) {
			if (player.getZ() == object.getZ() && object.ticks == 0) {
				if (player.distanceToPoint(object.getX(), object.getY()) <= VIEW_DISTANCE) {
					player.getSession().send(new SendObjectPacket(object.getID(), object.getX(), object.getY(), object.getZ(), object.getFace(), object.getType()));
				}
			}
		}

		if (player.distanceToPoint(2961, 3389) <= VIEW_DISTANCE) {
			player.getSession().send(new SendObjectPacket(6552, 2961, 3389, player.getZ(), -1, 10));
		}
	}

}
