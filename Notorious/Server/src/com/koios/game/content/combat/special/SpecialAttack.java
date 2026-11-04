package com.koios.game.content.combat.special;

import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;

/**
 * Interface that handles the special attacks.
 *
 * @author Gabriel | Wolfs Darker
 */
public interface SpecialAttack {

	/**
	 * Returns the amount of special energy required to perform the special attack.
	 *
	 * @param weapon
	 * @return the amount of energy
	 */
	public abstract int getSpecialRequired(int weapon);

	/**
	 * Checks if the player can perform the special attack.
	 *
	 * @param attacker
	 * @param target
	 * @param weapon_id
	 * @return if the player can perform
	 */
	public abstract boolean canExecute(Player attacker, EntityCharacter target, int weapon_id);

	/**
	 * Executes the special attack.
	 *
	 * @param attacker
	 * @param target
	 * @param weapon_id
	 * @return if it was successful
	 */
	public abstract boolean execute(Player attacker, EntityCharacter target, int weapon_id);

}
