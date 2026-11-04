package com.koios.game.content.combat;

import com.koios.Constants;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.ApplyMagicDamage;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.magic.MagicRequirements;
import com.koios.game.content.combat.melee.ApplyMeleeDamage;
import com.koios.game.content.combat.melee.MeleeData;
import com.koios.game.content.combat.melee.MeleeRequirements;
import com.koios.game.content.combat.mob.ApplyDragonFireDamage;
import com.koios.game.content.combat.mob.MobAttackManager;
import com.koios.game.content.combat.ranged.ApplyRangeDamage;
import com.koios.game.content.combat.ranged.RangedData;
import com.koios.game.content.combat.special.SpecialHandler;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.minigames.misc.TzhaarFightCaveConstants;
import com.koios.game.def.ItemDefinition;
import com.koios.game.def.WeaponDefinitions;
import com.koios.game.def.WeaponDefinitions.RangeTypes;
import com.koios.game.map.path.StraightPathFinder;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.task.SkullTimerTask;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * Handle the Entity vs Entity combat system ( Combat start > damage )
 *
 * @author Gabriel | Wolfs Darker
 */
public class Combat {

	/**
	 * Starts the combat ( check for if it is possible and executes it )
	 *
	 * @param attacker
	 * @param target
	 */
	public static void startAttack(EntityCharacter attacker, EntityCharacter target) {
        if(attacker.isPlayer() && target!=null && target.isPlayer() && com.koios.game.arpg.ArpgMelee.enabled(attacker.toPlayer())){resetCombat(attacker);return;}

		if (target == null || attacker.isDead() || target.isDead() || attacker.getZ() != target.getZ() || target.isTeleporting() || attacker.isTeleporting()) {
			resetCombat(attacker);
			return;
		}

		if (target.getCombat().getAggressor() != null && !target.inMulti()) {
			if (target.getCombat().getAggressor().getIndex() != attacker.getIndex()) {
				attacker.getCombat().setTarget(null);
				if (attacker.isPlayer()) {
					attacker.toPlayer().sendMessage("This monster is already in combat.");
					attacker.toPlayer().getMovementHandler().reset();
				}
				resetCombat(attacker);
				return;
			}
		}

		if (attacker.getCombat().getAggressor() != null && !target.inMulti()) {
			if (attacker.getCombat().getAggressor().getIndex() != target.getIndex()) {
				Combat.resetCombat(attacker);
				if (attacker.isPlayer()) {
					attacker.toPlayer().sendMessage("I am already under attack.");
					attacker.toPlayer().getMovementHandler().reset();
				}
				resetCombat(attacker);
				return;
			}
		}

		if (Location.getManhattanDistance(attacker.getLocation(),
				target.getLocation()) >= (attacker.isMob() && TzhaarFightCaveConstants.isTzhaarNpc(attacker.toMob().getId()) ? 150 : 25)) {
			resetCombat(attacker);
			return;
		}

		if (attacker.getCombat().getAttackType() == AttackType.MELEE) {
			if (!StraightPathFinder.isInteractionPathClear(attacker.getLocation(), target.getLocation())) {
				if (attacker.isMob() && target.isPlayer() && target.toPlayer().inChallenge() && attacker.isWithinDistance(target, 4)) {
					attacker.teleport(target.getLocation());
				}
				return;
			}
		} else {
			if (!StraightPathFinder.isProjectilePathClear(attacker.getLocation(), target.getLocation())) {
				if (attacker.isMob() && target.isPlayer() && target.toPlayer().inChallenge() && attacker.isWithinDistance(target, 4)) {
					attacker.teleport(target.getLocation());
				}
				return;
			}
		}

		if (target.isMob() && attacker.isPlayer()) {

			Mob mob = target.toMob();

			if (mob.isFamiliar() && attacker.isPlayer()) {
				Player player = attacker.toPlayer();
				Player summoner = mob.summoner == null ? null : mob.summoner.toPlayer();

				if (summoner == null) {
					resetCombat(attacker);
					return;
				}

				if (mob.inMulti()) {
					int combatDif1 = MeleeRequirements.getCombatDifference(summoner.getSkill().getCombatLevel(false), player.getSkill().getCombatLevel(false));

					if (combatDif1 > summoner.wildLevel || combatDif1 > player.wildLevel) {
						player.setFaceIndex(target.getIndex(), target.isPlayer());
						player.sendMessage("Your combat level difference is too great to attack that player's familiar here.");
						player.getMovementHandler().reset();
						Combat.resetCombat(player);
						return;
					}
				} else {
					player.sendMessage("You must be in multi combat areas to attack familiars.");
					Combat.resetCombat(player);
					return;
				}
			} else if (mob.summoner != null && mob.summoner.getIndex() != attacker.getIndex()) {
				if (attacker.isPlayer()) {
					attacker.toPlayer().sendMessage("This monster was not spawned for you.");
				}
				resetCombat(attacker);
				return;
			}
		}

		if (attacker.isMob()) {
			Mob mob = attacker.toMob();

			if (!attacker.getFollowing().isFollowing()) {
				attacker.getFollowing().setFollow(target, FollowType.COMBAT);
				return;
			}

			if (MobAttackManager.hasAttack(mob.getId())) {
				MobAttackManager.startAttack(mob, target);
				return;
			}

			MobManager.loadSpell(target, mob.getIndex());

			if (!CombatDistance.inCombatDistance(attacker, target, attacker.getCombat().getAttackType(), false)) {
				return;
			}

			if (mob.getCombat().getAttackType() == null) {
				mob.getCombat().setAttackType(AttackType.MELEE);
			}

			mob.getCombat().setAttackTimer(MobManager.getMobDelay(mob.getIndex()));
			mob.getCombat().setHitDelay(MobManager.getHitDelay(mob.getIndex()));

			if (mob.getCombat().getAttackType() == AttackType.DRAGON_FIRE) {
				mob.getCombat().setHitDelay(mob.getCombat().getHitDelay() + 2);
			}

			if (MobManager.multiAttacks(mob.getIndex())) {
				MobManager.multiAttackGfx(mob.getIndex());
				mob.playAnimation(new Animation(MobManager.getAttackEmote(mob.getIndex())));
				mob.getCombat().setOldTarget(target);
				return;
			}

			if (mob.projectileId > 0) {
				int nX = mob.getX() + MobManager.offset(mob.getIndex());
				int nY = mob.getY() + MobManager.offset(mob.getIndex());
				int pX = target.getX();
				int pY = target.getY();
				int offX = (nY - pY) * -1;
				int offY = (nX - pX) * -1;
				CombatExtras.createPlayersProjectile(nX, nY, mob.getZ(), offX, offY, 50, MobManager.getProjectileSpeed(mob.getIndex()), mob.projectileId, 43, 31,
						target.getProjetileIndex(), 65, 16);
			}

			MobManager.healRevenants(mob.getIndex());
			if (target.isPlayer()) {
				target.toPlayer().getSession().send(new SendRemoveInterfacePacket());
			}
			mob.getCombat().setOldTarget(target);
			mob.playAnimation(new Animation(MobManager.getAttackEmote(mob.getIndex())));
			target.getCombat().setAggressor(attacker);
			return;
		}

		if (attacker.getCombat().getAttackTime() <= 0) {

			Player player = attacker.toPlayer();

			if (target.isPlayer() && !MeleeRequirements.testRequirements(player)) {
				Combat.resetCombat(player);
				return;
			}

			player.getCombat().setAttackType(player.autocasting || player.spellCasting ? AttackType.MAGIC : CombatExtras.getAttackType(player));

			if (!attacker.getFollowing().isFollowing()) {
				attacker.getFollowing().setFollow(target, FollowType.COMBAT);
				return;
			}

			if (!CombatDistance.inCombatDistance(attacker, target, attacker.getCombat().getAttackType(), false)) {
				return;
			} else {
				attacker.getMovementHandler().reset();
			}

			player.getCombat().setWeaponUsed(WeaponDefinitions.getWeapon(player));

			WeaponDefinitions weaponUsed = player.getCombat().getWeaponUsed();

			player.getCombat().setAttackTimer(10);

			player.bonusAttack = 0;
			player.rangeItemUsed = 0;
			player.projectileStage = 0;

			if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
				if (player.getDuel().isRuleActive(DuelingRules.FUN_WEPAONS_ONLY)) {
					boolean canUseWeapon = false;

					for (int funWeapon : Constants.FUN_WEAPONS) {
						if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == funWeapon) {
							canUseWeapon = true;
						}
					}

					if (!canUseWeapon) {
						player.sendMessage("You can only use fun weapons in this duel!");
						Combat.resetCombat(player);
						return;
					}
				}
			}

