package com.koios.game.content.combat.mob;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.combat.mob.impl.ArmadylBossAttack;
import com.koios.game.content.combat.mob.impl.BandosBossAttack;
import com.koios.game.content.combat.mob.impl.ChaosElementalAttack;
import com.koios.game.content.combat.mob.impl.CorporealBeastAttack;
import com.koios.game.content.combat.mob.impl.JadAttack;
import com.koios.game.content.combat.mob.impl.PummellerAttack;
import com.koios.game.content.combat.mob.impl.SaradominBossAttack;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.mob.Mob;

public class MobAttackManager {

	private static Map<Integer, MobAttack> attackList = new HashMap<>();

	static {
		attackList.put(6260, new BandosBossAttack());
		attackList.put(6247, new SaradominBossAttack());
		attackList.put(6222, new ArmadylBossAttack());
		attackList.put(8133, new CorporealBeastAttack());
		attackList.put(10141, new PummellerAttack());
		attackList.put(2745, new JadAttack());
		attackList.put(3200, new ChaosElementalAttack());
		// attackList.put(TzhaarFightCaveConstants.TZ_KIH, new TzKihAttack());
	}

	public static boolean hasAttack(int id) {
		return attackList.get(id) != null;
	}

	public static void startAttack(Mob mob, EntityCharacter target) {
		if (mob == null || target == null) {
			return;
		}

		MobAttack attack = attackList.get(mob.getId());

		if (attack == null) {
			return;
		}

		attack.startAttack(mob, target);
	}

	public static void applyDamage(Mob mob, EntityCharacter target) {
		if (mob == null || target == null) {
			return;
		}

		MobAttack attack = attackList.get(mob.getId());

		if (attack == null) {
			return;
		}

		attack.applyDamage(mob, target);
	}

}
