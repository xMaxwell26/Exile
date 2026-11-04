package com.koios.game.content.skill.smithing;

import com.koios.game.content.skill.Skill;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public class SmithingInterface {

	private static String getForBars(final Player player, final int i, final int j) {
		return player.getInventory().hasItem(new Item(i, j)) ? "@gre@" : "@red@";
	}

	private static String getForLevel(final Player player, final int i) {
		return player.getSkill().getLevel(Skill.SMITHING) >= i ? "@whi@" : "@bla@";
	}

	private static void makeAddyInterface(final Player player) {
		final String fiveb = SmithingInterface.getForBars(player, 2361, 5);
		final String threeb = SmithingInterface.getForBars(player, 2361, 3);
		final String twob = SmithingInterface.getForBars(player, 2361, 2);
		final String oneb = SmithingInterface.getForBars(player, 2361, 1);
		player.getSession().send(new SendStringPacket(1112, fiveb + "5 Bars" + fiveb));
		player.getSession().send(new SendStringPacket(1109, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1110, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1118, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1111, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1095, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1115, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1090, threeb + "3 Bars" + threeb));
		player.getSession().send(new SendStringPacket(1113, twob + "2 Bars" + twob));
		player.getSession().send(new SendStringPacket(1116, twob + "2 Bars" + twob));
		player.getSession().send(new SendStringPacket(1114, twob + "2 Bars" + twob));
		player.getSession().send(new SendStringPacket(1089, twob + "2 Bars" + twob));
		player.getSession().send(new SendStringPacket(8428, " "));
		player.getSession().send(new SendStringPacket(8429, " "));
		player.getSession().send(new SendStringPacket(1124, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1125, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1126, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1127, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1128, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1129, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1130, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1131, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(13357, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(11459, oneb + "1 Bar" + oneb));
		player.getSession().send(new SendStringPacket(1101, SmithingInterface.getForLevel(player, 88) + "Plate Body" + SmithingInterface.getForLevel(player, 18)));
		player.getSession().send(new SendStringPacket(1099, SmithingInterface.getForLevel(player, 86) + "Plate Legs" + SmithingInterface.getForLevel(player, 16)));
		player.getSession().send(new SendStringPacket(1100, SmithingInterface.getForLevel(player, 86) + "Plate Skirt" + SmithingInterface.getForLevel(player, 16)));
		player.getSession().send(new SendStringPacket(1088, SmithingInterface.getForLevel(player, 84) + "2 Hand Sword" + SmithingInterface.getForLevel(player, 14)));
		player.getSession().send(new SendStringPacket(1105, SmithingInterface.getForLevel(player, 82) + "Kite Shield" + SmithingInterface.getForLevel(player, 12)));
		player.getSession().send(new SendStringPacket(1098, SmithingInterface.getForLevel(player, 81) + "Chain Body" + SmithingInterface.getForLevel(player, 11)));
		player.getSession().send(new SendStringPacket(1092, SmithingInterface.getForLevel(player, 80) + "Battle Axe" + SmithingInterface.getForLevel(player, 10)));
		player.getSession().send(new SendStringPacket(1083, SmithingInterface.getForLevel(player, 79) + "Warhammer" + SmithingInterface.getForLevel(player, 9)));
		player.getSession().send(new SendStringPacket(1104, SmithingInterface.getForLevel(player, 78) + "Square Shield" + SmithingInterface.getForLevel(player, 8)));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 77) + "Full Helm" + SmithingInterface.getForLevel(player, 7), 1103));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 77) + "Throwing Knives" + SmithingInterface.getForLevel(player, 7), 1106));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 76) + "Long Sword" + SmithingInterface.getForLevel(player, 6), 1086));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 75) + "Scimitar" + SmithingInterface.getForLevel(player, 5), 1087));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 75) + "Arrowtips" + SmithingInterface.getForLevel(player, 5), 1108));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 75) + "Bolts" + SmithingInterface.getForLevel(player, 5), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 74) + "Sword" + SmithingInterface.getForLevel(player, 4), 1085));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 74) + "Bolts" + SmithingInterface.getForLevel(player, 4), 9143));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 74) + "Nails" + SmithingInterface.getForLevel(player, 4), 13358));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 73) + "Medium Helm" + SmithingInterface.getForLevel(player, 3), 1102));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 72) + "Mace" + SmithingInterface.getForLevel(player, 2), 1093));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 70) + "Dagger" + SmithingInterface.getForLevel(player, 1), 1094));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 71) + "Axe" + SmithingInterface.getForLevel(player, 1), 1091));
		ActionSender.sendUpdateItem34(player, 1119, 0, new Item(1211, 1)); // dagger
		ActionSender.sendUpdateItem34(player, 1120, 0, new Item(1357, 1)); // axe
		ActionSender.sendUpdateItem34(player, 1121, 0, new Item(1111, 1)); // chain
		ActionSender.sendUpdateItem34(player, 1122, 0, new Item(1145, 1)); // med
		ActionSender.sendUpdateItem34(player, 1123, 0, new Item(9380, 10)); // Bolts

		ActionSender.sendUpdateItem34(player, 1119, 1, new Item(1287, 1)); // s-sword
		ActionSender.sendUpdateItem34(player, 1120, 1, new Item(1430, 1)); // mace
		ActionSender.sendUpdateItem34(player, 1121, 1, new Item(1073, 1)); // platelegs
		ActionSender.sendUpdateItem34(player, 1122, 1, new Item(1161, 1)); // full_helm
		ActionSender.sendUpdateItem34(player, 1123, 1, new Item(43, 15)); // arrowtips

		ActionSender.sendUpdateItem34(player, 1119, 2, new Item(1331, 1)); // scimmy
		ActionSender.sendUpdateItem34(player, 1120, 2, new Item(1345, 1)); // warhammer
		ActionSender.sendUpdateItem34(player, 1121, 2, new Item(1091, 1)); // plateskirt
		ActionSender.sendUpdateItem34(player, 1122, 2, new Item(1183, 1)); // Sq_shield
		ActionSender.sendUpdateItem34(player, 1123, 2, new Item(867, 5)); // throwing-knives

		ActionSender.sendUpdateItem34(player, 1119, 3, new Item(1301, 1)); // longsword
		ActionSender.sendUpdateItem34(player, 1120, 3, new Item(1371, 1)); // battleaxe
		ActionSender.sendUpdateItem34(player, 1121, 3, new Item(1123, 1)); // platebody
		ActionSender.sendUpdateItem34(player, 1122, 3, new Item(1199, 1)); // kiteshield

		ActionSender.sendUpdateItem34(player, 1119, 4, new Item(1317, 1)); // 2h_sword
		ActionSender.sendUpdateItem34(player, 1122, 4, new Item(4823, 15)); // nails
		ActionSender.sendUpdateItem34(player, 1123, 3, new Item(-1, 1));

		player.getSession().send(new SendStringPacket("", 1135));
		player.getSession().send(new SendStringPacket("", 1134));
		player.getSession().send(new SendStringPacket("", 11461));
		player.getSession().send(new SendStringPacket("", 11459));
		player.getSession().send(new SendStringPacket("", 1132));
		player.getSession().send(new SendStringPacket("", 1096));
		player.getInterfaceState().openInterface(994);
	}

	private static void makeBronzeInterface(final Player player) {
		final String fiveb = SmithingInterface.getForBars(player, 2349, 5);
		final String threeb = SmithingInterface.getForBars(player, 2349, 3);
		final String twob = SmithingInterface.getForBars(player, 2349, 2);
		final String oneb = SmithingInterface.getForBars(player, 2349, 1);
		player.getSession().send(new SendStringPacket(fiveb + "5 Bars" + fiveb, 1112));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1109));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1110));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1118));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1111));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1095));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1115));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1090));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1113));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1116));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1114));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1089));
		player.getSession().send(new SendStringPacket(" ", 8428));
		player.getSession().send(new SendStringPacket(" ", 8429));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1124));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1125));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1126));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1127));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1128));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1129));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1130));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1131));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 13357));
		player.getSession().send(new SendStringPacket(" ", 11459));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 18) + "Plate Body" + SmithingInterface.getForLevel(player, 18), 1101));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 16) + "Plate Legs" + SmithingInterface.getForLevel(player, 16), 1099));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 16) + "Plate Skirt" + SmithingInterface.getForLevel(player, 16), 1100));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 14) + "2 Hand Sword" + SmithingInterface.getForLevel(player, 14), 1088));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 12) + "Kite Shield" + SmithingInterface.getForLevel(player, 12), 1105));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 11) + "Chain Body" + SmithingInterface.getForLevel(player, 11), 1098));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 10) + "Battle Axe" + SmithingInterface.getForLevel(player, 10), 1092));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 9) + "Warhammer" + SmithingInterface.getForLevel(player, 9), 1083));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 8) + "Square Shield" + SmithingInterface.getForLevel(player, 8), 1104));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 7) + "Full Helm" + SmithingInterface.getForLevel(player, 7), 1103));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 7) + "Throwing Knives" + SmithingInterface.getForLevel(player, 7), 1106));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 6) + "Long Sword" + SmithingInterface.getForLevel(player, 6), 1086));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 5) + "Scimitar" + SmithingInterface.getForLevel(player, 5), 1087));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 5) + "Arrowtips" + SmithingInterface.getForLevel(player, 5), 1108));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 5) + "Bolts" + SmithingInterface.getForLevel(player, 5), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 4) + "Sword" + SmithingInterface.getForLevel(player, 4), 1085));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 4) + "Bolts" + SmithingInterface.getForLevel(player, 4), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 4) + "Nails" + SmithingInterface.getForLevel(player, 4), 13358));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 3) + "Medium Helm" + SmithingInterface.getForLevel(player, 3), 1102));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 2) + "Mace" + SmithingInterface.getForLevel(player, 2), 1093));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 1) + "Dagger" + SmithingInterface.getForLevel(player, 1), 1094));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 1) + "Axe" + SmithingInterface.getForLevel(player, 1), 1091));
		ActionSender.sendUpdateItem34(player, 1119, 0, new Item(1205, 1)); // dagger
		ActionSender.sendUpdateItem34(player, 1120, 0, new Item(1351, 1)); // axe
		ActionSender.sendUpdateItem34(player, 1121, 0, new Item(1103, 1)); // chain
		ActionSender.sendUpdateItem34(player, 1122, 0, new Item(1139, 1)); // med
		ActionSender.sendUpdateItem34(player, 1123, 0, new Item(9375, 10)); // Bolts

		ActionSender.sendUpdateItem34(player, 1119, 1, new Item(1277, 1)); // s-sword
		ActionSender.sendUpdateItem34(player, 1120, 1, new Item(1422, 1)); // mace
		ActionSender.sendUpdateItem34(player, 1121, 1, new Item(1075, 1)); // platelegs
		ActionSender.sendUpdateItem34(player, 1122, 1, new Item(1155, 1)); // full_helm
		ActionSender.sendUpdateItem34(player, 1123, 1, new Item(39, 15)); // arrowtips

		ActionSender.sendUpdateItem34(player, 1119, 2, new Item(1321, 1)); // scimmy
		ActionSender.sendUpdateItem34(player, 1120, 2, new Item(1337, 1)); // warhammer
		ActionSender.sendUpdateItem34(player, 1121, 2, new Item(1087, 1)); // plateskirt
		ActionSender.sendUpdateItem34(player, 1122, 2, new Item(1173, 1)); // Sq_shield
		ActionSender.sendUpdateItem34(player, 1123, 2, new Item(864, 5)); // throwing-knives

		ActionSender.sendUpdateItem34(player, 1119, 3, new Item(1291, 1)); // longsword
		ActionSender.sendUpdateItem34(player, 1120, 3, new Item(1375, 1)); // battleaxe
		ActionSender.sendUpdateItem34(player, 1121, 3, new Item(1117, 1)); // platebody
		ActionSender.sendUpdateItem34(player, 1122, 3, new Item(1189, 1)); // kiteshield

		ActionSender.sendUpdateItem34(player, 1119, 4, new Item(1307, 1)); // 2h_sword
		ActionSender.sendUpdateItem34(player, 1122, 4, new Item(4819, 15)); // nails
		ActionSender.sendUpdateItem34(player, 1123, 3, new Item(-1, 1));
		player.getSession().send(new SendStringPacket("", 1135));
		player.getSession().send(new SendStringPacket("", 1134));
		player.getSession().send(new SendStringPacket("", 11461));
		player.getSession().send(new SendStringPacket("", 11459));
		player.getSession().send(new SendStringPacket("", 1132));
		player.getSession().send(new SendStringPacket("", 1096));
		player.getInterfaceState().openInterface(994);
	}

	private static void makeIronInterface(final Player player) {
		final String fiveb = SmithingInterface.getForBars(player, 2351, 5);
		final String threeb = SmithingInterface.getForBars(player, 2351, 3);
		final String twob = SmithingInterface.getForBars(player, 2351, 2);
		final String oneb = SmithingInterface.getForBars(player, 2351, 1);
		player.getSession().send(new SendStringPacket(fiveb + "5 Bars" + fiveb, 1112));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1109));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1110));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1118));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1111));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1095));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1115));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1090));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1113));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1116));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1114));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1089));
		player.getSession().send(new SendStringPacket(" ", 8428));
		player.getSession().send(new SendStringPacket(" ", 8429));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1124));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1125));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1126));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1127));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1128));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1129));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1130));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1131));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 13357));
		player.getSession().send(new SendStringPacket(" ", 11459));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 33) + "Plate Body" + SmithingInterface.getForLevel(player, 18), 1101));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 31) + "Plate Legs" + SmithingInterface.getForLevel(player, 16), 1099));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 31) + "Plate Skirt" + SmithingInterface.getForLevel(player, 16), 1100));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 29) + "2 Hand Sword" + SmithingInterface.getForLevel(player, 14), 1088));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 27) + "Kite Shield" + SmithingInterface.getForLevel(player, 12), 1105));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 26) + "Chain Body" + SmithingInterface.getForLevel(player, 11), 1098));
		player.getSession().send(new SendStringPacket(" ", 11461));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 25) + "Battle Axe" + SmithingInterface.getForLevel(player, 10), 1092));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 24) + "Warhammer" + SmithingInterface.getForLevel(player, 9), 1083));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 23) + "Square Shield" + SmithingInterface.getForLevel(player, 8), 1104));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 22) + "Full Helm" + SmithingInterface.getForLevel(player, 7), 1103));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 21) + "Throwing Knives" + SmithingInterface.getForLevel(player, 7), 1106));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 21) + "Long Sword" + SmithingInterface.getForLevel(player, 6), 1086));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 20) + "Scimitar" + SmithingInterface.getForLevel(player, 5), 1087));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 20) + "Arrowtips" + SmithingInterface.getForLevel(player, 5), 1108));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 20) + "Bolts" + SmithingInterface.getForLevel(player, 5), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 19) + "Sword" + SmithingInterface.getForLevel(player, 4), 1085));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 19) + "Bolts" + SmithingInterface.getForLevel(player, 4), 9140));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 19) + "Nails" + SmithingInterface.getForLevel(player, 4), 13358));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 18) + "Medium Helm" + SmithingInterface.getForLevel(player, 3), 1102));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 17) + "Mace" + SmithingInterface.getForLevel(player, 2), 1093));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 15) + "Dagger" + SmithingInterface.getForLevel(player, 1), 1094));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 16) + "Axe" + SmithingInterface.getForLevel(player, 1), 1091));
		ActionSender.sendUpdateItem34(player, 1119, 0, new Item(1203, 1)); // dagger
		ActionSender.sendUpdateItem34(player, 1120, 0, new Item(1349, 1)); // axe
		ActionSender.sendUpdateItem34(player, 1121, 0, new Item(1101, 1)); // chain
		ActionSender.sendUpdateItem34(player, 1122, 0, new Item(1137, 1)); // med
		ActionSender.sendUpdateItem34(player, 1123, 0, new Item(9377, 10)); // Bolts

		ActionSender.sendUpdateItem34(player, 1119, 1, new Item(1279, 1)); // s-sword
		ActionSender.sendUpdateItem34(player, 1120, 1, new Item(1420, 1)); // mace
		ActionSender.sendUpdateItem34(player, 1121, 1, new Item(1067, 1)); // platelegs
		ActionSender.sendUpdateItem34(player, 1122, 1, new Item(1153, 1)); // full_helm
		ActionSender.sendUpdateItem34(player, 1123, 1, new Item(40, 15)); // arrowtips

		ActionSender.sendUpdateItem34(player, 1119, 2, new Item(1323, 1)); // scimmy
		ActionSender.sendUpdateItem34(player, 1120, 2, new Item(1335, 1)); // warhammer
		ActionSender.sendUpdateItem34(player, 1121, 2, new Item(1081, 1)); // plateskirt
		ActionSender.sendUpdateItem34(player, 1122, 2, new Item(1175, 1)); // Sq_shield
		ActionSender.sendUpdateItem34(player, 1123, 2, new Item(863, 5)); // throwing-knives

		ActionSender.sendUpdateItem34(player, 1119, 3, new Item(1293, 1)); // longsword
		ActionSender.sendUpdateItem34(player, 1120, 3, new Item(1363, 1)); // battleaxe
		ActionSender.sendUpdateItem34(player, 1121, 3, new Item(1115, 1)); // platebody
		ActionSender.sendUpdateItem34(player, 1122, 3, new Item(1191, 1)); // kiteshield

		ActionSender.sendUpdateItem34(player, 1119, 4, new Item(1309, 1)); // 2h_sword
		ActionSender.sendUpdateItem34(player, 1122, 4, new Item(4820, 15)); // nails
		ActionSender.sendUpdateItem34(player, 1123, 3, new Item(-1, 1));

		player.getSession().send(new SendStringPacket("", 1135));
		player.getSession().send(new SendStringPacket("", 1134));
		player.getSession().send(new SendStringPacket("", 1132));
		player.getSession().send(new SendStringPacket("", 1096));
		player.getInterfaceState().openInterface(994);
	}

	private static void makeMithInterface(final Player player) {
		final String fiveb = SmithingInterface.getForBars(player, 2359, 5);
		final String threeb = SmithingInterface.getForBars(player, 2359, 3);
		final String twob = SmithingInterface.getForBars(player, 2359, 2);
		final String oneb = SmithingInterface.getForBars(player, 2359, 1);
		player.getSession().send(new SendStringPacket(fiveb + "5 Bars" + fiveb, 1112));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1109));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1110));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1118));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1111));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1095));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1115));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1090));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1113));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1116));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1114));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1089));
		player.getSession().send(new SendStringPacket(" ", 8428));
		player.getSession().send(new SendStringPacket(" ", 8429));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1124));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1125));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1126));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1127));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1128));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1129));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1130));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1131));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 13357));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 11459));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 68) + "Plate Body" + SmithingInterface.getForLevel(player, 18), 1101));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 66) + "Plate Legs" + SmithingInterface.getForLevel(player, 16), 1099));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 66) + "Plate Skirt" + SmithingInterface.getForLevel(player, 16), 1100));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 64) + "2 Hand Sword" + SmithingInterface.getForLevel(player, 14), 1088));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 62) + "Kite Shield" + SmithingInterface.getForLevel(player, 12), 1105));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 61) + "Chain Body" + SmithingInterface.getForLevel(player, 11), 1098));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 60) + "Battle Axe" + SmithingInterface.getForLevel(player, 10), 1092));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 59) + "Warhammer" + SmithingInterface.getForLevel(player, 9), 1083));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 58) + "Square Shield" + SmithingInterface.getForLevel(player, 8), 1104));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 57) + "Full Helm" + SmithingInterface.getForLevel(player, 7), 1103));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 57) + "Throwing Knives" + SmithingInterface.getForLevel(player, 7), 1106));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 56) + "Long Sword" + SmithingInterface.getForLevel(player, 6), 1086));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 55) + "Scimitar" + SmithingInterface.getForLevel(player, 5), 1087));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 55) + "Arrowtips" + SmithingInterface.getForLevel(player, 5), 1108));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 55) + "Bolts" + SmithingInterface.getForLevel(player, 5), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 54) + "Sword" + SmithingInterface.getForLevel(player, 4), 1085));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 54) + "Bolts" + SmithingInterface.getForLevel(player, 4), 9142));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 54) + "Nails" + SmithingInterface.getForLevel(player, 4), 13358));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 53) + "Medium Helm" + SmithingInterface.getForLevel(player, 3), 1102));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 52) + "Mace" + SmithingInterface.getForLevel(player, 2), 1093));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 50) + "Dagger" + SmithingInterface.getForLevel(player, 1), 1094));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 51) + "Axe" + SmithingInterface.getForLevel(player, 1), 1091));

		ActionSender.sendUpdateItem34(player, 1119, 0, new Item(1209, 1)); // dagger
		ActionSender.sendUpdateItem34(player, 1120, 0, new Item(1355, 1)); // axe
		ActionSender.sendUpdateItem34(player, 1121, 0, new Item(1109, 1)); // chain
		ActionSender.sendUpdateItem34(player, 1122, 0, new Item(1143, 1)); // med
		ActionSender.sendUpdateItem34(player, 1123, 0, new Item(9379, 10)); // Bolts

		ActionSender.sendUpdateItem34(player, 1119, 1, new Item(1285, 1)); // s-sword
		ActionSender.sendUpdateItem34(player, 1120, 1, new Item(1428, 1)); // mace
		ActionSender.sendUpdateItem34(player, 1121, 1, new Item(1071, 1)); // platelegs
		ActionSender.sendUpdateItem34(player, 1122, 1, new Item(1159, 1)); // full_helm
		ActionSender.sendUpdateItem34(player, 1123, 1, new Item(42, 15)); // arrowtips

		ActionSender.sendUpdateItem34(player, 1119, 2, new Item(1329, 1)); // scimmy
		ActionSender.sendUpdateItem34(player, 1120, 2, new Item(1343, 1)); // warhammer
		ActionSender.sendUpdateItem34(player, 1121, 2, new Item(1085, 1)); // plateskirt
		ActionSender.sendUpdateItem34(player, 1122, 2, new Item(1181, 1)); // Sq_shield
		ActionSender.sendUpdateItem34(player, 1123, 2, new Item(866, 5)); // throwing-knives

		ActionSender.sendUpdateItem34(player, 1119, 3, new Item(1299, 1)); // longsword
		ActionSender.sendUpdateItem34(player, 1120, 3, new Item(1369, 1)); // battleaxe
		ActionSender.sendUpdateItem34(player, 1121, 3, new Item(1121, 1)); // platebody
		ActionSender.sendUpdateItem34(player, 1122, 3, new Item(1197, 1)); // kiteshield

		ActionSender.sendUpdateItem34(player, 1119, 4, new Item(1315, 1)); // 2h_sword
		ActionSender.sendUpdateItem34(player, 1122, 4, new Item(4822, 15)); // nails
		ActionSender.sendUpdateItem34(player, 1123, 3, new Item(-1, 1));

		player.getSession().send(new SendStringPacket("", 1135));
		player.getSession().send(new SendStringPacket("", 1134));
		player.getSession().send(new SendStringPacket("", 11461));
		player.getSession().send(new SendStringPacket("", 11459));
		player.getSession().send(new SendStringPacket("", 1132));
		player.getSession().send(new SendStringPacket("", 1096));
		player.getInterfaceState().openInterface(994);
	}

	private static void makeRuneInterface(final Player player) {
		final String fiveb = SmithingInterface.getForBars(player, 2363, 5);
		final String threeb = SmithingInterface.getForBars(player, 2363, 3);
		final String twob = SmithingInterface.getForBars(player, 2363, 2);
		final String oneb = SmithingInterface.getForBars(player, 2363, 1);
		player.getSession().send(new SendStringPacket(fiveb + "5 Bars" + fiveb, 1112));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1109));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1110));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1118));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1111));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1095));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1115));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1090));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1113));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1116));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1114));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1089));
		player.getSession().send(new SendStringPacket(" ", 8428));
		player.getSession().send(new SendStringPacket(" ", 8429));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1124));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1125));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1126));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1127));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1128));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1129));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1130));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1131));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 13357));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 11459));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 88) + "Plate Body" + SmithingInterface.getForLevel(player, 18), 1101));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 99) + "Plate Legs" + SmithingInterface.getForLevel(player, 16), 1099));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 99) + "Plate Skirt" + SmithingInterface.getForLevel(player, 16), 1100));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 99) + "2 Hand Sword" + SmithingInterface.getForLevel(player, 14), 1088));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 97) + "Kite Shield" + SmithingInterface.getForLevel(player, 12), 1105));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 96) + "Chain Body" + SmithingInterface.getForLevel(player, 11), 1098));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 95) + "Battle Axe" + SmithingInterface.getForLevel(player, 10), 1092));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 94) + "Warhammer" + SmithingInterface.getForLevel(player, 9), 1083));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 93) + "Square Shield" + SmithingInterface.getForLevel(player, 8), 1104));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 92) + "Full Helm" + SmithingInterface.getForLevel(player, 7), 1103));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 92) + "Throwing Knives" + SmithingInterface.getForLevel(player, 7), 1106));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 91) + "Long Sword" + SmithingInterface.getForLevel(player, 6), 1086));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 90) + "Scimitar" + SmithingInterface.getForLevel(player, 5), 1087));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 90) + "Arrowtips" + SmithingInterface.getForLevel(player, 5), 1108));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 90) + "Bolts" + SmithingInterface.getForLevel(player, 5), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 89) + "Sword" + SmithingInterface.getForLevel(player, 4), 1085));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 89) + "Bolts" + SmithingInterface.getForLevel(player, 4), 9144));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 89) + "Nails" + SmithingInterface.getForLevel(player, 4), 13358));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 88) + "Medium Helm" + SmithingInterface.getForLevel(player, 3), 1102));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 87) + "Mace" + SmithingInterface.getForLevel(player, 2), 1093));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 85) + "Dagger" + SmithingInterface.getForLevel(player, 1), 1094));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 86) + "Axe" + SmithingInterface.getForLevel(player, 1), 1091));
		ActionSender.sendUpdateItem34(player, 1119, 0, new Item(1213, 1)); // dagger
		ActionSender.sendUpdateItem34(player, 1120, 0, new Item(1359, 1)); // axe
		ActionSender.sendUpdateItem34(player, 1121, 0, new Item(1113, 1)); // chain
		ActionSender.sendUpdateItem34(player, 1122, 0, new Item(1147, 1)); // med
		ActionSender.sendUpdateItem34(player, 1123, 0, new Item(9381, 10)); // Bolts

		ActionSender.sendUpdateItem34(player, 1119, 1, new Item(1289, 1)); // s-sword
		ActionSender.sendUpdateItem34(player, 1120, 1, new Item(1432, 1)); // mace
		ActionSender.sendUpdateItem34(player, 1121, 1, new Item(1079, 1)); // platelegs
		ActionSender.sendUpdateItem34(player, 1122, 1, new Item(1163, 1)); // full_helm
		ActionSender.sendUpdateItem34(player, 1123, 1, new Item(44, 15)); // arrowtips

		ActionSender.sendUpdateItem34(player, 1119, 2, new Item(1333, 1)); // scimmy
		ActionSender.sendUpdateItem34(player, 1120, 2, new Item(1347, 1)); // warhammer
		ActionSender.sendUpdateItem34(player, 1121, 2, new Item(1093, 1)); // plateskirt
		ActionSender.sendUpdateItem34(player, 1122, 2, new Item(1185, 1)); // Sq_shield
		ActionSender.sendUpdateItem34(player, 1123, 2, new Item(868, 5)); // throwing-knives

		ActionSender.sendUpdateItem34(player, 1119, 3, new Item(1303, 1)); // longsword
		ActionSender.sendUpdateItem34(player, 1120, 3, new Item(1373, 1)); // battleaxe
		ActionSender.sendUpdateItem34(player, 1121, 3, new Item(1127, 1)); // platebody
		ActionSender.sendUpdateItem34(player, 1122, 3, new Item(1201, 1)); // kiteshield
		ActionSender.sendUpdateItem34(player, 1123, 3, new Item(-1, 1));

		ActionSender.sendUpdateItem34(player, 1119, 4, new Item(1319, 1)); // 2h_sword
		ActionSender.sendUpdateItem34(player, 1122, 4, new Item(4824, 15)); // nails

		player.getSession().send(new SendStringPacket("", 1135));
		player.getSession().send(new SendStringPacket("", 1134));
		player.getSession().send(new SendStringPacket("", 11461));
		player.getSession().send(new SendStringPacket("", 11459));
		player.getSession().send(new SendStringPacket("", 1132));
		player.getSession().send(new SendStringPacket("", 1096));
		player.getSession().send(new SendStringPacket(" ", 11460));
		player.getInterfaceState().openInterface(994);
	}

	private static void makeSteelInterface(final Player player) {
		final String fiveb = SmithingInterface.getForBars(player, 2353, 5);
		final String threeb = SmithingInterface.getForBars(player, 2353, 3);
		final String twob = SmithingInterface.getForBars(player, 2353, 2);
		final String oneb = SmithingInterface.getForBars(player, 2353, 1);
		player.getSession().send(new SendStringPacket(fiveb + "5 Bars" + fiveb, 1112));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1109));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1110));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1118));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1111));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1095));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1115));
		player.getSession().send(new SendStringPacket(threeb + "3 Bars" + threeb, 1090));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1113));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1116));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1114));
		player.getSession().send(new SendStringPacket(twob + "2 Bars" + twob, 1089));
		player.getSession().send(new SendStringPacket(" ", 8428));
		player.getSession().send(new SendStringPacket(" ", 8429));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1124));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1125));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1126));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1127));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1128));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1129));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1130));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1131));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 13357));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1132));
		player.getSession().send(new SendStringPacket(oneb + "1 Bar" + oneb, 1135));
		player.getSession().send(new SendStringPacket("", 11459));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 48) + "Plate Body" + SmithingInterface.getForLevel(player, 18), 1101));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 46) + "Plate Legs" + SmithingInterface.getForLevel(player, 16), 1099));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 46) + "Plate Skirt" + SmithingInterface.getForLevel(player, 16), 1100));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 44) + "2 Hand Sword" + SmithingInterface.getForLevel(player, 14), 1088));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 42) + "Kite Shield" + SmithingInterface.getForLevel(player, 12), 1105));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 41) + "Chain Body" + SmithingInterface.getForLevel(player, 11), 1098));
		player.getSession().send(new SendStringPacket("", 11461));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 40) + "Battle Axe" + SmithingInterface.getForLevel(player, 10), 1092));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 39) + "Warhammer" + SmithingInterface.getForLevel(player, 9), 1083));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 38) + "Square Shield" + SmithingInterface.getForLevel(player, 8), 1104));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 37) + "Full Helm" + SmithingInterface.getForLevel(player, 7), 1103));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 37) + "Throwing Knives" + SmithingInterface.getForLevel(player, 7), 1106));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 36) + "Long Sword" + SmithingInterface.getForLevel(player, 6), 1086));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 35) + "Scimitar" + SmithingInterface.getForLevel(player, 5), 1087));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 35) + "Arrowtips" + SmithingInterface.getForLevel(player, 5), 1108));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 35) + "Bolts" + SmithingInterface.getForLevel(player, 5), 1107));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 34) + "Sword" + SmithingInterface.getForLevel(player, 4), 1085));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 34) + "Bolts" + SmithingInterface.getForLevel(player, 4), 9141));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 34) + "Nails" + SmithingInterface.getForLevel(player, 4), 13358));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 33) + "Medium Helm" + SmithingInterface.getForLevel(player, 3), 1102));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 32) + "Mace" + SmithingInterface.getForLevel(player, 2), 1093));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 30) + "Dagger" + SmithingInterface.getForLevel(player, 1), 1094));
		player.getSession().send(new SendStringPacket(SmithingInterface.getForLevel(player, 31) + "Axe" + SmithingInterface.getForLevel(player, 1), 1091));
		player.getSession().send(new SendStringPacket(" ", 1096));
		player.getSession().send(new SendStringPacket(" ", 1134));

		ActionSender.sendUpdateItem34(player, 1119, 0, new Item(1207, 1)); // dagger
		ActionSender.sendUpdateItem34(player, 1120, 0, new Item(1353, 1)); // axe
		ActionSender.sendUpdateItem34(player, 1121, 0, new Item(1105, 1)); // chain
		ActionSender.sendUpdateItem34(player, 1122, 0, new Item(1141, 1)); // med
		ActionSender.sendUpdateItem34(player, 1123, 0, new Item(9378, 10)); // Bolts

		ActionSender.sendUpdateItem34(player, 1119, 1, new Item(1281, 1)); // s-sword
		ActionSender.sendUpdateItem34(player, 1120, 1, new Item(1424, 1)); // mace
		ActionSender.sendUpdateItem34(player, 1121, 1, new Item(1069, 1)); // platelegs
		ActionSender.sendUpdateItem34(player, 1122, 1, new Item(1157, 1)); // full_helm
		ActionSender.sendUpdateItem34(player, 1123, 1, new Item(41, 15)); // arrowtips

		ActionSender.sendUpdateItem34(player, 1119, 2, new Item(1325, 1)); // scimmy
		ActionSender.sendUpdateItem34(player, 1120, 2, new Item(1339, 1)); // warhammer
		ActionSender.sendUpdateItem34(player, 1121, 2, new Item(1083, 1)); // plateskirt
		ActionSender.sendUpdateItem34(player, 1122, 2, new Item(1185, 1)); // Sq_shield
		ActionSender.sendUpdateItem34(player, 1123, 2, new Item(865, 5)); // throwing-knives

		ActionSender.sendUpdateItem34(player, 1119, 3, new Item(1295, 1)); // longsword
		ActionSender.sendUpdateItem34(player, 1120, 3, new Item(1365, 1)); // battleaxe
		ActionSender.sendUpdateItem34(player, 1121, 3, new Item(1119, 1)); // platebody
		ActionSender.sendUpdateItem34(player, 1122, 3, new Item(1119, 1)); // kiteshield

		ActionSender.sendUpdateItem34(player, 1119, 4, new Item(1311, 1)); // 2h_sword
		ActionSender.sendUpdateItem34(player, 1122, 4, new Item(1539, 15)); // nails

		player.getInterfaceState().openInterface(994);
	}

	public static void showSmithInterface(Player c, int itemId) {
		if (!c.getInventory().contains(2347)) {
			c.sendMessage("You need a hammer to do this!");
			return;
		}
		switch (itemId) {
		case 2349:
			SmithingInterface.makeBronzeInterface(c);
			break;
		case 2351:
			SmithingInterface.makeIronInterface(c);
			break;
		case 2353:
			SmithingInterface.makeSteelInterface(c);
			break;
		case 2359:
			SmithingInterface.makeMithInterface(c);
			break;
		case 2361:
			SmithingInterface.makeAddyInterface(c);
			break;
		case 2363:
			SmithingInterface.makeRuneInterface(c);
			break;
		}
	}

}
