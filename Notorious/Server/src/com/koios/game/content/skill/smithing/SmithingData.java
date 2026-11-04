package com.koios.game.content.skill.smithing;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;

public class SmithingData {

	public enum SmeltingData {

		BRONZE(1, 2405, 6.2, new Item(436, 1), new Item[] { new Item(438, 1) }, 2349), IRON(1, 2406, 8, new Item(440, 1), null, 2351), SILVER(1, 2407, 13.7, new Item(442, 1), null,
				2355), STEEL(1, 2409, 12.5, new Item(440, 1), new Item[] { new Item(453, 1) }, 2353), GOLD(1, 2410, 22.5, new Item(444, 1), null, 2357), MITHRIL(1, 2411, 30,
						new Item(447, 1), new Item[] { new Item(453, 1) }, 2359), ADAMANT(1, 2412, 37.5, new Item(449, 1), new Item[] { new Item(453, 1) }, 2361), RUNE(1, 2413, 50,
								new Item(451, 1), new Item[] { new Item(453, 1) }, 2363);

		private final int levelReq;
		private final int frame;
		private final double exp;
		private final Item mainItem;
		private final Item[] secondaryItems;
		private final int barId;

		static Map<Integer, SmeltingData> smelt = new HashMap<>();

		static {
			for (SmeltingData s : values()) {
				smelt.put(s.getBarId(), s);
			}
		}

		private SmeltingData(int levelReq, int frame, double exp, Item mainItem, Item[] secondaryItems, int barId) {
			this.levelReq = levelReq;
			this.frame = frame;
			this.exp = exp;
			this.mainItem = mainItem;
			this.secondaryItems = secondaryItems;
			this.barId = barId;
		}

		public static SmeltingData forBar(int id) {
			return smelt.get(id);
		}

		public int getLevelRequired() {
			return levelReq;
		}

		public int getFrame() {
			return frame;
		}

		public double getExperience() {
			return exp;
		}

		public Item getMainOre() {
			return mainItem;
		}

		public Item[] getSecondaryItems() {
			return secondaryItems;
		}

		public int getBarId() {
			return barId;
		}
	}

