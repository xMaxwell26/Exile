package com.koios.network.in.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.randoms.RandomEvent;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.util.ItemIdentifiers;

/**
 * Magic on items
 */
public class PacketSpellWithItem implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		int slot = packet.readShort();
		int itemId = packet.readShortA();
		int interfaceId = packet.readShort();
		int spellId = packet.readShortA();

		if (!GameService.isOnlineMode()) {
			System.out.println("Slot: " + slot + ", item id: " + itemId + ", spell id: " + spellId);
		}

		if (interfaceId != Inventory.INTERFACE) {
			// Note: currently we only allow spells to be cast from the inventory.
			return;
		}

		if (slot < 0 || slot >= player.getInventory().getCapacity() || player.getInventory().get(slot) == null || player.getInventory().get(slot).getId() != itemId
				|| player.getInventory().get(slot).getCount() < 1) {
			return;
		}

		// Attempt to send a random event.
		RandomEvent.tryRandom(player);

		switch (spellId) {
		case 1162: // low alch
			if (System.currentTimeMillis() - player.alchDelay > 1000) {
				if (player.getSpellbook() != Spellbook.REGULAR) {
					// Low level alchemy can only be cast with regular magics.
					return;
				}

				if (player.getSkill().getLevel(Skill.MAGIC) < 21) {
					player.sendMessage("You need a Magic level of 21 to cast low level alchemy.");
					return;
				}

				if (itemId == ItemIdentifiers.COINS) {
					player.sendMessage("You can't alch coins");
					break;
				}

				if (!player.getInventory().hasItem(new Item(561, 1)) || !wearingStaff(player, 554) && !player.getInventory().hasItem(new Item(554, 3))) {
					player.sendMessage("You don't have the required runes to cast this spell.");
					return;
				}

				player.getInventory().remove(new Item(561, 1));

				if (!wearingStaff(player, 554)) {
					player.getInventory().remove(new Item(554, 3));
				}

				player.getInventory().remove(slot, new Item(itemId));
				int value = ItemDefinition.forId(itemId).getLowAlchemy();
				player.getInventory().add(new Item(ItemIdentifiers.COINS, value <= 0 ? 1 : value));

				if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)
						&& player.getEquipment().get(Equipment.SLOT_WEAPON).getDefinition().getName().toLowerCase().contains("staff")) {
					player.playAnimation(new Animation(9633));
					player.playGraphics(new Graphic(112));
				} else {
					player.playAnimation(new Animation(712));
					player.playGraphics(new Graphic(112));
				}

				player.alchDelay = System.currentTimeMillis();
				player.getSession().send(new SendOpenSideTabPacket(7));
				player.getSkill().addExperience(Skill.MAGIC, MagicData.MAGIC_SPELLS[49][7] * Constants.MAGIC_EXP_RATE / 4);
			}
			break;

		case 1178: // high alch
			if (System.currentTimeMillis() - player.alchDelay > 2000) {
				if (player.getSpellbook() != Spellbook.REGULAR) {
					// High level alchemy can only be cast with regular
					// magics.
					return;
				}

				if (player.getSkill().getLevel(Skill.MAGIC) < 55) {
					player.sendMessage("You need a Magic level of 55 to cast high level alchemy.");
					return;
				}

				if (itemId == ItemIdentifiers.COINS) {
					player.sendMessage("You can't alch coins");
					break;
				}

				if (!player.getInventory().hasItem(new Item(561, 1)) || !wearingStaff(player, 554) && !player.getInventory().hasItem(new Item(554, 5))) {
					player.sendMessage("You don't have the required runes to cast this spell.");
					return;
				}

				player.getInventory().remove(new Item(561, 1));
				if (!wearingStaff(player, 554)) {
					player.getInventory().remove(new Item(554, 5));
				}

				player.getInventory().remove(slot, new Item(itemId));
				int value = ItemDefinition.forId(itemId).getHighAlchemy();
				player.getInventory().add(new Item(ItemIdentifiers.COINS, value <= 0 ? 1 : value));

				if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)
						&& player.getEquipment().get(Equipment.SLOT_WEAPON).getDefinition().getName().toLowerCase().contains("staff")) {
					player.playAnimation(new Animation(9633));
					player.playGraphics(new Graphic(113));
				} else {
					player.playAnimation(new Animation(713));
					player.playGraphics(new Graphic(113));
				}

				player.alchDelay = System.currentTimeMillis();
				player.getSession().send(new SendOpenSideTabPacket(7));
				player.getSkill().addExperience(Skill.MAGIC, MagicData.MAGIC_SPELLS[50][7] * Constants.MAGIC_EXP_RATE / 3);
				AchievementManager.performed(player, Achievement.ALCH);
			}
			break;

		case 1516:
		case 18023:
		case 18018:
			// Familiar special on inventory item

			if (player.getSummoning().getFamiliar() == null) {
				player.sendMessage("You do not have a familiar.");
				return;
			}

			player.getSummoning().handleAction(SummoningData.CAST_SCROLL, new Object[] { slot, itemId });

			break;

		default:
			player.sendMessage("This spell cannot be cast at an item!");
		}
	}

	public static boolean wearingStaff(Player player, int rune) {
		switch (rune) {
		case 554:
			return player.getEquipment().contains(1387);

		case 555:
			return player.getEquipment().contains(1383);

		case 556:
			return player.getEquipment().contains(1381);

		case 557:
			return player.getEquipment().contains(1385);

		default:
			return false;
		}
	}

}
