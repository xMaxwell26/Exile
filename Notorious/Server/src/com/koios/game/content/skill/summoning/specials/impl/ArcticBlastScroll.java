package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.effects.impl.FreezeEffect;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.content.skill.summoning.specials.ScrollManager;
import com.koios.game.map.path.PathChecker;
import com.koios.game.map.path.RS317PathFinding;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handles the arctic bear's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ArcticBlastScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return true;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		MobFamiliar familiar = player.getSummoning().getFamiliar();

		if (!RS317PathFinding.accessable(familiar.getLocation().getX(), familiar.getLocation().getY(), familiar.getZ(), target.getX(), target.getY())
				|| !PathChecker.isProjectilePathClear(familiar.getLocation().getX(), familiar.getLocation().getY(), familiar.getZ(), target.getX(), target.getY())) {
			player.sendMessage("Your familiar can't reach that.");
			return false;
		}

		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		MobFamiliar familiar = player.getSummoning().getFamiliar();

		player.playGraphics(new Graphic(1316));
		player.playAnimation(new Animation(7660));
		familiar.playAnimation(new Animation(8293));
		familiar.playGraphics(new Graphic(1405));
		familiar.playAnimation(new Animation(4921));
		familiar.setFaceIndex(target.getIndex(), target.isPlayer());

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				ScrollManager.sendSummoningProjectile(familiar, target, 1406, 2, 55, 41, 28, 0);
				stop();
			}
		});

		GameService.getTaskScheduler().schedule(new Task(CombatDistance.calculateDamageDelay(familiar.getLocation(), target.getLocation()) + 1) {

			@Override
			public void execute() {
				if (target == null || familiar == null) {
					stop();
					return;
				}
				int damage = CalculateHit.execute(familiar, target, AttackType.MAGIC);
				target.setDamage(new Damage(damage, HitMasks.REGULAR_HIT, HitIcons.MAGIC), player);
				target.playGraphics(new Graphic(damage > 0 ? 1407 : 85));
				if (damage > 0) {
					target.getEF().queueEffect(new FreezeEffect(target, 25, "A magical force stops you from moving."));
				}
				stop();
			}
		});
		return true;
	}

}