	public static enum AnvilData {
		BRONZE_AXE(1351, 1, 1, 12.5), IRON_AXE(1349, 16, 1, 25.0), STEEL_AXE(1353, 31, 1, 37.5), MITHRIL_AXE(1355, 51, 1, 50.0), ADAMANT_AXE(1357, 71, 1, 62.5), RUNE_AXE(1359, 86,
				1, 75.0), BRONZE_DAGGER(1205, 1, 1, 12.5), IRON_DAGGER(1203, 15, 1, 25.0), STEEL_DAGGER(1207, 30, 1, 37.5), MITHRIL_DAGGER(1209, 50, 1, 50.0), ADAMANT_DAGGER(1211,
						70, 1, 62.5), RUNE_DAGGER(1213, 85, 1, 75.0), BRONZE_MACE(1422, 2, 1, 12.5), IRON_MACE(1420, 17, 1, 25.0), STEEL_MACE(1424, 32, 1, 37.5), MITHRIL_MACE(1428,
								52, 1, 50.0), ADAMANT_MACE(1430, 72, 1, 62.5), RUNE_MACE(1432, 87, 1, 75.0), BRONZE_MED_HELM(1139, 3, 1, 12.5), IRON_MED_HELM(1137, 18, 1,
										25.0), STEEL_MED_HELM(1141, 33, 1, 37.5), MITHRIL_MED_HELM(1355, 51, 1, 50.0), ADAMANT_MED_HELM(1145, 73, 1, 62.5), RUNE_MED_HELM(1147, 88,
												1, 75.0), BRONZE_BOLTS(9375, 3, 1, 12.5), IRON_BOLTS(9377, 18, 1, 25.0), STEEL_BOLTS(9378, 33, 1, 37.5), MITHRIL_BOLTS(9379, 53, 1,
														50.0), ADAMANT_BOLTS(9380, 73, 1, 62.5), RUNE_BOLTS(9381, 88, 1, 75.0), BRONZE_SWORD(1277, 4, 1, 12.5), IRON_SWORD(1279, 19,
																1, 25.0), STEEL_SWORD(1281, 34, 1, 37.5), MITHRIL_SWORD(1285, 54, 1, 50.0), ADAMANT_SWORD(1287, 74, 1,
																		62.5), RUNE_SWORD(1289, 89, 1, 75.0), BRONZE_NAILS(4819, 4, 1, 12.5), IRON_NAILS(4820, 19, 1,
																				25.0), STEEL_NAILS(1539, 34, 1, 37.5), MITHRIL_NAILS(4822, 54, 1, 50.0), ADAMANT_NAILS(4823, 74, 1,
																						62.5), RUNE_NAILS(4824, 89, 1, 75.0), BRONZE_ARROWTIPS(39, 5, 1, 12.5), IRON_ARROWTIPS(40,
																								20, 1, 25.0), STEEL_ARROWTIPS(41, 35, 1, 37.5), MITHRIL_ARROWTIPS(42, 55, 1,
																										50.0), ADAMANT_ARROWTIPS(43, 75, 1, 62.5), RUNE_ARROWTIPS(44, 90, 1,
																												75.0), BRONZE_SCIMITAR(1321, 5, 2, 25.0), IRON_SCIMITAR(1323, 20, 2,
																														50.0), STEEL_SCIMITAR(1325, 35, 2, 75.0), MITHRIL_SCIMITAR(
																																1329, 55, 2, 100.0), ADAMANT_SCIMITAR(1331, 75, 2,
																																		125.0), RUNE_SCIMITAR(1333, 90, 2,
																																				150.0), BRONZE_LONGSWORD(1291, 6, 2,
																																						25.0), IRON_LONGSWORD(1293,
																																								21, 2,
																																								50.0), STEEL_LONGSWORD(
																																										1295, 36, 2,
																																										75.0), MITHRIL_LONGSWORD(
																																												1299,
																																												56,
																																												2,
																																												100.0), ADAMANT_LONGSWORD(
																																														1301,
																																														76,
																																														2,
																																														125.0), RUNE_LONGSWORD(
																																																1303,
																																																91,
																																																2,
																																																150.0), BRONZE_KNIVES(
																																																		864,
																																																		7,
																																																		1,
																																																		12.5), IRON_KNIVES(
																																																				863,
																																																				22,
																																																				1,
																																																				25.0), STEEL_KNIVES(
																																																						865,
																																																						37,
																																																						1,
																																																						37.5), MITHRIL_KNIVES(
																																																								866,
																																																								57,
																																																								1,
																																																								50.0), ADAMANT_KNIVES(
																																																										867,
																																																										77,
																																																										1,
																																																										62.5), RUNE_KNIVES(
																																																												868,
																																																												92,
																																																												1,
																																																												75.0), BRONZE_FULL_HELM(
																																																														1155,
																																																														7,
																																																														2,
																																																														25.0), IRON_FULL_HELM(
																																																																1153,
																																																																22,
																																																																2,
																																																																50.0), STEEL_FULL_HELM(
																																																																		1157,
																																																																		37,
																																																																		2,
																																																																		75.0), MITHRIL_FULL_HELM(
																																																																				1159,
																																																																				57,
																																																																				2,
																																																																				100.0), ADAMANT_FULL_HELM(
																																																																						1161,
																																																																						77,
																																																																						2,
																																																																						125.0), RUNE_FULL_HELM(
																																																																								1163,
																																																																								92,
																																																																								2,
																																																																								150.0), BRONZE_SQ_SHIELD(
																																																																										1173,
																																																																										8,
																																																																										2,
																																																																										25.0), IRON_SQ_SHIELD(
																																																																												1175,
																																																																												23,
																																																																												2,
																																																																												50.0), STEEL_SQ_SHIELD(
																																																																														1177,
																																																																														38,
																																																																														2,
																																																																														75.0), MITHRIL_SQ_SHIELD(
																																																																																1181,
																																																																																58,
																																																																																2,
																																																																																100.0), ADAMANT_SQ_SHIELD(
																																																																																		1183,
																																																																																		78,
																																																																																		2,
																																																																																		125.0), RUNE_SQ_SHIELD(
																																																																																				1185,
																																																																																				93,
																																																																																				2,
																																																																																				150.0), BRONZE_WARHAMMER(
																																																																																						1337,
																																																																																						9,
																																																																																						3,
																																																																																						37.5), IRON_WARHAMMER(
																																																																																								1335,
																																																																																								24,
																																																																																								3,
																																																																																								75.0), STEEL_WARHAMMER(
																																																																																										1339,
																																																																																										39,
																																																																																										3,
																																																																																										112.5), MITHRIL_WARHAMMER(
																																																																																												1343,
																																																																																												59,
																																																																																												3,
																																																																																												150.0), ADAMANT_WARHAMMER(
																																																																																														1345,
																																																																																														79,
																																																																																														3,
																																																																																														187.5), RUNE_WARHAMMER(
																																																																																																1347,
																																																																																																94,
																																																																																																3,
																																																																																																225.0), BRONZE_BATTLEAXE(
																																																																																																		1375,
																																																																																																		10,
																																																																																																		3,
																																																																																																		37.5), IRON_BATTLEAXE(
																																																																																																				1363,
																																																																																																				25,
																																																																																																				3,
																																																																																																				75.0), STEEL_BATTLEAXE(
																																																																																																						1365,
																																																																																																						40,
																																																																																																						3,
																																																																																																						112.5), MITHRIL_BATTLEAXE(
																																																																																																								1369,
																																																																																																								60,
																																																																																																								3,
																																																																																																								150.0), ADAMANT_BATTLEAXE(
																																																																																																										1371,
																																																																																																										80,
																																																																																																										3,
																																																																																																										187.5), RUNE_BATTLEAXE(
																																																																																																												1373,
																																																																																																												95,
																																																																																																												3,
																																																																																																												225.0), BRONZE_CHAINBODY(
																																																																																																														1103,
																																																																																																														11,
																																																																																																														3,
																																																																																																														37.5), IRON_CHAINBODY(
																																																																																																																1101,
																																																																																																																26,
																																																																																																																3,
																																																																																																																75.0), STEEL_CHAINBODY(
																																																																																																																		1105,
																																																																																																																		41,
																																																																																																																		3,
																																																																																																																		112.5), MITHRIL_CHAINBODY(
																																																																																																																				1109,
																																																																																																																				61,
																																																																																																																				3,
																																																																																																																				150.0), ADAMANT_CHAINBODY(
																																																																																																																						1111,
																																																																																																																						81,
																																																																																																																						3,
																																																																																																																						187.5), RUNE_CHAINBODY(
																																																																																																																								1113,
																																																																																																																								96,
																																																																																																																								3,
																																																																																																																								225.0), BRONZE_KITESHIELD(
																																																																																																																										1189,
																																																																																																																										12,
																																																																																																																										3,
																																																																																																																										37.5), IRON_KITESHIELD(
																																																																																																																												1191,
																																																																																																																												27,
																																																																																																																												3,
																																																																																																																												75.0), STEEL_KITESHIELD(
																																																																																																																														1193,
																																																																																																																														42,
																																																																																																																														3,
																																																																																																																														112.5), MITHRIL_KITESHIELD(
																																																																																																																																1197,
																																																																																																																																62,
																																																																																																																																3,
																																																																																																																																150.0), ADAMANT_KITESHIELD(
																																																																																																																																		1199,
																																																																																																																																		82,
																																																																																																																																		3,
																																																																																																																																		187.5), RUNE_KITESHIELD(
																																																																																																																																				1201,
																																																																																																																																				97,
																																																																																																																																				3,
																																																																																																																																				225.0), BRONZE_TWO_HAND_SWORD(
																																																																																																																																						1307,
																																																																																																																																						14,
																																																																																																																																						3,
																																																																																																																																						37.5), IRON_TWO_HAND_SWORD(
																																																																																																																																								1309,
																																																																																																																																								29,
																																																																																																																																								3,
																																																																																																																																								75.0), STEEL_TWO_HAND_SWORD(
																																																																																																																																										1311,
																																																																																																																																										44,
																																																																																																																																										3,
																																																																																																																																										112.5), MITHRIL_TWO_HAND_SWORD(
																																																																																																																																												1315,
																																																																																																																																												64,
																																																																																																																																												3,
																																																																																																																																												150.0), ADAMANT_TWO_HAND_SWORD(
																																																																																																																																														1317,
																																																																																																																																														84,
																																																																																																																																														3,
																																																																																																																																														187.5), RUNE_TWO_HAND_SWORD(
																																																																																																																																																1319,
																																																																																																																																																99,
																																																																																																																																																3,
																																																																																																																																																225.0), BRONZE_PLATELEGS(
																																																																																																																																																		1075,
																																																																																																																																																		16,
																																																																																																																																																		3,
																																																																																																																																																		37.5), IRON_PLATELEGS(
																																																																																																																																																				1067,
																																																																																																																																																				31,
																																																																																																																																																				3,
																																																																																																																																																				75.0), STEEL_PLATELEGS(
																																																																																																																																																						1069,
																																																																																																																																																						46,
																																																																																																																																																						3,
																																																																																																																																																						112.5), MITHRIL_PLATELEGS(
																																																																																																																																																								1071,
																																																																																																																																																								66,
																																																																																																																																																								3,
																																																																																																																																																								150.0), ADAMANT_PLATELEGS(
																																																																																																																																																										1073,
																																																																																																																																																										86,
																																																																																																																																																										3,
																																																																																																																																																										187.5), RUNE_PLATELEGS(
																																																																																																																																																												1079,
																																																																																																																																																												99,
																																																																																																																																																												3,
																																																																																																																																																												225.0), BRONZE_PLATESKIRT(
																																																																																																																																																														1087,
																																																																																																																																																														16,
																																																																																																																																																														3,
																																																																																																																																																														37.5), IRON_PLATESKIRT(
																																																																																																																																																																1081,
																																																																																																																																																																31,
																																																																																																																																																																3,
																																																																																																																																																																75.0), STEEL_PLATESKIRT(
																																																																																																																																																																		1083,
																																																																																																																																																																		46,
																																																																																																																																																																		3,
																																																																																																																																																																		112.5), MITHRIL_PLATESKIRT(
																																																																																																																																																																				1085,
																																																																																																																																																																				66,
																																																																																																																																																																				3,
																																																																																																																																																																				150.0), ADAMANT_PLATESKIRT(
																																																																																																																																																																						1091,
																																																																																																																																																																						86,
																																																																																																																																																																						3,
																																																																																																																																																																						187.5), RUNE_PLATESKIRT(
																																																																																																																																																																								1093,
																																																																																																																																																																								99,
																																																																																																																																																																								3,
																																																																																																																																																																								225.0), BRONZE_PLATEBODY(
																																																																																																																																																																										1117,
																																																																																																																																																																										18,
																																																																																																																																																																										5,
																																																																																																																																																																										62.5), IRON_PLATEBODY(
																																																																																																																																																																												1115,
																																																																																																																																																																												33,
																																																																																																																																																																												5,
																																																																																																																																																																												125.0), STEEL_PLATEBODY(
																																																																																																																																																																														1119,
																																																																																																																																																																														48,
																																																																																																																																																																														5,
																																																																																																																																																																														187.5), MITHRIL_PLATEBODY(
																																																																																																																																																																																1121,
																																																																																																																																																																																68,
																																																																																																																																																																																5,
																																																																																																																																																																																250.0), ADAMANT_PLATEBODY(
																																																																																																																																																																																		1123,
																																																																																																																																																																																		88,
																																																																																																																																																																																		5,
																																																																																																																																																																																		312.5), RUNE_PLATEBODY(
																																																																																																																																																																																				1127,
																																																																																																																																																																																				99,
																																																																																																																																																																																				5,
																																																																																																																																																																																				375.0);

