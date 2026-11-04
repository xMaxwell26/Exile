package com.koios.network.in.impl;

import java.util.Objects;

import com.koios.Constants;
import com.koios.game.content.PetManager;
import com.koios.game.content.obj.StaticDestroyInventoryItem;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.Logger;

/**
 * Drop Item
 */
public class PacketItemOption5 implements IncomingPacketDecoder {

	@Override
	public void handle(final Player player, GamePacket packet) {
		final int id = packet.readShortA();
		final int interfaceId = packet.readShort();
		final int slot = packet.readShortA();
		player.alchDelay = System.currentTimeMillis();
		player.skillCycle[Skill.HERBLORE] = false;

		if (interfaceId != Inventory.INTERFACE) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (player.isDead() || player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			return;
		}

		if (player.getCombat().inCombat()) {
			player.sendMessage("You cannot drop items during combat.");
			return;
		}

		if (Objects.isNull(player.getInventory().get(slot))) {
			return;
		}

		if (slot < 0 || slot >= player.getInventory().getCapacity() || player.getInventory().get(slot) == null || player.getInventory().get(slot).getId() != id
				|| player.getInventory().get(slot).getCount() < 1) {
			return;
		}

		if (player.dialogue().isActive()) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

        if(com.koios.game.arpg.Tutorial.active(player)){
            player.sendMessage("Keep your supplies until the guided introduction is complete. You will need them for its next steps.");
            return;
        }

		if (player.inArena()) {
			player.sendMessage("You can't drop items inside the arena!");
			return;
		}

        if(player.getInventory().get(slot).hasRoll()){
            com.koios.game.model.item.Item rolled=player.getInventory().get(slot).copy();
            if(com.koios.game.arpg.MapRuns.inside(player)||com.koios.game.arpg.MapRuns.isHome(player))GroundItemManager.spawnPrivate(player,rolled,player.getLocation());else GroundItemManager.spawnNew(player,rolled);
            player.getInventory().set(slot,null);PlayerSaver.save(player);return;
        }
		if (PetManager.spawnPet(player, id, slot, false)) {
			return;
		}

		boolean droppable = true;

		for (int i : Constants.UNDROPPABLE_ITEMS) {
			if (i == id) {
				droppable = false;
				break;
			}
		}

		if (droppable) {
			if (ItemDefinition.forId(id).isUntradeable()) {
				Logger.writeLogs(player,
						player.getName() + " destroyed '" + ItemDefinition.getName(id) + "(ID: " + id + ")', Amount: '" + player.getInventory().get(slot).getCount() + "'.",
						"droplogs");
				StaticDestroyInventoryItem.destroy(player, id, slot);
				return;
			}

			Logger.writeLogs(player,
					player.getName() + " dropped '" + ItemDefinition.getName(id) + "(ID: " + id + ")', Amount: '" + player.getInventory().get(slot).getCount() + "'.", "droplogs");

			if (!player.inBarbDef() && !player.inSW() && !player.getMinigame().inMinigame()) {
				GroundItemManager.spawnNew(player, id, player.getX(), player.getY(), player.getInventory().get(slot).getCount(), player.inWild());
			}

			player.getInventory().set(slot, null);
			PlayerSaver.save(player);
		} else {
			player.sendMessage("A magical force prevents you from dropping this item.");
		}
	}

}
