package com.koios.network.in.impl;

import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

public class PacketBankModifiableX implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		if (!player.getBankField().isBanking()) {
			return;
		}

		int itemId = packet.readShortA();
		int amount = packet.readInt();

		if (amount < 1) {
			return;
		}

		Bank.withdraw(player, itemId, amount);
	}

}
