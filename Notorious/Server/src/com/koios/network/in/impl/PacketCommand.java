package com.koios.network.in.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.koios.GameService;
import com.koios.game.content.command.CommandManager;
import com.koios.game.content.friendschat.Friendschat;
import com.koios.game.content.obj.BoxSets;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.DonatorPrivilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.game.model.player.io.StoreUtils;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendSongPacket;
import com.koios.network.out.impl.SendSoundPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.StringUtil;

public class PacketCommand implements IncomingPacketDecoder {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	@Override
	public void handle(Player player, GamePacket packet) {

		String input = packet.readString();
        if(com.koios.game.arpg.QaFixtures.handle(player,input))return;
        if(input.equalsIgnoreCase("arpgmaps")&&!GameService.isOnlineMode()){for(int layout=0;layout<3;layout++)com.koios.game.arpg.MapRuns.grant(player,com.koios.game.arpg.MapRuns.map(1,0,layout));player.sendMessage("Added one map of each layout.");return;}
        if(input.equalsIgnoreCase("arpgbases")&&!GameService.isOnlineMode()){for(com.koios.game.arpg.EquipmentBases.Base base:com.koios.game.arpg.EquipmentBases.all())com.koios.game.arpg.MapRuns.grant(player,com.koios.game.arpg.EquipmentBases.create(base,1));player.sendMessage("Added "+com.koios.game.arpg.EquipmentBases.all().length+" equipment bases. Check each item's level requirement.");return;}
        if(input.equalsIgnoreCase("tutorial")){try{com.koios.game.arpg.Tutorial.begin(player);}catch(IllegalArgumentException ex){player.sendMessage(ex.getMessage());}return;}
        if(com.koios.game.arpg.PrototypePanel.handle(player,input))return;
        if(com.koios.game.arpg.GroundLootMetadata.handle(player,input))return;
        if(com.koios.game.arpg.HubServices.handle(player,input))return;
        if(com.koios.game.arpg.ArpgSkills.handle(player,input))return;

		if (input.startsWith("/") && input.length() > 1) {
			if (player.friendschatName == null) {
				player.sendMessage("You are not in a friends chat channel!");
			} else {
				Friendschat friendschat = GameService.getFriendschatProvider().get(player.friendschatName);

				if (friendschat == null) {
					player.sendMessage("Error: the requested friends chat is not available.");
				} else {
					String message = input.substring(1);
					friendschat.talk(player, message);
				}
			}

			return;
		}
		
		int indexOfSpace = input.indexOf(" ");
		String command = (indexOfSpace < 0 ? input : input.substring(0, indexOfSpace)).toLowerCase();
		String arguments = indexOfSpace != -1 && input.length() > indexOfSpace + 1 ? input.substring(indexOfSpace + 1) : "";
		
		if (GameService.isOnlineMode()) {
			if (!isProtectedAgainstLogs(command)) {
				appendCommandToLogs(player, command, arguments);
			}
		}
		
		if (command.equalsIgnoreCase("open")) {
			for (Item item : player.getInventory().toArray()) {
				if (item != null) {
					if (player.getInventory().freeSlots() < 4) {
						player.sendMessage("You do not have enough room to open this.");
						break;
					}
					if (BoxSets.isSet(item.getId())) {
						BoxSets.handleSet(player, item.getId());
					}
				}
			}
		}

		if (input.startsWith("[CN]")) {
			if (input.length() > 4) {
				String topic = input.substring(5);
				topic = topic.replaceAll("[^A-Za-z0-9 ]", "");

				if (topic.length() > 12) {
					topic = topic.substring(0, 12);
				}

				topic = StringUtil.capitalize(topic);

				if (player.getName().equalsIgnoreCase(player.friendschatName)) {
					Friendschat friendschat = GameService.getFriendschatProvider().get(player.getName());

					if (friendschat != null) {
						friendschat.setTopic(topic);
						player.getSession().send(new SendStringPacket(friendschat.getTopic(), 47814));
					} else {
						player.sendMessage("You must be in your friends chat channel to access the Clan Setup interface!");
					}
				} else {
					player.sendMessage("You must be in your friends chat channel to access the Clan Setup interface!");
				}
			}

			return;
		}
		
		if (player.isSuperAdministrator()) {
			if (command.startsWith("reload")) {
				if (arguments.equalsIgnoreCase("itemdef")) {
					ItemDefinition.load();
					player.sendMessage("Successfully reloaded item definitions.");
				}
				return;
			} else if (command.equalsIgnoreCase("config")) {
				String[] args = arguments.split(" ");
				int id = Integer.parseInt(args[0]);
				int state = Integer.parseInt(args[1]);
				player.getSession().send(new SendConfigPacket(id, state));
			} else if (command.equalsIgnoreCase("hurt")) {
				String[] args = arguments.split(" ");
				int damage = Integer.parseInt(args[0]);
				player.hurt(damage, player);
			} else if (command.equalsIgnoreCase("song")) {
				String[] args = arguments.split(" ");
				player.getSession().send(new SendSongPacket(Integer.parseInt(args[0])));
			} else if (command.equalsIgnoreCase("sound")) {
				String[] args = arguments.split(" ");
				player.getSession().send(new SendSoundPacket(Integer.parseInt(args[0]), 10, 1));
			}
		}

		if (CommandManager.consume(player, input)) {
			return;
		} else {
			if (player.isSuperAdministrator())
				player.sendMessage("console:Unknown command: " + command + ".");
		}
	}

