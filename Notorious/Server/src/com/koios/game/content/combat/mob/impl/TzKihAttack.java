package com.koios.game.content.combat.mob.impl;

import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.mob.MobAttack;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

public final class TzKihAttack implements MobAttack {

	@Override
	public void startAttack(Mob mob, EntityCharacter target) {
		if (target.isPlayer()) {
			Player player = target.toPlayer();

			if (player.getSkill().getLevel(Skill.PRAYER) > 0) {
				player.getSkill().decrementLevel(Skill.PRAYER);
			}

			mob.getCombat().setAttackType(AttackType.MELEE);
			mob.getCombat().setHitDelay(2);
		}
	}

	@Override
	public void applyDamage(Mob mob, EntityCharacter target) {

	}

}