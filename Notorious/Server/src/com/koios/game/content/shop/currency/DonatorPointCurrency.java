package com.koios.game.content.shop.currency;

import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public final class DonatorPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "Donator points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.donatorPoints;
	}

	@Override
	protected void delete(Player player, int amount) {
		player.donatorPoints -= amount;
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Donator Points: @gre@" + player.donatorPoints, QuestTabHandler.getLineId(PointsQuestPage.DONATOR_POINTS)));
			player.getSession().send(new SendStringPacket("@or1@Total Donated: @gre@$" + player.totalDonationAmount, QuestTabHandler.getLineId(PointsQuestPage.DONATION_AMOUNT)));
		}
	}

	@Override
	protected void add(Player player, int amount) {
		player.donatorPoints += amount;
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Donator Points: @gre@" + player.donatorPoints, QuestTabHandler.getLineId(PointsQuestPage.DONATOR_POINTS)));
			player.getSession().send(new SendStringPacket("@or1@Total Donated: @gre@$" + player.totalDonationAmount, QuestTabHandler.getLineId(PointsQuestPage.DONATION_AMOUNT)));
		}
	}

	@Override
	protected int getSellPrice(Item item) {
		int id = item.getId();
		if (id >= 1038 && id <= 1049) {
			return 18;
		}
		if (id >= 1053 && id <= 1057) {
			return 16;
		}
		if (id >= 20135 && id <= 20167) {
			return 20;
		}
		if (id >= 21787 && id <= 21793) {
			return 15;
		}
		if (id >= 13734 && id <= 13744) {
			return 14;
		}
		switch (id) {
		case 1050:
			return 22;
		case 19111:
			return 8;
		case 11694:
			return 19;
		case 11696:
			return 10;
		case 11698:
			return 15;
		case 11700:
			return 12;
		case 14484:
			return 24;
		case 19780:
			return 25;
		case 18349:
		case 18351:
			return 19;
		case 18353:
			return 21;
		case 18355:
		case 18357:
			return 15;
		}
		return Integer.MAX_VALUE; // default
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}