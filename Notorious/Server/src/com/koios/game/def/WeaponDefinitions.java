package com.koios.game.def;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.player.Player;
import com.koios.util.XStreamLibrary;

public class WeaponDefinitions {

	public enum RangeTypes {

		NEED_AMMO, NEED_WEAPON, NO_AMMO

	}

	public static class Special {

		private int anim;
		private boolean doubleDamage;
		private int drainAmount;
		private int gfx;
		private GraphicHeight gfxHeight;
		private boolean hasDifSpec;
		private int opponentGfx;
		private GraphicHeight opponentHeight;
		private int specialProjectile;

		public int getAnimation() {
			return anim;
		}

		public GraphicHeight getGfxHeight() {
			return gfxHeight;
		}

		public int getGraphic() {
			return gfx;
		}

		public int getOpponentGfx() {
			return opponentGfx;
		}

		public GraphicHeight getOpponentGfxHeight() {
			return opponentHeight;
		}

		public int getSpecialProjectile() {
			return specialProjectile;
		}

		public int getSpecialReq() {
			return drainAmount;
		}

		public boolean hasDifSpec() {
			return hasDifSpec;
		}

		public boolean isDoubleDamage() {
			return doubleDamage;
		}

	}

	private static Map<Integer, WeaponDefinitions> weapons;

	public static WeaponDefinitions getWeapon(int id) {
		return weapons.get(id);
	}

	public static WeaponDefinitions getWeapon(Player p) {
		return p.getEquipment().isSlotUsed(3) ? weapons.get(p.getEquipment().get(3).getId()) : null;
	}

	@SuppressWarnings("unchecked")
	public static void loadWeapons() throws FileNotFoundException {
		weapons = new HashMap<>();
		List<WeaponDefinitions> loaded = (ArrayList<WeaponDefinitions>) XStreamLibrary.load("./data/definitions/weapon_definitions.xml");

		for (WeaponDefinitions weapon : loaded) {
			for (int id : weapon.getId()) {
				weapons.put(id, weapon);
			}
		}
	}

	private final int[] attackEmotes = new int[4];
	private AttackType attackType;
	private int blockEmote;
	private boolean doubleDamage;
	private final int[] equipEmotes = new int[7];
	private GraphicHeight gfxHeight;
	private int interfaceId;
	private RangeTypes rangeType;
	private int specBar;
	private Special special;
	private int speed;
	private int[] weaponIds;

	public int get180Turn() {
		return equipEmotes[6];
	}

	public int get90CCWTurn() {
		return equipEmotes[5];
	}

	public int get90CWTurn() {
		return equipEmotes[4];
	}

	public int getAttackEmote(int fightStyle) {
		return attackEmotes[fightStyle];
	}

	public int getAttackSpeed() {
		return speed;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public int getBlockEmote() {
		return blockEmote;
	}

	public GraphicHeight getGfxHeight() {
		return gfxHeight;
	}

	public int[] getId() {
		return weaponIds;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public RangeTypes getRangeType() {
		return rangeType;
	}

	public int getRunEmote() {
		return equipEmotes[2];
	}

	public int getSpecBarId() {
		return specBar;
	}

	public Special getSpecial() {
		return special;
	}

	public int getStandEmote() {
		return equipEmotes[0];
	}

	public int getTurnEmote() {
		return equipEmotes[3];
	}

	public int getWalkEmote() {
		return equipEmotes[1];
	}

	public boolean isDoubleDamage() {
		return doubleDamage;
	}

}
