package com.koios.network.in.impl;

import java.util.Objects;

import com.koios.game.content.dialogue.impl.admin.RottenPotatoDialogue;
import com.koios.game.content.dialogue.impl.teleportation.GloryTeleportDialogue;
import com.koios.game.content.skill.summoning.SummoningData;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.util.ItemIdentifiers;

/**
 * Item Click 3 Or Alternative Item Option 1
 *
 * @author Ryan / Lmctruck30
 *         <p>
 *         Proper Streams
 */
public class PacketItemOption4 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
        if(player.getInventory().contains(i -> i.hasRoll())){player.sendMessage("Legacy item actions are disabled while carrying prototype rolled gear.");return;}
		final int interfaceId = packet.readLEShortA();
		final int slot = packet.readLEShort();
		final int id = packet.readShortA();

		if (interfaceId != Inventory.INTERFACE) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (Objects.isNull(player.getInventory().get(slot))) {
			return;
		}

		if (slot < 0 || slot >= player.getInventory().getCapacity() || player.getInventory().get(slot).getId() != id || player.getInventory().get(slot).getCount() < 1) {
			return;
		}

		if (player.getSummoning().handleAction(SummoningData.SUMMON_FAMILIAR, new Object[] { id })) {
			return;
		}

		if (player.getPotions().isPotionCheck(id) && System.currentTimeMillis() - player.lastEmpty >= 1500) {
			player.sendMessage("You empty the vial.");
			player.getInventory().remove(slot, new Item(id, 1));
			player.getInventory().add(new Item(229, 1));
			player.lastEmpty = System.currentTimeMillis();
		}

		switch (id) {
		case 5733: // Rotten potato
			player.dialogue().start(new RottenPotatoDialogue(player, RottenPotatoDialogue.STATE_COMMANDS));
			break;

		case 4155: // Slayer masters teleport - Enchanted gem
			player.castTeleport(
					new Teleport(new Location(3297, 9525, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 13263: // Slayer helmet dismantle
			player.getInventory().remove(new Item(id, 1));
			player.getInventory().add(new Item(ItemIdentifiers.BLACK_MASK_10, 1));
			player.getInventory().add(new Item(ItemIdentifiers.SPINY_HELMET, 1));
			player.getInventory().add(new Item(ItemIdentifiers.EARMUFFS, 1));
			player.getInventory().add(new Item(ItemIdentifiers.NOSE_PEG, 1));
			player.getInventory().add(new Item(ItemIdentifiers.FACE_MASK, 1));
			player.sendMessage("You dismantle the slayer helmet.");
			break;

		case 15492: // Full Slayer helmet dismantle
			player.getInventory().remove(new Item(id, 1));
			player.getInventory().add(new Item(ItemIdentifiers.SLAYER_HELMET, 1));
			player.getInventory().add(new Item(ItemIdentifiers.HEXCREST, 1));
			player.getInventory().add(new Item(ItemIdentifiers.FOCUS_SIGHT, 1));
			player.sendMessage("You dismantle the full slayer helmet.");
			break;

		case 8901: // Make-helmet on black mask (10)
			boolean makeHelmet = true;

			if (!player.getInventory().contains(ItemIdentifiers.SPINY_HELMET)) {
				player.sendMessage("You need a spiny helmet to do this.");
				makeHelmet = false;
			}

			if (!player.getInventory().contains(ItemIdentifiers.EARMUFFS)) {
				player.sendMessage("You need some earmuffs to do this.");
				makeHelmet = false;
			}

			if (!player.getInventory().contains(ItemIdentifiers.NOSE_PEG)) {
				player.sendMessage("You need a nose peg to do this.");
				makeHelmet = false;
			}

			if (!player.getInventory().contains(ItemIdentifiers.FACE_MASK)) {
				player.sendMessage("You need a face mask to do this.");
				makeHelmet = false;
			}

			if (makeHelmet) {
				player.getInventory().remove(new Item(ItemIdentifiers.BLACK_MASK_10));
				player.getInventory().remove(new Item(ItemIdentifiers.SPINY_HELMET));
				player.getInventory().remove(new Item(ItemIdentifiers.EARMUFFS));
				player.getInventory().remove(new Item(ItemIdentifiers.NOSE_PEG));
				player.getInventory().remove(new Item(ItemIdentifiers.FACE_MASK));
				player.getInventory().add(new Item(ItemIdentifiers.SLAYER_HELMET));
				player.sendMessage("You combine the equipment to make a slayer helmet.");
			}
			break;

		case 1704:
			player.sendMessage("It will need to be recharged before you can use it again.");
			break;

		case 1706:
		case 1708:
		case 1710:
		case 1712:
			player.dialogue().start(new GloryTeleportDialogue(player, id, slot, true));
			break;

		case 15086:
			player.getInventory().remove(new Item(15086, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15088:
			player.getInventory().remove(new Item(15088, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15090:
			player.getInventory().remove(new Item(15090, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15092:
			player.getInventory().remove(new Item(15092, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15094:
			player.getInventory().remove(new Item(15094, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15096:
			player.getInventory().remove(new Item(15096, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15098:
			player.getInventory().remove(new Item(15098, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15100:
			player.getInventory().remove(new Item(15100, 1));
			player.getInventory().add(new Item(15084, 1));
			player.sendMessage("You put away the dies from the dice bag.");
			break;

		default:
			player.dialogue().interrupt();
		}
	}

}
