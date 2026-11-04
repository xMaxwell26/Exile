package com.koios.network.in.impl;

import java.util.Random;

import com.koios.GameService;
import com.koios.game.content.dialogue.impl.admin.RottenPotatoDialogue;
import com.koios.game.content.randoms.RandomEvent;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.util.ItemIdentifiers;

/**
 * @author JaydenD12/Jaydennn
 * @author Erik Eide
 */
public class PacketItemWithPlayer implements IncomingPacketDecoder {

	private static void pullChristmasCracker(Player player, Player target, int slot) {
		assert player.getInventory().get(slot).getId() == ItemIdentifiers.CHRISTMAS_CRACKER;

		if (target.getInventory().freeSlots() < 1) {
			player.sendMessage("The other person doesn't have enough inventory space to do this.");
			return;
		}

		final int chance = random.nextInt(1000);
		final int id = CRACKER_ITEMS[random.nextInt(CRACKER_ITEMS.length)];

		player.sendMessage("You pull a Christmas cracker...");
		target.sendMessage("You pull a Christmas cracker...");

		if (chance >= 500) {
			player.getInventory().remove(slot, new Item(ItemIdentifiers.CHRISTMAS_CRACKER));
			player.getInventory().add(new Item(id));
			player.sendMessage("Hey! I got the cracker!");
			target.sendMessage("The person you pull the cracker with gets the prize.");
		} else {
			player.getInventory().remove(slot, new Item(ItemIdentifiers.CHRISTMAS_CRACKER));
			target.getInventory().add(new Item(id));
			player.sendMessage("The person you pulled the cracker with gets the prize.");
			target.sendMessage("You get the prize from the cracker.");
		}
	}

	private static final int[] CRACKER_ITEMS = { 1038, 1040, 1042, 1044, 1046, 1048 };
	private static final Random random = new Random();

	@Override
	public void handle(final Player player, GamePacket packet) {
		final int interfaceId = packet.readShortA();
		final int playerIndex = packet.readShort();
		final int id = packet.readShort();
		final int slot = packet.readLEShort();

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

		if (playerIndex < 0 || playerIndex >= GameService.getWorld().getPlayers().length) {
			return;
		}

		Player target = GameService.getWorld().getPlayer(playerIndex);

		// Make sure its a player online
		if (target == null || target == player) {
			return;
		}

		RandomEvent.tryRandom(player);

		switch (id) {
		case 4155: // Slayer gem
			player.getSlayer().sendRequest(target);
			break;

		case 962: // Christmas cracker
			pullChristmasCracker(player, target, slot);
			break;

		case 5733: // Rotten potato
			player.dialogue().start(new RottenPotatoDialogue(player, 5, target));
			break;

		default:
			player.sendMessage("Nothing interesting happens.");
		}
	}

}
