package com.koios.network.in.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.sql2o.Connection;
import org.sql2o.Query;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.DonatorPrivilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendMessagePacket;
import com.koios.util.StringUtil;

public final class PacketReportAbuse implements IncomingPacketDecoder {

	/**
	 * Class which represents an message sent by an {@link Player}.
	 */
	private static final class ChatSnapshot {

		/* The message they sent. */
		private final String message;

		/* The name of the {@link Player} that spoke. */
		private final String name;

		/* When they sent it. */
		private final String timestamp;

		/**
		 * Creates a new snapshot of chat.
		 *
		 * @param name The name of the sender.
		 * @param message The message.
		 */
		private ChatSnapshot(String name, String message) {
			this.name = name;
			this.message = message;
			timestamp = getTime();
		}

	}

	/**
	 * Expand the array if you have a huge playerbase. Five hundred should be more than enough for the regular server (10-20 players).
	 */
	private static final ChatSnapshot[] chatSnapshots = new ChatSnapshot[1024];

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	/**
	 * An array holding proper descriptions for the rules (because we receive an integer telling us what rule was supposedly broken).
	 */
	private static final String[] LIST_OF_RULES = { "Offensive language", "Item scamming", "Password scamming", "Bug abuse", "Exile staff impersonation",
			"Account sharing/trading", "Macroing", "Multiple logging in", "Encouraging others to break rules", "Misuse of customer support", "Advertising / website",
			"Real world item trading" };

	/**
	 * Logs an message sent by Players via public chat.
	 *
	 * @param name The name of the Player who spoke.
	 * @param message The message
	 */
	public static void createChatSnapshot(String name, String message) {
		for (int i = chatSnapshots.length - 1; i > 0; i--) {
			chatSnapshots[i] = chatSnapshots[i - 1];
		}

		chatSnapshots[0] = new ChatSnapshot(name, message);
	}

	/**
	 * Returns the current time (HH:mm:ss) in {@link String} format. Because we also use this method in our abuse report document name, we have to remove the ':'s (hence the
	 * {@link Boolean} b).
	 *
	 * @return Current time of the day in HH:mm:ss format.
	 */
	private static String getTime() {
		return dateTimeFormatter.format(LocalDateTime.now());
	}

