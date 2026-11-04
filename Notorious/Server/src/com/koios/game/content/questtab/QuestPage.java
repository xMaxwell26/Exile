package com.koios.game.content.questtab;

import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Represents a single page on the quest tab
 * 
 * @author Timothy Howard <Arithium>
 */
public abstract class QuestPage {

	/**
	 * Writes the quest tab to the player
	 * 
	 * @param player The {@link Player} to write the quest tab too
	 */
	public abstract void write(Player player);

	/**
	 * Writes a line of text to the quest tab
	 * 
	 * @param player The {@link Player} to write on their quest tab
	 * @param line The line to write the text on
	 * @param text The text to write on the line
	 */
	public void writeLine(Player player, int line, String text) {
		player.getSession().send(new SendStringPacket("@or2@" + text, QuestTabHandler.getLineId(line)));
	}

	/**
	 * Determines if the player has clicked the button
	 * 
	 * @param player The {@link Player} clicking the button
	 * @param buttonId The id of the button
	 * @return If the player has clicked an action button on the quest tab
	 */
	public abstract boolean buttonClick(Player player, int buttonId);

	/**
	 * The title of the quest tab page
	 * 
	 * @return The title of the quest tab page
	 */
	public abstract String title();

}
