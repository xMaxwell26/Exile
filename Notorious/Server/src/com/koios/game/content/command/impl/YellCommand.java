package com.koios.game.content.command.impl;

import java.util.concurrent.TimeUnit;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.in.impl.PacketReportAbuse;

public class YellCommand implements Command {

	@Override
	public void execute(Player player, String command) {
		
		if (!command.startsWith("yell")) {
			return;
		}
		String message = command.substring(4).trim();

		if (message.isEmpty()) {
			player.sendMessage("Shouting nothing out loud is impossible. With the obvious exception of 'nothing' :-)");
			return;
		}
		
		if (message.contains(":tradereq") || message.contains(":duelreq:") || message.contains(":chalreq:")) {
            player.sendMessage("You're not allowed to have tradereq/duelreq tags.");
            return;
        }

		PacketReportAbuse.createChatSnapshot(player.getName(), "[Yell]: " + message);

		if (message.isEmpty()) {
			player.sendMessage("Your message could not be sent. Perhaps you had too many invalid phrases?");
			return;
		}

		if (message.length() > 100) {
			// disregard big text
			message = message.substring(0, 100);
		}

		if (message.length() > 1) {
			char[] chars = message.toLowerCase().toCharArray();
			boolean capitalize = true;

			for (int i = 0; i < chars.length; i++) {
				if (capitalize) {
					if (chars[i] >= 'a' && chars[i] <= 'z') {
						chars[i] += '\uFFE0';
					}

					if (chars[i] != ' ' && chars[i] != '.' && chars[i] != '!' && chars[i] != '?') {
						capitalize = false;
					}
				} else if (chars[i] == '.' || chars[i] == '!' || chars[i] == '?') {
					capitalize = true;
				}
			}

			message = new String(chars);
		} else {
			message = Character.toString(Character.toUpperCase(message.charAt(0)));
		}

		String tag = player.getYT().getTag();
		String color = player.getYT().getColour();

		if (player.isSuperAdministrator()) {
			GameService.getWorld().broadcast(String.format("[<col=" + color + ">" + tag + "</col>] <img=%d>%s: <col=800000>%s", player.getChatIconId(), player.getName(), message));
		} else {
			GameService.getWorld().broadcast(String.format("[<col=" + color + ">" + tag + "</col>] <img=%d>%s: <col=800000>%s", player.getChatIconId(), player.getName(), message),
					p -> !p.disabledGlobalChat);
		}

		player.lastYell.reset().start();
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.NONE;
	}

	@Override
	public boolean testPermissions(Player player) {
		if (player.isMuted()) {
			player.sendMessage("You have been muted and cannot use yell!");
			return false;
		}

		if (player.disabledGlobalChat) {
			player.sendMessage("You cannot broadcast a global message when you have global chat disabled.");
			return false;
		}

		if (!player.isDonator() && player.getPrivilege().inferiorTo(RegularPrivilege.HELPER)) {
			player.sendMessage("You must be a Donator to use this command! @red@::Donate@bla@ for more information!");
			player.sendMessage("Alternatively the @blu@'" + Constants.SERVER_NAME + "'@bla@ friends chat is also a good place to talk...");
			return false;
		}

		if (player.getPrivilege().superiorTo(RegularPrivilege.HELPER)) {
			return true;
		}

		int seconds = 10;

		if (player.isSuperDonator()) {
			seconds = 7;
		}
		if (player.isExtremeDonator()) {
			seconds = 5;
		}
		if (player.isLegendaryDonator()) {
			seconds = 4;
		}
		
		seconds = 0;

		long elapsed = player.lastYell.elapsed(TimeUnit.SECONDS);
		if (elapsed < seconds) {
			player.sendMessage("You must wait another " + (seconds - elapsed) + " seconds to yell again.");
			return false;
		}

		return true;
	}

}
