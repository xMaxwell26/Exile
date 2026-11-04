package com.koios.game.content.skill.hunter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.MobManager;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

/**
 * @author J
 */
public final class Hunter {

	public static enum BoxTrapped implements Capturable {

		CARNIVOROUS_CHINCHOMPA("Carnivorous Chinchompa", 5080, new Item(10034), 63, 315), CHINCHOMPA("Chinchompa", 5079, new Item(10033), 53, 245), FERRET("Ferret", 5081,
				new Item(10092), 27, 115), GECKO("Gecko", 6915, new Item(12488), 27, 100), GRENWALL("Grenwall", 7010, new Item(12539), 77, 415), MONKEY("Monkey", 6942,
						new Item(12201), 27, 100), PAWYA("Pawya", 7012, new Item(12535), 66, 400), PENGUIN("Penguin", 6908, new Item(12481), 56, 150), PLATYPUS("Platypus", 7015,
								new Item(12547), 48, 0), RACCOON("Raccoon", 6913, new Item(12199), 27, 100);

		public static BoxTrapped[] VALUES = BoxTrapped.values();

		public static BoxTrapped getForMob(int npc) {
			for (BoxTrapped tested : BoxTrapped.VALUES) {
				if (tested.getNpc() == npc) {
					return tested;
				}
			}

			return null;
		}

		private final int experience;
		private final Item item;
		private final Item[] items;
		private final int levelRequired;
		private final String name;
		private final int npc;

		private BoxTrapped(String name, int npc, Item item, int levelRequired, int experience) {
			this.name = name;
			this.npc = npc;
			this.item = item;
			this.levelRequired = levelRequired;
			this.experience = experience;
			items = new Item[] { item };
		}

		@Override
		public int getExperience() {
			return experience;
		}

		public Item getItem() {
			return item;
		}

		@Override
		public Item[] getItems() {
			return items;
		}

		@Override
		public int getLevelRequired() {
			return levelRequired;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getNpc() {
			return npc;
		}

		@Override
		public TrapType getTrapType() {
			return TrapType.BOX;
		}

		@Override
		public int identifier() {
			return Capturable.BOX;
		}
	}

	public static enum Butterfly {

		BLACK_WARLOCK(5082, 10014, 45, 54, 95, 90, 650, 125), RUBY_HARVEST(5085, 10020, 15, 24, 80, 75, 300, 50), SAPPHIRE_GLACIALIS(5084, 10018, 25, 34, 85, 80, 400,
				70), SNOWY_KNIGHT(5083, 10016, 35, 44, 90, 85, 500, 100);

		private static final Butterfly[] VALUES = Butterfly.values();

		public static Butterfly getButterflyForJar(int id) {
			for (Butterfly butterfly : Butterfly.VALUES) {
				if (butterfly.getJar() == id) {
					return butterfly;
				}
			}

			return null;
		}

		private final int barehandedAgilityExperience;
		private final int barehandedAgilityLevel;
		private final int barehandedHunterExperience;
		private final int barehandedHunterLevel;
		private final int experience;
		private final int jar;
		private final int levelRequired;
		private final int mobId;

		Butterfly(int npc, int jar, int levelRequired, int experience, int barehandedHunterLevel, int barehandedAgilityLevel, int barehandedHunterExperience,
				int barehandedAgilityExperience) {
			mobId = npc;
			this.jar = jar;
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.barehandedHunterLevel = barehandedHunterLevel;
			this.barehandedAgilityLevel = barehandedAgilityLevel;
			this.barehandedHunterExperience = barehandedHunterExperience;
			this.barehandedAgilityExperience = barehandedAgilityExperience;
		}

		public int getBarehandedAgilityExperience() {
			return barehandedAgilityExperience;
		}

		public int getBarehandedAgilityLevel() {
			return barehandedAgilityLevel;
		}

		public int getBarehandedHunterExperience() {
			return barehandedHunterExperience;
		}

		public int getBarehandedHunterLevel() {
			return barehandedHunterLevel;
		}

		public int getExperience() {
			return experience;
		}

		public int getJar() {
			return jar;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getMobId() {
			return mobId;
		}

	}

	public interface Capturable {

		int BOX = 2;
		int NET = 3;
		int SNARE = 1;

		int getExperience();

		Item[] getItems();

		int getLevelRequired();

		String getName();

		TrapType getTrapType();

		int identifier();

	}

