package com.koios.game.content.command.impl;

import com.koios.game.content.command.Command;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;

public class YellTagCommands implements Command {

	@Override
	public void execute(Player player, String command) {
		if (command.contains("settag")) {
			String tag = command.substring(6);

			if (tag.isEmpty()) {
				return;
			}

			player.getYT().setTag(tag.replace(" ", ""));
		} else if (command.contains("setcolor")) {
			String color = command.substring(8);

			if (color.isEmpty()) {
				return;
			}

			player.getYT().setColour(color.replace(" ", ""));
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.NONE;
	}

	@Override
	public boolean testPermissions(Player player) {
		if (player.isMuted()) {
			player.sendMessage("You have been muted and cannot change your tag!");
			return false;
		} else if (!player.isSuperDonator()) {
			player.sendMessage("You must be a Super Donator to use this command! @red@::Donate@bla@ for more information.");
			return false;
		}

		return true;
	}

}
