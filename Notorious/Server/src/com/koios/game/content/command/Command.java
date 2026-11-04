package com.koios.game.content.command;

import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.Privilege;

/**
 * Represents a single command
 *
 * @author Arithium
 */
public interface Command {

	/**
	 * Executes the command
	 */
	public void execute(Player player, String command);

	/**
	 * Returns the rights level required to perform the command.
	 */
	public Privilege getMinimumPrivilege();

	/**
	 * Checks if the player is permitted to execute this command.
	 *
	 * @param player The player.
	 * @return {@code true} if the player is permitted, otherwise {@code false}.
	 */
	public boolean testPermissions(Player player);

}
