package com.koios.game.content.command.impl;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.command.Command;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.GamePacketBuilder;
import com.koios.network.in.impl.PacketReportAbuse;
import com.koios.util.StringUtil;

public class SanctionCommand implements Command {

	@Override
	public void execute(Player player, String command) {
		String[] args = command.split(" ");

		if ("kick".equals(args[0])) {
			if (args.length > 1) {
				String targetName = StringUtil.formatUsername(command.substring(args[0].length() + 1));
				Optional<Player> optionalPlayer = GameService.getWorld().getByUsername(targetName);

				if (optionalPlayer.isPresent()) {
					Player target = optionalPlayer.get();
					
					if (target.getCombat().inCombat()) {
						player.sendMessage("You cannot kick a player in combat.");
						return;
					}
					
					if (target.getMinigame().inMinigame()) {
						player.sendMessage("You cannot kick a player in a minimgame.");
						return;
					}

					if (target.getPrivilege().superiorTo(player.getPrivilege())) {
						player.sendMessage("You cannot kick a higher staff member.");
						return;
					}

					player.sendMessage("Kicking " + targetName + ".");
					target.write(new GamePacketBuilder(109).toPacket());
					GameService.getWorld().unregister(target);
				} else {
					player.sendMessage("Unable to find " + targetName + ".");
				}
			} else {
				player.sendMessage("Insufficent parameters: please specify target username.");
			}
		} else if ("ban".equalsIgnoreCase(args[0])) {
			if (player.getPrivilege().inferiorTo(RegularPrivilege.MODERATOR)) {
				player.sendMessage("You do not have permission to ban players.");
				return;
			}
			if (args.length < 3 || !isNumeric(args[2])) {
				player.sendMessage("Syntax: ban [user_name] [days] [reason]");
			} else {
				args = command.split(" ");
				int duration = Integer.parseInt(args[2]);
				String username = StringUtil.formatUsername(args[1].replaceAll("_", " "));
				String reason = args.length == 3 ? null : command.substring(3 + args[0].length() + args[1].length() + args[2].length());
				Optional<Player> target = GameService.getWorld().getByUsername(username);

				if (player.getPrivilege().inferiorTo(RegularPrivilege.ADMINISTRATOR)) {
					if (duration == 0) {
						player.sendMessage("You do not have permission to issue permanent bans.");
						return;
					} else if (reason == null) {
						player.sendMessage("You do not have permission to issue bans without a 'reason' tag.");
						return;
					} else if (duration > 7) {
						player.sendMessage("Request duration changed to 7 because of insufficient privileges.");
						duration = 7;
					}
				}

				if (reason == null) {
					reason = "N/A";
				} else {
					reason = Character.toUpperCase(reason.charAt(0)) + reason.substring(1);
				}

				if (target.isPresent() && target.get().isSuperAdministrator() && !player.isSuperAdministrator()) {
					return;
				}

				player.sendMessage("Attempting to ban " + username + " for " + duration + " days...");
				PacketReportAbuse.moderate(player.getName(), username, null, "ban", reason, duration);
				target.ifPresent(GameService.getWorld()::unregister);
			}
		} else if ("ipban".equals(args[0])) {
			if (player.getPrivilege().inferiorTo(RegularPrivilege.ADMINISTRATOR)) {
				player.sendMessage("You do not have permission to issue IP bans.");
				return;
			}

			if (args.length < 3 || !isNumeric(args[2])) {
				player.sendMessage("Syntax: ipban [user_name] [days] [reason]");
			} else {
				args = command.split(" ");
				int duration = Integer.parseInt(args[2]);
				String username = StringUtil.formatUsername(args[1].replaceAll("_", " "));
				String reason = args.length == 3 ? null : command.substring(3 + args[0].length() + args[1].length() + args[2].length());
				Optional<Player> target = GameService.getWorld().getByUsername(username);

				if (reason == null) {
					reason = "N/A";
				} else {
					reason = Character.toUpperCase(reason.charAt(0)) + reason.substring(1);
				}

				if (target.isPresent()) {
					String address = target.get().getSession().getHostAddress();
					player.sendMessage("Attempting to IP ban " + username + " for " + duration + " days...");
					PacketReportAbuse.moderate(player.getName(), username, address, "ban", reason, duration);

					for (Player other : GameService.getWorld().getPlayers()) {
						if (address.equals(other.getSession().getHostAddress())) {
							GameService.getWorld().unregister(other);
							player.sendMessage("Affected: " + other.getName());
						}
					}
				} else {
					player.sendMessage(username + " could not be found online!");
				}
			}
		} else if ("mute".equals(args[0])) {
			if (args.length < 3 || !isNumeric(args[2])) {
				player.sendMessage("Syntax: mute [user_name] [days] [reason]");
			} else {
				args = command.split(" ");
				int duration = Integer.parseInt(args[2]);
				String username = StringUtil.formatUsername(args[1].replaceAll("_", " "));
				String reason = args.length == 3 ? null : command.substring(3 + args[0].length() + args[1].length() + args[2].length());
				Optional<Player> target = GameService.getWorld().getByUsername(username);

				if (player.getPrivilege().inferiorTo(RegularPrivilege.ADMINISTRATOR)) {
					if (player.getPrivilege() == RegularPrivilege.HELPER && duration > 1) {
						player.sendMessage("Request duration changed to 1 because of insufficient privileges.");
						duration = 1;
					} else if (duration == 0) {
						player.sendMessage("You do not have permission to issue permanent mutes.");
						return;
					} else if (reason == null) {
						player.sendMessage("You do not have permission to issue mutes without a 'reason' tag.");
						return;
					} else if (duration > 7) {
						player.sendMessage("Request duration changed to 7 because of insufficient privileges.");
						duration = 7;
					}
				}

				if (reason == null) {
					reason = "N/A";
				} else {
					reason = Character.toUpperCase(reason.charAt(0)) + reason.substring(1);
				}

				player.sendMessage("Attempting to mute " + username + " for " + duration + " days...");
				PacketReportAbuse.moderate(player.getName(), username, null, "mute", reason, duration);
				target.ifPresent(p -> p.setMuted(true));
			}
		} else if ("ipmute".equals(args[0])) {
			if (args.length < 3 || !isNumeric(args[2])) {
				player.sendMessage("Syntax: ipmute [user_name] [days] [reason]");
			} else {
				args = command.split(" ");
				int duration = Integer.parseInt(args[2]);
				String username = StringUtil.formatUsername(args[1].replaceAll("_", " "));
				String reason = args.length == 3 ? null : command.substring(3 + args[0].length() + args[1].length() + args[2].length());
				Optional<Player> target = GameService.getWorld().getByUsername(username);

				if (player.getPrivilege().inferiorTo(RegularPrivilege.ADMINISTRATOR)) {
					if (duration == 0) {
						player.sendMessage("You do not have permission to issue permanent mutes.");
						return;
					} else if (reason == null) {
						player.sendMessage("You do not have permission to issue mutes without a 'reason' tag.");
						return;
					} else if (duration > 7) {
						player.sendMessage("Request duration changed to 7 because of insufficient privileges.");
						duration = 7;
					}
				}

				if (reason == null) {
					reason = "N/A";
				} else {
					reason = Character.toUpperCase(reason.charAt(0)) + reason.substring(1);
				}

				if (target.isPresent()) {
					String address = target.get().getSession().getHostAddress();
					player.sendMessage("Attempting to IP mute " + username + " for " + duration + " days...");
					PacketReportAbuse.moderate(player.getName(), username, address, "mute", reason, duration);

					for (Player other : GameService.getWorld().getPlayers()) {
						if (address.equals(other.getSession().getHostAddress())) {
							other.setMuted(true);
							player.sendMessage("Affected: " + other.getName());
						}
					}
				} else {
					player.sendMessage(username + " could not be found online!");
				}
			}
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.HELPER;
	}

	private boolean isNumeric(String str) {
		return str.matches("\\d+");
	}

	@Override
	public boolean testPermissions(Player Player) {
		return true;
	}

}
