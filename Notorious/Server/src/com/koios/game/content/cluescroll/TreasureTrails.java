package com.koios.game.content.cluescroll;

import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.Container.Type;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.NumberUtil;

/**
 * @author Michael | Chex
 */
public class TreasureTrails {

	/**
	 * Gives a reward for a specific scroll.
	 *
	 * @param player - The player receiving the reward.
	 * @param difficulty - The rewards table.
	 */
	public static boolean reward(Player player, Difficulty difficulty) {
		if (player.getInventory().freeSlots() < 6) {
			player.sendMessage("You need at least 6 free slots to open this casket.");
			return false;
		}

		player.getSession().send(new SendRemoveInterfacePacket());
		player.getInterfaceState().openInterface(6960);

		Container items = new Container(Type.STANDARD, 9);
		int length = 3 + NumberUtil.random(4);

		for (int i = 0; i < length; i++) {
			Item reward;

			do {
				reward = difficulty.getRewards().getReward();
			} while (items.contains(reward));

			items.add(reward);
			player.getInventory().add(reward);
		}

		ActionSender.sendInterfaceItems(player, 6963, items.toArray());
		return true;
	}

	/**
	 * Display an item in the chat interface.
	 *
	 * @param player - The player to send the chat interface to.
	 * @param item - The item to show.
	 * @param message - The message in the chat box.
	 */
	public static void showChatItem(Player player, int item, String message) {
		ActionSender.sendInterfaceModel(player, 11860, 150, item);
		player.getSession().send(new SendStringPacket(message, 11861));
		player.getSession().send(new SendChatInterfacePacket(11859));
	}

}
