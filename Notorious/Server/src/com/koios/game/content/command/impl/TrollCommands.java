package com.koios.game.content.command.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.content.titles.Title;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.task.Task;

public class TrollCommands implements Command {

	@Override
	public void execute(Player player, String command) {
		String[] args = command.split(" ");

		switch (args[0]) {
		case "tsetyelltag":
			if (args.length < 3) {
				return;
			}

			String name = args[1].replace("_", " ");
			String tag = args[2].replace("_", " ");
			Optional<Player> t = GameService.getWorld().getByUsername(name);

			if (!t.isPresent()) {
				player.sendMessage("No online player with '" + name + "' as name online.");
				player.sendMessage("(Usage example: tsetyelltag player_name tag)");
				return;
			}

			t.get().getYT().setTag(tag.replace(" ", ""));
			player.sendMessage("Succefully changed " + t.get().getName() + "'s yell tag to '" + tag + "'.");
			break;

		case "allsay":
			if (command.length() < 7) {
				return;
			}

			for (Player p : GameService.getWorld().getPlayers()) {
				if (p == null) {
					continue;
				}

				p.speak(command.substring(7));
			}
			break;

		case "tsetyellcolor":
			if (args.length < 3) {
				return;
			}

			String name2 = args[1].replace("_", " ");
			String color = args[2];
			Optional<Player> t2 = GameService.getWorld().getByUsername(name2);

			if (!t2.isPresent()) {
				player.sendMessage("No online player with '" + name2 + "' as name online.");
				player.sendMessage("(Usage example: tsetyellcolor player_name color)");
				return;
			}

			t2.get().getYT().setColour(color.replace(" ", ""));
			player.sendMessage("Succefully changed " + t2.get().getName() + "'s yell color to '" + color + "'.");
			break;

		case "tsettitle":
			if (args.length < 4) {
				return;
			}

			String name3 = args[1].replace("_", " ");
			String title = args[2].replace("_", " ");
			String colour = args[3];
			boolean prefix = true;

			if (args.length == 5) {
				prefix = Boolean.parseBoolean(args[4]);
			}

			Optional<Player> target = GameService.getWorld().getByUsername(name3);

			if (!target.isPresent()) {
				player.sendMessage("No online player with '" + name3 + "' as name online.");
				player.sendMessage("(Usage example: tsettitle player_name new_title color prefix(true/false)");
				return;
			}

			int rgb;

			try {
				rgb = Integer.decode("#" + colour);
			} catch (NumberFormatException ex) {
				player.sendMessage("Invalid RGB hexadecimal.");
				return;
			}

			target.get().setTitle(Title.of(title, rgb, prefix));
			player.sendMessage("Succefully changed " + target.get().getName() + "'s title to '" + title + "' and color to '" + colour + "'.");
			target.get().getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			break;

		case "tresettitle":
			if (args.length < 2) {
				return;
			}

			String name4 = args[1].replace("_", " ");
			Optional<Player> target2 = GameService.getWorld().getByUsername(name4);

			if (!target2.isPresent()) {
				player.sendMessage("No online player with '" + name4 + "' as name online.");
				player.sendMessage("(Usage example: tresettitle player_name");
				return;
			}

			target2.get().setTitle(null);
			target2.get().getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.sendMessage("Succefully reseted " + target2.get().getName() + "'s title.");
			break;

		case "sing":
			String targetName = command.length() > 5 ? command.substring(5) : "";
			Path targetPath = Paths.get("data/definitions/lyrics", targetName);

			try {
				List<String> lyrics = Files.readAllLines(targetPath, Charset.forName("ISO-8859-1"));
				execute(player, lyrics);
			} catch (IOException ex) {
				player.sendMessage("Unable to locate/read lyrics file: " + targetPath.toString());
			}
			break;

		case "tsing":
			if (args.length < 3) {
				return;
			}

			String singer = args[1].replace("_", " ");
			Optional<Player> targ = GameService.getWorld().getByUsername(singer);

			if (!targ.isPresent()) {
				player.sendMessage("No online player with '" + singer + "' as name online.");
				player.sendMessage("(Usage example: tsetyelltag player_name tag)");
				return;
			}

			String target_name = args[2];
			Path target_path = Paths.get("data/definitions/lyrics", target_name);

			try {
				List<String> lyrics = Files.readAllLines(target_path, Charset.forName("ISO-8859-1"));
				execute(targ.get(), lyrics);
			} catch (IOException ex) {
				player.sendMessage("Unable to locate/read lyrics file: " + target_path.toString());
			}
			break;
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.DEVELOPER;
	}

	@Override
	public boolean testPermissions(Player player) {
		return player.isSuperAdministrator();
	}

	private void execute(Player player, List<String> lyrics) {
		player.setSinging(true);

		GameService.getTaskScheduler().schedule(new Task(3) {

			private int next = 0;

			@Override
			protected void execute() {
				if (player.isDisconnected() || next >= lyrics.size()) {
					player.setSinging(false);
					stop();
				} else {
					String line = lyrics.get(next++);

					if (!player.isSinging()) {
						player.setSinging(true);
					}

					if (!line.isEmpty()) {
						player.speak(line);
					}
				}
			}

		});
	}

}
