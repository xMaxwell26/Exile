package com.koios.game.content.shop.currency;

import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.content.shop.PointCurrency;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public final class PkingPointCurrency extends PointCurrency {

	@Override
	protected String getName() {
		return "PK points";
	}

	@Override
	protected int getAmount(Player player) {
		return player.pvpPoints;
	}

	@Override
	protected void delete(Player player, int amount) {
		player.pvpPoints -= amount;
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Pk Points: @gre@" + player.pvpPoints, QuestTabHandler.getLineId(PointsQuestPage.PKING_POINTS)));
		}
	}

	@Override
	protected void add(Player player, int amount) {
		player.pvpPoints += amount;
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Pk Points: @gre@" + player.pvpPoints, QuestTabHandler.getLineId(PointsQuestPage.PKING_POINTS)));
		}
	}

	@Override
	protected int getSellPrice(Item item) {
		switch (item.getId()) {
		case 4708:
		case 4712:
		case 4714:
		case 4716:
		case 4718:
		case 4720:
		case 4722:
		case 4745:
		case 4749:
		case 4751:
		case 6914:
		case 6889:
		case 4736:
		case 4738:
			return 10;
		case 6585:
			return 15;
		case 4151:
			return 15;
		case 6737:
		case 6731:
		case 6733:
		case 6735:
			return 20;
		case 15243:
			return 1;
		case 15272:
			return 2;
		case 6920:
			return 10;
		case 11732:
			return 15;
		case 18830:
			return 1;
		case 11235:
			return 10;
		case 19111: //
			return 75;
		case 20171:
			return 300;
		case 13664:
			return 10;
		case 15241:
			return 150;
		case 13887:
		case 13893:
		case 13858:
		case 13861:
		case 13870:
		case 13873:
			return 150;

		case 13662:
			return 40;
		case 13671:
			return 50;
		case 13661:
			return 60;
		case 13660:
			return 100;
		case 13666:
			return 140;
		case 13667:
			return 180;
		case 13668:
			return 200;
		case 10548: // fighter hat
			return 15;
		case 18347:
			return 50;
		case 6199: // mystery box
			return 8;
		case 10551: // fighter torso
			return 15;
		case 20072: // dragon defender
			return 20;
		case 13736: // blessed spirit shield
			return 65;
		case 21371: // vine whip
			return 225;
		case 17273: // flameburst defender
			return 25;
		case 15300: // spec recover
			return 2;
		case 15332: // overload
			return 2;
		case 13744: // spectral ss
			return 200;
		case 13738: // arcane ss
			return 250;
		case 20000: // steads
		case 20001:
		case 20002:
			return 175;
		case 15220: // imbue rings
		case 15020:
		case 15019:
		case 15018:
			return 50;
		case 18335: // arcane stream
			return 65;
		case 18359:
		case 18363:
		case 18361:
			return 125;
		case 11698:
		case 11700:
		case 11696:
			return 100;
		case 11730:
		case 11716:
			return 35;
		case 19669: // ring of vigour
			return 145;
		case 11694: // ags
			return 450;
		case 14484: // dragon claws
			return 400;
		case 18349: // chaotic rapier
			return 285;
		case 18351: // chaotic longsword
			return 295;
		case 18353: // chaotic maul
			return 325;
		case 18355: // chaotic staff
			return 325;
		case 19780: // korasi
			return 125;
		case 18357: // chaotic crossbow
			return 245;
		case 15486: // staff of light
			return 50;
		case 15017:
			return 60;
		case 8841:
			return 25;
		case 21787:
		case 21790:
		case 21793:
			return 150;
		case 11718:
		case 11720:
		case 11722:
		case 11724:
		case 11726:
		case 11728:
			return 100;
		case 13740:
			return 5000;
		case 13742:
			return 300;
		}
		return Integer.MAX_VALUE; // default
	}

	@Override
	protected int getBuyPrice(Item item) {
		return 0;
	}

}