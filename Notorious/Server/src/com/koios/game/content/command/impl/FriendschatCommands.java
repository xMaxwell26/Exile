package com.koios.game.content.command.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.content.friendschat.Friendschat;
import com.koios.game.content.friendschat.FriendschatProvider;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.task.Task;
import com.koios.util.StringUtil;

public class FriendschatCommands implements Command {

	private static final Logger logger = LoggerFactory.getLogger(FriendschatCommands.class);

	@Override
	public void execute(Player player, String command) {
		
		if (!command.startsWith("fc_")) {
			return;
		}
		
		String[] args = command.split(" ", 2);
		args[0] = args[0].toLowerCase();

		switch (args[0]) {
		case "fc_load":
			if (args.length > 1) {
				logger.info("Attempting to load friendschat: '{}'", StringUtil.formatUsername(args[1]));
				player.sendMessage("Attempting to add channel...");
				Future<Friendschat> future = GameService.getFriendschatProvider().loadAndGetFuture(args[1]);

				GameService.getTaskScheduler().schedule(new Task(1) {

					// After 10 cycles we assume the request failed.
					private byte timeout = 10;

					@Override
					protected void execute() {
						if (player.isDisconnected()) {
							future.cancel(true);
							stop();
						} else if (future.isDone()) {
							try {
								Friendschat friendschat = future.get();
								friendschat.add(player);
								logger.info("Friendschat aquired after waiting {} cycle(s).", 10 - timeout);
								logger.info("Reference aquired: {}: {}", Objects.hashCode(friendschat), friendschat);
							} catch (InterruptedException | ExecutionException ex) {
								ex.printStackTrace();
							}

							stop();
						} else if (--timeout == 0) {
							future.cancel(true);
							logger.info("Unable to aquire friends chat in time: '{}'", StringUtil.formatUsername(args[1]));
							stop();
						}
					}

				});
			} else {
				player.sendMessage("Insufficient arguments - no target specified.");
			}
			break;

		case "fc_get":
			if (args.length > 1) {
				logger.info("Attempting to get friendschat: '{}'", StringUtil.formatUsername(args[1]));
				Friendschat friendschat = GameService.getFriendschatProvider().get(args[1]);
				logger.info("Aquired: {}", friendschat);
			} else {
				player.sendMessage("Insufficient arguments - no target specified.");
			}
			break;

		case "fc_unload":
			if (args.length > 1) {
				Friendschat friendschat = GameService.getFriendschatProvider().get(args[1]);

				if (friendschat == null) {
					logger.info("Friendschat already not loaded: '{}'", StringUtil.formatUsername(args[1]));
				} else if (GameService.getFriendschatProvider().unload(friendschat)) {
					logger.info("Unloaded friendschat: '{}'", StringUtil.formatUsername(args[1]));
				} else {
					logger.info("Unable to unload friendschat: '{}'", StringUtil.formatUsername(args[1]));
				}
			} else {
				player.sendMessage("Insufficient arguments - no target specified.");
			}
			break;

		case "fc_size":
			player.sendMessage("Cached friends chat channels: " + GameService.getFriendschatProvider().getCacheSize());
			break;

		case "fc_status":
			checkStatus(player);
		}
	}

	private void checkStatus(Player player) {
		FriendschatProvider provider = GameService.getFriendschatProvider();
		Collection<Friendschat> all = provider.all();

		if (all.isEmpty()) {
			player.sendMessage("There are no friends chat channels loaded.");
			return;
		}

		for (Friendschat friendschat : all) {
			player.sendMessage(String.format("<col=800000>%s</col> with <col=800000>%d</col> active users.", friendschat.getOwnerName(), friendschat.size()));
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

}