			if (player.getEquipment().get(Equipment.SLOT_CAPE) != null) {
				if (target.isMob()) {
					if (target.toMob().getId() == 9463 && player.getEquipment().get(Equipment.SLOT_CAPE).getId() != 6570) {
						Combat.resetCombat(player);
						player.sendMessage("You must be wearing a fire cape to harm Ice strykewyrms.");
						return;
					}
				}
			}

			player.setFaceIndex(target.getIndex(), target.isPlayer());

			if (player.getCombat().getAttackType() != AttackType.MAGIC && player.usingSpecial) {
				if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
					player.sendMessage("Special attacks have been disabled during this duel!");
					player.usingSpecial = false;
					player.getItems().updateSpecialBar();
					Combat.resetCombat(player);
					return;
				}
			}

			switch (player.getCombat().getAttackType()) {
			case MELEE:

				if (player.getDuel().isRuleActive(DuelingRules.NO_MELEE)) {
					player.sendMessage("Melee has been disabled in this duel!");
					return;
				}

				if (target.isMob()) {
					Mob m = target.toMob();
					if (m.getId() == 6222 || m.getId() == 6225 || m.getId() == 6227) {
						Combat.resetCombat(player);
						player.sendMessage("You must use ranged or magic attacks to harm Kree'arra.");
						return;
					}

					if (Equipment.isWieldingBalmung(player)) {
						for (int dagannoth : Constants.DAGANNOTHS) {
							if (target.toMob().getId() == dagannoth) {
								player.playGraphics(new Graphic(GraphicHeight.HIGH, 2273));
							}
						}
					}
				}

                if(com.koios.game.arpg.ArpgMelee.enabled(player)){
                    com.koios.game.arpg.ArpgMelee.attack(player,target);return;
                }

				if (player.usingSpecial) {

					if (!SpecialHandler.activateSpecial(player, target)) {
						player.getCombat().setLastWeapon(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
						Combat.resetCombat(player);
					}

					return;
				}

				player.playAnimation(new Animation(MeleeData.getWepAnim(player)));

				player.getCombat().setLastWeapon(player.getEquipment().get(Equipment.SLOT_WEAPON) == null ? -1 : player.getEquipment().get(Equipment.SLOT_WEAPON).getId());

				player.getCombat().setHitDelay(MeleeData.getHitDelay(player));
				player.projectileStage = 0;
				player.getCombat().setOldTarget(target);

				break;
			case RANGE:

				if (weaponUsed == null || !player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
					resetCombat(attacker);
					return;
				}

				if (player.getDuel().isRuleActive(DuelingRules.NO_RANGED)) {
					player.sendMessage("Range has been disabled in this duel!");
					return;
				}

				if (player.inChallenge()) {
					Combat.resetCombat(player);
					player.sendMessage("You cannot use ranged attacks in the Champion's arena!");
					return;
				}

				if (player.inSW() && weaponUsed.getAttackType() == AttackType.RANGE) {
					Combat.resetCombat(player);
					player.sendMessage("You cannot use ranged attacks against the Spirit warriors!");
					return;
				}

				if (weaponUsed.getAttackType() == AttackType.RANGE && weaponUsed.getRangeType() == RangeTypes.NEED_AMMO
						&& !player.getEquipment().isSlotUsed(Equipment.SLOT_ARROWS)) {
					player.sendMessage("You have run out of arrows!");
					player.getMovementHandler().reset();
					player.getCombat().setTarget(null);
					return;
				}
				
				if (weaponUsed.getAttackType() == AttackType.RANGE && weaponUsed.getRangeType() == RangeTypes.NEED_WEAPON
						&& !player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
					Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
					if (weapon != null) {
						boolean knife = ItemDefinition.getName(weapon.getId()).toLowerCase().contains("knife");
						player.sendMessage("You have run out of " + (knife ? "knives" : "darts") + "!");
						player.getMovementHandler().reset();
						player.getCombat().setTarget(null);
						return;
					}
				}

				if (!RangedData.correcRangedAmmo(player)) {

					Item firearmId = player.getEquipment().get(Equipment.SLOT_WEAPON);
					Item ammoId = player.getEquipment().get(Equipment.SLOT_ARROWS);

					if (firearmId != null && ammoId != null) {
						String firearmName = firearmId.getDefinition().getName().toLowerCase();
						String ammoName = ammoId.getDefinition().getName().toLowerCase();
						player.sendMessage("You can't use " + ammoName + "s with a " + firearmName + ".");
					} else {
						player.sendMessage("You don't have the correct ammo for your weapon.");
					}
					player.getMovementHandler().reset();
					player.getCombat().setTarget(null);
					return;
				}

				if (player.usingSpecial) {

					if (!SpecialHandler.activateSpecial(player, target)) {
						player.getCombat().setLastWeapon(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
						Combat.resetCombat(player);
					}
					return;
				}

				player.getCombat().setLastWeapon(player.getEquipment().get(Equipment.SLOT_WEAPON) == null ? -1 : player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
				player.lastArrowUsed = player.getEquipment().get(Equipment.SLOT_ARROWS) == null ? -1 : player.getEquipment().get(Equipment.SLOT_ARROWS).getId();

				player.playAnimation(new Animation(MeleeData.getWepAnim(player)));

				if (!RangedData.canBeThrow(player)) {

					player.playGraphics(new Graphic(GraphicHeight.HIGH, RangedData.getRangeStartGFX(player)));
					player.getCombat().setHitDelay(CombatDistance.calculateDamageDelay(player.getLocation(), target.getLocation()) + 1);
					player.projectileStage = 1;

					player.getCombat().setOldTarget(target);

					if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() >= 4212 && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() <= 4223) {
						player.rangeItemUsed = player.getEquipment().get(Equipment.SLOT_WEAPON).getId();
						player.crystalBowArrowCount++;
						player.playGraphics(new Graphic(GraphicHeight.HIGH, RangedData.getRangeStartGFX(player)));
						player.lastArrowUsed = 0;
					} else {
						player.rangeItemUsed = player.getEquipment().get(Equipment.SLOT_ARROWS) == null ? 0 : player.getEquipment().get(Equipment.SLOT_ARROWS).getId();

						player.getItems().deleteArrow();

						if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11235 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15701
								|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15702 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15703
								|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15704) {
							player.getItems().deleteArrow();
						}

						player.playGraphics(new Graphic(GraphicHeight.HIGH, RangedData.getRangeStartGFX(player)));
					}

					RangedData.fireProjectile(player);
				} else {

					player.rangeItemUsed = player.getEquipment().get(Equipment.SLOT_WEAPON).getId();
					player.getItems().deleteEquipment();
					player.playGraphics(new Graphic(GraphicHeight.HIGH, RangedData.getRangeStartGFX(player)));
					player.lastArrowUsed = 0;
					player.getCombat().setHitDelay(MeleeData.getHitDelay(player));
					player.projectileStage = 1;
					player.getCombat().setOldTarget(target);
					RangedData.fireProjectile(player);
				}

				break;

			case MAGIC:

				if (player.autocasting) {
					player.spellId = player.autocastId;
					player.spellCasting = true;
				}

				if (player.spellId > 0) {
					player.spellCasting = true;
					player.getMovementHandler().reset();
				}

				if (player.getDuel().isRuleActive(DuelingRules.NO_MAGIC)) {
					player.sendMessage("Magic has been disabled in this duel!");
					Combat.resetCombat(player);
					return;
				}

				if (player.inChallenge() && player.spellCasting) {
					Combat.resetCombat(player);
					player.sendMessage("You cannot use magic attacks in the Champion's arena!");
					return;
				}

				if (player.inSW()) {
					Combat.resetCombat(player);
					player.sendMessage("You cannot use magic attacks against the Spirit warriors!");
					return;
				}

				if (player.spellCasting && !MagicRequirements.testSpellRequirements(player, player.spellId)) {
					player.getMovementHandler().reset();
					player.getCombat().setTarget(null);
					return;
				}

				player.playAnimation(new Animation(MagicData.MAGIC_SPELLS[player.spellId][2]));

				int pX = player.getX();
				int pY = player.getY();
				int nX = target.getX();
				int nY = target.getY();
				int offX = (pY - nY) * -1;
				int offY = (pX - nX) * -1;
				player.castingMagic = true;
				player.projectileStage = 2;
				player.getFollowing().setFollow(target, FollowType.COMBAT);

				if (MagicData.MAGIC_SPELLS[player.spellId][3] > 0) {
					if (MagicData.getStartGfxHeight(player) == 100) {
						player.playGraphics(new Graphic(GraphicHeight.HIGH, MagicData.MAGIC_SPELLS[player.spellId][3]));
					} else {
						player.playGraphics(new Graphic(MagicData.MAGIC_SPELLS[player.spellId][3]));
					}
				}

				if (MagicData.MAGIC_SPELLS[player.spellId][4] > 0) {
					CombatExtras.createPlayersProjectile(pX, pY, player.getLocation().getZ(), offX, offY, 50, 78, MagicData.MAGIC_SPELLS[player.spellId][4],
							MagicData.getStartHeight(player), MagicData.getEndHeight(player), target.getProjetileIndex(), 50, 16);
				}

				player.getCombat().setHitDelay(MeleeData.getHitDelay(player));
				player.getCombat().setOldTarget(target);
				player.oldSpellId = player.spellId;
				player.spellId = 0;
				player.setFaceIndex(target.getIndex(), target.isPlayer());

				if (!player.autocasting) {
					player.getCombat().setTarget(null);
					player.setFaceIndex(target.getIndex(), target.isPlayer());
				}

				break;
			default:
				break;
			}

			if (!player.inDuelArena() && !FightPits.getInstance().isParticipant(player) && target.isPlayer()) {
				if (!target.toPlayer().attackedPlayers.contains(player.getName())) {

					if (player.skullTimer <= 0) {
						player.attackedPlayers.add(target.getName());
						player.isSkulled = true;
						player.skullTimer = Constants.SKULL_TIMER;
						player.getTaskScheduler().schedule(new SkullTimerTask(player));
						player.skullIcon = 0;
						player.requestUpdates();
					}
				}
			}
		}
	}

	/**
	 * Applys the damage after a certain delay.
	 *
	 * @param attacker
	 * @param target
	 */
	public static void applyDamage(EntityCharacter attacker, EntityCharacter target) {
		if (target == null || attacker.isDead() || target.isDead() || attacker.getZ() != target.getZ() || target.isTeleporting() || attacker.isTeleporting()) {
			return;
		}

		if (attacker.isMob() && MobAttackManager.hasAttack(attacker.toMob().getId())) {
			MobAttackManager.applyDamage(attacker.toMob(), target);
			return;
		}

		switch (attacker.getCombat().getAttackType()) {
		case MELEE:

			if (attacker.isPlayer()) {

				if (attacker.toPlayer().ignoreHit) {
					attacker.toPlayer().ignoreHit = false;
					return;
				}

			}

			ApplyMeleeDamage.execute(attacker, target, false, CalculateHit.execute(attacker, target, AttackType.MELEE));

			if (attacker.isPlayer() && attacker.toPlayer().doubleHit) {
				ApplyMeleeDamage.execute(attacker, target, true, CalculateHit.execute(attacker, target, AttackType.MELEE));
			}

			break;

		case MAGIC:
			ApplyMagicDamage.execute(attacker, target);
			break;

		case RANGE:
			ApplyRangeDamage.execute(attacker, target);
			break;

		case DRAGON_FIRE:
		case DRAGON_FIRE_BALL:
			ApplyDragonFireDamage.execute(attacker, target);
			break;
		default:
			break;
		}
	}

	public static void resetCombat(EntityCharacter e) {
        if(e.isPlayer())e.toPlayer().getArpgAttackClock().reset();
		e.setFaceIndex(0, false);
		e.getCombat().setTarget(null);
		e.getCombat().setAttackType(AttackType.MELEE);
		if (e.getFollowing().isFollowing()) {
			e.getFollowing().reset();
		}
		if (e.isPlayer()) {
			e.toPlayer().spellCasting = false;
		}
	}

}