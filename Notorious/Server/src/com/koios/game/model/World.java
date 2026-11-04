package com.koios.game.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.PrivateMessaging;
import com.koios.game.content.activity.PestControl;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.combat.DwarfMultiCannon;
import com.koios.game.content.friendschat.Friendschat;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.game.model.player.update.EntityUpdater;
import com.koios.game.model.player.update.MobUpdater;
import com.koios.game.model.player.update.PlayerUpdater;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.impl.SystemUpdateTask;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

public final class World {

	/**
	 * An array containing all of the players
	 */
	// TODO: Entity collection
	private final Player[] players = new Player[Constants.MAX_PLAYERS];

	/**
	 * Handles updating players
	 */
	private final EntityUpdater playerUpdater = new PlayerUpdater();

	/**
	 * Handles updating mobs
	 */
	private final EntityUpdater mobUpdater = new MobUpdater();

	/**
	 * A Map of online Players.
	 */
	private final Map<Long, Player> onlinePlayers = new HashMap<>();

	/**
	 * The update task for this World.
	 */
	private SystemUpdateTask updateTask;

	/**
	 * The amount of players online
	 */
	private int playersOnline;

	/**
	 * Registers a new {@link Player} to the game world
	 *
	 * @param player The {@link Player} to register to the game world
	 * @return if the player was registered
	 */
	public boolean register(Player player) {
		int index = -1;

		for (int i = 1; i < Constants.MAX_PLAYERS; i++) {
			if (players[i] == null || players[i].isDisconnected()) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			return false;
		}

		player.setIndex(index);
		players[index] = player;
		players[index].setActive(true);
		onlinePlayers.put(player.getUsernameHash(), player);
		playersOnline++;
		player.login();
		System.out.println("Registered: [Username=" + player.getName() + ", Rights: " + player.getPrivilege() + "]");
		execute(p -> p.getSession().send(new SendStringPacket("Players Online: @gre@" + getPlayerCount(), 640)));
		return true;
	}

	/**
	 * Unregisters a player from the world
	 *
	 * @param player The {@link Player} to unregister from the world
	 */
	public void unregister(Player player) {
        com.koios.game.arpg.TestArena.cleanup(player);com.koios.game.arpg.MapRuns.cleanup(player);
		final int index = player.getIndex();
		final long usernameHash = player.getUsernameHash();
		if (index < 0 || index >= players.length) {
			return;
		}
		if (players[index] == null || player.isDisconnected()) {
			// already disconnected, don't attempt it again
			return;
		}
		
		//GameService.submit(new HiscoresDatabaseQuery(player));
		player.setDisconnected(true);
		destruct(player);

		onlinePlayers.remove(usernameHash);
		players[player.getIndex()] = null;
		playersOnline--;
		System.out.println("Unregistered: [Username=" + player.getName() + ", Rights: " + player.getPrivilege() + "]");
		execute(p -> p.getSession().send(new SendStringPacket("Players Online: @gre@" + getPlayerCount(), 640)));
	}

	/**
	 * Destructs the player before removing them from the world
	 *
	 * @param player The {@link Player} to remove from the world
	 */
	private void destruct(Player player) {
		player.lastLogout = System.currentTimeMillis();

		if (player.privateChat != 2) {
			execute(target -> PrivateMessaging.updatePrivateStatus(target, player, 0));
		}

		player.getSession().send(new SendRemoveInterfacePacket());

		if (player.getMinigame().inMinigame() || player.getMinigame().inMinigameLobby()) {
			player.getMinigame().handleLogout();
		}

		player.saveCharacter = true;

		if (player.getCurrentSkill() != null) {
			player.getCurrentSkill().setMustAbort(true);
		}

		if (PestControl.isInPestControlBoat(player)) {
			PestControl.removePlayerGame(player);
			player.setPosition(2440, 3089, 0);
		}

		FightPits.getInstance().PARTICIPANTS.remove(player);
		FightPits.getInstance().WAITING_PLAYERS.remove(player);

		if (player.getSlayer().getPartner() != null) {
			player.getSlayer().getPartner().sendMessage("@red@" + player.getName() + " has disconnected. The co-op session has ended.");
			player.getSlayer().getPartner().getSlayer().setPartner(null);
		}

		if (player.hasCannon) {
			WorldObject cannon = null;

			for (WorldObject worldObject : GameService.getObjectHandler().globalObjects) {
				if (worldObject != null && worldObject.owner == player.getUsernameHash() && worldObject.getID() == DwarfMultiCannon.CANNON) {
					cannon = worldObject;
				}
			}

			if (cannon != null) {
				player.getDwarfMultiCannon().takeMultiCannon(cannon);
			}
		}

		player.getSummoning().handleLogout();

		if (player.friendschatName != null) {
			Friendschat friendschat = GameService.getFriendschatProvider().get(player.friendschatName);

			if (friendschat != null) {
				friendschat.remove(player, true);
			}
		}
		
		player.getTaskScheduler().stopAll();

		player.setActive(false);
		PlayerSaver.save(player);
		player.destruct();
	}

