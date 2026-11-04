package com.koios.game.content.command.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.mysql.SanctionDatabaseConnector;
import com.koios.task.Task;

public class SanctionAdminCommands implements Command {

	@Override
	public void execute(Player player, String input) {
		
		if (!input.startsWith("sa_")) {
			return;
		}
		
		SanctionDatabaseConnector database = SanctionDatabaseConnector.instance();
		String[] args = input.split(" ", 2);
		String command = args[0].toLowerCase();
		String context = input.substring(args[0].length() + 1, input.length());
		args = context.split(" ", 2);

		// Database not available - debug mode.
		if (database == null) {
			player.sendMessage("This command is not available.");
			return;
		}

		// Test context for possible value for sanction id.
		if (args.length < 1) {
			player.sendMessage("Missing command argument: <col=FF0000>sanction id</col>");
			return;
		}

		int sanctionId;

		// try this - user input could be malformed when retards try to use it
		try {
			sanctionId = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			player.sendMessage("That is not an integer value.");
			return;
		}

		switch (command) {
		case "sa_edit_reason":
			if (args.length < 2) {
				player.sendMessage("Missing command argument: <col=FF0000>new-reason</col>");
				return;
			}

			submit(player, database.editReason(sanctionId, args[1]));
			break;

		case "sa_edit_expiry":
			break;

		case "sa_force_close":
			submit(player, database.forceClose(sanctionId));
			break;

		default:
			player.sendMessage("Brassica Prime!");
			player.sendMessage("... this is an unexpected error.");
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.ADMINISTRATOR;
	}

	/* Internal procedure to await a response to be fetched from the database. */
	private void submit(Player player, Future<String> future) {
		GameService.getTaskScheduler().schedule(new Task(1) {

			// assert failure after 10 ticks without a prepared response.
			private int timeout = 10;

			@Override
			protected void execute() {
				if (player.isDisconnected()) {
					future.cancel(true);
					stop();
				} else if (future.isDone()) {
					try {
						String response = future.get();
						player.sendMessage(response);
					} catch (ExecutionException | InterruptedException ex) {
						player.sendMessage("An error occured whilst attempting to communicate with the database:");
						player.sendMessage("<col=FF0000>" + ex.getMessage());
						System.err.println(player.getName() + " caused an error to occur:");
						ex.printStackTrace();
					}

					stop();
				} else if (--timeout == 0) {
					future.cancel(true);
					stop();
				}
			}

		});
	}

	@Override
	public boolean testPermissions(Player player) {
		return player.isSuperAdministrator();
	}

}
