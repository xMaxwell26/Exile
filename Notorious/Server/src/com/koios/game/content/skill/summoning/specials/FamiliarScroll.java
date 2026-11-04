package com.koios.game.content.skill.summoning.specials;

import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;

/**
 * Interface for scroll's special abilitys.
 *
 * @author Gabriel | Wolfs Darker
 */
public interface FamiliarScroll {

	/**
	 * Checks if the scroll requires a target to be cast.
	 *
	 * @return if the target is necessary
	 */
	public boolean isTargetRequired();

	/**
	 * Checks if the player can cast the scroll.
	 *
	 * @param player
	 * @param scroll
	 * @return if the scroll can be casted
	 */
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data);

	/**
	 * Attempts to cast the scroll's special ability.
	 *
	 * @param player
	 * @param scroll
	 * @return if the cast was successful
	 */
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data);

}
