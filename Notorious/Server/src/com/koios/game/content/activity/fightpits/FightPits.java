package com.koios.game.content.activity.fightpits;

import java.util.LinkedList;
import java.util.List;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.model.player.ConfigKeys;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.task.SkullTimerTask;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.network.out.impl.SendWalkableWidget;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Handles the Fight Pits minigame.
 *
 * @author Ben Stulpin
 */
public final class FightPits {

	/**
	 * Gets the fight pits instance.
	 *
	 * @return the fight pits instance.
	 */
	public static FightPits getInstance() {
		return instance;
	}

	/**
	 * Creates a new instance.
	 */
	private static final FightPits instance = new FightPits();

	/**
	 * The minimum player count.
	 */
	private static final int MINIMUM_PLAYERS = 4;

	/**
	 * The start time.
	 */
	private static final int START_TIME = 80;

	/**
	 * The game started flag.
	 */
	private static boolean gameInProgress = false;

	/**
	 * The attack signal sent flag.
	 */
	private static boolean signalSent = false;

	/**
	 * The start timer.
	 */
	private static int startTimer = START_TIME;

	/**
	 * The champion.
	 */
	private static String champion = "none";

	/**
	 * The message.
	 */
	private static String message = "";

	/**
	 * The list of waiting players.
	 */
	public List<Player> WAITING_PLAYERS = new LinkedList<>();

	/**
	 * The list of participants.
	 */
	public List<Player> PARTICIPANTS = new LinkedList<>();

	/**
	 * Checks if a new game can be started.
	 */
	private boolean canStart() {
		/**
		 * We check if there is already a game started.
		 */
		if (gameInProgress) {
			message = "There is already a game in session.";
			return false;
		}

		/**
		 * We check if there is enough players.
		 */
		if (WAITING_PLAYERS.size() < MINIMUM_PLAYERS) {
			message = String.format("There must be at least %d players to start a new game.", MINIMUM_PLAYERS);
			return false;
		}

		return true;
	}

	/**
	 * Handles the death of a player.
	 *
	 * @param player the player.
	 */
	public void deathHook(Player player) {
		player.sendMessage("Oh dear, you are dead!");
		player.setFightPitPoints(player.getFightPitPoints() + 1);
		player.getSession().send(new SendConfigPacket(ConfigKeys.FIGHT_PIT_POINTS, player.getFightPitPoints()));
		AchievementManager.performed(player, Achievement.OBTAIN_FIGHT_POINTS, 1);
		player.getSession().send(new SendWalkableWidget(-1));
	}

	/**
	 * Handles the ending of a game.
	 */
	private void endGame() {
		/**
		 * We check if the amount of participants is equal to one.
		 */
		if (PARTICIPANTS.size() == 1) {
			/**
			 * We get the champion.
			 */
			final Player player = PARTICIPANTS.get(0);

			/**
			 * We make sure that the winner is not dead.
			 */
			// Where?

			/**
			 * We send the congratulations dialogue.
			 */
			SimpleDialogues.sendMobStatement(player, 2618, "Congratulations, you are the champion!", "You have been given 5 fight points as a reward.");

			/**
			 * We check if the player is already the champion.
			 */
			if (!champion.replace("JalYt-Ket-", "").equalsIgnoreCase(player.getName())) {
				/**
				 * We broadcast the new champion.
				 */
				GameService.getWorld().broadcast("@red@Fight Pits: The new champion is " + player.getName() + "!");
			}

			/**
			 * Check if we currently have a skull
			 */
			boolean active = player.skullTimer > 0;

			/**
			 * We apply the red champion skull.
			 */
			player.skullTimer = Constants.SKULL_TIMER;

			/**
			 * If we dont have a skull already, start the task
			 */
			if (!active) {
				player.getTaskScheduler().schedule(new SkullTimerTask(player));
			}

			player.skullIcon = 1;

			/**
			 * We set the champion.
			 */
			champion = "JalYt-Ket-" + player.getName();

			/**
			 * We give reward winner with fight pit points.
			 */
			player.setFightPitPoints(player.getFightPitPoints() + 5);
			player.getSession().send(new SendConfigPacket(ConfigKeys.FIGHT_PIT_POINTS, player.getFightPitPoints()));
			AchievementManager.performed(player, Achievement.OBTAIN_FIGHT_POINTS, 5);

			/**
			 * We move the player to the end location.
			 */
			player.setPosition(2399, 5177, 0);

			/**
			 * We remove the walkable interface.
			 */
			player.getSession().send(new SendWalkableWidget(-1));

			/**
			 * We clear the player's combat variables.
			 */
			player.getDamage().reset();
			player.requestUpdates();
			player.specialEnergy = 100;
			player.setLastVengeanceCast(0);

			if (player.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME)) {
				player.getEF().renewEffect(EffectConstants.VENGEANCE_NAME);
			}

			player.berserkerActive = false;
			player.getPrayers().clear();
			player.getSkill().restoreAll();
		}

		/**
		 * We clear the list of participants.
		 */
		PARTICIPANTS.clear();

