package com.koios.game.content.command.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.content.command.Command;
import com.koios.game.content.dialogue.impl.util.SimpleChoice;
import com.koios.game.content.highscores.Highscores;
import com.koios.game.content.minigames.prophunt.PropHuntLobby;
import com.koios.game.content.randoms.MysteryBoxRandomEvent;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.slayer.Master;
import com.koios.game.def.ItemDefinition;
import com.koios.game.def.MobDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.Container.Type;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.DonatorPrivilege;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendObjectPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.impl.SystemUpdateTask;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

public class SuperAdminCommands implements Command {

	@Override
	public void execute(Player player, String command) {
		String[] args = command.split(" ");

		switch (args[0]) {
		case "hs":
			Highscores.dispatch(player, Highscores.PK_HIGHSCORES);
			break;

		case "spec":
			player.specialEnergy = args.length >= 2 ? 1000 : 100;

			if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				player.getItems().addSpecialBar(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
				player.getItems().updateSpecialBar();
			}
			break;

		case "toggleloot":
			player.setDropItems(!player.dropItems());
			player.sendMessage(player.dropItems() ? "Loot will be dropped." : "Loot won't be dropped.");
			break;

		case "bonuses":
			for (int i = 0; i < player.playerBonus.length; i++) {
				player.sendMessage("[ " + i + " ] Bonus: " + player.playerBonus[i]);
			}
			break;

		case "definitions":
			switch (args[1]) {
			case "shops":
				GameService.getShopManager().init();
				player.sendMessage("Successfully reloaded shops.");
				break;
			case "mobdef":
				MobDefinition.parse().load();
				player.sendMessage("Successfully reloaded mob definitions.");
				break;
			case "itemdef":
				ItemDefinition.load();
				player.sendMessage("Successfully reloaded item definitions.");
				break;
			}
			break;

		case "sendstring":
			if (args.length < 3) {
				return;
			}

			String s = "";

			for (int i = 2; i < args.length; i++) {
				s += args[i];
			}

			player.getSession().send(new SendStringPacket(s, Integer.parseInt(args[1])));
			break;

		case "barrowsreset":
			for (int i = 0; i < player.getKilledBrothers().length; i++) {
				player.getKilledBrothers()[i] = false;
			}

			player.setHiddenBrother(-1);
			player.setBarrowsKillCount(0);
			break;

		case "config":
			if (args.length < 3) {
				return;
			}

			player.sendMessage("Sent config.");
			player.getSession().send(new SendConfigPacket(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
			break;

		case "starter":
			player.sendMessage("A starter kit has been supplied to " + player.getName() + ".");
			boolean invRefresh = player.getInventory().isFiringEvents();
			boolean equipRefresh = player.getEquipment().isFiringEvents();
			player.getInventory().setFiringEvents(false);
			player.getEquipment().setFiringEvents(false);
			player.getEquipment().clear();
			player.getInventory().clear();
			int[] equip = { -1, 10499, 1712, 1323, 6107, 3842, -1, 1095, -1, 7458, 3105, -1, 2550, -1 };

			for (int i = 0; i < equip.length; i++) {
				player.getEquipment().set(i, equip[i] == -1 ? null : new Item(equip[i]));
			}

			player.getInventory().add(new Item(ItemIdentifiers.COINS, 2_500_000));
			player.getInventory().add(new Item(2437, 50));
			player.getInventory().add(new Item(2441, 50));
			player.getInventory().add(new Item(386, 300));
			player.getInventory().add(new Item(558, 500));
			player.getInventory().add(new Item(556, 1_000));
			player.getInventory().add(new Item(555, 500));
			player.getInventory().add(new Item(557, 500));
			player.getInventory().add(new Item(554, 500));
			player.getInventory().add(new Item(560, 250));
			player.getInventory().add(new Item(562, 250));
			player.getInventory().add(new Item(882, 500));
			player.getInventory().add(new Item(841, 1));
			player.getInventory().add(new Item(861, 1));
			player.getInventory().add(new Item(1333, 1));
			player.getInventory().add(new Item(4587, 1));
			player.getInventory().add(new Item(1215, 1));
			player.getInventory().add(new Item(6108, 1));
			player.getInventory().add(new Item(1067, 1));
			player.getInventory().add(new Item(1129, 1));
			player.getInventory().add(new Item(1540, 1));
			player.getInventory().add(new Item(4155, 1));
			player.getItems().updateCombatStylesTab(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			GameService.getWorld().update(player);
			player.canChangeAppearance = true;
			player.getEquipment().setFiringEvents(equipRefresh);
			player.getInventory().setFiringEvents(invRefresh);
			player.getEquipment().fireItemsChanged();
			player.getInventory().fireItemsChanged();
			break;

		case "hunt":
			if (command.length() == "hunt".length()) {
				ActionSender.sendObjectTransformation(player, 1189, 1);
				return;
			}

			String[] toAdd = command.substring("hunt ".length()).split(";");

			for (String name : toAdd) {
				Optional<Player> maybePlayer = GameService.getWorld().getByUsername(name);

				if (maybePlayer.isPresent()) {
					maybePlayer.get().getMinigame().enterLobby(PropHuntLobby.getInstance(), new Object[] { Long.toString(System.nanoTime()) });
				} else {
					player.sendMessage(name + " was unable to be added to the minigame.");
				}
			}
			break;

		case "cluescroll":
			/** Change the difficulty here */
			Difficulty difficulty = Difficulty.MEDIUM;
			/*******************************/

			player.dialogue().start(new SimpleChoice(player, "Replace bank with 1,000 (" + difficulty.name().toLowerCase() + ") trials", p -> {
				Arrays.fill(player.getBankField().getTabAmounts(), 0);
				Bank.viewTab(player, 0);
				player.getBank().clear();

				for (int a = 0; a < 1_000; a++) {
					Container items = new Container(Type.STANDARD, 9);
					int length = 3 + NumberUtil.random(4);

					for (int i = 0; i < length; i++) {
						Item reward;

						do {
							reward = difficulty.getRewards().getReward();
						} while (items.contains(reward));

						items.add(reward);
						Bank.addToBank(player, reward);
					}
				}

				Arrays.sort(player.getBank().toArray(), (first, second) -> {
					if (first == null || second == null) {
						return 0;
					}

					return second.getCount() - first.getCount();
				});
				Bank.open(player);
			} , "Rewards interface", p -> {
				player.getSession().send(new SendRemoveInterfacePacket());
				player.getInterfaceState().openInterface(6960);

				Container items = new Container(Type.STANDARD, 9);
				int length = 3 + NumberUtil.random(4);

				for (int i = 0; i < length; i++) {
					Item reward;

					do {
						reward = difficulty.getRewards().getReward();
					} while (items.contains(reward));

					items.add(reward);
				}

				ActionSender.sendInterfaceItems(player, 6963, items.toArray());
			}));
			break;

		case "getgvar":
			String varname = command.substring("getgvar".length() + 1);
			Object value = GameService.getConfiguration().get(varname);
			player.sendMessage("console:Global variable '" + varname + "' is: " + String.valueOf(value));
			break;

		case "setgvar":
			String[] split = command.split(" ");

			if (split.length < 3) {
				player.sendMessage("Insufficient arguments.");
				return;
			}

			String varName = split[1].toLowerCase();
			int newValue;

			try {
				newValue = Integer.parseInt(split[2]);

				if (newValue < 0) {
					player.sendMessage("Global variable value cannot be negative: " + newValue);
					return;
				}
			} catch (NumberFormatException ex) {
				player.sendMessage("Invalid number format: " + split[2]);
				return;
			}

			int oldValue = GameService.getConfiguration().setInteger(varName, newValue);
			player.sendMessage("console:Global variable '" + varName + "' changed from " + oldValue + " to: " + newValue);
			break;

		case "goup":
			player.teleport(player.getLocation().transform(0, 0, 1));
			break;

		case "godown":
			player.teleport(player.getLocation().transform(0, 0, -1));
			break;

		case "give":
			try {
				if (args.length < 2) {
					player.sendMessage("Insufficient arguments.");
					return;
				}

				int amount;

				if (args.length > 2) {
					if ("max".equals(args[2])) {
						amount = Integer.MAX_VALUE;
					} else {
						args[2] = args[2].toLowerCase();
						args[2] = args[2].replace("k", "000");
						args[2] = args[2].replace("m", "000000");
						args[2] = args[2].replace("b", "000000000");
						amount = Integer.parseInt(args[2]);
					}
				} else {
					amount = 1;
				}

				String name = args[1].replace("_", " ").toLowerCase();
				List<Integer> results = new ArrayList<>();

				for (int i = 0; i < ItemDefinition.toArray().length; i++) {
					ItemDefinition definition = ItemDefinition.forId(i);
					String itemName = definition.getName().toLowerCase().replaceAll("[()]", "");

					if (itemName.contains(name) && !definition.isNoted() && !definition.isLended()) {
						results.add(i);
					}
				}

				if (results.isEmpty()) {
					player.sendMessage("No matches found for: " + name);
				} else if (results.size() == 1) {
					player.getInventory().add(new Item(results.get(0), amount));
				} else {
					int size = Math.min(350, results.size());
					int current;

					boolean bankFire = player.getBank().isFiringEvents();
					player.getBank().setFiringEvents(false);
					player.getBank().clear();
					Arrays.fill(player.getBankField().getTabAmounts(), 0);

					for (current = 0; current < size; current++) {
						int id = results.get(current);
						Bank.addToBank(player, new Item(id, amount));
					}

					player.getBank().setFiringEvents(bankFire);

					Bank.viewTab(player, 0);
					Bank.open(player);
				}
			} catch (NumberFormatException ex) {
				player.sendMessage("Invalid arguments! Usage: ::give item_name [amount]");
			}
			break;

		case "giveitem":
			try {
				if (args.length < 3) {
					player.sendMessage("Invalid arguments! Usage: ::give player_name itemid amount");
					return;
				}
				
				String targ = args[1].replace("_", " ");
				int id = Integer.parseInt(args[2]);
				int amount;

				if (id >= 22322) {
					player.sendMessage("Invalid item.");
					return;
				}

				Optional<Player> maybePlayer = GameService.getWorld().getByUsername(targ);

				if (!maybePlayer.isPresent()) {
					player.sendMessage("That player could not be found.");
					return;
				}
				
				Player target = maybePlayer.get();

				if (args.length > 2) {
					if ("max".equals(args[3])) {
						amount = Integer.MAX_VALUE;
					} else {
						args[2] = args[2].toLowerCase();
						args[2] = args[2].replace("k", "000");
						args[2] = args[2].replace("m", "000000");
						args[2] = args[2].replace("b", "000000000");
						amount = Integer.parseInt(args[3]);
					}
				} else {
					amount = 1;
				}
				
				String name = ItemDefinition.getName(id);
				target.getInventory().add(new Item(id, amount));
				target.sendMessage(player.getName() + " gave you the item " + name + " X " + amount + ".");
				player.sendMessage("You gave " + target.getName() + " the item " + name + " X " + amount + ".");
			} catch (NumberFormatException ex) {
				player.sendMessage("Invalid arguments! Usage: ::giveitem player_name itemid amount");
			}
			break;

		case "testrandom":
			new MysteryBoxRandomEvent().init(player);
			break;

		case "saveall":
			for (Player p : GameService.getWorld().getPlayers()) {
				if (p == null) {
					continue;
				}

				PlayerSaver.save(p);
			}
			break;

		case "tele":
			try {
				if (args.length > 3) {
					player.setPosition(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				} else if (args.length == 3) {
					player.setPosition(Integer.parseInt(args[1]), Integer.parseInt(args[2]), player.getZ());
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;

		case "barragerunes":
		case "barrage":
			player.getInventory().add(new Item(555, 600));
			player.getInventory().add(new Item(560, 400));
			player.getInventory().add(new Item(565, 200));
			break;

		case "vengrunes":
		case "veng":
			player.getInventory().add(new Item(9075, 400));
			player.getInventory().add(new Item(560, 200));
			player.getInventory().add(new Item(557, 1000));
			break;

		case "setlevel":
			try {
				int skillId = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				int exp = player.getSkill().getXPForLevel(level);

				if (level > 99 && (exp > 200_000_000 || exp < 0)) {
					exp = 200_000_000;
				}

				player.getSkill().setSkill(skillId, level, exp);
				player.sendMessage(String.format("%s set to level %d @ %s XP.", Skill.SKILL_NAME[skillId], level, NumberUtil.format(exp)));
				player.sendMessage("... cheat successful!");
			} catch (Exception ex) {
				player.sendMessage("Error: " + ex.toString());
			}
			break;

		case "settask":
			player.sendMessage("Current Slayer: " + player.getSkill().getLevelForExperience(Skill.SLAYER));
			player.getSlayer().assignTask(Master.MAZCHNA.getId());
			player.sendMessage("Task: " + player.getSlayer().getAssignment().getMobId() + ", Req: " + player.getSlayer().getAssignment().getLevelRequired());
			player.getSlayer().assignTask(Master.CHAELDAR.getId());
			player.sendMessage("Task: " + player.getSlayer().getAssignment().getMobId() + ", Req: " + player.getSlayer().getAssignment().getLevelRequired());
			player.getSlayer().assignTask(Master.DURADEL.getId());
			player.sendMessage("Task: " + player.getSlayer().getAssignment().getMobId() + ", Req: " + player.getSlayer().getAssignment().getLevelRequired());
			player.getSlayer().assignTask(Master.VANNAKA.getId());
			player.sendMessage("Task: " + player.getSlayer().getAssignment().getMobId() + ", Req: " + player.getSlayer().getAssignment().getLevelRequired());
			player.getSlayer().setKillsLeft(0);
			player.getSlayer().setAssignment(null);
			break;

		case "addexp":
			try {
				NumberFormat formatter = NumberFormat.getInstance(Locale.US);
				int skillId = Integer.parseInt(args[1]);
				int amount = Integer.parseInt(args[2]);
				int amountAdded = player.getSkill().addExperience(skillId, amount);

				if (amountAdded == 0) {
					player.sendMessage("Unable to add experience.");
				} else {
					player.sendMessage("Added " + Skill.SKILL_NAME[skillId].toLowerCase() + " XP: " + formatter.format(amountAdded));
				}
			} catch (Exception ex) {
				player.sendMessage("Error: " + ex.toString());
			}
			break;

		case "getitem":
			if (command.length() <= 8) {
				player.sendMessage("Error: no criteria specified.");
				return;
			}

			String name = command.split(" ", 2)[1].toLowerCase();
			int results = 0;
			player.sendMessage("Searching: " + name);

			for (int i = 0; i < ItemDefinition.toArray().length; i++) {
				ItemDefinition definition = ItemDefinition.forId(i);

				if (definition == null) {
					continue;
				}

				if (definition.getName().toLowerCase().contains(name)) {
					player.sendMessage("@red@" + definition.getName() + " - " + i);
					results++;
				}

				if (results >= 100) {
					player.sendMessage("Found at least 100 matches. Please be more specific.");
					break;
				}
			}
			break;

		case "getnpc":
			if (command.length() <= 7) {
				player.sendMessage("Error: no criteria specified.");
				return;
			}

			final String npc_name = command.split(" ", 2)[1].toLowerCase();
			int npc_results = 0;
			player.sendMessage("Searching: " + npc_name);

			for (int i = 0; i < MobDefinition.getHighestID(); i++) {
				final MobDefinition definition = MobDefinition.get(i);

				if (definition.getName().toLowerCase().contains(npc_name)) {
					player.sendMessage("@red@" + definition.getName() + " - " + i);
					npc_results++;
				}

				if (npc_results >= 100) {
					player.sendMessage("Found at least 100 matches. Please be more specific.");
					break;
				}
			}

			player.sendMessage(npc_results + " results found...");
			break;

		case "bank":
			player.getBankField().setBanking(true);
			Bank.open(player);
			break;

		case "object":
			player.getSession().send(new SendObjectPacket(Integer.parseInt(args[1]), player.getX(), player.getY(), player.getZ(), 0, 10));
			break;

		case "mypos":
			player.sendMessage("console:Your current position is " + player.getLocation().toString() + ".");
			break;

		case "gfx":
			player.playGraphics(new Graphic(Integer.parseInt(args[1])));
			break;

		case "anim":
			player.playAnimation(new Animation(Integer.decode(args[1])));
			break;

		case "update":
			int seconds = Integer.parseInt(args[1]);

			player.dialogue().start(new SimpleChoice(player, "Initiate Game Update", p -> {
				GameService.getWorld().startUpdate(new SystemUpdateTask(seconds));
				p.sendMessage("Shutting down in " + seconds + " seconds.");
			} , "No! Don't do that.", p -> p.sendMessage("Cancelled.")));
			break;

		case "cancelupdate":
			player.sendMessage("Attempting to cancel current system update...");
			GameService.getWorld().cancelUpdate();
			break;

		case "copy":
			Optional<Player> optionalPlayer = GameService.getWorld().getByUsername(command.substring(5));

			if (optionalPlayer.isPresent()) {
				// TODO NOT SURE IF THIS IS THE CORRECT IMPLEMENTATION
				Player target = optionalPlayer.get();
				System.arraycopy(target.getEquipment().toArray(), 0, player.getEquipment().toArray(), 0, player.getEquipment().toArray().length);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.sendMessage("You have copied the appearance of " + target.getName() + ".");
			} else {
				player.sendMessage("That player could not be found.");
			}
			player.getEquipment().setFiringEvents(true);
			break;

		case "putbank":
			if (args.length < 2) {
				player.sendMessage("Asparagus!");
				return;
			}

			try {
				int id = Integer.parseInt(args[1]);
				int amount = 1;

				if (args.length > 2) {
					if ("max".equals(args[2])) {
						amount = Integer.MAX_VALUE;
					} else {
						args[2] = args[2].toLowerCase();
						args[2] = args[2].replace("k", "000");
						args[2] = args[2].replace("m", "000000");
						args[2] = args[2].replace("b", "000000000");
						amount = Integer.parseInt(args[2]);
					}
				}

				if (Bank.addToBank(player, new Item(id, amount))) {
					player.sendMessage("Put " + NumberFormat.getInstance(Locale.US).format(amount) + " x " + ItemDefinition.forId(id).getName() + " in bank.");
				} else {
					player.sendMessage("Unable to of that item.");
				}
			} catch (NumberFormatException ex) {
				// Not using 'args' because it might be modified.
				player.sendMessage("An exception was thrown. Arguments: " + Arrays.toString(command.split(" ")));
			}
			break;

		case "item":
		case "putinv":
			if (args.length < 2) {
				player.sendMessage("Asparagus!");
				return;
			}

			try {
				int id = Integer.parseInt(args[1]);
				int amount = 1;

				if (args.length > 2) {
					if ("max".equals(args[2])) {
						amount = Integer.MAX_VALUE;
					} else {
						args[2] = args[2].toLowerCase();
						args[2] = args[2].replace("k", "000");
						args[2] = args[2].replace("m", "000000");
						args[2] = args[2].replace("b", "000000000");
						amount = Integer.parseInt(args[2]);
					}
				}

				if (player.getInventory().add(new Item(id, amount))) {
					player.sendMessage("Put " + NumberFormat.getInstance(Locale.US).format(amount) + " x " + ItemDefinition.forId(id).getName() + " in inventory.");
				} else {
					player.sendMessage("Unable to of that item.");
				}
			} catch (NumberFormatException ex) {
				player.sendMessage("An exception was thrown. Arguments: " + Arrays.toString(command.split(" ")));
				// Not using 'args' because it might be modified.
			}
			break;

		case "npc":
			try {
				int id = Integer.parseInt(command.substring(4));

				if (id < 0 || id >= MobDefinition.getHighestID()) {
					player.sendMessage("Bad mob data value.");
				} else {
					MobDefinition definition = MobDefinition.get(id);
					MobManager.spawnMob(player, id, player.getX(), player.getY(), player.getZ(), 0, 120, 7, 70, 70, false, false);
					player.sendMessage("You spawn [" + definition.getName() + "] at position [" + player.getX() + ", " + player.getY() + ", " + player.getZ() + "]");
				}
			} catch (Exception ex) {
				player.sendMessage("Error!");
			}
			break;

		case "runes":
			for (int i = 554; i < 567; i++) {
				player.getInventory().add(new Item(i, 100_000));
			}

			player.getInventory().add(new Item(9075, 100_000));
			break;

		case "interface":
			try {
				player.getInterfaceState().openInterface(Integer.parseInt(args[1]));
			} catch (Exception ex) {
				player.sendMessage("Usage: ::interface ####");
			}
			break;

		case "sm":
			GameService.getWorld().broadcast(command.substring(3));
			break;
			
		case "promote":
			if (args.length < 3) {
				player.sendMessage("Invalid format; [::promote user_name privilege, example; ::promote arithium administrator");
				return;
			}
			
			String username = args[1].replaceAll("_", " ");
			RegularPrivilege rights = null;
			try {
				rights = RegularPrivilege.valueOf(args[2].toUpperCase());
			} catch (Exception e) {
				player.sendMessage("Invalid privilege: " + args[2]);
				return;
			}
			Optional<Player> t = GameService.getWorld().getByUsernameHash(StringUtil.usernameToHash(username));
			
			if (t.isPresent()) {
				Player p = t.get();
				p.setPrivilege(rights);
				player.sendMessage("Successfully set " + username + "'s rank too " + rights + ".");
				p.sendMessage("Your rights have been set by " + player.getName() + " to " + rights + ", please logout.");
			} else {
				player.sendMessage("The player " + username + " is offline.");
			}
			break;

		case "demote":
			if (command.length() < 7 || args.length < 2) {
				return;
			}

			String target = command.substring(7);
			Optional<Player> tp = GameService.getWorld().getByUsername(target);

			if (!tp.isPresent()) {
				player.sendMessage("Wrong username or the player is currently offline!");
				return;
			}

			tp.get().setPrivilege(RegularPrivilege.NONE);
			player.sendMessage("You demoted " + tp.get().getName() + ".");
			tp.get().sendMessage("You have been demoted by " + player.getName() + ". (You deserved it!).");
			PlayerSaver.save(tp.get());
			break;

		case "givess":
			if (args.length < 2) {
				player.sendMessage("Invalid arguments.");
				return;
			}

			Optional<Player> op = GameService.getWorld().getByUsername(args[1].replace("_", " "));

			if (!op.isPresent()) {
				player.sendMessage("Wrong username or the player is currently offline!");
				return;
			}

			Player player2 = op.get();
			player2.setPrivilege(RegularPrivilege.HELPER);
			player2.sendMessage("You have been promoted to server support!");
			break;

		case "givedon":
			try {
				if (args.length != 3) {
					player.sendMessage("Wrong arguments! Usage: givedon player_name amount type");
					return;
				}

				Optional<Player> optional = GameService.getWorld().getByUsername(args[1].replace("_", " "));

				if (!optional.isPresent()) {
					player.sendMessage("Wrong username or the player is currently offline!");
					return;
				}

				Player targ = optional.get();
				int amount = Integer.parseInt(args[2]);
				String type = args[3];
				DonatorPrivilege privilege = DonatorPrivilege.valueOf(type.toUpperCase());
				targ.donatorPoints += amount;
				targ.setDonatorPrivilege(privilege);

				targ.sendMessage("You have been promoted to " + type + " donator by " + player.getName() + ".");
				targ.sendMessage("You received " + amount + " donation points from " + player.getName() + ".");
				player.sendMessage("You gave " + amount + " donation points to " + targ.getName() + "( " + targ.donatorPoints + " ).");
				player.sendMessage("You have promoted " + targ.getName() + " to " + type + " donator.");
				PlayerSaver.save(targ);
			} catch (Exception e) {
				player.sendMessage("Wrong arguments! Usage: givedon player_name amount type");
			}

			break;
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.ADMINISTRATOR;
	}

	@Override
	public boolean testPermissions(Player player) {
		return player.isSuperAdministrator();
	}

}