		private final int itemId, levelReq, barsReq;
		private final double exp;

		private static final Map<Integer, AnvilData> values = new HashMap<>();

		static {
			for (AnvilData a : values()) {
				values.put(a.getItem(), a);
			}
		}

		private AnvilData(int itemId, int levelReq, int barsReq, double exp) {
			this.itemId = itemId;
			this.levelReq = levelReq;
			this.exp = exp;
			this.barsReq = barsReq;
		}

		public static AnvilData forItem(int id) {
			return values.get(id);
		}

		public int getItem() {
			return itemId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public double getExperience() {
			return exp;
		}

		public Item getReqItem() {
			return new Item(getRequiredBar(ItemDefinition.forId(getItem()).getName().toLowerCase()), barsReq);
		}
	}

	public static int getRequiredBar(String itemName) {
		if (itemName.contains("iron")) {
			return 2351;
		}

		if (itemName.contains("steel")) {
			return 2353;
		}

		if (itemName.contains("mithril")) {
			return 2359;
		}

		if (itemName.contains("adamant")) {
			return 2361;
		}

		if (itemName.contains("rune")) {
			return 2363;
		}

		return 2349;
	}

	public static int getFinishedAmount(String itemName) {
		itemName = itemName.toLowerCase();
		if (itemName.contains("knife")) {
			return 5;
		}

		if (itemName.contains("bolt")) {
			return 10;
		}

		if (itemName.contains("nail") || itemName.contains("arrowheads")) {
			return 15;
		}

		return 1;
	}

}