	/**
	 * Gets a filtered Stream of valid registered Players.
	 *
	 * @return A filtered Stream of valid Players.
	 */
	public Stream<Player> playerStream() {
		return Stream.of(players).filter(p -> p != null && !p.isDisconnected() && p.isActive());
	}

	/**
	 * Gets the array containing all of the registered players
	 *
	 * @return An array containing registered players
	 */
	public Player[] getPlayers() {
		return playerStream().toArray(Player[]::new);
	}

	/**
	 * Executes the specified Consumer action for every online Player.
	 *
	 * @param action The Consumer action to perform.
	 */
	public void execute(Consumer<? super Player> action) {
		playerStream().forEach(action);
	}

	/**
	 * Executes the specified Consumer action for every online Player if the predicate test true.
	 *
	 * @param predicate A {@link Predicate} determining whether the Player should perform the specified Consumer action.
	 * @param action The Consumer action to perform.
	 */
	public void execute(Predicate<? super Player> predicate, Consumer<? super Player> action) {
		playerStream().filter(predicate).forEach(action);
	}

	/**
	 * Sends a server message to all currently connected players.
	 * <p>
	 * This function is equal to {@code PlayerHandler.broadcast(message, p -> true)}.
	 *
	 * @param message The message to be sent to all players.
	 */
	public void broadcast(String message) {
		broadcast(message, p -> true);
	}

	/**
	 * Broadcasts a message to every player.
	 *
	 * @param message The message.
	 * @param filter A {@link Predicate} determining whether a player should receive the message.
	 */
	public void broadcast(String message, Predicate<Player> filter) {
		execute(filter, p -> p.sendMessage(message));
	}

	/**
	 * USE FOR PACKETS ONLY UNTIL AN APPROPIATE ENTITY REPO HAS BEEN ADDED
	 */
	public Player getPlayer(int index) {
		Preconditions.checkElementIndex(index, players.length);
		return players[index];
	}
	
	private static int modifier;
	
	static {
		modifier = NumberUtil.random(7);
		if (modifier < 2) {
			modifier = 2;
		}
	}

	public int getPlayerCount() {
		return playersOnline;
	}

	/**
	 * Returns the first available {@link Player} who fulfills the contract of the supplied {@link Predicate}.
	 *
	 * @param filter The filter. Not null.
	 * @return A {@link Optional} containing the first match (if any). Not null.
	 */
	public Optional<Player> get(Predicate<Player> filter) {
		// findAny is relatively faster than findFirst
		return playerStream().filter(filter).findAny();
	}

	/**
	 * Returns an Optional containing an instance of {@link Player} representing the player with the given username, if they are logged in. The will not be present if the player is
	 * not logged in.
	 *
	 * @param username The username of the targeted player. Not null.
	 * @return An {@link Optional} player with the specified username. Not null.
	 */
	public Optional<Player> getByUsername(String username) {
		return getByUsernameHash(StringUtil.usernameToHash(username));
	}

	public Optional<Player> getByUsernameHash(long hash) {
		Player plr = onlinePlayers.get(hash);
		return Optional.ofNullable(plr);
	}

	public boolean isPlayerLoggedIn(String username) {
		return getByUsername(username).isPresent();
	}

	public List<Player> listAll(Predicate<Player> filter) {
		return playerStream().filter(filter).collect(Collectors.toList());
	}

	/**
	 * Handles updating all of the registered players
	 */
	public void update() {
		execute(Player::process);
		execute(this::update);
		execute(Player::clearUpdateFlags);
	}

	public void update(Player player) {
		playerUpdater.update(player);
		mobUpdater.update(player);
	}

	public void startUpdate(SystemUpdateTask updateTask) {
		this.updateTask = updateTask;

		GameService.getTaskScheduler().schedule(updateTask);
	}

	public void cancelUpdate() {
		if (updateTask != null) {
			updateTask.cancel();
			updateTask = null;
		}
	}

	public boolean isSystemUpdating() {
		return updateTask != null;
	}

}
