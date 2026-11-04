package com.koios.game.content.skill.fletching;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements Fletching.
 *
 * @author Gabriel ( Wolfs Darker )
 */

public class FletchingData {

	public enum ArrowData {

		HEADLESS_ARROW(1, 314, 52, 1, 53), BRONZE_ARROW(1, 53, 39, 2.6, 882), DRAGON_ARROW(90, 53, 11237, 16.3, 11212), IRON_ARROW(15, 53, 40, 3.8, 884), MITHIRIL_ARROW(45, 53, 42,
				8.8, 888), ADAMANT_ARROW(60, 53, 43, 10, 890), RUNE_ARROW(75, 53, 44, 13.8, 892), STEEL_ARROW(30, 53, 41, 6.3, 886);

		public static ArrowData forArrow(int itemId) {
			return values.get(itemId);
		}

		private static final Map<Integer, ArrowData> values = new HashMap<>();

		static {
			for (ArrowData a : values()) {
				values.put(a.getHeadId(), a);
			}
		}

		private final double expReceived;
		private final int levelReq, arrowHeadId, arrowId, shaftId;

		private ArrowData(int levelReq, int shaftId, int arrowHeadId, double expReceived, int arrowId) {
			this.levelReq = levelReq;
			this.shaftId = shaftId;
			this.arrowHeadId = arrowHeadId;
			this.expReceived = expReceived;
			this.arrowId = arrowId;
		}

		public int getArrowId() {
			return arrowId;
		}

		public int getShaftId() {
			return shaftId;
		}

		public double getExpReceived() {
			return expReceived;
		}

		public int getHeadId() {
			return arrowHeadId;
		}

		public int getLevelReq() {
			return levelReq;
		}

	}

	public enum BoltData {

		ADAMANT_BOLT(61, 9380, 314, 7, 9143), BLURITE_BOLT(24, 9376, 314, 1.6, 9139), BRONZE_BOLT(9, 9375, 314, 0.5, 877), DIAMOND_BOLT(65, 9192, 9143, 7, 9340), DRAGON_BOLT(71,
				9193, 9144, 8.2, 9341), EMERALD_BOLT(58, 9190, 9142, 5.5, 9338), IRON_BOLT(39, 9377, 314, 1.5, 9140), JADE_BOLT(26, 9187, 9139, 1.6, 9335), MITHIRIL_BOLT(54, 9379,
						314, 5, 9142), ONYX_BOLT(73, 9194, 9144, 9.4, 9342), OpalBolt(11, 877, 9139, 1.6, 879), PEARL_BOLT(41, 9140, 46, 3.2, 880), RUBY_BOLT(63, 9191, 9143, 6.3,
								9339), RUNITE_BOLT(69, 9381, 314, 10, 9144), SAPPHIRE_BOLT(56, 9189, 9142, 4.7, 9337), SILVER_BOLT(43, 9382, 314, 2.5, 9145), STEEL_BOLT(46, 9378,
										314, 3.5, 9141), TOPAZ_BOLT(48, 9188, 9141, 3.9, 9336);

		public static BoltData forBolt(int itemId) {
			return values.get(itemId);
		}

		private static final Map<Integer, BoltData> values = new HashMap<>();

		static {
			for (BoltData def : values()) {
				values.put(def.getFirstItemId(), def);
			}
		}

		private final double expReceived;
		private final int levelReq, itemId1, itemId2, boltId;

		private BoltData(int levelReq, int itemId1, int itemId2, double expReceived, int boltId) {
			this.levelReq = levelReq;
			this.itemId1 = itemId1;
			this.itemId2 = itemId2;
			this.expReceived = expReceived;
			this.boltId = boltId;
		}

		public int getBoltId() {
			return boltId;
		}

		public double getExpReceived() {
			return expReceived;
		}

		public int getFirstItemId() {
			return itemId1;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getSecondItemId() {
			return itemId2;
		}
	}

	public enum BoltTip {

		DIAMOND(65, 1601, 9192, 886, 7), DRAGONSTONE(71, 1615, 9193, 885, 8), EMERALD(58, 1605, 9190, 889, 5), JADE(26, 1611, 9187, 891, 2), ONYX(73, 6573, 9194, 885, 9), OPAL(11,
				1609, 45, 890, 1), OYSTERPEARL(41, 411, 46, 890, 3), OYSTERPEARLS(41, 413, 46, 890, 3), REDTOPAZ(48, 1613, 9188, 892, 3), RUBY(63, 1603, 9191, 887, 6), SAPPHIRE(56,
						1607, 9189, 888, 4);

		public static BoltTip forGem(int gemId) {
			return values.get(gemId);
		}

		private static final Map<Integer, BoltTip> values = new HashMap<>();

		static {
			for (BoltTip def : values()) {
				values.put(def.getGemId(), def);
			}
		}

		private final int levelRequired, gemId, tipId, animationId, xpAmount;

		private BoltTip(int levelReq, int gemId, int tipId, int animationId, int xpAmount) {
			levelRequired = levelReq;
			this.gemId = gemId;
			this.tipId = tipId;
			this.animationId = animationId;
			this.xpAmount = xpAmount;
		}

		public int getAnimationId() {
			return animationId;
		}

		public int getGemId() {
			return gemId;
		}

		public int getLevelReq() {
			return levelRequired;
		}

		public int getTipId() {
			return tipId;
		}

		public int getXpAmount() {
			return xpAmount;
		}

	}

}
