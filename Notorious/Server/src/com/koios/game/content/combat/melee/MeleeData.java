package com.koios.game.content.combat.melee;

import java.util.Optional;

import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

public class MeleeData {

	public static int balmungDamage(Player player) {
		int damage = CalculateHit.execute(player, player.getCombat().getTarget(), AttackType.MELEE);
		return damage *= 1.75;
	}

	public static int getAttackDelay(Player player) {
		int increment = player.miasmicCloud > 0 ? 2 : 0;
		int weap = player.getEquipment().get(Equipment.SLOT_WEAPON) != null ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1;
		Optional<ItemDefinition> definition = ItemDefinition.getOptional(weap);
		String weaponName = definition != null && definition.isPresent() ? definition.get().getName().toLowerCase() : "unarmed";

		switch (weap) {
		case 13902: // Statius's warhammer
		case 13905: // Vesta's spear
		case 13899: // Vesta's longsword
			return 5 + increment;
		}

		for (String[] element : DELAY_DATA) {
			if (weaponName.toLowerCase().contains(element[0].toLowerCase().replaceAll("_", " "))) {
				return Integer.parseInt(element[1]) + increment;
			}
		}

		if (player.spellCasting) {
			switch (MagicData.MAGIC_SPELLS[player.spellId][0]) {
			case 12871: // ice blitz
			case 13023: // shadow barrage
			case 12891: // ice barrage
				return 5;

			default:
				return 6;
			}
		}

		return 4 + increment;
	}

	public static int getBlockAnimation(EntityCharacter entity) {
		if (entity.isMob()) {
			return getMobDefenceAnimation(entity.toMob());
		} else {
			return getBlockEmote(entity.toPlayer());
		}
	}

