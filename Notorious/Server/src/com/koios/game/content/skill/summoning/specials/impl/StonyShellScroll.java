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
 * Handles the granite crab's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class StonyShellScroll implements FamiliarScroll {

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
		familiar.playAnimation(new Animation(8109));
		familiar.playGraphics(new Graphic(1326));
		ScrollManager.boostSkill(player, Skill.DEFENCE, 4);
		return true;
	}

}
