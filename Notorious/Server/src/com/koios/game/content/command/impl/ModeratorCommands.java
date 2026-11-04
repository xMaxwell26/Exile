package com.koios.game.content.command.impl;

import java.util.Arrays;
import java.util.Optional;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.out.impl.SendInventoryOverlayPacket;
import com.koios.util.StringUtil;

public class ModeratorCommands implements Command {

	@Override
	public void execute(Player player, String command) {
		String[] args = command.split(" ");

		switch (args[0]) {
		case "checkbank":
			if (args.length <= 1) {
				player.sendMessage("You must type in a username to check their bank.");
				return;
			}

			String targetUsername = command.substring("checkbank ".length());
			Optional<Player> optionalPlayer = GameService.getWorld().getByUsername(targetUsername);

			if (optionalPlayer.isPresent()) {
				Player target = optionalPlayer.get();
				player.getSession().send(new SendInventoryOverlayPacket(5292, Bank.PLAYER_INVENTORY_INTERFACE - 1));

				for (int tab = 0; tab < 9; tab++) {
					int from = target.getBankField().getOffset(tab);
					int to = from + target.getBankField().getTabAmounts()[tab];
					Item[] items = Arrays.copyOf(Arrays.copyOfRange(target.getBank().toArray(), from, to), Bank.SIZE);
					ActionSender.sendInterfaceItems(player, Bank.BANK_INVENTORY_INTERFACE + tab, items);
				}

				player.sendMessage("Checking " + StringUtil.formatUsername(targetUsername) + "'s bank.");
			} else {
				player.sendMessage(StringUtil.formatUsername(targetUsername) + " could not be found.");
			}
			break;

		case "checkinv":
			if (args.length <= 1) {
				player.sendMessage("You must type in a username to check their inventory.");
				return;
			}

			targetUsername = command.substring("checkinv ".length());
			optionalPlayer = GameService.getWorld().getByUsername(targetUsername);

			if (optionalPlayer.isPresent()) {
				ActionSender.sendInterfaceItems(player, 3214, optionalPlayer.get().getInventory().toArray());
				player.sendMessage("Checking " + StringUtil.formatUsername(targetUsername) + "'s inventory.");
			} else {
				player.sendMessage(StringUtil.formatUsername(targetUsername) + " could not be found.");
			}
			break;

		case "telehome":
		case "movehome":
			try {
				String playerToBan = command.substring(9);

				for (Player target : GameService.getWorld().getPlayers()) {
					if (target.getName().equalsIgnoreCase(playerToBan)) {
						if (player.inWild() && !player.isSuperAdministrator()) {
							player.sendMessage("You can't use this command in the wilderness.");
							return;
						}

						if (target.inWild() && !player.isSuperAdministrator()) {
							player.sendMessage("You can't use this command on a player in the wilderness.");
							return;
						}

						if (target.isSuperAdministrator() && !player.isSuperAdministrator()) {
							target.sendMessage("" + player.getName() + " attempted to teleport you to home.");
							return;
						}

						target.teleport(new Location(Constants.RESPAWN_X, Constants.RESPAWN_Y, 0));
						player.sendMessage("You have teleported " + target.getName() + " home.");
						target.sendMessage("You have been teleported home by " + player.getName() + ".");
					}
				}
			} catch (Exception e) {
				player.sendMessage("Unable to find target.");
			}
			break;

		case "teletome":
			try {
				if (player.inWild() && !player.isSuperAdministrator()) {
					player.sendMessage("You cannot teleport players into PvP areas.");
					return;
				}

				Optional<Player> optionalTarget = GameService.getWorld().getByUsername(command.split(" ", 2)[1]);

				if (optionalTarget.isPresent()) {
					Player target = optionalTarget.get();

					if (target.getPrivilege().superiorTo(player.getPrivilege())) {
						if (target.dialogue().isActive()) {
							player.sendMessage(target.getName() + " is too busy to accept requests.");
						} else {
							target.sendMessage("The moderator " + player.getName() + " tried to teleport you to him.");
							player.sendMessage("The super admin " + target.getName() + " will teleport to you if he wants.");
						}
					} else {
						target.teleport(new Location(player.getX(), player.getY(), player.getZ()));
						target.sendMessage("You've been teleported to " + player.getName());
						player.sendMessage("You've teleported " + target.getName() + " to yourself.");
					}
				} else {
					player.sendMessage("Unable to find " + StringUtil.formatUsername(command.split(" ", 2)[1]) + ".");
				}
			} catch (Exception ex) {
				player.sendMessage("An error occured: " + ex.toString());
			}
			break;

		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.MODERATOR;
	}

	@Override
	public boolean testPermissions(Player player) {
		return true;
	}

}