	public static int getBlockEmote(Player player) {
		Item shield = player.getEquipment().get(Equipment.SLOT_SHIELD);
		int id = shield == null ? -1 : shield.getId();
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		int weap = weapon == null ? -1 : weapon.getId();

		switch (id) {
		case 8844:
		case 8845:
		case 8846:
		case 8847:
		case 8848:
		case 8849:
		case 8850:
		case 17273:
		case 20072:
			return 4177;

		case 1171:
		case 1173:
		case 1175:
		case 1177:
		case 1179:
		case 1181:
		case 1183:
		case 1185:
		case 1187:
		case 1189:
		case 1191:
		case 1193:
		case 1195:
		case 1197:
		case 1199:
		case 1201:
		case 1540:
		case 2589:
		case 2597:
		case 2603:
		case 2611:
		case 2621:
		case 2629:
		case 2659:
		case 2675:
		case 2890:
		case 3122:
		case 3488:
		case 3758:
		case 4156:
		case 4224:
		case 4226:
		case 4227:
		case 4228:
		case 4229:
		case 4230:
		case 4231:
		case 4232:
		case 4233:
		case 4234:
		case 4235:
		case 4507:
		case 4512:
		case 6215:
		case 6217:
		case 6219:
		case 6221:
		case 6223:
		case 6225:
		case 6227:
		case 6229:
		case 6231:
		case 6233:
		case 6235:
		case 6237:
		case 6239:
		case 6241:
		case 6243:
		case 6245:
		case 6247:
		case 6249:
		case 6251:
		case 6253:
		case 6255:
		case 6257:
		case 6259:
		case 6261:
		case 6263:
		case 6265:
		case 6267:
		case 6269:
		case 6271:
		case 6273:
		case 6275:
		case 6277:
		case 6279:
		case 6524:
		case 6631:
		case 6633:
		case 6894:
		case 7332:
		case 7334:
		case 7336:
		case 7338:
		case 7340:
		case 7342:
		case 7344:
		case 7346:
		case 7348:
		case 7350:
		case 7352:
		case 7354:
		case 7356:
		case 7358:
		case 7360:
		case 7676:
		case 9731:
		case 10352:
		case 10665:
		case 10667:
		case 10669:
		case 10671:
		case 10673:
		case 10675:
		case 10677:
		case 10679:
		case 10827:
		case 11283:
		case 11284:
		case 12908:
		case 12910:
		case 12912:
		case 12915:
		case 12914:
		case 12916:
		case 12918:
		case 12920:
		case 12922:
		case 12924:
		case 12926:
		case 12928:
		case 12929:
		case 12930:
		case 12932:
		case 12934:
		case 13506:
		case 13734:
		case 13736:
		case 13738:
		case 13740:
		case 13742:
		case 13744:
		case 13964:
		case 13966:
		case 14578:
		case 14579:
		case 15808:
		case 15809:
		case 15810:
		case 15811:
		case 15812:
		case 15813:
		case 15814:
		case 15815:
		case 15816:
		case 15817:
		case 15818:
		case 16079:
		case 16933:
		case 16934:
		case 16971:
		case 16972:
		case 17341:
		case 17342:
		case 17343:
		case 17344:
		case 17345:
		case 17346:
		case 17347:
		case 17348:
		case 17349:
		case 17351:
		case 17353:
		case 17355:
		case 17357:
		case 17359:
		case 17361:
		case 17405:
		case 18359:
		case 18360:
		case 18361:
		case 18362:
		case 18363:
		case 18364:
		case 18582:
		case 18584:
		case 18691:
		case 18709:
		case 18747:
		case 19340:
		case 19345:
		case 19352:
		case 19410:
		case 19426:
		case 19427:
		case 19440:
		case 19441:
		case 19442:
		case 19749:
			return 1156;
		}

		switch (weap) {
		case 7671:
		case 7673:
			return 3679;

		case 1291:
		case 1293:
		case 1295:
		case 1297:
		case 1299:
		case 1301:
		case 1303:
		case 1305:
		case 6607:
		case 13474:
		case 13899:
		case 13901:
		case 13923:
		case 13925:
		case 13982:
		case 13984:
		case 16024:
		case 16025:
		case 16026:
		case 16027:
		case 16028:
		case 16029:
		case 16030:
		case 16031:
		case 16032:
		case 16033:
		case 16034:
		case 16383:
		case 16385:
		case 16387:
		case 16389:
		case 16391:
		case 16393:
		case 16395:
		case 16397:
		case 16399:
		case 16401:
		case 16403:
		case 16961:
		case 16963:
		case 18351:
		case 18352:
		case 18367:
		case 18368:
		case 1321:
		case 1323:
		case 1325:
		case 1327:
		case 1329:
		case 1331:
		case 1333:
		case 4587:
		case 6611:
		case 13979:
		case 13981:
		case 14097:
		case 14287:
		case 14289:
		case 14291:
		case 14293:
		case 14295:
		case 746:
		case 747:
		case 1203:
		case 1205:
		case 1207:
		case 1209:
		case 1211:
		case 1213:
		case 1215:
		case 1217:
		case 1219:
		case 1221:
		case 1223:
		case 1225:
		case 1227:
		case 1229:
		case 1231:
		case 1233:
		case 1235:
		case 1813:
		case 5668:
		case 5670:
		case 5672:
		case 5674:
		case 5676:
		case 5678:
		case 5680:
		case 5682:
		case 5684:
		case 5686:
		case 5688:
		case 5690:
		case 5692:
		case 5694:
		case 5696:
		case 5698:
		case 5700:
		case 5702:
		case 6591:
		case 6593:
		case 6595:
		case 6597:
		case 8872:
		case 8873:
		case 8875:
		case 8877:
		case 8879:
		case 13976:
		case 13978:
		case 14297:
		case 14299:
		case 14301:
		case 14303:
		case 14305:
		case 15826:
		case 15848:
		case 15849:
		case 15850:
		case 15851:
		case 15853:
		case 15854:
		case 15855:
		case 15856:
		case 15857:
		case 15858:
		case 15859:
		case 15860:
		case 15861:
		case 15862:
		case 15863:
		case 15864:
		case 15865:
		case 15866:
		case 15867:
		case 15868:
		case 15869:
		case 15870:
		case 15871:
		case 15872:
		case 15873:
		case 15874:
		case 15875:
		case 15876:
		case 15877:
		case 15879:
		case 15880:
		case 15881:
		case 15882:
		case 15883:
		case 15884:
		case 15885:
		case 15886:
		case 15887:
		case 15888:
		case 15889:
		case 15890:
		case 15891:
		case 16757:
		case 16759:
		case 16761:
		case 16763:
		case 16765:
		case 16767:
		case 16769:
		case 16771:
		case 16773:
		case 16775:
		case 16777:
		case 16779:
		case 16781:
		case 16783:
		case 16785:
		case 16787:
		case 16789:
		case 16791:
		case 16793:
		case 16795:
		case 16797:
		case 16799:
		case 16801:
		case 16803:
		case 16805:
		case 16807:
		case 16809:
		case 16811:
		case 16813:
		case 16815:
		case 16817:
		case 16819:
		case 16821:
		case 16823:
		case 16825:
		case 16827:
		case 16829:
		case 16831:
		case 16833:
		case 16835:
		case 16837:
		case 16839:
		case 16841:
		case 16843:
		case 17275:
		case 17277:
		case 667:
		case 1277:
		case 1279:
		case 1281:
		case 1283:
		case 1285:
		case 1287:
		case 1289:
		case 19780:
		case 16035:
		case 16036:
		case 16037:
		case 16038:
		case 16039:
		case 16040:
		case 16041:
		case 16042:
		case 16043:
		case 16044:
		case 16045:
		case 16935:
		case 16937:
		case 16939:
		case 16941:
		case 16943:
		case 16945:
		case 16947:
		case 16949:
		case 16951:
		case 16953:
		case 16955:
		case 16957:
		case 16959:
		case 18349:
		case 18350:
		case 18365:
		case 18366:
			return 12030;

		case 4151:
		case 13444:
		case 14661:
		case 15441:
		case 15442:
		case 15443:
		case 15444:
		case 21369:
		case 21371:
		case 21372:
		case 21373:
		case 21374:
		case 21375:
		case 23691:
			return 11974;

		case 8844:
		case 8845:
		case 8846:
		case 8847:
		case 8848:
		case 8849:
		case 8850:
		case 15455:
		case 15456:
		case 15457:
		case 15458:
		case 15459:
		case 15825:
		case 17273:
		case 20072:
			return 4177;

		case 3095:
		case 3096:
		case 3097:
		case 3098:
		case 3099:
		case 3100:
		case 3101:
		case 6587:
		case 14484:
			return 397;

		case 1379:
		case 1381:
		case 1383:
		case 1385:
		case 1387:
		case 1389:
		case 1391:
		case 1393:
		case 1395:
		case 1397:
		case 1399:
		case 1401:
		case 1403:
		case 1405:
		case 1407:
		case 1409:
		case 2415:
		case 2416:
		case 2417:
		case 3053:
		case 3054:
		case 3055:
		case 3056:
		case 4170:
		case 4675:
		case 4710:
		case 4862:
		case 4863:
		case 4864:
		case 4865:
		case 4866:
		case 4867:
		case 6562:
		case 6603:
		case 6727:
		case 9084:
		case 9091:
		case 9092:
		case 9093:
		case 11736:
		case 11738:
		case 11739:
		case 11953:
		case 13406:
		case 13629:
		case 13630:
		case 13631:
		case 13632:
		case 13633:
		case 13634:
		case 13635:
		case 13636:
		case 13637:
		case 13638:
		case 13639:
		case 13640:
		case 13641:
		case 13642:
		case 6908:
		case 6910:
		case 6912:
		case 6914:
			return 415;

		case 4153:
		case 6528:
			return 1666;

		case 1307:
		case 1309:
		case 1311:
		case 1313:
		case 1315:
		case 1317:
		case 1319:
		case 6609:
		case 7158:
		case 7407:
		case 16127:
		case 16128:
		case 16129:
		case 16130:
		case 16131:
		case 16132:
		case 16133:
		case 16134:
		case 16135:
		case 16136:
		case 16137:
		case 16889:
		case 16891:
		case 16893:
		case 16895:
		case 16897:
		case 16899:
		case 16901:
		case 16903:
		case 16905:
		case 16907:
		case 16909:
		case 16973:
		case 18369:
		case 20874:
		case 11694:
		case 11696:
		case 11698:
		case 11700:
		case 11730:
			return 13051;

		case 10034: // Chins
		case 10033:
			return 3176;

		case 18355:
		case 15486:
		case 3190:
		case 3192:
		case 3194:
		case 3196:
		case 3198:
		case 3200:
		case 3202:
		case 3204:
		case 6599:
			return 12806;

		case 15403:
			return 12004;

		case 18353:
		case 18354:
		case 16425:
			return 13054;

		case 15241:
			return 12156;

		case 4718:
			return 12004;

		case 10887:
			return 5866;

		case 4755:
			return 2063;

		default:
			return 424;
		}
	}

