package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.dialogue.impl.admin.RottenPotatoDialogue;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.impl.ReleaseButterfly;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Item Click 2 Or Alternative Item Option 1
 *
 * @author Ryan / Lmctruck30
 *         <p>
 *         Proper Streams
 */
public class PacketItemOption3 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
        if(player.getInventory().contains(i -> i.hasRoll())){player.sendMessage("Legacy item actions are disabled while carrying prototype rolled gear.");return;}
		final int id = packet.readShortA();
		final int slot = packet.readLEShortA();
		final int interfaceId = packet.readLEShortA();

		if (interfaceId != Inventory.INTERFACE) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (slot < 0 || slot >= player.getInventory().getCapacity() || player.getInventory().get(slot) == null || player.getInventory().get(slot).getId() != id
				|| player.getInventory().get(slot).getCount() < 1) {
			return;
		}

		Hunter.Butterfly butterfly = Hunter.Butterfly.getButterflyForJar(id);

		if (butterfly != null) {
			GameService.getTaskScheduler().schedule(new ReleaseButterfly(player, id, slot));
			return;
		}

		switch (id) {
		case 227:
			player.sendMessage("You empty the vial.");
			player.getInventory().remove(slot, new Item(id, 1));
			player.getInventory().add(new Item(229, 1));
			break;

		case 5733:
			player.dialogue().start(new RottenPotatoDialogue(player, RottenPotatoDialogue.STATE_OPTIONS));
			break;

		case 21371:
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(4151));
				player.getInventory().add(new Item(21369, 1));
				player.sendMessage("You split the Abyssal whip and the whip vine.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19335:
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(6585));
				player.getInventory().add(new Item(19333, 1));
				player.sendMessage("You split the amulet of fury and the ornament kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		/**
		 * Dragon orn and sp splitting
		 */

		case 19336: // Dragon full helm orn
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(11335));
				player.getInventory().add(new Item(19346, 1));
				player.sendMessage("You split the dragon full helm and the ornament kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19341: // Dragon full helm sp
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(11335));
				player.getInventory().add(new Item(19354, 1));
				player.sendMessage("You split the dragon full helm and the spiked kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19337: // Dragon platebody orn
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(14479));
				player.getInventory().add(new Item(19350, 1));
				player.sendMessage("You split the dragon platebody and the ornament kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19342: // Dragon platebody sp
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(14479));
				player.getInventory().add(new Item(19358, 1));
				player.sendMessage("You split the dragon platebody and the spiked kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19338: // Dragon platelegs orn
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(4087));
				player.getInventory().add(new Item(19348, 1));
				player.sendMessage("You split the dragon platelegs and the ornament kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19343: // Dragon platelegs sp
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(4087));
				player.getInventory().add(new Item(19356, 1));
				player.sendMessage("You split the dragon platelegs and the spiked kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19340: // Dragon sq shield orn
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(1187));
				player.getInventory().add(new Item(19352, 1));
				player.sendMessage("You split the dragon platelegs and the ornament kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		case 19345: // Dragon sq shield sp
			if (player.getInventory().freeSlots() >= 1) {
				player.getInventory().set(slot, new Item(1187));
				player.getInventory().add(new Item(19360, 1));
				player.sendMessage("You split the dragon platelegs and the spiked kit.");
			} else {
				player.sendMessage("You don't have enough inventory space to do this!");
			}
			break;

		// End of dragon orn and sp splitting

		case 11694:
			if (player.getInventory().freeSlots() > 0) {
				player.getInventory().set(slot, new Item(11690));
				player.getInventory().add(new Item(11702, 1));
				player.sendMessage("You dismantle the godsword blade from the hilt.");
			} else {
				player.sendMessage("You do not have enough inventory space to do this.");
			}

			break;

		case 11696:
			if (player.getInventory().freeSlots() > 0) {
				player.getInventory().set(slot, new Item(11690));
				player.getInventory().add(new Item(11704, 1));
				player.sendMessage("You dismantle the godsword blade from the hilt.");
			} else {
				player.sendMessage("You do not have enough inventory space to do this.");
			}

			break;

		case 11698:
			if (player.getInventory().freeSlots() > 0) {
				player.getInventory().set(slot, new Item(11690));
				player.getInventory().add(new Item(11706, 1));
				player.sendMessage("You dismantle the godsword blade from the hilt.");
			} else {
				player.sendMessage("You do not have enough inventory space to do this.");
			}

			break;

		case 11700:
			if (player.getInventory().freeSlots() > 0) {
				player.getInventory().set(slot, new Item(11690));
				player.getInventory().add(new Item(11708, 1));
				player.sendMessage("You dismantle the godsword blade from the hilt.");
			} else {
				player.sendMessage("You do not have enough inventory space to do this.");
			}

			break;

		case 11283:
		case 11284:
			player.sendMessage("Your shield has " + player.dfsCount + " charges.");
			break;

		default:
			if (player.getPrivilege() == RegularPrivilege.DEVELOPER) {
				System.out.println(player.getName() + " - Item3rdOption: " + id);
			}
		}
	}

}
