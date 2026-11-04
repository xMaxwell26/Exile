package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handle the steel titan's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class SteelOfLegendsScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return true;
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
		familiar.playAnimation(new Animation(8190));
		familiar.playGraphics(new Graphic(1449, GraphicHeight.LOW));
		GameService.getTaskScheduler().schedule(new Task(1) {
			int tick = 0;

			@Override
			protected void execute() {
				if (familiar == null || target == null || familiar.isDead()) {
					stop();
					return;
				}
				target.setDamage(new Damage(CalculateHit.execute(familiar, target, AttackType.RANGE), HitMasks.REGULAR_HIT, HitIcons.RANGE), player);
				target.setDamage(new Damage(CalculateHit.execute(familiar, target, AttackType.RANGE), HitMasks.REGULAR_HIT, HitIcons.RANGE), player);
				if (tick == 1) {
					stop();
				}
				tick++;
			}

		});

		return true;
	}

}
