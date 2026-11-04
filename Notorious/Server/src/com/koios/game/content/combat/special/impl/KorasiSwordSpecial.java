package com.koios.game.content.combat.special.impl;

import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.special.SpecialAttack;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handles the korasi sword special attack.
 *
 * @author Gabriel | Wolfs Darker
 */
public class KorasiSwordSpecial implements SpecialAttack {

	@Override
	public boolean canExecute(Player attacker, EntityCharacter target, int weapon_id) {
		if (attacker == null || target == null || attacker.isDead() || target.isDead()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean execute(Player attacker, EntityCharacter target, int weapon_id) {
		attacker.playAnimation(new Animation(4000));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			protected void execute() {
				if (attacker == null || target == null) {
					stop();
					return;
				}

				target.setDamage(new Damage(CalculateHit.execute(attacker, target, AttackType.MELEE)), attacker);
				attacker.getCombat().setAttackTimer(4);
				attacker.getCombat().setTarget(target);
				attacker.getCombat().setOldTarget(null);
				stop();
			}

		});

		return true;
	}

	@Override
	public int getSpecialRequired(int weapon) {
		return 60;
	}

}
