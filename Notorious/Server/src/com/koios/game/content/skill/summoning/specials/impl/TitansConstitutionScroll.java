package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.content.skill.summoning.specials.ScrollManager;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;

/**
 * Handle the moss,ice and fire titan's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class TitansConstitutionScroll implements FamiliarScroll {

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

		MobFamiliar familiar = player.getSummoning().getFamiliar();

		player.playGraphics(new Graphic(1313));
		player.playAnimation(new Animation(7660));
		player.getSummoning().getFamiliar().playGraphics(new Graphic(familiar.getId() == 7355 ? 1514 : familiar.getId() == 7357 ? 1513 : 1512));
		ScrollManager.boostSkill(player, Skill.HITPOINTS, 20);
		ScrollManager.boostSkill(player, Skill.DEFENCE, (int) (player.getSkill().getLevelForExperience(1) * 0.125));
		return true;
	}

}
