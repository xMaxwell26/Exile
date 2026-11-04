package com.koios.game.content.shop.currency;

import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public class VotePointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Vote Points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.getVotePoints();
	}

	@Override
	protected void delete(Player player, int amount) {
		player.setVotePoints(getAmount(player) - amount);
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Vote Points: @gre@" + player.getVotePoints(), QuestTabHandler.getLineId(PointsQuestPage.VOTE_POINTS)));
		}
	}

	@Override
	protected void add(Player player, int amount) {
		player.setVotePoints(getAmount(player) + amount);
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Vote Points: @gre@" + player.getVotePoints(), QuestTabHandler.getLineId(PointsQuestPage.VOTE_POINTS)));
		}
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {

		default:
			return 500;
		}
	}

	@Override
	protected int getBuyPrice(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}

}
