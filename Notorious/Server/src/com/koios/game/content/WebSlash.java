package com.koios.game.content;

import com.koios.game.map.MapRegion;
import com.koios.game.map.ObjectDefinition;
import com.koios.game.map.RSObject;
import com.koios.game.model.Location;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class WebSlash {

	public static boolean canSlash(Player player, int objectId, Location location) {
		ObjectDefinition def = ObjectDefinition.getById(objectId);

		if (def == null || def.name == null || !def.name.toLowerCase().contains("web")) {
			return false;
		}

		RSObject obj = MapRegion.getObject(location.getX(), location.getY(), location.getZ());

		if (obj == null || obj.getId() != objectId) {
			return false;
		}

		boolean success = NumberUtil.random(5) < 5;

		player.setFacePosition(location);

		if (!success) {
			player.sendMessage("You fail to slash the web apart.");
			return true;
		}

		player.sendMessage("You slash the web apart.");

		// Server.objectHandler.addObject(new WorldObject(734, obj_loc.getX(), obj_loc.getY(),
		// obj_loc.getZ(),
		// obj.getFace(), 10, 0));

		new GameObject(734, location.getX(), location.getY(), location.getZ(), obj.getFace(), 0, 733, 32, true);
		// MapLoading.removeObject(733, obj_loc.getX(), obj_loc.getY(), obj_loc.getZ(), 10,
		// obj.getFace());
		// MapRegion.getRegion(obj_loc).addObject(new RSObject(obj_loc.getX(), obj_loc.getY(),
		// obj_loc.getZ(), 734, 10,
		// obj.getFace()));
		/*
		 * Server.getTaskScheduler().schedule(new Task(32) {
		 * 
		 * @Override protected void execute() { MapLoading.addObject(false, obj.getId(), obj_loc.getX(), obj_loc.getY(), obj_loc.getZ(), 10, obj.getFace()); stop(); }
		 * 
		 * });
		 */
		/*
		 * Server.objectHandler.addObject(new WorldObject(object_id + 1, obj_loc.getX(), obj_loc.getY(), obj_loc.getZ(), obj.getFace(), obj.getType(), 0));
		 * 
		 * MapRegion.getRegion(obj_loc).replaceObject(object_id + 1, obj_loc.getX(), obj_loc.getY(), obj_loc.getZ());
		 */

		return true;
	}

}
