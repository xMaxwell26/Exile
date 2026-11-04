package com.koios.network.in.impl;

import java.util.Arrays;

import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.util.StringUtil;

public class PacketBankSearch implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		long textAsLong = packet.readLong();
		if (!StringUtil.checkValid(textAsLong)) {
			return;
		}
		String text = StringUtil.hashToUsername(textAsLong);
		text = text.replaceAll("_", " ").trim().toLowerCase();
		searchFor(player, text);
	}

	public static void searchFor(Player player, String keyword) {
		player.getBankField().setSearching(true);
		player.getBankField().setSearchText(keyword);
		player.getSession().send(new SendConfigPacket(1000 + player.getBankField().getTabIndex(), 0));
		if (keyword == null) {
			return;
		}
		keyword = keyword.trim().toLowerCase();
		Item[] resultContainer = new Item[Bank.SIZE];
		int caret = 0;

		if (keyword.length() > 0) {
			for (Item item : player.getBank().toArray()) {
				if (item == null) {
					continue;
				}

				if (item.getDefinition() == null) {
					continue;
				}

				if (item.getDefinition().getName() == null) {
					continue;
				}

				if (item.getDefinition().getName().replaceAll("_", " ").trim().toLowerCase().contains(keyword)) {
					resultContainer[caret++] = item;
				}
			}
		} else {
			int from = player.getBankField().getOffset(player.getBankField().getTabIndex());
			int to = from + player.getBankField().getTabAmounts()[player.getBankField().getTabIndex()];
			resultContainer = Arrays.copyOf(Arrays.copyOfRange(player.getBank().toArray(), from, to), Bank.SIZE);
		}

		ActionSender.sendInterfaceItems(player, Bank.BANK_INVENTORY_INTERFACE, resultContainer);
	}

}
