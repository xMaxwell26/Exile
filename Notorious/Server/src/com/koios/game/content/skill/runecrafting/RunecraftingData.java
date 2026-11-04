package com.koios.game.content.skill.runecrafting;

import java.util.HashMap;
import java.util.Map;

public class RunecraftingData {

	public enum AltarData {

		AIR(556, 1, 5, 2478, 11), MIND(558, 1, 5.5, 2479, 14), WATER(555, 5, 6, 2480, 19), EARTH(557, 9, 6.5, 2481, 26), FIRE(554, 14, 7, 2482, 35), BODY(559, 20, 7.5, 2483,
				46), COSMIC(564, 27, 8, 2484, 59), CHAOS(562, 35, 8.5, 2487, 74), ASTRAL(9075, 40, 8.75, 17010, 82), NATURE(561, 44, 9, 2486, 91), LAW(563, 54, 9.5, 2485,
						-1), DEATH(560, 65, 10, 2488, -1), BLOOD(565, 77, 10.5, 30624, -1), SOUL(566, 88, 11, 7138, -1);

		public static AltarData forAltar(int objectId) {
			return values.get(objectId);
		}

		private final int runeId, levelReq, altarId, multiplier;

		private final double expReceived;

		private static final Map<Integer, AltarData> values = new HashMap<>();

		static {
			for (AltarData a : values()) {
				values.put(a.getAltar(), a);
			}
		}

		private AltarData(int runeId, int levelReq, double expReceived, int altarId, int multiplier) {
			this.runeId = runeId;
			this.levelReq = levelReq;
			this.expReceived = expReceived;
			this.altarId = altarId;
			this.multiplier = multiplier;
		}

		public int getAltar() {
			return altarId;
		}

		public double getExp() {
			return expReceived;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getMultiplier() {
			return multiplier;
		}

		public int getRuneId() {
			return runeId;
		}

	}

}