	private boolean isProtectedAgainstLogs(String command) {
		return "yell".equals(command) || "changepassword".equals(command);
	}

	private void appendCommandToLogs(Player player, String command, String arguments) {
		try {
			Path file = StoreUtils.getDirectory("logs/commands").resolve(player.getName() + ".txt");
			List<String> nearbyPlayers = GameService.getWorld().listAll(p -> p != player && p.isWithinDistance(player, 16)).stream().map(Player::getName)
					.collect(Collectors.toList());
			RegularPrivilege rights = player.getPrivilege();
			DonatorPrivilege donatorPrivilege = player.getDonatorPrivilege();
			Location location = player.getLocation();

			// Alphabetical sort of nearby player names
			Collections.sort(nearbyPlayers);

			try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out))) {
				// Append timestamp information
				bw.write("Timestamp: ");
				bw.write(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
				bw.newLine();

				// Append host address information
				bw.write("Address: ");
				bw.write(player.getSession().getHostAddress());
				bw.newLine();

				// Append command information
				bw.write("Command: ");
				bw.write(command);

				if (!arguments.isEmpty()) {
					bw.write(" ");
					bw.write(arguments);
				}

				bw.newLine();

				// Append usergroup information
				bw.write("Usergroup: ");
				bw.write(rights.name());
				bw.write(", Donator group: ");
				bw.write(donatorPrivilege.name());
				bw.newLine();

				// Append location information
				bw.write("Location: [x=");
				bw.write(String.valueOf(location.getX()));
				bw.write(", y=");
				bw.write(String.valueOf(location.getY()));
				bw.write(", z=");
				bw.write(String.valueOf(location.getZ()));
				bw.write("]");
				bw.newLine();

				// Append nearby players
				if (!nearbyPlayers.isEmpty()) {
					bw.write("Nearby players count: ");
					bw.write(String.valueOf(nearbyPlayers.size()));
					bw.newLine();

					bw.write("Nearby players list: ");
					bw.write(nearbyPlayers.stream().collect(Collectors.joining(", ")));
					bw.newLine();
				}

				// Append log entry end
				bw.write("--------------------------------------------------------------------------------");
				bw.newLine();
			}
		} catch (IOException ex) {
			System.err.println("Error writings command logs for: " + player.getName() + ":");
			System.err.println("Context: ['" + command + "':'" + arguments + "']");
			ex.printStackTrace();
		}
	}

}
