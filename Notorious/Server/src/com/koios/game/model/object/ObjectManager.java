package com.koios.game.model.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.map.MapLoading;
import com.koios.game.map.MapRegion;
import com.koios.game.map.RSObject;
import com.koios.game.model.Location;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendObjectPacket;

/**
 * @author Sanity
 */
public class ObjectManager {

	public static void init() {

		CustomObjects.load();
	}

	private static final int VIEW_DISTANCE = 64;
	private List<GameObject> objects = new CopyOnWriteArrayList<>();
	private final List<GameObject> toRemove = new CopyOnWriteArrayList<>();

	public static final List<SceneryObject> customs = new ArrayList<>();

	public ObjectManager() {
		objects = new CopyOnWriteArrayList<>();
	}

	public void addObject(GameObject object) {
		MapLoading.removeObject(object.id, object.getX(), object.getY(), object.getHeight(), object.getType(), object.getFace());

		if (getObject(object.x, object.y, object.height) == null) {
			objects.add(object);
			placeObject(object);
		} else {
			objects.remove(getObject(object.x, object.y, object.height));
			objects.add(object);
			placeObject(object);
		}

		if (object.id != -1) {
			MapLoading.addObject(false, object.id, object.getX(), object.getY(), object.getHeight(), object.getType(), object.getFace());
		}
	}

	public boolean doesObjectExist(int x, int y) {
		for (GameObject object : objects) {
			if (object.getX() == x && object.getY() == y) {
				return true;
			}
		}

		return false;
	}

	private GameObject getObject(int x, int y, int z) {
		for (GameObject object : objects) {
			if (object.x == x && object.y == y && object.height == z) {
				return object;
			}
		}

		return null;
	}

	private boolean loadForPlayer(GameObject object, Player player) {
		if (object == null || player == null) {
			return false;
		}

		return player.distanceToPoint(object.getX(), object.getY()) <= VIEW_DISTANCE && player.getZ() == object.getHeight();
	}

	public void loadObjects(Player player) {
		Objects.requireNonNull(player);
		for (GameObject object : objects) {
			if (loadForPlayer(object, player)) {
				player.getSession().send(new SendObjectPacket(object.getId(), object.getX(), object.getY(), object.getHeight(), object.getFace(), object.getType()));
			}
		}

		loadObjectSpawns(player);
	}

	private void loadObjectSpawns(Player player) {
		for (SceneryObject sceneryObject : customs) {

			if (sceneryObject.getPosition().getZ() != player.getZ()) {
				continue;
			}

			int id = sceneryObject.getId();
			Location position = sceneryObject.getPosition();
			int direction = sceneryObject.getDirection();

			player.getSession().send(new SendObjectPacket(id, position.getX(), position.getY(), position.getZ(), direction, 10));
		}
	}

	private void placeObject(GameObject object) {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player.distanceToPoint(object.getX(), object.getY()) <= VIEW_DISTANCE) {
				player.getSession().send(new SendObjectPacket(object.getId(), object.getX(), object.getY(), object.getHeight(), object.getFace(), object.getType()));
			}
		}
	}

	public void removeAt(int x, int y) {
		GameService.getWorld().execute(p -> p.getSession().send(new SendObjectPacket(-1, x, y, 0, 0, 10)));
	}

	public void showRealObject(RSObject object) {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player.distanceToPoint(object.getX(), object.getY()) <= VIEW_DISTANCE) {
				player.getSession().send(new SendObjectPacket(object.getId(), object.getX(), object.getY(), object.getZ(), object.getFace(), object.getType()));
			}
		}
	}

	public void sendObjectAnimation(int x, int y, int animationId, int objectId, int orientation) {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player.distanceToPoint(x, y) <= 25) {
				ActionSender.createPlayersObjectAnim(player, x, y, animationId, objectId, orientation);
			}
		}
	}

	public void update() {
		for (GameObject object : objects) {
			if (object.ticks > 0) {
				object.ticks--;
			}

			else {
				updateObject(object);
				toRemove.add(object);
			}
		}

		for (GameObject object : toRemove) {
			removeObject(object);
		}

		toRemove.clear();
	}

	private void removeObject(GameObject object) {
		if (MapRegion.getObject(object.x, object.y, object.height) != null) {
			MapLoading.removeObject(object.id, object.getX(), object.getY(), object.getHeight(), object.getType(), object.getFace());
		}
		objects.remove(object);
	}

	private void updateObject(GameObject object) {
		for (Player player : GameService.getWorld().getPlayers()) {

			if (MapRegion.getObject(object.x, object.y, object.height) != null) {
				MapLoading.removeObject(object.id, object.getX(), object.getY(), object.getHeight(), object.getType(), object.getFace());
			}

			if (object.newId != -1) {
				MapLoading.addObject(false, object.newId, object.getX(), object.getY(), object.getHeight(), object.getType(), object.getFace());
			}
			player.getSession().send(new SendObjectPacket(object.newId, object.x, object.y, object.getHeight(), object.face, object.type));
		}
	}

}