	public static enum Impling {
		BABY(6055, 11238, 17, 20,
				new Item[] { new Item(995, 3000) }), DRAGON(6064, 11256, 83, 390,
						new Item[] { new Item(536, 10), new Item(1249), new Item(1305), new Item(1713, 5), new Item(1149), new Item(1434), new Item(4587),
								new Item(30000) }), EARTH(6058, 11244, 36, 126, new Item[] { new Item(995, 10000) }), ECLECTIC(6060, 11248, 50, 205,
										new Item[] { new Item(995, 15000) }), ESSENCE(6059, 11246, 42, 160, new Item[] { new Item(995, 12500) }), GOURMET(6057, 11242, 28, 82,
												new Item[] { new Item(995, 7500) }), KINGLY(7903, 15517, 91, 434,
														new Item[] { new Item(1149), new Item(1305), new Item(4587), new Item(9244, 100), new Item(11212,
																30), new Item(7158), new Item(1187), new Item(2362, 40), new Item(2364, 20), new Item(892, 300),
										new Item(995, 100000) }), MAGPIE(6062, 11252, 65, 289, new Item[] { new Item(995, 20000) }), NATURE(6061, 11250, 58, 250,
												new Item[] { new Item(995, 20000) }), NINJA(6063, 11254, 74, 339,
														new Item[] { new Item(1215), new Item(536, 5), new Item(1249), new Item(1305), new Item(1712), new Item(4091),
																new Item(4093), new Item(22500) }), PIRATE(7845, 13337, 76,
																		350,
																		new Item[] { new Item(536, 7), new Item(1249), new Item(1305), new Item(1712), new Item(4091),
																				new Item(4093), new Item(25000) }), SPIRIT(7866, 15513, 54, 227,
																						new Item[] { new Item(995, 17500) }), YOUNG(6056, 11240, 22, 48,
																								new Item[] { new Item(995, 5000) }), ZOMBIE(7902, 15515, 87, 412,
																										new Item[] { new Item(527, 100), new Item(533, 50), new Item(535, 50),
																												new Item(536, 25), new Item(4085, 15), new Item(6816, 10),
																												new Item(6730, 5), new Item(18331, 3), new Item(50000) });

		private static final Impling[] VALUES = Impling.values();

		public static Impling getImplingForJar(int id) {
			for (Impling impling : Impling.VALUES) {
				if (impling.getJar() == id) {
					return impling;
				}
			}

			return null;
		}

		public static Impling getImplingJar(int id) {
			for (Impling impling : Impling.VALUES) {
				if (impling.getMobId() == id) {
					return impling;
				}
			}

			return null;
		}

		private final int experience;
		private final int jar;
		private final int levelRequired;
		private final int mobId;
		private final Item[] rewards;

		private Impling(int mobId, int jar, int levelRequired, int experience, Item[] rewards) {
			this.mobId = mobId;
			this.jar = jar;
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.rewards = rewards;
		}

		public int getExperience() {
			return experience;
		}

		public int getJar() {
			return jar;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getMobId() {
			return mobId;
		}

		public Item getRandomReward() {
			return rewards[NumberUtil.random(rewards.length)];
		}
	}

	public static enum NetTrapped implements Capturable {

		BLACK_SALAMANDER("Black salamander", 5116, new Item(10148), new Item(10145), 67, 304, 19666, 19667), ORANGE_SALAMANDER("Orange Salamander", 5114, new Item(10146),
				new Item(10143), 47, 224, 19655, 19654),
				// PENGIUN("Penguin", -1, new Item(12188), new Item(341), 50, 250),
				RED_SALAMANDER("Red salamander", 5115, new Item(10147), new Item(10144), 59, 272, 19658, 19659),
				// BABY_SQUIRREL("Squirrel", 7312, new Item(12490), new Item(12130), 29, 152),
				SWAMP_LIZARD("Swamp lizard", 5117, new Item(10149), new Item(10142), 29, 152, 19674, 19675);

		public static final NetTrapped[] VALUES = NetTrapped.values();

		public static NetTrapped getForMob(int npc) {
			for (NetTrapped tested : NetTrapped.VALUES) {
				if (tested.getMobId() == npc) {
					return tested;
				}
			}

			return null;
		}

		private final Item bait;
		private final int capturedObject;
		private final int experience;
		private final Item[] items;
		private final int levelRequired;
		private final int mobId;
		private final String name;
		private final Item reward;
		private final int successObject;

		private NetTrapped(String name, int npc, Item reward, Item bait, int levelRequired, int experience, int successObject, int capturedObject) {
			this.name = name;
			mobId = npc;
			this.reward = reward;
			this.bait = bait;
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.successObject = successObject;
			this.capturedObject = capturedObject;
			items = new Item[] { reward };
		}

		public Item getBait() {
			return bait;
		}

		public int getCapturedObject() {
			return capturedObject;
		}

		@Override
		public int getExperience() {
			return experience;
		}

		@Override
		public Item[] getItems() {
			return items;
		}

		@Override
		public int getLevelRequired() {
			return levelRequired;
		}

		public int getMobId() {
			return mobId;
		}

