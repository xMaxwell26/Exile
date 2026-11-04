package com.koios.game.content.achievement;

public enum Achievement {

	// Easy Achievements
	WOODCUTTER(1, "Cut 50 Trees", "chopped", "logs", 50, 2, 29011, 113083),
	ROCK_CRABS(2, "Kill 50 Rock Crabs", "killed", "rock crabs", 50, 2, 29012, 113084),
	BURY_BONES(3, "Bury 50 Bones", "buried", "bones", 50, 2, 29013, 113085),
	PLANT_POTATO(4, "Grow 1 Potato Plant", "grown", "potato plants", 1, 2, 29014, 113086),
	DEATH_COUNT(5, "Die 5 times", "died", "times", 5, 2, 29015, 113087),
	FIREMAKING(6, "Burn 50 logs", "burned", "logs", 50, 2, 29016, 113088),
	COOKING(7, "Successfully cook 75 values", "cooked", "values", 75, 2, 29017, 113089),
	KILL_DHAROK(8, "Kill 2 Dharoks brothers", "killed", "dharoks brothers", 2, 2, 29018, 113090),
	KILL_GUTHANS(9, "Kill 2 Guthans brothers", "killed", "guthans brothers", 2, 2, 29019, 113091),
	KILL_VERACS(10, "Kill 2 Veracs brothers", "killed", "veracs brothers", 2, 2, 29020, 113092),
	KILL_KARILS(11, "Kill 2 Karils brothers", "killed", "karils brothers", 2, 2, 29021, 113093),
	KILL_AHRIMS(12, "Kill 2 Ahrims brothers", "killed", "ahrims brothers", 2, 2, 29022, 113094),
	KILL_TORAGS(13, "Kill 2 Torags brothers", "killed", "torags brothers", 2, 2, 29023, 113095),
	VOTE(14, "Vote 5 times", "voted", "times", 5, 2, 29024, 113096),
	CRAFT_LEATHER(15, "Craft 100 leather hides", "crafted", "hides", 100, 2, 29025, 113097),
	CUT_GEMS(16, "Cut 75 gems", "cut", "gems", 75, 2, 29026, 113098),
	CATCH_FISH(17, "Catch 100 fish", "caught", "fish", 100, 2, 29027, 113099),
	OBTAIN_SARADOMIN_CAPE(18, "Pray at the Saradomin Statue", "prayed", "times", 1, 2, 29028, 113100),
	OBTAIN_ZAMORAK_CAPE(19, "Pray at the Zamorak Statue", "prayed", "times", 1, 2, 29029, 113101),
	OBTAIN_GUTHIX_CAPE(20, "Pray at the Guthix Statue", "prayed", "times", 1, 2, 29030, 113102),
	KILL_MOSS_GIANTS(21, "Kill 50 Moss Giants", "killed", "moss giants", 50, 2, 29031, 113103),
	COMPLETE_GNOME_COURSE(22, "Complete the Gnome Course", "completed", "gnome courses", 1, 2, 29032, 113104),
	RECOLOR_WHIP(23, "Recolor an Abyssal Whip", "recolored", "abyssal whips", 1, 2, 29033, 113105),
	RECOLOR_DARK_BOW(24, "Recolor a Dark Bow", "recolored", "dark bows", 1, 2, 29034, 113106),

	// Medium Achievements
	SKILLCAPE_EMOTE(25, "Perform the skillcape emote", "performed", "emotes", 1, 4, 29035, 113107),
	ALCH(26, "Cast 250 alchemy spells", "casted", "alchemy spells", 250, 4, 29036, 113108),
	KILL_PLAYER(27, "Kill 100 Players in PvP", "killed", "players", 100, 4, 29037, 113109),
	PEST_CONTROL(28, "Win 10 Pest Control Games", "won", "pest control", 10, 4, 29038, 113110),
	GAMBLER(29, "Win 20 rounds of gambling", "won", "rounds", 20, 4, 29039, 113111),
	REVENANT_HELLHOUND(30, "Kill 50 Revenant Hellhounds", "killed", "revenant hellhounds", 50, 4, 29040, 113112),
	CATCH_SHARK(31, "Catch 150 sharks", "caught", "sharks", 150, 4, 29041, 113113),
	CUT_ONYX(32, "Cut an Onyx", "cut", "onyx", 1, 4, 29042, 113114),
	PICKPOCKET(33, "Pickpocket 125 NPCs", "pickpocketed", "npcs", 125, 4, 29043, 113115),
	WIN_DUEL(34, "Win 10 duels", "won", "duels", 10, 4, 29044, 113116),
	KILL_KBD(35, "Kill the King black dragon", "killed", "King black dragon", 1, 4, 29045, 113117),
	KILL_BRONZE_DRAGONS(36, "Kill 75 Bronze Dragons", "killed", "bronze dragons", 75, 4, 29046, 113118),
	KILL_ICE_WOLF(37, "Kill 125 Ice Wolfs", "killed", "ice wolfs", 125, 4, 29047, 113119),
	FIRE_CANNONBALLS(38, "Fire 1000 Cannon Shots", "fired", "cannon shots", 1000, 4, 29048, 113120),
	SET_BANK_PIN(39, "Set a Bank PIN", "set", "Bank PIN", 1, 4, 29049, 113121),
	SMITH_GOLD(40, "Smith 200 Gold Items", "smithed", "gold items", 200, 4, 29050, 113122),
	CHOP_MAGIC_TREE(41, "Cut 100 Magic Trees", "cut", "magic trees", 100, 4, 29051, 113123),
	BURY_DRAGON_BONES(42, "Bury 150 Dragon Bones", "buried", "dragon bones", 150, 4, 29052, 113124),
	KILL_SARADOMIN_MINIONS(43, "Kill 50 Saradomin Minions", "killed", "saradomin minions", 50, 4, 29053, 113125),
	KILL_BANDOS_MINIONS(44, "Kill 50 Bandos Minions", "killed", "bandos minions", 100, 4, 29054, 113126),
	KILL_ZAMORAK_MINIONS(45, "Kill 50 Zamorak Minions", "killed", "zamorak minions", 100, 4, 29055, 113127),
	EAT_FOOD(46, "Heal 5000 hitpoints", "healed", "hitpoints", 5000, 4, 29056, 113128),

