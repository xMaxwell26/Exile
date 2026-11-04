package com.koios.game.content.combat;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.melee.MeleeRequirements;
import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.ranged.RangedData;
import com.koios.game.content.combat.special.SpecialHandler;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.combat.util.CombatData;
import com.koios.game.content.combat.util.CombatData.PoisonType;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.effects.impl.FreezeEffect;
import com.koios.game.content.effects.impl.Poison;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.WeaponDefinitions;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.task.MiasmicEffectTask;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class CombatExtras {

	public static void addCharge(Player player) {
		if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() != 11283 && player.getEquipment().get(Equipment.SLOT_SHIELD).getId() != 11284) {
			return;
		}

		player.playGraphics(new Graphic(1160));
		player.dfsCount++;

		if (player.dfsCount == 50) {
			player.sendMessage("Your dragonfire shield is now fully charged.");
		}
	}

	public static void handleDragonFireShield(Player player) {
		if (player.getCombat().getTarget() == null) {
			return;
		}

		EntityCharacter target = player.getCombat().getTarget();

		if (target.getHitpoints() <= 0) {
			return;
		}

		if (player.dfsCount <= 0) {
			player.sendMessage("Your shield has no charges to fire.");
			return;
		}

		if (System.currentTimeMillis() - player.dfsDelay > 120000) {
			final int pX = player.getX();
			final int pY = player.getY();
			final int nX = target.getX();
			final int nY = target.getY();
			final int offX = (pY - nY) * -1;
			final int offY = (pX - nX) * -1;
			final int damage = NumberUtil.inclusiveRandom(25) + 5;
			player.dfsDelay = System.currentTimeMillis();
			player.dfsCount -= 1;
			player.playAnimation(new Animation(6696));
			player.playGraphics(new Graphic(1165));
			player.getCombat().setAttackTimer(player.getCombat().getAttackTime() + 4);

			GameService.getTaskScheduler().schedule(new Task(2) {

				@Override
				public void execute() {
					if (player.isDisconnected() || target == null) {
						stop();
						return;
					}

					createPlayersProjectile(pX, pY, player.getLocation().getZ(), offX, offY, 50, 10, 1166, 25, 27, target.getProjetileIndex(), 0, 16);
					stop();
				}
			});

			GameService.getTaskScheduler().schedule(new Task(3) {

				@Override
				public void execute() {
					if (target != null) {
						target.playGraphics(new Graphic(GraphicHeight.HIGH, 1167));
						target.setDamage(new Damage(damage, HitIcons.NONE), player);
						target.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
						stop();
					}
				}

			});
		} else {
			player.sendMessage("You must wait 2 minutes before using another charge.");
		}
	}

	public static void handleGmaul(Player player) {

		EntityCharacter target = player.getCombat().getTarget();

		if (target == null) {
			return;
		}

		if (player.goodDistance(player.getX(), player.getY(), target.getX(), target.getY(), MeleeRequirements.getRequiredDistance(player))) {
			if (target.isMob() || target.isPlayer() && MeleeRequirements.testRequirements(player)) {
				if (SpecialHandler.checkSpecial(player, 4153)) {
					int damage = CalculateHit.execute(player, target, AttackType.MELEE);

					if (target.getHitpoints() - damage <= 0) {
						damage = target.getHitpoints();
					}

					if (target.getHitpoints() > 0) {
						target.setDamage(new Damage(damage, HitIcons.MELEE), player);
						CombatCurse.handleCurses(player, target, damage, AttackType.MELEE);
						player.playAnimation(new Animation(1667));
						target.playGraphics(new Graphic(GraphicHeight.HIGH, 340));
					}
				}
			}
		}

	}

	public static void handleSpecialEffect(Player attacker, EntityCharacter target, int damage) {
		if (target.isPlayer()) {
			Player opponent = target.toPlayer();

			switch (attacker.specEffect) {
			case 1: // dragon scimmy special
				if (damage > 0) {
					opponent.sendMessage("You have been injured!");
					opponent.stopPrayerDelay = System.currentTimeMillis();
					opponent.getPrayers().deactivate(NormalPrayer.PROTECT_FROM_MAGIC, NormalPrayer.PROTECT_FROM_MELEE, NormalPrayer.PROTECT_FROM_MISSILES,
							CursedPrayer.DEFLECT_MAGIC, CursedPrayer.DEFLECT_MELEE, CursedPrayer.DEFLECT_MISSILES);
				}
				break;

			case 2:
				if (damage > 0) {

					opponent.getEF().queueEffect(new FreezeEffect(opponent, 16, "A magical force stops you from moving."));
					opponent.playGraphics(new Graphic(2104));
					opponent.sendMessage("You have been frozen.");
					opponent.frozenBy = attacker.getIndex();
					opponent.getMovementHandler().reset();
					attacker.sendMessage("You freeze your enemy.");
				}
				break;

			case 3:
				if (damage > 0) {
					opponent.getSkill().decrementLevel(1, damage);
					opponent.sendMessage("You feel weak.");

					if (opponent.getSkill().getLevel(1) < 1) {
						opponent.getSkill().setLevel(1, 1);
					}

				}
				break;

			case 4:
				if (damage > 0) {
					int heal = (int) (damage * 0.50);
					int prayer = (int) (damage * 0.25);

					if (heal < 10) {
						heal = 10;
					}

					if (prayer < 5) {
						prayer = 5;
					}

					if (attacker.getHitpoints() + heal > attacker.getSkill().getLevelForExperience(Skill.HITPOINTS)) {
						attacker.getSkill().restoreLevel(Skill.HITPOINTS);
					} else {
						attacker.getSkill().incrementLevel(Skill.HITPOINTS, heal);
					}

					if (attacker.getSkill().getLevel(Skill.PRAYER) + prayer > attacker.getSkill().getLevelForExperience(Skill.PRAYER)) {
						attacker.getSkill().restoreLevel(Skill.PRAYER);
					} else {
						attacker.getSkill().incrementLevel(Skill.PRAYER, prayer);
					}
				}
				break;

			case 5:
				opponent.playGraphics(new Graphic(GraphicHeight.HIGH, 341));
				break;

			case 6:
				opponent.playGraphics(new Graphic(GraphicHeight.HIGH, 1194));
				break;

			case 7:
				if (damage > 0) {
					opponent.getSkill().decrementLevel(1, (int) (damage + 1 * 0.3));
					opponent.sendMessage("You feel weak.");

					if (opponent.getSkill().getLevel(1) < 1) {
						opponent.getSkill().setLevel(1, 1);
					}
				}
				break;

			case 8:
				opponent.playGraphics(new Graphic(GraphicHeight.HIGH, 1248));
				break;
			}
		} else {
			switch (attacker.specEffect) {
			case 4:
				if (damage > 0) {
					int heal = (int) (damage * 0.50);
					int prayer = (int) (damage * 0.25);

					if (heal < 10) {
						heal = 10;
					}

					if (prayer < 5) {
						prayer = 5;
					}

					if (attacker.getHitpoints() + heal > attacker.getSkill().getLevelForExperience(Skill.HITPOINTS)) {
						attacker.getSkill().restoreLevel(Skill.HITPOINTS);
					} else {
						attacker.getSkill().incrementLevel(Skill.HITPOINTS, heal);
					}

					if (attacker.getSkill().getLevel(Skill.PRAYER) + prayer > attacker.getSkill().getLevelForExperience(Skill.PRAYER)) {
						attacker.getSkill().restoreLevel(Skill.PRAYER);
					} else {
						attacker.getSkill().incrementLevel(Skill.PRAYER, prayer);
					}
				}
				break;

			case 6:
				target.playGraphics(new Graphic(GraphicHeight.HIGH, 1194));
				break;

			case 8:
				target.playGraphics(new Graphic(GraphicHeight.HIGH, 1248));
				break;
			}
		}
	}

	public static void applySmite(Player player, Player target, int damage) {
		if (!player.getPrayers().isActive(NormalPrayer.SMITE, CursedPrayer.SOULSPLIT)) {
			return;
		}

		if (damage <= 0) {
			return;
		}

		target.getSkill().decrementLevel(Skill.PRAYER, damage / 4);

		if (target.getSkill().getLevel(Skill.PRAYER) <= 0) {
			target.getSkill().setLevel(Skill.PRAYER, 0);
			player.getPrayers().clear();
		}
	}

	public static void handleVengeance(EntityCharacter attacker, EntityCharacter target, int damage) {
		if (attacker == null || target == null || damage < 5) {
			return;
		}

		target.toPlayer().getEF().renewEffect(EffectConstants.VENGEANCE_NAME);

		target.speak("Taste vengeance!");

		if (target.getHitpoints() - damage > 0) {
			damage = (int) (damage * 0.75);

			if (damage > attacker.getHitpoints()) {
				damage = attacker.getHitpoints();
			}

			attacker.setDamage(new Damage(damage, HitIcons.NONE), target);
		}

		attacker.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

	}

	public static void handleRecoil(Player c, int dmg, EntityCharacter target) {

		if (c.getEquipment().get(Equipment.SLOT_RING) == null) {
			return;
		}
		boolean hasRecoil = c.getEquipment().get(Equipment.SLOT_RING).getId() == 2550;
		int recDamage = (int) (dmg * 0.1) + 1;
		if (!hasRecoil || dmg <= 0 || recDamage <= 0) {
			return;
		}
		if (recDamage > target.getHitpoints()) {
			recDamage = target.getHitpoints();
		}
		target.setDamage(new Damage(recDamage, HitMasks.REGULAR_HIT, HitIcons.DEFLECT), c);
	}

	public static void addExp(Player p, int damage) {
        if(p.getArpgProgress()!=null){if(!com.koios.game.arpg.MapRuns.inside(p))p.getArpgProgress().gain(damage);return;}
		switch (p.getCombat().getAttackType()) {
		case MELEE:
			switch (p.getCombat().getAttackStyle()) {
			case ACCURATE:
				p.getSkill().addExperience(0, damage * Constants.MELEE_EXP_RATE);
				p.getSkill().addExperience(3, damage * Constants.MELEE_EXP_RATE / 3);
				break;
			case AGGRESSIVE:
				p.getSkill().addExperience(2, damage * Constants.MELEE_EXP_RATE);
				p.getSkill().addExperience(3, damage * Constants.MELEE_EXP_RATE / 3);
				break;
			case CONTROLLED:
				p.getSkill().addExperience(0, damage * Constants.MELEE_EXP_RATE / 3);
				p.getSkill().addExperience(1, damage * Constants.MELEE_EXP_RATE / 3);
				p.getSkill().addExperience(2, damage * Constants.MELEE_EXP_RATE / 3);
				p.getSkill().addExperience(3, damage * Constants.MELEE_EXP_RATE / 3);
				break;
			case DEFENSIVE:
				p.getSkill().addExperience(1, damage * Constants.MELEE_EXP_RATE);
				p.getSkill().addExperience(3, damage * Constants.MELEE_EXP_RATE / 3);
				break;
			}
			break;
		case RANGE:
			switch (p.getCombat().getAttackStyle()) {
			case ACCURATE:
			case AGGRESSIVE:
				p.getSkill().addExperience(4, damage * Constants.RANGE_EXP_RATE);
				p.getSkill().addExperience(3, damage * Constants.RANGE_EXP_RATE / 3);
				break;
			case DEFENSIVE:
				p.getSkill().addExperience(1, damage * Constants.RANGE_EXP_RATE / 3);
				p.getSkill().addExperience(4, damage * Constants.RANGE_EXP_RATE / 3);
				p.getSkill().addExperience(3, damage * Constants.RANGE_EXP_RATE / 3);
				break;
			default:
				p.getSkill().addExperience(4, damage * Constants.RANGE_EXP_RATE);
				p.getSkill().addExperience(3, damage * Constants.RANGE_EXP_RATE / 3);
			}
			break;
		case MAGIC:
			p.getSkill().addExperience(4, damage * Constants.MAGIC_EXP_RATE);
			p.getSkill().addExperience(3, damage * Constants.MAGIC_EXP_RATE / 3);
			break;
		default:
			break;
		}
	}

	/**
	 * Handle the weapon's poison effect when used in combat.
	 *
	 * @param attacker the one attacking with a poisoned weapon
	 * @param target the one who will get poisoned
	 * @param type the type of attack
	 */
	public static void handlePoisoning(EntityCharacter attacker, EntityCharacter target, AttackType type) {
		if (attacker.isPlayer()) {
			Player p = attacker.toPlayer();

			int slot = type == AttackType.RANGE ? Equipment.SLOT_ARROWS : Equipment.SLOT_WEAPON;

			if (p.getEquipment().get(slot) == null) {
				return;
			}

			PoisonType poison = CombatData.getPoisonType(p.getEquipment().get(slot));

			if (poison == null || poison == PoisonType.NONE) {
				return;
			}

			if (NumberUtil.inclusiveRandom(4) != 1) {
				return;
			}

			target.getEF().queueEffect(new Poison(target, type == AttackType.RANGE ? poison.getRangeDamage() : poison.getMeleeDamage(), attacker));
		}
	}

	/**
	 * Returns the player's attack type.
	 *
	 * @param p
	 * @return
	 */
	public static AttackType getAttackType(Player p) {
		WeaponDefinitions weapon = p.getEquipment().get(3) != null ? weapon = WeaponDefinitions.getWeapon(p.getEquipment().get(3).getId()) : null;
		if (weapon != null) {
			return weapon.getAttackType();
		}
		return AttackType.MELEE;
	}

	/**
	 * Handles the chimchompa multi damage.
	 *
	 * @param player
	 * @param npcId
	 */
	public static void appendMultiChinchompa(Player player, int npcId) {
		Mob mob = MobManager.mobs[npcId];

		if (mob != null) {
			if (mob.isDead()) {
				return;
			}

			player.multiAttacking = true;
			int damage = CalculateHit.execute(player, mob, AttackType.RANGE);

			if (mob.hitpoints - damage < 0) {
				damage = mob.hitpoints;
			}
			mob.playGraphics(new Graphic(GraphicHeight.HIGH, RangedData.getRangeEndGFX(player)));
			mob.getCombat().setAggressor(player);
			mob.setDamage(new Damage(damage, HitIcons.RANGE), player);
		}
	}

	/**
	 * Creates the bolt graphic effect.
	 *
	 * @param player
	 * @param target
	 * @param gfx
	 * @param height100
	 */
	private static void createCombatGFX(Player player, EntityCharacter target, int gfx, boolean height100) {
		if (target == null) {
			return;
		}
		if (height100) {
			target.playGraphics(new Graphic(GraphicHeight.HIGH, gfx));
		} else {
			target.playGraphics(new Graphic(gfx));
		}
	}

	/**
	 * Handles the bolt's special effect.
	 *
	 * @param player
	 * @param target
	 */
	public static void crossbowSpecial(final Player player, EntityCharacter target) {
		if (player.lastArrowUsed == 9242) {
			createCombatGFX(player, target, 754, false);

			if (player.getHitpoints() - player.getHitpoints() / 20 < 1) {
				return;
			}

			player.setDamage(new Damage(player.getHitpoints() / 20, HitIcons.RANGE), player);

			if (player.getCombat().getTarget() != null) {
				player.getCombat().getTarget().setDamage(new Damage(player.getHitpoints() / 10, HitIcons.RANGE), player);
			}

			return;
		}

		if (player.getCombat().getTarget() != null) {

			player.crossbowDamage = 1.4;

			switch (player.lastArrowUsed) {
			case 9236: // Lucky Lightning
				createCombatGFX(player, target, 749, false);
				player.crossbowDamage = 1.25;
				break;
			case 9237: // Earth's Fury
				createCombatGFX(player, target, 755, false);
				break;
			case 9238: // Sea Curse
				createCombatGFX(player, target, 750, false);
				player.crossbowDamage = 1.10;
				break;
			case 9239: // Down to Earth
				createCombatGFX(player, target, 757, false);
				if (player.getCombat().getTarget().isPlayer()) {
					player.getCombat().getTarget().toPlayer().getSkill().decrementLevel(Skill.MAGIC, 2);
					player.getCombat().getTarget().toPlayer().sendMessage("Your magic has been lowered!");
				}
				break;
			case 9240: // Clear Mind
				createCombatGFX(player, target, 751, false);

				if (player.getCombat().getTarget().isPlayer()) {
					player.getCombat().getTarget().toPlayer().getSkill().decrementLevel(Skill.PRAYER, 2);
					player.getCombat().getTarget().toPlayer().sendMessage("Your prayer has been lowered!");
					player.getSkill().incrementLevel(Skill.PRAYER, 2);

					if (player.getSkill().getLevel(Skill.PRAYER) >= player.getSkill().getLevelForExperience(Skill.PRAYER)) {
						player.getSkill().restoreLevel(Skill.PRAYER);
					}
				}

				break;

			case 9241: // Magical Posion
				createCombatGFX(player, target, 752, false);

				if (player.getCombat().getTarget() != null) {
					player.getCombat().getTarget().getEF().queueEffect(new Poison(player.getCombat().getTarget(), 6, player));
				}

				break;

			case 9243: // Armour Piercing
				createCombatGFX(player, target, 758, true);
				player.crossbowDamage = 1.15;
				player.ignoreDefence = true;
				break;

			case 9244: // Dragon's Breath
				createCombatGFX(player, target, 756, false);
				if (player.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
					if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() != 1540 || player.getEquipment().get(Equipment.SLOT_SHIELD).getId() != 11283
							|| player.getEquipment().get(Equipment.SLOT_SHIELD).getId() != 11284) {
						player.crossbowDamage = 1.48;
					}
				}

				break;

			case 9245: // Life Leech
				createCombatGFX(player, target, 753, false);
				player.crossbowDamage = 1.15;

				player.getSkill().incrementLevel(Skill.HITPOINTS, player.boltDamage / 25);

				if (player.getHitpoints() >= 112) {
					player.getSkill().setLevel(Skill.HITPOINTS, 112);
				}
			}
		}
	}

	/**
	 * Handles the hand cannon explode chance.
	 *
	 * @param p
	 * @return if it should explode
	 */
	public static boolean handleHandCannonAttack(Player p) {
		int currentFmLvl = p.getSkill().getLevel(Skill.FIREMAKING);
		double calculate = currentFmLvl + NumberUtil.inclusiveRandom(100);
		int usingSpecialBonus = p.usingSpecial ? 20 : 0;
		int random = NumberUtil.inclusiveRandom(100) + usingSpecialBonus;

		boolean explode = calculate < random ? true : false;

		if (explode) {
			int explodeDamage = NumberUtil.inclusiveRandom(15) + 1;
			p.setDamage(new Damage(explodeDamage), p);
			p.playGraphics(new Graphic(2140));
			p.getEquipment().set(Equipment.SLOT_WEAPON, null);
			p.sendMessage("Your hand cannon malfunctions and explodes.");
			if (p.getCombat().getTarget() != null) {
				Combat.resetCombat(p);
				p.getMovementHandler().reset();
			}
			return true;
		}
		return false;
	}

	public static void appendMultiBarrage(Player player, Player other, boolean splashed) {
		for (Player surrounding : GameService.getWorld().getPlayers()) {

			if (surrounding.isDead()) {
				return;
			}

			if (checkMultiBarrageReqs(player, other)) {
				player.barrageCount++;

				if (!player.magicFailed) {
					if (MagicData.getEndGfxHeight(player) == 100) { // end GFX
						surrounding.playGraphics(new Graphic(GraphicHeight.HIGH, MagicData.MAGIC_SPELLS[player.oldSpellId][5]));
					} else {
						surrounding.playGraphics(new Graphic(MagicData.MAGIC_SPELLS[player.oldSpellId][5]));
					}
					int damage = NumberUtil.inclusiveRandom(CalculateHit.execute(player, surrounding, AttackType.MAGIC));
					if (surrounding.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC)) {
						damage *= (int) .60;
					}
					if (surrounding.getSkill().getLevel(3) - damage < 0) {
						damage = surrounding.getSkill().getLevel(3);
					}
					player.getSkill().addExperience(6, MagicData.MAGIC_SPELLS[player.oldSpellId][7] + damage * Constants.MAGIC_EXP_RATE);
					player.getSkill().addExperience(3, MagicData.MAGIC_SPELLS[player.oldSpellId][7] + damage * Constants.MAGIC_EXP_RATE / 3);
					other.setDamage(new Damage(damage, HitIcons.MAGIC), player);
					multiSpellEffect(player, other, damage);
				} else {
					surrounding.playGraphics(new Graphic(GraphicHeight.HIGH, 85));
				}
			}
		}
	}

	public static void appendMultiBarrageNPC(Player player, int npcId, boolean splashed) {
		if (MobManager.mobs[npcId] != null) {
			Mob n = MobManager.mobs[npcId];

			if (n.isDead()) {
				return;
			}

			if (checkMultiBarrageReqsNPC(npcId)) {
				player.barrageCount++;
				player.multiAttacking = true;
				MobManager.mobs[npcId].getCombat().setAggressor(player);

				int damage = NumberUtil.inclusiveRandom(CalculateHit.execute(player, MobManager.mobs[npcId], AttackType.MAGIC));

				if (damage > 0) {
					if (MagicData.getEndGfxHeight(player) == 100) {
						// end GFX
						n.playGraphics(new Graphic(GraphicHeight.HIGH, MagicData.MAGIC_SPELLS[player.oldSpellId][5]));
					} else {
						n.playGraphics(new Graphic(MagicData.MAGIC_SPELLS[player.oldSpellId][5]));
					}

					if (MobManager.mobs[npcId].hitpoints - damage < 0) {
						damage = MobManager.mobs[npcId].hitpoints;
					}

					MobManager.mobs[npcId].setDamage(new Damage(damage, HitIcons.MAGIC), player);
					multiSpellEffectNPC(player, npcId, damage);

					if (player.inBarbDef()) {
						player.barbarianDamage += damage;
					}

					multiSpellEffectNPC(player, npcId, damage);
				} else {
					n.playGraphics(new Graphic(GraphicHeight.HIGH, 85));
				}
			}
		}
	}

	private static boolean checkMultiBarrageReqs(Player player, Player other) {
		if (other.getIndex() == player.getIndex()) {
			return false;
		}

		if (!other.inWild() || other.isSkilling[Skill.AGILITY]) {
			return false;
		}

		if (Constants.COMBAT_LEVEL_DIFFERENCE) {
			int combatDif1 = MeleeRequirements.getCombatDifference(player.getSkill().getCombatLevel(false), other.getSkill().getCombatLevel(false));

			if (combatDif1 > player.wildLevel || combatDif1 > other.wildLevel) {
				player.sendMessage("Your combat level difference is too great to attack that player here.");
				return false;
			}
		}

		if (!other.inMulti()) {
			// single combat zones
			if (other.getCombat().getAggressor() != null) {
				if (other.getCombat().getAggressor().getIndex() != player.getIndex()) {
					return false;
				}
			}
			if (player.getCombat().getAggressor() != null) {
				if (other.getIndex() != player.getCombat().getAggressor().getIndex()) {
					player.sendMessage("You are already in combat.");
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkMultiBarrageReqsNPC(int i) {
		if (MobManager.mobs[i] == null) {
			return false;
		} else {
			return true;
		}
	}

	private static void multiSpellEffect(Player player, Player other, int damage) {
		switch (MagicData.MAGIC_SPELLS[player.oldSpellId][0]) {
		case 22168:
		case 21746:
			if (damage > 0) {
				if (other.miasmicCloud <= -1) {
					other.sendMessage("A cloud of Miasma slow's your attacks down");
					other.miasmicCloud = 80;
					other.getTaskScheduler().schedule(new MiasmicEffectTask(other));
				}
			}
			break;

		case 13011:
		case 13023:
			if (System.currentTimeMillis() - other.reduceStat > 35000) {
				other.reduceStat = System.currentTimeMillis();
				other.getSkill().decrementLevel(0, other.getSkill().getLevelForExperience(0) * 10 / 100);

			}
			break;

		case 12919: // blood spells
		case 12929:
			int heal = damage / 4;

			if (player.getSkill().getLevel(Skill.HITPOINTS) + heal >= player.getSkill().getLevelForExperience(Skill.HITPOINTS)) {
				player.getSkill().setLevel(Skill.HITPOINTS, player.getSkill().getLevelForExperience(Skill.HITPOINTS));
			} else {
				player.getSkill().incrementLevel(Skill.HITPOINTS, heal);
			}
			break;

		case 12891:
		case 12881:
			if (!other.getEF().isEffectListed(EffectConstants.FREEZE_NAME)) {
				other.getEF().queueEffect(new FreezeEffect(other, MagicData.getFreezeTime(player.oldSpellId), "A magical force stops you from moving."));
				other.getMovementHandler().reset();
			}
		}
	}

	private static void multiSpellEffectNPC(Player player, int npcId, int damage) {
		switch (MagicData.MAGIC_SPELLS[player.oldSpellId][0]) {
		case 12891:
		case 12881:
			if (!MobManager.mobs[npcId].getEF().isEffectListed(EffectConstants.FREEZE_NAME)) {
				MobManager.mobs[npcId].getEF().queueEffect(new FreezeEffect(MobManager.mobs[npcId], MagicData.getFreezeTime(player.oldSpellId), ""));
			}
		}
	}

	public static void createPlayersProjectile(int x, int y, int z, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time,
			int slope) {
		for (Player p : GameService.getWorld().getPlayers()) {
			if (p.getZ() == z) {
				if (p.distanceToPoint(x, y) <= 25) {
					ActionSender.createProjectile(p, x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
				}
			}
		}
	}

}
