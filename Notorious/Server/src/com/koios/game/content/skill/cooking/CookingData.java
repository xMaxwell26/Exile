package com.koios.game.content.skill.cooking;

import java.util.HashMap;
import java.util.Map;

public class CookingData {

	/**
	 * Handle Foods that will be Cooked on Range.
	 */
	public enum CookItemData {

		/**
		 * Uncooked Fishes **
		 */

		CRAFISH(1, 13435, 30, 33, 13433, 13437), SHRIMPS(1, 317, 33, 33, 315, 323), ANCHOVIES(1, 321, 30, 34, 319, 323), SARDINE(1, 327, 40, 35, 325, 367), POISONKARAMBWAN(1, 3146,
				80, 20, 3147, 3148), KARAMBWANJI(1, 3150, 10, 100, 3151, 592), GIANTCARP(1, 338, 3, 100, 337, 343), HERRING(5, 345, 50, 41, 347, 357), MACKEREL(10, 353, 60, 45,
						355, 357), TROUT(15, 335, 77, 50, 333, 343), COD(18, 341, 75, 52, 339, 343), PIKE(20, 349, 80, 64, 351, 343), SALMON(25, 331, 90, 58, 329,
								343), KARAMBWAN(30, 3142, 209, 100, 3144, 3148), TUNA(30, 359, 100, 65, 361, 367), RAINBOW(35, 10138, 121, 62, 10136, 10140), LOBSTER(40, 377, 120,
										74, 379, 381), BASS(43, 363, 130, 80, 365, 367), SWORDFISH(45, 371, 140, 86, 373, 375), MONKFISH(62, 7944, 150, 90, 7946,
												7948), SEATURTLE(82, 395, 211.3, 100, 397, 399), SHARK(80, 383, 210, 100, 385, 387), CAVEFISH(88, 15264, 214, 100, 15266,
														15268), MANTARAY(91, 389, 216, 100, 391, 393), ROCKTAIL(93, 15270, 225, 100, 15272, 15274),

		/**
		 * Uncooked Pie *
		 */

		REDBERRY(10, 2321, 78, 100, 2325, 2329), MEAT(20, 2319, 140, 100, 2327, 2329), MUD(29, 7168, 128, 100, 7170, 2329), APPLE(30, 2317, 130, 100, 2323, 2329), GARDEN(34, 7176,
				138, 100, 7178,
				2329), FISH(47, 7186, 164, 100, 7188, 2329), ADMIRAL(70, 7196, 210, 100, 7198, 2329), WILD(85, 7206, 240, 100, 7208, 2329), SUMMER(95, 7216, 260, 100, 7218, 2329),

		/**
		 * Uncooked Cake *
		 */

		CAKE(40, 1889, 180, 100, 1891, 1903), FISHCAKE(31, 7529, 100, 100, 7530, 7531),

		/**
		 * Uncooked Meat *
		 */

		TURKEY(1, 14539, 1, 1, 14550, 14541), TURKEYDRUMSTICK(1, 14542, 1, 1, 14543, 14544), COW(1, 2132, 30, 10, 2142, 2146), BEAR(1, 2134, 30, 10, 2142, 2146), RAT(1, 2136, 30,
				10, 2142, 2146), CHICKEN(1, 2138, 30, 10, 2140, 2144), RABBIT(1, 3226, 30, 10, 3228, 7222), YAK(1, 10816, 30, 10, 2142, 2146), BIRD(11, 9978, 62, 28, 9980,
						9982), BEAST(21, 9986, 82, 38, 9988, 9990), CRAB(21, 7518, 100, 40, 7521, 7520), CRAB2(21, 7519, 100, 40, 7521, 7520), CHOMPY(30, 2876, 140, 58, 2878,
								7226), JUBBLY(41, 7566, 160, 64, 7568, 7570),

		/**
		 * Uncooked Gnome Food *
		 */

		GNOMEBOWL(1, 2178, 30, 15, 2177, 2175), CRUNCHIES(1, 2202, 30, 15, 2201, 2199), BATTA(1, 2250, 30, 15, 2249, 2247), STEW(25, 2001, 117, 58, 2003, 2005), CURRY(60, 2009,
				280, 74, 2011, 2013),

		/**
		 * Uncooked Dungeoneering Food *
		 */

