package com.koios.game.content.questtab.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.koios.game.content.questtab.QuestPage;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.model.player.Player;

public class HomeQuestPage extends QuestPage {

	public static final int VOTE_POINTS = 1;
	public static final int SLAYER_POINTS = 2;
	public static final int ACHIEVEMENT_POINTS = 3;
	public static final int PK_POINTS = 4;

	@Override
	public void write(Player player) {
		writeLine(player, VOTE_POINTS, "           @or1@[Server Uptime]");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		writeLine(player, SLAYER_POINTS, "                @or2@" + sdf.format(cal.getTime()));
		writeLine(player, ACHIEVEMENT_POINTS, "");
		writeLine(player, PK_POINTS, "          @or1@- Click To View -");
		writeLine(player, 5, "               [Points]");
		writeLine(player, 6, "             [Pking Stats]");
		writeLine(player, 7, "              [Boss Log]");
		writeLine(player, 8, "          [Quick Teleports]");
		writeLine(player, 9, "             [Help Links]");
	}

	@Override
	public boolean buttonClick(Player player, int buttonId) {
		if (buttonId == 129006) {
			QuestTabHandler.write(player, QuestPages.POINTS);
		} else if (buttonId == 129007) {
			QuestTabHandler.write(player, QuestPages.PKING);
		} else if (buttonId == 129008) {
			player.sendMessage("Coming soon!");
			//QuestTabHandler.write(player, QuestPages.BOSS);
		} else if (buttonId == 129009) {
			QuestTabHandler.write(player, QuestPages.TELEPORTS);
		} else if (buttonId == 129010) {
			QuestTabHandler.write(player, QuestPages.HELP);
		}
		return false;
	}

	@Override
	public String title() {
		return "Home";
	}

}
