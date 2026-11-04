package com.koios.game.model.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.koios.Constants;
import com.koios.GameEngine;
import com.koios.GameService;
import com.koios.game.content.NewcomersHandler;
import com.koios.game.content.PrivateMessaging;
import com.koios.game.content.RunEnergy;
import com.koios.game.content.YellTags;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.impl.RecipeForDisasterAreaHandler;
import com.koios.game.content.bankpins.BankPinHandler;
import com.koios.game.content.bountyhunter.BountyHunter;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.DwarfMultiCannon;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.prayer.PrayerSet;
import com.koios.game.content.combat.prayer.QuickPrayers;
import com.koios.game.content.combat.special.SpecialHandler;
import com.koios.game.content.degrading.ArmourDegrading;
import com.koios.game.content.dialogue.DialogueManager;
import com.koios.game.content.dialogue.impl.edgeville.MysteriousCapeDialogue;
import com.koios.game.content.following.Following;
import com.koios.game.content.following.PlayerFollowing;
import com.koios.game.content.friendschat.Friendschat;
import com.koios.game.content.friendschat.FriendschatCommons;
import com.koios.game.content.lottery.LotteryManager;
import com.koios.game.content.minigames.MinigameVariables;
import com.koios.game.content.minigames.MinigamesManager;
import com.koios.game.content.minigames.duel.DuelArenaVariables;
import com.koios.game.content.music.MusicTab;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillInterfaces;
import com.koios.game.content.skill.farming.Allotments;
import com.koios.game.content.skill.farming.Bushes;
import com.koios.game.content.skill.farming.Compost;
import com.koios.game.content.skill.farming.Farming;
import com.koios.game.content.skill.farming.Flowers;
import com.koios.game.content.skill.farming.FruitTree;
import com.koios.game.content.skill.farming.Herbs;
import com.koios.game.content.skill.farming.Hops;
import com.koios.game.content.skill.farming.SpecialPlantOne;
import com.koios.game.content.skill.farming.SpecialPlantTwo;
import com.koios.game.content.skill.farming.WoodTrees;
import com.koios.game.content.skill.herblore.Potions;
import com.koios.game.content.skill.slayer.Slayer;
import com.koios.game.content.skill.summoning.Summoning;
import com.koios.game.content.skill.thieving.Thieving;
import com.koios.game.content.titles.Title;
import com.koios.game.content.wilderness.Killstreaks;
import com.koios.game.def.ItemDefinition;
import com.koios.game.def.MobDefinition;
import com.koios.game.def.WeaponDefinitions;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemAssistant;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.InterfaceContainerListener;
import com.koios.game.model.item.container.Inventory;
import com.koios.game.model.item.container.Container.Type;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.bank.BankField;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.item.container.equip.EquipmentContainerListener;
import com.koios.game.model.item.container.equip.WeaponContainerListener;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.movement.MovementHandler;
import com.koios.game.model.movement.PlayerMovementHandler;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Privileges.DonatorPrivilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.game.model.player.controller.Controller;
import com.koios.game.model.player.controller.ControllerManager;
import com.koios.game.model.player.task.MiasmicEffectTask;
import com.koios.game.model.player.task.PlaygroundAltarTask;
import com.koios.game.model.player.task.SkullTimerTask;
import com.koios.game.model.player.task.StaffOfLightTask;
import com.koios.network.GamePacket;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.impl.ResetCameraPacket;
import com.koios.network.out.impl.ResetFriendsChatTab;
import com.koios.network.out.impl.SendChatOptionsPacket;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendEnergyPacket;
import com.koios.network.out.impl.SendMapRegionPacket;
import com.koios.network.out.impl.SendOptionPacket;
import com.koios.network.out.impl.SendPlanePacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.network.out.impl.SendSkillGoalPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;
import com.koios.task.impl.PlayerDeathTask;
import com.koios.util.ItemIdentifiers;
import com.koios.util.StringUtil;
import com.koios.util.TimeUtil;

/**
 * Represents a player entity
 *
 * @author Timothy Howard <Arithium>
 */
public class Player extends EntityCharacter {

	/**
	 * A comparator to sort collection of {@link Player} by username.
	 */
	public static final Comparator<Player> PLAYER_COMPARATOR_BY_USERNAME = (a, b) -> {
		String an = a.getName();
		String bn = b.getName();
		return an.compareTo(bn);
	};

	/**
	 * Player object instances
	 */
	private final Allotments allotments = new Allotments(this);
	private final Appearance appearance = new Appearance();
	private final ArmourDegrading armourDegrading = new ArmourDegrading(this);
	private final BankField bankField = new BankField(this);
	private final BankPinHandler bankPinHandler = new BankPinHandler(this);
	private final Container bank = new Container(Container.Type.ALWAYS_STACK, Bank.SIZE);
	private final BountyHunter bounty_hunter = new BountyHunter(this);
	private final DwarfMultiCannon cannon = new DwarfMultiCannon(this);
	private final Bushes bushes = new Bushes(this);
	public Container chestDeposit = new Container(Type.STANDARD, 8);
	private final Compost compost = new Compost(this);
	private final DialogueManager dialogue = new DialogueManager(this);
	private final DialogueHandler dialogueHandler = new DialogueHandler(this);
	private final Slayer slayer = new Slayer(this);
	private final DuelArenaVariables duel = new DuelArenaVariables(this);
	private final Flowers flowers = new Flowers(this);
	private final Following following = new PlayerFollowing(this);
	private final FruitTree fruitTree = new FruitTree(this);
	private final Herbs herbs = new Herbs(this);
	private final Hops hops = new Hops(this);
	private final InterfaceState interfaceState = new InterfaceState(this);
	private final ItemAssistant itemAssistant = new ItemAssistant(this);
	private final Killstreaks killstreaks = new Killstreaks(this);
	private final MinigameVariables minigame = new MinigameVariables(this);
	private final MovementHandler movement_handler = new PlayerMovementHandler(this);
	private final Potions potions = new Potions(this);
	private final RequestManager requestManager = new RequestManager(this);
	private final RunEnergy runEnergy = new RunEnergy(this);
	private final SkillInterfaces skillInterfaces = new SkillInterfaces(this);
	private final Skill skills = new Skill(this);
	private final SpecialPlantOne specialPlantOne = new SpecialPlantOne(this);
	private final SpecialPlantTwo specialPlantTwo = new SpecialPlantTwo(this);
	private final Summoning summoning = new Summoning(this);
	public Container temporaryFamiliarInventory = new Container(Container.Type.STANDARD, 30);
	private final Thieving thieving = new Thieving(this);
	private final WoodTrees trees = new WoodTrees(this);
	private final YellTags yellTag = new YellTags(this);
	private final QuickPrayers quickPrayers = new QuickPrayers(this);
	private final PrayerSet prayers = new PrayerSet(this);

