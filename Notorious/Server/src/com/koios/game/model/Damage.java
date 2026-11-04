package com.koios.game.model;

/**
 * Handle the damage applied by entity
 *
 * @author Gabriel | Wolfs Darker.
 */
public class Damage {

	public static enum HitMasks {

		REGULAR_HIT, CRITICAL, POISON, PURPLE_LIGHT, PURPLE_DARK, YELLOW, YELLOW_DARK, YELLOW_LIGHT

	}

	public static enum HitIcons {

		MELEE, RANGE, MAGIC, DEFLECT, DWARF_CANNON, NONE

	}

	private int damage;
    private com.koios.game.arpg.DamageType type;
    private boolean attack;
    public Damage(int amount,com.koios.game.arpg.DamageType type,boolean attack){this(amount);this.type=type;this.attack=attack;}
    public com.koios.game.arpg.DamageType getType(){return type;}
    public boolean isAttack(){return attack;}
	private final HitMasks hitmask;
	private final HitIcons hitIcon;
	private final int aggressorIndex;

	public Damage(int damage) {
		this.damage = damage;
		hitmask = HitMasks.REGULAR_HIT;
		hitIcon = HitIcons.NONE;
		aggressorIndex = -1;
	}

	public Damage(int damage, HitMasks hitmark) {
		this.damage = damage;
		hitmask = hitmark;
		hitIcon = HitIcons.NONE;
		aggressorIndex = -1;
	}

	public Damage(int damage, HitIcons hiticon) {
		this.damage = damage;
		hitmask = HitMasks.REGULAR_HIT;
		hitIcon = hiticon;
		aggressorIndex = -1;
	}

	public Damage(int damage, HitMasks hitmark, HitIcons hiticon) {
		this.damage = damage;
		hitmask = hitmark;
		hitIcon = hiticon;
		aggressorIndex = -1;
	}

	public Damage(int damage, HitMasks hitmark, HitIcons hiticon, int aggressorIndex) {
		this.damage = damage;
		hitmask = hitmark;
		hitIcon = hiticon;
		this.aggressorIndex = aggressorIndex;
	}

	public int getDamage() {
		return damage;
	}

	public HitMasks getHitMask() {
		return hitmask;
	}

	public HitIcons getHitIcon() {
		return hitIcon;
	}

	public int getAggressorIndex() {
		return aggressorIndex;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

}
