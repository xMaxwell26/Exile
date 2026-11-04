package com.koios.game.model.player.update;

import com.koios.game.model.player.Player;

/**
 * Represents a single updater which can be used by players
 *
 * @author Timothy Howard <Arithium>
 */
public abstract class EntityUpdater {

	/**
	 * Updates the {@link Player}
	 *
	 * @param player The {@link Player} to update
	 */
	public abstract void update(Player player);

}
