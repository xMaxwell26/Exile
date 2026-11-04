package com.koios.game.content.combat.melee;

import com.koios.game.content.activity.PestControl;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.util.CombatData;
import com.koios.game.content.combat.util.CombatData.EquipmentSets;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handles the melee damage.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ApplyMeleeDamage {

	/**
	 * Executes the damage applier and starts the combat again.
	 *
	 * @param attacker
	 * @param target
	 * @param doubleDamage
	 * @param damage
	 */
	public static void execute(EntityCharacter attacker, EntityCharacter target, boolean doubleDamage, int damage) {
        execute(attacker,target,doubleDamage,damage,false);
    }
    public static void execute(EntityCharacter attacker, EntityCharacter target, boolean doubleDamage, int damage, boolean critical) {
		if (attacker == null || target == null || attacker.isDead() || target.isDead() || attacker.getZ() != target.getZ() || target.isTeleporting() || attacker.isTeleporting()) {
			if (attacker != null && attacker.isPlayer()) {
				attacker.toPlayer().doubleHit = false;
			}
			return;
		}

		if (target.getHitpoints() - damage <= 0) {
			damage = target.getHitpoints();
		}

		if (target.getCombat().getAttackTime() <= 3 || target.getCombat().getAttackTime() == 0 && target.getCombat().getTarget() == null) {
			target.playAnimation(new Animation(MeleeData.getBlockAnimation(target)));
		}

		if (target.getCombat().getTarget() == null) {
			if (target.isPlayer() && target.toPlayer().isAutoRetaliateEnabled == 1 && !target.toPlayer().isMoving() || target.isMob()) {
				target.getCombat().setTarget(attacker);
				target.getFollowing().setFollow(attacker, FollowType.COMBAT);
			}
		}

		if (attacker.isPlayer()) {
			Player player = attacker.toPlayer();
			final boolean instantKill = player.isInstantKill();
			boolean fullVeracsEffect = CombatData.hasEquipmentEffect(player, EquipmentSets.VERACS);
			boolean fullGuthansEffect = CombatData.hasEquipmentEffect(player, EquipmentSets.GUTHANS);

			if (instantKill) {
				damage = target.getHitpoints();
			}

			if (target.isMob()) {
				Mob mob = target.toMob();

				if ((mob.getId() == 2882 || mob.getId() == 2883 || mob.getId() == 1160 || mob.getId() == 8349) && !fullVeracsEffect && !instantKill) {
					damage = 0;
				}

				if (player.getEquipment().contains(15403)) {
					if (mob.getId() >= 1338 && mob.getId() <= 1343 || mob.getId() >= 2881 && mob.getId() <= 2883 || mob.getId() == 1347 || mob.getId() == 1356) {
						damage = MeleeData.balmungDamage(player);

						if (damage > mob.getHitpoints()) {
							damage = mob.getHitpoints();
						}
					}
				}

				if (damage > 0 && PestControl.isInGame(player)) {
					if (mob.getId() == 6142) {
						player.pcDamage += damage;

						if (damage < PestControl.portalHealth[0]) {
							PestControl.portalHealth[0] -= damage;
						} else {
							PestControl.portalHealth[0] = 0;
						}
					} else if (mob.getId() == 6143) {
						player.pcDamage += damage;

						if (damage < PestControl.portalHealth[1]) {
							PestControl.portalHealth[1] -= damage;
						} else {
							PestControl.portalHealth[1] = 0;
						}
					} else if (mob.getId() == 6144) {
						player.pcDamage += damage;

						if (damage < PestControl.portalHealth[2]) {
							PestControl.portalHealth[2] -= damage;
						} else {
							PestControl.portalHealth[2] = 0;
						}
					} else if (mob.getId() == 6145) {
						player.pcDamage += damage;

						if (damage < PestControl.portalHealth[3]) {
							PestControl.portalHealth[3] -= damage;
						} else {
							PestControl.portalHealth[3] = 0;
						}
					} else if (PestControl.isPestControlMob(mob.getId())) {
						PestControl.knightHealth += damage >> 2;
					}
				}

			} else {
				final Player opponent = target.toPlayer();

				if (opponent.getEquipment().get(Equipment.SLOT_WEAPON) != null && opponent.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 5698
						&& NumberUtil.inclusiveRandom(3) == 1) {
					player.bonusAttack += damage / 3;
				} else {
					player.bonusAttack += damage / 3;
				}

				if (opponent.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC) && player.specEffect == 8
						&& System.currentTimeMillis() - opponent.protMageDelay > 1500) {
					damage = damage * 60 / 100;
				}

				if (opponent.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MELEE, CursedPrayer.DEFLECT_MELEE) && System.currentTimeMillis() - opponent.protMeleeDelay > 1500
						&& !fullVeracsEffect && player.specEffect != 8) {
					damage = damage * 60 / 100;

					if (opponent.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
						if (opponent.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740) {
							if (opponent.getSkill().getLevel(Skill.PRAYER) > 0) {
								damage *= 0.7;
								opponent.getSkill().decrementLevel(Skill.PRAYER, (int) (damage * 0.3 / 2));
							}
						}

						if (opponent.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742) {
							if (NumberUtil.inclusiveRandom(4) == 3) {
								damage *= 0.75;
							}
						}
					}
				}

				if (opponent.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD)) {
					if (opponent.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740) {
						if (opponent.getSkill().getLevel(Skill.PRAYER) > 0) {
							damage *= 0.7;
							opponent.getSkill().decrementLevel(Skill.PRAYER, (int) (damage * 0.3 / 2));
						}
					}

					if (opponent.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742) {
						if (NumberUtil.inclusiveRandom(4) == 3) {
							damage *= 0.75;
						}
					}
				}

				if (opponent.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
					if (opponent.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15486 && opponent.staffOfLightProtection >= 1 && player.specEffect != 8) {
						damage = damage / 2;
					}
				}

				if (opponent.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME) && damage > 0) {
					CombatExtras.handleVengeance(attacker, target, damage);
				}

				CombatExtras.handleRecoil(opponent, damage, attacker);
				CombatExtras.applySmite(player, opponent, damage);
			}

			CombatExtras.handleSpecialEffect(player, target, damage);
			player.specEffect = 0;
			CombatExtras.addExp(player, damage);

			if (damage > 0 && fullGuthansEffect) {
				player.getSkill().incrementLevel(3, damage);

				if (player.getHitpoints() > player.getMaxHitpoints()) {
					player.getSkill().restoreLevel(3);
				}

				target.playGraphics(new Graphic(398));
			}

			if (player.inBarbDef()) {
				player.barbarianDamage += damage;
			}
		} else {
			Mob mob = attacker.toMob();

			if (target.isPlayer()) {
				Player p = target.toPlayer();
				boolean pray = p.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MELEE, CursedPrayer.DEFLECT_MELEE);

				if (mob.isFamiliar()) {
					if (p.getPrayers().isActive(CursedPrayer.DEFLECT_SUMMONING)) {
						damage = (int) (damage * 0.6);
					}
				}

				if (mob.getId() == 5666) {
					if (damage > 0) {
						p.getSkill().setLevel(1, (int) (p.getSkill().getLevel(1) - damage * 0.3));

						if (p.getSkill().getLevel(1) <= 1) {
							p.getSkill().setLevel(1, 1);
						}

						p.getSkill().setLevel(5, (int) (p.getSkill().getLevel(5) - damage * 0.4));

						if (p.getSkill().getLevel(5) < 0) {
							p.getSkill().setLevel(5, 0);
						}
					}
				}

				if (pray) {
					// protect from melee
					if (mob.getId() == 936 || mob.getId() == 937 || mob.getId() == 1977 || mob.getId() == 1913) {
						p.getPrayers().deactivate(NormalPrayer.PROTECT_FROM_MELEE, CursedPrayer.DEFLECT_MELEE);
						damage = NumberUtil.inclusiveRandom(mob.getMaxHit());
						mob.speak("The gods cannot protect you from me!");
					} else if (mob.getId() == 6688 || mob.getId() == 6689 || mob.getId() == 6691 || mob.getId() == 6729 || mob.getId() == 6730 || mob.getId() == 2030 || mob.getId() == 7133) {
						damage = damage / 2;
					} else if (mob.getId() == 6203) {
						if (NumberUtil.inclusiveRandom(5) == 1) {
							damage = NumberUtil.inclusiveRandom(49);
							p.getSkill().setLevel(5, p.getSkill().getLevel(5) - damage / 4);

							if (p.getSkill().getLevel(5) < 0) {
								p.getSkill().setLevel(5, 0);
							}
						}
					} else if (mob.getId() == 8596 || mob.getId() == 8597 || mob.getId() == 9437 || mob.getId() == 10127) {
						damage *= 0.8;
						p.getSkill().setLevel(5, p.getSkill().getLevel(5) - damage / 2);

						if (p.getSkill().getLevel(5) < 0) {
							p.getSkill().setLevel(5, 0);
						}
					} else {
						damage = 0;
					}
				}

				if (p.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
					if (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740) {
						damage *= 0.7;
						p.getSkill().setLevel(5, (int) (p.getSkill().getLevel(5) - damage * 0.3 / 2));

						if (p.getSkill().getLevel(5) < 0) {
							p.getSkill().setLevel(5, 0);
						}
					}

					if (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742) {
						if (NumberUtil.inclusiveRandom(4) == 3) {
							damage *= 0.75;
						}
					}
				}

				if (p.staffOfLightProtection >= 1) {
					damage = damage / 2;
				}
			}

			if (target.getHitpoints() - damage < 0) {
				damage = target.getHitpoints();
			}

		}

		attacker.getCombat().setLastAttacked(target);
		target.getCombat().setAggressor(attacker);
		attacker.getCombat().setLastDamageApplied(System.currentTimeMillis());
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());
		CombatExtras.handlePoisoning(attacker, target, AttackType.MELEE);
		target.setDamage(new Damage(damage, critical && damage>0 ? Damage.HitMasks.CRITICAL : Damage.HitMasks.REGULAR_HIT, HitIcons.MELEE), attacker.isMob() && attacker.toMob().isFamiliar() ? attacker.toMob().summoner : attacker);
		CombatCurse.handleCurses(attacker, target, damage, AttackType.MELEE);

		if (attacker.isPlayer()) {
			Player p = attacker.toPlayer();

			if (doubleDamage && p.doubleHit) {
				p.doubleHit = false;
			}

			if (p.usingSpecial) {
				p.usingSpecial = false;
				p.getItems().updateSpecialBar();
			}

			p.requestUpdates();
			p.getCombat().setOldTarget(null);
			p.projectileStage = 0;
			p.bowSpecShot = 0;
			p.getCombat().setWeaponUsed(null);
			p.getCombat().setLastWeapon(-1);
			p.bowSpecShot = 0;
			p.getCombat().setAttackTimer(MeleeData.getAttackDelay(p));
		}
	}

}
