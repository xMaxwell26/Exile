package com.koios.game.content.combat.special.impl;

import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.ApplyMeleeDamage;
import com.koios.game.content.combat.special.SpecialAttack;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.combat.util.CalculateMaxHit;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handles the dragon claws special attack.
 *
 * @author Gabriel | Wolfs Darker
 */
public class DragonClawsSpecial implements SpecialAttack {

	@Override
	public boolean canExecute(Player attacker, EntityCharacter target, int weapon_id) {
		if (attacker == null || target == null || attacker.isDead() || target.isDead()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean execute(Player attacker, EntityCharacter target, int weapon_id) {
		attacker.playAnimation(new Animation(10961));
		attacker.playGraphics(new Graphic(1950));

		int firstHit = CalculateHit.execute(attacker, attacker.getCombat().getTarget(), AttackType.MELEE);
		int secondHit = CalculateHit.execute(attacker, attacker.getCombat().getTarget(), AttackType.MELEE);
		int thirdHit = 0;
		int fourthHit = 0;

		if (firstHit > 0) {
			secondHit = firstHit / 2;
			thirdHit = secondHit / 2;
			fourthHit = thirdHit;
		}

		if (firstHit == 0 && secondHit > 0) {
			thirdHit = secondHit / 2;
			fourthHit = thirdHit;
		}

		if (firstHit == 0 && secondHit == 0) {
			int max = CalculateMaxHit.execute(attacker, AttackType.MELEE) * 3 / 4;
			int hit = Math.max(max, CalculateHit.execute(attacker, attacker.getCombat().getTarget(), AttackType.MELEE));
			thirdHit = hit;
			fourthHit = hit;
		}

		if (firstHit == 0 && secondHit == 0 && thirdHit == 0) {
			int hit = CalculateHit.execute(attacker, attacker.getCombat().getTarget(), AttackType.MELEE);
			int boost = (int) (hit + hit * 0.5);
			fourthHit = boost;
		}

		if (firstHit == 0 && secondHit == 0 && thirdHit == 0 && fourthHit == 0) {
			fourthHit = attacker.getRandom().nextInt(7) + 1;
		}

		ApplyMeleeDamage.execute(attacker, attacker.getCombat().getTarget(), false, firstHit);
		ApplyMeleeDamage.execute(attacker, attacker.getCombat().getTarget(), false, secondHit);

		int _thirdHit = thirdHit;
		int _fourthHit = fourthHit;

		// Only apply the third and fourth hits if they are still alive.
		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			protected void execute() {
				if (attacker.isDisconnected()) {
					stop();
					return;
				}

				ApplyMeleeDamage.execute(attacker, attacker.getCombat().getTarget(), false, _thirdHit);
				ApplyMeleeDamage.execute(attacker, attacker.getCombat().getTarget(), false, _fourthHit);
				attacker.getCombat().setLastDamageApplied(System.currentTimeMillis());
				target.getCombat().setLastDamageApplied(System.currentTimeMillis());
				stop();
			}

		});

		attacker.getCombat().setAttackTimer(3);
		attacker.getCombat().setTarget(target);
		attacker.getCombat().setOldTarget(null);
		return true;
	}

	@Override
	public int getSpecialRequired(int weapon) {
		return 50;
	}

}
