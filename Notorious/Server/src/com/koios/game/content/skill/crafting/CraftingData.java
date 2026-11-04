package com.koios.game.content.skill.crafting;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.model.item.Item;

public class CraftingData {

	public static enum CutGemData {

		SAPPHIRE(1623, 1727, 20, 50, 888), EMERALD(1621, 1729, 27, 67, 889), RUBY(1619, 1725, 34, 85, 887), DIAMOND(1617, 1731, 43, 107.5, 886), DRAGONSTONE(1631, 1704, 55, 137.5,
				885), ONYX(6571, 6585, 67, 168, 885), /**
														 * Need correct animation ID *
														 */
		OPAL(1625, 1609, 1, 12, 890), JADE(1627, 1611, 13, 20, 891), RED_TOPAZ(1629, 1613, 16, 25, 892);

		public static CutGemData forGem(int gemId) {
			return gem.get(gemId);
		}

		private final int uncut, cut, level, animation;
		private final double xp;
		static Map<Integer, CutGemData> gem = new HashMap<>();

		static {
			for (CutGemData c : values()) {
				gem.put(c.getUncutGem(), c);
			}
		}

		private CutGemData(final int uncut, final int cut, final int level, final double xp, final int animation) {
			this.uncut = uncut;
			this.cut = cut;
			this.level = level;
			this.xp = xp;
			this.animation = animation;
		}

		public int getAnimation() {
			return animation;
		}

		public int getCutGem() {
			return cut;
		}

		public double getExperience() {
			return xp;
		}

		public int getLevelReq() {
			return level;
		}

		public int getUncutGem() {
			return uncut;
		}
	}

	public static enum GlassData {

		BEER_GLASS(1, new Item(1919, 1), 17.5, new int[] { 48112, 48111, 48110, 48109 }), CANDLE_LANTERN(4, new Item(4527, 1), 19,
				new int[] { 48116, 48115, 48114, 48113 }), OIL_LAMP(12, new Item(4522, 1), 25, new int[] { 48120, 48119, 48118, 48117 }), VIAL(33, new Item(229, 1), 35,
						new int[] { 44210, 44209, 44208, 44207 }), FISHBOWL(42, new Item(6667, 1), 42.5, new int[] { 24059, 24058, 24057, 24056 }), UNPOWERED_ORB(46,
								new Item(567, 1), 52.5, new int[] { 48108, 48107, 48106, 44211 }), LANTERN_LENS(49, new Item(4542, 1), 55,
										new int[] { 48124, 48123, 48122, 48121 }), LIGHT_ORB(87, new Item(10973, 1), 70, new int[] { -1, -1, -1, -1 });

		public static GlassData getByButton(int button) {
			for (GlassData g : GlassData.values()) {
				for (int buttonId : g.getButtons()) {
					if (buttonId == button) {
						return g;
					}
				}
			}

			return null;
		}

		private final int levelReq;
		private final Item product;
		private final double expReceived;
		private final int[] buttons;
		static Map<Integer, GlassData> glassData = new HashMap<>();

		static {
			for (GlassData g : values()) {
				glassData.put(g.getProduct().getId(), g);
			}
		}

		private GlassData(final int levelReq, Item product, double expReceived, int[] buttons) {
			this.levelReq = levelReq;
			this.product = product;
			this.expReceived = expReceived;
			this.buttons = buttons;
		}

		public int[] getButtons() {
			return buttons;
		}

		public double getExperience() {
			return expReceived;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public Item getProduct() {
			return product;
		}

	}

	public static enum LeatherData {

		LEATHER_BODY(1741, 1129, 14, 25, 1), LEATHER_GLOVES(1741, 1059, 1, 13.8, 1), LEATHER_BOOTS(1741, 1061, 7, 16.25, 1), LEATHER_VAMBRACES(1741, 1063, 11, 22,
				1), LEATHER_CHAPS(1741, 1095, 18, 27, 1), LEATHER_COIF(1741, 1169, 38, 37, 1), LEATHER_COWL(1741, 1167, 9, 18.5, 1), HARD_LEATHER_BODY(1743, 1131, 28, 35,
						1), SNAKESKIN_BODY(6289, 6322, 53, 55, 15), SNAKESKIN_CHAPS(6289, 6324, 51, 50, 12), SNAKESKIN_BANDANA(6289, 6326, 48, 45, 5), SNAKESKIN_BOOTS(6289, 6328,
								45, 30, 6), SNAKESKIN_VAMBRACES(6289, 6330, 47, 35, 8), GREEN_DHIDE_VAMBRACES(1745, 1065, 57, 62, 1), GREEN_DHIDE_BODY(1745, 1135, 63, 186,
										3), GREEN_DHIDE_CHAPS(1745, 1099, 60, 124, 2), BLUE_DHIDE_VAMBRACES(2505, 2487, 66, 70, 1), BLUE_DHIDE_BODY(2505, 2499, 71, 210,
												3), BLUE_DHIDE_CHAPS(2505, 2493, 68, 140, 2), RED_DHIDE_VAMBRACES(2507, 2489, 73, 78, 1), RED_DHIDE_BODY(2507, 2501, 77, 234,
														3), RED_DHIDE_CHAPS(2507, 2495, 75, 156, 2), BLACK_DHIDE_VAMBRACES(2509, 2491, 79, 86, 1), BLACK_DHIDE_BODY(2509, 2503, 84,
																258, 3), BLACK_DHIDE_CHAPS(2509, 2497, 82, 172, 2);

