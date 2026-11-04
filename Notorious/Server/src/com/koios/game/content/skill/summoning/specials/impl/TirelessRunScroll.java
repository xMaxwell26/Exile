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
 * Handles the terror bird's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class TirelessRunScroll implements FamiliarScroll {

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

		player.playGraphics(new Graphic(1316));
		player.playAnimation(new Animation(7660));
		familiar.playAnimation(new Animation(8229));
		familiar.playGraphics(new Graphic(1521));
		player.getRunEnergy().setEnergy(player.getRunEnergy().getEnergy() + player.getSkill().getLevelForExperience(Skill.AGILITY) / 2);
		ScrollManager.boostSkill(player, Skill.AGILITY, 2);
		return true;
	}

}
