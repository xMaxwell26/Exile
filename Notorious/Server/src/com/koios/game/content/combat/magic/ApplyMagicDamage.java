package com.koios.game.content.combat.magic;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.melee.MeleeData;
import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.effects.impl.FreezeEffect;
import com.koios.game.content.minigames.misc.TzhaarFightCaveConstants;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.task.MiasmicEffectTask;
import com.koios.task.Task;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

/**
 * Handles the magic damage.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ApplyMagicDamage {

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

		boolean magic_failed = false;

		int damage = CalculateHit.execute(attacker, target, AttackType.MAGIC);

		Player a = attacker.isPlayer() ? attacker.toPlayer() : null;
		Player t = target.isPlayer() ? target.toPlayer() : null;

		if (attacker.isPlayer() && MagicData.godSpells(a) && System.currentTimeMillis() - a.godSpellDelay < Constants.GOD_SPELL_CHARGE) {
			damage += 10;
		}

		magic_failed = damage <= 0 ? true : false;

		if (target.getHitpoints() - damage <= 0) {
			damage = target.getHitpoints();
		}

		if (target.isPlayer()) {
			if (t.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC)) {
				damage = attacker.isMob() ? 0 : damage * 60 / 100;
			}

			if (t.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
				if (t.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13740 || t.specEffect == 8) {
					if (t.getSkill().getLevel(Skill.PRAYER) > 0) {
						damage *= 0.7;
						t.getSkill().decrementLevel(Skill.PRAYER, (int) (damage * 0.3 / 2));
					}
				}

				if (t.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 13742 && NumberUtil.inclusiveRandom(4) == 3) {
					damage *= 0.75;
				}
			}

			if (t.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME) && damage > 0) {
				CombatExtras.handleVengeance(attacker, target, damage);
			}

			if (damage > 0) {
				CombatExtras.handleRecoil(t, damage, attacker);
			}
		}

		if (magic_failed) {
			target.playGraphics(new Graphic(GraphicHeight.HIGH, 85));
		}

		if (attacker.isPlayer()) {
			if (a.magicDef) {
				a.getSkill().addExperience(Skill.DEFENCE, damage * Constants.MELEE_EXP_RATE / 3);
			}

			a.getSkill().addExperience(Skill.MAGIC, MagicData.MAGIC_SPELLS[a.oldSpellId][7] + damage * Constants.MAGIC_EXP_RATE);
			a.getSkill().addExperience(Skill.HITPOINTS, (MagicData.MAGIC_SPELLS[a.oldSpellId][7] + damage * Constants.MAGIC_EXP_RATE) / 3);

			if (MagicData.getEndGfxHeight(a) == 100 && !magic_failed) {
				target.playGraphics(new Graphic(GraphicHeight.HIGH, MagicData.MAGIC_SPELLS[a.oldSpellId][5]));
			} else if (!magic_failed) {
				target.playGraphics(new Graphic(MagicData.MAGIC_SPELLS[a.oldSpellId][5]));
			}

			if (!magic_failed) {
				if (target.isPlayer() && System.currentTimeMillis() - t.reduceStat > 35000) {

					switch (MagicData.MAGIC_SPELLS[a.oldSpellId][0]) {
					case 12987:
					case 13011:
					case 12999:
					case 13023:
						t.getSkill().decrementLevel(Skill.ATTACK, t.getSkill().getLevelForExperience(Skill.ATTACK) / 10);
					}
				}

				int freezeDelay = MagicData.getFreezeTime(a.oldSpellId);

				if (freezeDelay > 0 && !target.getEF().isEffectListed(EffectConstants.FREEZE_NAME)) {
					target.getEF().queueEffect(new FreezeEffect(target, freezeDelay, "A magical force stops you from moving."));
					if (target.isPlayer()) {
						t.getMovementHandler().reset();
						t.sendMessage("You have been frozen.");
						t.frozenBy = a.getIndex();
						a.sendMessage("You freeze your enemy.");
					}
				}

				switch (MagicData.MAGIC_SPELLS[a.oldSpellId][0]) {

				case 12445: // teleblock
					if (System.currentTimeMillis() - t.teleBlockTime > t.teleBlockDuration) {
						t.teleBlockTime = System.currentTimeMillis();
						t.sendMessage("You have been teleblocked.");

						if ((t.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC)) && System.currentTimeMillis() - t.protMageDelay > 1500) {
							t.teleBlockDuration = 150000;
						} else {
							t.teleBlockDuration = 300000;
						}
					}
					break;

				case 21744:
				case 21745:
				case 21746:
				case 22168:
					if (damage > 0 && t.miasmicCloud <= -1) {
						t.sendMessage("A cloud of Miasma slow's your attacks down");
						t.miasmicCloud = 80;
						t.getTaskScheduler().schedule(new MiasmicEffectTask(t));
					}
					break;

				case 12901:
				case 12919: // blood spells
				case 12911:
				case 12929:
					int heal = damage / 4;

					if (a.getHitpoints() + heal > a.getMaxHitpoints()) {
						a.setHitpoints(a.getMaxHitpoints());
					} else {
						a.getSkill().incrementLevel(Skill.HITPOINTS, heal);
					}
					break;

				case 1153:
					t.getSkill().decrementLevel(Skill.ATTACK, t.getSkill().getLevelForExperience(Skill.ATTACK) / 20);
					t.sendMessage("Your attack level has been reduced!");
					t.reduceSpellDelay[a.reduceSpellId] = System.currentTimeMillis();
					break;

				case 1157:
					t.getSkill().decrementLevel(Skill.STRENGTH, t.getSkill().getLevelForExperience(Skill.STRENGTH) / 20);
					t.sendMessage("Your strength level has been reduced!");
					t.reduceSpellDelay[a.reduceSpellId] = System.currentTimeMillis();
					break;

				case 1161:
					t.getSkill().decrementLevel(Skill.DEFENCE, t.getSkill().getLevelForExperience(Skill.DEFENCE) / 20);
					t.sendMessage("Your defence level has been reduced!");
					t.reduceSpellDelay[a.reduceSpellId] = System.currentTimeMillis();
					break;

				case 1542:
					t.getSkill().decrementLevel(Skill.DEFENCE, t.getSkill().getLevelForExperience(Skill.DEFENCE) / 10);
					t.sendMessage("Your defence level has been reduced!");
					t.reduceSpellDelay[a.reduceSpellId] = System.currentTimeMillis();
					break;

				case 1543:
					t.getSkill().decrementLevel(Skill.STRENGTH, t.getSkill().getLevelForExperience(Skill.STRENGTH) / 10);
					t.sendMessage("Your strength level has been reduced!");
					t.reduceSpellDelay[a.reduceSpellId] = System.currentTimeMillis();
					break;

				case 1562:
					t.getSkill().decrementLevel(Skill.ATTACK, t.getSkill().getLevelForExperience(Skill.ATTACK) / 10);
					t.sendMessage("Your attack level has been reduced!");
					t.reduceSpellDelay[a.reduceSpellId] = System.currentTimeMillis();
					break;
				}
			}
		} else {
			Mob mob = attacker.toMob();

			boolean magicFailed = false;

			damage = CalculateHit.execute(attacker, target, AttackType.MAGIC);

			if (damage <= 0) {
				damage = 0;
				magicFailed = true;
			}

			if (target.isPlayer()) {

				Player p = target.toPlayer();

				boolean pray = p.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC);

				if (pray) {
					damage = 0;
					magicFailed = true;
				} else {

					if (mob.getId() == 5666 && damage > 0) {
						p.getSkill().setLevel(1, (int) (p.getSkill().getLevel(1) - damage * 0.3));

						if (p.getSkill().getLevel(1) < 1) {
							p.getSkill().setLevel(1, 1);
						}

						p.getSkill().setLevel(5, (int) (p.getSkill().getLevel(5) - damage * 0.3));

						if (p.getSkill().getLevel(5) < 0) {
							p.getSkill().setLevel(5, 0);
						}
					}

					if (mob.getId() == 9463 && damage > 0) {
						if (!p.getEF().isEffectListed(EffectConstants.FREEZE_NAME)) {
							p.getEF().queueEffect(new FreezeEffect(p, 16, "A magical force stops you from moving."));
							p.playGraphics(new Graphic(369));
							p.sendMessage("You have been frozen.");
							p.getMovementHandler().reset();
						}
					}

					if (target.isPlayer() && p.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
						if (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == ItemIdentifiers.DIVINE_SPIRIT_SHIELD) {
							if (p.getSkill().getLevel(Skill.PRAYER) > 0) {
								damage *= 0.7;

								p.getSkill().setLevel(5, (int) (p.getSkill().getLevel(5) - damage * 0.3 / 2));

								if (p.getSkill().getLevel(5) < 0) {
									p.getSkill().setLevel(5, 0);
								}
							}
						}

						if (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD) {
							if (NumberUtil.random(4) == 0) {
								damage *= 0.75;
							}
						}
					}
				}
			}

			if (target.getHitpoints() - damage < 0) {
				damage = target.getHitpoints();
			}

			if (mob.endGfx > 0 && (!magicFailed || TzhaarFightCaveConstants.isTzhaarNpc(mob.getId()))) {
				if (mob.endGfx == 369) {
					target.playGraphics(new Graphic(mob.endGfx));
				} else {
					target.playGraphics(new Graphic(GraphicHeight.HIGH, mob.endGfx));
				}
			} else if (mob.getId() != 3067 && mob.getId() != 5666 && mob.getId() != 9463 && mob.getId() != 8528 && mob.getId() != 2745 && mob.getId() != 8133 && mob.getId() != 9000
					&& mob.getId() != 6222 && mob.getId() != 6203 && mob.getId() != 6247) {
				GameService.getTaskScheduler().schedule(new Task(2) {

					@Override
					public void execute() {
						if (target.isPlayer() && !target.toPlayer().isDisconnected() || target.isMob()) {
							target.playGraphics(new Graphic(GraphicHeight.HIGH, 85));
						}

						stop();
					}

				});
			}

		}

		if (attacker.isPlayer()) {
			attacker.getCombat().setAttackTimer(MeleeData.getAttackDelay(a));

			if (target.isPlayer()) {
				CombatExtras.applySmite(a, t, damage);
			}

			a.spellCasting = false;
			a.castingMagic = false;

			if (target.isPlayer() && target.inMulti() && MagicData.multiSpells(a)) {
				a.barrageCount = 0;

				for (Player player : GameService.getWorld().getPlayers()) {
					if (player.getIndex() == target.getIndex()) {
						continue;
					}

					if (a.barrageCount >= 9) {
						break;
					}

					if (t.goodDistance(target.getX(), target.getY(), player.getX(), player.getY(), 1)) {
						CombatExtras.appendMultiBarrage(a, player, magic_failed);
					}
				}
			}
		}

		if (attacker.isPlayer() && target.isPlayer()) {
			CombatExtras.applySmite(attacker.toPlayer(), target.toPlayer(), damage);
		}

		attacker.getCombat().setLastAttacked(target);
		target.getCombat().setAggressor(attacker);

		attacker.getCombat().setLastDamageApplied(System.currentTimeMillis());
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());

		if (!magic_failed) {
			target.setDamage(new Damage(damage, HitIcons.MAGIC), attacker.isMob() && attacker.toMob().isFamiliar() ? attacker.toMob().summoner : attacker);
			CombatCurse.handleCurses(attacker, target, damage, AttackType.MAGIC);
		}

		target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

		if (attacker.isPlayer()) {
			a.oldSpellId = 0;
			a.requestUpdates();

			if (a.bowSpecShot <= 0) {
				a.getCombat().setOldTarget(null);
				a.projectileStage = 0;
				a.doubleHit = false;
				a.bowSpecShot = 0;
				a.getCombat().setWeaponUsed(null);
				a.getCombat().setLastWeapon(-1);
			}

			if (a.bowSpecShot != 0) {
				a.bowSpecShot = 0;
			}
		}
	}

}
