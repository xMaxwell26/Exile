package com.koios.game.content.obj;

import com.koios.GameService;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * @author Raw Envy Handles fillable items on various world objects TODO: Make sure the task is actually stopping correctly...
 */

public class Fillables {

	private static final int FILL_ANIMATION = 832;

	// TODO this should not be static?
	private static boolean fillingWater = false;

	public static void fillTheItem(Player player, int itemId, int objectId) {
		player.usingFillables = true;
		player.walkFromFilling = false;

		if (fillingWater) {
			return;
		}

		fillingWater = true;

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				if (!fillingWater) {
					stop();
					return;
				}

				if (player.walkFromFilling) {
					stop();
					return;
				}

				if (Fillables.canFill(itemId, objectId) && player.getInventory().hasItem(new Item(itemId))) {
					player.getInventory().remove(new Item(itemId, 1));
					player.getInventory().add(new Item(counterpart(itemId), 1));
					player.sendMessage(fillMessage(itemId, objectId));
					player.playAnimation(new Animation(FILL_ANIMATION));
				}
			}

			@Override
			public void onStop() {
				fillingWater = false;
			}

		});

		GameService.getTaskScheduler().schedule(new Task(2) {

			@Override
			public void execute() {
				if (!fillingWater) {
					stop();
					return;
				}

				if (player.walkFromFilling) {
					stop();
					return;
				}

				if (!player.walkFromFilling && Fillables.canFill(itemId, objectId) && player.getInventory().hasItem(new Item(itemId))) {
					fillTheItem(player, itemId, objectId);
				}
			}

		});
	}

	private static boolean canFill(int id, int oid) {
		return counterpart(id) != -1 && !getObjectName(oid).equals("Error");
	}

	private static String fillMessage(int id, int oid) {
		return "You fill the " + getItemName(id) + " from the " + getObjectName(oid) + ".";
	}

	private static int counterpart(int id) {
		switch (id) {
		case 1925: // Bucket
			return 1929;

		case 1935: // Jug
			return 1937;

		case 229: // Vial
			return 227;

		case 1923: // Bowl
			return 1921;

		case 1980: // Cup
			return 4458;

		case 5331: // Watering can
		case 5333:
		case 5334:
		case 5335:
		case 5336:
		case 5337:
		case 5338:
		case 5339:
			return 5340;

		case 1831: // Waterskin
		case 1825:
		case 1827:
		case 1829:
			return 1823;

		case 6667:
			return 6668;
		}

		return -1;
	}

	private static String getItemName(int id) {
		switch (id) {
		case 1925:
			return "bucket";

		case 1935:
			return "jug";

		case 229:
			return "vial";

		case 1923:
			return "bowl";

		case 1980:
			return "cup";

		case 5331:
		case 5333:
		case 5334:
		case 5335:
		case 5336:
		case 5337:
		case 5338:
		case 5339:
			return "watering can";

		case 1831:
		case 1825:
		case 1827:
		case 1829:
			return "waterskin";

		case 6667:
			return "fishbowl";
		}

		return "There was a problem with your current action, please report this.";
	}

	private static String getObjectName(int id) {
		switch (id) {
		case 873:
		case 874:
			return "sink";

		case 11661:
			return "waterpump";

		case 879:
		case 11759:
			return "fountain";
		}

		return "Error";
	}

}
