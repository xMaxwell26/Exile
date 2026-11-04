package com.koios.game.content.combat;

import com.koios.game.def.WeaponDefinitions;
import com.koios.game.model.EntityCharacter;

/**
 * Handle the entity's combat variables
 *
 * @author Gabriel | Wolfs Darker
 */
public class CombatVariables {

	public enum AttackType {

		MELEE, RANGE, MAGIC, RECOIL, VENGEANCE, NO_ATTACK, OTHER, CORP_CORE, DRAGON_FIRE, DRAGON_FIRE_BALL;

		public static AttackType getAttackType(int i) {
			for (AttackType a : AttackType.values()) {
				if (a.ordinal() == i) {
					return a;
				}
			}
			return null;
		}

	}

	public enum AttackStyle {

		ACCURATE(0), AGGRESSIVE(1), DEFENSIVE(2), CONTROLLED(3);

		private final int index;

		private AttackStyle(int index) {
			this.index = index;
		}

		public int toInteger() {
			return index;
		}

		public static AttackStyle getAttackType(int index) {
			switch (index) {
			case 0:
				return ACCURATE;

			case 1:
				return AGGRESSIVE;

			case 2:
				return DEFENSIVE;

			case 3:
				return CONTROLLED;

			default:
				return ACCURATE;
			}
		}
	}

	private EntityCharacter oldTarget;

	private EntityCharacter target;

	private EntityCharacter aggressor;

	private EntityCharacter lastAttacked;

	private int attackTimer;

	private int hitDelay;

	private long lastHitDealt;
	
	private long lastDamageReceived;

	private long lastDamageApplied;

	private long attackAttempt;

	private AttackStyle attackStyle = AttackStyle.ACCURATE;

	private AttackType attackType = AttackType.MELEE;

	private WeaponDefinitions weapon_used = null;

	private int last_weapon = -1;

	public EntityCharacter getPreviousTarget() {
		return oldTarget;
	}

	public EntityCharacter getTarget() {
		return target;
	}

	public EntityCharacter getAggressor() {
		return aggressor;
	}

	public EntityCharacter getLastAttacked() {
		return lastAttacked;
	}

	public int getAttackTime() {
		return attackTimer;
	}

	public int getHitDelay() {
		return hitDelay;
	}
	
	public long getLastDamageReceived() {
		return lastDamageReceived;
	}

	public long getLastHitDealt() {
		return lastHitDealt;
	}

	public long getLastDamageApplied() {
		return lastDamageApplied;
	}

	public long getLastAttackAttempt() {
		return attackAttempt;
	}

	public AttackStyle getAttackStyle() {
		return attackStyle;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public WeaponDefinitions getWeaponUsed() {
		return weapon_used;
	}

	public int getLastWeapon() {
		return last_weapon;
	}

	public boolean canAttemptToAttack() {
		return System.currentTimeMillis() - attackAttempt >= 500;
	}

	public void setOldTarget(EntityCharacter e) {
		oldTarget = e;
	}

	public void setTarget(EntityCharacter e) {
		target = e;
	}

	public void setAggressor(EntityCharacter e) {
		aggressor = e;
	}

	public void setLastAttacked(EntityCharacter e) {
		lastAttacked = e;
	}

	public void setAttackTimer(int i) {
		attackTimer = i;
	}

	public void setHitDelay(int i) {
		hitDelay = i;
	}

	public void setLastHitDealt(long l) {
		lastHitDealt = l;
	}

	public void setLastDamageReceived(long l) {
		lastDamageReceived = l;
	}
	
	public void setLastDamageApplied(long l) {
		lastDamageApplied = l;
	}

	public void setAttackAttempt(long attackAttempt) {
		this.attackAttempt = attackAttempt;
	}

	public void setAttackStyle(AttackStyle style) {
		attackStyle = style;
	}

	public void setAttackType(AttackType type) {
		attackType = type;
	}

	public void setWeaponUsed(WeaponDefinitions w) {
		weapon_used = w;
	}

	public void setLastWeapon(int i) {
		last_weapon = i;
	}

	public boolean inCombat() {
		return target != null || oldTarget != null || aggressor != null;
	}

}
