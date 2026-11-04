package com.koios.game.model.item.container;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;

/**
 * A ContainerListener which updates a client-side interface to match the server-side copy of the container.
 *
 * @author Graham Edgecombe
 */
public class InterfaceContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The interface id.
	 */
	private final int interfaceId;

	/**
	 * Creates the container listener.
	 *
	 * @param player The player.
	 * @param interfaceId The interface id.
	 */
	public InterfaceContainerListener(Player player, int interfaceId) {
		this.player = player;
		this.interfaceId = interfaceId;
	}

	@Override
	public void onSingleItemChange(Container container, int slot) {
		Item item = container.get(slot);
		ActionSender.sendUpdateItem34(player, interfaceId, slot, item);
		ActionSender.sendWeight(player, player.getEquipment().getWeight() + player.getInventory().getWeight());
	}

	@Override
	public void onFullItemChange(Container container) {
		ActionSender.sendInterfaceItems(player, interfaceId, container.toArray());
		ActionSender.sendWeight(player, player.getEquipment().getWeight() + player.getInventory().getWeight());
	}

	@Override
	public void onItemGroupChange(Container container, int[] slots) {
		ActionSender.sendUpdateItems(player, interfaceId, slots, container.toArray());
		ActionSender.sendWeight(player, player.getEquipment().getWeight() + player.getInventory().getWeight());
	}

}