		/**
		 * We clear the variables.
		 */
		gameInProgress = false;
		signalSent = false;
	}

	/**
	 * We check if the player is a participant.
	 *
	 * @param player the player.
	 * @return return participant state.
	 */
	public boolean isParticipant(Player player) {
		return PARTICIPANTS.contains(player);
	}

	/**
	 * Gets the signal sent flag.
	 *
	 * @return the signal sent flag.
	 */
	public boolean isSignalSent() {
		return signalSent;
	}

	/**
	 * Handles the start of a game.
	 */
	private void startGame() {
		/**
		 * We check if a new game can be started.
		 */
		if (!canStart()) {
			/**
			 * We send the message to the players.
			 */
			GameService.getWorld().broadcast(message, WAITING_PLAYERS::contains);
			startTimer = START_TIME;
			return;
		}

		/**
		 * We loop through all of the waiting players.
		 */
		for (Player player : WAITING_PLAYERS) {
			/**
			 * We check if the player is a null.
			 */
			if (player == null) {
				continue;
			}

			/**
			 * We clear the player's prayers.
			 */
			player.getPrayers().clear();

			player.getSkill().restoreAll();
			/**
			 * We move the player to the fight arena.
			 */
			player.setPosition(2392 + NumberUtil.random(12), 5139 + NumberUtil.random(25), 0);

			/**
			 * We send the wait for signal dialogue.
			 */
			SimpleDialogues.sendMobStatement(player, 2618, "Wait for my signal before fighting.");
		}

		/**
		 * We transfer the waiting players to the participants list.
		 */
		PARTICIPANTS.addAll(WAITING_PLAYERS);

		/**
		 * We clear the waiting players list.
		 */
		WAITING_PLAYERS.clear();

		/**
		 * We submit a new task of 50 ticks.
		 */
		GameService.getTaskScheduler().schedule(new Task(50) {

			@Override
			public void execute() {
				/**
				 * We loop through all of the waiting players.
				 */
				for (Player player : PARTICIPANTS) {

					/**
					 * We check if the player is a null.
					 */
					if (player == null) {
						continue;
					}

					/**
					 * We send the signal dialogue.
					 */
					SimpleDialogues.sendMobStatement(player, 2618, "FIGHT!");
					player.sendMessage("FIGHT!");

					/**
					 * We set the signal sent flag.
					 */
					signalSent = true;
				}

				stop();
			}

		});

		/**
		 * We clear the variables.
		 */
		startTimer = START_TIME;
		gameInProgress = true;
	}

	/**
	 * Processes the Fight Pits game.
	 */
	public void update() {
		/**
		 * We check if the start timer is greater than 0.
		 */
		if (startTimer > 0) {
			startTimer--;
		}

		/**
		 * We check if the start timer equals 0.
		 */
		else if (startTimer == 0) {
			startGame();
		}

		/**
		 * We check if there is a game in progress.
		 */
		if (gameInProgress) {
			/**
			 * We check if there is less than one participants.
			 */
			if (PARTICIPANTS.size() <= 1) {
				endGame();
			}
		}

		/**
		 * We update the walkable interface.
		 */
		updateInterfaces();
	}

	/**
	 * Updates the game interface for the player.
	 *
	 * @param player the player.
	 */
	public void updateGameInterface(Player player) {
		/**
		 * We send the champion.
		 */
		player.getSession().send(new SendStringPacket("Current Champion: " + champion, 2805));

		/**
		 * We send the remaining foes.
		 */
		player.getSession().send(new SendStringPacket("Foes Remaining: " + PARTICIPANTS.size(), 2806));

		/**
		 * We send the walkable interface.
		 */
		player.getSession().send(new SendWalkableWidget(2804));

	}

	/**
	 * Updates the interfaces.
	 */
	public void updateInterfaces() {
		/**
		 * We check if there are players in the waiting room.
		 */
		if (WAITING_PLAYERS.size() > 0) {
			/**
			 * We loop through all of the waiting players.
			 */
			for (Player player : WAITING_PLAYERS) {
				/**
				 * We check if the player is a null.
				 */
				if (player == null) {
					continue;
				}

				/**
				 * We update the wait interface for the player.
				 */
				updateWaitInterface(player);
			}
		}

		/**
		 * We check if a game is in progress.
		 */
		if (gameInProgress && PARTICIPANTS.size() > 0) {
			/**
			 * We loop through all of the participants.
			 */
			for (Player player : PARTICIPANTS) {
				/**
				 * We check if the player is a null.
				 */
				if (player == null) {
					continue;
				}

				/**
				 * We update the game interface for the player.
				 */
				updateGameInterface(player);
			}
		}
	}

	/**
	 * Updates the wait interface for the player.
	 *
	 * @param player the player.
	 */
	public void updateWaitInterface(Player player) {
		/**
		 * We set the config.
		 */
		player.getSession().send(new SendConfigPacket(560, player.getName().equalsIgnoreCase("JalYt-Ket-" + champion) ? 0 : 1));

		/**
		 * We send the champion.
		 */
		player.getSession().send(new SendStringPacket("Current Champion: " + champion, 2805));

		/**
		 * We check if there is a game in progress.
		 */
		if (gameInProgress) {
			/** We send the remaining foes */
			player.getSession().send(new SendStringPacket("Foes Remaining: " + PARTICIPANTS.size(), 2806));
		} else {
			/** We send the time remaining */
			player.getSession().send(new SendStringPacket("Time Remaining: " + startTimer, 2806));
		}

		/** We send the walkable interface */
		player.getSession().send(new SendWalkableWidget(2804));
	}

}
