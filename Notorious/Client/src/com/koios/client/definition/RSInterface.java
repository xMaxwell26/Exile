package com.koios.client.definition;

import com.koios.client.Client;
import com.koios.client.cache.node.MRUNodes;
import com.koios.client.constants.SkillConstants;
import com.koios.client.graphic.Sprite;
import com.koios.client.graphic.TextDrawingArea;
import com.koios.client.stream.Stream;
import com.koios.client.stream.StreamLoader;
import com.koios.client.utils.TextClass;
import com.koios.client.world.Model;

public final class RSInterface {

	private static final int GREY_TEXT = 0xB9B855;
	private static final int ORANGE_TEXT = 0xFF981F;
	private static final int WHITE_TEXT = 0xFFFFFF;
	public static final int BANK_CONTAINER = 50088;

	private static String[] pouchNames = { "Spirit Wolf", "Dreadfowl", "Spirit Spider", "Thorny Snail", "Granite Crab",
			"Spirit Mosquito", "Desert Wyrm", "Spirit Scorpian", "Spirit Tz-Kih", "Albino rat", "Spirit Kalphite",
			"Compost mound", "Giant Chinchompa", "Vampire Bat", "Honey badger", "Beaver", "Void Ravager",
			"Void Spinner", "Void Torcher", "Void Shifter", "Bronze minotaur", "Bull ant", "Macaw", "Evil Turnip",
			"Spirit Cockatrice", "Spirit Guthatrice", "Spirit Saratrice", "Spirit Zamatrice", "Spirit Pengatrice",
			"Spirit Coraxatrice", "Spirit Vulatrice", "Iron minotaur", "Pyrelord", "Magpie", "Bloated Leech",
			"Spirit terrorbird", "Abyssal Parasite", "Spirit Jelly", "Steel Minotaur", "Ibis", "Spirit kyatt",
			"Spirit laurpia", "Spirit graahk", "Karamthulhu overlord", "Smoke Devil", "Abyssal Lurker", "Spirit cobra",
			"Stranger Plant", "Mithril minotaur", "Barker Toad", "War tortoise", "Bunyip", "Fruit bat",
			"Ravenous Locust", "Artic Bear", "Pheonix", "Obsidian Golem", "Granite Lobster", "Praying mantis",
			"Forge regent", "Adamant minotaur", "Talon Beast", "Giant ent", "Fire titan", "Moss titan", "Ice titan",
			"Hydra", "Spirit daggannoth", "Lava titan", "Swamp titan", "Rune minotaur", "Unicorn Stallion",
			"Geyser titan", "Wolpertinger", "Abyssal Titan", "Iron titan", "Pack Yack", "Steel titan" };

	private static String[] scrollNames = { "Howl", "Dreadfowl Strike", "Egg Spawn", "Slime Spray", "Stony Shell",
			"Pester", "Electric Lash", "Venom Shot", "Fireball Assault", "Cheese Feast", "Sandstorm",
			"Generate Compost", "Explode", "Vampire Touch", "Insane Ferocity", "Multichop", "Call of Arms",
			"Call of Arms", "Call of Arms", "Call of Arms", "Bronze Bull Rush", "Unburden", "Herbcall", "Evil Flames",
			"Petrifying gaze", "Petrifying gaze", "Petrifying gaze", "Petrifying gaze", "Petrifying gaze",
			"Petrifying gaze", "Petrifying gaze", "Iron Bull Rush", "Immense Heat", "Thieving Fingers", "Blood Drain",
			"Tireless Run", "Abyssal Drain", "Dissolve", "Steel Bull Rush", "Fish Rain", "Goad", "Ambush", "Rending",
			"Doomsphere Device", "Dust Cloud", "Abyssal Stealth", "Ophidian Incubation", "Poisonous Blast",
			"Mithril Bull Rush", "Toad Bark", "Testudo", "Swallow Whole", "Fruitfall", "Famine", "Arctic Blast",
			"Rise from the Ashes", "Volcanic Strength", "Crushing Claw", "Mantis Strike", "Adamant Bull Rush",
			"Inferno", "Deadly Claw", "Acorn Missile", "Titan's Consitution", "Titan's Consitution",
			"Titan's onsitution", "Regrowth", "Spike Shot", "Ebon Thunder", "Swamp Plague", "Rune Bull Rush",
			"Healing Aura", "Boil", "Magic Focus", "Essence Shipment", "Iron Within", "Winter Storage",
			"Steel of Legends", };

	private static final String[] ACHIEVEMENTS = { "Cut 50 Trees", "Kill 50 Rock Crabs", "Bury 50 Bones",
			"Grow 1 Potato Plant", "Die 5 times", "Burn 50 logs", "Successfully cook 75 food",
			"Kill 2 Dharoks brothers", "Kill 2 Guthans brothers", "Kill 2 Veracs brothers", "Kill 2 Karils brothers",
			"Kill 2 Ahrims brothers", "Kill 2 Torags brothers", "Vote 5 times", "Craft 100 leather hides",
			"Cut 75 gems", "Catch 100 fish", "Pray at the Saradomin Statue", "Pray at the Zamorak Statue",
			"Pray at the Guthix Statue", "Kill 50 Moss Giants", "Complete the Gnome Course", "Recolor an Abyssal Whip",
			"Recolor a Dark Bow", "Perform the skillcape emote", "Cast 250 alchemy spells", "Kill 100 Players in PvP",
			"Win 10 Pest Control Games", "Win 20 rounds of gambling", "Kill 50 Revenant Hellhounds",
			"Catch 150 sharks", "Cut an Onyx", "Pickpocket 125 NPCs", "Win 10 duels", "Kill the King black dragon",
			"Kill 75 Bronze Dragons", "Kill 125 Ice Wolfs", "Fire 1000 Cannon Shots", "Set a Bank PIN",
			"Smith 200 Gold Items", "Cut 100 Magic Trees", "Bury 150 Dragon Bones", "Kill 50 Saradomin Minions",
			"Kill 50 Bandos Minions", "Kill 50 Zamorak Minions", "Heal 5000 hitpoints", "Slay 1000 abyssal demons",
			"Defeat jad 3 times", "Kill General Graardor 5 times", "Kill Kree'arra 5 times",
			"Kill Commander Zilyana 5 times", "Kill K'ril Tsutsaroth 5 times", "Slay 1000 dark beasts",
			"Kill the corporeal beast 3 times", "Smith 5 rune scimitars", "kill 25 Tormented Demons",
			"Catch 5 Kingly Implings", "Kill 200 Armadyl Minions", "Open 250 Crystal Chests", "Buy a Maxed Cape",
			"Craft 100 rune arrows", "Catch 50 baby implings", "Runecraft 3000 Blood runes",
			"Runecraft 3000 Nature runes", "Runecraft 1500 Death runes", "Exchange 1 Ancient Statuette",
			"Mine 1000 ore", "Clean 1000 grimy herbs", "Make 1000 potions", "Heal 20000 hitpoints",
			"Kill 250 players in PvP", "Kill Corporeal Beast 20 times", "Complete 100 slayer tasks", "Vote 20 times",
			"Kill 100 Revenant Orks", "Kill 100 Revenant Dark Beasts", "Kill 175 Revenant Demons",
			"Kill 200 Revenant Knights", "Mine 500 Runite ore", "Kill Nomad 3 times", "Purchase a Completionist Cape",
			"Kill 500 Monkey Guards", "Kill 250 Frost Dragons", "Kill Dagannoth Rex 5 times",
			"Kill Dagannoth Prime 5 times", "Kill Dagannoth Supreme 5 times", "Get an Abyssal Whip drop",
			"Get a Dark Bow drop", "Steal from 1000 stalls", "Kill the Chaos Elemental 5 times", "Defeat Bork 5 times",
			"Defeat the KQ 5 times", "Defeat Barrelchest 5 times", "Kill 200 Cave Horrors", "Gain 1,000,000,000 XP",
			"Obtain Dragon Claw drop", "Gain 200 Fight Points" };

	private static int summoningLevelRequirements[] = { 1, 4, 10, 13, 16, 17, 18, 19, 22, 23, 25, 28, 29, 31, 32, 33,
			34, 34, 34, 34, 36, 40, 41, 42, 43, 43, 43, 43, 43, 43, 43, 46, 46, 47, 49, 52, 54, 55, 56, 56, 57, 57, 57,
			58, 61, 62, 63, 64, 66, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 76, 77, 78, 79, 79, 79, 80, 83, 83, 85,
			86, 88, 89, 92, 93, 95, 96, 99 };

	public RSInterface() {
	}

	public boolean inventoryHover;
	public static StreamLoader aClass44;
	public boolean drawsTransparent;
	public Sprite sprite1;
	public int anInt208;
	public Sprite sprites[];
	public static RSInterface interfaceCache[];
	public int requiredValues[];
	public int contentType;// anInt214
	public int spritesX[];
	public int textColorSelected;
	public int atActionType;
	public String spellName;
	public int anInt219;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public int scrollPosition;
	public String actions[];
	public int instructions[][];
	public boolean aBoolean227;
	public String aString228;
	public int hoverType;
	public int invSpritePadX;
	public int textColor;
	public int anInt233;
	public int mediaID;
	public boolean deletesTargetSlot;
	public int parentID;
	public int spellUsableOn;
	private static MRUNodes aMRUNodes_238;
	public int anInt239;
	public int children[];
	public int childX[];
	public boolean usableItemInterface;
	public TextDrawingArea textDrawingAreas;
	public int invSpritePadY;
	public int valueCompareType[];
	public int anInt246;
	public int spritesY[];
	public String message;
	public boolean isInventoryInterface;
	public int id;
	public int summonReq = 0;
	public int containerSizes[];
	public int container[];
	public byte opacity;
	private int anInt255;
	private int anInt256;
	public int anInt257;
	public int anInt258;
	public boolean allowSwapItems;
	public Sprite sprite2;
	public int scrollMax;
	public int type;
	public int xOffset;
	private static final MRUNodes aMRUNodes_264 = new MRUNodes(30);
	public int yOffset;
	public boolean isMouseoverTriggered;
	public int height;
	public boolean textShadow;
	public int modelZoom;
	public int modelRotation1;
	public int modelRotation2;
	public int childY[];
	public int textAlign;

	public void swapInventoryItems(int i, int j) {
		int k = container[i];
		container[i] = container[j];
		container[j] = k;
		k = containerSizes[i];
		containerSizes[i] = containerSizes[j];
		containerSizes[j] = k;
	}

	private static TextDrawingArea[] fonts;

