package com.koios.network.in.impl;

import java.util.stream.IntStream;

import com.koios.GameService;
import com.koios.game.content.Food;
import com.koios.game.content.SkillResourcePack;
import com.koios.game.content.caskets.Caskets;
import com.koios.game.content.cluescroll.ClueScroll;
import com.koios.game.content.cluescroll.ClueScrollConstants;
import com.koios.game.content.cluescroll.CoordinateScroll;
import com.koios.game.content.cluescroll.MapScroll;
import com.koios.game.content.cluescroll.TreasureTrails;
import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.impl.admin.EventToolkitDialogue;
import com.koios.game.content.dialogue.impl.admin.RottenPotatoDialogue;
import com.koios.game.content.dialogue.impl.admin.ShabbyBookDialogue;
import com.koios.game.content.dialogue.impl.util.SimpleChoice;
import com.koios.game.content.minigames.barrows.Barrows;
import com.koios.game.content.obj.BoxSets;
import com.koios.game.content.obj.MithrilSeeds;
import com.koios.game.content.obj.MysteryBox;
import com.koios.game.content.randoms.MysteryBoxRandomEvent;
import com.koios.game.content.skill.Prayer;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.herblore.HerbCleaning;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.impl.LayTrap;
import com.koios.game.content.skill.runecrafting.Pouches;
import com.koios.game.content.skill.woodcutting.BirdNests;
import com.koios.game.content.teleport.TeleportExecutor;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.content.teleport.Teleport.TeleportPlaces;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

/**
 * Clicking an item, bury bone, eat values etc
 */
public class PacketItemOption1 implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {

		final int interfaceId = packet.readLEShortA();
		final int slot = packet.readShortA();
		final int id = packet.readLEShort();

		// cache this value as it will be cancelled by the interface close call
		Class<? extends Dialogue> activeDialogue = player.dialogue().getActiveType();

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

        if(player.isDead()||player.isTeleporting()||player.isTrading()||player.getBankField().isBanking())return;
        if(com.koios.game.arpg.MapRuns.useScroll(player,slot))return;
        if(com.koios.game.arpg.MapRuns.isGem(player.getInventory().get(slot))){com.koios.game.arpg.PrototypePanel.handle(player,"arpgproto select "+slot);com.koios.game.arpg.PrototypePanel.show(player,"craft");return;}
        if(player.getInventory().contains(i -> i.hasRoll())){player.sendMessage("Legacy item actions are disabled while carrying prototype rolled gear.");return;}
		if (player.dialogue().isActive()) {
			player.getSession().send(new SendRemoveInterfacePacket());
		}

		ClueScroll scroll = ClueScrollConstants.getClueScroll(id);

		if (scroll != null && scroll.getItem() == id) {
			scroll.open(player);
			return;
		}

		if (player.inWild()) {
			if (ItemDefinition.forId(id).getName().contains("Extreme") || ItemDefinition.forId(id).getName().contains("Overload")) {
				player.sendMessage("You cannot do this in the wilderness.");
				return;
			}
		}

		if (BirdNests.isNest(id)) {
			BirdNests.search(player, id);
		}

		if (HerbCleaning.itemIsGrimy(id)) {
			HerbCleaning.cleanHerb(player, id, slot);
			return;
		}

		Hunter.TrapType trap = Hunter.TrapType.getTrapType(id);

		if (trap != null) {
			GameService.getTaskScheduler().schedule(new LayTrap(player, slot, trap));
			return;
		}

		ItemDefinition def = ItemDefinition.forId(id);

