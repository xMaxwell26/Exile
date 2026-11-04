package com.koios.game.content.minigames.fightpits;

import java.util.HashMap;
import java.util.Map;

import com.koios.GameService;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

public class FightPitsLobby extends MinigameLobby {

	private final Map<String, Player> players;

	public FightPitsLobby() {
		super(1);
		players = new HashMap<String, Player>();
		GameService.getTaskScheduler().schedule(this);
	}

	public static boolean inside(Player player) {
		return player.getLocation().getX() <= 2404 && player.getLocation().getX() >= 2394 && player.getLocation().getY() <= 5175 && player.getLocation().getY() >= 5169;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canLogout() {
		return false;
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
	public boolean canUseSummoning() {
		return false;
	}

	@Override
	public Map<String, Player> getPlayers() {
		return players;
	}

	@Override
	public void handleLogout(Player player) {
		if (player == null) {
			return;
		}

		player.teleport(getLeaveLocation());
		players.remove(player.getName());
	}

	@Override
	public void handleLogin(Player player) {
		if (player == null) {
			return;
		}

		players.put(player.getName(), player);
	}

	@Override
	public void handleTeleport(Player player) {
	}

	@Override
	public void joinLobby(Player player, Object[] objects) {
		if (player == null || players == null) {
			return;
		}

		players.put(player.getName(), player);
	}

	@Override
	public boolean isFull() {
		return players.size() > 16;
	}

	@Override
	public Location getJoinLocation() {
		return new Location(2399, 5175, 0);
	}

	@Override
	public Location getLeaveLocation() {
		return new Location(2399, 5177, 0);
	}

	@Override
	public void leaveLobby(Player player) {
	}

	@Override
	public void startMinigame() {
	}

	@Override
	protected void execute() {
	}

	@Override
	public Map<Player, String> getPlayersList() {
		return null;
	}

}
