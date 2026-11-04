package com.koios.game.content.questtab.impl;

import com.koios.game.content.questtab.QuestPage;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.model.player.Player;

public class PointsQuestPage extends QuestPage {
	
	public static final int PKING_POINTS = 1, VOTE_POINTS = 2, SLAYER_POINTS = 3, DONATOR_POINTS = 4, DONATION_AMOUNT = 5;

	@Override
	public void write(Player player) {
		writeLine(player, PKING_POINTS, "@or1@Pk Points: @gre@" + player.pvpPoints);
		writeLine(player, VOTE_POINTS, "@or1@Vote Points: @gre@" + player.getVotePoints());
		writeLine(player, SLAYER_POINTS, "@or1@Slayer Points: @gre@" + player.slayerPoints);
		writeLine(player, DONATOR_POINTS, "@or1@Donator Points: @gre@" + player.donatorPoints);
		writeLine(player, DONATION_AMOUNT, "@or1@Total Donated: @gre@$" + player.totalDonationAmount);
		writeLine(player, 7, "[Back]");
	}

	@Override
	public boolean buttonClick(Player player, int buttonId) {
		if (buttonId == 129008) {
			QuestTabHandler.write(player, QuestPages.HOME);
		}
		return false;
	}

	@Override
	public String title() {
		return "Points";
	}

}
