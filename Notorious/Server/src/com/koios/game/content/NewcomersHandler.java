package com.koios.game.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.friendschat.FriendschatCommons;
import com.koios.game.content.music.MusicTab;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public final class NewcomersHandler {

	private static final int CONFIG_ID_WALK_STATUS = 173;
	private static final int CONFIG_ID_BRIGHTNESS = 166;

	/**
	 * The maximum amount of starters you can get per ip
	 */
	private static final int MAX_COUNT = 2;

	/**
	 * A map of all the starters
	 */
	private static Map<String, Integer> starters = new HashMap<>();
	
	/**
	 * A path to all of the starters
	 */
	private static final Path STARTER_PATH = Paths.get("./data/starter.txt");

	/**
	 * Checks if the player can receive a starter
	 * @param player The {@link Player} receiving the starter
	 * @return If the player can get a starter pack
	 */
	private static boolean canAddStarter(Player player) {
		final String address = player.getSession().getHostAddress();

		int count = starters.get(address) == null ? 0 : starters.get(address).intValue();
		
		if (count >= MAX_COUNT) {
			player.sendMessage("You cannot receive another starter.");
			return false;
		}
		
		starters.put(address, count + 1);
		save();
		return true;
	}

	static {
		
		if (!Files.exists(STARTER_PATH)) {
			try {
				Files.createFile(STARTER_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		load();
	}

	/**
	 * Saves the starter information
	 */
	private static void save() {
		try (BufferedWriter writer = Files.newBufferedWriter(STARTER_PATH)) {
			for (Entry<String, Integer> entry : starters.entrySet()) {
				writer.write(entry.getKey() + " = " + entry.getValue());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the starter information
	 */
	private static void load() {
		try (BufferedReader reader = Files.newBufferedReader(STARTER_PATH)) {
			String line = "";
			
			while ((line = reader.readLine()) != null && !line.isEmpty()) {
				String[] args = line.split(" = ");
				starters.put(args[0], Integer.parseInt(args[1]));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void welcome(Player player) {
		// Configure settings
		player.getSession().send(new SendConfigPacket(CONFIG_ID_WALK_STATUS, 0));
		player.getSession().send(new SendConfigPacket(CONFIG_ID_BRIGHTNESS, 4));
		player.getSkill().incrementLevel(Skill.SUMMONING, 1);


		// populate music defaults
		MusicTab.initializeMusicBooleanFirstTime(player);

		// Join default friends chat if it has available space
		if (GameService.isOnlineMode()) {
			FriendschatCommons.attemptJoinFriendschat(player, Constants.DEFAULT_FRIENDSCHAT, false);
		}

        player.setAttribute("tutorial",false);
        com.koios.game.arpg.Tutorial.initialize(player);
	}

	private static void addEquipment(Player player) {
		Container equipment = player.getEquipment();
		equipment.set(Equipment.SLOT_HEAD, Item.of(ItemIdentifiers.IRON_FULL_HELM));
		equipment.set(Equipment.SLOT_CHEST, Item.of(ItemIdentifiers.IRON_PLATEBODY));
		equipment.set(Equipment.SLOT_LEGS, Item.of(ItemIdentifiers.IRON_PLATELEGS));
		equipment.set(Equipment.SLOT_BOOTS, Item.of(ItemIdentifiers.CLIMBING_BOOTS));
		equipment.set(Equipment.SLOT_AMULET, Item.of(ItemIdentifiers.AMULET_OF_POWER));
		equipment.set(Equipment.SLOT_GLOVES, Item.of(ItemIdentifiers.COMBAT_BRACELET_4));
		equipment.set(Equipment.SLOT_WEAPON, Item.of(ItemIdentifiers.IRON_SCIMITAR));
		equipment.set(Equipment.SLOT_SHIELD, Item.of(ItemIdentifiers.UNHOLY_BOOK));
		int capeId = NumberUtil.random(new int[] { 4325, 4345, 4365, 4385, 4407, 4371, 4347 });
		equipment.set(Equipment.SLOT_CAPE, Item.of(capeId));
	}

	private static void addInventory(Player player) {
		Container inventory = player.getInventory();
		inventory.add(Item.of(ItemIdentifiers.COINS, 500_000));
		inventory.add(Item.of(ItemIdentifiers.CHAOS_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.DEATH_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.FIRE_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.WATER_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.AIR_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.MIND_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.EARTH_RUNE, 500));
		inventory.add(Item.of(ItemIdentifiers.BLUE_SKIRT, 1));
		inventory.add(Item.of(ItemIdentifiers.WIZARD_HAT, 1));
		inventory.add(Item.of(ItemIdentifiers.WIZARD_ROBE, 1));
		inventory.add(Item.of(ItemIdentifiers.APPRENTICE_WAND, 1));
		inventory.add(Item.of(ItemIdentifiers.LEATHER_BODY, 1));
		inventory.add(Item.of(ItemIdentifiers.LEATHER_CHAPS, 1));
		inventory.add(Item.of(ItemIdentifiers.MAGIC_SHORTBOW, 1));
		inventory.add(Item.of(ItemIdentifiers.SHORTBOW, 1));
		inventory.add(Item.of(ItemIdentifiers.IRON_ARROW, 500));
		inventory.add(Item.of(ItemIdentifiers.ADAMANT_ARROW, 500));
		inventory.add(Item.of(ItemIdentifiers.RUNE_SCIMITAR, 1));
		inventory.add(Item.of(ItemIdentifiers.SWORDFISH_NOTE, 500));
		inventory.add(Item.of(ItemIdentifiers.IRON_PICKAXE, 1));
		inventory.add(Item.of(ItemIdentifiers.IRON_AXE, 1));
		inventory.add(Item.of(ItemIdentifiers.SMALL_FISHING_NET, 1));
	}

	private NewcomersHandler() {
		throw new InstantiationError();
	}

}
