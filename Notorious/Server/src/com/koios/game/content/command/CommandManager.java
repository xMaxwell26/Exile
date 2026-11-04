package com.koios.game.content.command;

import java.util.HashSet;
import java.util.Set;

import com.koios.game.content.command.impl.ChangePasswordCommand;
import com.koios.game.content.command.impl.FriendschatCommands;
import com.koios.game.content.command.impl.HelperCommands;
import com.koios.game.content.command.impl.ModeratorCommands;
import com.koios.game.content.command.impl.PlayerCommands;
import com.koios.game.content.command.impl.SanctionAdminCommands;
import com.koios.game.content.command.impl.SanctionCommand;
import com.koios.game.content.command.impl.SuperAdminCommands;
import com.koios.game.content.command.impl.TrollCommands;
import com.koios.game.content.command.impl.YellCommand;
import com.koios.game.content.command.impl.YellTagCommands;
import com.koios.game.model.player.Player;

public class CommandManager {

	/**
	 * Processes the command
	 */
	public static boolean consume(Player player, String command) {
		String name;

		if (command.indexOf(' ') > -1) {
			name = command.substring(0, command.indexOf(' '));
		} else {
			name = command;
		}

		name = name.toLowerCase();

		

		for (Command c : commands) {
			if (c.testPermissions(player) && !player.getPrivilege().inferiorTo(c.getMinimumPrivilege())) {
				try {
					c.execute(player, command);
				} catch (Exception ex) {
					player.sendMessage(String.format("Error: %s", ex.getMessage()));
					ex.printStackTrace();
				}
			}
		}

		return true;
	}

	/**
	 * Submits a new command into the list
	 */
	private static void submit(Command command) {
		commands.add(command);
	}

	/**
	 * A Map containing all of the commands
	 */
	private static final Set<Command> commands = new HashSet<>();

	/**
	 * Loads all of the commands
	 */
	static {

		submit(new PlayerCommands());
		submit(new HelperCommands());
		submit(new ModeratorCommands());
		submit(new SanctionAdminCommands());
		submit(new SuperAdminCommands());
		submit(new FriendschatCommands());
		submit(new TrollCommands());
		submit(new YellCommand());
		submit(new ChangePasswordCommand());
		submit(new SanctionCommand());
		submit(new YellTagCommands());
	}

}
