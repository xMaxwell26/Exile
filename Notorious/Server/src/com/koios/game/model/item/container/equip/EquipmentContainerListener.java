package com.koios.game.model.item.container.equip;

import java.util.Objects;

import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.ContainerListener;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;

/**
 * A ContainerListener which flags for an appearance update when the player equips or removes an item.
 *
 * @author Graham Edgecombe
 */
public class EquipmentContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Creates the container listener.
	 *
	 * @param player The player.
	 */
	public EquipmentContainerListener(Player player) {
		this.player = Objects.requireNonNull(player);
	}

	@Override
	public void onSingleItemChange(Container container, int slot) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		ActionSender.sendWeight(player, player.getEquipment().getWeight() + player.getInventory().getWeight());
	}

	@Override
	public void onItemGroupChange(Container container, int[] slots) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		ActionSender.sendWeight(player, player.getEquipment().getWeight() + player.getInventory().getWeight());
	}

	@Override
	public void onFullItemChange(Container container) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		ActionSender.sendWeight(player, player.getEquipment().getWeight() + player.getInventory().getWeight());
	}

}
