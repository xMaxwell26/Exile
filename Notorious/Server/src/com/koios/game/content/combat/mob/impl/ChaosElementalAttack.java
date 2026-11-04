package com.koios.game.content.combat.mob.impl;

import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.MeleeData;
import com.koios.game.content.combat.mob.MobAttack;
import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.NumberUtil;

public final class ChaosElementalAttack implements MobAttack {

	@Override
	public void startAttack(Mob mob, EntityCharacter target) {

		mob.getCombat().setAttackType(NumberUtil.random(2) == 1 ? AttackType.MAGIC : AttackType.RANGE);
		mob.getCombat().setHitDelay(2);
		if (target.isPlayer() && NumberUtil.random(2) == 1) {

			Player player = target.toPlayer();
			switch (mob.getCombat().getAttackType()) {
			case RANGE: // Teleport
				player.teleport(new Location(player.getLocation().getX() + NumberUtil.inclusiveRandom(3), player.getLocation().getY() + NumberUtil.inclusiveRandom(3),
						player.getLocation().getZ()));
				Combat.resetCombat(player);
				break;

			case MAGIC: // Disarming
				if (player.getInventory().freeSlots() > 0) {
					int slot = NumberUtil.inclusiveRandom(11);
					player.getInventory().add(player.getEquipment().get(slot));
					player.getEquipment().remove(player.getEquipment().get(slot));
				}
				break;

			default:
				break;
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

		if (target.getCombat().getAttackTime() <= 3
				|| target.getCombat().getAttackTime() == 0 && target.getCombat().getTarget() == null && target.getCombat().getPreviousTarget() == null) {
			target.playAnimation(new Animation(MeleeData.getBlockAnimation(target)));
		}

		if (target.isPlayer()) {
			Player player = target.toPlayer();

			if (player.getCombat().getTarget() == null) {
				if (player.isAutoRetaliateEnabled == 1) {
					player.getCombat().setTarget(mob);
				}
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
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());
		mob.getCombat().setLastDamageApplied(System.currentTimeMillis());
		target.setDamage(new Damage(damage, HitIcons.NONE), mob);
		CombatCurse.handleCurses(mob, target, real_damage, mob.getCombat().getAttackType());
		target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

	}

}