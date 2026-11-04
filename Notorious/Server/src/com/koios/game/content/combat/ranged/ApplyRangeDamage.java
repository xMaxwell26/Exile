package com.koios.game.content.combat.ranged;

import com.koios.game.content.activity.PestControl;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.MeleeData;
import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

/**
 * Handles the ranged damage.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ApplyRangeDamage {

	/**
	 * Executes the damage applier and starts the combat again.
	 *
	 * @param attacker
	 * @param target
	 */
	public static void execute(EntityCharacter attacker, EntityCharacter target) {
		if (target == null || attacker.isDead() || target.isDead() || attacker.getZ() != target.getZ() || target.isTeleporting() || attacker.isTeleporting()) {
			return;
		}

		int damage = CalculateHit.execute(attacker, target, AttackType.RANGE);
		int damage2 = -1;

		if (target.getHitpoints() - damage < 0) {
			damage = target.getHitpoints();
		}

		if (target.getCombat().getAttackTime() <= 3 || target.getCombat().getAttackTime() == 0 && target.getCombat().getTarget() == null) {
			target.playAnimation(new Animation(MeleeData.getBlockAnimation(target)));
		}

		if (target.getCombat().getTarget() == null) {
			if (target.isPlayer() && target.toPlayer().isAutoRetaliateEnabled == 1 || target.isMob()) {
				target.getCombat().setTarget(attacker);
				target.getFollowing().setFollow(attacker, FollowType.COMBAT);
			}
		}

		if (attacker.isPlayer()) {
			Player player = attacker.toPlayer();

			if (player.getCombat().getLastWeapon() == 11235 || player.getCombat().getLastWeapon() == 15701 || player.getCombat().getLastWeapon() == 15702
					|| player.getCombat().getLastWeapon() == 15703 || player.getCombat().getLastWeapon() == 15704 || player.bowSpecShot == 1) {
				damage2 = CalculateHit.execute(player, target, AttackType.RANGE);
			}

			player.rangeEndGFX = RangedData.getRangeEndGFX(player);

			if (player.getEquipment().get(3) != null && (player.getEquipment().get(3).getId() == 9185 || player.getEquipment().get(3).getId() == 18357)) {
				if (NumberUtil.inclusiveRandom(10) == 1) {
					if (damage > 0) {
						player.boltDamage = damage;
						CombatExtras.crossbowSpecial(player, target);
						damage *= player.crossbowDamage;
					}
				}
			}

			if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15241
					&& CombatExtras.handleHandCannonAttack(player)) {
				player.getCombat().setTarget(null);
				player.getMovementHandler().reset();
				return;
			}

			if (player.dbowSpec) {
				target.playGraphics(new Graphic(GraphicHeight.HIGH, player.lastArrowUsed == 11212 ? 1100 : 1103));

				if (damage < 8) {
					damage = 8;
				}

				if (damage2 < 8) {
					damage2 = 8;
				}

				player.dbowSpec = false;
			}

			Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon != null && player.usingSpecial) {
				switch (weapon.getId()) {
				case ItemIdentifiers.SARADOMIN_BOW:
					int heal = damage * 2;
					player.getSkill().incrementLevel(Skill.HITPOINTS, heal);
					break;

				case ItemIdentifiers.GUTHIX_BOW:
					player.getSkill().incrementLevel(Skill.HITPOINTS, damage + NumberUtil.random(3));
					damage = (damage * 2) - NumberUtil.random(5);
					break;

				case ItemIdentifiers.ZAMORAK_BOW:
					damage *= 2;
					break;
				}
			}

			if (target.isPlayer()) {
				Player t = target.toPlayer();
				if (t.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
					if (t.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740) {
						if (target.getSkill().getLevel(Skill.PRAYER) > 0) {
							damage *= 0.7;
							target.getSkill().decrementLevel(Skill.PRAYER, (int) (damage * 0.3 / 2));
						}
					}

					if (t.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742) {
						if (NumberUtil.inclusiveRandom(4) == 3) {
							damage *= 0.75;
						}
					}
				}

				if (t.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
					if (t.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740) {
						if (target.getSkill().getLevel(Skill.PRAYER) > 0) {
							damage *= 0.7;
							target.getSkill().decrementLevel(Skill.PRAYER, (int) (damage * 0.3 / 2));
						}
					}

					if (t.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742) {
						if (NumberUtil.inclusiveRandom(4) == 0) {
							damage *= 0.75;
						}
					}
				}
			} else {
				Mob mob = target.toMob();

				if (mob.getId() == 2881 || mob.getId() == 2883 || mob.getId() == 1158 || mob.getId() == 8351 && !player.ignoreDefence) {
					damage = 0;
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

				if (player.inBarbDef()) {
					player.barbarianDamage += damage;

					if (damage2 > 0) {
						player.barbarianDamage += damage2;
					}
				}

				if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)
						&& (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 10034 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 10033)) {
					for (int j = 1; j < MobManager.mobs.length; j++) {
						if (MobManager.mobs[j] != null && MobManager.mobs[j].maximumHitpoints > 0) {
							int nX = MobManager.mobs[j].getX();
							int nY = MobManager.mobs[j].getY();
							int pX = mob.getX();
							int pY = mob.getY();

							if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
								if (mob.inMultiCombat()) {
									CombatExtras.appendMultiChinchompa(player, j);
									Combat.startAttack(MobManager.mobs[j], player);
								}
							}
						}
					}
				}
			}

			if (target.getHitpoints() - damage < 0 || player.isInstantKill()) {
				damage = target.getHitpoints();
			}

			if (target.getHitpoints() - damage - damage2 < 0) {
				damage2 = target.getHitpoints() - damage;
			}

			if (damage < 0) {
				damage = 0;
			}

			if (damage2 < 0 && damage2 != -1) {
				damage2 = 0;
			}

			if (target.isPlayer()) {
				if (target.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME) && damage > 0) {
					CombatExtras.handleVengeance(player, target, damage);
				}

				if (damage > 0) {
					CombatExtras.handleRecoil(target.toPlayer(), damage, player);
				}

				if (damage2 > 0) {
					CombatExtras.handleRecoil(target.toPlayer(), damage2, player);
				}
			}

			CombatExtras.addExp(player, damage);
			player.getItems().dropArrow();

			if (player.rangeEndGFX > 0 && !RangedData.isBolt(player.lastArrowUsed)) {
				if (player.rangeEndGFXHeight) {
					target.playGraphics(new Graphic(GraphicHeight.HIGH, player.rangeEndGFX));
				} else {
					target.playGraphics(new Graphic(player.rangeEndGFX));
				}
			}
		} else {
			Mob mob = attacker.toMob();

			if (target.isPlayer()) {
				Player p = target.toPlayer();
				boolean pray = p.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MISSILES, CursedPrayer.DEFLECT_MISSILES);

				if (mob.getId() == 5666) {
					if (damage > 0) {
						p.getSkill().setLevel(1, (int) (p.getSkill().getLevel(1) - damage * 0.3));

						if (p.getSkill().getLevel(1) < 1) {
							p.getSkill().setLevel(1, 1);
						}

						p.getSkill().setLevel(5, (int) (p.getSkill().getLevel(5) - damage * 0.3));

						if (p.getSkill().getLevel(5) < 0) {
							p.getSkill().setLevel(5, 0);
						}
					}
				}

				if (pray) {
					damage = 0;
				}

				if (p.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD)) {
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
			}
		}

		if (attacker.isMob() && target.isPlayer()) {
			MobManager.handleSpecialEffects(target.toPlayer(), attacker.getIndex(), damage);

			if (target.toPlayer().getEF().isEffectListed(EffectConstants.VENGEANCE_NAME)) {
				MobManager.appendMobVengeance(target.toPlayer(), attacker.toMob(), damage);
			}
		}

		target.getCombat().setAggressor(attacker);
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());
		target.setDamage(new Damage(damage, HitIcons.RANGE), attacker.isMob() && attacker.toMob().isFamiliar() ? attacker.toMob().summoner : attacker);
		attacker.getCombat().setLastDamageApplied(System.currentTimeMillis());
		CombatCurse.handleCurses(attacker, target, damage, AttackType.RANGE);

		if (damage2 != -1) {
			target.setDamage(new Damage(damage2, HitIcons.RANGE), attacker.isMob() && attacker.toMob().isFamiliar() ? attacker.toMob().summoner : attacker);
			target.getCombat().setLastHitDealt(System.currentTimeMillis());
			attacker.getCombat().setLastDamageApplied(System.currentTimeMillis());
			CombatCurse.handleCurses(attacker, target, damage2, AttackType.RANGE);
		}

		if (attacker.isPlayer() && target.isPlayer()) {
			CombatExtras.applySmite(attacker.toPlayer(), target.toPlayer(), damage + damage2);
		}

		if (attacker.isPlayer()) {
			attacker.getCombat().setAttackTimer(MeleeData.getAttackDelay(attacker.toPlayer()));

			if (attacker.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE) {
				attacker.getCombat().setAttackTimer(attacker.getCombat().getAttackTime() - 1);
			}
		}

		CombatExtras.handlePoisoning(attacker, target, AttackType.RANGE);
		target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

		if (attacker.isPlayer()) {
			Player p = attacker.toPlayer();

			if (p.doubleHit) {
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
		}
	}

}
