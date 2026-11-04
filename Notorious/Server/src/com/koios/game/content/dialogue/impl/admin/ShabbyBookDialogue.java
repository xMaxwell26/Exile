package com.koios.game.content.dialogue.impl.admin;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.QuestInterface;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;
import com.koios.util.TimeUtil;

/**
 * Debug item - most of which are duplicates of the rotten potato. This item does not provide at-other tools.
 *
 * @author Erik Eide
 */
public final class ShabbyBookDialogue extends Dialogue {

	public ShabbyBookDialogue(Player player) {
		super(player);
	}

	@Override
	public void input(int value) {
		if (isPhase(0)) {
			// Set all stats
			if (value >= 1 && value <= 255) {
				int level = value > 99 ? 99 : value;
				int experience = player.getSkill().getXPForLevel(value);

				if (experience > 200_000_000) {
					experience = 200_000_000;
				} else if (experience < 0) {
					experience = 0;
				}

				for (int i = 0; i < 24; i++) {
					if (level < 10 && i == Skill.HITPOINTS) {
						player.getSkill().setSkill(i, 10, 1154);
					} else {
						player.getSkill().setSkill(i, level, experience);
					}
				}

				stop();
			} else {
				sendStatement("Requested skill level out of bounds (1-255)");
			}
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {
				// Set all stats
				takeValueInput();
			} else if (index == 2) {
				// Wipe inventory
				boolean hasPotato = player.getInventory().contains(ItemIdentifiers.ROTTEN_POTATO);
				player.getInventory().clear();

				if (hasPotato) {
					player.getInventory().add(new Item(ItemIdentifiers.ROTTEN_POTATO));
				}

				player.getInventory().add(new Item(ItemIdentifiers.SHABBY_BOOK));
				stop();
			} else if (index == 3) {
				// Access bank account
				stop();
				player.getBankField().setBanking(true);
				Bank.open(player);
			} else if (index == 4) {
				// Equipment Presets
				setPhase(1);
				choice("Choose Preset", "Barrows Equipment", "DPS Equipment");
			} else if (index == 5) {
				// More Options
				setPhase(5);
				choice("Cheat Sheet 2", "Players Online", "Inventory o' mysteries", "Slayer Log");
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Barrows Equipment
				setPhase(2);
				choice("Select an Option", "Ahrim's", "Dharok's", "Guthan's", "Karil's", "Next Page");
			} else if (index == 2) {
				// DPS Equipment
				setPhase(4);
				choice("Select an Option", "Bandos", "Torva", "Armadyl", "Pernix");
			}
		} else if (isPhase(2)) {
			if (index == 5) {
				// Next Page
				setPhase(3);
				choice("Select an Option", "Torag's", "Verac's", "Previous Page");
				return; // avoid call to stop
			}

			player.getInventory().clear();
			player.getEquipment().clear();
			player.getInventory().add(new Item(ItemIdentifiers.SHABBY_BOOK));

			if (index == 1) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.AHRIMS_HOOD));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.AHRIMS_ROBETOP));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.AHRIMS_ROBESKIRT));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.AHRIMS_STAFF));
				player.sendMessage("Equipped full Ahrim's.");
			} else if (index == 2) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.DHAROKS_HELM));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.DHAROKS_PLATEBODY));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.DHAROKS_PLATELEGS));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.DHAROKS_GREATAXE));
				player.sendMessage("Equipped full Dharok's.");
			} else if (index == 3) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.GUTHANS_HELM));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.GUTHANS_PLATEBODY));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.GUTHANS_CHAINSKIRT));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.GUTHANS_WARSPEAR));
				player.sendMessage("Equipped full Guthan's.");
			} else if (index == 4) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.KARILS_COIF));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.KARILS_LEATHERTOP));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.KARILS_LEATHERSKIRT));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.KARILS_CROSSBOW));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(ItemIdentifiers.BOLT_RACK, 10000));
				player.sendMessage("Equipped full Karil's.");
			}

			// append health effect
			player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints());
			stop();
		} else if (isPhase(3)) {
			if (index == 3) {
				// Previous Page
				setPhase(2);
				choice("Select an Option", "Ahrim's", "Dharok's", "Guthan's", "Karil's", "Next Page");
				return; // avoid call to stop
			}

			player.getEquipment().clear();
			player.getInventory().clear();
			player.getInventory().add(new Item(ItemIdentifiers.SHABBY_BOOK));

			if (index == 1) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.TORAGS_HELM));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.TORAGS_PLATEBODY));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.TORAGS_PLATELEGS));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.TORAGS_HAMMERS));
				player.sendMessage("Equipped full Torag's.");
			} else if (index == 2) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.VERACS_HELM));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.VERACS_BRASSARD));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.VERACS_PLATESKIRT));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.VERACS_FLAIL));
				player.sendMessage("Equipped full Verac's.");
			}

			stop();
		} else if (isPhase(4)) {
			player.getEquipment().clear();
			player.getInventory().clear();
			player.getInventory().add(new Item(ItemIdentifiers.SHABBY_BOOK));

			if (index == 1) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.HELM_OF_NEITIZNOT));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.BANDOS_CHESTPLATE));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.BANDOS_TASSETS));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.CHAOTIC_RAPIER));
				player.getEquipment().set(Equipment.SLOT_BOOTS, new Item(ItemIdentifiers.STEADFAST_BOOTS));
				player.getEquipment().set(Equipment.SLOT_GLOVES, new Item(ItemIdentifiers.BARROWS_GLOVES));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(ItemIdentifiers.COMPLETIONIST_CAPE));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(ItemIdentifiers.AMULET_OF_FURY));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(ItemIdentifiers.BERSERKER_RING_I));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(ItemIdentifiers.DRAGON_DEFENDER));

				player.getInventory().add(new Item(ItemIdentifiers.OVERLOAD_4));
				player.getInventory().add(new Item(ItemIdentifiers.SUPER_RESTORE_4, 2));
				player.getInventory().add(new Item(ItemIdentifiers.SARADOMIN_BREW_4, 4));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 19));
				player.getInventory().add(new Item(ItemIdentifiers.DRAGON_CLAWS));
				player.sendMessage("Equipped melee DPS set - Bandos.");
			} else if (index == 2) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.TORVA_FULL_HELM));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.TORVA_PLATEBODY));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.TORVA_PLATELEGS));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.CHAOTIC_RAPIER));
				player.getEquipment().set(Equipment.SLOT_BOOTS, new Item(ItemIdentifiers.STEADFAST_BOOTS));
				player.getEquipment().set(Equipment.SLOT_GLOVES, new Item(ItemIdentifiers.BARROWS_GLOVES));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(ItemIdentifiers.COMPLETIONIST_CAPE));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(ItemIdentifiers.AMULET_OF_FURY));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(ItemIdentifiers.BERSERKER_RING_I));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(ItemIdentifiers.DRAGON_DEFENDER));

				player.getInventory().add(new Item(ItemIdentifiers.OVERLOAD_4));
				player.getInventory().add(new Item(ItemIdentifiers.SUPER_RESTORE_4, 2));
				player.getInventory().add(new Item(ItemIdentifiers.SARADOMIN_BREW_4, 4));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 19));
				player.getInventory().add(new Item(ItemIdentifiers.DRAGON_CLAWS));
				player.sendMessage("Equipped melee DPS set - Torva.");
			} else if (index == 3) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.ARMADYL_HELMET));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.ARMADYL_CHESTPLATE));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.ARMADYL_CHAINSKIRT));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.CHAOTIC_CROSSBOW));
				player.getEquipment().set(Equipment.SLOT_BOOTS, new Item(ItemIdentifiers.GLAIVEN_BOOTS));
				player.getEquipment().set(Equipment.SLOT_GLOVES, new Item(ItemIdentifiers.BARROWS_GLOVES));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(ItemIdentifiers.AVAS_ACCUMULATOR));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(ItemIdentifiers.AMULET_OF_FURY));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(ItemIdentifiers.ARCHERS_RING_I));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(ItemIdentifiers.DRAGON_BOLTS_E, 100000));

				player.getInventory().add(new Item(ItemIdentifiers.OVERLOAD_4));
				player.getInventory().add(new Item(ItemIdentifiers.SUPER_RESTORE_4, 2));
				player.getInventory().add(new Item(ItemIdentifiers.SARADOMIN_BREW_4, 4));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 19));
				player.getInventory().add(new Item(ItemIdentifiers.ARMADYL_GODSWORD));
				player.sendMessage("Equipped ranged DPS set - Armadyl.");
			} else if (index == 4) {
				player.getEquipment().set(Equipment.SLOT_HEAD, new Item(ItemIdentifiers.PERNIX_COWL));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(ItemIdentifiers.PERNIX_BODY));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(ItemIdentifiers.PERNIX_CHAPS));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(ItemIdentifiers.CHAOTIC_CROSSBOW));
				player.getEquipment().set(Equipment.SLOT_BOOTS, new Item(ItemIdentifiers.GLAIVEN_BOOTS));
				player.getEquipment().set(Equipment.SLOT_GLOVES, new Item(ItemIdentifiers.BARROWS_GLOVES));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(ItemIdentifiers.AVAS_ACCUMULATOR));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(ItemIdentifiers.AMULET_OF_FURY));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(ItemIdentifiers.ARCHERS_RING_I));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(ItemIdentifiers.DRAGON_BOLTS_E, 100000));

				player.getInventory().add(new Item(ItemIdentifiers.OVERLOAD_4));
				player.getInventory().add(new Item(ItemIdentifiers.SUPER_RESTORE_4, 2));
				player.getInventory().add(new Item(ItemIdentifiers.SARADOMIN_BREW_4, 4));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 19));
				player.getInventory().add(new Item(ItemIdentifiers.ARMADYL_GODSWORD));
				player.sendMessage("Equipped ranged DPS set - Pernix.");
			}

			// append health effect
			player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints());
			stop();
		} else if (isPhase(5)) {
			if (index == 1) {
				// Players Online
				List<Player> list = GameService.getWorld().listAll(p -> true);
				Collections.sort(list, Player.PLAYER_COMPARATOR_BY_USERNAME);
				QuestInterface ui = new QuestInterface();

				for (int i = 0; i < list.size() && !ui.isFull(); i++) {
					Player target = list.get(i);
					String name = target.getName();
					String formattedTotalLevel = NumberUtil.format(target.getSkill().getTotalLevel());
					int combatLevel = target.getSkill().getCombatLevel(true);
					String time = TimeUtil.toString(target.cachedPlaytime + System.currentTimeMillis() - target.loginTimeMillis);
					String line = "@dre@%s@bla@ - Lvl: @dre@%d@bla@ (@dre@%s@bla@) - Time: @dre@%s";
					line = String.format(line, name, combatLevel, formattedTotalLevel, time);
					ui.addContent(line);
				}

				ui.withTitle("@dre@Players Online (" + list.size() + ")");
				ui.show(player);
				player.dialogue().interrupt(); // don't close loot interfaces
			} else if (index == 2) {
				// Inventory o' mysteries
				boolean hasPotato = player.getInventory().contains(ItemIdentifiers.ROTTEN_POTATO);
				player.getInventory().clear();

				if (hasPotato) {
					player.getInventory().add(new Item(ItemIdentifiers.ROTTEN_POTATO));
				}

				player.getInventory().add(new Item(ItemIdentifiers.SHABBY_BOOK));
				player.getInventory().add(new Item(ItemIdentifiers.MYSTERY_BOX, hasPotato ? 26 : 27));
				stop();
			} else if (index == 3) {
				// Slayer Log
				QuestInterface ui = new QuestInterface().withTitle("@dre@Slayer Log");
				NumberFormat f = NumberFormat.getInstance(Locale.US);
				ui.addContent("Abyssal demons: @dre@" + f.format(player.getVariable("kc_abyssal_demon", 0)));
				ui.addContent("Cave horrors: @dre@" + f.format(player.getVariable("kc_cave_horror", 0)));
				ui.addContent("Corporeal beast: @dre@" + f.format(player.getVariable("kc_corporeal_beast", 0)));
				ui.addContent("Dark beast: @dre@" + f.format(player.getVariable("kc_dark_beast", 0)));
				ui.addContent("King black dragon: @dre@" + f.format(player.getVariable("kc_king_black_dragon", 0)));
				ui.addContent("Nomad: @dre@" + f.format(player.getVariable("kc_nomad", 0)));
				ui.show(player);
				player.dialogue().interrupt(); // don't close loot interfaces
			}
		}
	}

	@Override
	protected void start() {
		choice("Cheat Sheet 1", "Set all stats", "Wipe inventory", "Access bank account", "Equipment Presets", "More Options");
	}

}