		CAVEPOTATO(1, 17817, 1, 100, 18093, 6701), HEIMCRAB(1, 17797, 9, 100, 18159, 18179), REDEYE(1, 17799, 27, 100, 18161, 18181), DUSKEEL(1, 17801, 45, 100, 18163,
				18183), GIANTFLATFISH(1, 17803, 63, 100, 18165, 18185), SHORTFINNEDEEL(1, 17805, 81, 100, 18167, 18187), WEBSNIPPER(1, 17807, 124, 100, 18169, 18189), BOULDABASS(1,
						17809, 136, 100, 18171,
						18191), SALVEEEL(1, 17811, 148, 100, 18173, 18193), BLUECRAB(1, 17813, 160, 100, 18175, 18195), CAVEMORAY(1, 17815, 172, 100, 18177, 18197),

		/**
		 * Other Food *
		 */

		BREAD(1, 2307, 40, 15, 6699, 6701), POTATO(7, 1942, 40, 20, 2309, 2311), THINSNAIL(12, 3363, 70, 100, 3369, 3375), EGG(13, 7076, 50, 25, 7078, 7090), SPIDERONSTICK(16,
				6293, 80, 100, 6297, 6301), SPIDERONSHAFT(16, 6295, 80, 100, 6299, 6301), LEANSNAIL(17, 3365, 80, 100, 3371, 3375), FATSNAIL(22, 3365, 95, 100, 3373,
						3375), SLIMYEEL(28, 3379, 95, 100, 3381, 3383), SWEETCORN(28, 5986, 104, 100, 5988, 5990), PIZZA(35, 2287, 143, 68, 2289, 2305), CAVEEEL(38, 5001, 115, 100,
								5003, 5002), ONION(42, 1957, 60, 100, 7084, 7092), SLICEDMUSHROOMS(46, 7080, 60, 100, 7082, 7094), HALFBOWL1(1, 9558, 30, 1, 9560,
										9560), HALFBOWL2(1, 9561, 30, 1, 9562, 9562), HALFBOWL3(40, 9559, 105, 1, 2187, 2187);

		/**
		 * Int to Handle Data. *
		 */

		public int levelRequeriment, uncookedFoodId, levelToStopBurning, cookedFoodId, burntFoodId;

		/**
		 * Double to Handle Exp Gained. *
		 */

		public double expGained;

		static Map<Integer, CookItemData> cookItemData = new HashMap<>();

		static {
			for (CookItemData c : values()) {
				cookItemData.put(c.getUncookedFoodId(), c);
			}
		}

		private CookItemData(int levelReq, int rawFoodId, double xp, int levelToStopBurning, int cookedFoodId, int burntFoodId) {
			levelRequeriment = levelReq;
			uncookedFoodId = rawFoodId;
			expGained = xp;
			this.levelToStopBurning = levelToStopBurning;
			this.cookedFoodId = cookedFoodId;
			this.burntFoodId = burntFoodId;
		}

		public static CookItemData forFood(int foodId) {
			return cookItemData.get(foodId);
		}

		public int getLevelReq() {
			return levelRequeriment;
		}

		public int getUncookedFoodId() {
			return uncookedFoodId;
		}

		public double getExp() {
			return expGained;
		}

		public int getLevelToStopBurning() {
			return levelToStopBurning;
		}

		public int getCookedFoodId() {
			return cookedFoodId;
		}

		public int getBurntFoodId() {
			return burntFoodId;
		}

	}

	public enum CookItemOnItemData {

		BUTTER_POTATO(39, 1942, 6697, 6703, 95.5);

		public int levelReq, firstItem, secondItem, finalItem;
		public double expGained;

		static Map<Integer, CookItemOnItemData> cookItemOnItemData = new HashMap<>();

		static {
			for (CookItemOnItemData c : values()) {
				cookItemOnItemData.put(c.getFirstItemId(), c);
				cookItemOnItemData.put(c.getSecondItemId(), c);
			}
		}

		private CookItemOnItemData(int levelReq, int firstItem, int secondItem, int finalItem, double expGained) {
			this.levelReq = levelReq;
			this.firstItem = firstItem;
			this.secondItem = secondItem;
			this.finalItem = finalItem;
			this.expGained = expGained;
		}

		public static CookItemOnItemData forId(int foodId) {
			return cookItemOnItemData.get(foodId);
		}

		public int getFirstItemId() {
			return firstItem;
		}

		public int getSecondItemId() {
			return secondItem;
		}

	}

}