		if (MysteryBoxRandomEvent.openMysteryBox(player, id)) {
			return;
		} else if (BoxSets.isSet(id)) {
			BoxSets.handleSet(player, id);
		} else if (Food.isItemFood(id)) {
			Food.eat(player, id, slot);
		} else if (Prayer.BoneDefinition.forId(id) != null) {
			Prayer.buryBone(player, id, slot);
		} else if (player.getPotions().isPotion(id)) {
			player.getPotions().handlePotion(id, slot);
		} else if (def.getName().toLowerCase().contains("casket")) {

			if (System.currentTimeMillis() - 1000 < player.lastAction) {
				return;
			}
			switch (id) {
			case ClueScrollConstants.CASKET_EASY:
				if (TreasureTrails.reward(player, Difficulty.EASY)) {
					player.getInventory().remove(new Item(id));
				}
				player.lastAction = System.currentTimeMillis();
				return;

			case ClueScrollConstants.CASKET_MEDIUM:
				if (TreasureTrails.reward(player, Difficulty.MEDIUM)) {
					player.getInventory().remove(new Item(id));
				}
				player.lastAction = System.currentTimeMillis();
				return;

			case ClueScrollConstants.CASKET_HARD:
				if (TreasureTrails.reward(player, Difficulty.HARD)) {
					player.getInventory().remove(new Item(id));
				}
				player.lastAction = System.currentTimeMillis();
				return;
			}

			Caskets.openCasket(player, id, def, slot);
		} else {
			switch (id) {
			case 6:
				player.getDwarfMultiCannon().setUpCannon();
				break;

			case 299:
				MithrilSeeds.plantSeed(player);
				break;

			case 6675: // Charm box
				player.getInventory().remove(new Item(id, 1));
				player.sendMessage("You open up the charm box and obtain 10 of each charm!");
				player.getInventory().add(new Item(12158, 10));
				player.getInventory().add(new Item(12159, 10));
				player.getInventory().add(new Item(12160, 10));
				player.getInventory().add(new Item(12163, 10));
				break;

			case 952:
				if (ClueScrollConstants.playerHasScroll(player)) {
					for (Item item : player.getInventory().toArray()) {

						if (item == null) {
							continue;
						}

						scroll = ClueScrollConstants.getClueScroll(item.getId());

						if (scroll == null) {
							continue;
						}

						if (scroll instanceof MapScroll || scroll instanceof CoordinateScroll) {
							if (scroll.inEndLocation(player)) {
								scroll.dig(player);
								return;
							}
						}
					}
				}
				Barrows.handleDigging(player);
				break;

			case 2205:
				int explosiveHit = 10;
				player.playAnimation(new Animation(827));
				player.setDamage(new Damage(explosiveHit, HitIcons.NONE), player);
				player.speak("Ow! That hurt!");
				break;

			case 2528: // Lamp
				player.sendMessage("You rub the mysterious lamp...");
				player.setAttribute("xp_lamp_reward", 100_000);
				player.setAttribute("xp_lamp_type", 2528);
				player.setAttribute("xp_lamp_lvlreq", 60);
				player.getSession().send(new SendConfigPacket(261, 0));
				player.getInterfaceState().openInterface(2808);
				break;

			case 13663: // Max cash ticket
				player.dialogue().start(new SimpleChoice(player, "Yes, tear this ticket apart (Good bye 2147m).", p -> {
					p.getInventory().remove(new Item(13663));
					p.sendMessage("You tear the ticket apart and throw the remains away.");
				} , "No! Why would anyone want to do that!?", p -> p.sendMessage("You choose to keep the ticket intact.")));
				break;

			case 2842: // Challenge scroll
				player.sendMessage("Sir Lucan in Edgeville has a challenge in exchange for this scroll.");
				break;

			case 4155: // Enchanted gem
				player.getSlayer().sendKillsLeft();
				break;

			case 5509: // Small pouch
				Pouches.addSmallPouch(player);
				break;

			case 5510:
			case 5511:
				Pouches.addMediumPouch(player);
				break;

			case 5512:
			case 5513:
				Pouches.addLargePouch(player);
				break;

			case 5514:
			case 5515:
				Pouches.addGiantPouch(player);
				break;

			case 5733: // Rotten potato
				if (activeDialogue == RottenPotatoDialogue.class) {
					if (player.isSuperAdministrator()) {
						Skill skill = player.getSkill();
						IntStream.range(0, 24).forEach(skill::restoreLevel);
						player.teleBlockDuration = 0;
						player.teleBlockTime = 0;
						player.alchDelay = 0;
						player.isSkulled = false;
						player.sendMessage("Your health and stats have been replenished.");
						player.dialogue().interrupt();
					} else {
						player.getInventory().remove(slot, new Item(id));
						player.sendMessage("Too late - it's gone!");
					}
				} else {
					player.dialogue().start(new RottenPotatoDialogue(player, RottenPotatoDialogue.STATE_EAT));
				}
				break;

			case 20432: // Shabby book
				player.dialogue().start(new ShabbyBookDialogue(player));
				break;

			case 6199: // Mystery box
				player.getInventory().set(slot, null);
				MysteryBox.newInstance(player).loot();
				break;

			case 7509:
				if (player.getHitpoints() > 1) {
					player.playAnimation(new Animation(829));
					player.setDamage(new Damage(1, HitIcons.NONE), player);
					player.speak("Ow! I nearly broke a tooth!");
				} else {
					player.sendMessage("You must have more than 1 hitpoint to eat the cake!");
				}
				break;

			case 7956:
				int[] reward = { 250_000, 300_000, 500_000, 750_000, 1_000_000 };
				int won = reward[NumberUtil.inclusiveRandom(reward.length - 1)];
				player.getInventory().remove(slot, new Item(7956));
				player.getInventory().add(new Item(995, won));
				player.sendMessage("You loot the casket... and receive " + NumberUtil.format(won) + " coins!");
				break;

			case ItemIdentifiers.VARROCK_TELEPORT:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.VARROCK, TeleportModes.TELEPORT_TAB);
				break;

			case ItemIdentifiers.LUMBRIDGE_TELEPORT:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.LUMBRIDGE, TeleportModes.TELEPORT_TAB);
				break;

			case ItemIdentifiers.FALADOR_TELEPORT:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.FALADOR, TeleportModes.TELEPORT_TAB);
				break;

			case ItemIdentifiers.CAMELOT_TELEPORT:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.CAMELOT, TeleportModes.TELEPORT_TAB);
				break;

			case ItemIdentifiers.ARDOUGNE_TELEPORT:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.ARDOUGNE, TeleportModes.TELEPORT_TAB);
				break;

			case ItemIdentifiers.WATCHTOWER_TPORT:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.WATCHTOWER, TeleportModes.TELEPORT_TAB);
				break;

			case ItemIdentifiers.TELEPORT_TO_HOUSE:
				player.getInventory().remove(id, 1);
				TeleportExecutor.startTeleportation(player, TeleportPlaces.HOME, TeleportModes.TELEPORT_TAB);
				break;

			case 15084: // Dice bag
				player.diceId = id;
				player.getDH().sendDialogues(106, 0);
				break;

			case 15362: // Vial pack
				SkillResourcePack.unpack(player, slot, id, ItemIdentifiers.VIAL_NOTE, 1);
				break;

			case 15363: // Vial of water pack
				SkillResourcePack.unpack(player, slot, id, ItemIdentifiers.VIAL_OF_WATER_NOTE, 1);
				break;

			case 15364: // Eye of newt pack
				SkillResourcePack.unpack(player, slot, id, ItemIdentifiers.EYE_OF_NEWT_NOTE, 1);
				break;

			case 19650:
				player.dialogue().start(new EventToolkitDialogue(player));
				break;

			default:
				player.sendMessage("Nothing interesting happens.");
			}
		}
	}

}
