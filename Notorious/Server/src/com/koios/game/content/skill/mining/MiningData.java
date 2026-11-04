package com.koios.game.content.skill.mining;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles mining variables
 *
 * @author Gabbe
 */
public class MiningData {

	public enum PickaxeData {

		INFERNO_ADZE(41, 13661, 7, 10249), DRAGON(61, 15259, 7, 12188), RUNE(41, 1275, 6, 624), ADDY(31, 1271, 5, 628), MITH(21, 1273, 4, 629), STEEL(6, 1269, 3, 627), IRON(1,
				1267, 2, 626), BRONZE(1, 1265, 1, 625);

		public static PickaxeData forPickaxe(int pickId) {
			return values.get(pickId);
		}

		private final int levelRequired, pickId, pickAnimation;
		private final double pickDelay;
		private static final Map<Integer, PickaxeData> values = new HashMap<>();

		static {
			for (PickaxeData p : values()) {
				values.put(p.getPickId(), p);
			}
		}

		private PickaxeData(int levelReq, int pick, double timer, int animId) {
			levelRequired = levelReq;
			pickId = pick;
			pickDelay = timer;
			pickAnimation = animId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getPickAnimation() {
			return pickAnimation;
		}

		public double getPickDelay() {
			return pickDelay;
		}

		public int getPickId() {
			return pickId;
		}
	}

	public enum RocksData {

		COPPER(new int[] { 2090, 2091, 9708, 9709, 9710, 11936, 11937, 11938, 31080, 31081, 31082 }, new int[] { 450, 451 }, 1, 10, 436, 15), TIN(
				new int[] { 2311, 2094, 2095, 11933, 11934, 11935, 31077, 31078, 31079, 37304, 37306, 37305 }, new int[] {}, 1, 10, 438,
				15), SILVER(new int[] { 2100, 2101, 9714, 9715, 9716 }, new int[] {}, 20, 40, 442, 25), IRON(
						new int[] { 2092, 2093, 9717, 9718, 9719, 31071, 31072, 31073, 37309, 37307, 37308 }, new int[] {}, 15, 35, 440,
						20), COAL(new int[] { 11930, 11932, 2096, 2097, 14850, 31068, 31069, 31070 }, new int[] {}, 30, 50, 453, 30), GOLD(
								new int[] { 2098, 2099, 9711, 9720, 9722, 31066, 31065, 37312, 37310 }, new int[] {}, 40, 65, 444,
								40), MITHRIL(new int[] { 11944, 11942, 2102, 2103, 14853, 31086, 31088 }, new int[] {}, 55, 80, 447, 50), ADAMANTITE(
										new int[] { 11939, 11941, 2104, 2105, 14862, 31083, 31085 }, new int[] {}, 70, 95, 449, 60), RUNITE(new int[] { 2106, 2107, 14859, 14860 },
												new int[] {}, 70, 125, 451, 70), ESSENCE(new int[] { 2491 }, new int[] {}, 30, 15, 1436, 5);

		public static RocksData forRock(int rockId) {
			for (RocksData def : RocksData.values()) {
				for (int i = 0; i < def.getRockId().length; i++) {
					if (rockId == def.getRockId()[i]) {
						return def;
					}
				}
			}

			return null;
		}

		private final double experienceGained;
		private final int levelReq, oreId, timer;
		private final int[] objectId;

		private RocksData(int[] objectId, int[] emptyId, int levelReq, double experienceGained, int oreId, int timer) {
			this.objectId = objectId;
			this.levelReq = levelReq;
			this.experienceGained = experienceGained;
			this.oreId = oreId;
			this.timer = timer;
		}

		public double getExperienceGained() {
			return experienceGained;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getOreId() {
			return oreId;
		}

		public int[] getRockId() {
			return objectId;
		}

		public int getTimer() {
			return timer;
		}

	}

}
