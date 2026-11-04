package com.koios.game.content.skill.fishing;

import java.util.HashMap;
import java.util.Map;

public class FishingData {

	/**
	 * Contains all data relevant to fishing values.
	 *
	 * @author Gabriel
	 */
	public enum Fishable {

		/**
		 * Shrimp
		 */
		SHRIMP(317, 303, 1, 10, -1), /**
										 * Crayfish
										 */
		CRAYFISH(13435, 13431, 1, 10, -1), /**
											 * Karambwanji
											 */
		KARAMBWANJI(3150, 303, 5, 5, -1), /**
											 * Sardine
											 */
		SARDINE(327, 307, 5, 20, 313),
		/* Herring */
		HERRING(345, 307, 10, 30, 313), /**
										 * Anchovies
										 */
		ANCHOVIES(321, 303, 15, 40, -1), /**
											 * Mackerel
											 */
		MACKEREL(353, 305, 16, 20, -1),
		/* Trout */
		TROUT(335, 309, 20, 50, 314), /**
										 * Cod
										 */
		COD(341, 305, 23, 45, -1),

		/**
		 * Pike
		 */
		PIKE(349, 307, 25, 60, 313),

		/**
		 * Slimy Eel
		 */
		SLIMY_EEL(3379, 307, 28, 65, 313), /**
											 * Salmon
											 */
		SALMON(331, 309, 30, 70, 314), /**
										 * Frog Spawn
										 */
		FROG_SPAWN(5004, 303, 33, 75, -1), /**
											 * Tuna
											 */
		TUNA(359, 311, 35, 80, -1), /**
									 * Cave Eel
									 */
		CAVE_EEL(5001, 307, 38, 80, 313), /**
											 * Lobster
											 */
		LOBSTER(377, 301, 40, 90, -1), /**
										 * Bass
										 */
		BASS(363, 305, 46, 100, -1), /**
										 * Sword Fish
										 */
		SWORD_FISH(371, 311, 50, 100, -1), /**
											 * Lava Eel
											 */
		LAVA_EEL(2148, 307, 53, 30, 313), /**
											 * Monk Fish
											 */
		MONK_FISH(7944, 303, 62, 120, -1), /**
											 * Karambwan
											 */
		KARAMBWAN(3142, 3157, 65, 100, -1), /**
											 * Shark
											 */
		SHARK(383, 311, 76, 110, -1), /**
										 * Sea Turtle
										 */
		SEA_TURTLE(395, -1, 79, 38, -1), /**
											 * Manta Ray
											 */
		MANTA_RAY(390, -1, 81, 46, -1),

		/**
		 * Cave Fish
		 */
		CAVE_FISH(15264, 307, 85, 300, 313),

		/**
		 * Cave Fish
		 */
		ROCKTAIL(15270, 307, 90, 380, 15263);

		public static Fishable forId(int rawFishId) {
			return values.get(rawFishId);
		}

		private final int rawFishId;

		private final int toolId;

		private final int levelRequired;

		private final int baitRequired;

		private final double experienceGain;

		private static final Map<Integer, Fishable> values = new HashMap<>();

		static {
			for (Fishable fishes : Fishable.values()) {
				values.put(fishes.getRawFishId(), fishes);
			}
		}

		/**
		 * @param rawFishId The id of the values you will catch
		 * @param toolId The id of the tool needed to catch the values
		 * @param levelRequired The level required to catch the values
		 * @param experienceGain The experience gained from catching the values.
		 */
		private Fishable(int rawFishId, int toolId, int levelRequired, double experienceGain, int baitRequired) {
			this.rawFishId = rawFishId;
			this.toolId = toolId;
			this.levelRequired = levelRequired;
			this.experienceGain = experienceGain;
			this.baitRequired = baitRequired;
		}

		public int getBaitRequired() {
			return baitRequired;
		}

		public double getExperience() {
			return experienceGain;
		}

		public int getRawFishId() {
			return rawFishId;
		}

		public int getRequiredLevel() {
			return levelRequired;
		}

		public int getToolId() {
			return toolId;
		}

	}

