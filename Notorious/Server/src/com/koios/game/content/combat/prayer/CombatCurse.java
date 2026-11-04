package com.koios.game.content.combat.prayer;

import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class CombatCurse {

	public static void handleCurses(EntityCharacter attacker, EntityCharacter target, int damage, AttackType type) {
		if (target.isPlayer()) {
			Player t = target.toPlayer();

			if (damage > 0) {
				if (t.getPrayers().isActive(CursedPrayer.DEFLECT_MAGIC) && type == AttackType.MAGIC) {
					handleDeflectPrayers(attacker, target, damage, 0);
				} else if (t.getPrayers().isActive(CursedPrayer.DEFLECT_MISSILES) && type == AttackType.RANGE) {
					handleDeflectPrayers(attacker, target, damage, 1);
				} else if (t.getPrayers().isActive(CursedPrayer.DEFLECT_MELEE) && type == AttackType.MELEE) {
					handleDeflectPrayers(attacker, target, damage, 2);
				}
			}
		}
		if (attacker.isPlayer()) {
			Player a = attacker.toPlayer();

			if (a.getPrayers().isActive(CursedPrayer.SOULSPLIT)) {
				handleSoulSplit(attacker, target, damage);
			}
		}
	}

	public static void handleDeflectPrayers(EntityCharacter attacker, EntityCharacter target, int damage, int i) {
		if (attacker.getHitpoints() <= 0) {
			return;
		}

		target.playGraphics(new Graphic(2228 + i));
		target.playAnimation(new Animation(12573));
		attacker.setDamage(new Damage((int) (damage * 0.05) + 1, HitMasks.REGULAR_HIT, HitIcons.DEFLECT), target);
	}

	public static void handleSoulSplit(final EntityCharacter attacker, final EntityCharacter target, int damage) {
		Player a = (Player) attacker;
		int form = damage / 4;

		if (form <= 0 || target == null || target.getHitpoints() <= 0) {
			return;
		}

		if (attacker.getHitpoints() + form >= attacker.getMaxHitpoints()) {
			attacker.setHitpoints(attacker.getMaxHitpoints());
		} else {
			attacker.setHitpoints(attacker.getHitpoints() + form);
		}

		if (target.isPlayer()) {
			Player targetPlayer = target.toPlayer();

			if (targetPlayer.getSkill().getLevel(Skill.PRAYER) - form > 0) {
				targetPlayer.getSkill().setLevel(Skill.PRAYER, ((Player) target).getSkill().getLevel(5) - form);
			} else {
				targetPlayer.getSkill().setLevel(Skill.PRAYER, 0);
			}
		}

		CombatExtras.createPlayersProjectile(attacker.getX(), attacker.getY(), a.getLocation().getZ(), target.getX(), target.getY(), 50, 30, 2263, 25, 27,
				target.getProjetileIndex(), 1, 16);
		target.playGraphics(new Graphic(2264));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				CombatExtras.createPlayersProjectile(target.getX(), target.getY(), a.getLocation().getZ(), attacker.getX(), attacker.getY(), 50, 30, 2263, 25, 27,
						attacker.getProjetileIndex(), 1, 16);
				stop();
			}

		});
	}

}