	public static int getHitDelay(Player player) {
		int weap = player.getEquipment().get(Equipment.SLOT_WEAPON) == null ? -1 : player.getEquipment().get(Equipment.SLOT_WEAPON).getId();
		Optional<ItemDefinition> definition = ItemDefinition.getOptional(weap);
		String weaponName = definition.isPresent() ? definition.get().getName().toLowerCase() : "unarmed";

		if (player.spellCasting) {
			switch (MagicData.MAGIC_SPELLS[player.spellId][0]) {
			case 12891:
				return 4;
			case 12871:
				return 6;
			default:
				return 4;
			}
		} else {
			if (weaponName.contains("knife") || weaponName.contains("dart") || weaponName.contains("javelin") || weaponName.contains("thrownaxe")) {
				return 3;
			}

			if (weaponName.contains("cross") || weaponName.contains("c'bow")) {
				return 4;
			}

			if (weaponName.contains("bow") && !player.dbowSpec) {
				return 4;
			} else if (player.dbowSpec) {
				return 4;
			}

			switch (weap) {
			case 6522: // Toktz-xil-ul
				return 3;

			case 15241: // hand cannon
				return 3;

			default:
				return 2;
			}
		}
	}

	public static int getMobDefenceAnimation(Mob mob) {
		if (mob.isFamiliar()) {
			return mob.getDefinition().getAttackAnimation();
		}
		switch (mob.getId()) {
		case 9000:
			return 6983;

		case 8528:
			return 12693;

		case 8596:
			return 11198;

		case 8597:
		case 9437:
			return 11203;

		case 3067: // Leon
			return 13051;

		case 936:
		case 937:
			return 5489;

		case 6247:
			return 6966;

		case 1977:
		case 1913:
			return 12030;

		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
			return 5096;

		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 5395;

		case 5247: // Penance queen
			return 5408;

		case 2025:
			return 420;

		case 2026:
			return 12004;

		case 2027:
		case 2028:
		case 2029:
			return 424;

		case 2030:
			return 2063;

		case 5666:
			return 5897;

		case 100:
			return 6189;

		case 101:
			return 6183;

		case 3835:
			return 6232;

		case 2037:
			return 5489;

		case 5529:
			return 5783;

		case 10127:
			return 13170;

		case 10057:
			return 10818;

		case 5904:
			return 6330;

		case 5903:
			return 6346;

		case 7133:
			return 8755;

		case 6624:
			return 7413;

		case 6619:
			return 7443;

		case 6649:
			return 7469;

		case 6646:
			return 7462;

		case 3836:
			return 6237;

		case 2783:
			return 2732;

		case 8133:
			return 10058;

		case 10141:
			return 13601;

		case 8349:
		case 8350:
		case 8351:
			return 10923;

		case 9947:
			return 13771;

		case 6260:
			return 7061;

		case 6261:
		case 6263:
		case 6265:
			return 6155;

		case 6222:
			return 6974;

		case 6223:
		case 6225:
		case 6227:
			return 6952;

		case 6203:
			return 6944;

		case 6204:
		case 6206:
		case 6208:
			return 65;

		case 6248:
			return 6375;

		case 6250:
			return 7017;

		case 6252:
			return 4311;

		case 6229:
		case 6230:
		case 6231:
		case 6232:
		case 6233:
		case 6234:
		case 6235:
		case 6236:
		case 6237:
		case 6238:
		case 6239:
		case 6240:
		case 6241:
		case 6242:
		case 6243:
		case 6244:
		case 6245:
		case 6246:
			return 6952;

		case 6267:
			return 360;

		case 6268:
			return 2933;

		case 6269:
		case 6270:
			return 4651;

		case 6271:
		case 6272:
		case 6273:
		case 6274:
			return 4322;

		case 6275:
			return 165;

		case 6276:
		case 6277:
		case 6278:
			return 4322;

		case 6279:
		case 6280:
			return 6183;

		case 6281:
			return 6136;

		case 6282:
			return 6189;

		case 6283:
			return 6183;

		case 6210:
			return 6578;

		case 6211:
			return 170;

		case 6212:
		case 6213:
			return 6538;

		case 6215:
			return 1550;

		case 6216:
		case 6217:
			return 1581;

		case 6218:
			return 4301;

		case 6258:
			return 2561;

		case 10775:
			return 13154;

		case 113:
			return 129;

		case 114:
			return 360;

		case 1265:
		case 2452:
			return 1313;

		case 118:
			return 100;

		case 2263:
			return 2181;

		case 82:// Lesser Demon
		case 83:// Greater Demon
		case 84:// Black Demon
		case 4695:// Lesser Demon
		case 4698:// Greater Demon
		case 4705:// Black Demon
		case 1472:// jungle demon
			return 65;

		case 3347:
		case 3346:
			return 3325;

		case 1192:
			return 1244;

		case 3060:
			return 2964;

		case 2892: // Spinolyp
		case 2894: // Spinolyp
		case 2896: // Spinolyp
			return 2869;

		case 1624:
			return 1555;

		case 3200:
			return 3148;

		case 1354:
		case 1341:
		case 2455:// dagannoth
		case 2454:
		case 2456:
			return 1340;

		case 127:
			return 186;

		case 119:
			return 100;

		case 2881: // supreme
		case 2882: // prime
		case 2883: // rex
			return 2852;

		case 3452:// penance queen
			return 5413;

		case 2743:
			return 9268;

		case 50:// drags
		case 53:
		case 54:
		case 55:
		case 941:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
			return 89;

		case 2598:
		case 2599:
		case 2600:
		case 2610:
		case 2601:
		case 2602:
		case 2603:
		case 2606:// tzhaar-xil
		case 2591:
		case 2604:// tzhar-hur
			return 2606;

		case 66:
		case 67:
		case 168:
		case 169:
		case 162:
		case 68:// gnomes
			return 193;

		case 160:
		case 161:
			return 194;

		case 163:
		case 164:
			return 193;

		case 438:
		case 439:
		case 440:
		case 441:
		case 442: // Tree spirit
		case 443:
			return 95;

		case 391:
		case 392:
		case 393:
		case 394:
		case 395:// river troll
		case 396:
			return 285;

		case 413:
		case 414:
		case 415:
		case 416:
		case 417:// rock golem
		case 418:
			return 154;

		case 9052:
			return 2977;

		case 2734:
		case 2627:// tzhaar
			return 9231;

		case 2630:
		case 2629:
		case 2736:
		case 2738:
			return 9235;

		case 2631:
		case 2632:
			return 9242;

		case 2741:
			return 9253;

		case 908:
			return 129;

		case 909:
			return 147;

		case 911:
			return 65;

		case 1459:// monkey guard
			return 1403;

		case 1633: // pyrefiend
		case 3406:
			return 1581;

		case 1612:// banshee
			return 1525;

		case 1648:
		case 1649:
		case 1650:
		case 1651:
		case 1652:
		case 1653:
		case 1654:
		case 1655:
		case 1656:
		case 1657:// crawling hand
			return 1591;

		case 1604:
		case 1605:
		case 1606:
		case 1607:// aberrant specter
			return 1509;

		case 1618:
		case 1619:// bloodveld
			return 1550;

		case 1643:
		case 1644:
		case 1645:
		case 1646:
		case 1647:// infernal mage
			return 430;

		case 1613:// nechryael
			return 1529;

		case 1610:
		case 1611:// gargoyle
			return 1519;

		case 1615:// abyssal demon
			return 1537;

		case 1770:
		case 1771:
		case 1772:
		case 1773:
		case 2678:
		case 2679:
		case 1774:
		case 1775:
		case 1776:// goblins
			return 312;

		case 132: // monkey
			return 221;

		case 1030:
		case 1031:
		case 1032:
		case 1033:
		case 1034:
		case 1035: // wolfman
			return 6538;

		case 1456:// monkey archer
			return 1395;

		case 108:// scorpion
		case 1477:
			return 247;
		case 107:
		case 144:
			return 6255;

		case 1125:// dad
			return 285;

		case 1096:
		case 1097:
		case 1098:
		case 1942:
		case 1101:// troll
		case 1106:
			return 285;
		case 1095:
			return 285;

		case 123:
		case 122:// hobgoblin
			return 165;

		case 49:// hellhound
		case 142:
		case 95:
			return 6578;

		case 1593:
		case 152:
		case 45:
		case 1558: // wolf
		case 1954:
			return 76;

		case 89:
			return 6375;
		case 133: // unicorns
			return 290;

		case 105:// bear
			return 4921;

		case 74:
		case 75:
		case 76:
			return 5574;

		case 73:
		case 751: // zombie
		case 77:
			return 5574;

		case 60:
		case 64:
		case 59:
		case 61:
		case 63:
		case 134:
		case 2035: // spider
		case 62:
		case 1009:
			return 147;

		case 103:
		case 749:
		case 104:
		case 655:
		case 491: // ghost
			return 124;

		case 1585:
		case 110: // giant
			return 4671;

		case 111:
			return 4671;

		case 117:
		case 116:
		case 112:
			return 4651;

		case 2889:
			return 2860;

		case 81: // cow
		case 397:
			return 5850;

		case 708: // imp
			return 170;

		case 86:
		case 87:
			return 139;

		case 47:// rat
			return 2706;

		case 2457:
			return 2366;

		case 128: // snake
		case 1479:
			return 276;

		case 1017:
		case 2693:
		case 41: // chicken
			return 55;

		case 90:
		case 91:
		case 92:
		case 93: // skeleton
			return 261;

		case 1:
			return 424;

		default:
			return -1;
		}
	}

