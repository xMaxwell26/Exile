package com.koios.network.in.impl;

import com.koios.game.content.SkillResourcePack;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.dialogue.impl.admin.RottenPotatoDialogue;
import com.koios.game.content.dialogue.impl.edgeville.MysteriousCapeDialogue;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.skill.runecrafting.Pouches;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemAssistant;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.item.container.equip.Equipment.EquipmentType;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.ItemIdentifiers;

/**
 * Wear Item
 */
public class PacketItemOption2 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		final int itemId = packet.readShort();
		final int slot = packet.readShortA();
		final int interfaceId = packet.readShortA();

		if (interfaceId != Inventory.INTERFACE) {
			return;
		}

		if (!player.hasFocus()) {
			return;
		}

		if (slot < 0 || slot >= player.getInventory().getCapacity() || player.getInventory().get(slot) == null || player.getInventory().get(slot).getId() != itemId
				|| player.getInventory().get(slot).getCount() < 1) {
			return;
		}

		if (player.dialogue().isActive() || player.getInterfaceState().getCurrentInterface() > 0 && player.getInterfaceState().getCurrentInterface() != 15106) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		if (player.getCombat().getTarget() != null) {
			Combat.resetCombat(player);
		}

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}

			if (!player.getCurrentSkill().canWalk()) {
				return;
			}
		}

		final int amount = player.getInventory().get(slot).getCount();

		// TODO: >>>> TEMPORARY FALLBACK WHILE WAITING FOR CLIENT UPDATE
		if (player.getAppearance().isFemale()) {
			if (itemId == ItemIdentifiers.GUTHIX_CAPE || itemId == ItemIdentifiers.ZAMORAK_CAPE || itemId == ItemIdentifiers.SARADOMIN_CAPE) {
				player.sendMessage("<col=800000>Female characters currently cannot equip " + ItemDefinition.forId(itemId).getName().toLowerCase() + "s.");
				return;
			}
		}
		// >>>> END OF TEMPORARY FALLBACK

		switch (itemId) {
		case 2422: // Blue partyhat (fake)
			player.sendMessage("Uh-oh! You accidently tore the blue partyhat apart...");
			player.sendMessage("... and followed up by furiously scattering its remaining pieces.");
			player.getInventory().remove(slot, new Item(itemId));
			break;

		case 5509:
			Pouches.removeSmallPouch(player);
			break;

		case 5510:
		case 5511:
			Pouches.removeMediumPouch(player);
			break;

		case 5512:
		case 5513:
			Pouches.removeLargePouch(player);
			break;

		case 5514:
		case 5515:
			Pouches.removeGiantPouch(player);
			break;

		case 5733: // Rotten potato
			player.dialogue().start(new RottenPotatoDialogue(player, RottenPotatoDialogue.STATE_HEAL));
			break;

		case 15362: // Vial pack
			SkillResourcePack.unpack(player, slot, itemId, ItemIdentifiers.VIAL_NOTE, amount);
			break;

		case 15363: // Vial of water pack
			SkillResourcePack.unpack(player, slot, itemId, ItemIdentifiers.VIAL_OF_WATER_NOTE, amount);
			break;

		case 15364: // Eye of newt pack
			SkillResourcePack.unpack(player, slot, itemId, ItemIdentifiers.EYE_OF_NEWT_NOTE, amount);
			break;

		default:
			if (slot < 0 || slot > Inventory.SIZE || player.getInventory().get(slot) == null) {
				return;
			}

			Item item = player.getInventory().get(slot);

			if ((item.getId() == 20767 || item.getId() == 20768) && !MysteriousCapeDialogue.canWearMaxCape(player, true)) {
				return;
			}

			if ((item.getId() == 20748 || item.getId() == 20770) && !MysteriousCapeDialogue.canWearCompletionistCape(player, true)) {
				return;
			}

			EquipmentType type = item.getDefinition().getEquipType();
			Item equip = player.getInventory().get(slot);
			Item oldEquip = null;

			if (player.getEquipment().get(type.getSlot()) != null) {
				oldEquip = player.getEquipment().get(type.getSlot());
			}

			if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().getMinigameName().equals("DUEL_ARENA")) {
				int index = DuelingRules.forEquipmentSlot(type.getSlot());

				if (index >= 0) {
					if (player.getDuel().getRules()[index]) {
						player.sendMessage("You can't wear this equipment in the duel!");
						return;
					}
				}
			}

			if(equip.hasRoll()&&!equip.getRoll().isIdentified()){player.sendMessage("Use a Scroll of Wisdom on the item before equipping it.");return;}
            if (equip.hasRoll() && player.getArpgProgress().level()<equip.getRoll().getRequiredLevel()){player.sendMessage("Your character level is too low for this item.");return;}
            if (!equip.hasRoll() && !ItemAssistant.canWear(player, itemId)) {
				return;
			}

			// Resets auto casts if player switches weapon - unless its a staff
			if (item.getDefinition().isWeapon() && !item.getDefinition().getName().contains("staff") || !item.getDefinition().getName().contains("wand")) {
				player.resetAutocast();
			}

			if (item.getDefinition().isTwoHanded()
					|| type.getSlot() == Equipment.SLOT_SHIELD && player.getEquipment().get(3) != null && player.getEquipment().get(3).getDefinition().isTwoHanded()) {
				int slotToRemove = type.getSlot() == Equipment.SLOT_WEAPON ? Equipment.SLOT_SHIELD : Equipment.SLOT_WEAPON;

				if (player.getEquipment().get(slotToRemove) != null) {
					if (player.getInventory().freeSlots() >= 1) {
						player.getInventory().add(player.getEquipment().get(slotToRemove));
						player.getEquipment().set(slotToRemove, null);
					} else {
						player.sendMessage("You do not have enough room in your inventory.");
						return;
					}
				}
			}

			if (oldEquip != null && oldEquip.getId() == itemId && equip.getDefinition().isStackable()) {
				equip.setCount(equip.getCount() + oldEquip.getCount());
				oldEquip = null;
			}

			if (player.getEquipment().get(type.getSlot()) != null) {
				if (player.getInventory().hasItem(new Item(player.getEquipment().get(type.getSlot()).getId()))) {
					if (player.getEquipment().get(type.getSlot()).getDefinition().isStackable()) {
						int newSlot = player.getInventory().getSlotById(player.getEquipment().get(type.getSlot()));
						Item newItem = new Item(player.getInventory().get(newSlot).getId(),
								player.getInventory().get(newSlot).getCount() + (oldEquip != null ? oldEquip.getCount() : 0));
						player.getInventory().set(newSlot, newItem);
						oldEquip = null;
					}
				}
			}
			
			player.getInventory().set(slot, oldEquip);
			player.getEquipment().set(type.getSlot(), equip);
		}
	}

}
