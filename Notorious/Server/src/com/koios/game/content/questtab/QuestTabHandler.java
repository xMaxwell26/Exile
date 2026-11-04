package com.koios.game.content.questtab;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.questtab.impl.BossLogQuestPage;
import com.koios.game.content.questtab.impl.HelpQuestPage;
import com.koios.game.content.questtab.impl.HomeQuestPage;
import com.koios.game.content.questtab.impl.PkingStatsQuestPage;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.content.questtab.impl.QuickTeleportsQuestPage;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Handles writing the quest tab
 * 
 * @author Timothy Howard <Arithium>
 */
public class QuestTabHandler {

	/**
	 * The key for the current quest page on the quest tab
	 */
	public static final String QUEST_TAB = "QUEST_TAB";
	
	/**
	 * The key to reset the quest tab interface
	 */
	private static final int RESET_KEY = 1225;

	/**
	 * A map to store all of the {@link QuestPage}s
	 */
	private static Map<QuestPages, QuestPage> pages = new HashMap<>();

	/**
	 * Submits all of the quest pages to the map
	 */
	static {
		submit(QuestPages.HOME, new HomeQuestPage());
		submit(QuestPages.BOSS, new BossLogQuestPage());
		submit(QuestPages.TELEPORTS, new QuickTeleportsQuestPage());
		submit(QuestPages.HELP, new HelpQuestPage());
		submit(QuestPages.PKING, new PkingStatsQuestPage());
		submit(QuestPages.POINTS, new PointsQuestPage());
	}

	/**
	 * Submits a new page to the map
	 * 
	 * @param key The {@link QuestPages} key to get the page
	 * @param value The {@link QuestPage} to get
	 */
	private static void submit(QuestPages key, QuestPage value) {
		pages.put(key, value);
	}

	/**
	 * Determines if the player is on the current quest page
	 * 
	 * @param player The {@link Player} to check the current quest page
	 * @param page The {@link QuestPages} to verify if the player is currently viewing
	 * @return If the player is on the current quest page
	 */
	public static boolean onPage(Player player, QuestPages page) {
		return player.getAttribute(QUEST_TAB, QuestPages.HOME) == page;
	}

	/**
	 * Writes a quest tab page to the players quest tab
	 * 
	 * @param player The {@link Player} to write the quest tab too
	 * @param page The {@link QuestPages} page to write
	 */
	public static void write(Player player, QuestPages page) {
		QuestPages currentPage = player.getAttribute(QUEST_TAB, null);
		if (currentPage != null && currentPage == page) {
			// no need to rewrite the quest tab page if we're already on it
			return;
		}

		/* Set the attribute to keep track of our current page */
		player.setAttribute(QUEST_TAB, page);

		/* Reset the quest tab */
		reset(player);

		/* Gets the current page to write */
		QuestPage questPage = pages.get(page);

		/* The title of the page */
		String title = questPage.title();

		/* Writes the title of the quest page */
		player.getSession().send(new SendStringPacket(title == null ? "No Name" : title, 663));

		/* Writes the current quest page for the player */
		questPage.write(player);
	}

	/**
	 * Gets the line id based on the provided line
	 * 
	 * @param line The line on the quest tab to get the line id
	 * @return The line id for the provided line
	 */
	public static int getLineId(int line) {
		if (line < 1)
			line = 1;
		return 33025 + line;
	}

	/**
	 * Resets the quest page strings
	 * 
	 * @param player The {@link Player} to reset the quest pages for
	 */
	private static void reset(Player player) {
		player.getSession().send(new SendConfigPacket(RESET_KEY, 0));
	}
	
	public static QuestPage get(QuestPages page) {
		return pages.get(page);
	}

}
