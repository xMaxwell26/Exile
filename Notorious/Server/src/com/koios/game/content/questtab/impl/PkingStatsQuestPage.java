package com.koios.game.content.questtab.impl;

import com.koios.game.content.questtab.QuestPage;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.model.player.Player;

public class PkingStatsQuestPage extends QuestPage {
	
	public static final int KILLS = 1, DEATHS = 2, KDR = 3;
	
	private static final int BACK = 5;

	@Override
	public void write(Player player) {
		writeLine(player, KILLS, "@or1@Kill Count: " + (player.killCount > player.deathCount ? "@gre@" : player.killCount < player.deathCount ? "@red@" : "@yel@") + "" + player.killCount);
		writeLine(player, DEATHS, "@or1@Death Count: @gre@" + player.deathCount);
		writeLine(player, KDR, "@or1@KDR: @gre@" + player.getKDR());
		writeLine(player, BACK, "[Back]");
	}

	@Override
	public boolean buttonClick(Player player, int buttonId) {
		if (buttonId == 129006) {
			QuestTabHandler.write(player, QuestPages.HOME);
			return true;
		}
		return false;
	}

	@Override
	public String title() {
		return "Pking Stats";
	}

}
