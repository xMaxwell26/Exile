package com.koios.game.content.skill.herblore;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.model.item.Item;

public class HerbloreData {

	public enum IngredientData {

		// lvlreq, unfinishedid, ingredientid, finishedid, xp
		GUAMPOTION(3, new Item[] { new Item(91), new Item(221) }, new Item(121), 25), 
		MERRENTILLPOTION(5, new Item[] { new Item(93), new Item(235) }, new Item(175), 38), 
		TARROMINPOTION(12, new Item[] { new Item(95), new Item(225) }, new Item(115), 50), 
		HARRALANDERPOTION1(22, new Item[] { new Item(97), new Item(223) }, new Item(127), 63), 
		HARRALANDERPOTION2(26, new Item[] { new Item(97), new Item(1975) }, new Item(3010), 68), 
		MARRENTILLPOTION(30, new Item[] { new Item(93), new Item(948) }, new Item(133), 45), 
		TOADFLAXPOTION1(34, new Item[] { new Item(3002), new Item(2152) }, new Item(3034), 80), 
		HARRALANDERPOTION3(36, new Item[] { new Item(97), new Item(9736) }, new Item(9741), 84), 
		RANARRPOTION(38, new Item[] { new Item(99), new Item(231) }, new Item(139), 88), 
		SPIRITWEEDPOTION(40, new Item[] { new Item(12181), new Item(12109) }, new Item(12142), 92), 
		IRITPOTION1(45, new Item[] { new Item(101), new Item(221) }, new Item(145), 100), 
		IRITPOTION2(48, new Item[] { new Item(101), new Item(235) }, new Item(181), 106), 
		AVANTOEPOTION1(50, new Item[] { new Item(103), new Item(231) }, new Item(151), 113), 
		AVANTOEPOTION2(52, new Item[] { new Item(103), new Item(2970) }, new Item(3018), 118), 
		AVANTOEPOTION3(53, new Item[] { new Item(103), new Item(10111) }, new Item(10000), 120), 
		KWUARMPOTION1(55, new Item[] { new Item(105), new Item(225) }, new Item(157), 125), 
		WERGALIPOTION(58, new Item[] { new Item(14856), new Item(11525) }, new Item(14848), 132), 
		KWUARMPOTION2(60, new Item[] { new Item(105), new Item(241) }, new Item(187), 138), 
		SNAPDRAGONPOTION(63, new Item[] { new Item(3004), new Item(223) }, new Item(3026), 143), 
		CADANTINEPOTION(66, new Item[] { new Item(107), new Item(239) }, new Item(163), 150), 
		LANTADYMEPOTION1(69, new Item[] { new Item(2483), new Item(241) }, new Item(2454), 158), 
		DWARFWEEDPOTION(72, new Item[] { new Item(109), new Item(245) }, new Item(169), 163), 
		LANTADYMEPOTION2(76, new Item[] { new Item(2483), new Item(3138) }, new Item(3042), 173), 
		TORSTOLPOTION(78, new Item[] { new Item(111), new Item(247) }, new Item(189), 175), 
		TOADFLAXPOTION2(81, new Item[] { new Item(3002), new Item(6693) }, new Item(6687), 180), 
		RECOVERSPECIAL(84, new Item[] { new Item(3018), new Item(5972) }, new Item(15301), 200), 
		EXTREMEATTACK(88, new Item[] { new Item(145), new Item(261) }, new Item(15309), 220),
		EXTREMESTRENGTH(89, new Item[] { new Item(157), new Item(267) }, new Item(15313), 230), 
		EXTREMEDEFENCE(90, new Item[] { new Item(163), new Item(2481) }, new Item(15317), 240), 
		EXTREMEMAGIC(91, new Item[] { new Item(3042), new Item(9594) }, new Item( 15321), 250), 
		EXTREMERANGE(92, new Item[] { new Item(169), new Item(12539, 5) }, new Item(15325), 260), 
		FELLSTALKPOTION(94, new Item[] { new Item(21628), new Item(21622) }, new Item(21632), 190), 
		OVERLOAD(96, new Item[] { new Item(269), new Item(15313), new Item(15309), new Item(15325), new Item(15321), new Item(15317) }, new Item(15333), 1000);

		private final int levelRequired;
		private final Item[] item_requireds;
		private final double exp_gained;
		private final Item finished_potion;

		private IngredientData(int levelReq, Item[] item_requireds, Item finished_potion, int exp_gained) {
			levelRequired = levelReq;
			this.finished_potion = finished_potion;
			this.item_requireds = item_requireds;
			this.exp_gained = exp_gained;
		}

		public int getLevelReq() {
			return levelRequired;
		}

		public Item[] getRequiredItems() {
			return item_requireds;
		}

		public double getExpGained() {
			return exp_gained;
		}

		public Item getFinishedPotion() {
			return finished_potion;
		}

		public static IngredientData forPot(int id, int id2) {
			boolean item1 = false, item2 = false;

			for (IngredientData i : IngredientData.values()) {
				for (int x = 0; x < i.getRequiredItems().length; x++) {
					if (i.getRequiredItems()[x].getId() == id) {
						item1 = true;
					}

					if (i.getRequiredItems()[x].getId() == id2) {
						item2 = true;
					}

					if (x == i.getRequiredItems().length - 1) {
						if (item1 && item2) {
							return i;
						} else {
							item1 = false;
							item2 = false;
						}
					}
				}
			}

			return null;
		}
	}

	public enum TarData {

		Guam(19, 249, 10142, 30, "guam"), Marrentil(31, 251, 10143, 42.5, "marrentil"), Tarromin(39, 253, 10144, 55, "tarromin"), Harralander(44, 255, 10145, 72.5, "harralander");

		public static TarData forItemId(int itemId) {
			return values.get(itemId);
		}

		private final int levelReq, weedId, tarId;
		private final double expReceived;
		private final String weedName;

		private static final Map<Integer, TarData> values = new HashMap<>();

		static {
			for (TarData t : values()) {
				values.put(t.getWeedId(), t);
			}
		}

		private TarData(int levelReq, int weedId, int tarId, double expReceived, String weedName) {
			this.levelReq = levelReq;
			this.weedId = weedId;
			this.tarId = tarId;
			this.expReceived = expReceived;
			this.weedName = weedName;
		}

		public double getExperience() {
			return expReceived;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getTarId() {
			return tarId;
		}

		public int getWeedId() {
			return weedId;
		}

		public String getWeedName() {
			return weedName;
		}

	}

	public enum UnfData {

		Guam(1, 249, 91), Marrentil(5, 251, 93), Tarromin(11, 253, 95), Harralander(20, 255, 97), Rannar(25, 257, 99), ToadFlax(30, 2998, 3002), Spirit(35, 12172, 12181), Irit(40,
				259, 101), Wergali(41, 14854, 14856), Avantoe(48, 261, 103), Kwuarm(54, 263, 105), Snapdragon(59, 3000, 3004), Candatine(65, 265, 107), Lantadyme(67, 2481,
						2483), DwarfWeed(70, 267, 109), Torstol(75, 269, 111);

		public static UnfData forItemId(int itemId) {
			return values.get(itemId);
		}

		private final int levelReq, weedId, unfId;

		private static final Map<Integer, UnfData> values = new HashMap<>();

		static {
			for (UnfData a : values()) {
				values.put(a.getWeedId(), a);
			}
		}

		private UnfData(int levelReq, int weedId, int unfId) {
			this.levelReq = levelReq;
			this.weedId = weedId;
			this.unfId = unfId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getUnfId() {
			return unfId;
		}

		public int getWeedId() {
			return weedId;
		}

	}

}
