package com.koios.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.koios.GameService;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Writes the logs from player actions related to item trading/droping/buying/selling.
 *
 * @author Gabriel | Wolfs Darker
 */
public class Logger {

	/**
	 * The format the date will be saved.
	 */
	public static final DateTimeFormatter date_time_tormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	/**
	 * Returns the path to save logs.
	 *
	 * @param path
	 * @return the path
	 */
	public static File getDirectory(String path) {
		String userHome = System.getProperty("user.home") + File.separator + "koios-store" + File.separator + "logs";
		File directory = new File(userHome, path);
		return directory;
	}

	/**
	 * Send the items list into a list ( string )
	 */
	public static String itemString(Item[] items) {
		String sendTrade = "Absolutely nothing!";

		if (items == null) {
			return sendTrade;
		}

		for (int i = 0; i < items.length; i++) {
			Item item = items[i];

			if (item == null || item.getId() < 0) {
				continue;
			}

			if (i == 0) {
				sendTrade = "ID:" + item.getId();

				if (item.getDefinition().isStackable()) {
					sendTrade += " x " + item.getCount();
				}
			} else {
				sendTrade += "ID:" + item.getId();

				if (item.getDefinition().isStackable()) {
					sendTrade += " x " + item.getCount();
				}
			}
		}

		return sendTrade;
	}

	/**
	 * Write's log for 2 players and 2 items array ( ex duel and trade )
	 *
	 * @param player
	 * @param other
	 * @param in
	 * @param out
	 * @param folder
	 */
	public static void writeLogs(Player player, Player other, Item[] in, Item[] out, String folder) {
		if (!GameService.isOnlineMode() || player == null || other == null || in == null && out == null || folder == null || folder.length() == 0) {
			return;
		}

		File logFile = new File(Logger.getDirectory(folder), player.getName() + ".txt");

		if (!logFile.getParentFile().exists()) {
			if (!logFile.getParentFile().mkdirs()) {
				System.err.println("Failed to make parent directories for: " + logFile.getAbsolutePath());
			}
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
			bw.write((folder.contains("duel") ? "Duel" : "Trade") + " with '" + other.getName() + "' on " + LocalDateTime.now().format(Logger.date_time_tormatter) + ", Ip: "
					+ player.getSession().getHostAddress());
			bw.newLine();
			bw.write("In: ");
			bw.write(itemString(in));
			bw.newLine();
			bw.write("Out: ");
			bw.write(itemString(out));
			bw.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Write logs for 1 message only ( ex buying items from shop )
	 *
	 * @param player
	 * @param message
	 * @param folder
	 */
	public static void writeLogs(Player player, String message, String folder) {
		if (!GameService.isOnlineMode() || player == null || message == null || folder == null || folder.length() == 0) {
			return;
		}

		File logFile = new File(getDirectory(folder), player.getName() + ".txt");

		if (!logFile.getParentFile().exists()) {
			if (!logFile.getParentFile().mkdirs()) {
				System.err.println("Failed to make parent directories for: " + logFile.getAbsolutePath());
			}
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
			bw.write(message);
			bw.newLine();
			bw.write("Time: " + LocalDateTime.now().format(date_time_tormatter) + ", Ip: " + player.getSession().getHostAddress());
			bw.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
