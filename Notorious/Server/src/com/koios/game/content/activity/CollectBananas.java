package com.koios.game.content.activity;

import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class CollectBananas {

	public static boolean collect(Player player, int objectId, Location objLoc) {
		if (objectId > 2078 || objectId < 2073 || System.currentTimeMillis() - player.objectClick < 1500) {
			return false;
		}

		if (objectId == 2078) {
			return false;
		}

		if (MobManager.hasSummoned(player, 1459)) {
			player.sendMessage("You can't do that right now!");
			return true;
		}

		if (player.getInventory().freeSlots() < 1) {
			player.sendMessage("You don't have enough inventory to collect this!");
			return false;
		}

		player.getInventory().add(new Item(1963));
		player.sendMessage("You collect a banana from the tree.");
		player.objectClick = System.currentTimeMillis();

		new GameObject(objectId + 1, objLoc.getX(), objLoc.getY(), objLoc.getZ(), 1, 10, 2073, 10, true);

		if ((NumberUtil.inclusiveRandom(10) == 1 || player.isSuperAdministrator())) {
			player.sendMessage("Uh oh! The monkeys are mad, run!");
			MobManager.spawnMob(player, 1459, player.getLocation(), 120, 25, 130, 130, true, true, "Leave my bananas alone!");
		}

		return true;
	}

}