	public static void getPlayerAnimIndex(Player player) {
		// Optional<ItemDefinition> definition =
		// ItemDefinition.getOptional(player.getEquipment()
		// .get(Equipment.SLOT_WEAPON).getId());
		// String weaponName = definition.isPresent() ?
		// definition.get().getName().toLowerCase() : "unarmed";

		/*
		 * Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON); String weaponName = weapon == null ? "unarmed" : weapon.getDefinition().getName().toLowerCase();
		 * 
		 * player.playerStandIndex = 0x328; player.playerTurnIndex = 0x337; player.playerWalkIndex = 0x333; player.playerTurn180Index = 0x334; player.playerTurn90CWIndex = 0x335;
		 * player.playerTurn90CCWIndex = 0x336; player.playerRunIndex = 0x338;
		 * 
		 * if (weaponName.contains("ahrim")) { player.playerStandIndex = 809; player.playerWalkIndex = 1146; player.playerRunIndex = 1210; return; }
		 * 
		 * if (weaponName.contains("balmung")) { player.playerStandIndex = 12000; player.playerWalkIndex = 11999; player.playerRunIndex = 12001; return; }
		 * 
		 * if (weaponName.contains("staff") || weaponName.contains("halberd") || weaponName.contains("guthan") || weaponName.contains("rapier") || weaponName.contains("wand")) {
		 * weaponInfo(player, 12010, 1146, 1210); player.playerTurnIndex = 1205; player.playerTurn180Index = 1206; player.playerTurn90CWIndex = 1207; player.playerTurn90CCWIndex =
		 * 1208; return; }
		 * 
		 * if (weaponName.contains("dharok")) { player.playerStandIndex = 0x811; player.playerWalkIndex = 0x67F; player.playerRunIndex = 12001; return; }
		 * 
		 * if (weaponName.contains("verac")) { weaponInfo(player, 1832, 1830, 1831); return; }
		 * 
		 * if (weaponName.contains("karil")) { player.playerStandIndex = 2074; player.playerWalkIndex = 2076; player.playerRunIndex = 2077; return; }
		 * 
		 * if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.contains("saradomin sw")) { player.playerStandIndex = 7047; player.playerWalkIndex =
		 * 7046; player.playerRunIndex = 7039; return; }
		 * 
		 * if (weaponName.contains("bow")) { player.playerStandIndex = 808; player.playerWalkIndex = 819; player.playerRunIndex = 824; return; }
		 * 
		 * if (weapon == null) { return; }
		 * 
		 * switch (weapon.getId()) { case 4151: case 21371: case 15445: case 15444: case 15443: case 15442: case 15441: weaponInfo(player, 11973, 11975, 11976); break;
		 * 
		 * case 15241: player.playerStandIndex = 12155; player.playerWalkIndex = 12154; player.playerRunIndex = 12154; break;
		 * 
		 * case 10887: player.playerStandIndex = 5869; player.playerWalkIndex = 5867; player.playerRunIndex = 5868; break;
		 * 
		 * case 18355: player.playerStandIndex = 808; break;
		 * 
		 * case 6528: player.playerStandIndex = 0x811; player.playerWalkIndex = 2064; player.playerRunIndex = 1664; break;
		 * 
		 * case 16425: case 18353: case 4153: case 7668: player.playerStandIndex = 1662; player.playerWalkIndex = 1663; player.playerRunIndex = 1664; break;
		 * 
		 * case 1305: case 13982: player.playerStandIndex = 809; break;
		 * 
		 * case 19784: player.playerStandIndex = 809; break;
		 * 
		 * case 11716: player.playerRunIndex = 12016; player.playerWalkIndex = 12012; player.playerStandIndex = 12010; }
		 */
	}