	// Hard Achievements
	SLAY_ABYSSAL(47, "Slay 1000 abyssal demons", "slain", "abyssal demons", 1000, 6, 29057, 113129),
	DEFEAT_JAD(48, "Defeat jad 3 times", "defeated", "jads", 3, 6, 29058, 113130),
	DEFEAT_BANDOS(49, "Kill General Graardor 5 times", "killed", "general graardors", 5, 6, 29059, 113131),
	DEFEAT_ARMADYL(50, "Kill Kree'arra 5 times", "killed", "kree'arras", 5, 6, 29060, 113132),
	DEFEAT_SARADOMIN(51, "Kill Commander Zilyana 5 times", "killed", "commander zilyanas", 5, 6, 29061, 113133),
	DEFEAT_ZAMORAK(52, "Kill K'ril Tsutsaroth 5 times", "killed", "k'ril tsutsaroths", 5, 6, 29062, 113134),
	DARK_BEASTS(53, "Slay 1000 dark beasts", "slain", "dark beasts", 1000, 6, 29063, 113135),
	CORPOREAL_BEAST(54, "Kill the corporeal beast 3 times", "killed", "corporeal beasts", 3, 6, 29064, 113136),
	SMITH_SCIMITAR(55, "Smith 5 rune scimitars", "smithed", "scimitars", 5, 6, 29065, 113137),
	KILL_TORMENTED_DEMONS(56, "kill 25 Tormented Demons", "killed", "tormented demons", 25, 6, 29066, 113138),
	CATCH_KINGLY_IMPLING(57, "Catch 5 Kingly Implings", "caught", "kingly implings", 5, 6, 29067, 113139),
	KILL_ARAMDYL_MINIONS(58, "Kill 200 Armadyl Minions", "killed", "armadyl minions", 200, 6, 29068, 113140),
	OPEN_CRYSTAL_CHEST(59, "Open 250 Crystal Chests", "opened", "crystal chests", 250, 6, 29069, 113141),
	PURCHASE_MAX_CAPE(60, "Buy a Maxed Cape", "bought", "maxed cape", 1, 6, 29070, 113142),
	CREATE_RUNE_ARROWS(61, "Craft 100 rune arrows", "crafted", "headless arrows", 100, 6, 29071, 113143),
	CATCH_BABY_IMPLYING(62, "Catch 50 baby implings", "caught", "baby implings", 50, 6, 29072, 113144),
	RUNECRAFT_BLOOD_RUNES(63, "Runecraft 3000 Blood runes", "crafted", "blood runes", 3000, 6, 29073, 113145),
	RUNECRAFT_NATURE_RUNES(64, "Runecraft 3000 Nature runes", "crafted", "nature runes", 3000, 6, 29074, 113146),
	RUNECRAFT_DEATH_RUNES(65, "Runecraft 1500 Death runes", "crafted", "death runes", 1500, 6, 29075, 113147),
	EXCHANGE_ANCIENT_ARTIFACT(66, "Exchange 1 Ancient Statuette", "exchanged", "artifacts", 1, 6, 29076, 113148),
	MINE_ORE(67, "Mine 1000 ore", "mined", "ore", 1000, 6, 29077, 113149),
	CLEAN_HERB(68, "Clean 1000 grimy herbs", "cleaned", "grimy herbs", 1000, 6, 29078, 113150),
	MAKE_POTION(69, "Make 1000 potions", "made", "potions", 1000, 6, 29079, 113151),
	EAT_FOOD_ELITE(70, "Heal 20000 hitpoints", "healed", "hitpoints", 20000, 6, 29080, 113152),

