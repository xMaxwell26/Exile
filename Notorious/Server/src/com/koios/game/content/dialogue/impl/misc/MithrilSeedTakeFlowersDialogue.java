package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendObjectPacket;

public class MithrilSeedTakeFlowersDialogue extends Dialogue {

	private final int id;
	private final int x;
	private final int y;

	public MithrilSeedTakeFlowersDialogue(Player player, int id, int x, int y) {
		super(player);
		this.id = id;
		this.x = x;
		this.y = y;
	}

	@Override
	public void select(int index) {
		if (index == 1) {
			if (player.getInventory().freeSlots() > 0) {
				player.getInventory().add(new Item(id));
				player.getSession().send(new SendObjectPacket(-1, x, y, player.getZ(), 0, 10));
			} else {
				player.sendMessage("Your inventory is too full to hold any more flowers!");
			}
		}

		stop();
	}

	@Override
	protected void start() {
		String name = ItemDefinition.forId(id).getName().toLowerCase();
		choice("Select an Option", String.format("Pick the %s.", name), "Leave the flowers.");
	}

}
