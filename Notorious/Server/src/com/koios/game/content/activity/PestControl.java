package com.koios.game.content.activity;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.NumberUtil;

/**
 * @author Harlan
 * @author Sanity
 */
public final class PestControl {

	private static final PestControl instance = new PestControl();

	public static PestControl getInstance() {
		return instance;
	}

	/**
	 * Moves a player into the hash and into the lobby
	 *
	 * @param player The player
	 */
	public static boolean addToWaitRoom(Player player) {
		return player != null && waitingBoat.put(player, 1) == null;
	}

	/**
	 * Checks if the portals are dead
	 *
	 * @return players dead
	 */
	private static boolean allPortalsDead() {
		int count = 0;

		for (int health : portalHealth) {
			if (health <= 0) {
				count++;
			}
		}

		return count >= 4;
	}

	/**
	 * Checks if a player is in the game
	 *
	 * @param player The player
	 * @return return
	 */
	public static boolean isInGame(Player player) {
		return gamePlayers.containsKey(player);
	}

	/**
	 * Checks if a player is in the pc boat (lobby)
	 *
	 * @param player The player
	 * @return return
	 */
	public static boolean isInPestControlBoat(Player player) {
		return waitingBoat.containsKey(player);
	}

	public static boolean isPestControlMob(int id) {
		return id >= 3727 && id <= 3776;
	}

	/**
	 * Removes the player from the list of players waiting to add the game
	 */
	public static boolean leaveWaitingBoat(Player player) {
		return waitingBoat.remove(player) != null;
	}

	/**
	 * Method we use for removing a player from the pc game
	 *
	 * @param player The Player.
	 */
	public static void removePlayerGame(Player player) {
		if (gamePlayers.containsKey(player)) {
			gamePlayers.remove(player);
		}
	}

	private static Map<Player, Integer> gamePlayers = new HashMap<>();

	public static boolean gameStarted = false;

	public static int knightHealth = -1;

	/**
	 * How many players we need to start a game
	 */
	private final static int PLAYERS_REQUIRED = 1;

	/**
	 * Array used for storing the portals health
	 */
	public static int[] portalHealth = { 200, 200, 200, 200 };

	/**
	 * how long before were put into the game from lobby
	 */
	private static final int WAIT_TIMER = 30;

	/**
	 * Hashmap for the players in lobby
	 */
	private static Map<Player, Integer> waitingBoat = new HashMap<>();

	private static int waitTimer = 20;

	private final int brawler = 3772 + NumberUtil.inclusiveRandom(4);
	private final int defiler = 3762 + NumberUtil.inclusiveRandom(9);
	private int gameTimer = -1;

	private final int[][] pcNPCData = { { 6142, 2628, 2591 }, // portal
			{ 6143, 2680, 2588 }, // portal
			{ 6144, 2669, 2570 }, // portal
			{ 6145, 2645, 2569 }, // portal
			{ 3782, 2656, 2592 }, // knight
	};

	private int properTimer = 0;
	private final int ravager = 3742 + NumberUtil.inclusiveRandom(4);
	private final int shifter = 3732 + NumberUtil.inclusiveRandom(9);
	private final int torcher = 3752 + NumberUtil.inclusiveRandom(7);

	private final int[][] voidMonsterData = { { shifter, 2675, 2589 }, { brawler, 2679, 2593 }, { defiler, 2679, 2585 }, { ravager, 2675, 2585 }, { torcher, 2675, 2593 },
			{ shifter, 2670, 2576 }, { brawler, 2674, 2573 }, { defiler, 2666, 2573 }, { ravager, 2666, 2577 }, { torcher, 2674, 2577 }, { shifter, 2646, 2576 },
			{ brawler, 2650, 2572 }, { defiler, 2642, 2572 }, { ravager, 2642, 2576 }, { torcher, 2650, 2576 }, { shifter, 2635, 2592 }, { brawler, 2631, 2588 },
			{ defiler, 2631, 2596 }, { ravager, 2635, 2596 }, { torcher, 2635, 2588 }, };

