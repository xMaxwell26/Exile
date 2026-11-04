package com.koios.game.content.dialogue.impl.teleportation;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;

public class GloryTeleportDialogue extends Dialogue {

	private final int id;
	private final int slot;
	private final boolean inv;

	public GloryTeleportDialogue(Player player, int id, int slot, boolean inv) {
		super(player);
		this.id = id;
		this.slot = slot;
		this.inv = inv;
	}

	@Override
	protected void next() {
	}

	@Override
	protected void select(final int index) {
		Location destination;

		if (index == 1) {
			// Edgeville
			destination = Location.create(3088, 3500);
		} else if (index == 2) {
			// Karamja
			destination = Location.create(2911, 3152);
		} else if (index == 3) {
			// draynor village
			destination = Location.create(3103, 3249);
		} else if (index == 4) {
			// Al kharid
			destination = Location.create(3293, 3174);
		} else {
			// Unknown
			stop();
			return;
		}

		if (!player.castTeleport(new Teleport(new Location(destination.getX(), destination.getY(), 0), TeleportModes.JEWELRY))) {
			// Teleport failed - do not consume a charge.
			stop();
			return;
		}

		if (inv && slot >= 0 && player.getInventory().get(slot) != null && player.getInventory().get(slot).getId() == id) {
			player.getInventory().set(slot, new Item(player.getInventory().get(slot).getId() - 2));
		} else if (!inv && player.getEquipment().get(Equipment.SLOT_AMULET) != null && player.getEquipment().get(Equipment.SLOT_AMULET).getId() == id) {
			player.getEquipment().set(Equipment.SLOT_AMULET, new Item(player.getEquipment().get(Equipment.SLOT_AMULET).getId() - 2));
		}

		if (id == 1706) {
			player.sendMessage("You use your amulet's last charge.");
		} else if (id == 1708) {
			player.sendMessage("Your amulet has one charge left.");
		} else if (id == 1710) {
			player.sendMessage("Your amulet has two charges left.");
		} else if (id == 1710) {
			player.sendMessage("Your amulet has three charges left.");
		}

		stop();
	}

	@Override
	protected void start() {
		choice("Select an Option", "Edgeville", "Karamja", "Draynor Village", "Al Kharid", "Nowhere");
		player.sendMessage("You rub the amulet...");
	}

}
