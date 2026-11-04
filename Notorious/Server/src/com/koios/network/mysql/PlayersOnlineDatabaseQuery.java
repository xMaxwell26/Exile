package com.koios.network.mysql;

import org.sql2o.Connection;
import org.sql2o.Query;

import com.koios.GameService;

/**
 * Updates the database with the current total players online
 * 
 * @author Arithium
 *
 */
public class PlayersOnlineDatabaseQuery implements SQLJob {

	/**
	 * The query to update the total players online
	 */
	private static final String UPDATE_QUERY = "UPDATE playersonline SET `currentlyOnline` = ?;";

	@Override
	public void executeQuery() {
		final int players = GameService.getWorld().getPlayerCount();

		try (Connection connection = GameService.getConnection()) {
			Query update = connection.createQuery(UPDATE_QUERY);
			update.addParameter("currentlyOnline", players);
			update.executeUpdate();
		}
	}

}
