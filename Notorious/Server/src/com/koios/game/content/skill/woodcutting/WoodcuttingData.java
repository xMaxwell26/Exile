package com.koios.game.content.skill.woodcutting;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles woodcutting variables
 *
 * @author Gabbe
 */
public class WoodcuttingData {

	public enum HatchetData {

		ADDY(31, 1357, 6, 869), BLACK(6, 1361, 4, 873), BRONZE(1, 1351, 1, 879), DRAGON(61, 6739, 8, 2846), INFERNO_ADZE(61, 13661, 8, 10251), IRON(1, 1349, 2, 877), MITH(21, 1355,
				5, 871), RUNE(41, 1359, 7, 867), STEEL(6, 1353, 3, 875);

		private static final Map<Integer, HatchetData> axeData = new HashMap<>();

		static {
			for (HatchetData a : values()) {
				axeData.put(a.getAxeId(), a);
			}
		}

		public static HatchetData forAxe(int axeId) {
			return axeData.get(axeId);
		}

		private final int levelRequired, axeId, axeAnimation;

		private HatchetData(int levelReq, int axeId, int timer, int animId) {
			levelRequired = levelReq;
			this.axeId = axeId;
			axeAnimation = animId;
		}

		public int getAxeAnimation() {
			return axeAnimation;
		}

		public int getAxeId() {
			return axeId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

	}

	public enum TreeData {

		MAGIC(new int[] { 1306 }, 7401, 75, 250, 1513, 150, 3, 303), MAPLE(new int[] { 1307 }, 1343, 45, 100, 1517, 48, 8, 135), OAK(new int[] { 1281, 38731 }, 1356, 15, 38, 1521,
				45, 20, 60), TREE(new int[] { 1276, 1278, 1282, 1286, 1315, 1316, 1383 }, 1342, 1, 25, 1511, 45, 100, 40), WILLOW1(
						new int[] { 1308, 139, 2409, 5551, 5552, 5553, 38627, 38760, 38783, 38785 }, 7399, 30, 68, 1519, 45, 8,
						90), YEW(new int[] { 1309 }, 7402, 60, 175, 1515, 79, 5, 202);

		public static TreeData forTree(int treeId) {
			for (TreeData r : TreeData.values()) {
				for (int x = 0; x < r.getTreeId().length; x++) {
					if (treeId == r.getTreeId()[x]) {
						return r;
					}
				}
			}

			return null;
		}

		private final int stumpId, levelReq, experienceGained, logId, timer, stumpChance;
		private final int[] treeId;

		private TreeData(int[] treeId, int stumpId, int levelReq, int experienceGained, int logId, int timer, int stumpChance, int firemakingExperience) {
			this.treeId = treeId;
			this.stumpId = stumpId;
			this.levelReq = levelReq;
			this.experienceGained = experienceGained;
			this.logId = logId;
			this.timer = timer;
			this.stumpChance = stumpChance;
		}

		public int getExperienceGained() {
			return experienceGained;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getLogId() {
			return logId;
		}

		public int getMisc() {
			return stumpChance;
		}

		public int getStumpId() {
			return stumpId;
		}

		public int getTimer() {
			return timer;
		}

		public int[] getTreeId() {
			return treeId;
		}

	}

}
