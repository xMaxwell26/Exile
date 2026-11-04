package com.koios.game.content.dialogue.impl.admin;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.QuestInterface;
import com.koios.game.content.dialogue.impl.misc.AncientAltarDialogue;
import com.koios.game.content.dialogue.impl.misc.TitleSetupDialogue;
import com.koios.game.content.dialogue.impl.util.SimpleChoice;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.MobDefinition;
import com.koios.game.model.Damage;
import com.koios.game.model.Location;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.mob.impl.MobAbyssalDemon;
import com.koios.game.model.player.Player;
import com.koios.network.in.impl.PacketReportAbuse;
import com.koios.network.out.impl.SendSetWidgetColor;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;
import com.koios.util.TimeUtil;

/**
 * An administrative dialogue that is invoked by interaction with the Rotten potato item.
 * <p>
 * The primary purpose of this utility is to have an item to hold temporary test commands.
 *
 * @author Erik Eide
 */
public final class RottenPotatoDialogue extends Dialogue {

	public static final int STATE_EAT = 0;
	public static final int STATE_HEAL = 20;
	public static final int STATE_OPTIONS = 1;
	public static final int STATE_COMMANDS = 2;
	public static final int STATE_TARGET = 5;

	// holder while we wait for a similiar core-level representation type
	private static class Position {

		private final String name;
		private final int x;
		private final int y;

		private Position(String name, int x, int y) {
			this.name = Objects.requireNonNull(name, "name");
			this.x = x;
			this.y = y;
		}

	}

	private static final Position[] TELEPORT_DESTINATIONS = { new Position("Edgeville", 3087, 3492), new Position("Varrock", 3210, 3424), new Position("Catherby", 2804, 3433),
			new Position("Pest Control", 2662, 2651) };

	/**
	 * Reference to the target.
	 */
	private final Player target;
	private final String targetName;

	public RottenPotatoDialogue(Player player, int defaultPhase) {
		super(player, defaultPhase);
		target = null;
		targetName = "null";
	}

