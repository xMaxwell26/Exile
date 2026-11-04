package com.koios.game.content.command.impl;

import com.koios.game.content.command.Command;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;

public class ChangePasswordCommand implements Command {

	@Override
	public void execute(Player player, String command) {
		
		if (!command.startsWith("changepass")) {
			return;
		}
		
		if (command.length() < 15) {
			player.sendMessage("Syntax: changepassword [new_password]");
			return;
		}

		String newPassword = command.substring(15).trim().toLowerCase();
		char[] chars = newPassword.toCharArray();

		if (chars.length < 6) {
			player.sendMessage("That password is too short. Your password must be 6-19 characters long.");
			return;
		}

		if (chars.length > 20) {
			player.sendMessage("That password is too long. Your password must be 6-19 characters long.");
			return;
		}

		// validate the characters in the new password
		for (char ch : chars) {
			if (!isPermitted(ch)) {
				player.sendMessage("That password contains a bad character: " + String.valueOf(ch) + ".");
				return;
			}
		}

		// we don't want to tell them their password changed if it didn't ...
		if (newPassword.equals(player.getPasswordPlain())) {
			player.sendMessage("Your password had no change.");
			return;
		}

		// the password is validated and OK to change
		player.setPasswordPlain(newPassword);
		player.sendMessage("@bla@Your password has been changed to: '@dre@" + newPassword + "@bla@'");
	}

	@Override
	public RegularPrivilege getMinimumPrivilege() {
		return RegularPrivilege.NONE;
	}

	private boolean isPermitted(char ch) {
		return ch == ' ' || ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z';
	}

	@Override
	public boolean testPermissions(Player player) {
		return true;
	}

}