	/**
	 * Implement Fishing Skill.
	 *
	 * @author Gabbe
	 */
	public enum Spots {

		Net_Bait(new int[] { 316, 320, 323, 325, 326, 327, 330, 1331, 2067, 2068, 2724, 4908, 5748, 5749 }, 317, 321, 325, 345), Lure_Bait(
				new int[] { 309, 310, 311, 314, 315, 317, 318, 328, 329 }, 335, 331, 349, -1), Rainbow_Spot(new int[] { 927 }, 10318, -1, 349, -1), Cave_Fish_Rocktail(
						new int[] { 1189, 1190, 3019, 8647 }, 15264, -1, 15270, -1), Cage_Harpoon(new int[] { 312, 321, 324, 333, 1332, 1399, 3804, 5470, 7046 }, 377, -1, 359,
								371), Net_Harpoon(new int[] { 313, 322, 334, 1191, 1333, 1405, 3574, 3575, 5471, 7044 }, 341, 363, 383, -1);

		public static Spots forSpot(int spotId) {
			for (Spots s : Spots.values()) {
				for (int x = 0; x < s.getSpotId().length; x++) {
					if (spotId == s.getSpotId()[x]) {
						return s;
					}
				}
			}

			return null;
		}

		private final int[] spotId;
		private final int firstFish, secondFish, thirdFish, fourthFish;

		private Spots(int[] spotId, int firstFish, int secondFish, int thirdFish, int fourthFish) {
			this.spotId = spotId;
			this.firstFish = firstFish;
			this.secondFish = secondFish;
			this.thirdFish = thirdFish;
			this.fourthFish = fourthFish;
		}

		public int getFirstFishId() {
			return firstFish;
		}

		public int getFourthFishId() {
			return fourthFish;
		}

		public int getSecondFishId() {
			return secondFish;
		}

		public int[] getSpotId() {
			return spotId;
		}

		public int getThirdFishId() {
			return thirdFish;
		}

	}

	/**
	 * @author Joshua Barry <Arsenic>
	 * @author Dominick Jones <Jones>
	 */
	public enum Tools {

		/**
		 * Small Net
		 */
		SMALL_NET(303, 621, 6, new short[] { 317, 3150, 321, 5004, 7994 }),

		/**
		 * Big Net
		 */
		BIG_NET(305, 620, 6, new short[] { 353, 341, 363 }),

		/**
		 * Crayfish Cage [needs proper anim]
		 */
		CRAYFISH_CAGE(13431, 619, 5, new short[] { 13435 }),

		/**
		 * Fishing Rod
		 */
		FISHING_ROD(307, 622, 1, new short[] { 327, 345, 349, 3379, 5001, 2148 }),

		/**
		 * Fly Fishing Rod
		 */
		FLYFISHING_ROD(309, 622, 1, new short[] { 335, 331 }),

		/**
		 * Karambwan Pot (need to find animation)
		 */
		KARAMBWAN_POT(3157, -1, -1, new short[] { 3142 }),

		/**
		 * Harpoon
		 */
		HARPOON(311, 618, 5, new short[] { 359, 371 }),

		/**
		 * Lobster Pot
		 */
		LOBSTER_POT(301, 619, 5, new short[] { 377 });

		public static Tools forId(int id) {
			return values.get(id);
		}

		private final int toolId;
		private final int animation;
		private final int animCycles;
		private static final Map<Integer, Tools> values = new HashMap<>();

		static {
			for (Tools tool : Tools.values()) {
				values.put(tool.getToolId(), tool);
			}
		}

		/**
		 * @param toolId The item id of the tool we will be using to catch values.
		 * @param outcomes An array of item id's of possible reward outcomes.
		 */
		private Tools(int toolId, int animation, int animCycles, short[] outcomes) {
			this.toolId = toolId;
			this.animation = animation;
			this.animCycles = animCycles;
		}

		public int getAnimationId() {
			return animation;
		}

		public int getAnimCycles() {
			return animCycles;
		}

		public int getToolId() {
			return toolId;
		}

	}

}
