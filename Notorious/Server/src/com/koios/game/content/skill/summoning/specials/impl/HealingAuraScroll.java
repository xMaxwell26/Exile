package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.content.skill.summoning.specials.ScrollManager;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.player.Player;

/**
 * Handles the unicorn's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class HealingAuraScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return false;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		player.getSummoning().getFamiliar().playAnimation(new Animation(8267));
		player.getSummoning().getFamiliar().playGraphics(new Graphic(1356));
		player.playAnimation(new Animation(7660));
		player.playGraphics(new Graphic(1298));
		ScrollManager.boostSkill(player, Skill.HITPOINTS, (int) (player.getSkill().getLevelForExperience(3) * 0.1));
		return true;
	}

}