		@Override
		public String getName() {
			return name;
		}

		public Item getReward() {
			return reward;
		}

		public int getSuccessObject() {
			return successObject;
		}

		@Override
		public TrapType getTrapType() {
			return TrapType.NET;
		}

		@Override
		public int identifier() {
			return Capturable.NET;
		}
	}

	public static enum SnareTrapped implements Capturable {

		CERULEAN_TWITCH("Cerulean twitch", 5074, 10089, 11, 65, 19181, 19182), COPPER_LONGTAIL("Copper longtail", 5076, 10091, 9, 61, 19185, 19186), CRIMSON_SWIFT("Crimson swift",
				5073, 10088, 1, 34, 19179, 19180), GOLDEN_WARBLER("Golden warbler", 5075, 10090, 5, 48, 19183, 19184), TROPICAL_WAGTAIL("Tropical wagtail", 5072, 10087, 19, 95,
						19177, 19178), WIMPY_BIRD("Wimpy bird", 7031, 11525, 39, 167, -1, -1);

		public static SnareTrapped[] VALUES = SnareTrapped.values();

		public static SnareTrapped getForMob(int npc) {
			for (SnareTrapped tested : SnareTrapped.VALUES) {
				if (tested.getNpc() == npc) {
					return tested;
				}
			}

			return null;
		}

		private final int capturedObject;
		private final int experience;
		private final Item feathers;
		private final Item[] items;
		private final int levelRequired;
		private final String name;
		private final int npc;
		private final int successObject;

		private SnareTrapped(String name, int npc, int feathers, int levelRequired, int experience, int successObject, int capturedObject) {
			this.name = name;
			this.npc = npc;
			this.feathers = new Item(feathers, 5);
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.successObject = successObject;
			this.capturedObject = capturedObject;
			items = new Item[] { new Item(ItemIdentifiers.RAW_BIRD_MEAT), this.feathers };
		}

		public int getCapturedObject() {
			return capturedObject;
		}

		@Override
		public int getExperience() {
			return experience;
		}

		public Item getFeathers() {
			return feathers;
		}

		@Override
		public Item[] getItems() {
			return items;
		}

		@Override
		public int getLevelRequired() {
			return levelRequired;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getNpc() {
			return npc;
		}

		public int getSuccessObject() {
			return successObject;
		}

		@Override
		public TrapType getTrapType() {
			return TrapType.SNARE;
		}

		@Override
		public int identifier() {
			return Capturable.SNARE;
		}
	}

	public static enum TrapType {

		BOX(ItemIdentifiers.BOX_TRAP, 19187, 5208, 5212), NET(ItemIdentifiers.SMALL_FISHING_NET, 19651, 5208, 5207), SNARE(ItemIdentifiers.BIRD_SNARE, 19175, 5208, 5207);

		public static TrapType[] VALUES = TrapType.values();

		public static TrapType getTrapType(int item) {
			for (TrapType type : TrapType.VALUES) {
				if (item == type.getItem().getId()) {
					return type;
				}
			}

			return null;
		}

		private final int dismantleAnimation;
		private final Item item;
		private final int layAnimation;
		private final int object;

		private TrapType(int id, int object, int layAnimation, int dismantleAnimation) {
			item = new Item(id);
			this.object = object;
			this.layAnimation = layAnimation;
			this.dismantleAnimation = dismantleAnimation;
		}

		public int getDismantleAnimation() {
			return dismantleAnimation;
		}

		public Item getItem() {
			return item;
		}

		public int getLayAnimation() {
			return layAnimation;
		}

		public int getObject() {
			return object;
		}

	}

	private static final String SPAWN_LOCATION = "./data/cfg/hunter-spawns.cfg";

	public static void init() {
		try (Scanner scanner = new Scanner(new File(SPAWN_LOCATION))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = line.split("\t");
				int id = Integer.parseInt(data[0]);
				int x = Integer.parseInt(data[1]);
				int y = Integer.parseInt(data[2]);
				int z = Integer.parseInt(data[3]);
				MobManager.spawnMob(id, Location.create(x, y, z), 1, 0, 0, 0, 0);
			}
		} catch (FileNotFoundException ex) {
			System.err.println("Missing hunter spawn configuration file:");
			ex.printStackTrace();
		}
	}

	public static boolean successfulCatch(int req, int level, boolean hasNet) {
		req *= hasNet ? 1.75F : 2.0F;
		level += 10;
		req = req * (req - req / 8);
		level = level * level;
		return NumberUtil.random(req) < NumberUtil.random(level);
	}

	public static boolean successfulTrap(int req, int level) {
		req += 20;
		level += 10;
		req = req * (req - req / 8);
		level = level * level;
		return NumberUtil.random(req) < NumberUtil.random(level);
	}

}