	public static int getWepAnim(Player player) {

		int weap = player.getEquipment().get(Equipment.SLOT_WEAPON) == null ? -1 : player.getEquipment().get(Equipment.SLOT_WEAPON).getId();
        weap=com.koios.game.arpg.EquipmentBases.template(weap);
		Optional<ItemDefinition> definition = ItemDefinition.getOptional(weap);
		String weaponName = definition.isPresent() ? definition.get().getName().toLowerCase() : "unarmed";

		if (weap <= 0) {
			if (player.getCombat().getAttackStyle() == AttackStyle.ACCURATE || player.getCombat().getAttackStyle() == AttackStyle.DEFENSIVE) {
				return 422;
			} else if (player.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE) {
				return 423;
			}
		}

		if (weaponName.contains("knife") || weaponName.contains("dart") || weaponName.contains("javelin") || weaponName.contains("thrownaxe")) {
			return 806;
		}

		if (weaponName.toLowerCase().contains("boxing")) {
			return 3678;
		}

		if (weaponName.contains("halberd")) {
			return 440;
		}

		if (weaponName.contains("chinchompa")) {
			return 2779;
		}

		if (weaponName.contains("pickaxe") || weaponName.contains("mace") || weaponName.contains("hammer") || weaponName.contains("chaotic staff")
				|| weaponName.contains("tzhaar-ket-em")) {
			return 13035;
		}

		if (weaponName.contains("wand") || weaponName.contains("staff") && !weaponName.contains("ahrim")) {
			return 393;
		}

		if (weaponName.endsWith("dagger") && !weaponName.contains("dragon dagger")) {
			return 13049;
		}

		if (weaponName.startsWith("dragon dagger") || weaponName.startsWith("drag dagger")) {
			return 402;
		}

		if (weaponName.equalsIgnoreCase("gravite 2h sword")) {
			return 7048;
		}

		if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.contains("aradomin sword")) {
			return 7041;
		}

