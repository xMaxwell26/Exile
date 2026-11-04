package com.koios.game.content.cluescroll;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.Chance;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;
import com.koios.util.Chance.RepresentedWeightedObject;

/**
 * Holds all the data for clue scrolls.
 *
 * @author Michael | Chex
 */
public class ClueScrollConstants {

	/**
	 * The casket (easy) item id.
	 */
	public static final int CASKET_EASY = 2714;

	/**
	 * The casket (medium) item id.
	 */
	public static final int CASKET_MEDIUM = 2802;

	/**
	 * The casket (hard) item id.
	 */
	public static final int CASKET_HARD = 2724;

	private static final Chance<Item> crossTrailRewardGenerator = new Chance<>(Arrays.asList(
			/**
			 * Total weight = 195
			 */
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.ZAMORAK_PAGE_1)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.ZAMORAK_PAGE_2)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.ZAMORAK_PAGE_3)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.ZAMORAK_PAGE_4)),

	new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.SARADOMIN_PAGE_1)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.SARADOMIN_PAGE_2)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.SARADOMIN_PAGE_3)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.SARADOMIN_PAGE_4)),

	new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.GUTHIX_PAGE_1)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.GUTHIX_PAGE_2)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.GUTHIX_PAGE_3)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.GUTHIX_PAGE_4)),

	new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.RED_FIRELIGHTER, 5)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLUE_FIRELIGHTER, 5)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GREEN_FIRELIGHTER, 5)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.PURPLE_FIRELIGHTER, 5)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.WHITE_FIRELIGHTER, 5)),

	new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.PURPLE_SWEETS, 10)),

	new RepresentedWeightedObject<>(5, Item.of(/* Willow Composite Bow */10280)), new RepresentedWeightedObject<>(5, Item.of(/* Yew Composite Bow */10282)),

	new RepresentedWeightedObject<>(7, Item.of(/* Dragonstone Amulet */1708)), new RepresentedWeightedObject<>(7, Item.of(/* Diamond Amulet */1700)),
			new RepresentedWeightedObject<>(7, Item.of(/* Ruby Amulet */1698)), new RepresentedWeightedObject<>(7, Item.of(/* Emerald Amulet */1696)),
			new RepresentedWeightedObject<>(7, Item.of(/* Sapphire Amulet */1694)),

	new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.AIR_RUNE, 1_000)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.EARTH_RUNE, 1_000)),
			new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.FIRE_RUNE, 1_000)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.WATER_RUNE, 1_000)),
			new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.MIND_RUNE, 1_000)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.LAW_RUNE, 350)),
			new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.NATURE_RUNE, 350)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.BLOOD_RUNE, 350)),
			new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.DEATH_RUNE, 350)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.CHAOS_RUNE, 350)),

	new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.COINS, 1_000_000))));

	private static final Chance<Item> easyRewardGenerator = new Chance<>(Arrays.asList(
			/**
			 * Total weight = 1752
			 */
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.RED_ELE_SHIRT)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.RED_ELE_BLOUSE)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.RED_ELE_LEGS)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.RED_ELE_SKIRT)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLUE_ELE_SHIRT)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLUE_ELE_BLOUSE)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLUE_ELE_LEGS)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLUE_ELE_SKIRT)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GREEN_ELE_SHIRT)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GREEN_ELE_BLOUSE)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GREEN_ELE_LEGS)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GREEN_ELE_SKIRT)),

	new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ZAMORAK_ROBE_TOP)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ZAMORAK_ROBE_LEGS)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.SARADOMIN_ROBE_TOP)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.SARADOMIN_ROBE_LEGS)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GUTHIX_ROBE_TOP)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GUTHIX_ROBE_LEGS)),

	new RepresentedWeightedObject<>(4, Item.of(ItemIdentifiers.BLACK_CANE)),

	new RepresentedWeightedObject<>(4, Item.of(ItemIdentifiers.AMULET_OF_MAGIC_T)),

	new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.FLARED_TROUSERS)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.SLEEPING_CAP)),
			new RepresentedWeightedObject<>(9, Item.of(/* A Powdered Wig */10392)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.PANTALOONS)),

	new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.BLACK_PLATEBODY_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.BLACK_PLATELEGS_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.BLACK_PLATESKIRT_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.BLACK_FULL_HELM_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.BLACK_KITESHIELD_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.WIZARD_ROBE_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.WIZARD_HAT_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.BLUE_SKIRT_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.STUDDED_BODY_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.STUDDED_CHAPS_G)),

	new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.BLACK_PLATEBODY_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.BLACK_PLATELEGS_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.BLACK_PLATESKIRT_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.BLACK_FULL_HELM_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.BLACK_KITESHIELD_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.WIZARD_ROBE_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.WIZARD_HAT_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.BLUE_SKIRT_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.STUDDED_BODY_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.STUDDED_CHAPS_T)),

	new RepresentedWeightedObject<>(17, Item.of(ItemIdentifiers.BLACK_BERET)), new RepresentedWeightedObject<>(17, Item.of(ItemIdentifiers.WHITE_BERET)),
			new RepresentedWeightedObject<>(17, Item.of(ItemIdentifiers.BLUE_BERET)),

	new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.BOB_SHIRT)), new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.BOB_SHIRT + 2)),
			new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.BOB_SHIRT + 4)), new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.BOB_SHIRT + 6)),
			new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.BOB_SHIRT + 8)),

	new RepresentedWeightedObject<>(26, Item.of(ItemIdentifiers.HIGHWAYMAN_MASK)),

	new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_PLATEBODY)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_PLATELEGS)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_PLATESKIRT)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_FULL_HELM)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_KITESHIELD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_NAILS, 50)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_AXE)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_LONGSWORD)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_SCIMITAR)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_DAGGER)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_MACE)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_CLAWS)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_SWORD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_SPEAR)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_WARHAMMER)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_BATTLEAXE)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_2H_SWORD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_HALBERD)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_DEFENDER)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_KNIFE, 100)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.BLACK_DART, 100)),

	new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.OAK_PLANK)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.WILLOW_SHORTBOW)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.COIF)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.LEATHER_BODY)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.LEATHER_CHAPS)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.LEATHER_GLOVES)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.LEATHER_VAMBRACES)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.LEATHER_BOOTS)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.YEW_SHORTBOW)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.EARTH_TALISMAN)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.AIR_TALISMAN)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.FIRE_TALISMAN)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.WATER_TALISMAN)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.STEEL_PICKAXE)),

	new RepresentedWeightedObject<>(46, Item.of(ItemIdentifiers.SALMON)), new RepresentedWeightedObject<>(46, Item.of(ItemIdentifiers.TROUT))));

	private static final Chance<Item> mediumRewardGenerator = new Chance<>(Arrays.asList(
			/**
			 * Total weight = 1752
			 */
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLACK_ELE_SHIRT)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BLACK_ELE_LEGS)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.WHITE_ELE_BLOUSE)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.WHITE_ELE_SKIRT)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.PURPLE_ELE_SHIRT)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.PURPLE_ELE_BLOUSE)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.PURPLE_ELE_LEGS)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.PURPLE_ELE_SKIRT)),

	new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.SARADOMIN_MITRE)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GUTHIX_MITRE)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ZAMORAK_MITRE)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.SARADOMIN_CLOAK)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.GUTHIX_CLOAK)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ZAMORAK_CLOAK)),

	new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ARMADYL_ROBE_TOP)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ARMADYL_ROBE_LEGS)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BANDOS_ROBE_TOP)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.BANDOS_ROBE_LEGS)),
			new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ANCIENT_ROBE_TOP)), new RepresentedWeightedObject<>(2, Item.of(ItemIdentifiers.ANCIENT_ROBE_LEGS)),

	new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.PITH_HELMET)),

	new RepresentedWeightedObject<>(4, Item.of(ItemIdentifiers.ADAMANT_CANE)),

	new RepresentedWeightedObject<>(6, Item.of(ItemIdentifiers.PENGUIN_MASK)), new RepresentedWeightedObject<>(6, Item.of(ItemIdentifiers.SHEEP_MASK)),
			new RepresentedWeightedObject<>(6, Item.of(ItemIdentifiers.WOLF_MASK)), new RepresentedWeightedObject<>(6, Item.of(ItemIdentifiers.CAT_MASK)),
			new RepresentedWeightedObject<>(6, Item.of(ItemIdentifiers.BAT_MASK)),

	new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.RANGER_BOOTS)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.WIZARD_BOOTS)),

	new RepresentedWeightedObject<>(10, Item.of(ItemIdentifiers.BLACK_HEADBAND)), new RepresentedWeightedObject<>(10, Item.of(ItemIdentifiers.RED_HEADBAND)),
			new RepresentedWeightedObject<>(10, Item.of(ItemIdentifiers.BROWN_HEADBAND)),

	new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.RED_BOATER)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.ORANGE_BOATER)),
			new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.GREEN_BOATER)), new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.BLUE_BOATER)),
			new RepresentedWeightedObject<>(11, Item.of(ItemIdentifiers.BLACK_BOATER)),

	new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.ADAM_PLATEBODY_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.ADAM_PLATEBODY_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.ADAM_PLATESKIRT_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.ADAM_KITESHIELD_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.ADAM_FULL_HELM_G)),

	new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.ADAM_PLATEBODY_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.ADAM_PLATEBODY_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.ADAM_PLATESKIRT_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.ADAM_KITESHIELD_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.ADAM_FULL_HELM_T)),

	new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_PLATEBODY)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_PLATELEGS)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_PLATESKIRT)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_FULL_HELM)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_KITESHIELD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANTITE_NAILS, 50)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_AXE)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_LONGSWORD)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_SCIMITAR)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_DAGGER)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_MACE)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_CLAWS)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_SWORD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_SPEAR)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADDY_WARHAMMER)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_BATTLEAXE)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_2H_SWORD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_HALBERD)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_DEFENDER)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_KNIFE, 100)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADAMANT_DART, 100)),

	new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.TEAK_PLANK)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.YEW_SHORTBOW)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.FIRE_BATTLESTAFF)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.AMULET_OF_POWER)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.GREEN_DHIDE_BODY)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.GREEN_DHIDE_CHAPS)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.ADAMANT_PICKAXE)),

	new RepresentedWeightedObject<>(46, Item.of(ItemIdentifiers.SWORDFISH)), new RepresentedWeightedObject<>(46, Item.of(ItemIdentifiers.LOBSTER))));

	private static final Chance<Item> hardRewardGenerator = new Chance<>(Arrays.asList(
			/**
			 * Total weight = 1752
			 */
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_PLATEBODY)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_PLATELEGS)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_KITESHIELD)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_FULL_HELMET)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_ROBE_TOP)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_ROBE)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_AMULET)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_MAGE_HAT)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_RANGE_TOP)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_RANGE_LEGS)),
			new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_RANGE_COIF)), new RepresentedWeightedObject<>(1, Item.of(ItemIdentifiers.THIRD_AGE_VAMBRACES)),

	new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.GILDED_PLATEBODY)), new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.GILDED_PLATEBODY)),
			new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.GILDED_PLATESKIRT)), new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.GILDED_KITESHIELD)),
			new RepresentedWeightedObject<>(3, Item.of(ItemIdentifiers.GILDED_FULL_HELM)),

	new RepresentedWeightedObject<>(4, Item.of(ItemIdentifiers.AMULET_OF_GLORY_T)),

	new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.SARADOMIN_PLATEBODY)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.SARADOMIN_PLATEBODY)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.SARADOMIN_PLATESKIRT)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.SARADOMIN_KITESHIELD)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.SARADOMIN_FULL_HELM)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.GUTHIX_PLATEBODY)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.GUTHIX_PLATEBODY)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.GUTHIX_PLATESKIRT)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.GUTHIX_KITESHIELD)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.GUTHIX_FULL_HELM)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.ZAMORAK_PLATEBODY)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.ZAMORAK_PLATEBODY)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.ZAMORAK_PLATESKIRT)), new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.ZAMORAK_KITESHIELD)),
			new RepresentedWeightedObject<>(5, Item.of(ItemIdentifiers.ZAMORAK_FULL_HELM)),

	new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.SARADOMIN_BODY)), new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.SARADOMIN_CHAPS)),
			new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.SARADOMIN_COIF)), new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.GUTHIX_BODY)),
			new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.GUTHIX_CHAPS)), new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.GUTHIX_COIF)),
			new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.ZAMORAK_BODY)), new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.ZAMORAK_CHAPS)),
			new RepresentedWeightedObject<>(7, Item.of(ItemIdentifiers.ZAMORAK_COIF)),

	new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.SARADOMIN_STOLE)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.SARADOMIN_CROZIER)),
			new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.GUTHIX_STOLE)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.GUTHIX_CROZIER)),
			new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.ZAMORAK_STOLE)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.ZAMORAK_CROZIER)),

	new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.ARMADYL_MITRE)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.ARMADYL_CLOAK)),
			new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.BANDOS_MITRE)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.BANDOS_CLOAK)),
			new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.ANCIENT_MITRE)), new RepresentedWeightedObject<>(9, Item.of(ItemIdentifiers.ANCIENT_CLOAK)),

	new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.RUNE_PLATEBODY_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.RUNE_PLATEBODY_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.RUNE_PLATESKIRT_G)), new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.RUNE_KITESHIELD_G)),
			new RepresentedWeightedObject<>(12, Item.of(ItemIdentifiers.RUNE_FULL_HELM_G)),

	new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.RUNE_PLATEBODY_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.RUNE_PLATEBODY_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.RUNE_PLATESKIRT_T)), new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.RUNE_KITESHIELD_T)),
			new RepresentedWeightedObject<>(15, Item.of(ItemIdentifiers.RUNE_FULL_HELM_T)),

	new RepresentedWeightedObject<>(17, Item.of(ItemIdentifiers.ENCHANTED_HAT)), new RepresentedWeightedObject<>(17, Item.of(ItemIdentifiers.ENCHANTED_TOP)),
			new RepresentedWeightedObject<>(17, Item.of(ItemIdentifiers.ENCHANTED_ROBE)),

	new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.BLACK_CAVALIER)), new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.TAN_CAVALIER)),
			new RepresentedWeightedObject<>(20, Item.of(ItemIdentifiers.DARK_CAVALIER)),

	new RepresentedWeightedObject<>(25, Item.of(ItemIdentifiers.PIRATES_HAT)), new RepresentedWeightedObject<>(25, Item.of(ItemIdentifiers.TOP_HAT)),
			new RepresentedWeightedObject<>(25, Item.of(ItemIdentifiers.ROBIN_HOOD_HAT)),

	new RepresentedWeightedObject<>(27, Item.of(ItemIdentifiers.RUNE_CANE)),

	new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_PLATEBODY)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_PLATELEGS)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_PLATESKIRT)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_FULL_HELM)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_KITESHIELD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_NAILS, 50)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNITE_BOLTS, 350)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_AXE)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_LONGSWORD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_SCIMITAR)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_DAGGER)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_MACE)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_CLAWS)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_SWORD)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_SPEAR)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.ADDY_WARHAMMER)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_BATTLEAXE)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_2H_SWORD)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_HALBERD)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_DEFENDER)),
			new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_KNIFE, 100)), new RepresentedWeightedObject<>(30, Item.of(ItemIdentifiers.RUNE_DART, 100)),

	new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.MAHOGANY_PLANK_NOTE, 50)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.RUNE_CROSSBOW)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.MAGIC_SHORTBOW)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.MAGIC_LONGBOW)),
			new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.BLACK_DHIDE_BODY)), new RepresentedWeightedObject<>(40, Item.of(ItemIdentifiers.BLACK_DHIDE_CHAPS)),

	new RepresentedWeightedObject<>(46, Item.of(ItemIdentifiers.SHARK)), new RepresentedWeightedObject<>(46, Item.of(ItemIdentifiers.LOBSTER))));

	/**
	 * The hash map containing all valid clue scrolls.
	 */
	public static HashMap<Integer, ClueScroll> clueScrolls = new HashMap<>();

	/**
	 * Populates the <code>clueScrolls</code> hash map with valid clue scroll objects.
	 */
	static {
		clueScrolls.put(2677, new MapScroll(2677, 7045, location -> location.equals(Location.create(3290, 3372, 0)), Difficulty.EASY));
		clueScrolls.put(2678, new MapScroll(2678, 7113, location -> location.equals(Location.create(3092, 3226, 0)), Difficulty.EASY));
		clueScrolls.put(2679, new MapScroll(2679, 7162, location -> location.equals(Location.create(2702, 3428, 0)), Difficulty.EASY));
		clueScrolls.put(2680, new MapScroll(2680, 17537, location -> location.equals(Location.create(2970, 3414, 0)), Difficulty.EASY));
		clueScrolls.put(2803, new MapScroll(2803, 7271, location -> location.equals(Location.create(3043, 3399, 0)), Difficulty.MEDIUM));
		clueScrolls.put(2805, new MapScroll(2805, 9043, location -> location.equals(Location.create(2616, 3077, 0)), Difficulty.MEDIUM));
		clueScrolls.put(2807, new MapScroll(2807, 9275, location -> location.equals(Location.create(3109, 3151, 0)), Difficulty.MEDIUM));
		clueScrolls.put(2809, new MapScroll(2809, 17634, location -> location.equals(Location.create(2722, 3339, 0)), Difficulty.MEDIUM));
		clueScrolls.put(2729, new CoordinateScroll(2729, "\n\n\n03 degrees 3 minutes south,\n05 degrees 3 minutes east\n \n \n \n ",
				location -> location.equals(Location.create(2602, 3062, 0)), Difficulty.HARD));
		clueScrolls.put(2731, new CoordinateScroll(2731, "\n\n\n17 degrees 50 minutes north,\n08 degrees 30 minutes east\n \n \n \n ",
				location -> location.equals(Location.create(2712, 3732, 0)), Difficulty.HARD));
		clueScrolls.put(3513,
				new EmoteScroll(3513, location -> location.withinRange(Location.create(2598, 3280, 0), 1), Difficulty.EASY, 2110,
						new Item[] {
								/** Head */
								null,
								/** Cape */
								null,
								/** Amulet */
								null,
								/** Weapon */
								null,
								/** Chest */
								Item.of(ItemIdentifiers.STUDDED_BODY),
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								Item.of(ItemIdentifiers.BRONZE_PLATELEGS),
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								null,
								/** Empty */
								null,
								/** Ring */
								null,
								/** Arrows */
								null },
						"\n\nBlow a raspberry at the monkey\ncage in Ardougne Zoo.\nEquip a studded body and bronze\nplatelegs. \n \n \n"));
		clueScrolls.put(3514,
				new EmoteScroll(3514, location -> location.withinRange(Location.create(2611, 3393, 0), 2), Difficulty.EASY, 2106,
						new Item[] {
								/** Head */
								null,
								/** Cape */
								null,
								/** Amulet */
								Item.of(/* Sapphire Amulet */1694),
								/** Weapon */
								null,
								/** Chest */
								Item.of(ItemIdentifiers.BRONZE_CHAINBODY),
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								null,
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								null,
								/** Empty */
								null,
								/** Ring */
								Item.of(ItemIdentifiers.EMERALD_RING),
								/** Arrows */
								null },
						"\n\nDance a jig by the entrance to the\nFishing Guild, equip a sapphire\namulet, an emerald ring and a\nbronze chainbody\n \n \n"));
		clueScrolls.put(2722,
				new EmoteScroll(2722, location -> location.withinRange(Location.create(2587, 3420, 0), 2), Difficulty.HARD, 2110,
						new Item[] {
								/** Head */
								null,
								/** Cape */
								null,
								/** Amulet */
								null,
								/** Weapon */
								Item.of(ItemIdentifiers.RUNE_WARHAMMER),
								/** Chest */
								null,
								/** Shield */
								Item.of(ItemIdentifiers.ELEMENTAL_SHIELD),
								/** Empty */
								null,
								/** Legs */
								Item.of(ItemIdentifiers.BLUE_DHIDE_CHAPS),
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								null,
								/** Empty */
								null,
								/** Ring */
								null,
								/** Arrows */
								null },
				"\n\nBlow a raspberry in the Fishing Guild\nbank. Beware of double agents!\nEquip an elemental shield, blue\ndragonhide chaps and a rune\nwarhammer\n \n"));
		clueScrolls.put(2723,
				new EmoteScroll(2723, location -> location.withinRange(Location.create(2920, 3163, 0), 10), Difficulty.HARD, 2112,
						new Item[] {
								/** Head */
								null,
								/** Cape */
								null,
								/** Amulet */
								Item.of(ItemIdentifiers.AMULET_OF_POWER),
								/** Weapon */
								null,
								/** Chest */
								null,
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								null,
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								null,
								/** Empty */
								null,
								/** Ring */
								Item.of(ItemIdentifiers.DIAMOND_RING),
								/** Arrows */
								null },
				"\nSalute in the banana plantation.\nBeware of double agents!\nEquip a diamond ring, amulet\nof power and nothing on\nyour chest and legs.\n \n \n "));
		clueScrolls.put(2682,
				new EmoteScroll(2682, location -> location.withinRange(Location.create(2977, 3240, 0), 11), Difficulty.EASY, 2113,
						new Item[] {
								/** Head */
								null,
								/** Cape */
								null,
								/** Amulet */
								Item.of(ItemIdentifiers.GOLD_NECKLACE),
								/** Weapon */
								Item.of(ItemIdentifiers.BRONZE_SPEAR),
								/** Chest */
								null,
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								null,
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								null,
								/** Empty */
								null,
								/** Ring */
								Item.of(ItemIdentifiers.GOLD_RING),
								/** Arrows */
								null },
						"\n\nShrug in the mine near Rimmington,\nEquip a gold necklace, a gold ring\nand a bronze spear.\n \n \n "));
		clueScrolls.put(2725,
				new EmoteScroll(2725, location -> location.withinRange(Location.create(2728, 3348, 0), 1), Difficulty.EASY, 858,
						new Item[] {
								/** Head */
								null,
								/** Cape */
								null,
								/** Amulet */
								Item.of(/** Emerald Amulet */
										1696),
								/** Weapon */
								Item.of(ItemIdentifiers.OAK_LONGBOW),
								/** Chest */
								null,
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								Item.of(ItemIdentifiers.IRON_PLATELEGS),
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								null,
								/** Empty */
								null,
								/** Ring */
								null,
								/** Arrows */
								null },
						"\n\nBow outside the entrance to the\nLegends' Guild. Equip iron platelegs,\nan emerald amulet and an oak\nlongbow.\n \n "));
		clueScrolls.put(2811,
				new EmoteScroll(2811, location -> location.withinRange(Location.create(3370, 3428, 0), 1), Difficulty.MEDIUM, 859,
						new Item[] {
								/** Head */
								Item.of(/** Green Hat */
										658),
								/** Cape */
								null,
								/** Amulet */
								null,
								/** Weapon */
								Item.of(ItemIdentifiers.IRON_PICKAXE),
								/** Chest */
								null,
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								null,
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								Item.of(ItemIdentifiers.SNAKESKIN_BOOTS),
								/** Empty */
								null,
								/** Ring */
								null,
								/** Arrows */
								null },
						"\n\nBeckon in the Digsite, near the eastern\nwinch. Equip a green hat, snakeskin\nboots and an iron pickaxe.\n \n \n \n "));
		clueScrolls.put(2727,
				new EmoteScroll(2727, location -> location.withinRange(Location.create(2926, 3484, 0), 5), Difficulty.EASY, 862,
						new Item[] {
								/** Head */
								Item.of(/** Blue Hat */
										740),
								/** Cape */
								null,
								/** Amulet */
								null,
								/** Weapon */
								Item.of(ItemIdentifiers.BRONZE_2H_SWORD),
								/** Chest */
								null,
								/** Shield */
								null,
								/** Empty */
								null,
								/** Legs */
								null,
								/** Empty */
								null,
								/** Gloves */
								null,
								/** Boots */
								Item.of(ItemIdentifiers.LEATHER_BOOTS),
								/** Empty */
								null,
								/** Ring */
								null,
								/** Arrows */
								null },
						"\n\nCheer at the Druids' Circle. Equip\na blue wizard hat, a bronze two-\nhanded sword and leather boots.\n \n \n \n "));
	}

	/**
	 * Provides a method to retrieve a random reward.
	 *
	 * @author Michael | Chex
	 */
	public interface ClueReward {

		/**
		 * Generates a random reward.
		 *
		 * @return The reward as an {@link com.koios.game.model.item.Item}.
		 */
		public Item getReward();
	}

	/**
	 * Generates a random item that can be rewarded by any level clue.
	 *
	 * @return The reward item.
	 */
	private static Item crossTrialItems() {
		return new ClueReward() {

			@Override
			public Item getReward() {
				Item reward = crossTrailRewardGenerator.nextObject().get().copy();
				if (reward.getCount() > 1) {
					reward.setCount(NumberUtil.random(reward.getCount()) + 1);
				}
				return reward;
			}
		}.getReward();
	}

	/**
	 * A list of difficulties for a clue scroll. Each difficulty has specific rewards.
	 *
	 * @author Michael | Chex
	 */
	public static enum Difficulty {

		/**
		 * Easy clue scroll rewards.
		 */
		EASY(() -> {
			Item reward = null;
			if (Math.random() > 1 - 1 / 5.0) {
				reward = crossTrialItems();
			} else {
				reward = easyRewardGenerator.nextObject().get().copy();
				if (reward.getCount() > 1) {
					reward.setCount(NumberUtil.random(reward.getCount()) + 1);
				}
			}
			return reward;
		}),

		/**
		 * Medium clue scroll rewards.
		 */
		MEDIUM(() -> {
			Item reward = null;
			if (Math.random() > 1 - 1 / 5.0) {
				reward = EASY.getRewards().getReward();
			} else {
				reward = mediumRewardGenerator.nextObject().get().copy();
				if (reward.getCount() > 1) {
					reward.setCount(NumberUtil.random(reward.getCount()) + 1);
				}
			}
			return reward;
		}),

		/**
		 * Hard clue scroll rewards.
		 */
		HARD(() -> {
			Item reward = null;

			if (Math.random() > 1 - 1 / 5.0) {
				reward = MEDIUM.getRewards().getReward();
			} else {
				reward = hardRewardGenerator.nextObject().get().copy();

				if (reward.getCount() > 1) {
					reward.setCount(NumberUtil.random(reward.getCount()) + 1);
				}
			}

			return reward;
		});

		/**
		 * All possible rewards for the difficulty.
		 */
		private final ClueReward reward;

		/**
		 * Constructs a new clue scroll difficulty.
		 *
		 * @param rewardsTable - All possible rewards for the difficulty.
		 */
		private Difficulty(ClueReward reward) {
			this.reward = reward;
		}

		/**
		 * Gets the rewards table. Each reward is formated:
		 *
		 * @return The list of rewards.
		 */
		public ClueReward getRewards() {
			return reward;
		}

	}

	/**
	 * Retrieves a clue scroll object, given the item id.
	 *
	 * @param item - The clue scroll item id.
	 * @return The clue scroll object, null if <code>item</code> is not a clue scroll.
	 */
	public static ClueScroll getClueScroll(int item) {
		return clueScrolls.get(item);
	}

	/**
	 * Checks if a player has a clue scroll in their inventory.
	 *
	 * @param player - The player to check.
	 * @return <code>True</code> if the player has a valid clue scroll.
	 */
	public static boolean playerHasScroll(Player player) {
		if (player.getBank().contains(item -> getClueScroll(item.getId()) != null)) {
			return true;
		}
		return player.getInventory().contains(item -> getClueScroll(item.getId()) != null);
	}

	/**
	 * Generates a random clue scroll by difficulty.
	 *
	 * @param player - The player to generate the clue scroll for.
	 * @param difficulty - The difficulty of the clue scroll to generate.
	 * @return The clue scroll as an {@link Item}.
	 */
	public static Item getRandomClue(Player player, Difficulty difficulty) {
		List<ClueScroll> scrolls = clueScrolls.values().stream()
				.filter(scroll -> scroll.getDifficulty().equals(difficulty) && !player.getBank().contains(scroll.getItem()) && !player.getInventory().contains(scroll.getItem()))
				.collect(Collectors.toList());

		if (scrolls.isEmpty()) {
			return null;
		}

		return Item.of(scrolls.get(NumberUtil.random(scrolls.size())).getItem());
	}

}