	/**
	 * Our players current controller
	 */
	private Controller controller = ControllerManager.DEFAULT_CONTROLLER;
	/**
	 * Player variables
	 */
	public boolean allowSaveRequests = true;
	public String[] atPlayerActions = new String[5];
	public ArrayList<String> attackedPlayers = new ArrayList<>();
	private final HashMap<String, Object> attributes = new HashMap<>();
	public boolean autocasting = false;
	public boolean avasDeviceEffect = false;
	public int awaitResponseDestroyItemId = 0;
	public int awaitResponseDestroyItemSlot = -1;
	public int rfdGloveCount = 0;
	public boolean specialEvent = false;
	public boolean specialGraphics = false;
	public int barbarianDamage = 0;
	public boolean berserkerActive = false;
	private int bounties;
	public int bowSpecShot, clickMobType, clickObjectType, objectId, objectX, objectY, objectXOffset, objectYOffset, objectDistance;
	public long cachedPlaytime = 0;
	public boolean canChangeAppearance = false;
	public int cannonBalls, cannonBaseX, cannonBaseY, cannonBaseH, rotation;
	public boolean[] canUseReducingSpell = { true, true, true, true, true, true };
	private byte[] chatText;
	private int chatTextColor = 0;
	private int chatTextEffects = 0;
	private byte chatTextSize = 0;
	private final Session session;
	public Stopwatch countdown = Stopwatch.createUnstarted();
	public double crossbowDamage;
	private final Location currentRegion = new Location(0, 0, 0);
	public int currentShop = -1;
	private SkillConstructor currentSkillAction;
	private GamePacketBuilder cachedBlock;
	public long protSummDelay;
	private int votePoints;
	public boolean dbowSpec = false;
	public int deathCount;
	public int dfsCount;
	public int dialogueAction = 0;
	long diceDelay;
	public int diceId = 15084;
	private boolean didTeleport = false;
	private boolean placementNeeded;
	private int dir1 = -1, dir2 = -1;
	public boolean disabledGlobalChat = false;
	private boolean disconnected = false;
	public boolean doubleHit, usingSpecial, spellCasting, castingMagic;
	private boolean dropItems = false;
	public long elapsedMinutes = 800_000; // Starting at a value higher than 0
	public boolean enabledXPLock;
	public boolean enterChallenge = false;
	private int fightPitPoints;
	public final long[] friends = new long[200];
	public String friendschatName = null;
	public int giantPouchPureEssence, giantPouchRuneEssence;
	private int[] godwards_kc = new int[4];
	public boolean hasMultiSign = false;
	public int headIcon = -1;
	private int hiddenBrother, barrowsKillCount;
	public boolean ignoreDefence, magicDef, ignoreHit;
	public final long[] ignores = new long[100];
	private boolean inattackable = true;
	private int index = -1;
	private boolean instantKill = false;
	private boolean invulnerable = false;
	private boolean isActive = false;
	public boolean isChatSplit;
	public boolean isMining = false;
	public boolean isMoving, walkingToItem;
	public boolean isPlantingMithrilSeed;
	public boolean[] isSkilling = new boolean[25];
	public boolean isSkulled = false;
	private boolean isTrading;
	public int killCount;
	private final boolean[] killedBrothers = new boolean[6];
	public int largePouchP, largePouchE;
	public long lastEmpty;
	public long lastFarmingUpdate = 0;
	public List<String> lastKilledIPs = new LinkedList<>();
	protected long lastLockPick, restoreStatsDelay, portalDelay;
	public long lastLogout = 0;
	public long lastRequestedLookup;
	private long lastSkillAction;
	public long lastSpear, dfsDelay, protMageDelay, protMeleeDelay, protRangeDelay, lastAction, lastThieve, alchDelay, teleBlockTime, godSpellDelay, reduceStat, buryDelay,
			foodDelay, potDelay, restoreDelay, objectClick;
	private long lastVengeanceCast;
	public int lastWait;
	public Stopwatch lastYell = Stopwatch.createUnstarted();
	public boolean loginFinished = false;
	public final long loginTimeMillis;
	public boolean mageAllowed;
	public boolean magicFailed;
	public int mediumPouchDecay, largePouchDecay, giantPouchDecay;
	public int mediumPouchP, mediumPouchE;
	public int miasmicCloud = -1;
	private short mobId = -1;
	public boolean mouseButton = false;
	public boolean multiAttacking, rangeEndGFXHeight;
	private boolean muted = false;
	private boolean newsfeedEnabled = true;
	public WorldObject oldCannon;
	public boolean partyChestOpen;
	private String passwordPlain;
	public int pkPoints, lastChatId = 1, privateChat = 0, dialogueId, specEffect, specBarId, skullTimer, nextChat = 0, dialogueMobId = -1, autocastId, barrageCount = 0,
			pcPoints = 0, lastArrowUsed = -1, isAutoRetaliateEnabled = 0, pcDamage = 0, frozenBy = 0, bonusAttack = 0, lastMobAttacked = 0, altarPrayed = 0,
			staffOfLightProtection = 0, brightnessLevel = 4;
	public int[] playerBonus = new int[18];
	private int playerObjectId;
	public boolean[] playerSkilling = new boolean[25];
	public int playgroundAltarCooldown = 0;
	public double prayerPoint = 1.0;
	public boolean profanityFilter = false;
	public int projectileStage, crystalBowArrowCount, rangeItemUsed, mobClickIndex, mobType, spellCastId, oldSpellId, spellId;
	public boolean protectItem = false;
	public int pvpPoints, slayerPoints, donatorPoints;
	public long randomAttemptDelay = System.currentTimeMillis();
	public int rangeEndGFX, boltDamage;
	public long[] reduceSpellDelay = new long[6];
	public int reduceSpellId;
	public int respawnTimer = -1;
	public boolean saveCharacter = false;
	public boolean settingUpCannon, hasCannon, cannonIsShooting, setUpBase, setUpStand, setUpBarrels, setUpFurnace, canLog;
	private boolean singing;
	public boolean[] skillCycle = new boolean[24];
	private Task skillingTask;
	public int skullIcon = -1;
	public int smallPouchPureEssence, smallPouchRuneEssence;
	public int specialEnergy = 100;
	private Spellbook spellbook;
	public boolean ssSpec = false;
	private boolean stopPacket;
	public long stopPrayerDelay;
	public boolean storing = false;
	private final boolean[] summonedBrothers = new boolean[6];
	public boolean takeAsNote;
	public int teleportDelay = -1;
	public boolean teleporting = false;
	protected Title title = null;
	public double totalDonationAmount = 0.00;
	public int totalExperience;
	public List<Title> unlockedTitles = new ArrayList<>();
	public int userId;
	public String username;
	private final long usernameHash; // unique 64-bit identifier for players
	public boolean usingFillables = false;
	public boolean walkFromFilling = false;
	public int wildLevel, teleBlockDuration;
	public boolean wonChallenge;
	public int runAnim = -1;
	public int standAnim = -1;
	public int walkAnim = -1;
	public boolean[] unlocked = new boolean[382];
	public int musicAmountUnlocked;

	private RegularPrivilege privilege = RegularPrivilege.NONE;
	private DonatorPrivilege donatorPrivilege = DonatorPrivilege.NONE;

	private int easyCluesCompleted = 0;
	private int mediumCluesCompleted = 0;
	private int hardCluesCompleted = 0;
	private int eliteCluesCompleted = 0;

	public boolean acceptAid = false;
	public int achievementPoints;
	public int temp_stage, temp_hunger;
	public int[] achievements = new int[98];
	public boolean addStarter = false;
	public int agilityFlags = 0;

	public final Container equipment = new Container(Container.Type.STANDARD, Equipment.SIZE);
	public final Container inventory = new Container(Container.Type.STANDARD, Inventory.SIZE);
	public final Container other = new Container(Container.Type.STANDARD, Inventory.SIZE);

	/**
	 * Gets the player's equipment.
	 *
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}

	/**
	 * Gets the inventory.
	 *
	 * @return The inventory.
	 */
	public Container getInventory() {
		return inventory;
	}

	/**
	 * @return the other
	 */
	public Container getOther() {
		return other;
	}

	public int getEasyCluesCompleted() {
		return easyCluesCompleted;
	}

	public int getMediumCluesCompleted() {
		return mediumCluesCompleted;
	}

	public int getHardCluesCompleted() {
		return hardCluesCompleted;
	}

	public int getEliteCluesCompleted() {
		return eliteCluesCompleted;
	}

	public void setEasyCluesCompleted(int easyCluesCompleted) {
		this.easyCluesCompleted = easyCluesCompleted;
	}

	public void setMediumCluesCompleted(int mediumCluesCompleted) {
		this.mediumCluesCompleted = mediumCluesCompleted;
	}