		public static LeatherData forProduct(int id) {
			return leatherData.get(id);
		}

		private final int leather, product, level, amount;

		private final double xp;

		static Map<Integer, LeatherData> leatherData = new HashMap<>();

		static {
			for (LeatherData l : values()) {
				leatherData.put(l.getProduct(), l);
			}
		}

		private LeatherData(final int leather, final int product, final int level, final double xp, final int amount) {
			this.leather = leather;
			this.product = product;
			this.level = level;
			this.xp = xp;
			this.amount = amount;
		}

		public int getHideAmount() {
			return amount;
		}

		public int getLeather() {
			return leather;
		}

		public int getLevel() {
			return level;
		}

		public int getProduct() {
			return product;
		}

		public double getXP() {
			return xp;
		}
	}

	public static enum LeatherDialogueData {

		GREEN_LEATHER(1745, 1065, 1099, 1135), BLUE_LEATHER(2505, 2487, 2493, 2499), RED_LEATHER(2507, 2489, 2495, 2501), BLACK_LEATHER(2509, 2491, 2497, 2503);

		public static LeatherDialogueData forLeather(int id) {
			return leatherData.get(id);
		}

		private final int leather, vambraces, chaps, body;

		static Map<Integer, LeatherDialogueData> leatherData = new HashMap<>();

		static {
			for (LeatherDialogueData l : values()) {
				leatherData.put(l.getLeather(), l);
			}
		}

		private LeatherDialogueData(final int leather, final int vambraces, final int chaps, final int body) {
			this.leather = leather;
			this.vambraces = vambraces;
			this.chaps = chaps;
			this.body = body;
		}

		public int getBody() {
			return body;
		}

		public int getChaps() {
			return chaps;
		}

		public int getLeather() {
			return leather;
		}

		public int getVamb() {
			return vambraces;
		}
	}

	public enum TanningData {

		SOFT_LEATHER(1739, 1741, 1, new int[] { 14777, 14785, 14769 }, "Soft leather"), HARD_LEATHER(1739, 1743, 3, new int[] { 14778, 14786, 14770 }, "Hard leather"), SNAKESKIN(
				6287, 6289, 15, new int[] { 14779, 14787, 14771 },
				"Snakeskin"), SNAKESKIN2(6287, 6289, 20, new int[] { 14780, 14788, 14772 }, "Snakeskin"), GREEN_DRAGON_LEATHER(1753, 1745, 20, new int[] { 14781, 14789, 14773 },
						"Green d'hide"), BLUE_DRAGON_LEATHER(1751, 2505, 20, new int[] { 14782, 14790, 14774 }, "Blue d'hide"), RED_DRAGON_LEATHER(1749, 2507, 20,
								new int[] { 14783, 14791, 14775 }, "Red d'hide"), BLACK_DRAGON_LEATHER(1747, 2509, 20, new int[] { 14784, 14792, 14776 }, "Black d'hide");

		public static TanningData get(int id) {
			for (TanningData t : TanningData.values()) {
				if (id == t.getLeatherId()) {
					return t;
				}
			}
			return null;
		}

		private final int hideId, leatherId, price;
		private final int[] frameId;

		private final String name;

		private TanningData(final int hideId, final int leatherId, final int price, final int[] frameId, final String name) {
			this.hideId = hideId;
			this.leatherId = leatherId;
			this.price = price;
			this.frameId = frameId;
			this.name = name;
		}

		public int getCostFrame() {
			return frameId[1];
		}

		public int getHideId() {
			return hideId;
		}

		public int getItemFrame() {
			return frameId[2];
		}

		public int getLeatherId() {
			return leatherId;
		}

		public String getName() {
			return name;
		}

		public int getNameFrame() {
			return frameId[0];
		}

		public int getPrice() {
			return price;
		}

	}

}