	/**
	 * @param moderatorName The username of the {@link Player} that issued the sanction.
	 * @param offenderName The username of the {@link Player} that should be sanctioned.
	 * @param address The IP address targeted; or {@code null} if none.
	 * @param type The sanction type; {@code "ban"} or {@code "mute"}
	 * @param reason The reason for this sanction.
	 * @param durationInDays The duration of this sanction in days; zero is permanent.
	 */
	public static void moderate(String moderatorName, String offenderName, String address, String type, String reason, int durationInDays) {
		if (!GameService.isOnlineMode()) {
			GameService.getWorld().getByUsername(moderatorName).ifPresent(p -> p.sendMessage("Can't do this in development mode."));
		} else if (Constants.SUPER_ADMINISTRATORS.contains(Long.valueOf(StringUtil.usernameToHash(offenderName)))) {
			GameService.getWorld().getByUsername(moderatorName).ifPresent(p -> p.sendMessage("You cannot " + type + " " + offenderName + "."));
		} else {
			new Thread() {

				@Override
				public void run() {
					try (Connection connection = GameService.getConnection()) {
						StringBuilder bldr = new StringBuilder();

						if (durationInDays > 0) {
							if (address == null) {
								bldr.append("INSERT INTO sanctions (name, mod_name, type, issue, expire, reason) ");
								bldr.append("VALUES (:name, :mod_name, :type, NOW(), DATE_ADD(NOW(), INTERVAL :interval DAY), :reason)");
							} else {
								bldr.append("INSERT INTO sanctions (name, ipaddress, mod_name, type, issue, expire, reason) ");
								bldr.append("VALUES (:name, :ipaddress, :mod_name, :type, NOW(), DATE_ADD(NOW(), INTERVAL :interval DAY), :reason)");
							}
						} else {
							if (address == null) {
								bldr.append("INSERT INTO sanctions (name, mod_name, type, issue, reason) ");
								bldr.append("VALUES (:name, :mod_name, :type, NOW(), :reason)");
							} else {
								bldr.append("INSERT INTO sanctions (name, ipaddress, mod_name, type, issue, reason) ");
								bldr.append("VALUES (:name, :ipaddress, :mod_name, :type, NOW(), :reason)");
							}
						}

						try (Query statement = connection.createQuery(bldr.toString())) {
							if (durationInDays > 0) {
								if (address == null) {
									statement.addParameter("name", offenderName);
									statement.addParameter("mod_name", moderatorName);
									statement.addParameter("type", type);
									statement.addParameter("interval", durationInDays);
									statement.addParameter("reason", reason);
								} else {
									statement.addParameter("name", offenderName);
									statement.addParameter("ipaddress", address);
									statement.addParameter("mod_name", moderatorName);
									statement.addParameter("type", type);
									statement.addParameter("interval", durationInDays);
									statement.addParameter("reason", reason);
								}
							} else {
								if (address == null) {
									statement.addParameter("name", offenderName);
									statement.addParameter("mod_name", moderatorName);
									statement.addParameter("type", type);
									statement.addParameter("reason", reason);
								} else {
									statement.addParameter("name", offenderName);
									statement.addParameter("ipaddress", address);
									statement.addParameter("mod_name", moderatorName);
									statement.addParameter("type", type);
									statement.addParameter("reason", reason);
								}
							}

							statement.executeUpdate();
						}

						Optional<Player> player = GameService.getWorld().getByUsername(moderatorName);

						if (player.isPresent()) {
							player.get().getSession().send(new SendMessagePacket("Attempt to " + type + ' ' + (address == null ? offenderName : address) + " successful."));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						Optional<Player> player = GameService.getWorld().getByUsername(moderatorName);

						if (player.isPresent()) {
							player.get().getSession().send(new SendMessagePacket("Error: Attempt to " + type + ' ' + (address == null ? offenderName : address) + " failed!"));
						}
					}
				}

			}.start();
		}
	}

	@Override
	public void handle(Player player, GamePacket packet) {
		long offenderNameAsLong = packet.readLong();
		if (!StringUtil.checkValid(offenderNameAsLong)) {
			return;
		}
		String reportedName = StringUtil.hashToUsername(offenderNameAsLong);
		byte brokenRuleIdent = packet.readByte();
		int moderation = packet.readByte();

		if (!player.hasFocus()) {
			return;
		}

		long lastReport = player.getAttribute("lastReport", 0L);

		if (brokenRuleIdent < 0 || brokenRuleIdent > 12) {
			return;
		}

		if (player.getPrivilege().inferiorTo(RegularPrivilege.MODERATOR) && System.currentTimeMillis() - lastReport < 60000) {
			// this looks well written would like to see this implemmented
			player.sendMessage("You can only report a player once every 60 seconds.");
			return;
		}

		if (reportedName.equalsIgnoreCase(player.getName())) {
			player.sendMessage("You cannot report yourself!");
			return;
		}

		if (moderation > 0 && player.getPrivilege().superiorTo(DonatorPrivilege.REGULAR)) {
			moderate(player.getName(), reportedName, null, moderation == 1 ? "mute" : "ban", LIST_OF_RULES[brokenRuleIdent], 2);
			Optional<Player> target = GameService.getWorld().getByUsernameHash(offenderNameAsLong);

			if (target.isPresent()) {
				if (moderation == 1) {
					target.get().setMuted(true);
				} else if (moderation == 2) {
					GameService.getWorld().unregister(target.get());
				}
			}
		}

		StringBuilder bldr = new StringBuilder().append(player.getName()).append(" reported ").append(reportedName).append(" for ").append(LIST_OF_RULES[brokenRuleIdent])
				.append(".");

		try {
			File directory = new File("./Data/abuse-reports/");

			if (!directory.exists()) {
				if (!directory.mkdirs() && !directory.mkdir()) {
					throw new IOException("failed to of our report directory.");
				}
			}

			long now = System.currentTimeMillis();
			File file = new File(directory, reportedName + " - " + now + ".txt");

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
				bw.write(bldr.toString());

				for (int i = chatSnapshots.length - 1; i >= 0; i--) {
					if (chatSnapshots[i] == null || !reportedName.equalsIgnoreCase(chatSnapshots[i].name) && !player.getName().equalsIgnoreCase(chatSnapshots[i].name)) {
						continue;
					}

					bw.write("\n[" + chatSnapshots[i].timestamp + "] " + chatSnapshots[i].name + ": " + chatSnapshots[i].message);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			player.sendMessage("Failed to write the abuse report. Please contact an administrator.");
			return;
		}

		player.sendMessage("Thank-you, your abuse report has been received.");
		player.setAttribute("lastReport", System.currentTimeMillis());
	}

}