	public RottenPotatoDialogue(Player player, int defaultPhase, Player target) {
		super(player, defaultPhase);
		Objects.requireNonNull(target, "target");
		this.target = target;
		targetName = target.getName();
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
		} else if (isPhase(5)) {

			if (target != null) {
				if (target.setEntityAppearance(value)) {
					String mobName = MobDefinition.get(value).getName();
					target.sendMessage(player.getName() + " changed your appearance to: " + mobName);
					player.sendMessage("You've changed " + target.getName() + "'s appearance to: " + mobName);
					stop();
				} else {
					sendStatement("Invalid mob data value.");
				}
			} else {
				sendStatement(targetName + " is no longer available!");
			}
		} else if (isPhase(10)) {
			// Transmogrify me...

			if (player.setEntityAppearance(value)) {
				player.sendMessage("You've changed appearance to: " + MobDefinition.get(value).getName());
				stop();
			} else {
				sendStatement("Invalid mob data value.");
			}
		}
	}

	@Override
	public void input(String name) {
		// Target player

		if (isPhase(11)) {
			Optional<Player> target = GameService.getWorld().getByUsername(name);

			if (target.isPresent()) {
				player.dialogue().start(new RottenPotatoDialogue(player, STATE_TARGET, target.get()));
			} else {
				setPhase(0xBABE); // unused label to cancel on action
				sendStatement(StringUtil.formatUsername(name) + " could not be found.");
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
				player.getInventory().clear();
				player.getInventory().add(new Item(5733, 1));
				stop();
			} else if (index == 3) {
				// Access bank account
				stop();
				player.getBankField().setBanking(true);
				Bank.open(player);
			} else if (index == 4) {
				// Transmogrify me...
				setPhase(10);
				takeValueInput();
			} else if (index == 5) {
				// Be yourself

				if (player.getEntityAppearance() != -1) {
					String name = MobDefinition.get(player.getEntityAppearance()).getName();
					player.sendMessage("You do no longer have the appearance of " + name + ".");
					player.setEntityAppearance(-1);
				} else {
					player.sendMessage("You have no appearance override!");
				}

				stop();
			}
		} else if (isPhase(1)) {
			if (index == 1) {
				// Toggle invulnerability
				boolean invulnerable = !player.isInvulnerable();
				player.setInvulnerable(invulnerable);

				if (invulnerable) {
					player.sendMessage("You will no longer take damage when hit.");
				} else {
					player.sendMessage("You will now take damage when hit.");
				}

				stop();
			} else if (index == 2) {
				// Toggle instant-kill
				boolean instaKill = !player.isInstantKill();
				player.setInstantKill(instaKill);

				if (instaKill) {
					player.sendMessage("You will now one-hit all entities with melee.");
				} else {
					player.sendMessage("You will no longer one-hit all entities with melee.");
				}

				stop();
			} else if (index == 3) {
				// Toggle invisibility
				player.setVisible(!player.isVisible());

				if (!player.isVisible()) {
					player.sendMessage("You are no longer visible to surrounding players.");
				} else {
					player.sendMessage("You are now visible to surrounding players.");
				}

				player.setPosition(player.getX(), player.getY(), player.getZ());
				stop();
			} else if (index == 4) {
				// Toggle inattackable
				boolean inattackable = !player.isInattackable();
				player.setInattackable(inattackable);

				if (inattackable) {
					player.sendMessage("Other entities will no longer be able to attack you.");
				} else {
					player.sendMessage("You are now attackable by other entities.");
				}

				stop();
			} else if (index == 5) {
				// Next Page
				setPhase(11);
				choice("Rotten Potato", "Completionist cape", "Wipe bank", "Go to Duel Arena", "Target player", "Previous Page");
			}
		} else if (isPhase(5)) {

			if (target != null) {
				if (index == 1) {
					// Account Options
					setPhase(7);
					choice(target.getName(), "Check Time Online", "Check Connection Details", "Donation Status", "Next Page", "Sanctions Menu");
				} else if (index == 2) {
					// Force teleport
					setPhase(6);
					choice("Destination", "Edgeville", "Varrock", "Catherby", "Pest Control", "Go Back");
				} else if (index == 3) {
					// Transmogrify...
					takeValueInput();
				} else if (index == 4) {
					// Reset appearance

					if (target.getEntityAppearance() != -1) {
						target.sendMessage("You do no longer have the appearance of " + MobDefinition.get(target.getEntityAppearance()).getName() + ".");
						target.setEntityAppearance(-1);
					}

					stop();
				}
			} else {
				sendStatement(targetName + " is no longer available!");
			}
		} else if (isPhase(6)) {

			if (target != null) {
				if (index == 5) {
					setPhase(5);
					choice(target.getName(), "Account Options", "Force teleport", "Transmogrify...", "Reset appearance");
				} else {
					// Force teleport
					int x = TELEPORT_DESTINATIONS[index - 1].x;
					int y = TELEPORT_DESTINATIONS[index - 1].y;
					String name = TELEPORT_DESTINATIONS[index - 1].name;

					target.setPosition(x, y, 0);
					target.sendMessage(player.getName() + " teleported you to " + name + ".");
					player.sendMessage("Teleported " + target.getName() + " to " + name + ".");
					stop();
				}
			} else {
				sendStatement(targetName + " is no longer available!");
			}
		} else if (isPhase(7)) {

			if (target != null) {
				if (index == 1) {
					// Check Time Online
					sendStatement(target.getName() + "'s time online:", TimeUtil.toString(target.cachedPlaytime + System.currentTimeMillis() - target.loginTimeMillis));
				} else if (index == 2) {
					// Check Connection Details
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.US);
					LocalDateTime sessionStart = LocalDateTime.ofEpochSecond(target.loginTimeMillis / 1000, 0, ZoneOffset.UTC);

					sendStatement("@blu@" + target.getName() + "'s Connection Details:", "Host Address: @dre@" + target.getSession().getHostAddress(),
							"Host Name: @dre@" + target.getSession().getHostName(), "Started: @dre@" + sessionStart.format(formatter),
							"Duration: @dre@" + TimeUtil.toString(System.currentTimeMillis() - target.loginTimeMillis) + "@bla@ | Window Focus: @dre@" + target.hasFocus());
				} else if (index == 3) {
					// Donation Status
					NumberFormat formatter = NumberFormat.getInstance(Locale.US);

					sendStatement("@blu@" + target.getName() + "'s Donation Status:", player.getDonatorPrivilege().name() + "",
							"Points Claimed (total): @dre@" + formatter.format(target.totalDonationAmount), ":: Tracking Since June ::");
				} else if (index == 4) {
					// Next Page
					setPhase(15);
					choice(target.getName(), "Just kill " + target.getName(), "Reset Slayer Assignment", "View Slayer Log", "Previous Page");
				} else if (index == 5) {
					// Next Page
					setPhase(8);
					choice(target.getName(), "Suspend (permanent)", "Silence (permanent)", "Suspend Address (permanent)", "Silence Address (permanent)");
				}
			} else {
				sendStatement(targetName + " is no longer available!");
			}
		} else if (isPhase(8)) {

			if (target != null) {
				if (index == 1) {
					// Permanent single-user ban.
					PacketReportAbuse.moderate(player.getName(), target.getName(), null, "ban", "Rotten potato ban!", 0);
					GameService.getWorld().unregister(player);
					stop();
				} else if (index == 2) {
					// Permanent single-user mute.
					PacketReportAbuse.moderate(player.getName(), target.getName(), null, "mute", "Rotten potato mute!", 0);
					target.setMuted(true);
					stop();
				} else if (index == 3) {
					// Potato IP-ban
					String address = target.getSession().getHostAddress();
					PacketReportAbuse.moderate(player.getName(), target.getName(), target.getSession().getHostAddress(), "ban", "Rotten potato ban!", 0);

					GameService.getWorld().execute(p -> address.equals(p.getSession().getHostAddress()), p -> {
						GameService.getWorld().unregister(p);
						player.sendMessage("Affected: " + p.getName());
					});

					stop();
				} else if (index == 4) {
					// Potato IP-mute
					String address = target.getSession().getHostAddress();
					PacketReportAbuse.moderate(player.getName(), target.getName(), target.getSession().getHostAddress(), "mute", "Rotten potato mute!", 0);

					GameService.getWorld().execute(p -> address.equals(p.getSession().getHostAddress()), p -> {
						p.setMuted(true);
						player.sendMessage("Affected: " + p.getName());
					});

					stop();
				}
			} else {
				sendStatement(targetName + " is no longer available!");
			}
		} else if (isPhase(20)) {
			if (index == 1) {
				// List Online Players (100 max.)
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
				// Equipment Presets
				setPhase(21);
				choice("Choose Preset", "Barrows Equipment", "DPS Equipment");
			} else if (index == 3) {
				// Title Setup
				player.dialogue().start(new TitleSetupDialogue(player));
			}
		} else if (isPhase(21)) {
			if (index == 1) {
				// Barrows Equipment
				setPhase(22);
				choice("Select an Option", "Ahrim's", "Dharok's", "Guthan's", "Karil's", "Next Page");
			} else if (index == 2) {
				// DPS Equipment
				setPhase(24);
				choice("Select an Option", "Bandos", "Torva", "Armadyl", "Pernix");
			}
		} else if (isPhase(22)) {
			if (index == 5) {
				// Next Page
				setPhase(23);
				choice("Select an Option", "Torag's", "Verac's", "Previous Page");
				return; // avoid call to stop
			}

			player.getInventory().clear();
			player.getEquipment().clear();
			player.getInventory().add(new Item(ItemIdentifiers.ROTTEN_POTATO));

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
		} else if (isPhase(23)) {
			if (index == 3) {
				// Previous Page
				setPhase(22);
				choice("Select an Option", "Ahrim's", "Dharok's", "Guthan's", "Karil's", "Next Page");
				return; // avoid call to stop
			}

			player.getEquipment().clear();
			player.getInventory().clear();
			player.getInventory().add(new Item(ItemIdentifiers.ROTTEN_POTATO));

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
		} else if (isPhase(24)) {
			player.getEquipment().clear();
			player.getInventory().clear();
			player.getInventory().add(new Item(ItemIdentifiers.ROTTEN_POTATO));

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
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 16));
				player.getInventory().add(new Item(ItemIdentifiers.DRAGON_CLAWS));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 8));
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
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 16));
				player.getInventory().add(new Item(ItemIdentifiers.DRAGON_CLAWS));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 8));
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
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 16));
				player.getInventory().add(new Item(ItemIdentifiers.ARMADYL_GODSWORD));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 8));
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
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 16));
				player.getInventory().add(new Item(ItemIdentifiers.ARMADYL_GODSWORD));
				player.getInventory().add(new Item(ItemIdentifiers.ROCKTAIL, 8));
				player.sendMessage("Equipped ranged DPS set - Pernix.");
			}

			// append health effect
			player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints());
			stop();
		} else if (isPhase(11)) {
			if (index == 1) {
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(ItemIdentifiers.COMPLETIONIST_CAPE));
				stop();
			} else if (index == 2) {
				Arrays.fill(player.getBankField().getTabAmounts(), 0);
				Bank.viewTab(player, 0);
				player.getBank().clear();
				player.sendMessage("Bank wiped!");
				stop();
			} else if (index == 3) {
				player.setPosition(3367, 3268, 0);
				stop();
			} else if (index == 4) {
				takeNameInput();
			} else if (index == 5) {
				String op1 = player.isInvulnerable() ? "Disable" : "Enable";
				String op2 = player.isInstantKill() ? "Disable" : "Enable";
				String op3 = !player.isVisible() ? "Disable" : "Enable";
				String op4 = player.isInattackable() ? "Disable" : "Enable";

				setPhase(1);
				choice("Rotten Potato", op1 + " invulnerability", op2 + " instant kill", op3 + " invisibility", op4 + " inattackability", "Next Page");
			}
		} else if (isPhase(12)) {
			if (index == 1) {
				// Kill all nearby entities
				if (GameService.getConfiguration().getInteger("rp_killall") != 1) {
					player.sendMessage("Gvar 'rp_killall' must be '1' to perform this action.");
					stop();
					return;
				}

				// Note: no need to stop() here because we loot a new dialogue.
				AtomicInteger count = new AtomicInteger();

				player.dialogue().start(new SimpleChoice(player, "Yes, kill all nearby entities.", p -> {
					GameService.getWorld().execute(t -> t != p && t.isWithinDistance(player, 3), target -> {
						int damage = target.getHitpoints();
						target.setDamage(new Damage(damage, HitIcons.NONE), player);
						count.incrementAndGet();
					});

					MobManager.execute(t -> t.isWithinDistance(player, 3) && t.isAttackable(), target -> {
						int damage = target.getHitpoints();
						target.setDamage(new Damage(damage, HitIcons.NONE), player);
						count.incrementAndGet();
					});

					int killCount = count.get();

					if (killCount > 0) {
						p.sendMessage("You've killed a total of <col=800000>" + killCount + "</col> entities.");
					}
				} , "No! Let them live!", p -> p.sendMessage("Cancelled.")));
			} else if (index == 2) {
				// Spawn dummy mob

				for (int x = -1; x < 2; x++) {
					for (int y = -1; y < 2; y++) {
						Mob mob = new MobAbyssalDemon(new Location(player.getX() + x, player.getY() + y, player.getZ()));
						mob.summoner = player;
						MobManager.spawn(mob);
					}
				}

				stop();
			} else if (index == 3) {
				// Set all stats 200M XP
				// Note: set, rather than add, to avoid level-up broadcast
				// messages.

				for (int i = 0; i < 24; i++) {
					player.getSkill().setSkill(i, 99, 200_000_000);
				}

				player.sendMessage("... cheat successful!");
				stop();
			} else if (index == 4) {
				// Spawn Blue Partyhat
				MobManager.spawnMob(player, 637, player.getX(), player.getY(), player.getZ(), 0, 0, 0, 0, 0, false, false);
				stop();
			} else if (index == 5) {
				// Next Page
				setPhase(13);
				choice("Rotten Potato", "Reset Achievement State", "Restock all shops", "Spellbook Swap", "Previous Page");
			}
		} else if (isPhase(13)) {
			if (index == 1) {
				// Reset Achievement State

				for (Achievement achievement : Achievement.values()) {
					player.setAchievementStatus(achievement.getUniqueId(), 0);
					player.getSession().send(new SendSetWidgetColor(achievement.getWidgetId(), 0xFF0000));
				}

				player.setAchievementPoints(0);
				player.sendMessage("All achievement states have been clear.");
				stop();
			} else if (index == 2) {
				// Restock all shops
				GameService.getShopManager().init();
				player.sendMessage("All shops have restocked.");
				stop();
			} else if (index == 3) {
				// Spellbook Swap
				player.dialogue().start(new AncientAltarDialogue(player));
			} else if (index == 4) {
				// Previous Page
				setPhase(12);
				choice("Rotten Potato", "Kill all nearby entities", "Spawn dummy mob", "Set all stats 200M XP", "Spawn Blue Partyhat", "Next Page");
			}
		} else if (isPhase(14)) {
			if (index == 1) {
				// Yes. Kill PLAYERNAME!

				if (target != null) {
					int damage = target.getHitpoints();
					target.setDamage(new Damage(damage, HitIcons.MELEE), player);
					target.isSkulled = true;
					stop();
				} else {
					sendStatement(targetName + " is no longer available!");
				}
			} else if (index == 2) {
				// Oh shit!
				player.sendMessage("This confirm option is a real life-saver.");
				stop();
			}
		} else if (isPhase(15)) {

			if (target != null) {
				if (index == 1) {
					// Just kill PLAYERNAME
					setPhase(14);
					choice("Confirm Action", "Yes. Kill " + target.getName() + "!", "Oh shit!");
				} else if (index == 2) {
					// Reset Slayer Assignment
					target.getSlayer().setAssignment(null);
					target.getSlayer().setKillsLeft(0);
					target.getSlayer().setPartner(null);
					target.sendMessage("Your Slayer assignment has been cancelled by " + player.getName() + ".");
					player.sendMessage("Cancelled " + target.getName() + "'s Slayer Assignment.");
					stop();
				} else if (index == 3) {
					// View Slayer Log
					QuestInterface ui = new QuestInterface().withTitle("@dre@" + target.getName() + " - Slayer Log");
					NumberFormat f = NumberFormat.getInstance(Locale.US);
					ui.addContent("Abyssal demons: @dre@" + f.format(target.getVariable("kc_abyssal_demon", 0)));
					ui.addContent("Bork: @dre@" + f.format(target.getVariable("kc_bork", 0)));
					ui.addContent("Cave horrors: @dre@" + f.format(target.getVariable("kc_cave_horror", 0)));
					ui.addContent("Chaos Elemental: @dre@" + f.format(target.getVariable("kc_chaos_elemental", 0)));
					ui.addContent("Corporeal beast: @dre@" + f.format(target.getVariable("kc_corporeal_beast", 0)));
					ui.addContent("Unholy cursebearer: @dre@" + f.format(target.getVariable("kc_cursebearer", 0)));
					ui.addContent("Dark beast: @dre@" + f.format(target.getVariable("kc_dark_beast", 0)));
					ui.addContent("Dagannoth Prime: @dre@" + f.format(target.getVariable("kc_dagannoth_prime", 0)));
					ui.addContent("Dagannoth Rex: @dre@" + f.format(target.getVariable("kc_dagannoth_rex", 0)));
					ui.addContent("Dagannoth Supreme: @dre@" + f.format(target.getVariable("kc_dagannoth_supreme", 0)));
					ui.addContent("King black dragon: @dre@" + f.format(target.getVariable("kc_king_black_dragon", 0)));
					ui.addContent("Nomad: @dre@" + f.format(target.getVariable("kc_nomad", 0)));
					ui.addContent("Tormented demon: @dre@" + f.format(target.getVariable("kc_tormented_demon", 0)));
					ui.show(player);
					player.dialogue().interrupt(); // don't close loot
													// interfaces
				} else if (index == 4) {
					// Previous Page
					setPhase(7);
					choice(target.getName(), "Check Time Online", "Check Connection Details", "Donation Status", "Next Page", "Sanction ");
				}
			} else {
				sendStatement(targetName + " is no longer available!");
			}
		}
	}

	@Override
	public void start() {
		if (!player.isSuperAdministrator()) {
			// Note: delete if used by a regular user.
			player.sendMessage("Too late - it's gone!");
			player.getInventory().remove(Item.of(ItemIdentifiers.ROTTEN_POTATO));
			stop();
			return;
		}

		if (isPhase(0)) {
			// Inventory action: Eat
			choice("Rotten Potato", "Set all stats", "Wipe inventory", "Access bank account", "Transmogrify me...", "Be yourself");
		} else if (isPhase(20)) {
			// Inventory action: Heal
			choice("Rotten Potato", "List Online Players (100 max.)", "Equipment Presets", "Title Setup");
		} else if (isPhase(1)) {
			// Inventory action: Options
			String op1 = player.isInvulnerable() ? "Disable" : "Enable";
			String op2 = player.isInstantKill() ? "Disable" : "Enable";
			String op3 = !player.isVisible() ? "Disable" : "Enable";
			String op4 = player.isInattackable() ? "Disable" : "Enable";

			choice("Rotten Potato", op1 + " invulnerability", op2 + " instant kill", op3 + " invisibility", op4 + " inattackability", "Next Page");
		} else if (isPhase(2)) {
			// Inventory action: Commands
			setPhase(12);
			choice("Rotten Potato", "Kill all nearby entities", "Spawn dummy mob", "Set all stats 200M XP", "Spawn Blue Partyhat", "Next Page");
		} else if (isPhase(5)) {
			// Use with Player

			if (target != null) {
				choice(target.getName(), "Account Options", "Teleport with force", "Transmogrify...", "Reset appearance");
			} else {
				// this should never happen here
				player.sendMessage("Unable to find " + targetName + ".");
				stop();
			}
		}
	}

}