	private boolean allPortalsDead3() {
		int count = 0;

		for (Mob mob : MobManager.mobs) {
			if (mob != null) {
				if (mob.getId() >= 6142 && mob.getId() <= 6145) {
					if (mob.isDead()) {
						count++;
					}
				}
			}
		}

		return count >= 4;
	}

	/**
	 * Resets the game variables and map
	 */
	private void cleanUp() {
		gameTimer = -1;
		knightHealth = -1;
		waitTimer = WAIT_TIMER;
		gameStarted = false;
		gamePlayers.clear();

		/* Removes the npcs from the game if any left over for whatever reason */
		for (int[] aPcNPCData : pcNPCData) {
			for (int i = 0; i < MobManager.mobs.length; i++) {
				if (MobManager.mobs[i] != null) {
					if (MobManager.mobs[i].getId() == aPcNPCData[0]) {
						MobManager.mobs[i] = null;
					}
				}
			}
		}

		for (int[] aPcNPCData : voidMonsterData) {
			for (int i = 0; i < MobManager.mobs.length; i++) {
				if (MobManager.mobs[i] != null) {
					if (MobManager.mobs[i].getId() == aPcNPCData[0]) {
						MobManager.mobs[i] = null;
					}
				}
			}
		}
	}

	/**
	 * Cleans the player of any damage, loss they may of received
	 */
	private void cleanUpPlayer() {
		for (Player player : gamePlayers.keySet()) {
			if (player != null) {
				player.getPrayers().clear();

				player.getSkill().restoreAll();

				player.specialEnergy = 100;
				player.pcDamage = 0;
				if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					player.getItems().addSpecialBar(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
					player.getItems().updateSpecialBar();
				}
			}
		}
	}

	/**
	 * Ends the game
	 *
	 * @param won Did you win?
	 */
	private void endGame(boolean won) {
		for (Player player : gamePlayers.keySet()) {
			if (player == null) {
				continue;
			}

			// player.getPA().movePlayer(2657, 2639, 0);
			player.teleport(new Location(2657, 2639));

			if (won && player.pcDamage > 50) {
				// player.getDH().sendDialogues(79, 3790);
				int POINT_REWARD = 25;
				player.sendMessage("You have won the pest control game and have been awarded " + POINT_REWARD + " Pest control points.");
				player.pcPoints += POINT_REWARD;
				player.getSession().send(new SendStringPacket("- @or2@Pest Points: @gre@" + NumberFormat.getInstance(Locale.US).format(player.pcPoints), 7383));
				player.getInventory().add(new Item(995, player.getSkill().getCombatLevel(true) * 1000));
				AchievementManager.performed(player, Achievement.PEST_CONTROL);
			} else if (won) {
				// player.getDH().sendDialogues(77, 3790);
				// int POINT_REWARD2 = 4;
				// player.pcPoints += POINT_REWARD2;
				player.sendMessage("The void knights notice your lack of zeal. You receive no points.");
			} else {
				// player.getDH().sendDialogues(78, 3790);
				player.sendMessage("The failed to keep the Void Knight alive! You receive no points.");
			}
		}

		cleanUpPlayer();
		cleanUp();
	}

	/**
	 * Checks how many players are in the waiting lobby
	 *
	 * @return players waiting
	 */
	private int playersInBoat() {
		int players = 0;

		for (Player player : waitingBoat.keySet()) {

			if (player != null) {
				players++;
			} else {
				players--;
			}
		}

		return players;
	}

	/**
	 * Checks how many players are in the game
	 *
	 * @return players in the game
	 */
	private int playersInGame() {
		int players = 0;

		for (Player player : gamePlayers.keySet()) {

			if (player != null) {
				players++;
			} else {
				players--;
			}
		}

		return players;
	}

