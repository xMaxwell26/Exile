package com.koios.game.model.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;

/**
 * Handles ground items
 */
public final class GroundItemManager {

	public static final ItemBonusDefinition[] definitions = new ItemBonusDefinition[Constants.ITEM_LIMIT];
	private static final List<Integer> AMMUNITION = Collections.unmodifiableList(Arrays.asList(78, 879, 880, 881, 882, 884, 886, 888, 890, 892, 4740, 8882, 9139, 9140, 9141, 9142,
			9143, 9144, 9145, 9144, 9236, 9237, 9238, 9239, 9240, 9241, 9242, 9243, 9244, 9245, 9335, 9336, 9337, 9338, 9339, 9340, 9341, 9342, 11212));
	private static final List<GroundItemEntity> groundItems = new ArrayList<>();
	private static final int HIDE_TICKS = 100;

	public GroundItemManager() {
		Arrays.fill(definitions, null);
	}

	public void loadDefinitions() {
		parseBonusDefinitions("item.cfg");
		parsePriceDefinitions("prices.txt");
	}

	/**
	 * Remove item for everyone within 60 squares
	 */
	private static void removeGlobalItem(GroundItemEntity item) {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player != null) {
				if (player.distanceToPoint(item.getX(), item.getY()) <= 60) {
					player.getItems().removeGroundItem(item.getId(), item.getX(), item.getY(), item.getAmount());
				}
			}
		}

		removeItem(item);
	}

	/**
	 * Removes item from list
	 */
	private static void removeItem(GroundItemEntity item) {
		groundItems.remove(item);
        com.koios.game.arpg.GroundLootMetadata.removed(item);
	}

	/**
	 * Shows items for everyone who is within 60 squares
	 *
	 * @param item The item entity to be made global. Not null.
	 */
	private static void setVisibleToAll(GroundItemEntity item) {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player == null) {
				continue;
			}

			if (player.getUsernameHash() != item.getOwner()) {
				if (ItemDefinition.forId(item.getId()).isUntradeable()) {
					continue;
				}

				if (player.getZ() != item.getZ()) {
					continue;
				}

				if (player.distanceToPoint(item.getX(), item.getY()) <= 60) {
					player.getItems().createGroundItem(Item.of(item.getId(), item.getAmount()), Location.create(item.getX(), item.getY()));
				}
			}
		}
	}

 public static void spawnPrivate(Player player, Item item, Location at){
  GroundItemEntity entity=new GroundItemEntity(item,at.getX(),at.getY(),at.getZ(),Integer.MAX_VALUE,player.getUsernameHash());
  groundItems.add(entity);player.getItems().createGroundItem(item,at);com.koios.game.arpg.GroundLootMetadata.added(player,entity);
 }
 public static void clearArea(int x,int y,int size){for(GroundItemEntity item:new ArrayList<>(groundItems))if(item.getX()>=x&&item.getX()<x+size&&item.getY()>=y&&item.getY()<y+size)removeItem(item);}
 public static void syncMetadata(Player player){com.koios.game.arpg.GroundLootMetadata.snapshot(player,groundItems);}
	public static void spawnNew(Player player, Item item) {
		GroundItemEntity entity=new GroundItemEntity(item,player.getX(),player.getY(),player.getZ(),HIDE_TICKS,player.getUsernameHash());
        groundItems.add(entity);player.getItems().createGroundItem(item,player.getLocation());com.koios.game.arpg.GroundLootMetadata.added(player,entity);
	}

	public static void spawnNew(Player player, int id, int x, int y, int amount) {
		spawnNew(player, id, x, y, amount, false);
	}

	public static void spawnNew(Player player, int id, int x, int y, int amount, boolean instantVisibility) {
		if (id < 0 || id >= ItemDefinition.toArray().length) {
			throw new IllegalArgumentException(String.format("Bad logic [%d, %d, %d, %d]", id, amount, x, y));
		}

		if (ItemDefinition.forId(id) != null && !ItemDefinition.forId(id).isStackable() && amount == 1) {
			for (int i = 0; i < amount; i++) {
				player.getItems().createGroundItem(Item.of(id, 1), Location.create(x, y));
				GroundItemEntity item = new GroundItemEntity(id, x, y, player.getZ(), 1, instantVisibility ? 1 : HIDE_TICKS, player.getUsernameHash());
				groundItems.add(item);
            com.koios.game.arpg.GroundLootMetadata.added(player,item);
			}
		} else {
			player.getItems().createGroundItem(Item.of(id, amount), Location.create(x, y));
			GroundItemEntity item = new GroundItemEntity(id, x, y, player.getZ(), amount, instantVisibility ? 1 : HIDE_TICKS, player.getUsernameHash());
			groundItems.add(item);
            com.koios.game.arpg.GroundLootMetadata.added(player,item);
		}
	}

	public static void update() {
		ArrayList<GroundItemEntity> toRemove = new ArrayList<>();

		for (GroundItemEntity groundItem : groundItems) {
			if (groundItem != null) {
				if (groundItem.hideTicks > 1) {
					groundItem.hideTicks--;
				}

				if (groundItem.hideTicks == 1) {
					// item can now be seen by others
					groundItem.hideTicks = 0;
					setVisibleToAll(groundItem);
					groundItem.removeTicks = HIDE_TICKS;
				}

				if (groundItem.removeTicks > 0) {
					if (--groundItem.removeTicks == 0) {
						toRemove.add(groundItem);
					}
				}
			}
		}

		toRemove.stream().forEach(GroundItemManager::removeGlobalItem);
	}

	/**
	 * Item exists
	 */
	public boolean exists(Player player, int id, int x, int y) {
		for (GroundItemEntity item : groundItems) {
			if (item.getId() == id && item.getX() == x && item.getY() == y && item.getZ() == player.getZ()) {
				return item.hideTicks > 0 && item.getOwner() == player.getUsernameHash() || item.hideTicks <= 0;
			}
		}

		return false;
	}

	/**
	 * Item amount
	 */
	int getAmountOf(Player player, int id, int x, int y, int z) {
		for (GroundItemEntity item : groundItems) {
			if (item.getId() == id && item.getX() == x && item.getY() == y && item.getZ() == z) {
				if (item.hideTicks >= 1 && item.getOwner() == player.getUsernameHash() || item.hideTicks < 1) {
					return item.getAmount();
				}
			}
		}

		return 0;
	}

	private int[] getRequirements(final String name, final int id) {
		int[] requirements = new int[24];

		if (id == 10346 || id == 10348 || id == 10350 || id == 10352) {
			// Third-age warrior armour
			requirements[Skill.DEFENCE] = 65;
		} else if (id == 10338 || id == 10340 || id == 10342 || id == 10344) {
			// Third-age mage armour
			requirements[Skill.DEFENCE] = 30;
			requirements[Skill.MAGIC] = 65;
		} else if (id == 10330 || id == 10332 || id == 10334 || id == 10336) {
			// Third-age ranger armour
			requirements[Skill.DEFENCE] = 45;
			requirements[Skill.RANGED] = 65;
		} else if (id == 9948 || id == 9949 || id == 9950) {
			// Hunter cape, Hunter cape (t) and Hunter hood
			requirements[Skill.HUNTER] = 99;
		} else if (id == 13922 || id == 13910 || id == 13916) {
			// Corrupt statius equipment
			requirements[Skill.DEFENCE] = 20;
		} else if (id == 13926) {
			// Corrupt statius warhammer
			requirements[Skill.ATTACK] = 20;
		} else if (id == 11696 || id == 11700 || id == 11694 || id == 11698) {
			// Godswords
			requirements[Skill.ATTACK] = 75;
		} else if (name.contains("mystic") || name.contains("nchanted")) {
			if (name.contains("staff of light")) {
				requirements[Skill.MAGIC] = 75;
				requirements[Skill.ATTACK] = 75;
			}
			if (name.contains("staff")) {
				requirements[Skill.MAGIC] = 20;
				requirements[Skill.ATTACK] = 40;
			} else {
				requirements[Skill.MAGIC] = 20;
				requirements[Skill.DEFENCE] = 20;
			}
		} else if (name.startsWith("gilded")) {
			requirements[Skill.DEFENCE] = 40;
		} else if (name.contains("rock-shell")) {
			requirements[Skill.DEFENCE] = 40;
		} else if (name.contains("Dagon'hai")) {
			requirements[Skill.MAGIC] = 40;
			requirements[Skill.DEFENCE] = 20;
		} else if (name.contains("prosely")) {
			requirements[Skill.DEFENCE] = 30;
			requirements[Skill.PRAYER] = 20;
		} else if (name.contains("infinity")) {
			requirements[Skill.MAGIC] = 50;
			requirements[Skill.DEFENCE] = 25;
		} else if (name.contains("splitbark")) {
			requirements[Skill.MAGIC] = 40;
			requirements[Skill.DEFENCE] = 40;
		} else if (name.contains("rune c'bow") || name.contains("rune crossbow")) {
			requirements[Skill.RANGED] = 61;
		} else if (name.contains("black d'hide")) {
			requirements[Skill.RANGED] = 70;
		} else if (name.contains("tzhaar-ket-om")) {
			requirements[Skill.STRENGTH] = 60;
		} else if (name.contains("red d'hide")) {
			requirements[Skill.RANGED] = 60;
		} else if (name.contains("blue d'hide")) {
			requirements[Skill.RANGED] = 50;
		} else if (name.contains("green d'hide")) {
			requirements[Skill.RANGED] = 40;
		} else if (name.contains("snakeskin")) {
			requirements[Skill.RANGED] = 40;
			requirements[Skill.DEFENCE] = 30;
		} else if (name.contains("initiate")) {
			requirements[Skill.DEFENCE] = 20;
			requirements[Skill.PRAYER] = 10;
		} else if (name.contains("bronze")) {
			if (!name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe")) {
				requirements[Skill.ATTACK] = requirements[Skill.DEFENCE] = 1;
			}
		} else if (name.contains("iron")) {
			if (!name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe")) {
				requirements[Skill.ATTACK] = requirements[Skill.DEFENCE] = 1;
			}
		} else if (name.contains("steel")) {
		} else if (name.contains("bolts")) {
		} else if (name.contains("beret")) {
		} else if (name.contains("arrow")) {
			if (!name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe") && !name.contains("scimitar")) {
				requirements[Skill.DEFENCE] = 0;
			} else if (name.contains("scimitar")) {
				requirements[Skill.ATTACK] = 5;
			}
		} else if (name.contains("black")) {
			if (!name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe") && !name.contains("vamb") && !name.contains("chap")
					&& !name.contains("scimitar") && !name.contains("boater") && !name.contains("headband") && !name.contains("cavalier") && !name.contains("mask")
					&& !name.contains("ele shirt") && !name.contains("ele legs")) {
				requirements[Skill.DEFENCE] = 10;
			} else if (name.contains("scimitar")) {
				requirements[Skill.ATTACK] = 10;
			}
		} else if (name.contains("mithril")) {
			if (!name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe") && !name.contains("scimitar")
					&& !name.contains("arrow") && !name.contains("crossbow")) {
				requirements[Skill.DEFENCE] = 20;
			} else if (name.contains("scimitar")) {
				requirements[Skill.ATTACK] = 20;
			}
		} else if (name.contains("polypore")) {
			requirements[Skill.MAGIC] = 80;
		} else if (name.contains("larupia")) {
			requirements[Skill.HUNTER] = 28;
		} else if (name.contains("kyatt")) {
			requirements[Skill.HUNTER] = 52;
		} else if (name.contains("adamant")) {
			if (!name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe") && !name.contains("scimitar")
					&& !name.contains("crossbow")) {
				requirements[Skill.DEFENCE] = 30;
			} else if (name.contains("scimitar")) {
				requirements[Skill.ATTACK] = 30;
			}
		} else if (name.contains("rune")) {
			if (!name.contains("cape") && !name.contains("knife") && !name.contains("dart") && !name.contains("javelin") && !name.contains("thrownaxe") && !name.contains("arrow")
					&& !name.contains("scimitar") && !name.contains("2h") && !name.contains("c'bow") && !name.contains("crossbow") && !name.contains("runecraft")) {
				requirements[Skill.DEFENCE] = 40;
			} else if (name.contains("scimitar") || name.contains("2h")) {
				requirements[Skill.ATTACK] = 40;
			}
		} else if (name.contains("granite shield") || name.contains("granite body") || name.contains("granite legs") || name.contains("granite helm")) {
			if (!name.contains("maul")) {
				requirements[Skill.DEFENCE] = 50;
			}
		} else if (name.contains("granite maul")) {
			if (!name.contains("shield")) {
				requirements[Skill.ATTACK] = 50;
				requirements[Skill.STRENGTH] = 50;
			}
		} else if (name.contains("warrior")) {
			if (!name.contains("ring")) {
				requirements[Skill.DEFENCE] = 40;
			}
		} else if (name.contains("dragonfire")) {
			requirements[Skill.DEFENCE] = 75;
		} else if (name.contains("enchanted")) {
			requirements[Skill.DEFENCE] = 40;
		} else if (name.contains("d'hide")) {
			if (!name.contains("chaps")) {
				requirements[Skill.DEFENCE] = 40;
				requirements[Skill.RANGED] = 40;
			}
		} else if (name.contains("ava's")) {
			requirements[Skill.RANGED] = 50;
		} else if (name.startsWith("fighter hat")) {
			requirements[Skill.DEFENCE] = 45;
		} else if (name.startsWith("dragon boots")) {
			requirements[Skill.DEFENCE] = 60;
		} else if (name.contains("dragon dagger")) {
			requirements[Skill.ATTACK] = 60;
		} else if (name.contains("drag dagger")) {
			requirements[Skill.ATTACK] = 60;
		} else if (name.contains("ancient")) {
			requirements[Skill.ATTACK] = 50;
		} else if (name.contains("hardleather")) {
			requirements[Skill.DEFENCE] = 10;
		} else if (name.contains("studded")) {
			requirements[Skill.DEFENCE] = 20;
		} else if (name.contains("bandos")) {
			if (!name.contains("godsword")) {
				requirements[Skill.STRENGTH] = 65;
				requirements[Skill.DEFENCE] = 65;
			}
		} else if (name.startsWith("dragon") || name.startsWith("corrupt dragon")) {
			if (name.endsWith("scimitar") || name.endsWith("claws") || name.endsWith("mace") || name.endsWith("battleaxe") || name.endsWith("longsword") || name.endsWith("hatchet")
					|| name.endsWith("pickaxe")) {
				requirements[Skill.ATTACK] = 60;
			} else if (name.endsWith("full helm") || name.endsWith("platebody") || name.endsWith("platelegs") || name.endsWith("plateskirt") || name.endsWith("defender")
					|| name.endsWith("med helm") || name.endsWith("chainbody")) {
				requirements[Skill.DEFENCE] = 60;
			}
		} else if (name.contains("crystal")) {
			if (name.contains("shield")) {
				requirements[Skill.DEFENCE] = 70;
			} else {
				requirements[Skill.RANGED] = 70;
			}
		} else if (name.contains("torva")) {
			requirements[Skill.DEFENCE] = requirements[Skill.STRENGTH] = requirements[Skill.HITPOINTS] = 80;
		} else if (name.contains("pernix")) {
			requirements[Skill.DEFENCE] = requirements[Skill.RANGED] = requirements[Skill.HITPOINTS] = 80;
		} else if (name.contains("virtus")) {
			requirements[Skill.DEFENCE] = requirements[Skill.MAGIC] = requirements[Skill.HITPOINTS] = 80;
		} else if (name.contains("ahrim")) {
			if (name.contains("staff")) {
				requirements[Skill.MAGIC] = 70;
				requirements[Skill.ATTACK] = 70;
			} else {
				requirements[Skill.MAGIC] = 70;
				requirements[Skill.DEFENCE] = 70;
			}
		} else if (name.contains("karil")) {
			if (name.contains("crossbow")) {
				requirements[Skill.RANGED] = 70;
			} else {
				requirements[Skill.RANGED] = 70;
				requirements[Skill.DEFENCE] = 70;
			}
		} else if (name.contains("armadyl")) {
			if (name.contains("godsword")) {
				requirements[Skill.ATTACK] = 75;
			} else if (!name.contains("staff")) {
				requirements[Skill.RANGED] = requirements[Skill.DEFENCE] = 65;
			}
		} else if (name.contains("saradomin")) {
			if (name.contains("sword")) {
				requirements[Skill.ATTACK] = 70;
			}
			if (name.contains("crozier")) {
				requirements[Skill.ATTACK] = 1;

				if (name.contains("robe")) {
					requirements[Skill.ATTACK] = 1;
				} else {
					requirements[Skill.DEFENCE] = 40;
				}
			}
		} else if (name.contains("godsword")) {
			requirements[Skill.ATTACK] = 75;
		} else if (name.contains("3rd age") && !name.contains("amulet")) {
			requirements[Skill.DEFENCE] = 60;
		} else if (name.contains("verac") || name.contains("guthan") || name.contains("dharok") || name.contains("torag")) {
			if (name.contains("hammers")) {
				requirements[Skill.ATTACK] = 70;
				requirements[Skill.STRENGTH] = 70;
			} else if (name.contains("axe")) {
				requirements[Skill.ATTACK] = 70;
				requirements[Skill.STRENGTH] = 70;
			} else if (name.contains("warspear")) {
				requirements[Skill.ATTACK] = 70;
				requirements[Skill.STRENGTH] = 70;
			} else if (name.contains("flail")) {
				requirements[Skill.ATTACK] = 70;
				requirements[Skill.STRENGTH] = 70;
			} else {
				requirements[Skill.DEFENCE] = 70;
			}
		}

		switch (id) {
		case 19478:
			requirements[Skill.ATTACK] = 80;
			break;

		case 10069: // Spotted cape
			requirements[Skill.HUNTER] = 40;
			break;

		case 10071: // Spottier cape
			requirements[Skill.HUNTER] = 66;
			break;

		case 10075: // Gloves of silence
			requirements[Skill.HUNTER] = 54;
			break;

		case 2577:
		case 2581:
			requirements[Skill.RANGED] = 40;
			break;

		case 19780: // Korasi sword
			requirements[Skill.ATTACK] = 78;
			requirements[Skill.STRENGTH] = 78;
			break;

		case 20671: // Brackish blade
			requirements[Skill.ATTACK] = 60;
			break;

		case 2665:
		case 2661:
		case 2663:
		case 2667:
		case 2657:
		case 2653:
		case 2655:
		case 2659:
		case 2673:
		case 2669:
		case 2671:
		case 2675:
			requirements[Skill.DEFENCE] = 40;
			break;

		case 15486: // Staff of light
			requirements[Skill.ATTACK] = 75;
			requirements[Skill.MAGIC] = 75;
			break;

		case 8839:
		case 8840:
		case 8842:
		case 11663:
		case 11664:
		case 11665:
			requirements[Skill.ATTACK] = 42;
			requirements[Skill.RANGED] = 42;
			requirements[Skill.STRENGTH] = 42;
			requirements[Skill.MAGIC] = 42;
			requirements[Skill.DEFENCE] = 42;
			break;

		case 11716:
		case 11730:
			requirements[Skill.ATTACK] = 70;
			break;

		case 6528:
			requirements[Skill.STRENGTH] = 50;
			break;

		case 10551:
		case 2503:
		case 2501:
		case 2499:
		case 1135:
			requirements[Skill.DEFENCE] = 40;
			break;

		case 11235:
		case 6522:
			requirements[Skill.RANGED] = 60;
			break;

		case 6524:
			requirements[Skill.DEFENCE] = 60;
			break;

		case 20000:
			requirements[Skill.DEFENCE] = 75;
			requirements[Skill.ATTACK] = 75;
			break;

		case 20001:
			requirements[Skill.DEFENCE] = 75;
			requirements[Skill.RANGED] = 75;
			break;

		case 20002:
			requirements[Skill.DEFENCE] = 75;
			requirements[Skill.MAGIC] = 75;
			break;

		case 11284:
		case 11283:
			requirements[Skill.DEFENCE] = 75;
			break;

		case 6889:
		case 6914:
			requirements[Skill.MAGIC] = 60;
			break;

		case 13905:
		case 13899:
		case 13902:
			requirements[Skill.ATTACK] = 78;
			break;

		case 1187: // Dragon sq
		case 13964:
			requirements[Skill.DEFENCE] = 60;
			break;

		case 13887:
		case 13893: // Pvp armor
		case 13877:
		case 13890:
		case 13884:
		case 13896:
			requirements[Skill.DEFENCE] = 78;
			break;

		case 13876:
		case 13870:
		case 13873:
			requirements[Skill.DEFENCE] = 78;
			break;

		case 13944:
		case 13950:
		case 13947:
			requirements[Skill.DEFENCE] = 20;
			break;

		case 13858:
		case 13862:
		case 13864:
			requirements[Skill.MAGIC] = 78;
			requirements[Skill.DEFENCE] = 78;
			break;

		case 13736:
		case 13734:
		case 13740:
		case 13742:
		case 13738:
		case 13744:
			requirements[Skill.DEFENCE] = 75;
			break;

		case 13882:
		case 13881:
		case 13880:
		case 13879:
		case 13883:
			requirements[Skill.RANGED] = 78;
			break;

		case 861:
			requirements[Skill.RANGED] = 50;
			break;

		case 20171:
			requirements[Skill.RANGED] = 80;
			break;

		case 10828:
			requirements[Skill.DEFENCE] = 55;
			break;

		case 11724:
		case 11726:
		case 11728:
			requirements[Skill.DEFENCE] = 65;
			break;

		case 3751:
		case 3749:
		case 3755:
			requirements[Skill.DEFENCE] = 40;
			break;

		case 7462:
		case 7461:
			requirements[Skill.DEFENCE] = 40;
			break;

		case 8846:
			requirements[Skill.DEFENCE] = 5;
			break;

		case 8847:
			requirements[Skill.DEFENCE] = 10;
			break;

		case 8848:
			requirements[Skill.DEFENCE] = 20;
			break;

		case 8849:
			requirements[Skill.DEFENCE] = 30;
			break;

		case 8850:
			requirements[Skill.DEFENCE] = 40;
			break;

		case 20072:
			requirements[Skill.DEFENCE] = 60;
			break;

		case 10887:
			requirements[Skill.STRENGTH] = 60;
			break;

		case 7460:
			requirements[Skill.DEFENCE] = 20;
			break;

		case 15071:
			requirements[Skill.DEFENCE] = 60;
			break;

		case 837:
			requirements[Skill.RANGED] = 61;
			break;

		case 15441:
		case 15442:
		case 15443:
		case 15444:
		case 4151: // if you don't want to use names
			requirements[Skill.ATTACK] = 70;
			break;

		case 21371:
			requirements[Skill.ATTACK] = 85;
			requirements[Skill.SLAYER] = 80;
			break;

		case 6724: // seercull
			requirements[Skill.RANGED] = 60; // idk if that is correct
			break;

		case 1319: // rune 2h
			requirements[Skill.ATTACK] = 40;
			break;

		case 14484: // dclaw
			requirements[Skill.ATTACK] = 60;
			break;

		case 15241: // hand cannon
			requirements[Skill.RANGED] = 61;
			break;

		case 18349: // c rapier
			requirements[Skill.ATTACK] = 80;
			break;

		case 18351: // cls
			requirements[Skill.ATTACK] = 80;
			break;

		case 18353: // c maul
			requirements[Skill.ATTACK] = 80;
			break;

		case 16425:
			requirements[Skill.STRENGTH] = 99;
			break;

		case 18355: // c staff
			requirements[Skill.ATTACK] = 80;
			requirements[Skill.MAGIC] = 80;
			break;

		case 19476: // c staff
			requirements[Skill.MAGIC] = 80;
			break;

		case 18357: // c cbow
			requirements[Skill.RANGED] = 80;
			break;

		case 15042: // eagle-eye
			requirements[Skill.RANGED] = 80;
			requirements[Skill.DEFENCE] = 80;
			break;

		case 15043: // c kite
			requirements[Skill.DEFENCE] = 80;
			break;

		case 4153:
			requirements[Skill.ATTACK] = 50;
			requirements[Skill.STRENGTH] = 50;
			break;
		}

		return requirements;
	}

	private void newItemList(int id, double value, int[] bonuses) {
		definitions[id] = new ItemBonusDefinition(id);
		definitions[id].generalStoreValue = value;
		ItemDefinition itemDef = ItemDefinition.forId(id);
		int[] requirements = getRequirements(itemDef == null ? "" : itemDef.getName().toLowerCase(), id);

		for (int bonus : bonuses) {
			if (bonus == 0) {
				continue;
			}

			definitions[id].bonuses = bonuses;
			break;
		}

		for (int requirement : requirements) {
			if (requirement == 0) {
				continue;
			}

			definitions[id].requirements = requirements;
			break;
		}
	}

	private boolean parseBonusDefinitions(String FileName) {
		String line;
		String token;
		String token2;
		String token2_2;
		String[] token3;
		BufferedReader characterfile;

		try {
			characterfile = new BufferedReader(new FileReader("./data/cfg/" + FileName));
		} catch (FileNotFoundException fileex) {
			System.out.println(FileName + ": file not found.");
			return false;
		}

		try {
			characterfile.readLine();
		} catch (IOException ioexception) {
			System.out.println(FileName + ": error loading file.");

			try {
				characterfile.close();
				return false;
			} catch (IOException ex) {
			}
		}

		try {
			while ((line = characterfile.readLine()) != null) {
				line = line.trim();
				int spot = line.indexOf("=");

				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token2_2 = token2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token3 = token2_2.split("\t");

					if (token.equals("item")) {
						final int id = Integer.parseInt(token3[0]);

						if (id < 0 || id >= ItemDefinition.toArray().length) {
							continue;
						}

						int[] bonuses = new int[12];

						for (int i = 0; i < 12; i++) {
							if (token3[6 + i] != null) {
								bonuses[i] = Integer.parseInt(token3[6 + i]);
							} else {
								break;
							}
						}

						newItemList(id, Double.parseDouble(token3[4]), bonuses);
					}
				} else {
					if (line.equals("[ENDOFITEMLIST]")) {
						try {
							characterfile.close();
							return true;
						} catch (IOException ioexception) {
						}
					}
				}
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}

		return false;
	}

	private void parsePriceDefinitions(String filename) {
		try (Scanner scanner = new Scanner(new File("./data/cfg/" + filename))) {
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split(" ");
				int id = Integer.parseInt(line[0]);
				ItemBonusDefinition def = definitions[id];

				if (def == null) {
					definitions[id] = new ItemBonusDefinition(id);
					def = definitions[id];
				}

				def.generalStoreValue = Integer.parseInt(line[1]);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Reloads any items if you enter a new region
	 */
	public void reloadItems(Player player) {
		Objects.requireNonNull(player);
        syncMetadata(player);

		for (GroundItemEntity item : groundItems) {
			if (item.getZ() != player.getZ()) {
				continue;
			}

			if (!ItemDefinition.forId(item.getId()).isUntradeable() || item.getOwner() == player.getUsernameHash()) {
				if (player.distanceToPoint(item.getX(), item.getY()) <= 60) {
					if (item.hideTicks == 0 || item.getOwner() == player.getUsernameHash()) {
						player.getItems().removeGroundItem(item.getId(), item.getX(), item.getY(), item.getAmount());
						player.getItems().createGroundItem(Item.of(item.getId(), item.getAmount()), Location.create(item.getX(), item.getY()));
					}
				}
			}
		}
	}

	/**
	 * Remove item for just the item controller (item not global yet)
	 */
	private void removeControllersItem(GroundItemEntity item, Player player, int id, int x, int y, int amount) {
		player.getItems().removeGroundItem(id, x, y, amount);
		removeItem(item);
	}

	/**
	 * Removing the ground item
	 */
	public boolean removeGroundItem(Player player,int id,int x,int y,int z,boolean take){
  for(GroundItemEntity item:groundItems)if(item.getId()==id&&item.getX()==x&&item.getY()==y&&item.getZ()==z&&(item.hideTicks<=0||item.getOwner()==player.getUsernameHash()))return takeGroundEntity(player,item,take);
  return false;
 }
 public boolean removeExactGroundItem(Player player,GroundItemEntity item){if(item==null||!groundItems.contains(item)||item.getOwner()!=player.getUsernameHash()||item.getZ()!=player.getZ()||item.getX()!=player.getX()||item.getY()!=player.getY())return false;return takeGroundEntity(player,item,true);}
 private boolean takeGroundEntity(Player player,GroundItemEntity item,boolean take){int id=item.getId(),x=item.getX(),y=item.getY();
if (item.hideTicks > 0 && item.getOwner() == player.getUsernameHash()) {
					if (take) {
						if (!ItemAssistant.specialCase(id)) {
							if (player.getEquipment().get(Equipment.SLOT_ARROWS) != null && player.getEquipment().get(Equipment.SLOT_ARROWS).getId() == id
									&& AMMUNITION.contains(id)) {
								int amount = player.getEquipment().get(Equipment.SLOT_ARROWS).getCount() + item.getAmount();

								if (amount < 0) // overflow
								{
									player.sendMessage("You don't have enough space to pick this up.");
									return false;
								} else {
									player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(id, amount));
									removeControllersItem(item, player, item.getId(), item.getX(), item.getY(), item.getAmount());
									return true;
								}
							} else if (player.getInventory().add(item.getItem())) {
								removeControllersItem(item, player, item.getId(), item.getX(), item.getY(), item.getAmount());
								return true;
							} else {
								player.sendMessage("You do not have enough inventory space.");
								return false;
							}

						} else {
							removeControllersItem(item, player, item.getId(), item.getX(), item.getY(), item.getAmount());
							return true;
						}
					} else {
						removeControllersItem(item, player, item.getId(), item.getX(), item.getY(), item.getAmount());
						return true;
					}
				} else if (item.hideTicks <= 0) {
					if (take) {
						if (player.getInventory().add(item.getItem())) {
							removeGlobalItem(item);
							return true;
						} else {
							player.sendMessage("You do not have enough inventory space.");
							return false;
						}
					} else {
						removeGlobalItem(item);
						return true;
					}
				}
  return false;
 }

	/**
	 * Gets a list of ground items for a specific player
	 *
	 * @param player The {@link Player} to get the ground item for
	 * @return A list containing all of the ground items belonging to a specific player
	 */
	public List<GroundItemEntity> getPlayerItems(Player player) {
		return groundItems.stream().filter(item -> item.getOwner() == player.getUsernameHash()).collect(Collectors.toList());
	}

	/**
	 * Check if the player has a ground item
	 *
	 * @param player The {@link Player} to check ground items for
	 * @param id The id of the item to check for
	 * @return If the player has a local ground item
	 */
	public boolean playerHasItem(Player player, int id) {
		return groundItems.stream().filter(e -> e.getOwner() == player.getUsernameHash() && e.getId() == id).findAny().isPresent();
	}

}
