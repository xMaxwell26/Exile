package com.koios.network.in.impl;

import com.koios.game.content.dialogue.impl.donators.ZamboCakeDialogue;
import com.koios.game.content.randoms.RandomEvent;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemInteraction;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.mob.impl.event.hideandseek.HNSEventActionHandler;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.ItemIdentifiers;

public class PacketItemWithMob implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int id = packet.readShortA();
		final int index = packet.readShortA();
		final int slot = packet.readLEShort();
		final int interfaceId = packet.readShortA();

		if (interfaceId != Inventory.INTERFACE) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (slot < 0 || slot >= player.getInventory().getCapacity() || player.getInventory().get(slot).getId() != id || player.getInventory().get(slot).getCount() < 1) {
			return;
		}

		if (index < 0 || index >= MobManager.mobs.length) {
			return;
		}

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}
		}

		if (player.dialogue().isActive()) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		Mob mob = MobManager.mobs[index];

		if (mob == null) {
			return;
		}

		int mobId = mob.getId();
		RandomEvent.tryRandom(player);

		if (id == ItemIdentifiers.ROTTEN_POTATO) {
			if (!player.isSuperAdministrator()) {
				// Regular users should not be able to utilize the rotten potato.
				player.sendMessage("Your rotten potato has vanished!");
				player.getInventory().remove(new Item(ItemIdentifiers.ROTTEN_POTATO, 28));
			} else {
				String name = mob.getName();
				int x = mob.getX();
				int y = mob.getY();

				player.sendMessage(String.format("Removed mob '%s' [id: %d, x: %d, y: %d]", name, mobId, x, y));
				mob.setDead(true);
				mob.getLocation().setAs(new Location(0, 0));
			}
		} else if (mobId == 568 && mob.getX() == 3089 && mob.getY() == 3509) {
			if (id == ItemIdentifiers.CAKE) {
				// Use regular cake with Zambo in Edgeville.
				player.dialogue().start(new ZamboCakeDialogue(player));
			} else if (id == ItemIdentifiers.ABYSSAL_WHIP) {
				// Use normal abyssal whip with Zambo in Edgeville.
				// Credits to Mod Ash for the hidden message during Carnillean's Rising.
				player.sendMessage("Exile isn't that kind of role-playing game.");

				// 25% chance Zambo will call for assistance from the Guards.
				if (mob.getRandom().nextInt(4) == 0) {
					mob.speak("Guards! Guards!");
				}
			} else {
				player.sendMessage("Zambo does not seem to be interested in your " + ItemDefinition.forId(id).getName().toLowerCase() + ".");
			}
		} else if (mobId == 8104) {
			HNSEventActionHandler.addCoffers(player, mob, slot);
		} else {
			ItemInteraction.onItemWithMob(player, id, mobId, slot);
		}
	}

}