	public void setHardCluesCompleted(int hardCluesCompleted) {
		this.hardCluesCompleted = hardCluesCompleted;
	}

	public void setEliteCluesCompleted(int eliteCluesCompleted) {
		this.eliteCluesCompleted = eliteCluesCompleted;
	}

	/**
	 * The date the player joined
	 */
	private String joinDate;

	/**
	 * Creates a new player with the specified client and username.
	 *
	 * @param client The client. Not null.
	 * @param username The username. Not null.
	 */
	public Player(Session client, String username) {
		session = Objects.requireNonNull(client, "client");
		this.username = Objects.requireNonNull(username, "username");
		loginTimeMillis = System.currentTimeMillis();
		usernameHash = StringUtil.usernameToHash(username);
		getEquipment().clear();
		getInventory().clear();
		getLocation().setAs(new Location(Constants.START_LOCATION_X, Constants.START_LOCATION_Y, 0));
		spellbook = Spellbook.REGULAR;
		currentRegion.setAs(new Location(0, 0, 0));
		getMovementHandler().reset();
	}

	private final com.koios.game.arpg.RewardMailbox arpgRewards=new com.koios.game.arpg.RewardMailbox();
    public com.koios.game.arpg.RewardMailbox getArpgRewards(){return arpgRewards;}
    private final com.koios.game.arpg.PassiveTree arpgPassives=new com.koios.game.arpg.PassiveTree();
    public com.koios.game.arpg.PassiveTree getArpgPassives(){return arpgPassives;}
    private final com.koios.game.arpg.CharacterProgress arpgProgress=new com.koios.game.arpg.CharacterProgress();
    private final com.koios.game.arpg.Vitals arpgVitals=new com.koios.game.arpg.Vitals();
    public com.koios.game.arpg.CharacterProgress getArpgProgress(){return arpgProgress;}
    public com.koios.game.arpg.Vitals getArpgVitals(){return arpgVitals;}
    private final com.koios.game.arpg.SkillState arpgSkills=new com.koios.game.arpg.SkillState();
    public com.koios.game.arpg.SkillState getArpgSkills(){return arpgSkills;}
    private final com.koios.game.arpg.AttackClock arpgAttackClock=new com.koios.game.arpg.AttackClock();
    public com.koios.game.arpg.AttackClock getArpgAttackClock(){return arpgAttackClock;}
    public java.util.Map<com.koios.game.arpg.Stat,Double> getArpgStats(){
        return com.koios.game.arpg.ArpgStats.equipment(getEquipment().toArray(),getArpgProgress().level(),getArpgPassives().modifiers());
    }

    public void addAchievementPoints(int amount) {
		achievementPoints += amount;
		getSession().send(new SendConfigPacket(ConfigKeys.ACHIEVEMENT_POINTS, achievementPoints));
	}

	/**
	 * Sets the players position
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(int x, int y, int z) {
		setPosition(new Location(x, y, z));
	}

	public void setPosition(Location loc) {
		Combat.resetCombat(this);
		teleport(loc);
		getSession().send(new SendRemoveInterfacePacket());
		requestUpdates();
	}

	@Override
	public void teleport(Location loc) {

		boolean z_change = loc.getZ() != getLocation().getZ();

		getLocation().setAs(loc);

		setTeleporting(true);
		setPlacementNeeded(true);

		movement_handler.getLastLocation().setAs(new Location(getLocation().getX(), getLocation().getY() + 1));

		movement_handler.reset();

		ControllerManager.setControllerOnWalk(this);
		ControllerManager.updateControllerOnWalk(this);

		if (z_change) {
			GameService.getGroundItemManager().reloadItems(this);
			GameService.getObjectHandler().updateObjects(this);
			GameService.getObjectManager().loadObjects(this);
			getSession().send(new SendPlanePacket());
			session.send(new SendMapRegionPacket(this));
		} else {
			int deltaX = loc.getX() - getCurrentRegion().getRegionX() * 8;
			int deltaY = loc.getY() - getCurrentRegion().getRegionY() * 8;

			if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
				GameService.getGroundItemManager().reloadItems(this);
				GameService.getObjectHandler().updateObjects(this);
				GameService.getObjectManager().loadObjects(this);
				getSession().send(new SendPlanePacket());
				getSession().send(new SendMapRegionPacket(this));
			}
		}
	}

	private void correctCoordinates() {
		if (inPestControlGame()) {
			setPosition(2657, 2639, 0);
		}

		if (inFightPits() || inFightPitsWaiting()) {
			setPosition(2399, 5177, 0);
		}
		
		if (AreaBoundary.inArea(getLocation(), RecipeForDisasterAreaHandler.AREA)) {
			teleport(new Location(3097, 3486));
		}
	}

	private boolean hasEquipRequirements(int id) {
		switch (id) {
		case 20748:
		case 20770:
			if (!MysteriousCapeDialogue.canWearCompletionistCape(this, false)) {
				return false;
			}
			break;

		case 20767:
		case 20768:
			if (!MysteriousCapeDialogue.canWearMaxCape(this, false)) {
				return false;
			}
		}

		for (int i = 0; i < 23; i++) {
			if (GroundItemManager.definitions[id] != null && GroundItemManager.definitions[id].requirements != null && GroundItemManager.definitions[id].requirements[i] > 0) {
				if (GroundItemManager.definitions[id].requirements[i] > getSkill().getLevelForExperience(i)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Writes a {@link GamePacketBuilder} to the out channel
	 *
	 * @param packet The {@link GamePacketBuilder} to write
	 */
	public void write(GamePacketBuilder packet) {
		write(packet.toPacket());
	}

	/**
	 * Writes a packet on a player's connection channel.
	 *
	 * @param packet The packet to write on the channel.
	 * @return The Player instance.
	 */
	public void write(GamePacket packet) {
		session.write(packet);
	}

	public long getLastLogin() {
		if (lastLogout == 0) {
			return -1;
		}

		long elapsed = System.currentTimeMillis() - lastLogout;
		double toSeconds = elapsed / 1000;
		double rounded = Math.ceil(toSeconds);
		long increment = (long) Math.floor(rounded / 60);

		if (increment > 1) {
			elapsedMinutes += increment;
		}

		if (increment != 22435768) {
			return increment;
		}

		return -1;
	}

	/**
	 * Sets the side bar tabs plus their interfaces
	 */
	private void setSidebarInterfaces() {
		getSession().send(new SendSidebarInterfacePacket(0, 2423));// Combat
		// getSession().setSidebarInterface(1, 29001);// Achievement
		getSession().send(new SendSidebarInterfacePacket(1, 3917));// Skill
		getSession().send(new SendSidebarInterfacePacket(2, 638));// Quest
		getSession().send(new SendSidebarInterfacePacket(3, 3213));// Inventory
		getSession().send(new SendSidebarInterfacePacket(4, 1644));// Equipment

		if (altarPrayed == 0) {
			// Prayer
			getSession().send(new SendSidebarInterfacePacket(5, 5608));
		} else {
			getSession().send(new SendSidebarInterfacePacket(5, 22500));// 21356
		}

		getSession().send(new SendSidebarInterfacePacket(6, getSpellbook().getSidebarId()));// Magic
		getSession().send(new SendSidebarInterfacePacket(7, 18128));// Summoning
		getSession().send(new SendSidebarInterfacePacket(8, 5065));// Friends
		getSession().send(new SendSidebarInterfacePacket(9, 5715));// Ignore
		getSession().send(new SendSidebarInterfacePacket(10, 2449));// Clan Chat
		getSession().send(new SendSidebarInterfacePacket(11, 904));// Options
		getSession().send(new SendSidebarInterfacePacket(12, 147));// Emote
		getSession().send(new SendSidebarInterfacePacket(13, 962)); // Music
		getSession().send(new SendSidebarInterfacePacket(14, 18128));
		// getSession().setSidebarInterface(15, 18128);
	}

