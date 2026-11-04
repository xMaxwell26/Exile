package com.koios.network.in.impl;

import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

public class PacketBankAllButOne implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		if (!player.getBankField().isBanking()) {
			return;
		}

		int itemId = packet.readShortA();
		int amount = player.getBank().getCount(itemId);

		if (amount <= 1) {
			// OSRS message - http://2007.runescape.wikia.com/wiki/Bank#Withdrawals
			player.sendMessage("You only have one of this item, so withdrawing all but one does nothing.");
			return;
		}

		Bank.withdraw(player, itemId, amount - 1);
	}

}
