package com.koios.game.content.skill.hunter.impl;

import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;
import com.koios.util.ItemIdentifiers;

@SuppressWarnings("rawtypes")
public class ReleaseButterfly extends EntityTask {

	private final int item;
	private final int slot;
	private final Player player;

	@SuppressWarnings("unchecked")
	public ReleaseButterfly(Player player, int item, int slot) {
		super(1, player);
		this.item = item;
		this.slot = slot;
		this.player = player;
	}

	@Override
	public void execute() {
		if (player.getInventory().get(slot).getId() != item) {
			stop();
			return;
		}

		player.getInventory().remove(slot, new Item(item));
		player.getInventory().add(new Item(ItemIdentifiers.BUTTERFLY_JAR));
		player.sendMessage("You release the butterfly from its jar.");
		stop();
		return;
	}

}