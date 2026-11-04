package com.koios.game.content.combat.mob.impl;

import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.MeleeData;
import com.koios.game.content.combat.mob.MobAttack;
import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.NumberUtil;

public class ArmadylBossAttack implements MobAttack {

	@Override
	public void startAttack(Mob mob, EntityCharacter target) {
		int nX = mob.getX();
		int nY = mob.getY();
		int pX = target.getX();
		int pY = target.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		boolean can_melee = CombatDistance.inCombatDistance(mob, target, AttackType.MELEE, false);

		switch (NumberUtil.inclusiveRandom(can_melee ? 2 : 1)) {
		case 1:
			mob.getCombat().setAttackType(AttackType.MAGIC);

			if (!CombatDistance.inCombatDistance(mob, target, mob.getCombat().getAttackType(), false)) {
				return;
			}

			mob.getCombat().setHitDelay(3);
			mob.playAnimation(new Animation(6976));

			if (target.isPlayer()) {
				Player player = target.toPlayer();
				CombatExtras.createPlayersProjectile(nX, nY, player.getLocation().getZ(), offX, offY, 50, 105, 1198, 43, 31, target.getProjetileIndex(), 65, 16);
			}
			break;

		case 2:
			mob.getCombat().setAttackType(AttackType.MELEE);

			if (!CombatDistance.inCombatDistance(mob, target, mob.getCombat().getAttackType(), false)) {
				return;
			}

			mob.getCombat().setHitDelay(2);
			mob.playAnimation(new Animation(6977));
			break;

		default:
			mob.getCombat().setAttackType(AttackType.RANGE);

			if (!CombatDistance.inCombatDistance(mob, target, mob.getCombat().getAttackType(), false)) {
				return;
			}

			mob.getCombat().setHitDelay(3);
			mob.playAnimation(new Animation(6976));

			if (target.isPlayer()) {
				Player player = target.toPlayer();
				CombatExtras.createPlayersProjectile(nX, nY, player.getLocation().getZ(), offX, offY, 50, 105, 1197, 43, 31, target.getProjetileIndex(), 65, 16);
			}
		}

		mob.getCombat().setOldTarget(target);
		mob.getCombat().setAttackTimer(MobManager.getMobDelay(mob.getIndex()));

		if (target.isPlayer()) {
			target.toPlayer().getSession().send(new SendRemoveInterfacePacket());
		}
	}

	@Override
	public void applyDamage(Mob mob, EntityCharacter target) {
		int damage = CalculateHit.execute(mob, target, mob.getCombat().getAttackType());
		int real_damage = damage;

		if (target.isPlayer()) {
			Player player = target.toPlayer();

			if (player.getCombat().getTarget() == null) {
				if (player.isAutoRetaliateEnabled == 1) {
					player.getCombat().setTarget(mob);
				}
			}

			if (player.getCombat().getAttackTime() <= 3
					|| player.getCombat().getAttackTime() == 0 && player.getCombat().getTarget() == null && player.getCombat().getPreviousTarget() == null) {
				player.playAnimation(new Animation(MeleeData.getBlockEmote(player)));
			}

			if (mob.getCombat().getAttackType() == AttackType.MELEE && (player.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MELEE, CursedPrayer.DEFLECT_MELEE))
					|| mob.getCombat().getAttackType() == AttackType.RANGE && (player.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MISSILES, CursedPrayer.DEFLECT_MISSILES))
					|| mob.getCombat().getAttackType() == AttackType.MAGIC && (player.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC))) {
				damage = 0;
			} else {
				if (player.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD)) {
					if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740) {

						damage *= 0.7;
						real_damage = damage;
						player.getSkill().setLevel(5, (int) (player.getSkill().getLevel(5) - damage * 0.3 / 2));

						if (player.getSkill().getLevel(5) < 0) {
							player.getSkill().setLevel(5, 0);
						}
					}

					if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742) {
						if (NumberUtil.inclusiveRandom(4) == 3) {
							damage *= 0.75;
							real_damage = damage;
						}
					}
				}
			}

			if (player.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME)) {
				MobManager.appendMobVengeance(player, mob, damage);
			}
		}

		if (target.getHitpoints() - damage < 0) {
			damage = target.getHitpoints();
			real_damage = damage;
		}

		if (mob.getCombat().getAggressor() != null) {
			mob.getCombat().setTarget(mob.getCombat().getAggressor());
			mob.getFollowing().setFollow(mob.getCombat().getAggressor(), FollowType.COMBAT);
		}

		target.getCombat().setAggressor(mob);
		mob.getCombat().setLastDamageApplied(System.currentTimeMillis());
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());
		target.setDamage(new Damage(damage, HitIcons.NONE), mob);
		CombatCurse.handleCurses(mob, target, real_damage, mob.getCombat().getAttackType());
		target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

}
