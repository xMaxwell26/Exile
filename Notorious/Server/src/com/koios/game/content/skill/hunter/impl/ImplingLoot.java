package com.koios.game.content.skill.hunter.impl;

import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;
import com.koios.util.ItemIdentifiers;

@SuppressWarnings("rawtypes")
public class ImplingLoot extends EntityTask {

	private final int slot;
	private final Hunter.Impling impling;
	private final Player player;

	@SuppressWarnings("unchecked")
	public ImplingLoot(Player player, int slot, Hunter.Impling impling) {
		super(1, player);
		this.slot = slot;
		this.impling = impling;
		this.player = player;
	}

	@Override
	public void execute() {
		if (player.getInventory().get(slot).getId() != impling.getJar()) {
			stop();
			return;
		}

		if (player.getInventory().freeSlots() < 2) {
			player.sendMessage("You do not have enough free inventory slots to do this.");
			stop();
			return;
		}

		player.getInventory().remove(slot, new Item(impling.getJar()));
		player.getInventory().add(impling.getRandomReward());
		player.getInventory().add(new Item(ItemIdentifiers.IMPLING_JAR));
		player.sendMessage("You loot the impling jar for a reward.");
		stop();
	}

}