		if (weaponName.contains("sword") || weaponName.contains("scimitar")) {
			return 390;
		}

		if (weaponName.contains("bow") && !weaponName.contains("'bow") && !weaponName.contains("crossbow")) {
			return 426;
		}

		if (weaponName.contains("'bow")) {
			return 4230;
		}

		switch (weap) { // if you don't want to use strings
		case 15241: // hand cannon
			return 12153;

		case 11716: // zamorakian spear
			return 12006;

		// dragon daggers
		case 1215:
		case 1231:
		case 5680:
		case 5698:
			return 402;

		case 20671: // Brackish blade
			return 13049;

		case 10887:
			return 5865;

		case 6522:
			return 2614;

		// Cross bows
		case 18357: // chaotic
		case 9174: // bronze
		case 9183: // addy
		case 9179: // steel
		case 9177: // iron
		case 837: // crossbow
		case 9181: // mith
		case 9185: // rune
			return 4230;

		case 4153: // granite maul
			return 1665;

		case 4726: // guthan
			return 2080;

		case 4747: // torag
			return 0x814;

		case 4718: // dharok
		case 15403:
			return 2066;

		case 4710: // ahrim
			return 406;

		case 4755: // verac
			return 2062;

		case 4734: // karil
			return 2075;

		case 4151:
		case 21371:
		case 15441:
		case 15442:
		case 15443:
		case 15444: // whips
			return 1658;

		case 18349:
		case 16955:
			return 13049;

		case 6528:
			return 2661;

		case 18353:
		case 16425:
			return 13055;

		default:
			return 390;
		}
	}

	public static boolean isUsingHalberd(Player player) {
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		int id = weapon == null ? -1 : weapon.getId();

		switch (id) {
		case 3190:
		case 3192:
		case 3194:
		case 3196:
		case 3198:
		case 3200:
		case 3202:
		case 3204:
			return true;
		}

		return false;
	}

	private static final String[][] DELAY_DATA = { { "dart", "2" }, { "knife", "2" }, { "Blisterwood stake", "3" }, { "Shortbow", "3" }, { "karils", "4" }, { "karil's", "4" },
			{ " Toktz-xil-ul", "4" }, { " Dagger", "4" }, { "Bronze sword", "4" }, { "Iron sword", "4" }, { "Steel sword", "4" }, { "Black sword", "4" }, { "Mithril sword", "4" },
			{ "Adamant sword", "4" }, { "Rune sword", "4" }, { "Scimitar", "4" }, { "Abyssal whip", "4" }, { "claws", "4" }, { "Zamorakian spear", "4" },
			{ "Saradomin sword", "4" }, { "Toktz-xil-ak", "4" }, { "Toktz-xil-ek", "4" }, { "Saradomin staff", "4" }, { "Zamorak staff", "4" }, { "Guthix staff", "4" },
			{ "Slayer's staff", "4" }, { "Ancient staff", "4" }, { "Gravite rapier", "4" }, { "rapier", "4" }, { "Armadyl battlestaff", "4" }, { "Longsword", "5" },
			{ "mace", "5" }, { "axe", "5" }, { "pickaxe", "5" }, { "Tzhaar-ket-em", "5" }, { "Torags hammers", "5" }, { "Guthans warspear", "5" }, { "Veracs flail", "5" },
			{ "Staff", "5" }, { "Staff of air", "5" }, { "Staff of water", "5" }, { "Staff of earth", "5" }, { "Staff of fire", "5" }, { "Magic staff", "5" },
			{ "Mystic fire staff", "5" }, { "Mystic fire staff", "5" }, { "Mystic water staff", "5" }, { "Mystic water staff", "5" }, { "Mystic air staff", "5" },
			{ "Mystic air staff", "5" }, { "Mystic earth staff", "5" }, { "Mystic earth staff", "5" }, { "Battlestaff", "5" }, { "Iban's staff", "5" }, { "Staff of light", "4" },
			{ "Salamander", "5" }, { "Maple longbow (sighted)", "5" }, { "Magic longbow (sighted)", "5" }, { "thrownaxe", "5" }, { "Comp ogre bow", "5" },
			{ "New crystal bow", "5" }, { "Crystal bow", "5" }, { "Seercull", "5" }, { "Chaotic longsword", "5" }, { "Gravite longsword", "5" }, { "Primal maul", "6" },
			{ "Battleaxe", "6" }, { "warhammer", "6" }, { "godsword", "6" }, { "Barrelchest anchor", "6" }, { "Ahrims staff", "6" }, { "Toktz-mej-tal", "6" },
			{ "Gravite 2h sword", "6" }, { "Chaotic maul", "6" }, { "Longbow", "6" }, { "Zamorak bow", "6" }, { "Saradomin bow", "6" }, { "Guthix bow", "6" }, { "javelin", "6" },
			{ "Dorgeshuun c'bow", "6" }, { "c'bow", "6" }, { "Zaryte bow", "6" }, { "Phoenix crossbow", "5" }, { "Sagaie", "6" }, { "Bolas", "6" }, { "Auspicious katana", "6" },
			{ "2h sword", "7" }, { "halberd", "7" }, { "Granite maul", "7" }, { "Balmung", "7" }, { "Tzhaar-ket-om", "7" }, { "Ivandis flail", "7" }, { "Hand cannon", "7" },
			{ "Dharoks greataxe", "7" }, { "Ogre bow", "8" }, { "Dark bow", "9" }, { "Dreadnip", "10" }, { "Swagger stick", "10" }, { "hatchet", "5" }, { "Crossbow", "5" },
			{ "Dharok's greataxe", "7" } };

}
