package com.koios.game.content.combat.mob.impl;

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
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.NumberUtil;

public class PummellerAttack implements MobAttack {

	@Override
	public void startAttack(Mob mob, EntityCharacter target) {

		if (NumberUtil.inclusiveRandom(3) == 1 || !CombatDistance.inCombatDistance(mob, target, AttackType.MELEE, false)) {
			mob.getCombat().setAttackType(AttackType.MAGIC);

			if (!CombatDistance.inCombatDistance(mob, target, mob.getCombat().getAttackType(), false)) {
				return;
			}

			mob.getCombat().setHitDelay(3);
			mob.playAnimation(new Animation(13604));
		} else {
			mob.getCombat().setAttackType(AttackType.MELEE);

			if (!CombatDistance.inCombatDistance(mob, target, mob.getCombat().getAttackType(), false)) {
				return;
			}

			mob.getCombat().setHitDelay(2);
			int random = NumberUtil.inclusiveRandom(6);
			mob.playAnimation(new Animation(13599 + (random != 2 && random != 3 && random != 5 && random < 6 ? random : 0)));
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

		if (mob.getCombat().getAttackType() == AttackType.MAGIC) {
			target.playGraphics(new Graphic(872));
		}

		if (mob.getCombat().getAggressor() != null) {
			mob.getCombat().setTarget(mob.getCombat().getAggressor());
			mob.getFollowing().setFollow(mob.getCombat().getAggressor(), FollowType.COMBAT);
		}

		target.getCombat().setAggressor(mob);
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());
		mob.getCombat().setLastDamageApplied(System.currentTimeMillis());
		target.setDamage(new Damage(damage, HitIcons.NONE), mob);
		CombatCurse.handleCurses(mob, target, real_damage, mob.getCombat().getAttackType());
		target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

}
