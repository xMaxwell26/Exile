package com.koios.game.content.effects;

import com.koios.game.model.player.Player;

/**
 * Interface to build entity's effects
 *
 * @author Gabriel | Wolfs Darker
 */
public interface Effect {

	public enum Duration {

		/**
		 * The effect will fade after a duration
		 */
		TEMPORARY,

		/**
		 * Effect will always happen
		 */
		PERMANENT

	}

	public enum QueuePolicy {

		/**
		 * Effect will always queue
		 */
		ALWAYS,

		/**
		 * Effect will never queue ( instant effect )
		 */
		NEVER

	}

	public enum Walk {

		/**
		 * Player can walk while the effect is on
		 */
		WALKABLE,

		/**
		 * Player can't walk while the effect is on
		 */
		NON_WALKABLE

	}

	/**
	 * Get the effect's name
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Get the effect's time left
	 *
	 * @return the time
	 */
	public int getTimeLeft();

	/**
	 * Get the effect's queue type
	 *
	 * @return the type
	 */
	public QueuePolicy getQueueType();

	/**
	 * Get the effect's duration ( temporary or permanent )
	 *
	 * @return the duration
	 */
	public Duration getDuration();

	/**
	 * Get the effect's walk ability ( walkable or not )
	 *
	 * @return the ability
	 */
	public Walk getWalkEffect();

	/**
	 * Renew the effect, if possible
	 */
	public void renew();

	/**
	 * Start the effect
	 */
	public void start();

	/**
	 * Tick the effect's features
	 */
	public void tick();

	/**
	 * Finish the effect
	 */
	public void end();

	/**
	 * Load the effect's information from player's file
	 *
	 * @param s the information
	 */
	public void load(Player p, String s);

	/**
	 * Returns the message to the player if he cant walk.
	 *
	 * @return
	 */
	public String getNonWalkableMessage();

}
