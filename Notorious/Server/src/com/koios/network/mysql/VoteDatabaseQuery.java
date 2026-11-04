package com.koios.network.mysql;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.data.Row;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Checks the database for a successful vote
 * @author Arithium
 *
 */
public class VoteDatabaseQuery implements SQLJob {
	
	/**
	 * The query for selecting a player
	 */
	private static final String SELECT_QUERY = "SELECT * FROM `mv_auths` INNER JOIN `mv_votes` ON `mv_auths`.`auth` = `mv_votes`.`id` WHERE `ready` = '1' AND `fulfilled` = '0' AND `auth` = :auth;";
	
	/**
	 * The query for updating the database for a player
	 */
	private static final String UPDATE_QUERY = "UPDATE `mv_votes` SET `fulfilled` = '1' WHERE `id` = :id";
	
	/**
	 * The {@link Player} checking for a successful vote
	 */
	private final Player player;
	
	/**
	 * The authorization code for the vote
	 */
	private final String auth;
	
	/**
	 * The amount of points earned for voting
	 */
	private static final int POINTS_PER_VOTE = 3;
	
	/**
	 * Constructs a new {@link VoteDatabaseQuery} to check the database for a successful vote
	 * @param player The {@link Player} checking for a successful vote
	 * @param auth The authorization code for the database
	 */
	public VoteDatabaseQuery(Player player, String auth) {
		this.player = player;
		this.auth = auth;
	}

	@Override
	public void executeQuery() {
		
		try (Connection connection = GameService.getConnection()) {
			
			/*
			 * First try to find a vote
			 */
			Query select = connection.createQuery(SELECT_QUERY);
			select.addParameter("auth", auth);
			
			/*
			 * Create a list of results
			 */
			List<Row> results = select.executeAndFetchTable().rows();
			
			/*
			 * Check if there are any votes available
			 */
			if (results.isEmpty()) {
				player.sendMessage("There are no pending votes available.");
				return;
			}
			
			/*
			 * Loop the results to fetch pending votes
			 */
			for (Row row : results) {
				
				/*
				 * Gets the vote id for the specific vote
				 */
				final int voteId = row.getInteger("vote");
				
				/*
				 * Create a new query to update the database
				 */
				Query update = connection.createQuery(UPDATE_QUERY);
				update.addParameter("id", voteId);
				update.executeUpdate();
				
				player.setVotePoints(player.getVotePoints() + POINTS_PER_VOTE);
				
				if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
					player.getSession().send(new SendStringPacket("@or1@Vote Points: @gre@" + player.getVotePoints(), QuestTabHandler.getLineId(PointsQuestPage.VOTE_POINTS)));
				}
				
				player.sendMessage("Thank you for voting for " + Constants.SERVER_NAME + "!");
			}
		}
	}

}