	// Elite Achievements
	KILL_PLAYER_ELITE(71, "Kill 250 players in PvP", "killed", "players", 250, 8, 29081, 113153),
	CORPOREAL_BEAST_ELITE(72, "Kill Corporeal Beast 20 times", "killed", "corporeal beasts", 20, 8, 29082, 113154),
	COMPLETE_SLAYER(73, "Complete 100 slayer tasks", "completed", "slayer tasks", 100, 8, 29083, 113155),
	VOTE_ELITE(74, "Vote 20 times", "voted", "times", 20, 8, 29084, 113156),
	REVENANT_ORK(75, "Kill 100 Revenant Orks", "killed", "revenant orks", 100, 8, 29085, 113157),
	REVENANT_DARK_BEAST(76, "Kill 100 Revenant Dark Beasts", "killed", "revenant dark beast", 100, 8, 29086, 113158),
	REVENANT_DEMON(77, "Kill 175 Revenant Demons", "killed", "revenant demons", 175, 8, 29087, 113159),
	REVENANT_KNIGHT(78, "Kill 200 Revenant Knights", "killed", "revenant knights", 200, 8, 29088, 113160),
	MINE_RUNITE(79, "Mine 500 Runite ore", "mined", "runite ores", 500, 8, 29089, 113161),
	KILL_NOMAD(80, "Kill Nomad 3 times", "killed", "nomads", 3, 8, 29090, 113162),
	OBTAIN_COMPLETIONIST_CAPE(81, "Purchase a Completionist Cape", "bought", "completionist capes", 1, 8, 29091, 113163),
	KILL_MONKEY_GUARD(82, "Kill 500 Monkey Guards", "killed", "monkey guards", 500, 8, 29092, 113164),
	KILL_FROST_DRAGONS(83, "Kill 250 Frost Dragons", "killed", "frost dragons", 250, 8, 29093, 113165),
	KILL_DAG_REX(84, "Kill Dagannoth Rex 5 times", "killed", "dagannoth rexs", 5, 8, 29094, 113166),
	KILL_DAG_PRIME(85, "Kill Dagannoth Prime 5 times", "killed", "dagannoth primes", 5, 8, 29095, 113167),
	KILL_DAG_SUPREME(86, "Kill Dagannoth Supreme 5 times", "killed", "dagannoth supremes", 5, 8, 29096, 113168),
	OBTAIN_WHIP(87, "Get an Abyssal Whip dropExtraordinary", "received", "abyssal whips", 1, 8, 29097, 113169),
	OBTAIN_DARK_BOW(88, "Get a Dark Bow dropExtraordinary", "received", "dark bows", 1, 8, 29098, 113170),
	THIEVE_STALL(89, "Steal from 1000 stalls", "stole", "stalls", 1000, 8, 29099, 113171),
	KILL_CHAOS_ELEMENTAL(90, "Kill the Chaos Elemental 5 times", "killed", "chaos elementals", 5, 8, 29100, 113172),
	DEFEAT_BORK(91, "Defeat Bork 5 times", "killed", "borks", 5, 8, 29101, 113173),
	DEFEAT_KQ(92, "Defeat the KQ 5 times", "killed", "kalphite queens", 5, 8, 29102, 113174),
	DEFEAT_BARRELCHEST(93, "Defeat Barrelchest 5 times", "killed", "barrelchests", 5, 8, 29103, 113175),
	KILL_CAVE_HORRORS(94, "Kill 200 Cave Horrors", "killed", "cave horrors", 200, 8, 29104, 113176),
	GAIN_100_MILLION(95, "Gain 1,000,000,000 XP", "gained", "XP", 1_000_000_000, 8, 29105, 113177),
	OBTAIN_DRAGON_CLAWS(96, "Obtain Dragon Claw dropExtraordinary", "received", "dragon claw drops", 1, 8, 29106, 113178),
	OBTAIN_FIGHT_POINTS(97, "Gain 200 Fight Points", "received", "fight points", 200, 8, 29107, 113179);

	private final int uniqueId;
	private final String description;
	private final String actionType;
	private final String object;
	private final int requiredAmount;
	private final int points;
	private final int lineId;
	private final int actionId;

	Achievement(int uniqueId, String description, String actionType, String object, int requiredAmount, int points, int lineId, int actionId) {
		this.uniqueId = uniqueId;
		this.description = description;
		this.actionType = actionType;
		this.object = object;
		this.requiredAmount = requiredAmount;
		this.points = points;
		this.lineId = lineId;
		this.actionId = actionId;
	}

	public int getActionId() {
		return actionId;
	}

	public String getActionType() {
		return actionType;
	}

	public String getDescription() {
		return description;
	}

	public String getObject() {
		return object;
	}

	public int getPoints() {
		return points;
	}

	public int getRequiredAmount() {
		return requiredAmount;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public int getWidgetId() {
		return lineId;
	}

}
