package com.koios.game.model.mob;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.activity.PestControl;
import com.koios.game.content.cluescroll.ClueScrollConstants;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.effects.impl.Poison;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.misc.TzhaarFightCaveConstants;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.MobDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.mob.impl.MobPet;
import com.koios.game.model.mob.impl.MobWildRevenant;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendEntityHintIconPacket;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class MobManager {

	public static final Mob[] mobs = new Mob[Constants.MAX_MOBS];
	static final int[] revIds = { 6688, 6689, 6729, 6691, 6730, 6688, 6689, 6729, 6691, 6730, 6688, 6689, 6729, 6691, 6730 };
	static final String[] VOID_KNIGHT_SHOUTS = { "We must not fail!", "Take down the portals", "The Void Knights will not fall!", "Hail the Void Knights!",
			"We are beating these scum!", "Don't let these creatures leech my health!!", "Do not let me die!!!", "Please....help me!", "For the knights we shall prevail!" };

	public MobManager() {
		Arrays.fill(mobs, null);
	}

	public void loadMobSpawns() {
		loadAutoSpawn("./data/cfg/spawn-config.cfg");
		spawnAllRevs();
	}

	/**
	 * @param player the Client , the player that got hit.
	 * @param index the NPC that hit the player and will receive the rebound damage.
	 * @param damage the damage the NPC has dealt to the player.
	 */
	public static void appendMobVengeance(Player player, Mob mob, int damage) {
		if (damage <= 0) {
			return;
		}

		player.speak("Taste vengeance!");
		player.getEF().renewEffect(EffectConstants.VENGEANCE_NAME);

		if (player.getHitpoints() - damage > 0) {
			int rebound = (int) (damage * 0.75);

			if (rebound > mob.hitpoints) {
				rebound = mob.hitpoints;
			}

			mob.setDamage(new Damage(rebound, HitIcons.NONE), player);
		}

		mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	static void dropItems(Mob mob, Player player) {
		int probability = 0;
		List<Integer> closed = new ArrayList<>();
		List<DropItem> drops = null;

		for (int[] table : MobConstants.DROP_TABLE) {
			if (mob.getId() == table[0]) {
				if (table[3] == 0) {
					mob.drop(table[1], table[4] == 0 ? table[2] : NumberUtil.random(1, table[2]));
				} else if (probability == table[3] || !closed.contains(Integer.valueOf(table[3])) && player.getRandom().nextInt(table[3]) == 0) {
					closed.add(table[3]);

					if (table[1] > Constants.MAX_ITEMS) {
						continue;
					}

					if (drops == null) {
						probability = table[3];
						drops = new ArrayList<>();
					} else if (probability < table[3]) {
						probability = table[3];
						drops.clear();
					} else if (probability != table[3]) {
						continue;
					}

					if (ClueScrollConstants.getClueScroll(table[1]) != null && ClueScrollConstants.playerHasScroll(player)) {
						continue;
					}

					drops.add(new DropItem(table[1], table[4] == 0 ? table[2] : NumberUtil.random(1, table[2])));
				}
			}
		}

		if (drops != null) {
			DropItem item;

			if (drops.size() <= 0) {
				return;
			}

			if (drops.size() == 1) {
				item = drops.get(0);
			} else {
				item = drops.get(player.getRandom().nextInt(drops.size()));
			}

			mob.drop(item.id, item.amount);
		}
	}

	public static void execute(Predicate<Mob> filter, Consumer<Mob> action) {
		Objects.requireNonNull(filter, "filter");
		Objects.requireNonNull(action, "action");

		for (int i = 1; i < MobManager.mobs.length; i++) {
			Mob mob = MobManager.mobs[i];

			if (mob != null && filter.test(mob)) {
				action.accept(mob);
			}
		}
	}

	public static int followDistance(int index) {
		if (TzhaarFightCaveConstants.isTzhaarNpc(mobs[index].getId()) || mobs[index].inZombiesGame()) {
			return 100;
		}

		switch (mobs[index].getId()) {
		case 6260:
		case 6261:
		case 6247:
		case 6248:
		case 6223:
		case 6225:
		case 6227:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 6250:
		case 6252:
		case 6263:
		case 6265:
		case 8133:
		case 9000:
			return 25;

		case 8596:
		case 9437:
		case 8597:
		case 3247:
		case 6270:
		case 6219:
		case 6255:
		case 6229:
		case 6277:
		case 6233:
		case 6232:
		case 6218:
		case 6269:
		case 3248:
		case 6212:
		case 6220:
		case 6276:
		case 6256:
		case 6230:
		case 6239:
		case 6221:
		case 6231:
		case 6257:
		case 6278:
		case 6272:
		case 6274:
		case 6254:
		case 4291: // Cyclops
		case 4292: // Ice cyclops
		case 6258:
		case 10141:
			return 7;

		case 2883:
			return 4;

		case 7133:
		case 1160:
			return 5;

		case 2881:
		case 2882:
		case 10127:
			return 1;
		}

		return 1;
	}

	public static boolean followPlayer(int index) {
		switch (mobs[index].getId()) {
		case 2892:
		case 2894:
		case 9463:
			return false;
		}

		return true;
	}

	public static int getAttackEmote(int index) {
		if (mobs[index].isFamiliar()) {
			return mobs[index].getDefinition().getAttackAnimation();
		}

		if (mobs[index].getCombat().getAttackType() == AttackType.MELEE) {
			return mobs[index].getDefinition().getAttackAnimation();
		} else if (mobs[index].getCombat().getAttackType() == AttackType.RANGE) {
			return mobs[index].getDefinition().getRangeAnimation();
		} else if (mobs[index].getCombat().getAttackType() == AttackType.MAGIC) {
			return mobs[index].getDefinition().getMagicAnimation();
		} else {
			switch (mobs[index].getId()) {
			case 1624:
				return 1557;

			case 2743:// Ket-Zek
				return 9264;

			case 50:// drags
			case 53:
			case 54:
			case 55:
			case 941:
			case 4677:
			case 1590:
			case 1591:
			case 1592:
			case 5362:
			case 5363:
				if (mobs[index].getCombat().getAttackType() == AttackType.DRAGON_FIRE) {
					return 81;
				}

			case 10129:
			case 10219:
			case 10220:
			case 10221:
			case 10222:
			case 10223:
			case 10224:
			case 10604:
			case 10605:
			case 10606:
			case 10607:
			case 10608:
			case 10609:
			case 10770:
			case 10771:
			case 10772:
			case 10773:
			case 10774:
			case 10775:
			case 10776:
			case 10777:
			case 10778:
			case 10779:
			case 10780:
			case 10781:
			case 10815:
			case 10816:
			case 10817:
			case 10818:
			case 10819:
			case 10820:
				if (mobs[index].getCombat().getAttackType() == AttackType.DRAGON_FIRE) {
					return 13155;
				}

			default:
				return 451;
			}
		}
	}

	public static Player getCloseRandomPlayer(int index) {
		Set<Player> available = new HashSet<>();
		boolean tzhaar = TzhaarFightCaveConstants.isTzhaarNpc(mobs[index].getId());

		for (Player target : GameService.getWorld().getPlayers()) {

			if (target.getZ() != mobs[index].getZ()) {
				continue;
			}

			if (Location.getManhattanDistance(mobs[index].getLocation(), target.getLocation()) <= (tzhaar ? 100 : 10)
					&& (target.getCombat().getAggressor() == null || target.inMulti())) {
				available.add(target);
			}
		}

		if (available.size() > 0) {
			return Iterables.get(available, NumberUtil.random(available.size()));
		}

		return null;
	}

	public static int getDeadEmote(int id) {
		if (id >= 3732 && id <= 3741) {
			return 3903;
		}

		if (id >= 3742 && id <= 3746) {
			return 3917;
		}

		if (id >= 3747 && id <= 3751) {
			return 3909;
		}

		if (id >= 3752 && id <= 3761) {
			return 3881;
		}

		if (id >= 3762 && id <= 3771) {
			return 3922;
		}

		if (id >= 3772 && id <= 3776) {
			return 3894;
		}

		switch (id) {
		case 4353: // Cave horror
		case 4354: // Cave horror
		case 4355: // Cave horror
		case 4356: // Cave horror
		case 4357: // Cave horror
			return 4233;

		case 73:
			return 5575;

		case 9000:
			return 6951;

		case 10127:
			return 13171;

		case 2745:
			return 9279;

		case 2627:
			return 9230;

		case 2630:
			return 9234;

		case 2738:
			return 9234;

		case 2631:
			return 9239;

		case 2741:
			return 9257;

		case 2743:
			return 9269;

		case 8528:
			return 12694;

		case 9052:
			return 2980;

		case 8596:
			return 11199;

		case 9437:
		case 8597:
			return 11204;

		case 7133:
			return 8756;

		case 9462:
		case 9463:
		case 9464:
		case 9465:
		case 9466:
		case 9467:
			return 12793;

		case 8349:
		case 8350:
		case 8351:
			return 10924;

		case 8133:
			return 10059;

		case 6203:
			return 6946;

		case 6204:
		case 6206:
		case 6208:
			return 67;

		case 6276:
		case 6277:
		case 6278:
			return 4321;

		case 1977:
		case 1913:
		case 6255:
		case 6256:
		case 6257:
		case 6219:
		case 6220:
		case 6221:
			return 836;

		case 6229:
		case 6230:
		case 6231:
			return 6956;

		case 936:
		case 937:
			return 5491;

		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
			return 5098;

		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 5397;

		case 5247: // Penance queen
			return 5412;

		case 3340:
			return 3310;

		case 7641:// Zombie hand
			return 9126;

		case 7801:// Aberrant spectre
			return 9467;

		case 6103:// Skeleton warlord
		case 6106:// Skeleton heavy
			return 836;

		case 7795:// Insectoid assassin
			return 7609;

		case 751:// Berserk barbarian spirit
			return 6727;

		case 7798:// Monstrous cave crawler
			return 9418;

		case 7797:// Kurask overlord
		case 7805:
			return 9440;

		case 7800:// Mightiest turoth
			return 9477;

		case 3062:// imp
			return 172;

		case 3065:// skeleton
			return 5514;

		case 3066:// zombie
			return 5575;

		case 3058:// giant
			return 6369;

		case 8986:// banshee
			return 9450;

		case 3059:// ghoul
			return 836;

		case 7078:// ogress
			return 8640;

		case 8987:// abberant
			return 9467;

		case 3057:// earth
			return 2946;

		case 8989:// mummy
			return 12518;

		case 3064:// demon
			return 67;

		case 3067:// leon
			return 836;

		case 1158:
			return 6242;

		case 1160:
			return 6233;

		case 5666:// barrelchest
			return 5898;

		case 1956:
			return 6576;

		case 1265:
		case 2452:
			return 1314;

		case 6730:
			return 7439;

		case 6729:
			return 7416;

		case 6691:
			return 7470;

		case 6689:
			return 7475;

		case 6688:
			return 7463;

		case 110:
		case 111:
		case 112:
		case 117:
		case 1681:
		case 4686:
		case 4688:
		case 1584:// giants
			return 4668;

		case 2892:
		case 2894:
			return 2865;

		case 1612: // banshee
		case 7793:
			return 1524;

		case 2558:
			return 3503;

		case 2559:
		case 2560:
		case 2561:
			return 6956;

		// Saradomin
		case 6247:
			return 6965;

		case 6248:
			return 6377;

		case 6250:
			return 7016;

		case 6252:
			return 7011;

		// bandos
		case 6260:
			return 7062;

		case 6261:
		case 6263:
		case 6265:
			return 6156;

		// armadyl
		case 6222:
			return 6975;

		case 6223:
		case 6225:
		case 6227:
			return 6956;

		// PC
		case 6142:
		case 6143:
		case 6144:
		case 6145:
			return -1;

		case 3200:
			return 3147;

		case 2035: // spider
			return 146;

		case 2033: // rat
			return 141;

		case 2031: // bloodveld
		case 7643:
			return 2073;

		case 101: // goblin
			return 313;

		case 81: // cow
			return 0x03E;

		case 41: // chicken
			return 57;

		case 1338: // dagannoth
		case 1340:
		case 1342:
			return 1342;

		case 2881:
		case 2882:
		case 2883:
			return 2856;

		case 125: // ice warrior
			return 843;

		case 1626:
		case 1627:
		case 1628:
		case 1629:
		case 1630:
		case 1631:
		case 1632: // turoth!
			return 1597;

		case 82:// Lesser Demon
		case 83:// Greater Demon
		case 84:// Black Demon
		case 4695:// Lesser Demon
		case 4698:// Greater Demon
		case 4705:// Black Demon
			return 67;

		case 1605:// abby spec
			return 1508;

		case 51:// baby drags
		case 52:
		case 1589:
		case 3376:
			return 28;

		case 1610:
		case 1611:
			return 9456;

		case 1616: // basilisk
		case 7799:// basilisk boss
			return 264;

		case 1618:
		case 1619:
			return 9131;

		case 1620:
		case 1621:
			return 1563;

		case 2783:
			return 2732;

		case 1615:
			return 1538;

		case 1624:
			return 1558;

		case 1613:
			return 9488;

		case 1643:
			return 7185;

		case 1633:
		case 1634:
		case 1635:
		case 1636:
			return 1580;

		case 1648:
		case 1649:
		case 1650:
		case 1651:
		case 1652:
		case 1653:
		case 1654:
		case 1655:
		case 1656:
		case 1657:
			return 9126;

		case 100:
		case 102:
			return 313;

		case 105:
		case 106:
			return 44;

		case 412:
		case 78:
			return 36;

		case 122:
		case 123:
			return 167;

		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
		case 134:
			return 146;

		case 1153:
		case 1154:
		case 1155:
		case 1156:
		case 1157:
			return 6228;

		case 103:
		case 104:
			return 123;

		case 118:
		case 119:
			return 102;

		case 50:// drags
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
			return 92;

		case 10219:
		case 10220:
		case 10221:
		case 10222:
		case 10223:
		case 10224:
		case 10604:
		case 10605:
		case 10606:
		case 10607:
		case 10608:
		case 10609:
		case 10770:
		case 10771:
		case 10772:
		case 10773:
		case 10774:
		case 10775:
		case 10776:
		case 10777:
		case 10778:
		case 10779:
		case 10780:
		case 10781:
		case 10815:
		case 10816:
		case 10817:
		case 10818:
		case 10819:
		case 10820:
			return 13153;

		default:
			return -1;
		}
	}

	public static int getDeadTime(int id) {
		switch (id) {
		case 2025:
		case 2026:
		case 2027:
		case 2028:
		case 2029:
		case 2030:
			return 1;
		case 1265:
		case 2452:
		case 3057:
		case 3200:
		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 1615: // Abyssal demon
		case 5237: // Penance ranger
			return 2;

		case 4353: // Cave horror
		case 4354: // Cave horror
		case 4355: // Cave horror
		case 4356: // Cave horror
		case 4357: // Cave horror
			return 1;

		case 5666:
			return 11;

		default:
			return 4;
		}
	}

	/**
	 * Hit delays
	 */
	public static int getHitDelay(int index) {
		switch (mobs[index].getId()) {
		case 2881:
		case 2882:
		case 3200:
		case 2892:
		case 2894:
		case 1158:
		case 1160:
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237:
		case 2743:
		case 2631:
		case 6222:
		case 6225:
		case 6250:
		case 6252:
		case 6263:
		case 6265:
		case 8349:
		case 8350:
		case 8351:
		case 9463:
		case 2028:
			return 3;

		case 9000:
		case 8133:
		case 2025:
			return 4;

		case 2745:
			return 5;

		default:
			return 2;
		}
	}

	// private static int getMaxHit(int index)
	// {
	// switch (mobs[index].getId())
	// {
	// case 2881:
	// return 30;
	//
	// case 2882:
	// return 61;
	//
	// case 2883:
	// return 28;
	//
	// case 3200:
	// return 28;
	//
	// case 50:
	// return 25;
	//
	// case 2745:
	// return 96;
	//
	// case 1158:
	// case 1160:
	// return 31;
	//
	// case 2892:
	// return 10;
	//
	// case 6247:
	// return 31;
	//
	// case 6260:
	// if (mobs[index].getCombat().getAttackType() == AttackType.RANGE)
	// {
	// return 35;
	// }
	// else
	// {
	// return 61;
	// }
	//
	// case 6222:
	// if (mobs[index].getCombat().getAttackType() == AttackType.RANGE)
	// {
	// return 45;
	// }
	// else
	// {
	// return 21;
	// }
	//
	// case 6203:
	// if (mobs[index].getCombat().getAttackType() == AttackType.MAGIC)
	// {
	// return 30;
	// }
	// else
	// {
	// return 46;
	// }
	//
	// case 8133:
	// if (mobs[index].getCombat().getAttackType() == AttackType.MAGIC)
	// {
	// return 69;
	// }
	// else if (mobs[index].getCombat().getAttackType() == AttackType.RANGE)
	// {
	// return 58;
	// }
	// else if (mobs[index].getCombat().getAttackType() == AttackType.MELEE)
	// {
	// return 51;
	// }
	//
	// case 9000:
	// if (mobs[index].getCombat().getAttackType() == AttackType.MAGIC)
	// {
	// return 90;
	// }
	// else if (mobs[index].getCombat().getAttackType() == AttackType.RANGE)
	// {
	// return 70;
	// }
	// }
	//
	// return 1;
	// }

	/**
	 * Attack delays
	 */
	public static int getMobDelay(int index) {
		switch (mobs[index].getId()) {
		case 2025:
		case 2028:
		case 8133:
		case 2745:
			return 7;

		case 6222:
		case 6223:
		case 6225:
		case 6227:
		case 6260:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 8349:
		case 8350:
		case 8351:
		case 9000:
			return 6;

		case 6247:
		case 8528:
			return 6;

		case 1977:
		case 1913:
		case 937:
		case 936:
		case 10127:
			return 4;

		default:
			return 5;
		}
	}

	public static Mob[] getMobs() {
		return mobs;
	}

	public static int getMove(int Place1, int Place2) {
		if (Place1 - Place2 == 0) {
			return 0;
		} else if (Place1 - Place2 < 0) {
			return 1;
		} else if (Place1 - Place2 > 0) {
			return -1;
		}

		return 0;
	}

	public static int getProjectileSpeed(int index) {
		switch (mobs[index].getId()) {
		case 1158:
		case 1160:
		case 50:
			return 90;

		case 2881:
		case 2882:
		case 3200:
			return 85;

		case 6222:
		case 6225:
		case 6223:
		case 6227:
		case 6247:
		case 6248:
		case 6250:
		case 6252:
		case 6260:
		case 6261:
		case 6263:
		case 6265:
		case 6203:
		case 6208:
		case 8349:
		case 8350:
		case 8351:
		case 8133:
		case 2025:
		case 9463:
		case 9000:
			return 105;

		case 2745:
			return 115;

		case 2028:
		case 27:
		case 690:
			return 80;

		default:
			return 85;
		}
	}

	public static int getRespawnTime(int id) {
		switch (id) {
		case 2881:
		case 2882:
		case 2883:
		case 50:
		case 3200:
		case 5666:
		case 6222:
		case 6225:
		case 6223:
		case 6227:
		case 6247:
		case 6248:
		case 6250:
		case 6252:
		case 6260:
		case 6261:
		case 6263:
		case 6265:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 8349:
		case 8350:
		case 8351:
		case 8133:
		case 7133:
		case 8596:
		case 9437:
		case 8597:
		case 8528:
		case 10127:
		case 9000:
		case 10141:
			return 60;

		case 6142:
		case 6143:
		case 6144:
		case 6145:
		case 1158:
		case 1160:
			return -1;

		default:
			return 25;
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return objectX - playerX <= distance && objectX - playerX >= -distance && objectY - playerY <= distance && objectY - playerY >= -distance;
	}

	public static void handleSpecialEffects(Player player, int index, int damage) {
		Objects.requireNonNull(player, "player");

		if (mobs[index].getId() == 2892 || mobs[index].getId() == 2894 || mobs[index].getId() == 1158 || mobs[index].getId() == 1160 || mobs[index].getId() == 3059) {
			if (damage > 0) {
				if (player.getSkill().getLevel(Skill.PRAYER) > 0) {
					player.getSkill().decrementLevel(Skill.PRAYER);
					player.getEF().queueEffect(new Poison(player, 6, mobs[index]));
				}
			}
		}

		if (mobs[index].getId() == 50 || mobs[index].getId() == 53 || mobs[index].getId() == 54 || mobs[index].getId() == 55 || mobs[index].getId() == 941
				|| mobs[index].getId() == 1590 || mobs[index].getId() == 1591 || mobs[index].getId() == 1592 || mobs[index].getId() == 5362 || mobs[index].getId() == 5363
				|| mobs[index].getId() == 4677 || mobs[index].getId() == 10773) {
			if (mobs[index].getCombat().getAttackType() == AttackType.DRAGON_FIRE) {
				if (player.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
					if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11283 || player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11284) {
						if (player.dfsCount < 50) {
							CombatExtras.addCharge(player);
						} else if (player.dfsCount > 50) {
							player.sendMessage("Your dragonfire shield is already fully charged.");
							player.dfsCount = 50;
						}
					}
				}
			}
		}
	}

	public static void healRevenants(int index) {
		if (mobs[index].getId() == 6688 || mobs[index].getId() == 6689 || mobs[index].getId() == 6729 || mobs[index].getId() == 6691 || mobs[index].getId() == 6730) {
			if (NumberUtil.random(3) == 0) {
				if (mobs[index].hitpoints < 50) {
					mobs[index].hitpoints += 25;
					mobs[index].getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
			}
		}
	}

	static boolean isAggressive(int index) {

		if (mobs[index] == null) {
			return false;
		}

		switch (mobs[index].getId()) {
		case 1931:
		case 10141:
		case 1158:
		case 1160:
			return true;
		}

		if (mobs[index].getDefinition().isAggressive()) {
			return true;
		}

		if (mobs[index].isInWilderness() && mobs[index].maximumHitpoints > 0) {
			return true;
		}

		if (TzhaarFightCaveConstants.isTzhaarNpc(mobs[index].getId()) || PestControl.isPestControlMob(mobs[index].getId()) || isSpiritMob(index)) {
			return true;
		}

		if (mobs[index].inZombiesGame()) {
			return true;
		}

		return false;
	}

	private static boolean isSpiritMob(int index) {
		switch (mobs[index].getId()) {
		case 6276:
		case 6277:
		case 6278:
		case 1977:
		case 1913:
		case 6255:
		case 6256:
		case 6257:
		case 6219:
		case 6220:
		case 6221:
		case 6229:
		case 6230:
		case 6231:
		case 937:
		case 936:
			return true;
		}

		return false;
	}

	public static void loadSpell(EntityCharacter entity, int i) {
		if (mobs[i] == null) {
			System.err.println("mobs[i] is null --- loadSpell(" + entity.getName() + ", " + i + ")");
		}

		boolean can_melee = CombatDistance.inCombatDistance(mobs[i], entity, AttackType.MELEE, false);
		boolean can_magic = CombatDistance.inCombatDistance(mobs[i], entity, AttackType.MAGIC, false);

		if (!can_magic && !can_melee) {
			entity.getCombat().setAttackTimer(2);
			return;
		}

		switch (mobs[i].getId()) {
		case 8528: // Nomad
			if (NumberUtil.inclusiveRandom(2) == 1) {
				mobs[i].projectileId = 88;
				mobs[i].endGfx = 89;
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			} else {
				mobs[i].projectileId = -1;
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
			}
			break;

		case 8349:
		case 8350:
		case 8351:
			int r3 = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));

			if (r3 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].playGraphics(new Graphic(1885));
				mobs[i].projectileId = 1884;
				mobs[i].endGfx = 1883;
			} else if (r3 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
				mobs[i].projectileId = 1887;
			} else if (r3 == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 1886));
				mobs[i].projectileId = -1;
			}
			break;

		case 8133:
			r3 = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));
			if (r3 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].endGfx = -1;
				mobs[i].projectileId = 1825;
			} else if (r3 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
				mobs[i].endGfx = -1;
				mobs[i].projectileId = 1824;
			} else if (r3 == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 1834));
				mobs[i].projectileId = -1;
			}
			break;

		/* 0 - melee, 1 - range, 2 - mage */
		case 5247: // Penance queen

			if (!can_melee || NumberUtil.inclusiveRandom(2) == 1) {
				mobs[i].projectileId = 871;
				mobs[i].endGfx = 872;
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
			} else {
				mobs[i].projectileId = -1;
				mobs[i].endGfx = -1;
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
			}
			break;

		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger (ranged)
			mobs[i].projectileId = 866;
			mobs[i].endGfx = 865;
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			break;

		// lesser champ
		case 3064:
			if (!can_melee || NumberUtil.inclusiveRandom(2) == 1) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC); // flames
				mobs[i].endGfx = 346;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
				mobs[i].projectileId = -1;
			}
			break;

		// champion leon
		case 3067:
			if (can_melee && NumberUtil.inclusiveRandom(2) == 1) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
				mobs[i].playGraphics(new Graphic(-1));
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC); // bgs
				// spec
				mobs[i].playGraphics(new Graphic(1223));
			}
			break;

		case 5666:
			int spec = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));

			if (spec == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
			} else if (spec == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC); // special
			} else if (spec == 1) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE); // special
			}
			break;

		// kalphite queen form 1
		case 1158:
			int kq1 = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));

			if (kq1 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC); // mage
				mobs[i].playGraphics(new Graphic(278));
				mobs[i].projectileId = 280;
				mobs[i].endGfx = 281;
			} else if (kq1 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE); // range
				mobs[i].playGraphics(new Graphic(-1));
				mobs[i].endGfx = -1;
				mobs[i].projectileId = 473;
			} else if (kq1 == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
				mobs[i].projectileId = -1;
			}
			break;

		// kalphite queen form 2
		case 1160:
			kq1 = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));

			if (kq1 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC); // mage
				mobs[i].playGraphics(new Graphic(279));
				mobs[i].projectileId = 280;
				mobs[i].endGfx = 281;
			} else if (kq1 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE); // range
				mobs[i].playGraphics(new Graphic(-1));
				mobs[i].endGfx = -1;
				mobs[i].projectileId = 473;
			} else if (kq1 == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
				mobs[i].projectileId = -1;
			}
			break;
		case 10127:
			int misc = NumberUtil.inclusiveRandom(2 + (can_melee ? 1 : 0));
			if (entity.isPlayer()) {
				Player player = entity.toPlayer();
				if (misc == 0) {
					player.playGraphics(new Graphic(2253));
					if (player.getSkill().getLevel(Skill.ATTACK) > 1) {
						player.getSkill().setLevel(Skill.ATTACK, player.getSkill().getLevelForExperience(Skill.ATTACK) / 2);
					}
					player.sendMessage("The cursebearer drains half your attack.");
					mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				} else if (misc == 1) {
					player.playGraphics(new Graphic(2245));
					if (player.getSkill().getLevel(Skill.DEFENCE) > 1) {
						player.getSkill().setLevel(Skill.DEFENCE, player.getSkill().getLevelForExperience(Skill.DEFENCE) / 2);
					}
					player.sendMessage("The cursebearer drains half your defense.");
					mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				} else if (misc == 2) {
					player.playGraphics(new Graphic(2249));
					if (player.getSkill().getLevel(Skill.STRENGTH) > 1) {
						player.getSkill().setLevel(Skill.STRENGTH, player.getSkill().getLevelForExperience(Skill.STRENGTH) / 2);
					}
					player.sendMessage("The cursebearer drains half your strength.");
					mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				} else if (misc == 3) {
					mobs[i].getCombat().setAttackType(AttackType.MELEE);
				}

			} else {
				mobs[i].getCombat().setAttackType(misc > 2 ? AttackType.MELEE : AttackType.MAGIC);
			}
			break;

		case 27:
		case 690:
			mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 21));
			mobs[i].projectileId = 21;
			mobs[i].endGfx = 92;
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			break;

		case 2892: // spinolyp
			int r1 = NumberUtil.random(2);

			if (r1 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
				mobs[i].projectileId = 298;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].projectileId = 94;
				mobs[i].endGfx = 95;
			}
			break;

		case 9463: // ice strykewyrm
			if (!can_melee || NumberUtil.inclusiveRandom(2) == 1) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC); // mage
				mobs[i].projectileId = 368;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
				mobs[i].projectileId = -1;
			}
			break;

		case 50: // dragons
		case 52:
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
		case 10773:

			int randomfire = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));

			if (randomfire == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE); // melee
				mobs[i].projectileId = -1;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.DRAGON_FIRE); // fire
				mobs[i].projectileId = 393;
				mobs[i].endGfx = 430;
			}
			break;

		/* God wars npcs */
		case 6203:
			int zammy = NumberUtil.inclusiveRandom(2);

			if (can_melee && (zammy == 0 || zammy == 1)) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].endGfx = -1;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].projectileId = 1211;
			}
			break;

		case 6227:// Flight Kilisa
			mobs[i].getCombat().setAttackType(AttackType.MELEE);
			break;

		case 6223:// Wingman Skree
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].projectileId = 1199;
			break;

		case 6225:// Flockleader Geerin
		case 6230:
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 1190;
			break;

		case 6239:// Aviansie
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 1191;
			break;

		case 6232:// Aviansie
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 1191;
			break;

		case 6276:// Spiritual ranger
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 1195;
			break;

		case 6278:// Spiritual ranger
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].projectileId = 165;
			mobs[i].endGfx = 166;
			break;

		case 6257:// Spiritual mage
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].endGfx = 76;
			break;

		case 6221:// Spiritual mage
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].endGfx = 78;
			break;

		case 6231:// Spiritual mage
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].projectileId = 1199;
			break;

		case 6206:
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 1209;
			break;

		case 6208:
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].projectileId = 1213;
			mobs[i].playGraphics(new Graphic(1212));
			break;

		case 6256:
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 16;
			break;

		case 6220:
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 17;
			break;

		case 6247: // Commander Zilyana
			int random = NumberUtil.inclusiveRandom(1);

			if (random == 0 || !can_melee) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].endGfx = 1194;
				mobs[i].projectileId = -1;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
			}
			break;

		case 6248: // Starlight
			mobs[i].getCombat().setAttackType(AttackType.MELEE);
			break;

		case 6250: // Growler
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].projectileId = 1203;
			break;

		case 6252: // Bree
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 9;
			break;

		// bandos npcs
		case 6260:// General Graardor
			random = NumberUtil.inclusiveRandom(2);

			if (can_melee && (random == 0 || random == 1)) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].playGraphics(new Graphic(-1));
			} else {
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
				mobs[i].playGraphics(new Graphic(1219));
			}
			break;

		case 6261:// strongstack
			mobs[i].getCombat().setAttackType(AttackType.MELEE);
			break;

		case 6263:// steelwill
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].playGraphics(new Graphic(1202));
			mobs[i].projectileId = 1203;
			mobs[i].endGfx = 163;
			break;

		case 6265:// grimspike
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 1206;
			break;

		// End godwars npcs
		case 2025:
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			int r = NumberUtil.inclusiveRandom(3);

			if (r == 0) {
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 158));
				mobs[i].projectileId = 159;
				mobs[i].endGfx = 160;
			}

			if (r == 1) {
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 161));
				mobs[i].projectileId = 162;
				mobs[i].endGfx = 163;
			}

			if (r == 2) {
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 164));
				mobs[i].projectileId = 165;
				mobs[i].endGfx = 166;
			}

			if (r == 3) {
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 155));
				mobs[i].projectileId = 156;
			}
			break;

		case 2881:// supreme
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 298;
			break;

		case 2882:// prime
			mobs[i].getCombat().setAttackType(AttackType.MAGIC);
			mobs[i].projectileId = 162;
			mobs[i].endGfx = 477;
			break;

		case 2028:
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 27;
			break;

		case 3200:
			int r2 = can_melee ? NumberUtil.inclusiveRandom(5) : NumberUtil.inclusiveRandom(1);

			if (r2 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 550));
				mobs[i].projectileId = 551;
				mobs[i].endGfx = 552;
			} else if (r2 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 553));
				mobs[i].projectileId = 554;
				mobs[i].endGfx = 555;
			} else {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 556));
				mobs[i].projectileId = 557;
				mobs[i].endGfx = 558;
			}
			break;

		case 2745:
			r3 = NumberUtil.inclusiveRandom(1 + (can_melee ? 1 : 0));

			if (r3 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].endGfx = 157;
				mobs[i].projectileId = 1627;
			} else if (r3 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.RANGE);
				mobs[i].endGfx = 1628;
				mobs[i].playGraphics(new Graphic(GraphicHeight.HIGH, 1625));
				mobs[i].projectileId = -1;
			} else if (r3 == 2) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].projectileId = -1;
			}
			break;

		case 2631:
			mobs[i].getCombat().setAttackType(AttackType.RANGE);
			mobs[i].projectileId = 443;
			break;

		case 2743:
			r3 = NumberUtil.inclusiveRandom(1);

			if (!can_melee || r3 == 0) {
				mobs[i].getCombat().setAttackType(AttackType.MAGIC);
				mobs[i].projectileId = 445;
				mobs[i].endGfx = 446;
				break;
			} else if (r3 == 1) {
				mobs[i].getCombat().setAttackType(AttackType.MELEE);
				mobs[i].projectileId = -1;
			}
		}
	}

	// private static void multiAttackDamage(final int i)
	// {
	//
	// for (int j = 1; j < GameService.getWorld().getPlayers().length; j++)
	// {
	// final Player player = GameService.getWorld().getPlayer(j];
	//
	// if (player == null || player.isDead() || (player.getZ() !=
	// mobs[i].getZ()))
	// {
	// continue;
	// }
	//
	// if (player.goodDistance(player.getX(), player.getY(), mobs[i].getX(),
	// mobs[i].getY(), 15))
	// {
	// if (mobs[i].getCombat().getAttackType() == AttackType.MAGIC)
	// {
	// if (!player.prayerActive[16] && !player.curseActive[7])
	// {
	// int dam = CalculateHit.execute(mobs[i], player, AttackType.MAGIC);
	// if (player.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD))
	// {
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13740)
	// {
	// if (player.getSkill().getLevel(Skill.PRAYER) > 0)
	// {
	// dam *= 0.7;
	// player.getSkill().setLevel(Skill.PRAYER,
	// (int) (player.getSkill().getLevel(Skill.PRAYER) - ((dam * 0.3) / 2)));
	// }
	// }
	//
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13742)
	// {
	// if (NumberUtil.inclusiveRandom(4) == 3)
	// {
	// dam *= 0.75;
	// }
	// }
	// }
	//
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(dam, HitIcons.MAGIC), mobs[i]);
	// CombatCurse.handleCurses(mobs[i], player, dam, AttackType.MAGIC);
	// }
	// else
	// {
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(0), mobs[i]);
	//
	// Server.getTaskScheduler().schedule(new Task()
	// {
	//
	// @Override
	// public void execute()
	// {
	// if ((mobs[i] != null) && !player.isDisconnected())
	// {
	// if ((mobs[i].getId() != 2745) && (mobs[i].getId() != 8133)
	// && (mobs[i].getId() != 9000) && (mobs[i].getId() != 6222)
	// && (mobs[i].getId() != 6203) && (mobs[i].getId() != 6247)
	// && (mobs[i].getId() != 5666))
	// {
	// player.playGraphics(new Graphic(GraphicHeight.HIGH, 85));
	// stop();
	// }
	// }
	// else
	// {
	// stop();
	// }
	// }
	//
	// });
	// }
	//
	// if (player.prayerActive[16] || player.curseActive[7])
	// {
	// int dam = (CalculateHit.execute(mobs[i], player, AttackType.MAGIC) * 40)
	// / 100;
	//
	// if (dam > 0)
	// {
	//
	// if (player.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD))
	// {
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13740)
	// {
	// if (player.getSkill().getLevel(5) > 0)
	// {
	// dam *= 0.7;
	// player.getSkill().decrementLevel(5, (int) ((dam * 0.3) / 2));
	// }
	// }
	//
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13740)
	// {
	// if (player.getSkill().getLevel(5) > 0)
	// {
	// dam *= 0.7;
	// player.getSkill().decrementLevel(5, (int) ((dam * 0.3) / 2));
	// }
	// }
	//
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13742)
	// {
	// if (NumberUtil.inclusiveRandom(4) == 3)
	// {
	// dam *= 0.75;
	// }
	// }
	// }
	//
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(dam, HitIcons.MAGIC), mobs[i]);
	// CombatCurse.handleCurses(mobs[i], player, dam, AttackType.MAGIC);
	// }
	// else
	// {
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(0), mobs[i]);
	//
	// Server.getTaskScheduler().schedule(new Task()
	// {
	//
	// @Override
	// public void execute()
	// {
	// if (!player.isDisconnected())
	// {
	// if ((mobs[i].getId() != 2745) && (mobs[i].getId() != 8133)
	// && (mobs[i].getId() != 9000) && (mobs[i].getId() != 6222)
	// && (mobs[i].getId() != 6203) && (mobs[i].getId() != 6247)
	// && (mobs[i].getId() != 5666))
	// {
	// player.playGraphics(new Graphic(GraphicHeight.HIGH, 85));
	// }
	// }
	//
	// stop();
	// }
	//
	// });
	// }
	// }
	//
	// if (mobs[i].getCombat().getAttackType() == AttackType.RANGE)
	// {
	// if (!player.prayerActive[17] && !player.curseActive[8])
	// {
	// int dam = CalculateHit.execute(mobs[i], player, AttackType.RANGE);
	//
	// if (player.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD))
	// {
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13740)
	// {
	// if (player.getSkill().getLevel(Skill.PRAYER) > 0)
	// {
	// dam *= 0.7;
	// player.getSkill().decrementLevel(5, (int) ((dam * 0.3) / 2));
	// }
	// }
	//
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13742)
	// {
	// if (NumberUtil.inclusiveRandom(4) == 3)
	// {
	// dam *= 0.75;
	// }
	// }
	// }
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(dam, HitIcons.RANGE), mobs[i]);
	// CombatCurse.handleCurses(mobs[i], player, dam, AttackType.RANGE);
	// }
	// else
	// {
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(0), mobs[i]);
	// }
	//
	// if (player.prayerActive[17] || player.curseActive[8])
	// {
	// int dam = CalculateHit.execute(mobs[i], player, AttackType.RANGE);
	//
	// if (player.getEquipment().isSlotUsed(Equipment.SLOT_SHIELD))
	// {
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13740)
	// {
	// if (player.getSkill().getLevel(Skill.PRAYER) > 0)
	// {
	// dam *= 0.7;
	// player.getSkill().decrementLevel(5, (int) ((dam * 0.3) / 2));
	// }
	// }
	//
	// if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId()
	// == 13742)
	// {
	// if (NumberUtil.inclusiveRandom(4) == 3)
	// {
	// dam *= 0.75;
	// }
	// }
	// }
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(dam, HitIcons.RANGE), mobs[i]);
	// }
	// else
	// {
	// mobs[i].getCombat().setLastDamageApplied(System.currentTimeMillis());
	// player.setDamage(new Damage(0), mobs[i]);
	// }
	// }
	//
	// if (mobs[i].endGfx > 0)
	// {
	// player.playGraphics(new Graphic(mobs[i].endGfx));
	// }
	// }
	//
	// player.client().send(new SendSkillLevelPacket(Skill.HITPOINTS));
	// }
	// }
	// }

	public static void multiAttackGfx(int index) {
		if (mobs[index].projectileId < 0) {
			return;
		}

		for (Player player : GameService.getWorld().getPlayers()) {
			if (player.getZ() != mobs[index].getZ()) {
				continue;
			}

			if (player.goodDistance(player.getX(), player.getY(), mobs[index].getX(), mobs[index].getY(), 15)) {
				int nX = MobManager.mobs[index].getX() + offset(index);
				int nY = MobManager.mobs[index].getY() + offset(index);
				int pX = player.getX();
				int pY = player.getY();
				int offX = (nY - pY) * -1;
				int offY = (nX - pX) * -1;
				CombatExtras.createPlayersProjectile(nX, nY, player.getLocation().getZ(), offX, offY, 50, getProjectileSpeed(index), mobs[index].projectileId, 43, 31,
						-player.index() - 1, 65, 16);
			}
		}
	}

	public static boolean multiAttacks(int i) {
		switch (mobs[i].getId()) {
		case 1158: // kq
			if (mobs[i].getCombat().getAttackType() == AttackType.RANGE || mobs[i].getCombat().getAttackType() == AttackType.MAGIC) {
				return true;
			}
			break;

		case 1160: // kq 2
			if (mobs[i].getCombat().getAttackType() == AttackType.RANGE || mobs[i].getCombat().getAttackType() == AttackType.MAGIC) {
				return true;
			}
			break;

		case 6222:// kree
			if (mobs[i].getCombat().getAttackType() == AttackType.RANGE || mobs[i].getCombat().getAttackType() == AttackType.MAGIC) {
				return true;
			}
			break;

		case 6247:// zilyana
			if (mobs[i].getCombat().getAttackType() == AttackType.MAGIC) {
				return true;
			}
			break;

		case 6260:// bandos
			if (mobs[i].getCombat().getAttackType() == AttackType.RANGE) {
				return true;
			}
			break;

		case 6203:// zammy
			if (mobs[i].getCombat().getAttackType() == AttackType.MAGIC) {
				return true;
			}
			break;

		case 8133:// corp
			if (mobs[i].getCombat().getAttackType() == AttackType.MAGIC || mobs[i].getCombat().getAttackType() == AttackType.RANGE) {
				return true;
			}
			break;
		}

		return false;
	}

	public static int offset(int i) {
		switch (mobs[i].getId()) {
		case 50:
		case 52:
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363: // dragons
		case 10773:
			return 2;

		case 2881:
		case 2882:
		case 2745:
		case 2743:
		case 8349:
		case 8350:
		case 8351:
		case 8133:
			// case 8528:
			// return 1;

		case 1158:
		case 1160:
			return 2;

		case 9000:
			return 1;
		}

		return 0;
	}

	public static void remove(Mob mob) {
		if (mob == null || mobs[mob.getIndex()] == null) {
			return;
		}
		Walking.setMobOnTile(mob, false);
		mobs[mob.getIndex()].getLocation().setAs(new Location(0, 0));
		mobs[mob.getIndex()] = null;
	}

	static void resetPlayersInCombat(int index) {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player.getCombat().getAggressor() != null) {
				if (player.getCombat().getAggressor().getIndex() == index) {
					player.getCombat().setAggressor(null);
				}
			}
		}
	}

	static boolean retaliates(int id) {
		return id < 6142 || id > 6145 && !(id >= 2440 && id <= 2446);
	}

	public static void spawn(Mob mob) {
		Objects.requireNonNull(mob, "mob");

		for (int i = 1; i < mobs.length; i++) {
			if (mobs[i] == null) {
				mob.index = i;
				mobs[i] = mob;
				return;
			}
		}

		throw new IllegalStateException("virtual world too full to add new mob!");
	}

	public static void spawnAllRevs() {
		for (int i : revIds) {
			spawnRev(i);
		}
	}

	public static void spawnFamiliar(Player player, MobFamiliar familiar) {
		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; i++) {
			if (mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalStateException("World full!");
		}

		MobDefinition def = MobDefinition.get(familiar.getId());

		familiar.setWalkingType(0);
		familiar.setMaxHit(def.getMaxHit() > 0 ? def.getMaxHit() : 7);
		familiar.hitpoints = def.getHitpoints();
		familiar.maximumHitpoints = def.getHitpoints();
		familiar.setAttack(def.getAttackLevel());
		familiar.defence = def.getDefenceLevel();
		familiar.setFaceIndex(player.getIndex(), true);
		familiar.index = index;

		if (familiar.getPouchDefinitions().getSpawnAnim() > 0) {
			familiar.playAnimation(new Animation(familiar.getPouchDefinitions().getSpawnAnim()));
		}

		familiar.playGraphics(new Graphic(familiar.getSize() <= 1 ? 1314 : 1315));
		familiar.summoner = player;

		mobs[index] = familiar;

		player.getSummoning().setFamiliar(familiar);
	}

	public static void spawnPet(Player player, MobPet pet) {
		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; i++) {
			if (mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalStateException("World full!");
		}

		pet.setWalkingType(0);
		pet.setMaxHit(0);
		pet.setAttack(0);
		pet.defence = 0;
		pet.setFaceIndex(player.getIndex(), true);
		pet.index = index;
		pet.summoner = player;
		mobs[index] = pet;
		player.getSummoning().setPet(pet);
	}

	public static void spawnFirstKalphiteForm(final int x, final int y, final int z) {
		GameService.getTaskScheduler().schedule(new Task(18) {

			@Override
			public void execute() {
				spawnMobInternal(1158, x, y, z, 1, 255, 31, 600, 300);

				stop();
			}

		});
	}

	static Mob spawnMob(int id, int x, int y, int plane, int walkType, int health, int maxHit, int attack, int defence) {
		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; i++) {
			if (mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalStateException("No available mob slots!");
		}

		MobDefinition def = MobDefinition.get(id);

		if (def.getAttackLevel() != -1) {
			attack = def.getAttackLevel();
		}
		if (def.getDefenceLevel() != -1) {
			defence = def.getDefenceLevel();
		}

		if (def.getHitpoints() != -1) {
			health = def.getHitpoints();
		}

		if (def.getMaxHit() != -1) {
			maxHit = def.getMaxHit();
		}

		Mob mob = MobFactory.create(index, id, new Location(x, y, plane));
		mob.setWalkingType(walkType);
		mob.hitpoints = health;
		mob.maximumHitpoints = health;

		mob.setMaxHit(maxHit);
		mob.setAttack(attack);
		mob.defence = defence;
		mob.setSkill(new Skill(mob, attack, defence, health));
		mobs[index] = mob;
		return mob;
	}

	public static Mob spawnMob(int id, Location position, int walkType, int health, int maxHit, int attack, int defence) {
		return MobManager.spawnMob(id, position.getX(), position.getY(), position.getZ(), walkType, health, maxHit, attack, defence);
	}

	public static Mob spawnMob(int id, Location position) {
		return MobManager.spawnMob(id, position, 0, 0, 0, 0, 0);
	}

	/**
	 * Summon npc, barrows, etc
	 */
	public static Mob spawnMob(Player player, int id, int x, int y, int z, int WalkingType, int HP, int maxHit, int attack, int defence, boolean attackPlayer, boolean headIcon) {
		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; ++i) {
			if (mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalStateException("No available mob slots!");
		}

		MobDefinition def = MobDefinition.get(id);

		if (def.getAttackLevel() != -1) {
			attack = def.getAttackLevel();
		}
		if (def.getDefenceLevel() != -1) {
			defence = def.getDefenceLevel();
		}

		if (def.getHitpoints() != -1) {
			HP = def.getHitpoints();
		}

		if (def.getMaxHit() != -1) {
			maxHit = def.getMaxHit();
		}

		Mob mob = MobFactory.create(index, id, new Location(x, y, z));
		mob.setWalkingType(WalkingType);
		mob.hitpoints = HP;
		mob.maximumHitpoints = HP;
		mob.setMaxHit(maxHit);
		mob.setAttack(attack);
		mob.defence = defence;
		mob.summoner = player;
		mob.setSkill(new Skill(mob, attack, defence, HP));

		if (headIcon) {
			// player.getSession().send(new SendEntityHintIconPacket(1, index, 0, 0);
			player.getSession().send(new SendEntityHintIconPacket(mob));
		}

		if (attackPlayer) {
			if (mob.getId() == 3067) {
				mob.speak("Time for me to show you why they call me the Champion of Champions!");
			} else if (mob.getId() == 937) {
				mob.speak("Get away from my treasure!");
			} else if (mob.getId() == 936) {
				mob.speak("Keep your hands away from my chest you foolish mortal!");
			} else if (mob.getId() == 1913) {
				mob.speak("I will perish again before I let you take what is mine!");
			} else if (mob.getId() == 1977) {
				mob.speak("You dare stand between me and my spoils mortal?!");
			} else if (mob.getId() == 5145) {
				mob.speak("I expect you to die!");
			}

			mob.getCombat().setTarget(player);
			mob.getFollowing().setFollow(player, FollowType.COMBAT);
		}

		mobs[index] = mob;
		return mob;
	}

	/**
	 * Spawn npcs for minigames
	 */
	public static void spawnMob(Player player, int id, Location location, int hitpoints, int maxHit, int attack, int defence, boolean attackPlayer, boolean headIcon,
			String forceSpeak) {
		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; ++i) {
			if (mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalStateException("No available mob slots!");
		}

		MobDefinition def = MobDefinition.get(id);

		if (def.getAttackLevel() != -1) {
			attack = def.getAttackLevel();
		}
		if (def.getDefenceLevel() != -1) {
			defence = def.getDefenceLevel();
		}

		if (def.getHitpoints() != -1) {
			hitpoints = def.getHitpoints();
		}

		if (def.getMaxHit() != -1) {
			maxHit = def.getMaxHit();
		}

		Mob mob = MobFactory.create(index, id, location);
		mob.setWalkingType(50);
		mob.hitpoints = hitpoints;
		mob.maximumHitpoints = hitpoints;
		mob.setMaxHit(maxHit);
		mob.setAttack(attack);
		mob.defence = defence;
		mob.summoner = player;
		mob.setSkill(new Skill(mob, attack, defence, hitpoints));

		if (headIcon) {
			player.getSession().send(new SendEntityHintIconPacket(mob));
		}

		if (forceSpeak != null && forceSpeak.length() > 0) {
			mob.speak(forceSpeak);
		}

		if (attackPlayer) {
			mob.getCombat().setTarget(player);
			mob.getFollowing().setFollow(player, FollowType.COMBAT);
		}

		mobs[index] = mob;
	}

	public static void spawnMobInternal(final int id, final int x, final int y, final int plane, int WalkingType, int hitpoints, int maxHit, int attack, int defence) {
		int slot = -1;

		for (int i = 1; i < Constants.MAX_MOBS; i++) {
			if (mobs[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1) {
			throw new IllegalStateException("No available mob slots!");
		}

		MobDefinition def = MobDefinition.get(id);

		if (def.getAttackLevel() != -1) {
			attack = def.getAttackLevel();
		}
		if (def.getDefenceLevel() != -1) {
			defence = def.getDefenceLevel();
		}

		if (def.getHitpoints() != -1) {
			hitpoints = def.getHitpoints();
		}

		if (def.getMaxHit() != -1) {
			maxHit = def.getMaxHit();
		}

		Mob mob = new Mob(slot, id, new Location(x, y, plane));
		mob.setWalkingType(WalkingType);
		mob.hitpoints = hitpoints;
		mob.maximumHitpoints = hitpoints;
		mob.setMaxHit(maxHit);
		mob.setAttack(attack);
		mob.defence = defence;
		mob.setSkill(new Skill(mob, mob.getAttack(), mob.getDefence(), mob.hitpoints));
		mobs[slot] = mob;
	}

	public static Mob spawnRev(int id) {
		final int x = MobWildRevenant.SPAWN_COORDINATIONS[NumberUtil.random(MobWildRevenant.SPAWN_COORDINATIONS.length)][0];
		final int y = MobWildRevenant.SPAWN_COORDINATIONS[NumberUtil.random(MobWildRevenant.SPAWN_COORDINATIONS.length)][1];
		int index = -1;

		for (int i = 1; i < Constants.MAX_MOBS; i++) {
			if (mobs[i] == null) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalStateException("No available mob slots!");
		}

		Mob mob = new MobWildRevenant(index, id, new Location(x, y, 0));
		mob.setWalkingType(0);
		mob.hitpoints = 200;
		mob.maximumHitpoints = 200;
		mob.setMaxHit(35);
		mob.setAttack(mob.getDefinition().getAttackLevel());
		mob.defence = mob.getDefinition().getDefenceLevel();
		mob.setSkill(new Skill(mob, mob.getAttack(), mob.getDefence(), mob.hitpoints));
		mobs[index] = mob;
		return mob;
	}

	/**
	 * KQ spawn
	 */
	public static void spawnSecondForm(final int x, final int y, final int z) {
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				spawnMobInternal(1160, x, y, z, 1, 255, 31, 600, 300);
				stop();
			}

		});
	}

	private boolean loadAutoSpawn(String fileName) {
		String line = null;
		String token;
		String token2;
		String token2_2;
		String[] token3;
		boolean EndOfFile = false;
		BufferedReader characterfile;

		try {
			characterfile = new BufferedReader(new FileReader("./" + fileName));
		} catch (FileNotFoundException fileex) {
			System.out.println(fileName + ": file not found.");
			return false;
		}

		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			System.out.println(fileName + ": error loading file.");

			try {
				characterfile.close();
				return false;
			} catch (IOException ex) {
			}
		}

		while (!EndOfFile && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");

			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");

				if (token.equals("spawn")) {
					spawnMob(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]), Integer.parseInt(token3[2]), Integer.parseInt(token3[3]), Integer.parseInt(token3[4]),
							MobDefinition.get(Integer.parseInt(token3[0])).getHitpoints(), Integer.parseInt(token3[5]), Integer.parseInt(token3[6]), Integer.parseInt(token3[7]));
				}
			} else if (line.equals("[ENDOFSPAWNLIST]")) {
				try {
					characterfile.close();
					return true;
				} catch (IOException ioexception) {
				}
			}

			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}

		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}

		return false;
	}

	public static boolean hasSummoned(Player player, int id) {
		for (Mob mob : mobs) {
			if (mob != null && mob.getId() == id && player.equals(mob.summoner) && !mob.isDead()) {
				return true;
			}
		}

		return false;
	}

	public void update() {
		for (int i = 1; i < Constants.MAX_MOBS; i++) {
			if (mobs[i] != null) {
				mobs[i].getTaskScheduler().pulse();
				mobs[i].internalReset();
				mobs[i].update();
			}
		}
	}

	// Holder class until we have a more appropiate core-level equivalent
	private static class DropItem {

		private final int amount;
		private final short id;

		private DropItem(int id, int amount) {
			this.id = (short) id;
			this.amount = amount;
		}

	}

}
