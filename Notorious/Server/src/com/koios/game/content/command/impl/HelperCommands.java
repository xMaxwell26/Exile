package com.koios.game.content.command.impl;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.util.StringUtil;

public class HelperCommands implements Command {

	@Override
	public void execute(Player player, String command) {
		if (command.startsWith("teleto")) {
			try {
				Optional<Player> optionalTarget = GameService.getWorld().getByUsername(command.split(" ", 2)[1]);

				if (optionalTarget.isPresent()) {
					Player target = optionalTarget.get();

					if (target.getPrivilege().superiorTo(player.getPrivilege())) {
						if (target.dialogue().isActive()) {
							player.sendMessage(target.getName() + " is busy.");
						} else {
							target.sendMessage("The moderator " + player.getName() + " tried to teleport to you.");
							player.sendMessage("The super admin " + target.getName() + " will teleport you to him if he wants.");
						}
					} else {
						player.getMovementHandler().reset();
						player.teleport(new Location(target.getX(), target.getY(), target.getZ()));
						player.sendMessage("You've teleported to " + target.getName() + ".");
					}
				} else {
					player.sendMessage("Unable to find " + StringUtil.formatUsername(command.split(" ", 2)[1]) + ".");
				}
			} catch (Exception ex) {
				player.sendMessage("An error occured: " + ex.toString());
			}
		} else if (command.startsWith("staffzone")) {
			if (player.castTeleport(new Teleport(new Location(2037, 4532, 0), TeleportModes.MODERN_SPELL)))
				player.sendMessage("You teleport to the Staff zone!");
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.HELPER;
	}

	@Override
	public boolean testPermissions(Player player) {
		return true;
	}

}
