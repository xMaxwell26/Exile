package com.koios;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.koios.game.content.friendschat.FriendschatProvider;
import com.koios.game.content.friendschat.MySQLFriendschatProvider;
import com.koios.game.content.lottery.LotteryManager;
import com.koios.game.content.shop.ShopManager;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.impl.ImplingGenerator;
import com.koios.game.def.ItemDefinition;
import com.koios.game.def.LevelRequirement;
import com.koios.game.def.MobDefinition;
import com.koios.game.map.MapLoading;
import com.koios.game.map.MapRegion;
import com.koios.game.map.ObjectAlternates;
import com.koios.game.map.ObjectDefinition;
import com.koios.game.model.World;
import com.koios.game.model.item.GlobalDropsHandler;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.object.ObjectHandler;
import com.koios.game.model.object.ObjectManager;
import com.koios.network.mysql.SQLJob;
import com.koios.task.Task;
import com.koios.task.TaskScheduler;
import com.koios.util.XStreamLibrary;

public final class GameService {

	private final ExecutorService taskService = Executors.newCachedThreadPool();

	private final ServiceConfiguration configuration;

	private final TaskScheduler<Task> taskScheduler;

	private final boolean onlineMode;

	private final World world;

	private final MobManager mobManager;

	private final GroundItemManager groundItemManager;

	private final FriendschatProvider friendschatProvider;

	private final ObjectHandler objectHandler;

	private final ObjectManager objectManager;

	private final ShopManager shopManager;

	private Sql2o database;

	private final GameEngine engine;
	
	GameService(ServiceConfiguration configuration, GameEngine engine, boolean onlineMode) {
		this.configuration = Objects.requireNonNull(configuration, "configuration cannot be null");
		this.engine = Objects.requireNonNull(engine, "engine cannot be null.");
		taskScheduler = new TaskScheduler<>();
		this.onlineMode = onlineMode;
		world = new World();
		mobManager = new MobManager();
		groundItemManager = new GroundItemManager();
		objectHandler = new ObjectHandler();
		objectManager = new ObjectManager();
		shopManager = new ShopManager();
		friendschatProvider = new MySQLFriendschatProvider();
		//new UpdatePlayersOnline();
	}

	public void init() {
		ObjectDefinition.load();
		ObjectAlternates.loadAlternateIds();
		MapLoading.load();
		MapRegion.sort();
		MapLoading.processDoors();
		ObjectAlternates.clearAlternates();
		MobDefinition.parse().load();
		ItemDefinition.load();
        // Validate editable ARPG loot before opening the world; failed edits must not eat kill rewards.
        com.koios.game.arpg.MapDropTables.initialize();
		groundItemManager.loadDefinitions();
		shopManager.init();
		LevelRequirement.init();
		XStreamLibrary.init();
		GlobalDropsHandler.init();
		new ObjectHandler();
		ImplingGenerator.init();
		Hunter.init();
		LotteryManager.init();
		ObjectManager.init();
		mobManager.loadMobSpawns();
	}

	public static void execute(Runnable command) {
		singleton.taskService.execute(command);
	}

	public static <T> Future<T> submit(Callable<T> task) {
		return singleton.taskService.submit(task);
	}

	public static void submit(Runnable task) {
		singleton.taskService.submit(task);
	}

	public static ServiceConfiguration getConfiguration() {
		return singleton.configuration;
	}

	public static TaskScheduler<Task> getTaskScheduler() {
		return singleton.taskScheduler;
	}

	public static boolean isOnlineMode() {
		return singleton.onlineMode;
	}

	public static World getWorld() {
		return singleton.world;
	}

	public static MobManager getMobManager() {
		return singleton.mobManager;
	}

	public static GroundItemManager getGroundItemManager() {
		return singleton.groundItemManager;
	}

	public static ObjectHandler getObjectHandler() {
		return singleton.objectHandler;
	}

	public static ObjectManager getObjectManager() {
		return singleton.objectManager;
	}

	public static ShopManager getShopManager() {
		return singleton.shopManager;
	}

	public static FriendschatProvider getFriendschatProvider() {
		return singleton.friendschatProvider;
	}

	public static Connection getConnection() {
		return singleton.database.open();
	}

	public GameEngine getEngine() {
		return engine;
	}
	
	private static GameService singleton;

	/**
	 * @param service
	 *            The game service. Not null.
	 */
	static void setGameService(GameService service) {
		if (singleton != null) {
			throw new IllegalStateException("GameService can only be set once");
		}

		singleton = Objects.requireNonNull(service, "service cannot be null");
	}

	static void setDatabase(Sql2o database) {
		if (singleton.database != null) {
			throw new IllegalStateException("database can only be set once");
		}
		singleton.database = Objects.requireNonNull(database, "database cannot be null");
	}

	/**
	 * Submits an {@link SQLJob} to execute a query
	 * 
	 * @param job
	 *            The {@link SQLJob} to execute
	 */
	public static void submit(SQLJob job) {
		if (singleton.database == null) {
			// No need to submit an sql job if the database is null
			return;
		}

		job.executeQuery();
	}

}
