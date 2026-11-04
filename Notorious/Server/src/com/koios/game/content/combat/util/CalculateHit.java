package com.koios.game.content.combat.util;

import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Calculates a damage for combat.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CalculateHit {

	/**
	 * Calculates a damage for the attacker with chance of blocking.
	 *
	 * @param attacker
	 * @param target
	 * @param type
	 * @return
	 */
	public static int execute(EntityCharacter attacker, EntityCharacter target, AttackType type) {

		if (attacker == null || target == null) {
			return 0;
		}

		Player p = attacker.isPlayer() ? attacker.toPlayer() : null;
		Player t = target.isPlayer() ? target.toPlayer() : null;

		boolean korasi_effect = false;

		if (attacker.isPlayer() && type == AttackType.MELEE && p.usingSpecial && p.getEquipment().get(3) != null && p.getEquipment().get(3).getId() == 19780) {
			korasi_effect = true;
		}

		boolean hit = CalculateAccuracy.execute(attacker, target, korasi_effect ? AttackType.MAGIC : type);

		if (!hit) {
			if (attacker.isPlayer() && type == AttackType.MAGIC) {
				attacker.toPlayer().magicFailed = true;
			}
			return 0;
		}

		int damage = NumberUtil.inclusiveRandom(CalculateMaxHit.execute(attacker, type));

		if (attacker.isMob()) {
			return damage;
		}

		boolean veracs_effect = false;

		if (attacker.isPlayer() && type == AttackType.MELEE) {
			if (Equipment.hasFullVeracs(p)) {
				if (NumberUtil.inclusiveRandom(4) == 1) {
					veracs_effect = true;
				}
			}

			if (korasi_effect) {
				type = AttackType.MAGIC;
			}

		}

		if (target.isPlayer()) {
			switch (type) {
			case MELEE:
				if ((t.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MELEE, CursedPrayer.DEFLECT_MELEE)) && !veracs_effect) {
					damage = attacker.isMob() ? 0 : (int) (damage * 0.6);
				}
				break;

			case MAGIC:
			case DRAGON_FIRE:
			case DRAGON_FIRE_BALL:
				if (t.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC)) {
					damage = attacker.isMob() ? 0 : (int) (damage * 0.6);
				}
				break;

			case RANGE:
				if (t.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MISSILES, CursedPrayer.DEFLECT_MISSILES)) {
					damage = attacker.isMob() ? 0 : (int) (damage * 0.6);
				}
				break;
			default:
				break;

			}
		}

		return damage;
	}
}