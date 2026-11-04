package com.koios.game.content.obj;

import com.koios.GameService;
import com.koios.game.content.dialogue.impl.misc.MithrilSeedTakeFlowersDialogue;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendObjectPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Plants a mithril seed and allows the player to pick it
 *
 * @author Arithium
 */
public final class MithrilSeeds {

	/**
	 * An array containing all of the flowers
	 */
	private static final int[] FLOWER_ITEM_ID = { 2460, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476 };

	private static final int[] FLOWER_SCENERY_ID = { 2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988 };

	/**
	 * Plants a mithril seed and creates a flower
	 */
	public static void plantSeed(Player player) {
		if (player.isPlantingMithrilSeed) {
			return;
		}

		if (!player.getInventory().contains(299)) {
			return;
		}

		int select = NumberUtil.random(FLOWER_SCENERY_ID.length);
		player.isPlantingMithrilSeed = true;
		player.getInventory().remove(new Item(299, 1));
		player.playAnimation(new Animation(827));
		int x = player.getX();
		int y = player.getY();

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				if (!player.isDisconnected()) {
					String name = ItemDefinition.forId(FLOWER_ITEM_ID[select]).getName().toLowerCase();
					player.sendMessage(String.format("You plant some %s.", name));
					player.getSession().send(new SendObjectPacket(FLOWER_SCENERY_ID[select], x, y, player.getZ(), 0, 10));
				}

				stop();
			}

		});

		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				if (!player.isDisconnected()) {
					player.dialogue().start(new MithrilSeedTakeFlowersDialogue(player, FLOWER_ITEM_ID[select], x, y));
				}

				stop();
			}

			@Override
			public void onStop() {
				player.isPlantingMithrilSeed = false;
			}

		});

		GameService.getTaskScheduler().schedule(new Task(50) {

			@Override
			public void execute() {
				if (!player.isDisconnected()) {
					player.getSession().send(new SendObjectPacket(-1, x, y, player.getZ(), 0, 10));

					if (player.dialogue().isActive()) {
						player.getSession().send(new SendRemoveInterfacePacket());
					}
				}

				stop();
			}

		});
	}

}
