package com.koios;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.koios.game.content.activity.PestControl;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.skill.hunter.TrapProcessor;
import com.koios.game.content.skill.hunter.impl.ImplingGenerator;
import com.koios.game.model.World;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * A service dedicated to handling all game logic. This service executes packets every {@code 300}ms instead of every {@code 600}ms to improve the speed of packet processing.
 *
 * @author Mobster
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class GameEngine implements Runnable {

	/**
	 * The maximum amount of {@link Player}s allowed to be logged out per cycle.
	 */
	private static final int LOGOUT_THRESHOLD = 25;

	/**
	 * A logger which will log messages.
	 */
	private static final Logger logger = Logger.getLogger(GameEngine.class.getName());

	/**
	 * A queue of {@link Player}s who are waiting to be logged in.
	 */
	private static final Queue<Player> loginQueue = new ConcurrentLinkedQueue<>();

	/**
	 * A queue of {@link Player}s who are waiting to be logged out.
	 */
	private static final Queue<Player> logoutQueue = new ConcurrentLinkedQueue<>();

	/**
	 * A ChannelGroup for storing multiple connected channels.
	 */
	private static final ChannelGroup channelGroup = new DefaultChannelGroup("game-engine", GlobalEventExecutor.INSTANCE);

	/**
	 * The state that this engine is currently in.
	 */
	private static EngineState state = EngineState.NORMAL;

	/**
	 * The pulse rate of the server
	 */
	private final static int PULSE_RATE = 600;

	private static final ScheduledExecutorService DEFAULT_SERVICE = construct();

	private final ScheduledExecutorService service;

	public GameEngine() {
		this(DEFAULT_SERVICE);
	}

	/**
	 * Constructs a new {@link GameLogicService} with the specified ScheduledExecutorService.
	 *
	 * @param service The ScheduledExecutorService used to schedule this Service.
	 */
	public GameEngine(ScheduledExecutorService service) {
		this.service = service;
	}

	public void start() {
		service.scheduleAtFixedRate(this, 0, 300, TimeUnit.MILLISECONDS);
	}

	/**
	 * Constructs an {@link ScheduledExecutorService} for this Service.
	 *
	 * @return The constructed ScheduledExecutorService.
	 */
	public static ScheduledExecutorService construct() {
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GameLogicService").setPriority(Thread.MAX_PRIORITY).build();
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(factory);
		return service;
	}

	@Override
	public void run() {
		try {
			switch (state) {
			case NORMAL:
				cycle();

				break;
			case PACKET_PROCESS:
				subcycle();
				break;
			}
			state = state.next();
		} catch (Exception reason) {
			logger.log(Level.SEVERE, "Fatal error while handling server logic loop", reason);
			reason.printStackTrace();
		}
	}

	/**
	 * Adds {@code player} to the login queue.
	 *
	 * @param player the player to add to the login queue.
	 */
	public static void queueLogin(Player player) {
		if (!loginQueue.contains(player)) {
			loginQueue.offer(player);
		}
	}

	/**
	 * The delay to debug the cycle time
	 */
	private static long startTime = System.currentTimeMillis();

	/**
	 * Adds the specified {@link Player} to the logout queue.
	 *
	 * @param player The Player to add.
	 */
	public static void queueLogout(Player player) {
		if (!logoutQueue.contains(player)) {
			logoutQueue.offer(player);
		}
	}

	/**
	 * Performs a normal cycle where the {@link World} is synchronized with the game thread. These are done every {@code 600}ms.
	 */
	private static void cycle() {
		// for (int count = 0; count < LOGIN_THRESHOLD; count++) {
		// Player p = loginQueue.poll();
		// if (p == null) {
		// break;
		// }
		// GameService.getWorld().register(p);
		// }

		long start = System.nanoTime();

		startTime = System.currentTimeMillis();

		for (int count = 0; count < LOGOUT_THRESHOLD; count++) {
			// get, but do not remove the head of the queue
			Player player = logoutQueue.peek();
			if (player == null) {
				break;
			}

			if (System.currentTimeMillis() - player.getCombat().getLastDamageReceived() < 10_000) { // 10 seconds
				continue;
			}

			GameService.getWorld().unregister(player);
			logoutQueue.remove(player);
		}

		debug(startTime, "Logout Queue");

		GameService.getTaskScheduler().pulse();
		debug(startTime, "Tasks");
		GroundItemManager.update();
		debug(startTime, "Ground Items");
		GameService.getWorld().update();
		debug(startTime, "World");
		GameService.getMobManager().update();
		debug(startTime, "Mobs");
		GameService.getObjectManager().update();
		debug(startTime, "Object Handler");
		TrapProcessor.process();
		debug(startTime, "Trap");
		PestControl.getInstance().update();
		debug(startTime, "Pest Control");
		FightPits.getInstance().update();
		debug(startTime, "Fight Pits");
		ImplingGenerator.process();
		debug(startTime, "Implings");

		long finish = System.nanoTime();
		long ns = finish - start;
		long ms = ns / 1_000_000;
		int difference = (int) (PULSE_RATE - ms);

		if (difference < 0) {
			logger.warning("Warning:");
			logger.warning("The game service is unable to keep up!");
			logger.warning("[max=600ms, accomplished=[" + NumberUtil.format(ns) + "ns, " + NumberUtil.format(ms) + "ms]]");
		}
	}

	/**
	 * Debugs the time taken per task
	 *
	 * @param stopwatch
	 */
	private static void debug(long start, String type) {
		boolean debug = true;

		long elapsed = System.currentTimeMillis() - start;
		if (debug && elapsed >= 50) {
			System.err.println(type + ": " + " processing is slow, Time Elapsed: " + elapsed);
		}
		startTime = System.currentTimeMillis();
	}

	/**
	 * Performs a sub-cycle where packets are processed. These are done every {@code 300}ms.
	 */
	private static void subcycle() {
		GameService.getWorld().execute(p -> p.getSession().processQueuedPackets());
	}

	/**
	 * Returns an unmodifable collection of {@link Player}s that need to be logged in.
	 *
	 * @return the queue of players that need to be logged in.
	 */
	public static Collection<Player> getLoginQueue() {
		return Collections.unmodifiableCollection(loginQueue);
	}

	/**
	 * The enumerated type representing the different states of this engine.
	 */
	private enum EngineState {

		/**
		 * The engine needs to perform a normal cycle; Synchronization with the {@link World}.
		 */
		NORMAL,

		/**
		 * The engine needs to perform a sub-cycle; Packet processing for improved speed.
		 */
		PACKET_PROCESS;

		public EngineState next() {
			return this == NORMAL ? PACKET_PROCESS : NORMAL;
		}
	}

	public static ChannelGroup getChannelGroup() {
		return channelGroup;
	}
}
