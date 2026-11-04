package com.koios.game.content.combat.special.impl;

import com.koios.GameService;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.ranged.RangedData;
import com.koios.game.content.combat.special.SpecialAttack;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class HandCannonSpecial implements SpecialAttack {

	@Override
	public boolean canExecute(Player attacker, EntityCharacter target, int weapon_id) {
		if (attacker == null || target == null || attacker.isDead() || target.isDead()) {
			return false;
		}

		int currentFmLvl = attacker.getSkill().getLevel(Skill.FIREMAKING);
		double calculate = currentFmLvl + NumberUtil.inclusiveRandom(100);
		int usingSpecialBonus = attacker.usingSpecial ? 20 : 0;
		int random = NumberUtil.inclusiveRandom(100) + usingSpecialBonus;
		boolean explode = calculate < random ? true : false;

		if (explode) {
			int explodeDamage = NumberUtil.inclusiveRandom(15) + 1;
			attacker.setDamage(new Damage(explodeDamage), attacker);
			attacker.playGraphics(new Graphic(2140));
			attacker.getEquipment().set(Equipment.SLOT_WEAPON, null);
			attacker.sendMessage("Your hand cannon malfunctions and explodes.");

			if (attacker.getCombat().getTarget() != null) {
				Combat.resetCombat(attacker);
				attacker.getMovementHandler().reset();
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean execute(Player attacker, EntityCharacter target, int weapon_id) {
		attacker.getCombat().setOldTarget(target);
		attacker.playAnimation(new Animation(12175));
		attacker.getCombat().setAttackTimer(10);
		attacker.getCombat().setHitDelay(10);
		attacker.specialGraphics = true;

		GameService.getTaskScheduler().schedule(new Task(1) {

			int stage = 0;
			int ticks = 2;

			@Override
			public void execute() {

				if (attacker == null || target == null) {
					stop();
					attacker.getCombat().setAttackTimer(0);
					return;
				}

				ticks--;

				if (ticks > 0) {
					return;
				}

				switch (stage) {
				case 0:
					attacker.playAnimation(new Animation(12174));
					attacker.playGraphics(new Graphic(2138));
					ticks = CombatDistance.calculateDamageDelay(attacker.getLocation(), target.getLocation());
					RangedData.fireProjectile(attacker);
					break;

				case 1:
					target.setDamage(new Damage(CalculateHit.execute(attacker, target, AttackType.RANGE)), attacker);
					RangedData.fireProjectile(attacker);
					ticks = 1;
					break;

				case 2:
					target.setDamage(new Damage(CalculateHit.execute(attacker, target, AttackType.RANGE)), attacker);
					attacker.specialGraphics = false;
					attacker.ignoreHit = false;
					attacker.getCombat().setAttackTimer(4);
					attacker.getCombat().setTarget(target);
					attacker.getCombat().setOldTarget(null);
					stop();
					break;
				}

				stage++;
			}

		});

		return true;
	}

	@Override
	public int getSpecialRequired(int weapon) {
		return 50;
	}

}
