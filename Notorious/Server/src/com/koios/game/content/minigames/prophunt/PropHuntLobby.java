package com.koios.game.content.minigames.prophunt;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * The lobby for prop hunt games is set here. This class cannot be instantiated. For use, refer to this class through {@code PropHuntLobby.getInstance()}
 *
 * @author Michael | Chex
 */
public class PropHuntLobby extends MinigameLobby {

	/**
	 * A map of the players in the prop hunt game.
	 */
	private static Map<Player, String> players = new HashMap<>();

	/**
	 * Creates an instance of {@link PropHuntLobby}.
	 */
	private static final PropHuntLobby instance = new PropHuntLobby();

	/**
	 * Gets the instance of {@linkplain PropHuntLobby}.
	 */
	public static PropHuntLobby getInstance() {
		return instance;
	}

	/**
	 * The ticks needed for the next game to begin.
	 */
	private static int WAIT_TIME = 120;

	/**
	 * The count down ticks for the next game. This variable decrements 1 second every tick (600ms per tick).
	 */
	private int waitTicks = WAIT_TIME;

	/**
	 * A boolean if the next player joining the minigame will be a hunter.
	 */
	private boolean hunter = false;

	/**
	 * Construct a new {@link PropHuntLobby}.
	 */
	private PropHuntLobby() {
		super(1);
	}

	@Override
	public boolean canTeleport() {
		return true;
	}

	@Override
	public boolean canMove() {
		return true;
	}

	@Override
	public boolean canTrade() {
		return false;
	}

	@Override
	public boolean canLogout() {
		return true;
	}

	@Override
	public boolean canUseSummoning() {
		return false;
	}

	@Override
	public Map<String, Player> getPlayers() {
		return null;
	}

	@Override
	public void handleLogout(Player player) {
		leaveLobby(player);
	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleTeleport(Player player) {
		leaveLobby(player);
		player.sendMessage("You have left the prop hunt lobby.");
	}

	@Override
	public void joinLobby(Player player, Object[] objects) {
		if (getPlayersList().containsKey(player)) {
			getPlayersList().replace(player, (String) objects[0]);
		} else {
			getPlayersList().put(player, (String) objects[0]);
			player.sendMessage("Welcome to the Prop Hunt lobby!");
		}
	}

	@Override
	public boolean isFull() {
		return getPlayersList().size() >= 20;
	}

	@Override
	public Location getJoinLocation() {
		int randomX = Math.random() > 0.5 ? -1 : 1;
		int randomY = Math.random() > 0.5 ? -1 : 1;
		return Location.create(2662 + randomX, 3306 + randomY, 0);
	}

	@Override
	public Location getLeaveLocation() {
		return Location.create(3087, 3501, 0);
	}

	@Override
	public void leaveLobby(Player player) {
		player.getLocation().setAs(new Location(getLeaveLocation().getX(), getLeaveLocation().getY(), player.getZ()));
		getPlayersList().remove(player);
	}

	@Override
	public void startMinigame() {
		hunter = false;

		if (Math.random() > 0.5) {
			hunter = true;
		}

		Player[] toRemove = new Player[getPlayersList().size()];

		for (int index = 0; index < toRemove.length; index++) {
			Player player = (Player) getPlayersList().keySet().toArray()[index];
			toRemove[index] = player;
			player.getMinigame().enterMinigame(PropHunt.getInstance());
			player.sendMessage("You are a <col=FF0000>" + (hunter ? "hunter" : "prop") + "</col>. Good luck!");
		}

		for (Player element : toRemove) {
			getPlayersList().remove(element);
		}
	}

	@Override
	protected void execute() {
		waitTicks -= WAIT_TIME / 10;

		if (waitTicks <= 0) {
			waitTicks = WAIT_TIME;
			// if (getPlayersList().size() == 1) {
			// broadcastMessage("There must be at least 2 players to begin this round.");
			// } else
			{
				startMinigame();
			}
		} else if (waitTicks % 30 == 0) {
			broadcastMessage("The next game will begin in approximatly " + waitTicks / 2 + " seconds.");
		}
	}

	public boolean isHunter() {
		return hunter;
	}

	public void inverseHunter() {
		hunter = !hunter;
	}

	private void broadcastMessage(String message) {
		players.keySet().stream().forEach(p -> p.sendMessage(message));
	}

	@Override
	public Map<Player, String> getPlayersList() {
		return players;
	}

}