	/**
	 * Setting the interfaces for the waiting lobby
	 */
	private void setBoatInterface() {
		for (Player player : waitingBoat.keySet()) {

			if (player != null) {

				if (gameStarted) {
					player.getSession().send(new SendStringPacket("Next Departure: " + (waitTimer + gameTimer) / 60 + " minutes", 21120));
				} else {
					player.getSession().send(new SendStringPacket("Next Departure: " + waitTimer + " seconds", 21120));
				}

				player.getSession().send(new SendStringPacket("Players Ready: " + playersInBoat(), 21121));
				player.getSession().send(new SendStringPacket("(Need " + PLAYERS_REQUIRED + " to 25 players)", 21122));
				player.getSession().send(new SendStringPacket("Points: " + player.pcPoints + "", 21123));

				if (waitTimer == 60) {
					player.sendMessage("Next game will start in: 60 seconds.");
				}
			}
		}
	}

	/**
	 * Setting the interface for in game players
	 */
	private void setGameInterface() {
		for (Player player : gamePlayers.keySet()) {

			if (player != null) {
				for (int i = 0; i < portalHealth.length; i++) {
					if (portalHealth[i] > 0) {
						player.getSession().send(new SendStringPacket(String.valueOf(portalHealth[i]), 21111 + i));
					} else {
						player.getSession().send(new SendStringPacket("@red@-", 21111 + i));
					}
				}

				player.getSession().send(new SendStringPacket(String.valueOf(knightHealth), 21115));
				player.getSession().send(new SendStringPacket(String.valueOf(player.pcDamage), 21116));

				if (gameTimer > 60) {
					player.getSession().send(new SendStringPacket("Time remaining: " + gameTimer / 60 + " minutes", 21117));
				} else {
					player.getSession().send(new SendStringPacket("Time remaining: " + gameTimer + " seconds", 21117));
				}
			}
		}
	}

	private void spawnNPC() {
		for (int[] aPcNPCData : pcNPCData) {
			MobManager.spawnMobInternal(aPcNPCData[0], aPcNPCData[1], aPcNPCData[2], 0, 0, 200, 0, 0, playersInBoat() * 100);
		}

		for (int[] voidMonsters : voidMonsterData) {
			MobManager.spawnMobInternal(voidMonsters[0], voidMonsters[1], voidMonsters[2], 0, 1, 100, 10, 200, 200);
		}
	}

	/**
	 * Moving players to arena if there's enough players
	 */
	private void startGame() {
		if (playersInBoat() < PLAYERS_REQUIRED) {
			waitTimer = WAIT_TIMER;
			return;
		}

		Arrays.fill(portalHealth, 200);
		gameTimer = 300;
		knightHealth = 200;
		waitTimer = -1;
		spawnNPC();
		gameStarted = true;

		for (Player player : waitingBoat.keySet()) {
			if (player == null) {
				continue;
			}

			int team = waitingBoat.get(player);
			player.teleport(new Location(2656 + NumberUtil.random(3), 2614 - NumberUtil.random(4)));
			player.sendMessage("@red@The Pest Control game has begun!");
			gamePlayers.put(player, team);
		}

		waitingBoat.clear();
	}

	public void update() {

		if (properTimer > 0) {
			properTimer--;
			return;
		} else {
			properTimer = 1;
		}

		setBoatInterface();

		if (waitTimer > 0) {
			waitTimer--;
		} else if (waitTimer == 0) {
			startGame();
		}

		if (knightHealth == 0) {
			endGame(false);
		}

		if (gameStarted && playersInGame() < 1) {
			endGame(false);
		}

		if (gameTimer > 0 && gameStarted) {
			gameTimer--;
			knightHealth--;
			setGameInterface();

			if (allPortalsDead() || allPortalsDead3()) {
				endGame(true);
			}
		} else if (gameTimer <= 0 && gameStarted) {
			endGame(false);
		}
	}

}