	public void login() {
		// Connection accepted

		write(new GamePacketBuilder(249).writeByteA(1).writeLEShortA(index()).toPacket());
		write(new GamePacketBuilder(68).toPacket());

		getInventory().addListener(new InterfaceContainerListener(this, Inventory.INTERFACE));
		getEquipment().addListener(new InterfaceContainerListener(this, Equipment.INTERFACE));
		getEquipment().addListener(new EquipmentContainerListener(this));
		getEquipment().addListener(new WeaponContainerListener(this));
		setSidebarInterfaces();
		prayers.clear();
		getSession().send(new SendConfigPacket(502, isChatSplit));
		getSession().send(new SendConfigPacket(287, isChatSplit));
		getSession().send(new SendConfigPacket(43, getCombat().getAttackStyle().ordinal()));
		// getSession().send(new SendSetWidgetColor(7341, disabledGlobalChat ?
		// 0xFF0000 : 0xFF00));
		// getSession().send(new SendSetWidgetColor(7346, isNewsfeedEnabled() ?
		// 0xFF00 : 0xFF0000));
		// getSession().send(new SendSetWidgetColor(7342, enabledXPLock ? 0xFF00
		// : 0xFF0000));
		setSidebarInterfaces();
		getSession().send(new SendConfigPacket(166, brightnessLevel));
		getSession().send(new SendConfigPacket(429, getRunEnergy().isRunning()));
		getSession().send(new SendEnergyPacket());
		getSession().send(new SendConfigPacket(504, true));
		getSession().send(new SendConfigPacket(505, false));// options
		getSession().send(new SendConfigPacket(506, true));
		getSession().send(new SendConfigPacket(507, false));
		getSession().send(new SendConfigPacket(508, false));
		getSession().send(new SendConfigPacket(108, false));// resets autocast
															// button
		getSession().send(new SendConfigPacket(172, true));

		// send music volume slider config
		getSession().send(new SendConfigPacket(168, getAttribute("music_volume", 2)));
		getSession().send(new SendConfigPacket(169, getAttribute("sound_volume", 2)));

		QuestTabHandler.write(this, QuestPages.HOME);

		for (int i = 744; i < 761; i++) {
			// getSession().send(new SendConfigPacket(i, 1));
		}
		getSession().send(new ResetCameraPacket());
		getSession().send(new SendChatOptionsPacket(0, 0, 0)); // clear private

		// messaging options
		long minutesAgo = getLastLogin();
		Bank.initializeBankConfigs(this);
		Farming.onLogin(this);
		for (int i = 0; i < Skill.SKILL_COUNT; i++) {
			getSession().send(new SendSkillGoalPacket(i));
		}
		final Player instance = this;
		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				MinigamesManager.handleLogin(instance);
				correctCoordinates();
				stop();
			}

		});

		if (playgroundAltarCooldown > 0) {
			getTaskScheduler().schedule(new PlaygroundAltarTask(this));
		}

		if (miasmicCloud > 0) {
			getTaskScheduler().schedule(new MiasmicEffectTask(this));
		}

		if (staffOfLightProtection > 0) {
			getTaskScheduler().schedule(new StaffOfLightTask(this));
		}

		if (skullTimer > 0) {
			getTaskScheduler().schedule(new SkullTimerTask(this));
		}

		MusicTab.loadMusicTab(this);

		getSummoning().handleLogin();

		if (specialEnergy < 100) {
			GameService.getTaskScheduler().schedule(new SpecialHandler.SpecialRestoreTask(this));
		}

		if (minutesAgo > 0) {
			String duration = TimeUtil.toString(minutesAgo * 60L * 1000L);
			sendMessage("Welcome back to " + Constants.SERVER_NAME + "!");
			sendMessage("You last logged on " + duration + " ago.");
		} else {
			sendMessage("Welcome to " + Constants.SERVER_NAME + "!");
		}

		for (String s : Constants.UPDATE_MESSAGES) {
			sendMessage(s);
		}

		if (Constants.DOUBLE_EXP) {
			sendMessage("@red@Double Experience:@blu@ Currently active!");
		}

		if (Constants.DOUBLE_PVP_POINTS) {
			sendMessage("@red@Double PvP Points:@blu@ Currently active!");
		}

		if (getBankField().isLoadError()) {
			sendMessage("<col=336699><img=9> Your bank items saved out of order. They have been reorganized for proper use.");
		}

		if (!isSuperAdministrator()) {
			for (Item item : getEquipment().toArray()) {
				if (item == null || item.getId() <= 0) {
					continue;
				}

				if (!hasEquipRequirements(item.getId())) {
					sendMessage("<col=80000>" + ItemDefinition.forId(item.getId()).getName() + " has been taken off as you no longer meet the requirements.");
					getEquipment().remove(item);

					if (getInventory().freeSlots() > 0) {
						getInventory().add(item);
					} else {
						Bank.addToBank(this, item);
					}
				}
			}
		}

		write(new GamePacketBuilder(214, PacketType.SHORT).toPacket());

		getSession().send(new SendConfigPacket(ConfigKeys.ACHIEVEMENT_POINTS, achievementPoints));
		getSession().send(new SendOptionPacket(4, "Trade With", false));
		getSession().send(new SendOptionPacket(5, "Follow", false));
		getItems().updateCombatStylesTab(getEquipment().get(Equipment.SLOT_WEAPON) == null ? -1 : getEquipment().get(Equipment.SLOT_WEAPON).getId());
		PrivateMessaging.logIntoPM(this);
		getItems().addSpecialBar(getEquipment().get(Equipment.SLOT_WEAPON) == null ? -1 : getEquipment().get(Equipment.SLOT_WEAPON).getId());
		AchievementManager.setup(this);

		saveCharacter = true;

		if (friendschatName != null) {
			FriendschatCommons.attemptJoinFriendschat(this, friendschatName, false);
		} else {
			 getSession().send(new SendStringPacket("Join chat", 18135));
			 if (getName().equalsIgnoreCase("arithium2"))
				 getSession().send(new ResetFriendsChatTab());
		}

		getSession().send(new SendConfigPacket(172, isAutoRetaliateEnabled));
		getMovementHandler().getLastLocation().setAs(new Location(getLocation().getX(), getLocation().getY() + 1, getLocation().getZ()));
		setPosition(getLocation());
		// GameService.getWorld().execute(p -> p.getSession().send(new
		// SendConfigPacket(ConfigKeys.PLAYERS_ONLINE,
		// GameService.getWorld().getPlayerCount())));
		LotteryManager.checkReward(this);
		getRunEnergy().tick();

		if (addStarter) {
			NewcomersHandler.welcome(this);
		}
		com.koios.game.arpg.ArpgSkills.prepareLogin(this);
		getSession().send(new SendMapRegionPacket(this));
	}

	public void logout() {
		if (FightPits.getInstance().isParticipant(this) && FightPits.getInstance().PARTICIPANTS.size() == 2) {
			sendMessage("You cannot logout when there are only two fighters left!");
			return;
		}

		if (controller != null && !controller.canLogOut(this)) {
			return;
		}

		if (settingUpCannon) {
			sendMessage("Please wait until you've finished setting up your cannon to logout!");
			return;
		}

		if (getMinigame().inMinigameLobby() && !getMinigame().getCurrentLobby().canLogout()) {
			sendMessage("You can't logout out of this lobby!");
			return;
		}

		if (getMinigame().inMinigame() && !getMinigame().getCurrentMinigame().canLogout()) {
			sendMessage("You can't logout during this minigame!");
			return;
		}

		if (System.currentTimeMillis() - getCombat().getLastHitDealt() > 10000) {
			write(new GamePacketBuilder(109));
			GameEngine.queueLogout(this);
		} else {
			sendMessage("You must wait a few seconds from being out of combat to logout.");
		}
	}

	public void requestUpdates() {
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void process() {
		session.processQueuedPackets();
		taskScheduler.pulse();

		if (System.currentTimeMillis() - lastFarmingUpdate >= 60000) {
			elapsedMinutes += 4;
			Farming.onLogin(this);
		}

		/**
		 * tick our controller
		 */
		controller.tick(this);
		getEF().updateEffects();
		getMovementHandler().process();

		getFollowing().process();
		prayers.handlePrayerDrain();

		if (getCombat().getAggressor() != null && System.currentTimeMillis() - getCombat().getLastHitDealt() >= 5000) {
			getCombat().setAggressor(null);
		}

		if (System.currentTimeMillis() - restoreStatsDelay > 60_000) {
			restoreStatsDelay = System.currentTimeMillis();

			for (int i = 0; i < Skill.SKILL_COUNT; i++) {
				if (i != Skill.PRAYER && i != Skill.SUMMONING) {
					if (i == Skill.HITPOINTS && getMinigame().inMinigame() && getMinigame().getCurrentMinigame().getMinigameName().equals("PROP_HUNT")) {
						continue;
					}

					getSkill().normalizeLevel(i);
				}
			}
		}

		com.koios.game.arpg.ArpgSkills.tick(this);

        if (getCombat().getHitDelay() > 0) {
			getCombat().setHitDelay(getCombat().getHitDelay() - 1);
		}

		if (getCombat().getHitDelay() == 1 && getCombat().getPreviousTarget() != null) {
			Combat.applyDamage(this, getCombat().getPreviousTarget());
		}

		if (getCombat().getAttackTime() > 0) {
			getCombat().setAttackTimer(getCombat().getAttackTime() - 1);
		}

		if (getCombat().getTarget() != null && getCombat().getAttackTime() <= 0) {
			getCombat().setAttackTimer(0);
			Combat.startAttack(this, getCombat().getTarget());
		}
		
		increaseVariable("timeout", 1);
		if (getAttribute("timeout", 0) == 30) {
			GameEngine.queueLogout(this);
		}
	}

	public void addKill(Player target) {
		if (target != null) {
			if (lastKilledIPs.size() >= Constants.KILLS_BEFORE_PKP_IP_GAIN) {
				lastKilledIPs.remove(0);
			}

			lastKilledIPs.add(target.getSession().getHostAddress());
		} else {
			System.err.println("Player#addKill(Player) -> Killed is 'null'");
		}
	}

	public void assignAutocast(int button) {
		for (int i = 0; i < MagicData.AUTOCAST_IDS.length; i++) {
			if (MagicData.AUTOCAST_IDS[i] == button) {
				autocasting = true;
				autocastId = MagicData.AUTOCAST_IDS[i + 1];
				getSession().send(new SendConfigPacket(108, 1));
				getSession().send(new SendSidebarInterfacePacket(0, 328));
				break;
			}
		}
	}

	public boolean atHome() {
		return inBounds(3069, 3109, 3088, 3502);
	}

	public void clearUpdateFlags() {
		updateflags.clear();
		reset();
		setFocusAtPointX(-1);
		setFocusAtPointY(-1);
		setTeleporting(false);
		setPlacementNeeded(false);
		getMovementHandler().resetMoveDirections();
		super.spokenText = null;
		setCachedBlock(null);
	}

	@Override
	public void death() {
	}

	public void destruct() {
		getLocation().setAs(new Location(0, 0, 0));
		currentRegion.setAs(new Location(0, 0, 0));
		getMovementHandler().reset();
	}

	public void resetAutocast() {
		autocastId = 0;
		autocasting = false;
		getSession().send(new SendConfigPacket(108, 0));
		sendMessage(":resetautocast:");
	}

	public int distanceToPoint(int x, int y) {
		return (int) Math.sqrt(Math.pow(getX() - x, 2) + Math.pow(getY() - y, 2));
	}

	public boolean dropItems() {
		return dropItems && isSuperAdministrator();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj != null && obj.getClass() == getClass()) {
			Player that = (Player) obj;
			return that.username.equals(username);
		}

		return false;
	}

	public int get180Turn(Item item) {
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.get180Turn();
			}
		}

		return 0x334;
	}

	public int get90CCWTurn(Item item) {
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.get90CCWTurn();
			}
		}

		return 0x336;
	}

	public int get90CWTurn(Item item) {
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.get90CWTurn();
			}
		}

		return 0x335;
	}

	public <T> T getAttribute(String key, T defaultValue) {

		if (defaultValue != null && key != null) {
			Object object = attributes.get(key);

			if (object != null && object.getClass() == defaultValue.getClass()) {
				@SuppressWarnings("unchecked")
				T t = (T) object;
				return t;
			}
		}

		return defaultValue;
	}

	public int getAttributeValue(String key) {
		Object value = attributes.get(key);

		if (null == value || value.getClass() != Integer.class) {
			return -1;
		}

		return (Integer) value;
	}

	public Optional<Friendschat> getFriendschat() {
		if (friendschatName == null) {
			// FriendschatProvider#get will throw an NPE if friendschatName is
			// == null.
			return Optional.empty();
		}

		return Optional.ofNullable(GameService.getFriendschatProvider().get(friendschatName));
	}

	@Override
	public int getMaxHitpoints() {
        if(arpgVitals!=null)return (int)Math.min(Integer.MAX_VALUE,getArpgStats().get(com.koios.game.arpg.Stat.LIFE));
		int hitpoints = getSkill().getLevelForExperience(Skill.HITPOINTS);

		int legsId = getEquipment().get(Equipment.SLOT_LEGS) == null ? -1 : getEquipment().get(Equipment.SLOT_LEGS).getId();
		int chestId = getEquipment().get(Equipment.SLOT_CHEST) == null ? -1 : getEquipment().get(Equipment.SLOT_CHEST).getId();
		int headId = getEquipment().get(Equipment.SLOT_HEAD) == null ? -1 : getEquipment().get(Equipment.SLOT_HEAD).getId();

		if (legsId == ItemIdentifiers.TORVA_PLATELEGS || legsId == ItemIdentifiers.PERNIX_CHAPS || legsId == ItemIdentifiers.VIRTUS_ROBE_LEGS) {
			hitpoints += 13;
		}

		if (chestId == ItemIdentifiers.TORVA_PLATEBODY || chestId == ItemIdentifiers.PERNIX_BODY || chestId == ItemIdentifiers.VIRTUS_ROBE_TOP) {
			hitpoints += 20;
		}

		if (headId == ItemIdentifiers.TORVA_FULL_HELM || headId == ItemIdentifiers.PERNIX_COWL || headId == ItemIdentifiers.VIRTUS_MASK) {
			hitpoints += 7;
		}

		return hitpoints;
	}

	public int getRunAnim(Item item) {
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.getRunEmote();
			}
		}

		return runAnim > 0 ? runAnim : 0x338;
	}

	public int getStandAnim(Item item) {
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.getStandEmote();
			}
		}

		return standAnim > 0 ? standAnim : 0x328;
	}

	public int getTurnAnim(Item item) {
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.getTurnEmote();
			}
		}

		return 0x337;
	}

	public int getVariable(String key, int def) {
		Object value = attributes.get(key);

		if (null == value || value.getClass() != Integer.class) {
			return def;
		}

		return (Integer) value;
	}

	public int getWalkAnim(Item item) {

		if (getMovementHandler().isForceMove()) {
			return walkAnim;
		}
		if (item != null) {
			WeaponDefinitions weapon = WeaponDefinitions.getWeapon(item.getId());

			if (weapon != null) {
				return weapon.getWalkEmote();
			}
		}

		return walkAnim > 0 ? walkAnim : 0x333;
	}

	public boolean isSkilling(int time) {
		return System.currentTimeMillis() - lastSkillAction <= time;
	}

	public boolean isSuperAdministrator() {
		if (getPrivilege().inferiorTo(RegularPrivilege.DEVELOPER)) {
			// this short-circuit weeds out most requests
			return false;
		}

		// if (!GameService.isOnlineMode()) {
		// return true;
		// }

		Long n = getUsernameHash();
		return Constants.SUPER_ADMINISTRATORS.contains(n);
	}

	public boolean isWithinDistance(Mob mob) {
		if (getZ() != mob.getZ()) {
			return false;
		}

		if (mob.deathStage == 2) {
			return false;
		}

		int deltaX = mob.getX() - getX();
		int deltaY = mob.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean isWithinDistance(Player player) {
		if (getZ() != player.getZ()) {
			// return false;
		}

		int deltaX = player.getX() - getX();
		int deltaY = player.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public void packetAnimation(int animation) {
		packetAnimation(animation, 2, null);
	}

	public void packetAnimation(int animation, int wait) {
		packetAnimation(animation, wait, null);
	}

	public void packetAnimation(int animation, int wait, Runnable add) {
		if (countdown.elapsed(TimeUnit.SECONDS) >= lastWait) {
			playAnimation(new Animation(animation));
			if (add != null) {
				add.run();
			}
			getSession().send(new SendRemoveInterfacePacket());
			countdown.reset().start();
			lastWait = wait;
		}
	}

	public <T> T removeAttribute(String key) {
		@SuppressWarnings("unchecked")
		T t = (T) attributes.remove(key);
		return t;
	}

	@Override
	public void reset() {
		chatText = null;
		chatTextColor = 0;
		chatTextEffects = 0;
		chatTextSize = 0;
		super.reset();
	}

	public void resetBarrowsKillCount() {
		barrowsKillCount = 0;
	}

	public void resetBonuses() {
		playerBonus = new int[playerBonus.length];
	}

	public void resetGodwarsKc() {
		godwards_kc = new int[4];
	}

	public void resetResting() {
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		getSession().send(new SendConfigPacket(765, 0));
	}

	/**
	 * Writes a game message to the chatbox of the represented player.
	 *
	 * @param message the message to be sent.
	 */
	public void sendMessage(Object message) {
		write(new GamePacketBuilder(253, PacketType.BYTE).writeString(Objects.toString(message)));
	}

	/**
	 * Increasing the achievement points the player has
	 *
	 * @param amount the amount of achievement points
	 */
	public void setAchievementPoints(int amount) {
		achievementPoints = amount;
		getSession().send(new SendConfigPacket(ConfigKeys.ACHIEVEMENT_POINTS, achievementPoints));
	}

	public boolean setEntityAppearance(int npcId) {
		if (npcId < 0 && npcId != -1 || npcId >= MobDefinition.getHighestID()) {
			return false;
		}

		setMobId((short) npcId);
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		return true;
	}

	@Override
	public void setHitpoints(int hp) {
        if(arpgVitals!=null){arpgVitals.setLife(hp,getMaxHitpoints());return;}
		if (hp < 0) {
			hp = 0;
		}

		if (hp > getMaxHitpoints()) {
			hp = getMaxHitpoints();
		}

		skills.setLevel(3, hp);
	}

	/**
	 * Setting the achievement status
	 *
	 * @param index the respective ordinal
	 * @param amount the amount of progression with said achievement.
	 */
	public void setAchievementStatus(int index, int amount) {
		achievements[index] = amount;
	}

	public int getWildyLevel() {
		int modY = getY() > 6400 ? getY() - 6400 : getY();
		int level = (modY - 3520) / 8;

		if (level < 10) {
			level = 10;
		}

		return level;
	}

	public boolean inDiceArea() {
		return getX() > 2834 && getX() < 2843 && getY() > 10200 && getY() < 10218;
	}

	public boolean inDuelArena() {
		return getX() >= 3324 && getX() <= 3390 && getY() >= 3203 && getY() <= 3379 || getX() >= 3322 && getX() <= 3323 && getY() >= 3223 && getY() <= 3250
				|| getX() >= 3312 && getX() <= 3321 && getY() >= 3223 && getY() <= 3247;
	}

	public boolean inFightCaves() {
		return getX() >= 2360 && getX() <= 2445 && getY() >= 5045 && getY() <= 5125;
	}

	/**
	 * Determines if the player is in fight pits
	 */
	public boolean inFightPits() {
		return getX() >= 2371 && getX() <= 2425 && getY() >= 5127 && getY() <= 5168;
	}

	/**
	 * Determines if the player is in fight pits waiting room
	 */
	public boolean inFightPitsWaiting() {
		return getX() >= 2393 && getX() <= 2404 && getY() >= 5169 && getY() <= 5175;
	}

	public boolean inFunPK() {
		return getX() >= 2579 && getX() <= 2601 && getY() >= 3903 && getY() <= 3924;
	}

	public boolean inMiniGame() {
		return inBarbDef() || inBarrows() || inChallenge() || inFightCaves() || inDuelArena() || inPestControlGame() || inPestControlBoat();
	}

	public boolean inPestControlBoat() {
		return getX() >= 2660 && getX() <= 2663 && getY() >= 2638 && getY() <= 2643;
	}

	public boolean inPestControlGame() {
		return getX() >= 2624 && getX() <= 2690 && getY() >= 2550 && getY() <= 2619;
	}

	public boolean inSW() {
		return getX() >= 2538 && getX() <= 2590 && getY() >= 4932 && getY() <= 4987;
	}

	public boolean inWild() {
		return getX() > 2941 && getX() < 3392 && getY() > 3524 && getY() < 3967 || getX() > 2941 && getX() < 3392 && getY() > 9918 && getY() < 10366 || inFunPK();
	}

	public boolean isAutoButton(final int buttonId) {
		for (int i = 0; i < MagicData.AUTOCAST_IDS.length; i += 2) {
			if (MagicData.AUTOCAST_IDS[i] == buttonId) {
				return true;
			}
		}

		return false;
	}

	public boolean hasRingOfWealth() {
		Item ring = getEquipment().get(Equipment.SLOT_RING);
		if (ring == null) {
			return false;
		}

		switch (ring.getId()) {
		case ItemIdentifiers.RING_OF_WEALTH:
		case ItemIdentifiers.RING_OF_WEALTH_1:
		case ItemIdentifiers.RING_OF_WEALTH_2:
		case ItemIdentifiers.RING_OF_WEALTH_3:
		case ItemIdentifiers.RING_OF_WEALTH_4:
			return true;
		}

		return false;
	}

	@Override
	public void hurt(int damage, EntityCharacter aggressor) {
		if (!teleporting) {
			if (!isInvulnerable()) {
				skills.setLevel(Skill.HITPOINTS, getHitpoints() - damage);
			}

			if (getHitpoints() < 1 && !isDead()) {
				prayers.effect(NormalPrayer.RETRIBUTION);
				prayers.effect(CursedPrayer.WRATH);
				boolean flag = minigame.inMinigame();
				setDead(true);
				GameService.getTaskScheduler().schedule(new PlayerDeathTask(this, aggressor, flag));
			}
		}
	}

	public boolean inArena() {
		return getX() > 3331 && getX() < 3391 && getY() > 3242 && getY() < 3260;
	}

	public boolean inBank() {
		return inBounds(3090, 3099, 3487, 3500) || inBounds(3089, 3090, 3492, 3498) || inBounds(3248, 3258, 3413, 3428) || inBounds(3179, 3191, 3432, 3448)
				|| inBounds(2944, 2948, 3365, 3374) || inBounds(2942, 2948, 3367, 3374) || inBounds(2944, 2950, 3365, 3370) || inBounds(3008, 3019, 3352, 3359)
				|| inBounds(3017, 3022, 3352, 3357) || inBounds(3203, 3213, 3200, 3237) || inBounds(3212, 3215, 3200, 3235) || inBounds(3215, 3220, 3202, 3235)
				|| inBounds(3220, 3227, 3202, 3229) || inBounds(3227, 3230, 3208, 3226) || inBounds(3226, 3228, 3230, 3211) || inBounds(3227, 3229, 3208, 3226)
				|| getX() > 2834 && getX() < 2843 && getY() > 10200 && getY() < 10218;
	}

	public boolean inBarbDef() {
		return inBounds(3332, 3358, 3206, 3220);
	}

	boolean inBarrows() {
		return getX() > 3520 && getX() < 3598 && getY() > 9653 && getY() < 9750 || getX() > 2467 && getX() < 2478 && getY() > 9673 && getY() < 9682;
	}

	@Override
	public boolean inBounds(int x1, int x2, int y1, int y2) {
		return getX() >= x1 && getX() <= x2 && getY() >= y1 && getY() <= y2;
	}

	public boolean inChallenge() {
		return getZ() == index() * 4 && inBounds(3155, 3181, 9748, 9769);
	}

	public void increaseBarrowsKillCount() {
		barrowsKillCount += 1;
	}

	public int increaseVariable(String key, int amount) {
		int current = getVariable(key, 0);
		int newValue = Math.addExact(current, amount);
		attributes.put(key, newValue);
		return newValue;
	}

	public YellTags getYT() {
		return yellTag;
	}

	public boolean hasFocus() {
		return true;//return focus;
	}

	@Override
	public int hashCode() {
		// delegate to username.hashCode() - the username is what differentiate
		// two players from one another
		return username.hashCode();
	}

	public boolean hasMoved() {
		return getWalkingDirection() != -1 || getRunningDirection() != -2;
	}

	public int index() {
		return getIndex();
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public boolean isInattackable() {
		return inattackable && isSuperAdministrator();
	}

	public boolean isInstantKill() {
		return instantKill && isSuperAdministrator();
	}

	public boolean isInvulnerable() {
		return invulnerable && isSuperAdministrator();
	}

	@Override
	public boolean isMob() {
		return false;
	}

	@Override
	public boolean isMoving() {
		return isMoving;
	}

	public boolean isMuted() {
		return muted;
	}

	public boolean isNewsfeedEnabled() {
		return newsfeedEnabled;
	}

	public boolean isPacketReadDisabled() {
		return stopPacket;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	public boolean isSinging() {
		return singing;
	}

	public void onControllerFinish() {
		controller = ControllerManager.DEFAULT_CONTROLLER;
	}

	/**
	 * Sets the players controller without initializing it
	 * 
	 * @param controller
	 * @return
	 */
	public boolean setControllerNoInit(Controller controller) {
		this.controller = controller;
		return true;
	}

	/**
	 * Sets our controller and initializes it
	 * 
	 * @param controller
	 * @return
	 */
	public boolean setController(Controller controller) {
		this.controller = controller;
		controller.onControllerInit(this);
		return true;
	}

	/**
	 * Gets our players controller
	 * 
	 * @return
	 */
	public Controller getController() {
		if (controller == null) {
			setController(ControllerManager.DEFAULT_CONTROLLER);
		}

		return controller;
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public void setBarrowsKillCount(int i) {
		barrowsKillCount = i;
	}

	public void setBounties(int i) {
		bounties = i;
	}

	public void setChatText(byte[] chatText) {
		this.chatText = chatText;
	}

	public void setChatTextColor(int chatTextColor) {
		this.chatTextColor = chatTextColor;
	}

	public void setChatTextEffects(int chatTextEffects) {
		this.chatTextEffects = chatTextEffects;
	}

	public void setChatTextSize(byte chatTextSize) {
		this.chatTextSize = chatTextSize;
	}

	public void setCurrentSkill(SkillConstructor currentSkillAction) {
		this.currentSkillAction = currentSkillAction;
	}

	@Override
	public int setDirection1(int i) {
		setWalkingDirection(i);
		return getWalkingDirection();
	}

	@Override
	public int setDirection2(int i) {
		setRunningDirection(i);
		return getRunningDirection();
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public void setDropItems(boolean dropItems) {
		this.dropItems = dropItems;
	}

	public void setFightPitPoints(int fightPitPoints) {
		this.fightPitPoints = fightPitPoints;
	}

	public void setFocus(boolean focus) {
	}

	public void setHiddenBrother(int index) {
		hiddenBrother = index;
	}

	public void setInattackable(boolean inattackable) {
		this.inattackable = inattackable;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setInstantKill(boolean instantKill) {
		this.instantKill = instantKill;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void setIsTrading(boolean isTrading) {
		this.isTrading = isTrading;
	}

	public void setLastSkillAction(long time) {
		lastSkillAction = time;
	}

	public void setLastVengeanceCast(long l) {
		lastVengeanceCast = l;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public void setNewsfeedEnabled(boolean newsfeedEnabled) {
		this.newsfeedEnabled = newsfeedEnabled;
	}

	public void setPasswordPlain(String passwordHash) {
		passwordPlain = passwordHash;
	}

	/**
	 * Sets the player object id and sends an update
	 *
	 * @param id is the id of the object player has taken
	 */
	public void setPlayerObjectId(int id) {
		playerObjectId = id + 1;
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void setRespawnTimer(int i) {
		respawnTimer = i;
	}

	public void setSinging(boolean b) {
		singing = b;
	}

	public void setSkilling(Task skillingTask) {
		this.skillingTask = skillingTask;

		if (this.skillingTask != null) {
			GameService.getTaskScheduler().schedule(this.skillingTask);
		}
	}

	public void setSpellbook(Spellbook spellbook) {
		this.spellbook = Objects.requireNonNull(spellbook);
	}

	public void setStopPacket(boolean stopPacket) {
		this.stopPacket = stopPacket;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public int setVariable(String key, int value) {
		Object oldValue = attributes.put(key, value);
		return oldValue != null ? (int) oldValue : -1;
	}

	@Override
	public void setZ(int z) {
		getMovementHandler().reset();
		getSession().send(new SendPlanePacket());
		getSession().send(new SendMapRegionPacket(this));
		super.setZ(z);
	}

	@Override
	public void speak(String text) {
		super.speak(text);
		getUpdateFlags().flag(UpdateFlag.FORCED_CHAT);
	}

	public void stopTask() {
		if (skillingTask != null) {
			skillingTask.stop();
		}
	}

	@Override
	public Player toPlayer() {
		return this;
	}

	/**
	 * Gets the players current vote points
	 *
	 * @return The players current vote points
	 */
	public int getVotePoints() {
		return votePoints;
	}

	/**
	 * Sets the players current vote points
	 *
	 * @param points The players current vote points
	 */
	public void setVotePoints(int points) {
		votePoints = points;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getWalkingDirection() {
		return dir1;
	}

	public void setWalkingDirection(int dir1) {
		this.dir1 = dir1;
	}

	public int getRunningDirection() {
		return dir2;
	}

	public void setRunningDirection(int dir2) {
		this.dir2 = dir2;
	}

	public int getPlayerObjectId() {
		return playerObjectId;
	}

	public short getMobId() {
		return mobId;
	}

	public void setMobId(short mobId) {
		this.mobId = mobId;
	}

	@Override
	public boolean isTeleporting() {
		return didTeleport;
	}

	@Override
	public void setTeleporting(boolean didTeleport) {
		this.didTeleport = didTeleport;
	}

	public boolean isPlacementNeeded() {
		return placementNeeded;
	}

	public void setPlacementNeeded(boolean placementNeeded) {
		this.placementNeeded = placementNeeded;
	}

	public DialogueManager dialogue() {
		return dialogue;
	}

	public GamePacketBuilder getCachedBlock() {
		return cachedBlock;
	}

	public void setCachedBlock(GamePacketBuilder buffer) {
		cachedBlock = buffer;
	}

	public boolean isTrading() {
		return isTrading;
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	@Override
	public int getStrength() {
		return skills.getLevel(Skill.STRENGTH);
	}

	public boolean[] getSummonedBrothers() {
		return summonedBrothers;
	}

	public final Summoning getSummoning() {
		return summoning;
	}

	public Thieving getThieving() {
		return thieving;
	}

	public Title getTitle() {
		return title;
	}

	public WoodTrees getTrees() {
		return trees;
	}

	public MinigameVariables getMinigame() {
		return minigame;
	}

	@Override
	public MovementHandler getMovementHandler() {
		return movement_handler;
	}

	@Override
	public String getName() {
		return username;
	}

	public String getPasswordPlain() {
		return passwordPlain;
	}

	public Potions getPotions() {
		return potions;
	}

	@Override
	public int getRanged() {
		return skills.getLevel(Skill.RANGED);
	}

	/**
	 * Gets the request manager.
	 *
	 * @return The request manager.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}

	public int getRespawnTimer() {
		return respawnTimer;
	}

	public RunEnergy getRunEnergy() {
		return runEnergy;
	}

	public SkillInterfaces getSI() {
		return skillInterfaces;
	}

	public boolean isDonator() {
		return privilege.superiorTo(RegularPrivilege.HELPER) || donatorPrivilege.superiorTo(DonatorPrivilege.NONE);
	}

	public boolean isSuperDonator() {
		return privilege.superiorTo(RegularPrivilege.HELPER) || donatorPrivilege.superiorTo(DonatorPrivilege.REGULAR);
	}

	public boolean isExtremeDonator() {
		return privilege.superiorTo(RegularPrivilege.HELPER) || donatorPrivilege.superiorTo(DonatorPrivilege.SUPER);
	}

	public boolean isLegendaryDonator() {
		return privilege.superiorTo(RegularPrivilege.HELPER) || donatorPrivilege.superiorTo(DonatorPrivilege.EXTREME);
	}

	public void setDonatorPrivilege(DonatorPrivilege donatorPrivilege) {
		this.donatorPrivilege = Objects.requireNonNull(donatorPrivilege, "privilege");
	}

	public void setPrivilege(RegularPrivilege privilege) {
		this.privilege = Objects.requireNonNull(privilege, "privilege");
	}

	public RegularPrivilege getPrivilege() {
		return privilege;
	}

	public DonatorPrivilege getDonatorPrivilege() {
		return donatorPrivilege;
	}

	public int getChatIconId() {
		// normal privileges override donator ones
		return privilege.superiorTo(RegularPrivilege.NONE) ? privilege.getCrownId() : donatorPrivilege.getCrownId();
	}

	@Override
	public int getSize() {
		return 1;
	}

	/**
	 * Gets the player's skills.
	 *
	 * @return The player's skills.
	 */
	@Override
	public Skill getSkill() {
		return skills;
	}

	public Task getSkilling() {
		return skillingTask;
	}

	public Slayer getSlayer() {
		return slayer;
	}

	public SpecialPlantOne getSpecialPlantOne() {
		return specialPlantOne;
	}

	public SpecialPlantTwo getSpecialPlantTwo() {
		return specialPlantTwo;
	}

	public Spellbook getSpellbook() {
		return spellbook;
	}

	public Session getSession() {
		return session;
	}

	/**
	 * Auto-generated getter for the achievements array
	 *
	 * @param index the respective ordinal
	 */
	public int getAchievementStatus(int index) {
		return achievements[index];
	}

	public Allotments getAllotments() {
		return allotments;
	}

	/**
	 * Gets the player's appearance.
	 *
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}

	/**
	 * Gets the player's armour degrading.
	 *
	 * @return the player's armour degrading.
	 */
	public ArmourDegrading getArmourDegrading() {
		return armourDegrading;
	}

	@Override
	public int getAttack() {
		return skills.getLevel(0);
	}

	/**
	 * Gets the player's bank.
	 *
	 * @return The player's bank.
	 */
	public Container getBank() {
		return bank;
	}

	public BankField getBankField() {
		return bankField;
	}

	/**
	 * Gets the player's bank pin handler.
	 *
	 * @return the player's bank pin handler.
	 */
	public BankPinHandler getBankPinHandler() {
		return bankPinHandler;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int getBounties() {
		return bounties;
	}

	public BountyHunter getBountyHunter() {
		return bounty_hunter;
	}

	public Bushes getBushes() {
		return bushes;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public int getChatTextColor() {
		return chatTextColor;
	}

	public int getChatTextEffects() {
		return chatTextEffects;
	}

	public byte getChatTextSize() {
		return chatTextSize;
	}

	@Override
	public int getClientIndex() {
		return getIndex() + 32768;
	}

	public Compost getCompost() {
		return compost;
	}

	public long getCurrentPlaytime() {
		return cachedPlaytime + System.currentTimeMillis() - loginTimeMillis;
	}

	public Location getCurrentRegion() {
		return currentRegion;
	}

	public SkillConstructor getCurrentSkill() {
		return currentSkillAction;
	}

	@Override
	public int getDefence() {
		return skills.getLevel(1);
	}

	public DialogueHandler getDH() {
		return dialogueHandler;
	}

	@Override
	public int getDirection1() {
		return getWalkingDirection();
	}

	@Override
	public int getDirection2() {
		return getRunningDirection();
	}

	public DuelArenaVariables getDuel() {
		return duel;
	}

	public DwarfMultiCannon getDwarfMultiCannon() {
		return cannon;
	}

	public int getEntityAppearance() {
		return getMobId();
	}

	public int getFightPitPoints() {
		return fightPitPoints;
	}

	public Flowers getFlowers() {
		return flowers;
	}

	@Override
	public Following getFollowing() {
		return following;
	}

	public FruitTree getFruitTree() {
		return fruitTree;
	}

	public int[] getGodwardsKc() {
		return godwards_kc;
	}

	public Herbs getHerbs() {
		return herbs;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	@Override
	public int getHitpoints() {
        if(arpgVitals!=null){arpgVitals.sync(getArpgStats());return (int)Math.ceil(arpgVitals.life());}
		return skills.getLevel(Skill.HITPOINTS);
	}

	public Hops getHops() {
		return hops;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public InterfaceState getInterfaceState() {
		return interfaceState;
	}

	public ItemAssistant getItems() {
		return itemAssistant;
	}

	public double getKDR() {
		return killCount / Math.max(1D, deathCount);
	}

	public boolean[] getKilledBrothers() {
		return killedBrothers;
	}

	public Killstreaks getKS() {
		return killstreaks;
	}

	public long getLastSkillAction() {
		return System.currentTimeMillis() - lastSkillAction;
	}

	public long getLastVengeanceCast() {
		return lastVengeanceCast;
	}

	@Override
	public int getMagicLevel() {
		return skills.getLevel(Skill.MAGIC);
	}

	public Location getForcePosition() {
		return new Location(forceX, forceY, getZ());
	}

	public QuickPrayers getQuickPrayers() {
		return quickPrayers;
	}

	/**
	 * Gets the players join date
	 * 
	 * @return The date the players account was created
	 */
	public String getJoinDate() {
		return joinDate;
	}

	/**
	 * Sets the players join date
	 * 
	 * @param date The date when the players account is created
	 */
	public void setJoinDate(String date) {
		this.joinDate = date;
	}

	public PrayerSet getPrayers() {
		return prayers;
	}

}