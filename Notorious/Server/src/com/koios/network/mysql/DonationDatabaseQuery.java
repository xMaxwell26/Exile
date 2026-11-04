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
 * Submits a query to the database to check for pending donations
 * 
 * @author Arithium
 *
 */
public class DonationDatabaseQuery implements SQLJob {

	/**
	 * A query to select donations from the database
	 */
	private static final String SELECT_QUERY = "SELECT * FROM `orders` WHERE `member_name` = :username AND `claimed` = '0';";

	/**
	 * The {@link Player} checking for a donation
	 */
	private final Player player;

	/**
	 * Constructs a new {@link DonationDatabaseQuery} to check the database
	 * for pending donations
	 * 
	 * @param player
	 *            The {@link Player} checking for the donation
	 */
	public DonationDatabaseQuery(Player player) {
		this.player = player;
	}

	@Override
	public void executeQuery() {
		final String username = player.getName().toLowerCase();

		try (Connection connection = GameService.getConnection(); Query query = connection.createQuery(SELECT_QUERY)) {
			query.addParameter("member_name", username);

			List<Row> results = query.executeAndFetchTable().rows();

			if (results.isEmpty()) {
				player.sendMessage("You do not have any pending donations.");
				return;
			}
			

			for (Row row : results) {
				final int tokens = row.getInteger("amount");
				//final double price = row.getBigDecimal("amount").doubleValue();
				final int id = row.getInteger("id");
				player.donatorPoints += tokens;
				player.totalDonationAmount += tokens;//price;
				Query update = connection.createQuery("UPDATE `orders` SET `claimed` = '1' WHERE `id` = :id;");
				update.addParameter("id", id);
				update.executeUpdate();
			}
			
			if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
				player.getSession().send(new SendStringPacket("@or1@Donator Points: @gre@" + player.donatorPoints, QuestTabHandler.getLineId(PointsQuestPage.DONATOR_POINTS)));
				player.getSession().send(new SendStringPacket("@or1@Total Donated: @gre@$" + player.totalDonationAmount, QuestTabHandler.getLineId(PointsQuestPage.DONATION_AMOUNT)));
			}

			player.sendMessage("Thank you for your contribution to " + Constants.SERVER_NAME + "!");
			// TODO: Update quest tab to reflect your total donations
		} catch (Exception e) {
			player.sendMessage("The donation redemption system is currently experiencing difficulties.");
			player.sendMessage("Please try again later.");
			e.printStackTrace();
		}
	}

}