	public static void unpack(StreamLoader streamLoader, TextDrawingArea textDrawingAreas[], StreamLoader streamLoader_1) {
		aMRUNodes_238 = new MRUNodes(50000);
		fonts = textDrawingAreas;
		Stream stream = new Stream(streamLoader.getDataForName("data"));
		int i = -1;
		stream.readUnsignedShort();
		interfaceCache = new RSInterface[65000];
		while (stream.currentOffset < stream.buffer.length) {
			int k = stream.readUnsignedShort();
			if (k == 65535) {
				i = stream.readUnsignedShort();
				k = stream.readUnsignedShort();
			}
			RSInterface rsInterface = interfaceCache[k] = new RSInterface();
			rsInterface.id = k;
			rsInterface.parentID = i;
			rsInterface.type = stream.readUnsignedByte();
			rsInterface.atActionType = stream.readUnsignedByte();
			rsInterface.contentType = stream.readUnsignedShort();
			rsInterface.width = stream.readUnsignedShort();
			rsInterface.height = stream.readUnsignedShort();
			rsInterface.opacity = (byte) stream.readUnsignedByte();
			rsInterface.hoverType = stream.readUnsignedByte();
			if (rsInterface.hoverType != 0)
				rsInterface.hoverType = (rsInterface.hoverType - 1 << 8) + stream.readUnsignedByte();
			else
				rsInterface.hoverType = -1;
			int i1 = stream.readUnsignedByte();
			if (i1 > 0) {
				rsInterface.valueCompareType = new int[i1];
				rsInterface.requiredValues = new int[i1];
				for (int j1 = 0; j1 < i1; j1++) {
					rsInterface.valueCompareType[j1] = stream.readUnsignedByte();
					rsInterface.requiredValues[j1] = stream.readUnsignedShort();
				}

			}
			int k1 = stream.readUnsignedByte();
			if (k1 > 0) {
				rsInterface.instructions = new int[k1][];
				for (int l1 = 0; l1 < k1; l1++) {
					int i3 = stream.readUnsignedShort();
					rsInterface.instructions[l1] = new int[i3];
					for (int l4 = 0; l4 < i3; l4++)
						rsInterface.instructions[l1][l4] = stream.readUnsignedShort();

				}

			}
			if (rsInterface.type == 0) {
				rsInterface.drawsTransparent = false;
				rsInterface.scrollMax = stream.readUnsignedShort();
				rsInterface.isMouseoverTriggered = stream.readUnsignedByte() == 1;
				int i2 = stream.readUnsignedShort();
				rsInterface.children = new int[i2];
				rsInterface.childX = new int[i2];
				rsInterface.childY = new int[i2];
				for (int j3 = 0; j3 < i2; j3++) {
					rsInterface.children[j3] = stream.readUnsignedShort();
					rsInterface.childX[j3] = stream.readSignedWord();
					rsInterface.childY[j3] = stream.readSignedWord();
				}
			}
			if (rsInterface.type == 1) {
				stream.readUnsignedShort();
				stream.readUnsignedByte();
			}
			if (rsInterface.type == 2) {
				rsInterface.container = new int[rsInterface.width * rsInterface.height];
				rsInterface.containerSizes = new int[rsInterface.width * rsInterface.height];
				rsInterface.allowSwapItems = stream.readUnsignedByte() == 1;
				rsInterface.isInventoryInterface = stream.readUnsignedByte() == 1;
				rsInterface.usableItemInterface = stream.readUnsignedByte() == 1;
				rsInterface.deletesTargetSlot = stream.readUnsignedByte() == 1;
				rsInterface.invSpritePadX = stream.readUnsignedByte();
				rsInterface.invSpritePadY = stream.readUnsignedByte();
				rsInterface.spritesX = new int[20];
				rsInterface.spritesY = new int[20];
				rsInterface.sprites = new Sprite[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = stream.readUnsignedByte();
					if (k3 == 1) {
						rsInterface.spritesX[j2] = stream.readSignedWord();
						rsInterface.spritesY[j2] = stream.readSignedWord();
						String s1 = stream.readString();
						if (streamLoader_1 != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");
							rsInterface.sprites[j2] = method207(Integer.parseInt(s1.substring(i5 + 1)), streamLoader_1, s1.substring(0, i5));
						}
					}
				}
				rsInterface.actions = new String[5];
				for (int l3 = 0; l3 < 5; l3++) {
					rsInterface.actions[l3] = stream.readString();
					if (rsInterface.actions[l3].length() == 0)
						rsInterface.actions[l3] = null;
					if (rsInterface.parentID == 3822) {
						rsInterface.actions[4] = "Sell 50";
					}

					if (rsInterface.parentID == 3824) {
						rsInterface.actions[4] = "Buy 100";
					}
					if (rsInterface.parentID == 1644)
						rsInterface.actions[2] = "Operate";
				}
			}
			if (rsInterface.type == 3)
				rsInterface.aBoolean227 = stream.readUnsignedByte() == 1;
			if (rsInterface.type == 4 || rsInterface.type == 1) {
				rsInterface.textAlign = ((stream.readUnsignedByte() == 1) ? 1 : 0);
				int k2 = stream.readUnsignedByte();
				if (textDrawingAreas != null)
					rsInterface.textDrawingAreas = textDrawingAreas[k2];
				rsInterface.textShadow = stream.readUnsignedByte() == 1;
			}
			if (rsInterface.type == 4) {
				rsInterface.message = stream.readString().replaceAll("RuneScape", "RuneScape");
				rsInterface.aString228 = stream.readString();
			}
			if (rsInterface.type == 1 || rsInterface.type == 3 || rsInterface.type == 4)
				rsInterface.textColor = stream.readInt();
			if (rsInterface.type == 3 || rsInterface.type == 4) {
				rsInterface.anInt219 = stream.readInt();
				rsInterface.textColorSelected = stream.readInt();
				rsInterface.anInt239 = stream.readInt();
			}
			if (rsInterface.type == 5) {
				rsInterface.drawsTransparent = false;
				String s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int i4 = s.lastIndexOf(",");
					rsInterface.sprite1 = method207(Integer.parseInt(s.substring(i4 + 1)), streamLoader_1, s.substring(0, i4));
				}
				s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int j4 = s.lastIndexOf(",");
					rsInterface.sprite2 = method207(Integer.parseInt(s.substring(j4 + 1)), streamLoader_1, s.substring(0, j4));
				}
			}
			if (rsInterface.type == 6) {
				int l = stream.readUnsignedByte();
				if (l != 0) {
					rsInterface.anInt233 = 1;
					rsInterface.mediaID = (l - 1 << 8) + stream.readUnsignedByte();
				}
				l = stream.readUnsignedByte();
				if (l != 0) {
					rsInterface.anInt255 = 1;
					rsInterface.anInt256 = (l - 1 << 8) + stream.readUnsignedByte();
				}
				l = stream.readUnsignedByte();
				if (l != 0)
					rsInterface.anInt257 = (l - 1 << 8) + stream.readUnsignedByte();
				else
					rsInterface.anInt257 = -1;
				l = stream.readUnsignedByte();
				if (l != 0)
					rsInterface.anInt258 = (l - 1 << 8) + stream.readUnsignedByte();
				else
					rsInterface.anInt258 = -1;
				rsInterface.modelZoom = stream.readUnsignedShort();
				rsInterface.modelRotation1 = stream.readUnsignedShort();
				rsInterface.modelRotation2 = stream.readUnsignedShort();
			}
			if (rsInterface.type == 7) {
				rsInterface.container = new int[rsInterface.width * rsInterface.height];
				rsInterface.containerSizes = new int[rsInterface.width * rsInterface.height];
				rsInterface.textAlign = ((stream.readUnsignedByte() == 1) ? 1 : 0);
				int l2 = stream.readUnsignedByte();
				if (textDrawingAreas != null)
					rsInterface.textDrawingAreas = textDrawingAreas[l2];
				rsInterface.textShadow = stream.readUnsignedByte() == 1;
				rsInterface.textColor = stream.readInt();
				rsInterface.invSpritePadX = stream.readSignedWord();
				rsInterface.invSpritePadY = stream.readSignedWord();
				rsInterface.isInventoryInterface = stream.readUnsignedByte() == 1;
				rsInterface.actions = new String[5];
				for (int k4 = 0; k4 < 5; k4++) {
					rsInterface.actions[k4] = stream.readString();
					if (rsInterface.actions[k4].length() == 0)
						rsInterface.actions[k4] = null;
				}

			}
			if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
				rsInterface.selectedActionName = stream.readString();
				rsInterface.spellName = stream.readString();
				rsInterface.spellUsableOn = stream.readUnsignedShort();
			}

			if (rsInterface.type == 8)
				rsInterface.message = stream.readString();

			if (rsInterface.atActionType == 1 || rsInterface.atActionType == 4 || rsInterface.atActionType == 5 || rsInterface.atActionType == 6) {
				rsInterface.tooltip = stream.readString();
				if (rsInterface.tooltip.length() == 0) {
					if (rsInterface.atActionType == 1)
						rsInterface.tooltip = "Ok";
					if (rsInterface.atActionType == 4)
						rsInterface.tooltip = "Select";
					if (rsInterface.atActionType == 5)
						rsInterface.tooltip = "Select";
					if (rsInterface.atActionType == 6)
						rsInterface.tooltip = "Continue";
				}
			}
		}
		aClass44 = streamLoader;
		//optionTab1(textDrawingAreas);
		constructLunar();
		Bank(textDrawingAreas);
		addGodwarsKillCounter(textDrawingAreas);
		summoningTab(textDrawingAreas);
		petSummoningTab(textDrawingAreas);
		appendAchievementDiaryTab(textDrawingAreas);
		questTab(textDrawingAreas);
		clanChatTab(textDrawingAreas);
		editClan(textDrawingAreas);
		// Sidebar0(textDrawingAreas);
		equipmentScreen(textDrawingAreas);
		equipmentTab(textDrawingAreas);
		configureLunar(textDrawingAreas);
		quickCurses(textDrawingAreas);
		makePestControlBoatPanel(textDrawingAreas);
		makePestcontrolGamePanel(textDrawingAreas);
		quickPrayers(textDrawingAreas);
		edgevilleHomeTeleport(textDrawingAreas);
		duelInterface(textDrawingAreas);
		itemsOnDeathDATA(textDrawingAreas);
		itemsOnDeath(textDrawingAreas);
		buildTradeScreen(textDrawingAreas);
		Skills(textDrawingAreas);
		skilllevel(textDrawingAreas);
		barrowText(textDrawingAreas);
		Shop(textDrawingAreas);
		bobInterface(textDrawingAreas);
		bountyHunter(textDrawingAreas);
		pouches(textDrawingAreas);
		scrolls(textDrawingAreas);
		aMRUNodes_238 = null;
	}

	public static void edgevilleHomeTeleport(TextDrawingArea[] TDA) {
		RSInterface rsi = interfaceCache[21741];
		rsi.atActionType = 1;
		rsi.tooltip = "Cast @gre@Edgeville Home Teleport";
	}

	public static void addButton(int id, int sid, String spriteName, String tooltip) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(sid, spriteName);
		tab.sprite2 = imageLoader(sid, spriteName);
		tab.width = tab.sprite1.myWidth;
		tab.height = tab.sprite2.myHeight;
		tab.tooltip = tooltip;
	}

	public static void addTooltip(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.id = id;
		rsi.type = 0;
		rsi.drawsTransparent = true;
		rsi.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsi.totalChildren(1);
		rsi.child(0, id + 1, 0, 0);
	}

	public static void addTooltipBox(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.id = id;
		rsi.parentID = id;
		rsi.type = 9;
		rsi.message = text;
	}

	private static Sprite CustomSpriteLoader(int id, String s) {
		long l = (TextClass.method585(s) << 8) + (long) id;
		Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
		if (sprite != null) {
			return sprite;
		}
		try {
			sprite = new Sprite("/Attack/" + id + s);
			aMRUNodes_238.removeFromCache(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public static RSInterface addInterface(int id) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.id = id;
		rsi.parentID = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.message = text;
		rsi.textColor = color;
		rsi.id = id;
		rsi.type = 4;
	}

	public static void textColor(int id, int color) {
		RSInterface rsi = interfaceCache[id];
		rsi.textColor = color;
	}

	public static void textSize(int id, TextDrawingArea tda[], int idx) {
		RSInterface rsi = interfaceCache[id];
		rsi.textDrawingAreas = tda[idx];
	}

	public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.sprite1 = method207(sprite1, aClass44, sprites);
		rsi.sprite2 = method207(sprite2, aClass44, sprites);
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 5;
	}

	public static void sprite1(int id, int sprite) {
		RSInterface class9 = interfaceCache[id];
		class9.sprite1 = CustomSpriteLoader(sprite, "");
	}

	public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.sprite1 = CustomSpriteLoader(sprite, "");
		if (sprite2 == sprite)
			rsi.sprite2 = CustomSpriteLoader(sprite, "a");
		else
			rsi.sprite2 = CustomSpriteLoader(sprite2, "");
		rsi.tooltip = s;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = width;
		rsi.hoverType = 52;
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
	}

	public static void addToggleButton(int id, int sprite, int setconfig, int width, int height, String s) {
		RSInterface rsi = addInterface(id);
		rsi.sprite1 = CustomSpriteLoader(sprite, "");
		rsi.sprite2 = CustomSpriteLoader(sprite, "a");
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.instructions = new int[1][3];
		rsi.instructions[0][0] = 5;
		rsi.instructions[0][1] = setconfig;
		rsi.instructions[0][2] = 0;
		rsi.atActionType = 4;
		rsi.width = width;
		rsi.hoverType = -1;
		rsi.parentID = id;
		rsi.id = id;
		rsi.type = 5;
		rsi.height = height;
		rsi.tooltip = s;
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public static void removeSomething(int id) {
		interfaceCache[id] = new RSInterface();
	}

	public static void bountyHunter(TextDrawingArea[] TDA) {
		RSInterface tab = addTabInterface(23300);

		addAdvancedSprite(23301, 161);
		addAdvancedSprite(23302, 168);
		addConfigSprite(23303, 162, -1, 1, 876);
		addSprite(23304, 169);
		addText(23305, "---", TDA, 0, 0xffff00, 3, true);
		addText(23306, "Target:", TDA, 0, 0xffff00, 3, true);
		addText(23307, "None", TDA, 1, 0xffffff, 3, true);
		addText(23308, "Level: ------", TDA, 0, 0xffff00, 3, true);
		addText(23309, "Current  Record", TDA, 0, 0xffff00, 3, true);
		addText(23310, "0", TDA, 0, 0xffff00, 3, true);
		addText(23311, "0", TDA, 0, 0xffff00, 3, true);
		addText(23312, "0", TDA, 0, 0xffff00, 3, true);
		addText(23313, "0", TDA, 0, 0xffff00, 3, true);
		addText(23314, "Rogue:", TDA, 0, 0xffff00, 3, true);
		addText(23315, "Hunter:", TDA, 0, 0xffff00, 3, true);
		addConfigSprite(23316, -1, 163, 1, 877);
		addConfigSprite(23317, -1, 164, 1, 878);
		addConfigSprite(23318, -1, 165, 1, 879);
		addConfigSprite(23319, -1, 166, 1, 880);
		addConfigSprite(23320, -1, 167, 1, 881);
		tab.totalChildren(21);
		tab.child(0, 23301, 319, 8);
		tab.child(1, 23302, 339, 56);
		tab.child(2, 23303, 345, 18);
		tab.child(3, 23304, 348, 73);
		tab.child(4, 23305, 358, 41);
		tab.child(5, 23306, 455, 12);
		tab.child(6, 23307, 456, 25);
		tab.child(7, 23308, 457, 41);
		tab.child(8, 23309, 460, 59);
		tab.child(9, 23310, 438, 72);
		tab.child(10, 23311, 481, 72);
		tab.child(11, 23312, 438, 85);
		tab.child(12, 23313, 481, 85);
		tab.child(13, 23314, 393, 72);
		tab.child(14, 23315, 394, 85);
		tab.child(15, 23316, 345, 18);
		tab.child(16, 23317, 345, 18);
		tab.child(17, 23318, 345, 18);
		tab.child(18, 23319, 345, 18);
		tab.child(19, 23320, 345, 18);
		tab.child(20, 197, 2, 2);
	}

	public static void Shop(TextDrawingArea[] tda) {
		RSInterface widget = addTabInterface(3824);
		setChildren(6, widget);
		addSprite(3825, Client.cacheSprite[156]);
		addHoverButton(3902, 91, 21, 21, "Close", 250, 3826, 3);
		addHoveredButton(3826, 92, 21, 21, 3827);
		addText(3903, "Right-click on an item in the shop or your inventory to see the available options.", 0xE4A146, 1, true, 52, 0);
		setBounds(3825, 6, 8, 0, widget);// Shop interface
		setBounds(3826, 478, 10, 2, widget);// Close Button
		setBounds(3900, 60, 50, 3, widget);// Item models
		setBounds(3901, 230, 11, 4, widget);// Store name
		setBounds(3902, 478, 10, 1, widget);// Close button
		setBounds(3903, 260, 302, 5, widget);// Available Options Text
		widget = interfaceCache[3900];
		widget.invSpritePadX = 8;
		widget.width = 10;
		widget.height = 4;
		widget.invSpritePadY = 19;
	}

	public static void pouches(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addInterface(52700);
		addSprite(52701, 326);
		addHover(52702, 3, 0, 52703, 485, 17, 17, "Close Window");
		addHovered(52703, 486, 17, 17, 52704);
		addButton(52705, 487, "Pouches", 27640, 1, 116, 20);
		addButton(52706, 488, "Scrolls", 27640, 1, 116, 20);
		addText(52707, "Summoning Pouch Creation", TDA, 2, 0xff981f, false, true);

		RSInterface scroll = addTabInterface(52710);
		scroll.width = 430;
		scroll.height = 230;
		scroll.scrollMax = 550;
		// scroll.newScroller = true;
		int lastId = 52710;
		int lastImage = 170;
		for (int i = 0; i < 78; i++) {
			addPouches(lastId + 1, lastImage, lastImage + 1, i);
			lastId += 5;
			lastImage += 2;
		}

		rsinterface.children = new int[7];
		rsinterface.childX = new int[7];
		rsinterface.childY = new int[7];

		rsinterface.children[0] = 52701;
		rsinterface.childX[0] = 14;
		rsinterface.childY[0] = 17;
		rsinterface.children[1] = 52702;
		rsinterface.childX[1] = 475;
		rsinterface.childY[1] = 30;
		rsinterface.children[2] = 52703;
		rsinterface.childX[2] = 475;
		rsinterface.childY[2] = 30;
		rsinterface.children[3] = 52705;
		rsinterface.childX[3] = 25;
		rsinterface.childY[3] = 30;
		rsinterface.children[4] = 52706;
		rsinterface.childX[4] = 128;
		rsinterface.childY[4] = 30;
		rsinterface.children[5] = 52707;
		rsinterface.childX[5] = 268;
		rsinterface.childY[5] = 30;
		rsinterface.children[6] = 52710;
		rsinterface.childX[6] = 35;
		rsinterface.childY[6] = 65;

		scroll.children = new int[78];
		scroll.childX = new int[78];
		scroll.childY = new int[78];

		int lastNumber = -1;
		int lastId2 = 52710;
		int lastX = -52;
		int lastY = 0;
		for (int i = 0; i < 78; i++) {
			scroll.children[lastNumber += 1] = lastId2 += 1;
			scroll.childX[lastNumber] = lastX += 52;
			scroll.childY[lastNumber] = lastY;
			lastId2 += 4;
			if (lastX == 364) {
				lastX = -52;
				lastY += 55;
			}
		}
	}

	public static void scrolls(TextDrawingArea[] TDA) {
		RSInterface rsinterface = addInterface(51700);

		addButton(51701, 327, "Pouches", 27640, 1, 116, 20);
		addButton(51702, 328, "Scrolls", 27640, 1, 116, 20);

		RSInterface scroll = addTabInterface(51710);
		scroll.width = 430;
		scroll.height = 230;
		scroll.scrollMax = 550;
		// scroll.newScroller = true;
		int lastId = 51710;
		int lastImage = 329;

		for (int i = 0; i < 78; i++) {
			addScrolls(lastId + 1, lastImage, lastImage + 1, i);
			lastId += 5;
			lastImage += 2;
		}

		rsinterface.children = new int[7];
		rsinterface.childX = new int[7];
		rsinterface.childY = new int[7];

		rsinterface.children[0] = 52701;
		rsinterface.childX[0] = 14;
		rsinterface.childY[0] = 17;
		rsinterface.children[1] = 52702;
		rsinterface.childX[1] = 475;
		rsinterface.childY[1] = 30;
		rsinterface.children[2] = 52703;
		rsinterface.childX[2] = 475;
		rsinterface.childY[2] = 30;
		rsinterface.children[3] = 51701;
		rsinterface.childX[3] = 25;
		rsinterface.childY[3] = 30;
		rsinterface.children[4] = 51702;
		rsinterface.childX[4] = 128;
		rsinterface.childY[4] = 30;
		rsinterface.children[5] = 52707;
		rsinterface.childX[5] = 268;
		rsinterface.childY[5] = 30;
		rsinterface.children[6] = 51710;
		rsinterface.childX[6] = 35;
		rsinterface.childY[6] = 65;

		scroll.children = new int[78];
		scroll.childX = new int[78];
		scroll.childY = new int[78];

		int lastNumber = -1;
		int lastId2 = 51710;
		int lastX = -52;
		int lastY = 0;
		for (int i = 0; i < 78; i++) {
			scroll.children[lastNumber += 1] = lastId2 += 1;
			scroll.childX[lastNumber] = lastX += 52;
			scroll.childY[lastNumber] = lastY;
			lastId2 += 4;
			if (lastX == 364) {
				lastX = -52;
				lastY += 55;
			}
		}
	}

	public static void bobInterface(TextDrawingArea[] tda) {
		RSInterface widget = addTabInterface(2700);
		addSprite(2701, Client.cacheSprite[160]);
		addBobStorage(2702);
		addHoverButton(2703, 91, 21, 21, "Close", 250, 2704, 3);
		addHoveredButton(2704, 92, 21, 21, 2705);
		widget.totalChildren(4);
		widget.child(0, 2701, 90, 14);
		widget.child(1, 2702, 106, 57);
		widget.child(2, 2703, 431, 23);
		widget.child(3, 2704, 431, 23);
	}
	
	private static void summoningTab(TextDrawingArea[] tda) {
		RSInterface widget = addTabInterface(18017);
		addButton(18018, 85, "Cast special");
		addText(18019, "S P E C I A L  M O V E", tda, 0, WHITE_TEXT, false, false);
		addSprite(18020, Client.cacheSprite[66]);
		addFamiliarHead(18021, 75, 50, 875);
		addSprite(18022, Client.cacheSprite[67]);
		addConfigButton(18023, 18017, 69, 68, 30, 31, "Cast special", 0, 1, 330);
		addText(18024, "0", tda, 0, ORANGE_TEXT, true, true);
		addSprite(18025, Client.cacheSprite[70]);
		addFamiliarAttack(18026, 18017, 72, 71, 29, 39, "Attack", 0, 2, 333);
		addSprite(18027, Client.cacheSprite[73]);
		addText(18028, "18028", tda, 2, ORANGE_TEXT, true, false);
		addHoverButton(18029, 74, 38, 38, "Withdraw BoB", -1, 18030, 1);
		addHoveredButton(18030, 75, 38, 38, 18031);
		addHoverButton(18032, 76, 38, 38, "Renew familiar", -1, 18033, 1);
		addHoveredButton(18033, 77, 38, 38, 18034);
		addHoverButton(18035, 78, 38, 38, "Call follower", -1, 18036, 1);
		addHoveredButton(18036, 79, 38, 38, 18037);
		addHoverButton(18038, 80, 38, 38, "Dismiss familiar", -1, 18039, 1);
		addHoveredButton(18039, 81, 38, 38, 18040);
		addFamiliarHead2(23027, 50, 50, 800);
		addSprite(18041, Client.cacheSprite[82]);
		addSprite(18042, Client.cacheSprite[83]);
		addText(18043, "18043", tda, 0, GREY_TEXT, false, true);
		addSprite(18044, Client.cacheSprite[84]);
		addText(18045, "18045", tda, 0, GREY_TEXT, false, true);
		setChildren(25, widget);
		setBounds(18018, 23, 10, 0, widget);
		setBounds(18019, 43, 12, 1, widget);
		setBounds(18020, 10, 32, 2, widget);
		setBounds(18021, 63, 60, 3, widget);
		setBounds(18022, 11, 32, 4, widget);
		setBounds(18023, 14, 35, 5, widget);
		setBounds(18024, 25, 69, 6, widget);
		setBounds(18025, 138, 32, 7, widget);
		setBounds(18026, 143, 36, 8, widget);
		setBounds(18027, 12, 144, 9, widget);
		setBounds(18028, 67, 146, 10, widget);
		setBounds(18029, 23, 168, 11, widget);
		setBounds(18030, 23, 168, 12, widget);
		setBounds(18032, 75, 168, 13, widget);
		setBounds(18033, 75, 168, 14, widget);
		setBounds(18035, 23, 213, 15, widget);
		setBounds(18036, 23, 213, 16, widget);
		setBounds(18038, 75, 213, 17, widget);
		setBounds(18039, 75, 213, 18, widget);
		setBounds(18041, 130, 168, 19, widget);
		setBounds(18042, 153, 170, 20, widget);
		setBounds(18043, 148, 198, 21, widget);
		setBounds(18044, 149, 213, 22, widget);
		setBounds(18045, 145, 241, 23, widget);
		setBounds(23027, 75, 55, 24, widget);
	}
	
	private static void petSummoningTab(TextDrawingArea[] tda)
	{
		RSInterface widget = addTabInterface(19017);
		addSprite(19018, Client.cacheSprite[66]);
		addFamiliarHead(19019, 75, 50, 900);
		addSprite(19020, Client.cacheSprite[73]);
		addText(19021, "None", tda, 2, ORANGE_TEXT, true, false);
		addHoverButton(19022, 78, 38, 38, "Call follower", -1, 19023, 1);
		addHoveredButton(19023, 79, 38, 38, 19024);
		addHoverButton(19025, 80, 38, 38, "Dismiss familiar", -1, 19026, 1);
		addHoveredButton(19026, 81, 38, 38, 19027);
		addSprite(19028, Client.cacheSprite[82]);
		addSprite(19029, Client.cacheSprite[555]);
		addText(19030, "0%", tda, 0, GREY_TEXT, false, true);
		addSprite(19031, Client.cacheSprite[556]);
		addText(19032, "0%", tda, 0, WHITE_TEXT, false, true);
		setChildren(13, widget);
		setBounds(19018, 10, 32, 0, widget);
		setBounds(19019, 65, 65, 1, widget);
		setBounds(19020, 12, 145, 2, widget);
		setBounds(19021, 76, 147, 3, widget);
		setBounds(19022, 23, 213, 4, widget);
		setBounds(19023, 23, 213, 5, widget);
		setBounds(19025, 75, 213, 6, widget);
		setBounds(19026, 75, 213, 7, widget);
		setBounds(19028, 130, 168, 8, widget);
		setBounds(19029, 148, 170, 9, widget);
		setBounds(19030, 152, 198, 10, widget);
		setBounds(19031, 149, 220, 11, widget);
		setBounds(19032, 155, 241, 12, widget);
	}

	public static void equipmentScreen(TextDrawingArea[] wid) {
		RSInterface tab = addTabInterface(15106);
		addSprite(15107, 510);
		addHoverButton(15210, 91, 21, 21, "Close", 250, 15211, 3);
		addHoveredButton(15211, 92, 21, 21, 10602);
		addText(15111, "", wid, 2, 0xe4a146, 0, true);
		addText(15112, "Attack bonuses", wid, 2, 0xFF8900, 0, true);
		addText(15113, "Defence bonuses", wid, 2, 0xFF8900, 0, true);
		addText(15114, "Other bonuses", wid, 2, 0xFF8900, 0, true);
		addText(29148, "Summoning: +0", wid, 1, 0xFF8900, 0, true);
		addText(29149, "Absorb Melee: +0%", wid, 1, 0xFF9200, 0, true);
		addText(29150, "Absorb Magic: +0%", wid, 1, 0xFF9200, 0, true);
		addText(29151, "Absorb Ranged: +0%", wid, 1, 0xFF9200, 0, true);
		addText(29152, "Ranged Strength: 0", wid, 1, 0xFF9200, 0, true);
		addText(29153, "Magic Damage: +0%", wid, 1, 0xFF9200, 0, true);

		for (int i = 1675; i <= 1684; i++) {
			textSize(i, wid, 1);
		}

		textSize(1686, wid, 1);
		textSize(1687, wid, 1);
		addChar(15125);
		tab.totalChildren(51);
		tab.child(0, 15107, 15, 5);
		tab.child(1, 15210, 476, 8);
		tab.child(2, 15211, 476, 8);
		tab.child(3, 15111, 14, 30);
		int Child = 4;
		int Y = 45;
		tab.child(16, 15112, 24, 30);

		for (int i = 1675; i <= 1679; i++) {
			tab.child(Child, i, 29, Y - 3);
			Child++;
			Y += 14;
		}

		int edit = 10;
		tab.child(18, 15113, 24, 122 - edit); // 147
		tab.child(9, 1680, 29, 137 - edit - 2); // 161
		tab.child(10, 1681, 29, 153 - edit - 3);
		tab.child(11, 1682, 29, 168 - edit - 3);
		tab.child(12, 1683, 29, 183 - edit - 3);
		tab.child(13, 1684, 29, 197 - edit - 3);
		tab.child(44, 29148, 29, 211 - edit - 3);
		tab.child(45, 29149, 29, 225 - edit - 3);
		tab.child(46, 29150, 29, 239 - edit - 3);
		tab.child(47, 29151, 29, 253 - edit - 3);

		/* bottom */
		int edit2 = 30, edit3 = 2;
		tab.child(19, 15114, 24, 223 + edit2);
		tab.child(14, 1686, 29, ((262 - 24) + edit2) - edit3);
		tab.child(17, 29152, 29, ((276 - 24) + edit2) - edit3);
		tab.child(48, 1687, 29, ((290 - 24) + edit2) - edit3);
		tab.child(49, 29153, 29, ((304 - 24) + edit2) - edit3);
		tab.child(15, 15125, 170, 200);
		tab.child(20, 1645, 104 + 295, 149 - 52);
		tab.child(21, 1646, 399, 163);
		tab.child(22, 1647, 399, 163);
		tab.child(23, 1648, 399, 58 + 146);
		tab.child(24, 1649, (26 + 22 + 297) - 2, (((110 - 44) + 118) - 13) + 5);
		tab.child(25, 1650, 321 + 22, 58 + 154);
		tab.child(26, 1651, 321 + 134, 58 + 118);
		tab.child(27, 1652, 321 + 134, 58 + 154);
		tab.child(28, 1653, 321 + 48, 58 + 81);
		tab.child(29, 1654, 321 + 107, 58 + 81);
		tab.child(30, 1655, 321 + 58, 58 + 42);
		tab.child(31, 1656, 321 + 112, 58 + 41);
		tab.child(32, 1657, 321 + 78, 58 + 4);
		tab.child(33, 1658, 321 + 37, 58 + 43);
		tab.child(34, 1659, 321 + 78, 58 + 43);
		tab.child(35, 1660, 321 + 119, 58 + 43);
		tab.child(36, 1661, 321 + 22, 58 + 82);
		tab.child(37, 1662, 321 + 78, 58 + 82);
		tab.child(38, 1663, 321 + 134, 58 + 82);
		tab.child(39, 1664, 321 + 78, 58 + 122);
		tab.child(40, 1665, 321 + 78, 58 + 162);
		tab.child(41, 1666, 321 + 22, 58 + 162);
		tab.child(42, 1667, 321 + 134, 58 + 162);
		tab.child(43, 1688, (50 + 297) - 2, (110 - 13) + 5);

		for (int i = 1675; i <= 1684; i++) {
			RSInterface rsi = interfaceCache[i];
			rsi.textColor = 0xFF9200;
		}

		for (int i = 1686; i <= 1687; i++) {
			RSInterface rsi = interfaceCache[i];
			rsi.textColor = 0xFF9200;
		}

		int mainId = 50097;
		RSInterface component = addInterface(mainId);
		component.totalChildren(2);
		addHoverButton(mainId + 1, 541, 34, 37, "Back To Bank", 0, mainId + 2, 1);
		addHoveredButton(mainId + 2, 542, 34, 37, mainId + 3);
		component.child(0, mainId + 1, 0, 0);
		component.child(1, mainId + 2, 0, 0);
		component.width = 34;
		component.height = 37;
		component.isMouseoverTriggered = true;
		tab.child(50, mainId, 454, 30);
	}

	public static void barrowText(TextDrawingArea[] tda) {
		RSInterface tab = addInterface(16110);
		addText(16111, "Brothers slain:", tda, 1, 0xCDC72F, true, true);
		addText(16112, "Ahrim", tda, 1, 0x7D7B4F, true, true);
		addText(16113, "Dharok", tda, 1, 0x7D7B4F, true, true);
		addText(16114, "Guthan", tda, 1, 0x7D7B4F, true, true);
		addText(16115, "Karil", tda, 1, 0x7D7B4F, true, true);
		addText(16116, "Torag", tda, 1, 0x7D7B4F, true, true);
		addText(16117, "Verac", tda, 1, 0x7D7B4F, true, true);
		addText(16118, "Creatures slain:", tda, 1, 0xCDC72F, true, true);
		addText(16119, "#", tda, 1, 0x7D7B4F, true, true);
		tab.totalChildren(9);
		tab.child(0, 16111, 352 + 68, 225 - 23);
		tab.child(1, 16112, 428 + 29 + 14, 240 - 23);
		tab.child(2, 16113, 428 + 35, 255 - 26);
		tab.child(3, 16114, 428 + 34, 270 - 29);
		tab.child(4, 16115, 428 + 36 + 14, 285 - 32);
		tab.child(5, 16116, 428 + 26 + 18, 300 - 35);
		tab.child(6, 16117, 428 + 43, 315 - 38);
		tab.child(7, 16118, 413, (326 - 38) + 7);
		tab.child(8, 16119, 490, (326 - 38) + 22);
	}
	
	public static void prepareAtLogin() {

		// Total level in skills tab
		interfaceCache[3984].setText("Total level: %1");
		interfaceCache[3984].instructions = new int[][] { { 9, 0 } };

		// Prayer tab current/max
		interfaceCache[687].setText("%1/%2");
		interfaceCache[687].instructions = new int[][] { { 1, 5, 0 }, { 2, 5, 0 } };

		// Hitpoints skill box
		interfaceCache[4016].instructions = new int[][] { { 1, 3, 0 } };
		interfaceCache[4017].instructions = new int[][] { { 2, 3, 0 } };

		interfaceCache[4439].message = "Darude - Sandstorm";


		buildStatInterface();
	}
	
	private static void buildStatInterface() {
		int[] curIds = new int[] { 4004, 4008, 4006, 4016, 4010, 4012, 4014, 4034, 4038, 4026, 4032, 4036, 4024, 4030,
				4028, 4020, 4018, 4022, 12166, 13926, 4152, 60204, 60206, 60208 };
		
		int[] maxIds = new int[] { 4005, 4009, 4007, 4017, 4011, 4013, 4015, 4035, 4039, 4027, 4033, 4037, 4025, 4031,
				4029, 4021, 4019, 4023, 12167, 13927, 4153, 60205, 60207, 60209 };

		for (int i = 0; i < SkillConstants.MORE_DETAILS_PANEL_ID.length; i++) {
			if (interfaceCache[SkillConstants.MORE_DETAILS_PANEL_ID[i]] == null) {
				continue;
			}

			interfaceCache[SkillConstants.MORE_DETAILS_PANEL_ID[i]].setText(SkillConstants.SKILL_NAMES_ORDERED[i] + " Lvl: %1\nCurrent XP: %2");
			interfaceCache[SkillConstants.MORE_DETAILS_PANEL_ID[i]].instructions = new int[][] { { 2, i, 0 }, { 3, i, 0 },
					{ 6, i, 0 }, { 6, i, 15, 3, i, 0 }, { 21, i, 0 }, { 22, i, 15, 3, i, 23, 0 } };
			interfaceCache[curIds[i]].setText("%1");
			interfaceCache[curIds[i]].instructions = new int[][] { { 1, i, 0 } };
			interfaceCache[maxIds[i]].setText("%1");
			interfaceCache[maxIds[i]].instructions = new int[][] { { 2, i, 0 } };
		}
	}

	public void specialBar(int id) // 7599
	{
		/*
		 * addActionButton(ID, SpriteOFF, SpriteON, Width, Height, "SpriteText");
		 */
		addActionButton(id - 12, 7587, -1, 150, 26, "Use @gre@Special Attack");
		/* removeSomething(ID); */
		for (int i = id - 11; i < id; i++)
			removeSomething(i);

		RSInterface rsi = interfaceCache[id - 12];
		rsi.width = 150;
		rsi.height = 26;

		rsi = interfaceCache[id];
		rsi.width = 150;
		rsi.height = 26;

		rsi.child(0, id - 12, 0, 0);

		rsi.child(12, id + 1, 3, 7);

		rsi.child(23, id + 12, 16, 8);

		for (int i = 13; i < 23; i++) {
			rsi.childY[i] -= 1;
		}

		rsi = interfaceCache[id + 1];
		rsi.type = 5;
		rsi.sprite1 = CustomSpriteLoader(7600, "");

		for (int i = id + 2; i < id + 12; i++) {
			rsi = interfaceCache[i];
			rsi.type = 5;
		}

		sprite1(id + 2, 7601);
		sprite1(id + 3, 7602);
		sprite1(id + 4, 7603);
		sprite1(id + 5, 7604);
		sprite1(id + 6, 7605);
		sprite1(id + 7, 7606);
		sprite1(id + 8, 7607);
		sprite1(id + 9, 7608);
		sprite1(id + 10, 7609);
		sprite1(id + 11, 7610);
	}

	public static void buildTradeScreen(TextDrawingArea[] TDA) {
		RSInterface RSInterface = addTabInterface(3323);
		RSInterface.textAlign = 1;
		setChildren(14, RSInterface);
		addSprite(3324, 100);
		addHoverButton(3442, 91, 21, 21, "Close", 250, 3325, 3);
		addHoveredButton(3325, 92, 21, 21, 3326);
		addText(3417, "Trading With:", 0xFF9933, true, true, 52, TDA, 2);
		addText(3421, "Accept", 0x00C000, false, true, 52, TDA, 1);
		addText(3423, "Decline", 0xC00000, false, true, 52, TDA, 1);
		addText(3418, "Player Name has\\n 28 free\\n inventory slots.", 0xFF9933, false, true, 52, TDA, 0);
		addText(3431, "Waiting for other player...", 0xFFFFFF, false, true, 52, TDA, 1);
		addHoverButton(3420, 103, 65, 32, "Accept", 0, 3327, 1);
		addHoveredButton(3327, 102, 72, 32, 3328);
		addHoverButton(3422, 103, 65, 32, "Decline", 0, 3329, 1);
		addHoveredButton(3329, 102, 72, 32, 3330);
		setBounds(3324, 0, 16, 0, RSInterface);
		setBounds(3442, 485, 24, 1, RSInterface);
		setBounds(3325, 485, 24, 2, RSInterface);
		setBounds(3417, 192, 25, 3, RSInterface);
		setBounds(3420, 223, 120, 4, RSInterface);
		setBounds(3327, 223, 120, 5, RSInterface);
		setBounds(3422, 223, 160, 6, RSInterface);
		setBounds(3329, 223, 160, 7, RSInterface);
		setBounds(3421, 236, 127, 8, RSInterface);
		setBounds(3423, 236, 167, 9, RSInterface);
		setBounds(3431, 185, 303, 10, RSInterface);
		setBounds(3415, 12, 64, 11, RSInterface);
		setBounds(3416, 321, 64, 12, RSInterface);
		setBounds(3418, 215, 68, 13, RSInterface);
		RSInterface = addTabInterface(3443);
		setChildren(15, RSInterface);
		addSprite(3444, 101);
		addButton(3546, 0, "", 63, 24, "Accept", 1);
		addButton(3548, 0, "", 63, 24, "Decline", 3);
		addText(3547, "Accept", 0x00C000, false, true, 52, TDA, 1);
		addText(3549, "Decline", 0xC00000, false, true, 52, TDA, 1);
		addText(3450, "Trading With:", 0x00FFFF, true, true, 52, TDA, 2);
		addText(3451, "Player name", 0xFFFFFF, true, true, 52, TDA, 2);
		setBounds(3444, 12, 20, 0, RSInterface);
		setBounds(3442, 470, 32, 1, RSInterface);
		setBounds(3325, 470, 32, 2, RSInterface);
		setBounds(3535, 130, 28, 3, RSInterface);
		setBounds(3536, 105, 47, 4, RSInterface);
		setBounds(3546, 189, 295, 5, RSInterface);
		setBounds(3548, 258, 295, 6, RSInterface);
		setBounds(3547, 200, 299, 7, RSInterface);
		setBounds(3549, 268, 299, 8, RSInterface);
		setBounds(3557, 71, 87, 9, RSInterface);
		setBounds(3558, 315, 87, 10, RSInterface);
		setBounds(3533, 64, 70, 11, RSInterface);
		setBounds(3534, 297, 70, 12, RSInterface);
		setBounds(3450, 65, 289, 13, RSInterface);
		setBounds(3451, 65, 304, 14, RSInterface);
	}

	public static void Skills(TextDrawingArea[] TDA) {
		RSInterface Interface = addInterface(3917);
		addSprite(25049, Client.cacheSprite[104]);
		addText(3984, "Total Lvl: 0", 0xffff00, false, true, 52, TDA, 2);
		setChildren(3, Interface);
		setBounds(25049, 3, 230, 0, Interface);
		setBounds(3984, 65, 237, 1, Interface);
		setBounds(20001, 0, 0, 2, Interface);
		Interface = addInterface(20001);
		setChildren(144 + 72, Interface);
		int[] logoutID = { 2450, 2451, 2452 };
		int[] logoutID2 = { 2458 };

		for (int i : logoutID) {
			RSInterface Logout = interfaceCache[i];
			Logout.textColor = 0xFF981F;
			Logout.contentType = 0;
		}

		for (int i : logoutID2) {
			RSInterface Logout = interfaceCache[i];
			Logout.contentType = 0;
		}

		int frame = 48;

		int[] skillID1 = { 4004, 4016, 4028, 4006, 4018, 4030, 4008, 4020, 4032, 4010, 4022, 4034, 4012, 4024, 4036,
				4014, 4026, 4038, 4152, 12166, 13926, 60204, 60206, 60208 };

		int[] skillID2 = { 4005, 4017, 4029, 4007, 4019, 4031, 4009, 4021, 4033, 4011, 4023, 4035, 4013, 4025, 4037,
				4015, 4027, 4038, 4155, 12167, 13927, 60205, 60207, 60209 };

		int[] skillButtonIDs = { 60002, 60003, 60004, 60005, 60006, 60007, 60008, 60009, 60010, 60011, 60012, 60013,
				60014, 60015, 60016, 60017, 60018, 60019, 60020, 60021, 60022, 60210, 60220, 60230 };

		int[] hoverIDs = { 60023, 60030, 60037, 60044, 60051, 60058, 60065, 60072, 60080, 60087, 60094, 60101, 60108,
				60115, 60122, 60129, 60136, 60143, 60150, 60157, 60164, 60215, 60225, 60235 };

		for (int skill = 0; skill < SkillConstants.SKILL_NAMES.length; skill++) {
			/** Skill Text Here **/
			addSprite(20002 + skill, Client.cacheSprite[105]);
			setBounds(20002 + skill, (62 * (skill % 3)) + 3, (28 * (skill / 3)) + 6, skill, Interface);

			addSprite(25025 + skill, Client.cacheSprite[106 + skill]);
			setBounds(25025 + skill, (62 * (skill % 3)) + 4, (28 * (skill / 3)) + 7, skill + 24, Interface);

			addHoverButton(27000 + skill, "", 0, 60, 27, "Set Level Target", 997, -1, 1);
			addHoverButton(27024 + skill, "", 0, 60, 27, "Set XP Target", 998, -1, 1);
			addHoverButton(27048 + skill, "", 0, 60, 27, "Clear Target", 999, -1, 1);
			setBounds(27048 + skill, (62 * (skill % 3)) + 3, (28 * (skill / 3)) + 6, frame++, Interface);
			setBounds(27024 + skill, (62 * (skill % 3)) + 3, (28 * (skill / 3)) + 6, frame++, Interface);
			setBounds(27000 + skill, (62 * (skill % 3)) + 3, (28 * (skill / 3)) + 6, frame++, Interface);

			addText(skillID1[skill], "%1", 0xFFFF00, 3, true, 52, 0);
			setBounds(skillID1[skill], (62 * (skill % 3)) + 37, (28 * (skill / 3)) + 8, frame++, Interface);
			int[][] instructions = new int[][] { { 1, getSkillIndex(SkillConstants.SKILL_NAMES[skill]), 0 } };
			interfaceCache[skillID1[skill]].instructions = instructions;

			addText(skillID2[skill], "%1", 0xFFFF00, 3, true, 52, 0);
			setBounds(skillID2[skill], (62 * (skill % 3)) + 52, (28 * (skill / 3)) + 20, frame++, Interface);
			instructions = new int[][] { { 2, getSkillIndex(SkillConstants.SKILL_NAMES[skill]), 0 } };
			interfaceCache[skillID2[skill]].instructions = instructions;

			addSkillButton(skillButtonIDs[skill], 0, "", SkillConstants.SKILL_NAMES[skill], 1, hoverIDs[skill], TDA, SkillConstants.SKILL_NAMES[skill]);
			setBounds(skillButtonIDs[skill], (62 * (skill % 3)) + 3, (28 * (skill / 3)) + 6, frame++, Interface);
		}

		for (int skill = 0; skill < 24; skill++) {
			setBounds(hoverIDs[skill], 0, 0, frame++, Interface);
		}
	}

	private static void skilllevel(TextDrawingArea[] tda) {
		RSInterface attack = interfaceCache[6247];
		RSInterface defence = interfaceCache[6253];
		RSInterface str = interfaceCache[6206];
		RSInterface hits = interfaceCache[6216];
		RSInterface rng = interfaceCache[4443];
		RSInterface pray = interfaceCache[6242];
		RSInterface mage = interfaceCache[6211];
		RSInterface cook = interfaceCache[6226];
		RSInterface wood = interfaceCache[4272];
		RSInterface flet = interfaceCache[6231];
		RSInterface fish = interfaceCache[6258];
		RSInterface fire = interfaceCache[4282];
		RSInterface craf = interfaceCache[6263];
		RSInterface smit = interfaceCache[6221];
		RSInterface mine = interfaceCache[4416];
		RSInterface herb = interfaceCache[6237];
		RSInterface agil = interfaceCache[4277];
		RSInterface thie = interfaceCache[4261];
		RSInterface slay = interfaceCache[12122];
		RSInterface farm = interfaceCache[5267];
		RSInterface rune = interfaceCache[4267];
		RSInterface cons = interfaceCache[7267];
		RSInterface hunt = interfaceCache[8267];
		RSInterface summ = addInterface(9267);
		RSInterface dung = addInterface(10267);

		addSprite(17878, 131);
		addSprite(17879, 132);
		addSprite(17880, 133);
		addSprite(17881, 134);
		addSprite(17882, 135);
		addSprite(17883, 136);
		addSprite(17884, 137);
		addSprite(17885, 138);
		addSprite(17886, 139);
		addSprite(17887, 140);
		addSprite(17888, 141);
		addSprite(17889, 142);
		addSprite(17890, 143);
		addSprite(17891, 144);
		addSprite(17892, 145);
		addSprite(17893, 146);
		addSprite(17894, 147);
		addSprite(17895, 148);
		addSprite(17896, 149);
		addSprite(11897, 150);
		addSprite(17898, 151);
		addSprite(17899, 152);
		addSprite(17900, 153);
		addSprite(17901, 154);
		addSprite(17902, 155);

		setChildren(4, attack);
		setBounds(17878, 20, 30, 0, attack);
		setBounds(4268, 80, 15, 1, attack);
		setBounds(4269, 80, 45, 2, attack);
		setBounds(358, 95, 75, 3, attack);
		setChildren(4, defence);
		setBounds(17879, 20, 30, 0, defence);
		setBounds(4268, 80, 15, 1, defence);
		setBounds(4269, 80, 45, 2, defence);
		setBounds(358, 95, 75, 3, defence);
		setChildren(4, str);
		setBounds(17880, 20, 30, 0, str);
		setBounds(4268, 80, 15, 1, str);
		setBounds(4269, 80, 45, 2, str);
		setBounds(358, 95, 75, 3, str);
		setChildren(4, hits);
		setBounds(17881, 20, 30, 0, hits);
		setBounds(4268, 80, 15, 1, hits);
		setBounds(4269, 80, 45, 2, hits);
		setBounds(358, 95, 75, 3, hits);
		setChildren(4, rng);
		setBounds(17882, 20, 30, 0, rng);
		setBounds(4268, 80, 15, 1, rng);
		setBounds(4269, 80, 45, 2, rng);
		setBounds(358, 95, 75, 3, rng);
		setChildren(4, pray);
		setBounds(17883, 20, 30, 0, pray);
		setBounds(4268, 80, 15, 1, pray);
		setBounds(4269, 80, 45, 2, pray);
		setBounds(358, 95, 75, 3, pray);
		setChildren(4, mage);
		setBounds(17884, 20, 30, 0, mage);
		setBounds(4268, 80, 15, 1, mage);
		setBounds(4269, 80, 45, 2, mage);
		setBounds(358, 95, 75, 3, mage);
		setChildren(4, cook);
		setBounds(17885, 20, 30, 0, cook);
		setBounds(4268, 80, 15, 1, cook);
		setBounds(4269, 80, 45, 2, cook);
		setBounds(358, 95, 75, 3, cook);
		setChildren(4, wood);
		setBounds(17886, 20, 30, 0, wood);
		setBounds(4268, 80, 15, 1, wood);
		setBounds(4269, 80, 45, 2, wood);
		setBounds(358, 95, 75, 3, wood);
		setChildren(4, flet);
		setBounds(17887, 20, 30, 0, flet);
		setBounds(4268, 80, 15, 1, flet);
		setBounds(4269, 80, 45, 2, flet);
		setBounds(358, 95, 75, 3, flet);
		setChildren(4, fish);
		setBounds(17888, 20, 30, 0, fish);
		setBounds(4268, 80, 15, 1, fish);
		setBounds(4269, 80, 45, 2, fish);
		setBounds(358, 95, 75, 3, fish);
		setChildren(4, fire);
		setBounds(17889, 20, 30, 0, fire);
		setBounds(4268, 80, 15, 1, fire);
		setBounds(4269, 80, 45, 2, fire);
		setBounds(358, 95, 75, 3, fire);
		setChildren(4, craf);
		setBounds(17890, 20, 30, 0, craf);
		setBounds(4268, 80, 15, 1, craf);
		setBounds(4269, 80, 45, 2, craf);
		setBounds(358, 95, 75, 3, craf);
		setChildren(4, smit);
		setBounds(17891, 20, 30, 0, smit);
		setBounds(4268, 80, 15, 1, smit);
		setBounds(4269, 80, 45, 2, smit);
		setBounds(358, 95, 75, 3, smit);
		setChildren(4, mine);
		setBounds(17892, 20, 30, 0, mine);
		setBounds(4268, 80, 15, 1, mine);
		setBounds(4269, 80, 45, 2, mine);
		setBounds(358, 95, 75, 3, mine);
		setChildren(4, herb);
		setBounds(17893, 20, 30, 0, herb);
		setBounds(4268, 80, 15, 1, herb);
		setBounds(4269, 80, 45, 2, herb);
		setBounds(358, 95, 75, 3, herb);
		setChildren(4, agil);
		setBounds(17894, 20, 30, 0, agil);
		setBounds(4268, 80, 15, 1, agil);
		setBounds(4269, 80, 45, 2, agil);
		setBounds(358, 95, 75, 3, agil);
		setChildren(4, thie);
		setBounds(17895, 20, 30, 0, thie);
		setBounds(4268, 80, 15, 1, thie);
		setBounds(4269, 80, 45, 2, thie);
		setBounds(358, 95, 75, 3, thie);
		setChildren(4, slay);
		setBounds(17896, 20, 30, 0, slay);
		setBounds(4268, 80, 15, 1, slay);
		setBounds(4269, 80, 45, 2, slay);
		setBounds(358, 95, 75, 3, slay);
		setChildren(3, farm);
		setBounds(4268, 80, 15, 0, farm);
		setBounds(4269, 80, 45, 1, farm);
		setBounds(358, 95, 75, 2, farm);
		setChildren(4, rune);
		setBounds(17898, 20, 30, 0, rune);
		setBounds(4268, 80, 15, 1, rune);
		setBounds(4269, 80, 45, 2, rune);
		setBounds(358, 95, 75, 3, rune);
		setChildren(3, cons);
		setBounds(4268, 80, 15, 0, cons);
		setBounds(4269, 80, 45, 1, cons);
		setBounds(358, 95, 75, 2, cons);
		setChildren(3, hunt);
		setBounds(4268, 80, 15, 0, hunt);
		setBounds(4269, 80, 45, 1, hunt);
		setBounds(358, 95, 75, 2, hunt);
		setChildren(4, summ);
		setBounds(17901, 20, 30, 0, summ);
		setBounds(4268, 80, 15, 1, summ);
		setBounds(4269, 80, 45, 2, summ);
		setBounds(358, 95, 75, 3, summ);
		setChildren(4, dung);
		setBounds(17902, 20, 30, 0, dung);
		setBounds(4268, 80, 15, 1, dung);
		setBounds(4269, 80, 45, 2, dung);
		setBounds(358, 95, 75, 3, dung);
	}
	
	public static void optionTab1(TextDrawingArea[] tda) {
        RSInterface tab = addTabInterface(904);
        RSInterface energy = interfaceCache[149];
        energy.textColor = 0xff9933;
        addSprite(949, 35, "Options/SPRITE");
        addSprite(950, 36, "Options/SPRITE");
        addSprite(952, 37, "Options/SPRITE");
        addSprite(954, 38, "Options/SPRITE");
        addSprite(956, 29, "Options/SPRITE");
        addSprite(958, 39, "Options/SPRITE");
        addSprite(960, 40, "Options/SPRITE");
        addSprite(961, 41, "Options/SPRITE");
        addSprite(962, 9, "Options/SPRITE");
        addSprite(963, 17, "Options/SPRITE");
        addSprite(964, 12, "Options/SPRITE");
        addSprite(965, 11, "Options/SPRITE");
        addSprite(966, 10, "Options/SPRITE");
        addSprite(968, 42, "Options/SPRITE");
        addSprite(971, 43, "Options/SPRITE");
        addSprite(972, 44, "Options/SPRITE");
        addConfigButton(152, 904, 30, 31, "Options/SPRITE", 40, 40, "Toggle Run", 1, 5, 173);
        addConfigButton(12464, 904, 31, 30, "Options/SPRITE", 40, 40, "Toggle Accept Aid", 0, 5, 427);
        addConfigButton(951, 904, 31, 30, "Options/SPRITE", 40, 40, "Open House Options", 0, 5, 427);
        addConfigButton(953, 904, 30, 31, "Options/SPRITE", 40, 40, "Display", 0, 5, 427);
        addConfigButton(955, 904, 31, 30, "Options/SPRITE", 40, 40, "Audio", 0, 5, 427);
        addConfigButton(957, 904, 31, 30, "Options/SPRITE", 40, 40, "Chat", 0, 5, 427);
        addConfigButton(959, 904, 31, 30, "Options/SPRITE", 40, 40, "Controls", 0, 5, 427);
        addConfigButton(967, 904, 30, 31, "Options/SPRITE", 40, 40, "Toggle Data Orbs", 0, 5, 427);
        addConfigButton(969, 904, 30, 31, "Options/SPRITE", 40, 40, "Toggle roof-removal", 0, 5, 427);
        addConfigButton(970, 904, 31, 30, "Options/SPRITE", 40, 40, "Toggle 'Remaining XP'", 0, 5, 427);
        tab.totalChildren(27);
        tab.child(0, 12464, 19, 220);
        tab.child(1, 949, 23, 225);
        tab.child(2, 152, 75, 220);
        tab.child(3, 149, 80, 243);
        tab.child(4, 950, 86, 224);
        tab.child(5, 951, 131, 220);
        tab.child(6, 952, 135, 224);
        tab.child(7, 953, 6, 1);
        tab.child(8, 954, 10, 5);
        tab.child(9, 955, 52, 1);
        tab.child(10, 956, 57, 5);
        tab.child(11, 957, 98, 1);
        tab.child(12, 958, 102, 5);
        tab.child(13, 959, 144, 1);
        tab.child(14, 960, 149, 5);
        tab.child(15, 961, 0, 42);
        tab.child(16, 962, 9, 70);
        tab.child(17, 963, 149, 80);
        tab.child(18, 964, 117, 80);
        tab.child(19, 965, 85, 80);
        tab.child(20, 966, 53, 80);
        tab.child(21, 967, 19, 121);
        tab.child(22, 968, 23, 125);
        tab.child(23, 969, 75, 121);
        tab.child(24, 970, 131, 121);
        tab.child(25, 971, 79, 125);
        tab.child(26, 972, 135, 125);
    }

	public static void questTab(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(638);
		RSInterface list = addTabInterface(16025);
		addText(640, "Quests", tda, 2, 0xeb981f, false, true);
		addText(663, "Free", tda, 2, 0xff9900, false, true);
		addText(682, "Members", tda, 2, 0xff9900, false, true);
		addSprite(16022, Client.cacheSprite[57]);
		addText(16023, "Quest Points: ---", tda, 0, 0xeb981f, false, true);
		addButton(16020, Client.cacheSprite[59], "Swap to Achievement Diary");
		addSprite(16024, Client.cacheSprite[48]);
		tab.totalChildren(7);
		tab.child(0, 640, 14, 4);
		tab.child(1, 16024, 0, 25);
		tab.child(2, 16025, 6, 24);
		tab.child(3, 16022, 0, 22);
		tab.child(4, 16022, 0, 249);
		tab.child(5, 16023, 4, 251);
		tab.child(6, 16020, 162, 1);
		/* List/scrollbar */
		for (int i = 33026; i <= 33125; i++) {
			addClickableText(i, "" + i, "Show", tda, 0, 0xff0000, false, true, 150);
		}
		list.totalChildren(101);
		list.child(0, 663, 4, 2);
		for (int id = 1, cid = 33026; id <= 100 && cid <= 33125; id++, cid++) {
			list.childY[1] = 18;
			list.child(id, cid, 9, list.childY[id - 1] + 13);
		}
		list.width = 168;
		list.height = 225;
		list.scrollMax = 1320;
	}

	private static void makePestControlBoatPanel(TextDrawingArea[] drawingArea) {
		RSInterface component = addInterface(21119);
		addText(21120, "Cabbage", 0x999999, 0, true, 52, drawingArea, 1);
		addText(21121, "Asparagus", 0x33cc00, 0, true, 52, drawingArea, 1);
		addText(21122, "(Need 5 to 25 players)", 0xFFcc33, 0, true, 52, drawingArea, 1);
		addText(21123, "Points", 0x33ccff, 0, true, 52, drawingArea, 1);
		component.children = new int[4];
		component.childX = new int[4];
		component.childY = new int[4];
		setBounds(21120, 15, 12, 0, component);
		setBounds(21121, 15, 30, 1, component);
		setBounds(21122, 15, 48, 2, component);
		setBounds(21123, 15, 66, 3, component);
	}

	private static void makePestcontrolGamePanel(TextDrawingArea[] drawingArea) {
		RSInterface component = addInterface(21100);
		addSprite(21101, 94);
		addSprite(21102, 95);
		addSprite(21103, 96);
		addSprite(21104, 97);
		addSprite(21105, 98);
		addSprite(21106, 99);
		addText(21107, "", 0xCC00CC, 0, true, 52, drawingArea, 1);
		addText(21108, "", 0x0000FF, 0, true, 52, drawingArea, 1);
		addText(21109, "", 0xFFFF44, 0, true, 52, drawingArea, 1);
		addText(21110, "", 0xCC0000, 0, true, 52, drawingArea, 1);
		addText(21111, "250", 0x99FF33, 0, true, 52, drawingArea, 1);// w purp
		addText(21112, "250", 0x99FF33, 0, true, 52, drawingArea, 1);// e blue
		addText(21113, "250", 0x99FF33, 0, true, 52, drawingArea, 1);// se yel
		addText(21114, "250", 0x99FF33, 0, true, 52, drawingArea, 1);// sw red
		addText(21115, "200", 0x99FF33, 0, true, 52, drawingArea, 1);// attacks
		addText(21116, "0", 0x99FF33, 0, true, 52, drawingArea, 1);// knights hp
		addText(21117, "Time Remaining:", 0xFFFFFF, 0, true, 52, drawingArea, 0);
		addText(21118, "", 0xFFFFFF, 0, true, 52, drawingArea, 0);
		component.children = new int[18];
		component.childX = new int[18];
		component.childY = new int[18];
		setBounds(21101, 361, 26, 0, component);
		setBounds(21102, 396, 26, 1, component);
		setBounds(21103, 436, 26, 2, component);
		setBounds(21104, 474, 26, 3, component);
		setBounds(21105, 3, 21, 4, component);
		setBounds(21106, 3, 50, 5, component);
		setBounds(21107, 371, 60, 6, component);
		setBounds(21108, 409, 60, 7, component);
		setBounds(21109, 443, 60, 8, component);
		setBounds(21110, 479, 60, 9, component);
		setBounds(21111, 362, 10, 10, component);
		setBounds(21112, 398, 10, 11, component);
		setBounds(21113, 436, 10, 12, component);
		setBounds(21114, 475, 10, 13, component);
		setBounds(21115, 32, 32, 14, component);
		setBounds(21116, 32, 62, 15, component);
		setBounds(21117, 8, 88, 16, component);
		setBounds(21118, 87, 88, 17, component);
	}

	public static void appendAchievementDiaryTab(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(29001);
		RSInterface scroll = addTabInterface(29006);

		addText(29002, "Achievements Diary", tda, 2, 0xEB981F, false, true);
		addSprite(29003, Client.cacheSprite[57]);
		addSprite(29004, Client.cacheSprite[48]);
		addButton(29005, Client.cacheSprite[58], "Swap to Quest Journal");
		addSprite(29115, Client.cacheSprite[57]);
		tab.totalChildren(7);
		tab.child(0, 29002, 14, 4);
		tab.child(1, 29003, 0, 22);
		tab.child(2, 29004, 0, 25);
		tab.child(3, 29005, 162, 1);
		tab.child(4, 29006, 0, 25);
		tab.child(6, 29115, 0, 249);
		scroll.width = 174;
		scroll.height = 224;
		scroll.scrollMax = 1488;
		scroll.totalChildren(4 + 24 + 22 + 24 + 25);

		for (int i = 0; i < ACHIEVEMENTS.length; i++) {
			addClickableText(29011 + i, ACHIEVEMENTS[i], "Read Journal", tda, 0, 0xFF0000, 0xFFFFFF, 90, 11);
		}

		addText(29007, "Easy Tasks", tda, 2, 0xFF9900, false, true);
		addText(29008, "Medium Tasks", tda, 2, 0xFF9900, false, true);
		addText(29009, "Hard Tasks", tda, 2, 0xFF9900, false, true);
		addText(29010, "Elite Tasks", tda, 2, 0xFF9900, false, true);

		// Add headers (easy, medium, hard, elite task)
		scroll.child(0, 29007, 10, 8);
		scroll.child(1, 29008, 10, 368);
		scroll.child(2, 29009, 10, 700);
		scroll.child(3, 29010, 10, 1060);

		// Easy tasks
		for (int i = 0; i < 24; i++) {
			scroll.child(4 + i, 29011 + i, 10, 28 + (i * 14));
		}

		// Medium tasks
		for (int i = 0; i < 22; i++) {
			scroll.child(4 + 24 + i, 29011 + 24 + i, 10, 388 + (i * 14));
		}

		// Hard tasks
		for (int i = 0; i < 24; i++) {
			scroll.child(4 + 24 + 22 + i, 29011 + 24 + 22 + i, 10, 720 + (i * 14));
		}

		// Elite tasks
		for (int i = 0; i < 25; i++) {
			scroll.child(4 + 24 + 22 + 24 + i, 29011 + 24 + 22 + 24 + i, 10, 1080 + (i * 14));
		}

		addText(29109, "Achievement Points: @gre@%1", tda, 0, 0xFF9900, false, true);
		tab.child(5, 29109, 4, 251);
		interfaceCache[29109].instructions = new int[][] { { 5, 500, 0 } };
	}

	private static void equipmentTab(TextDrawingArea[] wid) {
		RSInterface Interface = interfaceCache[1644];
		addSprite(15101, -1);
		addSprite(15102, -1);
		addSprite(15109, -1);
		addSprite(15110, -1);
		setToDefault(21338);
		setToDefault(21344);
		setToDefault(21342);
		setToDefault(21341);
		setToDefault(21340);
		setToDefault(15103);
		setToDefault(15104);
		Interface.children[26] = 27650;
		Interface.childX[26] = 0;
		Interface.childY[26] = 0;
		Interface = addInterface(27650);
		addHoverButton(15201, 60, 40, 40, "Show Equipment Screen", 0, 15202, 1);
		addHoveredButton(15202, 64, 40, 40, 15203);
		addHoverButton(15204, 61, 40, 40, "Show Items Kept on Death", 0, 15205, 1);
		addHoveredButton(15205, 63, 40, 40, 15206);
		addHoverButton(15207, 62, 40, 40, "XP Lock/Unlock", 0, 15208, 1);
		addHoveredButton(15208, 65, 40, 40, 15209);
		setChildren(6, Interface);
		setBounds(15201, 21, 210, 0, Interface);
		setBounds(15202, 21, 210, 1, Interface);
		setBounds(15204, 132, 210, 2, Interface);
		setBounds(15205, 132, 210, 3, Interface);
		setBounds(15207, 76, 210, 4, Interface);
		setBounds(15208, 76, 210, 5, Interface);
	}

	public static void addGodwarsKillCounter(TextDrawingArea[] tda) {
		RSInterface component = addTabInterface(14500);
		addText(14501, "NPC Killcount", tda, 1, 0xFF9040, false, true);
		addText(14502, "Armadyl kills", tda, 1, 0xFF9040, false, true);
		addText(14503, "Bandos kills", tda, 1, 0xFF9040, false, true);
		addText(14504, "Saradomin kills", tda, 1, 0xFF9040, false, true);
		addText(14505, "Zamorak kills", tda, 1, 0xFF9040, false, true);
		addText(14506, "0", tda, 1, 0x66FFFF, true, true);
		addText(14507, "0", tda, 1, 0x66FFFF, true, true);
		addText(14508, "0", tda, 1, 0x66FFFF, true, true);
		addText(14509, "0", tda, 1, 0x66FFFF, true, true);
		int[][] configs = { { 14501, 381, 9 }, { 14502, 381, 33 }, { 14503, 381, 47 }, { 14504, 381, 61 },
				{ 14505, 381, 75 }, { 14506, 482, 33 }, { 14507, 482, 47 }, { 14508, 482, 61 }, { 14509, 482, 75 } };
		component.totalChildren(9);

		for (int i = 0; i < configs.length; i++) {
			component.child(i, configs[i][0], configs[i][1], configs[i][2]);
		}
	}

	public static void Sidebar0(TextDrawingArea[] tda) {
		/*
		 * Sidebar0a(id, id2, id3, "text1", "text2", "text3", "text4", str1x, str1y, str2x, str2y, str3x, str3y, str4x,
		 * str4y, img1x, img1y, img2x, img2y, img3x, img3y, img4x, img4y, tda);
		 */
		Sidebar0a(1698, 1701, 7499, "Chop", "Hack", "Smash", "Block", 42, 75, 127, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(2276, 2279, 7574, "Stab", "Lunge", "Slash", "Block", 43, 75, 124, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(2423, 2426, 7599, "Chop", "Slash", "Lunge", "Block", 42, 75, 125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(3796, 3799, 7624, "Pound", "Pummel", "Spike", "Block", 39, 75, 121, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(4679, 4682, 7674, "Lunge", "Swipe", "Pound", "Block", 40, 75, 124, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(4705, 4708, 7699, "Chop", "Slash", "Smash", "Block", 42, 75, 125, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(5570, 5573, 7724, "Spike", "Impale", "Smash", "Block", 41, 75, 123, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(7762, 7765, 7800, "Chop", "Slash", "Lunge", "Block", 42, 75, 125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		/*
		 * Sidebar0b(id, id2, "text1", "text2", "text3", "text4", str1x, str1y, str2x, str2y, str3x, str3y, str4x,
		 * str4y, img1x, img1y, img2x, img2y, img3x, img3y, img4x, img4y, tda);
		 */
		Sidebar0b(776, 779, "Reap", "Chop", "Jab", "Block", 42, 75, 126, 75, 46, 128, 125, 128, 122, 103, 122, 50, 40, 103, 40, 50, tda);
		/*
		 * Sidebar0c(id, id2, id3, "text1", "text2", "text3", str1x, str1y, str2x, str2y, str3x, str3y, img1x, img1y,
		 * img2x, img2y, img3x, img3y, tda);
		 */
		Sidebar0c(425, 428, 7474, "Pound", "Pummel", "Block", 39, 75, 121, 75, 42, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(1749, 1752, 7524, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(1764, 1767, 7549, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(4446, 4449, 7649, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(5855, 5857, 7749, "Punch", "Kick", "Block", 40, 75, 129, 75, 42, 128, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0c(6103, 6132, 6117, "Bash", "Pound", "Block", 43, 75, 124, 75, 42, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(8460, 8463, 8493, "Jab", "Swipe", "Fend", 46, 75, 124, 75, 43, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(12290, 12293, 12323, "Flick", "Lash", "Deflect", 44, 75, 127, 75, 36, 128, 40, 50, 40, 103, 122, 50, tda);
		/*
		 * Sidebar0d(id, id2, "text1", "text2", "text3", str1x, str1y, str2x, str2y, str3x, str3y, img1x, img1y, img2x,
		 * img2y, img3x, img3y, tda);
		 */
		Sidebar0d(328, 331, "Bash", "Pound", "Focus", 42, 66, 39, 101, 41, 136, 40, 120, 40, 50, 40, 85, tda);

		RSInterface rsi = addInterface(19300);
		/* textSize(ID, wid, Size); */
		textSize(3983, tda, 0);
		/* addToggleButton(id, sprite, config, width, height, wid); */
		addToggleButton(150, 150, 172, 150, 44, "Auto Retaliate");

		rsi.totalChildren(2, 2, 2);
		rsi.child(0, 3983, 52, 25); // combat level
		rsi.child(1, 150, 21, 153); // auto retaliate

		rsi = interfaceCache[3983];
		rsi.textAlign = 1;
		rsi.textColor = 0xff981f;
	}

	public static void Sidebar0a(int id, int id2, int id3, String text1, String text2, String text3, String text4, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int str4x, int str4y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, int img4x, int img4y, TextDrawingArea[] tda) // 4button
																																																																															// spec
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);
		/* specialBar(ID); */
		rsi.specialBar(id3); // 7599

		rsi.width = 190;
		rsi.height = 261;

		int last = 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 46);
		frame++; // 2429
		rsi.child(frame, id2 + 4, 104, 99);
		frame++; // 2430
		rsi.child(frame, id2 + 5, 21, 99);
		frame++; // 2431
		rsi.child(frame, id2 + 6, 105, 46);
		frame++; // 2432

		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++; // bottomright 2433
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++; // topleft 2434
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++; // bottomleft 2435
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++; // topright 2436

		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++; // chop 2437
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++; // slash 2438
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++; // lunge 2439
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++; // block 2440

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon 2426
		rsi.child(frame, id3, 21, 205);
		frame++; // special attack 7599

		for (int i = id2 + 3; i < id2 + 7; i++) { // 2429 - 2433
			rsi = interfaceCache[i];
			rsi.sprite1 = CustomSpriteLoader(19301, "");
			rsi.sprite2 = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0b(int id, int id2, String text1, String text2, String text3, String text4, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int str4x, int str4y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, int img4x, int img4y, TextDrawingArea[] tda) // 4button
																																																																													// nospec
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);

		rsi.width = 190;
		rsi.height = 261;

		int last = 14;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 46);
		frame++; // 2429
		rsi.child(frame, id2 + 4, 104, 99);
		frame++; // 2430
		rsi.child(frame, id2 + 5, 21, 99);
		frame++; // 2431
		rsi.child(frame, id2 + 6, 105, 46);
		frame++; // 2432

		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++; // bottomright 2433
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++; // topleft 2434
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++; // bottomleft 2435
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++; // topright 2436

		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++; // chop 2437
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++; // slash 2438
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++; // lunge 2439
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++; // block 2440

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon 2426

		for (int i = id2 + 3; i < id2 + 7; i++) { // 2429 - 2433
			rsi = interfaceCache[i];
			rsi.sprite1 = CustomSpriteLoader(19301, "");
			rsi.sprite2 = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0c(int id, int id2, int id3, String text1, String text2, String text3, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, TextDrawingArea[] tda) // 3button
																																																																// spec
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);
		/* specialBar(ID); */
		rsi.specialBar(id3); // 7599

		rsi.width = 190;
		rsi.height = 261;

		int last = 12;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 99);
		frame++;
		rsi.child(frame, id2 + 4, 105, 46);
		frame++;
		rsi.child(frame, id2 + 5, 21, 46);
		frame++;

		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++; // topleft
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++; // bottomleft
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++; // topright

		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++; // chop
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++; // slash
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++; // lunge

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon
		rsi.child(frame, id3, 21, 205);
		frame++; // special attack 7599

		for (int i = id2 + 3; i < id2 + 6; i++) {
			rsi = interfaceCache[i];
			rsi.sprite1 = CustomSpriteLoader(19301, "");
			rsi.sprite2 = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0d(int id, int id2, String text1, String text2, String text3, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, TextDrawingArea[] tda) // 3button
																																																														// nospec
																																																														// (magic
																																																														// intf)
	{
		RSInterface rsi = addInterface(id); // 2423
		/* addText(ID, "Text", tda, Size, Colour, Centered); */
		addText(id2, "-2", tda, 3, 0xff981f, true); // 2426
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);

		// addText(353, "Spell", tda, 0, 0xff981f, false);
		removeSomething(353);
		addText(354, "Spell", tda, 0, 0xff981f, false);

		addCacheSprite(337, 19, 0, "combaticons");
		addCacheSprite(338, 13, 0, "combaticons2");
		addCacheSprite(339, 14, 0, "combaticons2");

		/* addToggleButton(id, sprite, config, width, height, tooltip); */
		// addToggleButton(349, 349, 108, 68, 44, "Select");
		removeSomething(349);
		addToggleButton(350, 350, 108, 68, 44, "Select");

		rsi.width = 190;
		rsi.height = 261;

		int last = 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 20, 115);
		frame++;
		rsi.child(frame, id2 + 4, 20, 80);
		frame++;
		rsi.child(frame, id2 + 5, 20, 45);
		frame++;

		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++; // topleft
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++; // bottomleft
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++; // topright

		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++; // bash
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++; // pound
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++; // focus

		rsi.child(frame, 349, 105, 46);
		frame++; // spell1
		rsi.child(frame, 350, 104, 106);
		frame++; // spell2

		rsi.child(frame, 353, 125, 74);
		frame++; // spell
		rsi.child(frame, 354, 125, 134);
		frame++; // spell

		rsi.child(frame, 19300, 0, 0);
		frame++; // stuffs
		rsi.child(frame, id2, 94, 4);
		frame++; // weapon
	}

	public static void emoteTab() {
		RSInterface tab = addTabInterface(147);
		RSInterface scroll = addTabInterface(148);
		tab.totalChildren(1);
		tab.child(0, 148, 0, 1);
		addButton(168, 0, "/Emotes/EMOTE", "Yes", 41, 47);
		addButton(169, 1, "/Emotes/EMOTE", "No", 41, 47);
		addButton(164, 2, "/Emotes/EMOTE", "Bow", 41, 47);
		addButton(165, 3, "/Emotes/EMOTE", "Angry", 41, 47);
		addButton(162, 4, "/Emotes/EMOTE", "Think", 41, 47);
		addButton(163, 5, "/Emotes/EMOTE", "Wave", 41, 47);
		addButton(13370, 6, "/Emotes/EMOTE", "Shrug", 41, 47);
		addButton(171, 7, "/Emotes/EMOTE", "Cheer", 41, 47);
		addButton(167, 8, "/Emotes/EMOTE", "Beckon", 41, 47);
		addButton(170, 9, "/Emotes/EMOTE", "Laugh", 41, 47);
		addButton(13366, 10, "/Emotes/EMOTE", "Jump for Joy", 41, 47);
		addButton(13368, 11, "/Emotes/EMOTE", "Yawn", 41, 47);
		addButton(166, 12, "/Emotes/EMOTE", "Dance", 41, 47);
		addButton(13363, 13, "/Emotes/EMOTE", "Jig", 41, 47);
		addButton(13364, 14, "/Emotes/EMOTE", "Spin", 41, 47);
		addButton(13365, 15, "/Emotes/EMOTE", "Headbang", 41, 47);
		addButton(161, 16, "/Emotes/EMOTE", "Cry", 41, 47);
		addButton(11100, 17, "/Emotes/EMOTE", "Blow kiss", 41, 47);
		addButton(13362, 18, "/Emotes/EMOTE", "Panic", 41, 47);
		addButton(13367, 19, "/Emotes/EMOTE", "Raspberry", 41, 47);
		addButton(172, 20, "/Emotes/EMOTE", "Clap", 41, 47);
		addButton(13369, 21, "/Emotes/EMOTE", "Salute", 41, 47);
		addButton(13383, 22, "/Emotes/EMOTE", "Goblin Bow", 41, 47);
		addButton(13384, 23, "/Emotes/EMOTE", "Goblin Salute", 41, 47);
		addButton(667, 24, "/Emotes/EMOTE", "Glass Box", 41, 47);
		addButton(6503, 25, "/Emotes/EMOTE", "Climb Rope", 41, 47);
		addButton(6506, 26, "/Emotes/EMOTE", "Lean On Air", 41, 47);
		addButton(666, 27, "/Emotes/EMOTE", "Glass Wall", 41, 47);
		addButton(18464, 28, "/Emotes/EMOTE", "Zombie Walk", 41, 47);
		addButton(18465, 29, "/Emotes/EMOTE", "Zombie Dance", 41, 47);
		addButton(15166, 30, "/Emotes/EMOTE", "Scared", 41, 47);
		addButton(18686, 31, "/Emotes/EMOTE", "Rabbit Hop", 41, 47);
		addConfigButton(154, 147, 32, 33, "EMOTE", 41, 47, "Skillcape Emote", 0, 1, 700);
		scroll.totalChildren(33);
		scroll.child(0, 168, 10, 7);
		scroll.child(1, 169, 54, 7);
		scroll.child(2, 164, 98, 14);
		scroll.child(3, 165, 137, 7);
		scroll.child(4, 162, 9, 56);
		scroll.child(5, 163, 48, 56);
		scroll.child(6, 13370, 95, 56);
		scroll.child(7, 171, 137, 56);
		scroll.child(8, 167, 7, 105);
		scroll.child(9, 170, 51, 105);
		scroll.child(10, 13366, 95, 104);
		scroll.child(11, 13368, 139, 105);
		scroll.child(12, 166, 6, 154);
		scroll.child(13, 13363, 50, 154);
		scroll.child(14, 13364, 90, 154);
		scroll.child(15, 13365, 135, 154);
		scroll.child(16, 161, 8, 204);
		scroll.child(17, 11100, 51, 203);
		scroll.child(18, 13362, 99, 204);
		scroll.child(19, 13367, 137, 203);
		scroll.child(20, 172, 10, 253);
		scroll.child(21, 13369, 53, 253);
		scroll.child(22, 13383, 88, 258);
		scroll.child(23, 13384, 138, 252);
		scroll.child(24, 667, 2, 303);
		scroll.child(25, 6503, 49, 302);
		scroll.child(26, 6506, 93, 302);
		scroll.child(27, 666, 137, 302);
		scroll.child(28, 18464, 9, 352);
		scroll.child(29, 18465, 50, 352);
		scroll.child(30, 15166, 94, 356);
		scroll.child(31, 18686, 141, 353);
		scroll.child(32, 154, 5, 401);
		scroll.width = 173;
		scroll.height = 258;
		scroll.scrollMax = 403;
	}

	public static void quickCurses(TextDrawingArea[] TDA) {
		RSInterface tab = addTabInterface(17234);
		addTransparentSprite(17229, 0, "Quicks/Quickprayers", 50);
		addSprite(17201, 3, "Quicks/Quickprayers");
		addText(17230, "Select your quick prayers:", TDA, 0, 0xFF981F, false, true);
		for (int i = 17202, j = 630; i <= 17228 || j <= 656; i++, j++) {
			addConfigButton(i, 17200, 2, 1, "Quicks/Quickprayers", 14, 15, "Select", 0, 1, j);
		}
		addHoverButton(17231, "Quicks/Quickprayers", 4, 190, 24, "Confirm Selection", -1, 17232, 1);
		addHoveredButton(17232, "Quicks/Quickprayers", 5, 190, 24, 17233);
		int frame = 0;
		setChildren(46, tab);
		setBounds(21358, 11, 8 + 20, frame++, tab);
		setBounds(21360, 50, 11 + 20, frame++, tab);
		setBounds(21362, 87, 11 + 20, frame++, tab);
		setBounds(21364, 122, 10 + 20, frame++, tab);
		setBounds(21366, 159, 11 + 20, frame++, tab);
		setBounds(21368, 12, 45 + 20, frame++, tab);
		setBounds(21370, 46, 45 + 20, frame++, tab);
		setBounds(21372, 83, 46 + 20, frame++, tab);
		setBounds(21374, 119, 45 + 20, frame++, tab);
		setBounds(21376, 157, 45 + 20, frame++, tab);
		setBounds(21378, 11, 83 + 20, frame++, tab);
		setBounds(21380, 49, 84 + 20, frame++, tab);
		setBounds(21382, 84, 83 + 20, frame++, tab);
		setBounds(21384, 123, 84 + 20, frame++, tab);
		setBounds(21386, 159, 83 + 20, frame++, tab);
		setBounds(21388, 12, 119 + 20, frame++, tab);
		setBounds(21390, 49, 119 + 20, frame++, tab);
		setBounds(21392, 88, 119 + 20, frame++, tab);
		setBounds(21394, 122, 121 + 20, frame++, tab);
		setBounds(21396, 155, 122 + 20, frame++, tab);
		setBounds(17229, 0, 25, frame++, tab);// Faded backing
		setBounds(17201, 0, 22, frame++, tab);// Split
		setBounds(17201, 0, 237, frame++, tab);// Split
		setBounds(17202, 13 - 3, 8 + 17, frame++, tab);
		setBounds(17203, 52 - 3, 8 + 17, frame++, tab);
		setBounds(17204, 90 - 3, 8 + 17, frame++, tab);
		setBounds(17205, 126 - 3, 8 + 17, frame++, tab);
		setBounds(17206, 162 - 3, 8 + 17, frame++, tab);
		setBounds(17207, 13 - 3, 45 + 17, frame++, tab);
		setBounds(17208, 52 - 3, 45 + 17, frame++, tab);
		setBounds(17209, 90 - 3, 45 + 17, frame++, tab);
		setBounds(17210, 126 - 3, 45 + 17, frame++, tab);
		setBounds(17211, 162 - 3, 45 + 17, frame++, tab);
		setBounds(17212, 13 - 3, 80 + 17, frame++, tab);
		setBounds(17213, 52 - 3, 80 + 17, frame++, tab);
		setBounds(17214, 90 - 3, 80 + 17, frame++, tab);
		setBounds(17215, 126 - 3, 80 + 17, frame++, tab);
		setBounds(17216, 162 - 3, 80 + 17, frame++, tab);
		setBounds(17217, 13 - 3, 119 + 17, frame++, tab);
		setBounds(17218, 52 - 3, 119 + 17, frame++, tab);
		setBounds(17219, 90 - 3, 119 + 17, frame++, tab);
		setBounds(17220, 126 - 3, 119 + 17, frame++, tab);
		setBounds(17221, 162 - 3, 119 + 17, frame++, tab);
		setBounds(17230, 5, 5, frame++, tab);// text
		setBounds(17231, 0, 237, frame++, tab);// confirm
		setBounds(17232, 0, 237, frame++, tab);// Confirm hover
	}

	public static void duelInterface(TextDrawingArea[] TDA) {
		RSInterface widget = addInterface(37888);

		addSprite(37889, Client.cacheSprite[86]);
		addConfigButton(37890, 904, -1, 87, 15, 15, "No Ranged", 1, 5, 780);
		addConfigButton(37891, 904, -1, 87, 15, 15, "No Melee", 1, 5, 781);
		addConfigButton(37892, 904, -1, 87, 15, 15, "No Magic", 1, 5, 782);
		addConfigButton(37893, 904, -1, 87, 15, 15, "Fun Weapons", 1, 5, 783);
		addConfigButton(37894, 904, -1, 87, 15, 15, "No Forfeit", 1, 5, 784);
		addConfigButton(37895, 904, -1, 87, 15, 15, "No Drinks", 1, 5, 785);
		addConfigButton(37896, 904, -1, 87, 15, 15, "No Food", 1, 5, 786);
		addConfigButton(37897, 904, -1, 87, 15, 15, "No Prayer", 1, 5, 787);
		addConfigButton(37898, 904, -1, 87, 15, 15, "No Movement", 1, 5, 788);
		addConfigButton(37899, 904, -1, 87, 15, 15, "Obstacles", 1, 5, 789);
		addConfigButton(37900, 904, -1, 87, 15, 15, "No Special Attack", 1, 5, 790);
		addHoverButton(3442, 91, 21, 21, "Close", 250, 3325, 3);
		addHoveredButton(3325, 92, 16, 16, 3326);
		addText(17800, "Accept", TDA, 0, 0x33ff00, false, true);
		addText(17801, "Decline", TDA, 0, 0xff0000, false, true);
		addHoverButton(37910, 89, 72, 32, "Accept", 0, 37911, 1);
		addHoveredButton(37911, 90, 72, 32, 37912);
		addHoverButton(37913, 89, 72, 32, "Decline", 250, 37914, 1);
		addHoveredButton(37914, 90, 72, 32, 37915);
		addConfigButton(37926, 904, -1, 88, 36, 36, "Helmet", 1, 5, 890);
		addConfigButton(37916, 904, -1, 88, 36, 36, "Neck", 1, 5, 891);
		addConfigButton(37917, 904, -1, 88, 36, 36, "Body", 1, 5, 892);
		addConfigButton(37918, 904, -1, 88, 36, 36, "Legs", 1, 5, 893);
		addConfigButton(37919, 904, -1, 88, 36, 36, "Feet", 1, 5, 894);
		addConfigButton(37920, 904, -1, 88, 36, 36, "Cape", 1, 5, 895);
		addConfigButton(37921, 904, -1, 88, 36, 36, "Arrows", 1, 5, 896);
		addConfigButton(37922, 904, -1, 88, 36, 36, "Weapon", 1, 5, 897);
		addConfigButton(37923, 904, -1, 88, 36, 36, "Shield", 1, 5, 898);
		addConfigButton(37924, 904, -1, 88, 36, 36, "Gloves", 1, 5, 899);
		addConfigButton(37925, 904, -1, 88, 36, 36, "Ring", 1, 5, 900);
		setChildren(36, widget);
		setBounds(37889, 0, 0, 0, widget);
		setBounds(3442, 482, 21, 1, widget);
		setBounds(3325, 482, 21, 2, widget);
		setBounds(37890, 350, 51, 3, widget);
		setBounds(37891, 350, 69, 4, widget);
		setBounds(37892, 350, 87, 5, widget);
		setBounds(37893, 350, 105, 6, widget);
		setBounds(37894, 350, 123, 7, widget);
		setBounds(37895, 350, 141, 8, widget);
		setBounds(37896, 350, 159, 9, widget);
		setBounds(37897, 350, 177, 10, widget);
		setBounds(37898, 350, 195, 11, widget);
		setBounds(37899, 350, 213, 12, widget);
		setBounds(37900, 350, 231, 13, widget);
		setBounds(37910, 247, 260, 14, widget);
		setBounds(37911, 247, 260, 15, widget);
		setBounds(37913, 377, 260, 16, widget);
		setBounds(37914, 377, 260, 17, widget);
		setBounds(17800, 265, 270, 18, widget);
		setBounds(17801, 395, 270, 19, widget);
		setBounds(38140, -1, 38, 20, widget);
		setBounds(38240, -1, 179, 21, widget);
		setBounds(37926, 242, 50, 22, widget);
		setBounds(37916, 242, 89, 23, widget);
		setBounds(37917, 242, 128, 24, widget);
		setBounds(37918, 242, 168, 25, widget);
		setBounds(37919, 242, 208, 26, widget);
		setBounds(37920, 201, 89, 27, widget);
		setBounds(37921, 283, 89, 28, widget);
		setBounds(37922, 186, 128, 29, widget);
		setBounds(37923, 298, 128, 30, widget);
		setBounds(37924, 186, 208, 31, widget);
		setBounds(37925, 298, 208, 32, widget);
		addText(38003, "Opponent: --", 0xE1981F, false, true, 52, TDA, 1);
		addText(38004, "Opponent's Level: --", 0xE1981F, false, true, 52, TDA, 1);
		addText(38005, "", TDA, 0, 0xAAA9A9, 3, true);
		setBounds(38003, 185, 21, 33, widget);
		setBounds(38004, 353, 21, 34, widget);
		setBounds(38005, 330, 302, 35, widget);

		widget = addInterface(38140);
		widget.height = 122;
		widget.width = 163;
		widget.scrollMax = 386;
		setChildren(32, widget);
		int Ypos = 18;
		int frameID = 0;

		for (int iD = 38145; iD <= 38175; iD++) {
			addText(iD, "", 0x000080, true, false, 52, TDA, 1);
			setBounds(iD, 202, Ypos, frameID, widget);
			frameID++;
			Ypos += 19;
			Ypos++;
		}

		addDuelContainer(38000, true);
		setBounds(38000, 29, 10, 31, widget);
		widget = addInterface(38240);
		widget.height = 119;
		widget.width = 163;
		widget.scrollMax = 386;
		setChildren(32, widget);
		int Y2pos = 18;
		int frameID2 = 0;

		for (int iD = 38245; iD <= 38275; iD++) {
			addText(iD, "", 0x000080, true, false, 52, TDA, 1);
			setBounds(iD, 202, Y2pos, frameID2, widget);
			frameID2++;
			Ypos += 19;
			Ypos++;
		}

		addDuelContainer(38001, false);
		setBounds(38001, 29, 10, 31, widget);
	}

	public static void Bank(TextDrawingArea[] wid) {
		RSInterface Interface = addTabInterface(5292);
		setChildren(46, Interface);

		int id = 50000;

		/**
		 * Background
		 */
		addSprite(id, 511);

		/**
		 * Close Window
		 */
		addHoverButton(id + 1, 91, 21, 21, "Close", 250, id + 2, 3);
		addHoveredButton(id + 2, 92, 16, 16, id + 3);

		/**
		 * Swap Insert Mode
		 */
		addHoverConfigButton(id + 4, id + 5, 512, 513, 35, 25, "Swap Insert Mode", 0, 1009);
		addHoveredConfigButton(interfaceCache[id + 4], id + 5, id + 6, 537, 538);

		/**
		 * Search
		 */
		addHoverConfigButton(id + 7, id + 8, 514, 515, 35, 25, "Search", 0, 1010);
		addHoveredConfigButton(interfaceCache[id + 7], id + 8, id + 9, 539, 540);

		/**
		 * Bank Pin
		 */
		addHoverButton(id + 10, 516, 114, 25, "Set A Bank PIN", 0, id + 11, 1);
		addHoveredButton(id + 11, 517, 114, 25, id + 12);

		/**
		 * Withdraw As Note
		 */
		addHoverConfigButton(id + 13, id + 14, 518, 519, 35, 25, "Withdraw as note", 0, 1011);
		addHoveredConfigButton(interfaceCache[id + 13], id + 14, id + 15, 520, 521);

		/**
		 * Total Items
		 */
		addSprite(id + 16, 522);
		addText(id + 17, "0", 0xB4965A, 3, true, -1, 0);
		addText(id + 18, "Tab", 0xB4965A, 3, true, -1, 0);
		addText(id + 19, "0", 0xB4965A, 3, true, -1, 0);
		addText(id + 20, "350", 0xB4965A, 3, true, -1, 0);

		/**
		 * Deposit Carried Items
		 */
		addHoverButton(id + 21, 523, 35, 25, "Deposit carried items", 0, id + 22, 1);
		addHoveredButton(id + 22, 524, 35, 25, id + 23);

		/**
		 * Deposit Worn Items
		 */
		addHoverButton(id + 24, 525, 35, 25, "Deposit worn items", 0, id + 25, 1);
		addHoveredButton(id + 25, 526, 35, 25, id + 26);

		/**
		 * Deposit BoB Items
		 */
		addHoverButton(id + 27, 527, 35, 25, "Deposit BoB items", 0, id + 28, 1);
		addHoveredButton(id + 28, 528, 35, 25, id + 29);

		/**
		 * Open Equipment
		 */
		addHoverButton(id + 30, 529, 34, 34, "Open Equipment", 0, id + 31, 1);
		addHoveredButton(id + 31, 530, 34, 34, id + 32);

		/**
		 * Set Up Tab Buttons
		 */
		addTabButton(id + 33, id + 34, "TAB", "View all", 1000);
		addTabButton(id + 35, id + 36, "TAB", "View tab 1", 1001);
		addTabButton(id + 37, id + 38, "TAB", "View tab 2", 1002);
		addTabButton(id + 39, id + 40, "TAB", "View tab 3", 1003);
		addTabButton(id + 41, id + 42, "TAB", "View tab 4", 1004);
		addTabButton(id + 43, id + 44, "TAB", "View tab 5", 1005);
		addTabButton(id + 45, id + 46, "TAB", "View tab 6", 1006);
		addTabButton(id + 47, id + 48, "TAB", "View tab 7", 1007);
		addTabButton(id + 49, id + 50, "TAB", "View tab 8", 1008);

		/**
		 * Set Up Tab Display Containers
		 */
		addTabItem(id + 51);
		addTabItem(id + 52);
		addTabItem(id + 53);
		addTabItem(id + 54);
		addTabItem(id + 55);
		addTabItem(id + 56);
		addTabItem(id + 57);
		addTabItem(id + 58);
		addTabItem(id + 59);

		/**
		 * Add The Infinity Sign
		 */
		addTabSprite(id + 60, id + 61, 533, 48, 38);

		/**
		 * Add The '+' Sign
		 */
		addTabSprite(id + 62, id + 63, 534, 48, 38);
		setBounds(id, 13, 13, 0, Interface);
		setBounds(5383, 190, 15, 1, Interface);
		setBounds(id + 1, 478, 16, 2, Interface);
		setBounds(id + 2, 478, 16, 3, Interface);
		setBounds(id + 4, 25, 285, 4, Interface);
		setBounds(id + 5, 25, 285, 5, Interface);
		setBounds(id + 7, 65, 285, 6, Interface);
		setBounds(id + 8, 65, 285, 7, Interface);
		setBounds(id + 10, 110, 285, 8, Interface);
		setBounds(id + 11, 110, 285, 9, Interface);
		setBounds(id + 13, 236, 285, 10, Interface);
		setBounds(id + 14, 236, 285, 11, Interface);
		setBounds(id + 16, 277, 284, 12, Interface);
		setBounds(id + 17, 290, 287, 13, Interface);
		setBounds(id + 18, 310, 297, 14, Interface);
		setBounds(id + 19, 333, 287, 15, Interface);
		setBounds(id + 20, 352, 297, 16, Interface);
		setBounds(id + 21, 375, 285, 17, Interface);
		setBounds(id + 22, 375, 285, 18, Interface);
		setBounds(id + 24, 415, 285, 19, Interface);
		setBounds(id + 25, 415, 285, 20, Interface);
		setBounds(id + 27, 455, 285, 21, Interface);
		setBounds(id + 28, 455, 285, 22, Interface);
		setBounds(id + 30, 456, 36, 23, Interface);
		setBounds(id + 31, 456, 36, 24, Interface);
		setBounds(5385, -4, 74, 25, Interface);
		setBounds(id + 33, 21, 36, 26, Interface);
		setBounds(id + 35, 69, 36, 27, Interface);
		setBounds(id + 37, 117, 36, 28, Interface);
		setBounds(id + 39, 165, 36, 29, Interface);
		setBounds(id + 41, 213, 36, 30, Interface);
		setBounds(id + 43, 261, 36, 31, Interface);
		setBounds(id + 45, 309, 36, 32, Interface);
		setBounds(id + 47, 357, 36, 33, Interface);
		setBounds(id + 49, 405, 36, 34, Interface);
		setBounds(id + 51, 77 - 48, 38, 35, Interface);
		setBounds(id + 52, 77, 38, 36, Interface);
		setBounds(id + 53, 77 + 48, 38, 37, Interface);
		setBounds(id + 54, 77 + 96, 38, 38, Interface);
		setBounds(id + 55, 77 + 144, 38, 39, Interface);
		setBounds(id + 56, 77 + 192, 38, 40, Interface);
		setBounds(id + 57, 77 + 240, 38, 41, Interface);
		setBounds(id + 58, 77 + 288, 38, 42, Interface);
		setBounds(id + 59, 77 + 336, 38, 43, Interface);
		setBounds(id + 60, 27, 38, 44, Interface);
		setBounds(id + 62, 76, 38, 45, Interface);

		/**
		 * Set Up Bank Tab Container
		 */
		Interface = interfaceCache[5385];
		Interface.totalChildren(33);

		addText(id + 64, "Tab 1", wid, 0, 0xF2AA3E, true);
		addText(id + 65, "Tab 2", wid, 0, 0xF2AA3E, true);
		addText(id + 66, "Tab 3", wid, 0, 0xF2AA3E, true);
		addText(id + 67, "Tab 4", wid, 0, 0xF2AA3E, true);
		addText(id + 68, "Tab 5", wid, 0, 0xF2AA3E, true);
		addText(id + 69, "Tab 6", wid, 0, 0xF2AA3E, true);
		addText(id + 70, "Tab 7", wid, 0, 0xF2AA3E, true);
		addText(id + 71, "Tab 8", wid, 0, 0xF2AA3E, true);

		addSprite(id + 72, 535);
		addSprite(id + 73, 535);
		addSprite(id + 74, 535);
		addSprite(id + 75, 535);
		addSprite(id + 76, 535);
		addSprite(id + 77, 535);
		addSprite(id + 78, 535);
		addSprite(id + 79, 535);

		addSprite(id + 80, 536);
		addSprite(id + 81, 536);
		addSprite(id + 82, 536);
		addSprite(id + 83, 536);
		addSprite(id + 84, 536);
		addSprite(id + 85, 536);
		addSprite(id + 86, 536);
		addSprite(id + 87, 536);

		addBankContainer(id + 88);
		addBankContainer(id + 89);
		addBankContainer(id + 90);
		addBankContainer(id + 91);
		addBankContainer(id + 92);
		addBankContainer(id + 93);
		addBankContainer(id + 94);
		addBankContainer(id + 95);
		addBankContainer(id + 96);

		/**
		 * Tab Index Text
		 */
		setBounds(id + 64, 30, 7, 0, Interface);
		setBounds(id + 65, 30, 7, 1, Interface);
		setBounds(id + 66, 30, 7, 2, Interface);
		setBounds(id + 67, 30, 7, 3, Interface);
		setBounds(id + 68, 30, 7, 4, Interface);
		setBounds(id + 69, 30, 7, 5, Interface);
		setBounds(id + 70, 30, 7, 6, Interface);
		setBounds(id + 71, 30, 7, 7, Interface);

		/**
		 * Tab Highlight Sprite
		 */
		setBounds(id + 72, 36, 3, 8, Interface);
		setBounds(id + 73, 36, 3, 9, Interface);
		setBounds(id + 74, 36, 3, 10, Interface);
		setBounds(id + 75, 36, 3, 11, Interface);
		setBounds(id + 76, 36, 3, 12, Interface);
		setBounds(id + 77, 36, 3, 13, Interface);
		setBounds(id + 78, 36, 3, 14, Interface);
		setBounds(id + 79, 36, 3, 15, Interface);

		/**
		 * Tab Divider Sprite
		 */
		setBounds(id + 80, 30, 2, 16, Interface);
		setBounds(id + 81, 30, 2, 17, Interface);
		setBounds(id + 82, 30, 2, 18, Interface);
		setBounds(id + 83, 30, 2, 19, Interface);
		setBounds(id + 84, 30, 2, 20, Interface);
		setBounds(id + 85, 30, 2, 21, Interface);
		setBounds(id + 86, 30, 2, 22, Interface);
		setBounds(id + 87, 30, 2, 23, Interface);

		/**
		 * Tab Container
		 */
		setBounds(id + 88, 38, 7, 24, Interface);
		setBounds(id + 89, 38, 7, 25, Interface);
		setBounds(id + 90, 38, 7, 26, Interface);
		setBounds(id + 91, 38, 7, 27, Interface);
		setBounds(id + 92, 38, 7, 28, Interface);
		setBounds(id + 93, 38, 7, 29, Interface);
		setBounds(id + 94, 38, 7, 30, Interface);
		setBounds(id + 95, 38, 7, 31, Interface);
		setBounds(id + 96, 38, 7, 32, Interface);

		Interface.width = 480;
		Interface.height = 208;
	}

	public static RSInterface addDuelContainer(int id, boolean type) {
		RSInterface widget = addInterface(id);
		widget.parentID = id;
		widget.type = 2;
		widget.width = 3;
		widget.height = 10;
		widget.sprites = new Sprite[20];
		widget.spritesX = new int[20];
		widget.spritesY = new int[20];
		widget.invSpritePadX = 8;
		widget.invSpritePadY = 6;
		widget.container = new int[30];
		widget.containerSizes = new int[30];
		widget.allowSwapItems = false;

		if (type) {
			widget.actions = new String[] { "Withdraw 1", "Withdraw 5", "Withdraw 10", "Withdraw All", "Withdraw X" };
		} else {
			widget.actions = new String[5];
		}

		return widget;
	}

	public static RSInterface addBankContainer(int id) {
		RSInterface widget = addInterface(id);
		widget.parentID = id;
		widget.type = 2;
		widget.contentType = 206;
		widget.width = 10;
		widget.height = 35;
		widget.sprites = new Sprite[20];
		widget.spritesX = new int[20];
		widget.spritesY = new int[20];
		widget.invSpritePadX = 12;
		widget.invSpritePadY = 12;
		widget.container = new int[350];
		widget.containerSizes = new int[350];
		widget.allowSwapItems = true;
		widget.actions = new String[] { "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X", null,
				"Withdraw-All but one" };
		return widget;
	}

	private static void itemsOnDeath(TextDrawingArea[] tda) {
		RSInterface component = addInterface(17100);

		addSprite(17101, 93);
		addHoverButton(17102, 91, 17, 17, "Close Window", 250, 10601, 3);
		addHoveredButton(10601, 92, 17, 17, 10602);
		addText(17103, "Items Kept On Death", 0xff981f, 0, true, 0, tda, 2);
		addText(17104, "Items you will keep on death (if not skulled):", 0xff981f, 0, true, 0, tda, 2);
		addText(17105, "Items you will lose on death (if not skulled):", 0xff981f, 0, true, 0, tda, 2);
		addText(17106, "Information", 0xff981f, 0, true, 0, tda, 1);
		addText(17107, "Max items kept on death:", 0xff981f, 0, true, 0, tda, 1);
		addText(17108, "~ 3 ~", 0xffcc33, 0, true, 0, tda, 1);

		component.scrollMax = 0;
		component.isMouseoverTriggered = false;
		component.children = new int[12];
		component.childX = new int[12];
		component.childY = new int[12];
		component.children[0] = 17101;
		component.childX[0] = 7;
		component.childY[0] = 8;
		component.children[1] = 17102;
		component.childX[1] = 480;
		component.childY[1] = 17;
		component.children[2] = 17103;
		component.childX[2] = 185;
		component.childY[2] = 18;
		component.children[3] = 17104;
		component.childX[3] = 22;
		component.childY[3] = 50;
		component.children[4] = 17105;
		component.childX[4] = 22;
		component.childY[4] = 110;
		component.children[5] = 17106;
		component.childX[5] = 347;
		component.childY[5] = 47;
		component.children[6] = 17107;
		component.childX[6] = 349;
		component.childY[6] = 270;
		component.children[7] = 17108;
		component.childX[7] = 398;
		component.childY[7] = 298;
		component.children[8] = 17115;
		component.childX[8] = 348;
		component.childY[8] = 64;
		component.children[9] = 10494;
		component.childX[9] = 26;
		component.childY[9] = 74;
		component.children[10] = 10600;
		component.childX[10] = 26;
		component.childY[10] = 133;
		component.children[11] = 10601;
		component.childX[11] = 480;
		component.childY[11] = 17;
		component = interfaceCache[10494];
		component.invSpritePadX = 6;
		component.invSpritePadY = 5;
		component = interfaceCache[10600];
		component.invSpritePadX = 6;
		component.invSpritePadY = 5;
	}

	private static void itemsOnDeathDATA(TextDrawingArea[] tda) {
		RSInterface component = addInterface(17115);

		addText(17109, "", 0xff981f, 0, false, 0, tda, 0);
		addText(17110, "The normal amount of", 0xff981f, 0, false, 0, tda, 0);
		addText(17111, "items kept is three.", 0xff981f, 0, false, 0, tda, 0);
		addText(17112, "", 0xff981f, 0, false, 0, tda, 0);
		addText(17113, "If you are skulled,", 0xff981f, 0, false, 0, tda, 0);
		addText(17114, "you will lose all your", 0xff981f, 0, false, 0, tda, 0);
		addText(17117, "items, unless an item", 0xff981f, 0, false, 0, tda, 0);
		addText(17118, "protecting prayer is", 0xff981f, 0, false, 0, tda, 0);
		addText(17119, "used.", 0xff981f, 0, false, 0, tda, 0);
		addText(17120, "", 0xff981f, 0, false, 0, tda, 0);
		addText(17121, "Item protecting prayers", 0xff981f, 0, false, 0, tda, 0);
		addText(17122, "will allow you to keep", 0xff981f, 0, false, 0, tda, 0);
		addText(17123, "one extra item.", 0xff981f, 0, false, 0, tda, 0);
		addText(17124, "", 0xff981f, 0, false, 0, tda, 0);
		addText(17125, "The items kept are", 0xff981f, 0, false, 0, tda, 0);
		addText(17126, "selected by the server", 0xff981f, 0, false, 0, tda, 0);
		addText(17127, "and include the most", 0xff981f, 0, false, 0, tda, 0);
		addText(17128, "expensive items you're", 0xff981f, 0, false, 0, tda, 0);
		addText(17129, "carrying.", 0xff981f, 0, false, 0, tda, 0);
		addText(17130, "", 0xff981f, 0, false, 0, tda, 0);

		component.parentID = 17115;
		component.id = 17115;
		component.type = 0;
		component.atActionType = 0;
		component.contentType = 0;
		component.width = 130;
		component.height = 197;
		component.opacity = 0;
		component.hoverType = -1;
		component.scrollMax = 280;
		component.children = new int[20];
		component.childX = new int[20];
		component.childY = new int[20];
		component.children[0] = 17109;
		component.childX[0] = 0;
		component.childY[0] = 0;
		component.children[1] = 17110;
		component.childX[1] = 0;
		component.childY[1] = 12;
		component.children[2] = 17111;
		component.childX[2] = 0;
		component.childY[2] = 24;
		component.children[3] = 17112;
		component.childX[3] = 0;
		component.childY[3] = 36;
		component.children[4] = 17113;
		component.childX[4] = 0;
		component.childY[4] = 48;
		component.children[5] = 17114;
		component.childX[5] = 0;
		component.childY[5] = 60;
		component.children[6] = 17117;
		component.childX[6] = 0;
		component.childY[6] = 72;
		component.children[7] = 17118;
		component.childX[7] = 0;
		component.childY[7] = 84;
		component.children[8] = 17119;
		component.childX[8] = 0;
		component.childY[8] = 96;
		component.children[9] = 17120;
		component.childX[9] = 0;
		component.childY[9] = 108;
		component.children[10] = 17121;
		component.childX[10] = 0;
		component.childY[10] = 120;
		component.children[11] = 17122;
		component.childX[11] = 0;
		component.childY[11] = 132;
		component.children[12] = 17123;
		component.childX[12] = 0;
		component.childY[12] = 144;
		component.children[13] = 17124;
		component.childX[13] = 0;
		component.childY[13] = 156;
		component.children[14] = 17125;
		component.childX[14] = 0;
		component.childY[14] = 168;
		component.children[15] = 17126;
		component.childX[15] = 0;
		component.childY[15] = 180;
		component.children[16] = 17127;
		component.childX[16] = 0;
		component.childY[16] = 192;
		component.children[17] = 17128;
		component.childX[17] = 0;
		component.childY[17] = 204;
		component.children[18] = 17129;
		component.childX[18] = 0;
		component.childY[18] = 216;
		component.children[19] = 17130;
		component.childX[19] = 0;
		component.childY[19] = 228;
	}

	public static void quickPrayers(TextDrawingArea[] TDA) {
		int frame = 0;
		RSInterface tab = addTabInterface(17200);

		setChildren(58, tab);//
		setBounds(5632, 5, 8 + 20, frame++, tab);
		setBounds(5633, 44, 8 + 20, frame++, tab);
		setBounds(5634, 79, 11 + 20, frame++, tab);
		setBounds(19813, 116, 10 + 20, frame++, tab);
		setBounds(19815, 153, 9 + 20, frame++, tab);
		setBounds(5635, 5, 48 + 20, frame++, tab);
		setBounds(5636, 44, 47 + 20, frame++, tab);
		setBounds(5637, 79, 49 + 20, frame++, tab);
		setBounds(5638, 116, 50 + 20, frame++, tab);
		setBounds(5639, 154, 50 + 20, frame++, tab);
		setBounds(5640, 4, 84 + 20, frame++, tab);
		setBounds(19817, 44, 87 + 20, frame++, tab);
		setBounds(19820, 81, 85 + 20, frame++, tab);
		setBounds(5641, 117, 85 + 20, frame++, tab);
		setBounds(5642, 156, 87 + 20, frame++, tab);
		setBounds(5643, 5, 125 + 20, frame++, tab);
		setBounds(5644, 43, 124 + 20, frame++, tab);
		setBounds(13984, 83, 124 + 20, frame++, tab);
		setBounds(5645, 115, 121 + 20, frame++, tab);
		setBounds(19822, 154, 124 + 20, frame++, tab);
		setBounds(19824, 5, 160 + 20, frame++, tab);
		setBounds(5649, 41, 158 + 20, frame++, tab);
		setBounds(5647, 79, 163 + 20, frame++, tab);
		setBounds(5648, 116, 158 + 20, frame++, tab);
		setBounds(19826, 161, 160 + 20, frame++, tab);
		setBounds(19828, 4, 207 + 12, frame++, tab);

		setBounds(17229, 0, 25, frame++, tab);// Faded backing
		setBounds(17201, 0, 22, frame++, tab);// Split
		setBounds(17201, 0, 237, frame++, tab);// Split

		setBounds(17202, 5 - 3, 8 + 17, frame++, tab);
		setBounds(17203, 44 - 3, 8 + 17, frame++, tab);
		setBounds(17204, 79 - 3, 8 + 17, frame++, tab);
		setBounds(17205, 116 - 3, 8 + 17, frame++, tab);
		setBounds(17206, 153 - 3, 8 + 17, frame++, tab);
		setBounds(17207, 5 - 3, 48 + 17, frame++, tab);
		setBounds(17208, 44 - 3, 48 + 17, frame++, tab);
		setBounds(17209, 79 - 3, 48 + 17, frame++, tab);
		setBounds(17210, 116 - 3, 48 + 17, frame++, tab);
		setBounds(17211, 153 - 3, 48 + 17, frame++, tab);
		setBounds(17212, 5 - 3, 85 + 17, frame++, tab);
		setBounds(17213, 44 - 3, 85 + 17, frame++, tab);
		setBounds(17214, 79 - 3, 85 + 17, frame++, tab);
		setBounds(17215, 116 - 3, 85 + 17, frame++, tab);
		setBounds(17216, 153 - 3, 85 + 17, frame++, tab);
		setBounds(17217, 5 - 3, 124 + 17, frame++, tab);
		setBounds(17218, 44 - 3, 124 + 17, frame++, tab);
		setBounds(17219, 79 - 3, 124 + 17, frame++, tab);
		setBounds(17220, 116 - 3, 124 + 17, frame++, tab);
		setBounds(17221, 153 - 3, 124 + 17, frame++, tab);
		setBounds(17222, 5 - 3, 160 + 17, frame++, tab);
		setBounds(17223, 44 - 3, 160 + 17, frame++, tab);
		setBounds(17224, 79 - 3, 160 + 17, frame++, tab);
		setBounds(17225, 116 - 3, 160 + 17, frame++, tab);
		setBounds(17226, 153 - 3, 160 + 17, frame++, tab);
		setBounds(17227, 4 - 3, 207 + 4, frame++, tab);

		setBounds(17230, 5, 5, frame++, tab);// text
		setBounds(17231, 0, 237, frame++, tab);// confirm
		setBounds(17232, 0, 237, frame++, tab);// Confirm hover
	}

	private static void clanChatTab(TextDrawingArea[] tda) {
		RSInterface component = addTabInterface(18128);
		addHoverButton(18129, 506, 72, 32, "Join/Leave a Clan", 550, 18130, 1);
		addHoveredButton(18130, 507, 72, 32, 18131);
		addHoverButton(18132, 506, 72, 32, "Open Clan Setup", -1, 18133, 5);
		addHoveredButton(18133, 507, 72, 32, 18134);
		addButton(18250, 489, "Toggle lootshare");
		addText(18135, "Join Chat", tda, 0, 0xff9b00, 1, true);
		addText(18136, "Clan Setup", tda, 0, 0xff9b00, 1, true);
		addSprite(18137, 490);
		addText(18138, "Clan Chat", tda, 1, 0xff9b00, 1, true);
		addText(18139, "Talking in: Not in chat", tda, 0, 0xff9b00, 0, true);
		addText(18140, "Owner: None", tda, 0, 0xff9b00, 0, true);
		addHoverButton(18343, 508, 23, 23, "Kick", 677, 18344, 1);
		addHoveredButton(18344, 509, 23, 23, 18345);
		component.totalChildren(14);
		component.child(0, 18137, 0, 62);
		component.child(1, 18143, 0, 62);
		component.child(2, 18129, 15, 226);
		component.child(3, 18130, 15, 226);
		component.child(4, 18132, 103, 226);
		component.child(5, 18133, 103, 226);
		component.child(6, 18135, 51, 237);
		component.child(7, 18136, 139, 237);
		component.child(8, 18138, 95, 1);
		component.child(9, 18139, 10, 23);
		component.child(10, 18140, 25, 38);
		component.child(11, 18250, 145, 5);
		component.child(12, 18343, 147, 35);
		component.child(13, 18344, 147, 35);

		/* Text area */
		RSInterface list = addTabInterface(18143);
		list.totalChildren(200);

		for (int i = 38144; i <= 38244; i++) {
			addSpriteIcon(i, 491);
		}

		for (int i = 18144; i <= 18244; i++) {
			newText(i, "", tda, 0, 0xffffff, 0, true, true);
		}

		for (int id = 38144, i = 100; (id <= 38243) && (i <= 199); id++, i++) {
			list.children[i] = id;
			list.childX[i] = 5;

			for (int id2 = 38144, i2 = 101; (id2 <= 38243) && (i2 <= 199); id2++, i2++) {
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}

		for (int id = 18144, i = 0; (id <= 18243) && (i <= 99); id++, i++) {
			list.children[i] = id;
			list.childX[i] = 18;

			for (int id2 = 18144, i2 = 1; (id2 <= 18243) && (i2 <= 99); id2++, i2++) {
				list.childY[0] = 2;
				list.childY[i2] = list.childY[i2 - 1] + 14;
			}
		}

		list.height = 158;
		list.width = 174;
		list.scrollMax = 1405;
	}

	private static void editClan(TextDrawingArea[] tda) {
		RSInterface component = addTabInterface(40172);
		addSprite(47251, 501);
		addHoverButton(47252, 502, 150, 35, "Set name", 22222, 47253, 1);
		addHoveredButton(47253, 503, 150, 35, 47254);
		addHoverButton(47255, 502, 150, 35, "Anyone", -1, 47256, 1);
		addHoveredButton(47256, 503, 150, 35, 47257);
		addHoverButton(48000, -1, 150, 35, "Only me", -1, 47999, 1);
		addHoverButton(48001, -1, 150, 35, "General+", -1, 47999, 1);
		addHoverButton(48002, -1, 150, 35, "Captain+", -1, 47999, 1);
		addHoverButton(48003, -1, 150, 35, "Lieutenant+", -1, 47999, 1);
		addHoverButton(48004, -1, 150, 35, "Sergeant+", -1, 47999, 1);
		addHoverButton(48005, -1, 150, 35, "Corporal+", -1, 47999, 1);
		addHoverButton(48006, -1, 150, 35, "Recruit+", -1, 47999, 1);
		addHoverButton(48007, -1, 150, 35, "Any friends", -1, 47999, 1);
		addHoverButton(47258, 502, 150, 35, "Anyone", -1, 47259, 1);
		addHoveredButton(47259, 503, 150, 35, 17260);
		addHoverButton(48010, -1, 150, 35, "Only me", -1, 47999, 1);
		addHoverButton(48011, -1, 150, 35, "General+", -1, 47999, 1);
		addHoverButton(48012, -1, 150, 35, "Captain+", -1, 47999, 1);
		addHoverButton(48013, -1, 150, 35, "Lieutenant+", -1, 47999, 1);
		addHoverButton(48014, -1, 150, 35, "Sergeant+", -1, 47999, 1);
		addHoverButton(48015, -1, 150, 35, "Corporal+", -1, 47999, 1);
		addHoverButton(48016, -1, 150, 35, "Recruit+", -1, 47999, 1);
		addHoverButton(48017, -1, 150, 35, "Any friends", -1, 47999, 1);
		addHoverButton(47261, 502, 150, 35, "Only me", -1, 47262, 1);
		addHoveredButton(47262, 503, 150, 35, 47263);
		addHoverButton(48021, -1, 150, 35, "General+", -1, 47999, 1);
		addHoverButton(48022, -1, 150, 35, "Captain+", -1, 47999, 1);
		addHoverButton(48023, -1, 150, 35, "Lieutenant+", -1, 47999, 1);
		addHoverButton(48024, -1, 150, 35, "Sergeant+", -1, 47999, 1);
		addHoverButton(48025, -1, 150, 35, "Corporal+", -1, 47999, 1);
		addHoverButton(48026, -1, 150, 35, "Recruit+", -1, 47999, 1);
		addHoverButton(47267, 505, 16, 16, "Close", -1, 47268, 1);
		addHoveredButton(47268, 504, 16, 16, 47269);
		addText(47800, "Clan name:", tda, 0, 0xff981f, 0, true);
		addText(47801, "Who can enter chat?", tda, 0, 0xff981f, 0, true);
		addText(47812, "Who can talk on chat?", tda, 0, 0xff981f, 0, true);
		addText(47813, "Who can kick on chat?", tda, 0, 0xff981f, 0, true);
		addText(47814, "Alex", tda, 0, 0xffffff, 1, true);
		addText(47815, "Anyone", tda, 0, 0xffffff, 1, true);
		addText(47816, "Anyone", tda, 0, 0xffffff, 1, true);
		addText(47817, "Only me", tda, 0, 0xffffff, 1, true);
		component.totalChildren(42);
		component.child(0, 47251, 15, 15);
		component.child(1, 47252, 25, 47 + 20);
		component.child(2, 47253, 25, 47 + 20);
		component.child(3, 47267, 476, 23);
		component.child(4, 47268, 476, 23);
		component.child(5, 48000, 25, 87 + 25);
		component.child(6, 48001, 25, 87 + 25);
		component.child(7, 48002, 25, 87 + 25);
		component.child(8, 48003, 25, 87 + 25);
		component.child(9, 48004, 25, 87 + 25);
		component.child(10, 48005, 25, 87 + 25);
		component.child(11, 48006, 25, 87 + 25);
		component.child(12, 48007, 25, 87 + 25);
		component.child(13, 47255, 25, 87 + 25);
		component.child(14, 47256, 25, 87 + 25);
		component.child(15, 48010, 25, 128 + 30);
		component.child(16, 48011, 25, 128 + 30);
		component.child(17, 48012, 25, 128 + 30);
		component.child(18, 48013, 25, 128 + 30);
		component.child(19, 48014, 25, 128 + 30);
		component.child(20, 48015, 25, 128 + 30);
		component.child(21, 48016, 25, 128 + 30);
		component.child(22, 48017, 25, 128 + 30);
		component.child(23, 47258, 25, 128 + 30);
		component.child(24, 47259, 25, 128 + 30);
		component.child(25, 48021, 25, 168 + 35);
		component.child(26, 48022, 25, 168 + 35);
		component.child(27, 48023, 25, 168 + 35);
		component.child(28, 48024, 25, 168 + 35);
		component.child(29, 48025, 25, 168 + 35);
		component.child(30, 48026, 25, 168 + 35);
		component.child(31, 47261, 25, 168 + 35);
		component.child(32, 47262, 25, 168 + 35);
		component.child(33, 47800, 73, 54 + 20);
		component.child(34, 47801, 53, 95 + 25);
		component.child(35, 47812, 53, 136 + 30);
		component.child(36, 47813, 53, 177 + 35);
		component.child(37, 47814, 100, 54 + 20 + 12);
		component.child(38, 47815, 100, 95 + 25 + 12);
		component.child(39, 47816, 100, 136 + 30 + 12);
		component.child(40, 47817, 100, 177 + 35 + 12);
		component.child(41, 44000, 0, 94);
		component = addTabInterface(44000);
		component.width = 474;
		component.height = 213;
		component.scrollMax = 2030;

		for (int i = 44001; i <= 44200; i++) {
			addText(i, "", tda, 1, 0xffff64, 0, true, true);
		}

		for (int i = 44801; i <= 45000; i++) {
			addHoverText(i, "Not ranked", "[CC]", tda, 1, 0xffffff, 0, false, 150);
		}

		component.totalChildren(400);
		int Child = 0;
		int Y = 3;

		for (int i = 44001; i <= 44200; i++) {
			component.child(Child, i, 204, Y);
			Child++;
			Y += 13;
		}

		Y = 3;

		for (int i = 44801; i <= 45000; i++) {
			component.child(Child, i, 343, Y);
			Child++;
			Y += 13;
		}
	}

	private static void addHoverText(int id, String text, String tooltip, TextDrawingArea tda[], int idx, int color, int textAlignment, boolean aBoolean268, int width) {
		RSInterface widget = addInterface(id);
		widget.id = id;
		widget.parentID = id;
		widget.type = 4;
		widget.atActionType = 1;
		widget.width = width;
		widget.height = 11;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.textAlign = textAlignment;
		widget.textShadow = aBoolean268;
		widget.textDrawingAreas = tda[idx];
		widget.setText(text);
		widget.aString228 = "";
		widget.textColor = color;
		widget.anInt219 = 0;
		widget.textColorSelected = 0xFFFFFF;
		widget.anInt239 = 0;
		widget.tooltip = tooltip;
	}

	public int transparency;

	private static void addTransparentSprite(int id, int spriteId, String spriteName, int transparency) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.transparency = (byte) transparency;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static void Pestpanel(TextDrawingArea[] tda) {
		RSInterface RSinterface = addInterface(21119);
		addText(21120, "What", 0x999999, false, true, 52, tda, 1);
		addText(21121, "What", 0x33cc00, false, true, 52, tda, 1);
		addText(21122, "(Need 5 to 25 players)", 0xFFcc33, false, true, 52, tda, 1);
		addText(21123, "Points", 0x33ccff, false, true, 52, tda, 1);
		int last = 4;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21120, 15, 12, 0, RSinterface);
		setBounds(21121, 15, 30, 1, RSinterface);
		setBounds(21122, 15, 48, 2, RSinterface);
		setBounds(21123, 15, 66, 3, RSinterface);
	}

	public static void Pestpanel2(TextDrawingArea[] tda) {
		RSInterface RSinterface = addInterface(21100);
		addSprite(21101, 0, "Pest Control/PEST1");
		addSprite(21102, 1, "Pest Control/PEST1");
		addSprite(21103, 2, "Pest Control/PEST1");
		addSprite(21104, 3, "Pest Control/PEST1");
		addSprite(21105, 4, "Pest Control/PEST1");
		addSprite(21106, 5, "Pest Control/PEST1");
		addText(21107, "", 0xCC00CC, false, true, 52, tda, 1);
		addText(21108, "", 0x0000FF, false, true, 52, tda, 1);
		addText(21109, "", 0xFFFF44, false, true, 52, tda, 1);
		addText(21110, "", 0xCC0000, false, true, 52, tda, 1);
		addText(21111, "250", 0x99FF33, false, true, 52, tda, 1);// w purp
		addText(21112, "250", 0x99FF33, false, true, 52, tda, 1);// e blue
		addText(21113, "250", 0x99FF33, false, true, 52, tda, 1);// se yel
		addText(21114, "250", 0x99FF33, false, true, 52, tda, 1);// sw red
		addText(21115, "200", 0x99FF33, false, true, 52, tda, 1);// attacks
		addText(21116, "0", 0x99FF33, false, true, 52, tda, 1);// knights hp
		addText(21117, "Time Remaining:", 0xFFFFFF, false, true, 52, tda, 0);
		addText(21118, "", 0xFFFFFF, false, true, 52, tda, 0);
		int last = 18;
		RSinterface.children = new int[last];
		RSinterface.childX = new int[last];
		RSinterface.childY = new int[last];
		setBounds(21101, 361, 26, 0, RSinterface);
		setBounds(21102, 396, 26, 1, RSinterface);
		setBounds(21103, 436, 26, 2, RSinterface);
		setBounds(21104, 474, 26, 3, RSinterface);
		setBounds(21105, 3, 21, 4, RSinterface);
		setBounds(21106, 3, 50, 5, RSinterface);
		setBounds(21107, 371, 60, 6, RSinterface);
		setBounds(21108, 409, 60, 7, RSinterface);
		setBounds(21109, 443, 60, 8, RSinterface);
		setBounds(21110, 479, 60, 9, RSinterface);
		setBounds(21111, 362, 10, 10, RSinterface);
		setBounds(21112, 398, 10, 11, RSinterface);
		setBounds(21113, 436, 10, 12, RSinterface);
		setBounds(21114, 475, 10, 13, RSinterface);
		setBounds(21115, 32, 32, 14, RSinterface);
		setBounds(21116, 32, 62, 15, RSinterface);
		setBounds(21117, 8, 88, 16, RSinterface);
		setBounds(21118, 87, 88, 17, RSinterface);
	}

	public String hoverText;

	public static void addHoverBox(int id, int ParentID, String text, String text2, int configId, int configFrame) {
		RSInterface rsi = addTabInterface(id);
		rsi.id = id;
		rsi.parentID = ParentID;
		rsi.type = 8;
		rsi.aString228 = text;
		rsi.message = text2;
		rsi.valueCompareType = new int[1];
		rsi.requiredValues = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.requiredValues[0] = configId;
		rsi.instructions = new int[1][3];
		rsi.instructions[0][0] = 5;
		rsi.instructions[0][1] = configFrame;
		rsi.instructions[0][2] = 0;
	}

	private static void addText(int id, String s, int textColor, int textAlignment, boolean textShadow, int hoverType, int fontId) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.id = id;
		widget.type = 4;
		widget.atActionType = 0;
		widget.width = 0;
		widget.height = 0;
		widget.contentType = 0;
		widget.hoverType = hoverType;
		widget.textAlign = textAlignment;
		widget.textShadow = textShadow;
		widget.textDrawingAreas = fonts[fontId];
		widget.setText(s);
		widget.textColor = textColor;
	}

	private static void newText(int id, String text, TextDrawingArea tda[], int idx, int color, int textAlignment, boolean shadow, boolean clickable) {
		RSInterface component = addTabInterface(id);
		component.parentID = id;
		component.id = id;
		component.type = 4;
		component.atActionType = 0;
		component.width = 0;
		component.height = 11;
		component.contentType = 0;
		component.opacity = 0;
		component.hoverType = -1;
		component.textAlign = textAlignment;
		component.textShadow = shadow;
		component.textDrawingAreas = tda[idx];
		component.setText(text);
		component.aString228 = "";
		component.textColor = color;
		component.anInt219 = 0;
		component.textColorSelected = 0;
		component.anInt239 = 0;
	}

	private static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, int textAlignment, boolean shadow, boolean cc) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.id = id;
		widget.type = 4;
		widget.atActionType = 0;
		widget.width = 0;
		widget.height = 11;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.textAlign = textAlignment;
		widget.textShadow = shadow;
		widget.textDrawingAreas = tda[idx];
		widget.setText(text);
		widget.aString228 = "";
		widget.textColor = color;
		widget.anInt219 = 0;
		widget.textColorSelected = 0;
		widget.anInt239 = 0;
	}

	public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean center, boolean shadow) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.textAlign = center ? 1 : 0;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.message = text;
		tab.aString228 = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.textColorSelected = 0;
		tab.anInt239 = 0;
	}

	private static void addText(int i, String s, int k, int textAlignment, boolean m, int a, TextDrawingArea[] TDA, int j) {
		RSInterface widget = addInterface(i);
		widget.parentID = i;
		widget.id = i;
		widget.type = 4;
		widget.atActionType = 0;
		widget.width = 0;
		widget.height = 0;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = a;
		widget.textAlign = textAlignment;
		widget.textShadow = m;
		widget.textDrawingAreas = TDA[j];
		widget.setText(s);
		widget.aString228 = "";
		widget.textColor = k;
	}

	public static void addClickableText(int id, String text, String tooltip, TextDrawingArea tda[], int idx, int color, boolean center, boolean shadow, int width) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.id = id;
		tab.type = 4;
		tab.atActionType = 1;
		tab.width = width;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.textAlign = center ? 1 : 0;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.message = text;
		tab.aString228 = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.textColorSelected = 0xffffff;
		tab.anInt239 = 0;
		tab.tooltip = tooltip;
	}

	public static void addClickableText(int id, String text, String tooltip, TextDrawingArea tda[], int idx, int color, int colorSelected, int width, int height) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.id = id;
		widget.type = 4;
		widget.atActionType = 1;
		widget.width = width;
		widget.height = height;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.textAlign = 0;
		widget.textShadow = true;
		widget.textDrawingAreas = tda[idx];
		widget.setText(text);
		widget.tooltip = tooltip;
		widget.textColorSelected = colorSelected;
		widget.textColor = color;
	}

	public void setText(String text) {
		this.message = text;
	}

	public static void addText(int i, String text, int textColor, boolean centerText, boolean shadowText, int hoverType, TextDrawingArea[] TDA, int j) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.parentID = i;
		RSInterface.id = i;
		RSInterface.type = 4;
		RSInterface.atActionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = hoverType;
		RSInterface.textAlign = centerText ? 1 : 0;
		RSInterface.textShadow = shadowText;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.message = text;
		RSInterface.aString228 = "";
		RSInterface.textColor = textColor;
	}

	private static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, int textAlignment, boolean shadow) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.id = id;
		widget.type = 4;
		widget.atActionType = 0;
		widget.width = 0;
		widget.height = 11;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.textAlign = textAlignment;
		widget.textShadow = shadow;
		widget.textDrawingAreas = tda[idx];
		widget.setText(text);
		widget.aString228 = "";
		widget.textColor = color;
		widget.anInt219 = 0;
		widget.textColorSelected = 0;
		widget.anInt239 = 0;
	}

	public static void addButton(int id, int sid, String spriteName, String tooltip, int w, int h) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(sid, spriteName);
		tab.sprite2 = imageLoader(sid, spriteName);
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}

	public static void addButton(int id, int sid, String tooltip, int mOver, int atAction, int width, int height) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = atAction;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = mOver;
		widget.sprite1 = Client.cacheSprite[sid];
		widget.sprite2 = Client.cacheSprite[sid];
		widget.width = width;
		widget.height = height;
		widget.tooltip = tooltip;
		widget.inventoryHover = true;
	}

	private static void addButton(int id, int sid, String tooltip) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 1;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;
		widget.sprite1 = Client.cacheSprite[sid];
		widget.sprite2 = Client.cacheSprite[sid];
		widget.width = widget.sprite1.myWidth;
		widget.height = widget.sprite2.myHeight;
		widget.tooltip = tooltip;
	}

	private static void addButton(int id, Sprite sprite, String tooltip) {

		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 1;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;
		widget.sprite1 = sprite;
		widget.sprite2 = sprite;
		widget.width = widget.sprite1.myWidth;
		widget.height = widget.sprite2.myHeight;
		widget.tooltip = tooltip;
	}

	public static void addSkillButton(int i, int spriteID, String name, String skillGuide, int AT, int hoverID, TextDrawingArea[] TDA, String skillName) {
		RSInterface widget = addInterface(i);
		widget.id = i;
		widget.parentID = i;
		widget.type = 5;
		widget.atActionType = AT;
		widget.contentType = 0;
		widget.opacity = 0;
		widget.hoverType = hoverID;
		widget.sprite1 = method207(spriteID, aClass44, name);
		widget.sprite2 = method207(spriteID, aClass44, name);
		widget.width = 60;
		widget.height = 27;
		widget.tooltip = "@whi@View @or1@" + skillGuide + " @whi@Guide";
		widget = addInterface(hoverID);
		widget.isMouseoverTriggered = true;
		widget.type = 0;
		widget.atActionType = 0;
		widget.hoverType = -1;
		widget.width = 512;
		widget.height = 334;
		widget.parentID = hoverID;
		widget.id = hoverID;
		addBox(hoverID + 1, 0, false, 0x000000, skillName + " Lvl:\nCurrent XP:\nNext Lvl:\nRemainder:");
		setChildren(1, widget);
		setBounds(hoverID + 1, 0, 0, 0, widget);
	}

	private static void addConfigButton(int id, int parentId, int bID, int bID2, int width, int height, String tooltip, int configID, int aT, int configFrame) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = parentId;
		widget.id = id;
		widget.type = 5;
		widget.atActionType = aT;
		widget.contentType = 0;
		widget.width = width;
		widget.height = height;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.requiredValues = new int[1];
		widget.valueCompareType = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = configID;
		widget.instructions = new int[1][3];
		widget.instructions[0][0] = 5;
		widget.instructions[0][1] = configFrame;
		widget.instructions[0][2] = 0;
		widget.sprite1 = bID < 0 ? null : Client.cacheSprite[bID];
		widget.sprite2 = bID2 < 0 ? null : Client.cacheSprite[bID2];
		widget.tooltip = tooltip;
	}

	public static void addConfigButton(int ID, int pID, int bID, int bID2, String bName, int width, int height, String tT, int configID, int aT, int configFrame) {
		RSInterface Tab = addTabInterface(ID);
		Tab.parentID = pID;
		Tab.id = ID;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.instructions = new int[1][3];
		Tab.instructions[0][0] = 5;
		Tab.instructions[0][1] = configFrame;
		Tab.instructions[0][2] = 0;
		Tab.sprite1 = imageLoader(bID, bName);
		Tab.sprite2 = imageLoader(bID2, bName);
		Tab.tooltip = tT;
	}

	public static void addSprite(int id, int spriteId, String spriteName) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
	}

	private static void addSprite(int id, Sprite sprite) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;
		widget.sprite1 = sprite;
		widget.sprite2 = sprite;
		widget.width = 512;
		widget.height = 334;
	}

	public static void addConfigSprite(int id, int spriteId, int spriteId2, int state, int config) {
		RSInterface widget = addTabInterface(id);
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.width = 512;
		widget.height = 334;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.requiredValues = new int[1];
		widget.valueCompareType = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = state;
		widget.instructions = new int[1][3];
		widget.instructions[0][0] = 5;
		widget.instructions[0][1] = config;
		widget.instructions[0][2] = 0;
		widget.sprite1 = spriteId < 0 ? null : Client.cacheSprite[spriteId];
		widget.sprite2 = spriteId2 < 0 ? null : Client.cacheSprite[spriteId2];
	}

	public static void addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver, int aT) {// hoverable
																																					// button
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.sprite1 = imageLoader(j, imageName);
		tab.sprite2 = imageLoader(j, imageName);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {// hoverable
																									// button
		RSInterface tab = addTabInterface(i);
		tab.parentID = i;
		tab.id = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.isMouseoverTriggered = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage(IMAGEID, j, j, imageName);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, int j, int k, String name) {
		RSInterface tab = addTabInterface(i);
		tab.id = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(j, name);
		tab.sprite2 = imageLoader(k, name);
	}

	public static void addTransparentSprite(int id, int spriteId, String spriteName) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.sprite1 = imageLoader(spriteId, spriteName);
		tab.sprite2 = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	public static RSInterface addScreenInterface(int id) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;
		tab.parentID = id;
		tab.type = 0;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = (byte) 0;
		tab.hoverType = 0;
		return tab;
	}

	public static RSInterface addTabInterface(int id) {
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = id;// 250
		tab.parentID = id;// 236
		tab.type = 0;// 262
		tab.atActionType = 0;// 217
		tab.contentType = 0;
		tab.width = 512;// 220
		tab.height = 700;// 267
		tab.opacity = (byte) 0;
		tab.hoverType = -1;// Int 230
		return tab;
	}

	private static Sprite imageLoader(int i, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(s + " " + i);
			aMRUNodes_238.removeFromCache(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	private Model method206(int i, int j) {
		Model model = (Model) aMRUNodes_264.insertFromCache((i << 16) + j);
		if (model != null)
			return model;
		if (i == 1)
			model = Model.method462(j);
		if (i == 2)
			model = NPCDefinition.forID(j).method160();
		if (i == 3)
			model = Client.myPlayer.method453();
		if (i == 4)
			model = ItemDefinition.forID(j).method202(50);
		if (i == 5)
			model = null;
		if (model != null)
			aMRUNodes_264.removeFromCache(model, (i << 16) + j);
		return model;
	}

	private static Sprite method207(int i, StreamLoader streamLoader, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		Sprite sprite = (Sprite) aMRUNodes_238.insertFromCache(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(streamLoader, s, i);
			aMRUNodes_238.removeFromCache(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;// was parameter
		int j = 5;// was parameter
		if (flag)
			return;
		aMRUNodes_264.unlinkAll();
		if (model != null && j != 4)
			aMRUNodes_264.removeFromCache(model, (j << 16) + i);
	}

	public Model method209(int j, int k, boolean flag) {
		Model model;
		if (flag)
			model = method206(anInt255, anInt256);
		else
			model = method206(anInt233, mediaID);
		if (model == null)
			return null;
		if (k == -1 && j == -1 && model.anIntArray1640 == null)
			return model;
		Model model_1 = new Model(true, FrameReader.notNulled(k) & FrameReader.notNulled(j), false, model);
		if (k != -1 || j != -1)
			model_1.method469();
		if (k != -1)
			model_1.method470(k);
		if (j != -1)
			model_1.method470(j);
		model_1.method479(64, 768, -50, -10, -50, true);
		return model_1;
	}

	public static void addLunarSprite(int i, int j, String name) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.sprite1 = imageLoader(j, name);
		RSInterface.width = 500;
		RSInterface.height = 500;
		RSInterface.tooltip = "";
	}

	public static void drawRune(int i, int id, String runeName) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.sprite1 = imageLoader(id, "Lunar/RUNE");
		RSInterface.width = 500;
		RSInterface.height = 500;
	}

	public static void addRuneText(int ID, int runeAmount, int RuneID, TextDrawingArea[] font) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 4;
		rsInterface.atActionType = 0;
		rsInterface.contentType = 0;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.opacity = 0;
		rsInterface.hoverType = -1;
		rsInterface.valueCompareType = new int[1];
		rsInterface.requiredValues = new int[1];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = runeAmount;
		rsInterface.instructions = new int[1][4];
		rsInterface.instructions[0][0] = 4;
		rsInterface.instructions[0][1] = 3214;
		rsInterface.instructions[0][2] = RuneID;
		rsInterface.instructions[0][3] = 0;
		rsInterface.textAlign = 1;
		rsInterface.textDrawingAreas = font[0];
		rsInterface.textShadow = true;
		rsInterface.message = "%1/" + runeAmount + "";
		rsInterface.aString228 = "";
		rsInterface.textColor = 12582912;
		rsInterface.anInt219 = 49152;
	}

	public static void homeTeleport() {
		RSInterface RSInterface = addInterface(30000);
		RSInterface.tooltip = "Cast @gre@Lunar Home Teleport";
		RSInterface.id = 30000;
		RSInterface.parentID = 30000;
		RSInterface.type = 5;
		RSInterface.atActionType = 5;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 30001;
		RSInterface.sprite1 = imageLoader(1, "Lunar/SPRITE");
		RSInterface.width = 20;
		RSInterface.height = 20;
		RSInterface Int = addInterface(30001);
		Int.isMouseoverTriggered = true;
		Int.hoverType = -1;
		setChildren(1, Int);
		addLunarSprite(30002, 0, "SPRITE");
		setBounds(30002, 0, 0, 0, Int);
	}

	public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1, int ra2, int rune1, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast On";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[3];
		rsInterface.requiredValues = new int[3];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = lvl;
		rsInterface.instructions = new int[3][];
		rsInterface.instructions[0] = new int[4];
		rsInterface.instructions[0][0] = 4;
		rsInterface.instructions[0][1] = 3214;
		rsInterface.instructions[0][2] = r1;
		rsInterface.instructions[0][3] = 0;
		rsInterface.instructions[1] = new int[4];
		rsInterface.instructions[1][0] = 4;
		rsInterface.instructions[1][1] = 3214;
		rsInterface.instructions[1][2] = r2;
		rsInterface.instructions[1][3] = 0;
		rsInterface.instructions[2] = new int[3];
		rsInterface.instructions[2][0] = 1;
		rsInterface.instructions[2][1] = 6;
		rsInterface.instructions[2][2] = 0;
		rsInterface.sprite2 = imageLoader(sid, "Lunar/LUNARON");
		rsInterface.sprite1 = imageLoader(sid, "Lunar/LUNAROFF");
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(7, INT);
		addLunarSprite(ID + 2, 0, "Lunar/BOX");
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 37, 35, 3, INT);// Rune
		setBounds(rune1, 112, 35, 4, INT);// Rune
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 50, 66, 5, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 123, 66, 6, INT);
	}

	public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.instructions = new int[4][];
		rsInterface.instructions[0] = new int[4];
		rsInterface.instructions[0][0] = 4;
		rsInterface.instructions[0][1] = 3214;
		rsInterface.instructions[0][2] = r1;
		rsInterface.instructions[0][3] = 0;
		rsInterface.instructions[1] = new int[4];
		rsInterface.instructions[1][0] = 4;
		rsInterface.instructions[1][1] = 3214;
		rsInterface.instructions[1][2] = r2;
		rsInterface.instructions[1][3] = 0;
		rsInterface.instructions[2] = new int[4];
		rsInterface.instructions[2][0] = 4;
		rsInterface.instructions[2][1] = 3214;
		rsInterface.instructions[2][2] = r3;
		rsInterface.instructions[2][3] = 0;
		rsInterface.instructions[3] = new int[3];
		rsInterface.instructions[3][0] = 1;
		rsInterface.instructions[3][1] = 6;
		rsInterface.instructions[3][2] = 0;
		rsInterface.sprite2 = imageLoader(sid, "Lunar/LUNARON");
		rsInterface.sprite1 = imageLoader(sid, "Lunar/LUNAROFF");
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 0, "Lunar/BOX");
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 14, 35, 3, INT);
		setBounds(rune1, 74, 35, 4, INT);
		setBounds(rune2, 130, 35, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 66, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 66, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 66, 8, INT);
	}

	public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.instructions = new int[4][];
		rsInterface.instructions[0] = new int[4];
		rsInterface.instructions[0][0] = 4;
		rsInterface.instructions[0][1] = 3214;
		rsInterface.instructions[0][2] = r1;
		rsInterface.instructions[0][3] = 0;
		rsInterface.instructions[1] = new int[4];
		rsInterface.instructions[1][0] = 4;
		rsInterface.instructions[1][1] = 3214;
		rsInterface.instructions[1][2] = r2;
		rsInterface.instructions[1][3] = 0;
		rsInterface.instructions[2] = new int[4];
		rsInterface.instructions[2][0] = 4;
		rsInterface.instructions[2][1] = 3214;
		rsInterface.instructions[2][2] = r3;
		rsInterface.instructions[2][3] = 0;
		rsInterface.instructions[3] = new int[3];
		rsInterface.instructions[3][0] = 1;
		rsInterface.instructions[3][1] = 6;
		rsInterface.instructions[3][2] = 0;
		rsInterface.sprite2 = imageLoader(sid, "Lunar/LUNARON");
		rsInterface.sprite1 = imageLoader(sid, "Lunar/LUNAROFF");
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 1, "Lunar/BOX");
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 21, 2, INT);
		setBounds(30016, 14, 48, 3, INT);
		setBounds(rune1, 74, 48, 4, INT);
		setBounds(rune2, 130, 48, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, INT);
	}

	public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.id = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast @gre@" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.instructions = new int[4][];
		rsInterface.instructions[0] = new int[4];
		rsInterface.instructions[0][0] = 4;
		rsInterface.instructions[0][1] = 3214;
		rsInterface.instructions[0][2] = r1;
		rsInterface.instructions[0][3] = 0;
		rsInterface.instructions[1] = new int[4];
		rsInterface.instructions[1][0] = 4;
		rsInterface.instructions[1][1] = 3214;
		rsInterface.instructions[1][2] = r2;
		rsInterface.instructions[1][3] = 0;
		rsInterface.instructions[2] = new int[4];
		rsInterface.instructions[2][0] = 4;
		rsInterface.instructions[2][1] = 3214;
		rsInterface.instructions[2][2] = r3;
		rsInterface.instructions[2][3] = 0;
		rsInterface.instructions[3] = new int[3];
		rsInterface.instructions[3][0] = 1;
		rsInterface.instructions[3][1] = 6;
		rsInterface.instructions[3][2] = 0;
		rsInterface.sprite2 = imageLoader(sid, "Lunar/LUNARON");
		rsInterface.sprite1 = imageLoader(sid, "Lunar/LUNAROFF");
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 2, "Lunar/BOX");
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 34, 2, INT);
		setBounds(30016, 14, 61, 3, INT);
		setBounds(rune1, 74, 61, 4, INT);
		setBounds(rune2, 130, 61, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 92, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 92, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 92, 8, INT);
	}

	public static void setChildren(int total, RSInterface i) {
		i.children = new int[total];
		i.childX = new int[total];
		i.childY = new int[total];
	}

	public static void configureLunar(TextDrawingArea[] TDA) {
		homeTeleport();
		constructLunar();
		drawRune(30003, 1, "Fire");
		drawRune(30004, 2, "Water");
		drawRune(30005, 3, "Air");
		drawRune(30006, 4, "Earth");
		drawRune(30007, 5, "Mind");
		drawRune(30008, 6, "Body");
		drawRune(30009, 7, "Death");
		drawRune(30010, 8, "Nature");
		drawRune(30011, 9, "Chaos");
		drawRune(30012, 10, "Law");
		drawRune(30013, 11, "Cosmic");
		drawRune(30014, 12, "Blood");
		drawRune(30015, 13, "Soul");
		drawRune(30016, 14, "Astral");
		addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004, 64, "Bake Pie", "Bake pies without a stove", TDA, 0, 16, 2);
		addLunar2RunesSmallBox(30025, 9075, 557, 0, 7, 30006, 65, "Cure Plant", "Cure disease on farming patch", TDA, 1, 4, 2);
		addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65, "Monster Examine", "Detect the combat statistics of a\\nmonster", TDA, 2, 2, 2);
		addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005, 66, "NPC Contact", "Speak with varied NPCs", TDA, 3, 0, 2);
		addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006, 67, "Cure Other", "Cure poisoned players", TDA, 4, 8, 2);
		addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003, 67, "Humidify", "Fills certain vessels with water", TDA, 5, 0, 5);
		addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006, 68, "Moonclan Teleport", "Teleports you to moonclan island", TDA, 6, 0, 5);
		addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69, "Tele Group Moonclan", "Teleports players to Moonclan\\nisland", TDA, 7, 0, 5);
		addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006, 70, "Ourania Teleport", "Teleports you to ourania rune altar", TDA, 8, 0, 5);
		addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012, 70, "Cure Me", "Cures Poison", TDA, 9, 0, 5);
		addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit", "Get a kit of hunting gear", TDA, 10, 0, 5);
		addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004, 71, "Waterbirth Teleport", "Teleports you to Waterbirth island", TDA, 11, 0, 5);
		addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72, "Tele Group Waterbirth", "Teleports players to Waterbirth\\nisland", TDA, 12, 0, 5);
		addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012, 73, "Cure Group", "Cures Poison on players", TDA, 13, 0, 5);
		addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74, "Stat Spy", "Cast on another player to see their\\nskill levels", TDA, 14, 8, 2);
		addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74, "Barbarian Teleport", "Teleports you to the Barbarian\\noutpost", TDA, 15, 0, 5);
		addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75, "Tele Group Barbarian", "Teleports players to the Barbarian\\noutpost", TDA, 16, 0, 5);
		addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005, 76, "Superglass Make", "Make glass without a furnace", TDA, 17, 16, 2);
		addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004, 77, "Khazard Teleport", "Teleports you to Port khazard", TDA, 18, 0, 5);
		addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004, 78, "Tele Group Khazard", "Teleports players to Port khazard", TDA, 19, 0, 5);
		addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78, "Dream", "Take a rest and restore hitpoints 3\\n times faster", TDA, 20, 0, 5);
		addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004, 79, "String Jewellery", "String amulets without wool", TDA, 21, 0, 5);
		addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004, 80, "Stat Restore Pot\\nShare", "Share a potion with up to 4 nearby\\nplayers", TDA, 22, 0, 5);
		addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004, 81, "Magic Imbue", "Combine runes without a talisman", TDA, 23, 0, 5);
		addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82, "Fertile Soil", "Fertilise a farming patch with super\\ncompost", TDA, 24, 4, 2);
		addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83, "Boost Potion Share", "Shares a potion with up to 4 nearby\\nplayers", TDA, 25, 0, 5);
		addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004, 84, "Fishing Guild Teleport", "Teleports you to the fishing guild", TDA, 26, 0, 5);
		addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004, 85, "Tele Group Fishing Guild", "Teleports players to the Fishing\\nGuild", TDA, 27, 0, 5);
		addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010, 85, "Plank Make", "Turn Logs into planks", TDA, 28, 16, 5);
		addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004, 86, "Catherby Teleport", "Teleports you to Catherby", TDA, 29, 0, 5);
		addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004, 87, "Tele Group Catherby", "Teleports players to Catherby", TDA, 30, 0, 5);
		addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004, 88, "Ice Plateau Teleport", "Teleports you to Ice Plateau", TDA, 31, 0, 5);
		addLunar3RunesLargeBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89, "Tele Group Ice Plateau", "Teleports players to Ice Plateau", TDA, 32, 0, 5);
		addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90, "Energy Transfer", "Spend HP and SA energy to\\n give another SA and run energy", TDA, 33, 8, 2);
		addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91, "Heal Other", "Transfer up to 75% of hitpoints\\n to another player", TDA, 34, 8, 2);
		addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92, "Vengeance Other", "Allows another player to rebound\\ndamage to an opponent", TDA, 35, 8, 2);
		addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006, 93, "Vengeance", "Rebound damage to an opponent", TDA, 36, 0, 5);
		addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94, "Heal Group", "Transfer up to 75% of hitpoints\\n to a group", TDA, 37, 0, 5);
		addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95, "Spellbook Swap", "Change to another spellbook for 1\\nspell cast", TDA, 38, 0, 5);
	}

	public static void constructLunar() {
		RSInterface Interface = addTabInterface(29999);
		setChildren(80, Interface);
		setBounds(30000, 11, 10, 0, Interface);
		setBounds(30017, 40, 9, 1, Interface);
		setBounds(30025, 71, 12, 2, Interface);
		setBounds(30032, 103, 10, 3, Interface);
		setBounds(30040, 135, 12, 4, Interface);
		setBounds(30048, 165, 10, 5, Interface);
		setBounds(30056, 8, 38, 6, Interface);
		setBounds(30064, 39, 39, 7, Interface);
		setBounds(30075, 71, 39, 8, Interface);
		setBounds(30083, 103, 39, 9, Interface);
		setBounds(30091, 135, 39, 10, Interface);
		setBounds(30099, 165, 37, 11, Interface);
		setBounds(30106, 12, 68, 12, Interface);
		setBounds(30114, 42, 68, 13, Interface);
		setBounds(30122, 71, 68, 14, Interface);
		setBounds(30130, 103, 68, 15, Interface);
		setBounds(30138, 135, 68, 16, Interface);
		setBounds(30146, 165, 68, 17, Interface);
		setBounds(30154, 14, 97, 18, Interface);
		setBounds(30162, 42, 97, 19, Interface);
		setBounds(30170, 71, 97, 20, Interface);
		setBounds(30178, 101, 97, 21, Interface);
		setBounds(30186, 135, 98, 22, Interface);
		setBounds(30194, 168, 98, 23, Interface);
		setBounds(30202, 11, 125, 24, Interface);
		setBounds(30210, 42, 124, 25, Interface);
		setBounds(30218, 74, 125, 26, Interface);
		setBounds(30226, 103, 125, 27, Interface);
		setBounds(30234, 135, 125, 28, Interface);
		setBounds(30242, 164, 126, 29, Interface);
		setBounds(30250, 10, 155, 30, Interface);
		setBounds(30258, 42, 155, 31, Interface);
		setBounds(30266, 71, 155, 32, Interface);
		setBounds(30274, 103, 155, 33, Interface);
		setBounds(30282, 136, 155, 34, Interface);
		setBounds(30290, 165, 155, 35, Interface);
		setBounds(30298, 13, 185, 36, Interface);
		setBounds(30306, 42, 185, 37, Interface);
		setBounds(30314, 71, 184, 38, Interface);
		setBounds(30322, 104, 184, 39, Interface);
		setBounds(30001, 6, 184, 40, Interface);// hover
		setBounds(30018, 5, 176, 41, Interface);// hover
		setBounds(30026, 5, 176, 42, Interface);// hover
		setBounds(30033, 5, 163, 43, Interface);// hover
		setBounds(30041, 5, 176, 44, Interface);// hover
		setBounds(30049, 5, 176, 45, Interface);// hover
		setBounds(30057, 5, 176, 46, Interface);// hover
		setBounds(30065, 5, 176, 47, Interface);// hover
		setBounds(30076, 5, 163, 48, Interface);// hover
		setBounds(30084, 5, 176, 49, Interface);// hover
		setBounds(30092, 5, 176, 50, Interface);// hover
		setBounds(30100, 5, 176, 51, Interface);// hover
		setBounds(30107, 5, 176, 52, Interface);// hover
		setBounds(30115, 5, 163, 53, Interface);// hover
		setBounds(30123, 5, 176, 54, Interface);// hover
		setBounds(30131, 5, 163, 55, Interface);// hover
		setBounds(30139, 5, 163, 56, Interface);// hover
		setBounds(30147, 5, 163, 57, Interface);// hover
		setBounds(30155, 5, 176, 58, Interface);// hover
		setBounds(30163, 5, 176, 59, Interface);// hover
		setBounds(30171, 5, 176, 60, Interface);// hover
		setBounds(30179, 5, 163, 61, Interface);// hover
		setBounds(30187, 5, 176, 62, Interface);// hover
		setBounds(30195, 5, 149, 63, Interface);// hover
		setBounds(30203, 5, 176, 64, Interface);// hover
		setBounds(30211, 5, 163, 65, Interface);// hover
		setBounds(30219, 5, 163, 66, Interface);// hover
		setBounds(30227, 5, 176, 67, Interface);// hover
		setBounds(30235, 5, 149, 68, Interface);// hover
		setBounds(30243, 5, 176, 69, Interface);// hover
		setBounds(30251, 5, 5, 70, Interface);// hover
		setBounds(30259, 5, 5, 71, Interface);// hover
		setBounds(30267, 5, 5, 72, Interface);// hover
		setBounds(30275, 5, 5, 73, Interface);// hover
		setBounds(30283, 5, 5, 74, Interface);// hover
		setBounds(30291, 5, 5, 75, Interface);// hover
		setBounds(30299, 5, 5, 76, Interface);// hover
		setBounds(30307, 5, 5, 77, Interface);// hover
		setBounds(30323, 5, 5, 78, Interface);// hover
		setBounds(30315, 5, 5, 79, Interface);// hover
	}

	public static void setBounds(int ID, int X, int Y, int frame, RSInterface RSinterface) {
		RSinterface.children[frame] = ID;
		RSinterface.childX[frame] = X;
		RSinterface.childY[frame] = Y;
	}

	private static void setToDefault(int id) {
		interfaceCache[id] = new RSInterface();
	}

	public static void addButton(int i, int j, String name, int W, int H, String S, int AT) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.id = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = AT;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.sprite1 = imageLoader(j, name);
		RSInterface.sprite2 = imageLoader(j, name);
		RSInterface.width = W;
		RSInterface.height = H;
		RSInterface.tooltip = S;
	}

	private static void addSprite(int id, int spriteId) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;

		if (spriteId != -1) {
			widget.sprite1 = Client.cacheSprite[spriteId];
			widget.sprite2 = Client.cacheSprite[spriteId];
		}

		widget.width = 512;
		widget.height = 334;
	}

	public static void addAdvancedSprite(int id, int spriteId) {
		RSInterface widget = addInterface(id);
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.hoverType = 52;
		widget.sprite1 = Client.cacheSprite[spriteId];
		widget.sprite2 = Client.cacheSprite[spriteId];
		widget.drawsTransparent = true;
		widget.opacity = 64;
		widget.width = 512;
		widget.height = 334;
	}

	private static void addHoverButton(int id, int spriteId, int width, int height, String text, int contentType, int hoverOver, int actionType) {
		RSInterface widget = addTabInterface(id);
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = actionType;
		widget.contentType = contentType;
		widget.opacity = 0;
		widget.hoverType = hoverOver;

		if (spriteId >= 0) {
			widget.sprite1 = Client.cacheSprite[spriteId];
			widget.sprite2 = Client.cacheSprite[spriteId];
		}

		widget.width = width;
		widget.height = height;
		widget.tooltip = text;
	}

	private static void addHoveredButton(int i, int j, int w, int h, int IMAGEID) {
		RSInterface widget = addTabInterface(i);
		widget.parentID = i;
		widget.id = i;
		widget.type = 0;
		widget.atActionType = 0;
		widget.width = w;
		widget.height = h;
		widget.isMouseoverTriggered = true;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.scrollMax = 0;
		addHoverImage(IMAGEID, j, j);
		widget.totalChildren(1);
		widget.child(0, IMAGEID, 0, 0);
	}

	private static void addHoverImage(int i, int enabledSprite, int disabledSprite) {
		RSInterface widget = addTabInterface(i);
		widget.id = i;
		widget.parentID = i;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.width = 512;
		widget.height = 334;
		widget.opacity = 0;
		widget.hoverType = 52;
		widget.sprite1 = Client.cacheSprite[enabledSprite];
		widget.sprite2 = Client.cacheSprite[disabledSprite];
	}

	private static void addFamiliarHead(int interfaceID, int width, int height, int zoom) {
		RSInterface widget = addTabInterface(interfaceID);
		widget.type = 6;
		widget.anInt233 = 2;
		widget.mediaID = 4000;
		widget.modelZoom = zoom;
		widget.modelRotation1 = 40;
		widget.modelRotation2 = 1800;
		widget.height = height;
		widget.width = width;
	}

	private static void addFamiliarHead2(int id, int w, int h, int zoom) {
		RSInterface widget = addInterface(id);
		widget.type = 6;
		widget.anInt233 = 2;
		widget.mediaID = 4000;
		widget.modelZoom = zoom;
		widget.modelRotation1 = 40;
		widget.modelRotation2 = 1900;
		widget.height = h;
		widget.width = w;
	}

	public static void addFamiliarAttack(int id, int parentId, int bID, int bID2, int width, int height, String tooltip, int configID, int aT, int configFrame) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = parentId;
		widget.id = id;
		widget.type = 5;
		widget.spellUsableOn = 10;
		widget.selectedActionName = "Attack";
		widget.spellName = "";
		widget.atActionType = aT;
		widget.contentType = 0;
		widget.width = width;
		widget.height = height;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.requiredValues = new int[1];
		widget.valueCompareType = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = configID;
		widget.instructions = new int[1][3];
		widget.instructions[0][0] = 5;
		widget.instructions[0][1] = configFrame;
		widget.instructions[0][2] = 0;
		widget.sprite1 = Client.cacheSprite[bID];
		widget.sprite2 = Client.cacheSprite[bID2];
		widget.tooltip = tooltip;
	}

	public static void addBox(int id, int byte1, boolean filled, int color, String text) {
		RSInterface widget = addInterface(id);
		widget.id = id;
		widget.parentID = id;
		widget.type = 9;
		widget.opacity = (byte) byte1;
		widget.aBoolean227 = filled;
		widget.hoverType = -1;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.textColor = color;
		widget.setText(text);
	}

	private static int getSkillIndex(String name) {
		for (int i = 0; i < SkillConstants.SKILL_NAMES.length; i++) {
			if (name.equals(SkillConstants.SKILL_NAMES[i])) {
				return i;
			}
		}

		return -1;
	}

	public static void addBobStorage(int index) {
		RSInterface widget = interfaceCache[index] = new RSInterface();
		widget.actions = new String[10];
		widget.spritesX = new int[20];
		widget.containerSizes = new int[30];
		widget.container = new int[30];
		widget.spritesY = new int[20];
		widget.children = new int[0];
		widget.childX = new int[0];
		widget.childY = new int[0];
		widget.actions[0] = "Take 1";
		widget.actions[1] = "Take 5";
		widget.actions[2] = "Take 10";
		widget.actions[3] = "Take All";
		widget.actions[4] = "Take X";
		widget.textAlign = 1;
		widget.aBoolean227 = false;
		widget.deletesTargetSlot = false;
		widget.usableItemInterface = false;
		widget.isInventoryInterface = false;
		widget.allowSwapItems = true;
		widget.textShadow = false;
		widget.invSpritePadX = 22;
		widget.invSpritePadY = 21;
		widget.height = 5;
		widget.width = 6;
		widget.parentID = 2702;
		widget.id = 2700;
		widget.type = 2;
	}

	public static void addHover(int i, int aT, int cT, int hoverid, int sId, int W, int H, String tip) {
		RSInterface widget = addTabInterface(i);
		widget.id = i;
		widget.parentID = i;
		widget.type = 5;
		widget.atActionType = aT;
		widget.contentType = cT;
		widget.hoverType = hoverid;
		widget.sprite1 = Client.cacheSprite[sId];
		widget.sprite2 = Client.cacheSprite[sId];
		widget.width = W;
		widget.height = H;
		widget.tooltip = tip;
	}

	public static void addHovered(int id, int spriteId, int w, int h, int IMAGEID) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.id = id;
		widget.type = 0;
		widget.atActionType = 0;
		widget.width = w;
		widget.height = h;
		widget.aBoolean227 = true;
		widget.hoverType = -1;
		addSprite(IMAGEID, spriteId);
		setChildren(1, widget);
		setBounds(IMAGEID, 0, 0, 0, widget);
	}

	public static RSInterface addPouches(int id, int grey_sprite_id, int colorfull_sprite_id, int index) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 1;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;
		widget.summonReq = summoningLevelRequirements[index];
		widget.sprite1 = Client.cacheSprite[grey_sprite_id];
		widget.sprite2 = Client.cacheSprite[colorfull_sprite_id];
		widget.actions = new String[] { "Infuse 1 " + pouchNames[index] + " Pouchs",
				"Infuse 5 " + pouchNames[index] + " Pouchs", "Infuse 10 " + pouchNames[index] + " Pouchs",
				"Infuse All " + pouchNames[index] + " Pouchs", "Infuse X " + pouchNames[index] + " Pouchs" };
		widget.width = 46;
		widget.height = 50;
		return widget;
	}

	public static RSInterface addScrolls(int id, int grey_sprite_id, int colorfull_sprite_id, int index) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 1;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;
		widget.summonReq = summoningLevelRequirements[index];
		widget.sprite1 = Client.cacheSprite[grey_sprite_id];
		widget.sprite2 = Client.cacheSprite[colorfull_sprite_id];
		widget.actions = new String[5];
		widget.actions[0] = "Infuse 1 " + scrollNames[index] + " Scroll";
		widget.actions[1] = "Infuse 5 " + scrollNames[index] + " Scrolls";
		widget.actions[2] = "Infuse 10 " + scrollNames[index] + " Scrolls";
		widget.actions[3] = "Infuse All " + scrollNames[index] + " Scrolls";
		widget.actions[4] = "Infuse X " + scrollNames[index] + " Scrolls";
		widget.width = 46;
		widget.height = 50;
		return widget;
	}

	private static void addSpriteIcon(int id, int spriteId1) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.opacity = (byte) 0;
		widget.hoverType = 52;
		widget.sprite1 = widget.sprite2 = Client.cacheSprite[spriteId1];
		widget.width = 512;
		widget.height = 334;
	}

	private static void addChar(int id) {
		RSInterface widget = interfaceCache[id] = new RSInterface();
		widget.id = id;
		widget.parentID = id;
		widget.type = 6;
		widget.atActionType = 0;
		widget.contentType = 328;
		widget.width = 180;
		widget.height = 190;
		widget.opacity = 0;
		widget.hoverType = 0;
		widget.modelZoom = 560;
		widget.modelRotation1 = 30;
		widget.modelRotation2 = 0;
		widget.anInt257 = -1;
		widget.anInt258 = -1;
	}

	public static void addHoverConfigButton(int id, int hoverOver, int disabledID, int enabledID, int width, int height, String tooltip, int configId, int configFrame) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.id = id;
		widget.type = 5;
		widget.atActionType = 1;
		widget.contentType = 0;
		widget.width = width;
		widget.height = height;
		widget.opacity = 0;
		widget.hoverType = hoverOver;
		widget.valueCompareType = new int[1];
		widget.requiredValues = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = configId;
		widget.instructions = new int[1][3];
		widget.instructions[0][0] = 5;
		widget.instructions[0][1] = configFrame;
		widget.instructions[0][2] = 0;
		widget.sprite1 = Client.cacheSprite[disabledID];
		widget.sprite2 = Client.cacheSprite[enabledID];
		widget.tooltip = tooltip;
	}

	public static void addHoveredConfigButton(RSInterface original, int ID, int IMAGEID, int disabledID, int enabledID) {
		RSInterface widget = addTabInterface(ID);
		widget.parentID = original.id;
		widget.id = ID;
		widget.type = 0;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.width = original.width;
		widget.height = original.height;
		widget.opacity = 0;
		widget.hoverType = -1;
		RSInterface hover = addInterface(IMAGEID);
		hover.type = 5;
		hover.width = original.width;
		hover.height = original.height;
		hover.valueCompareType = original.valueCompareType;
		hover.requiredValues = original.requiredValues;
		hover.instructions = original.instructions;
		hover.sprite1 = Client.cacheSprite[disabledID];
		hover.sprite2 = Client.cacheSprite[enabledID];
		widget.totalChildren(1);
		setBounds(IMAGEID, 0, 0, 0, widget);
		widget.tooltip = original.tooltip;
		widget.isMouseoverTriggered = true;
	}

	public static void addTabButton(int parentId, int childId, String spriteLoc, String toolTip, int configFrame) {
		RSInterface widget = addTabInterface(parentId);
		widget.type = 0;
		widget.width = 48;
		widget.height = 38;
		widget.totalChildren(childId != 50034 ? 2 : 1);
		RSInterface tab = addTabInterface(childId);
		tab.parentID = parentId;
		tab.id = childId;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.width = 48;
		tab.height = 38;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 0;
		tab.requiredValues[0] = 1;
		tab.instructions = new int[1][3];
		tab.instructions[0][0] = 5;
		tab.instructions[0][1] = configFrame;
		tab.instructions[0][2] = 0;
		tab.sprite1 = Client.cacheSprite[531];
		tab.sprite2 = Client.cacheSprite[532];
		tab.tooltip = toolTip;

		if (childId != 50034) {
			addButton(childId + 200, 0, "", "Collapse", 48, 38);
			setBounds(childId + 200, 0, 0, 0, widget);
		}

		setBounds(childId, 0, 0, childId != 50034 ? 1 : 0, widget);
	}

	public static void addTabItem(int id) {
		RSInterface widget = addInterface(id);
		widget.parentID = 50000;
		widget.type = 2;
		widget.contentType = 206;
		widget.width = 1;
		widget.height = 1;
		widget.sprites = new Sprite[20];
		widget.spritesX = new int[20];
		widget.spritesY = new int[20];
		widget.invSpritePadX = 12;
		widget.invSpritePadY = 12;
		widget.container = new int[1];
		widget.containerSizes = new int[1];
		widget.actions = null;
	}

	public static void addTabSprite(int id, int childId, int spriteId, int width, int height) {
		RSInterface widget = addInterface(id);
		widget.width = width;
		widget.height = height;
		widget.totalChildren(1);
		RSInterface tab = addInterface(childId);
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.sprite1 = Client.cacheSprite[spriteId];
		tab.width = width;
		tab.height = height;
		setBounds(childId, 0, 0, 0, widget);
	}

	public String getText() {
		return message;
	}

}