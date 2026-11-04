package com.koios.network.in.impl;

import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;

/**
 * Dialogue
 */
public class PacketNextDialogue implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		if (player.dialogue().next()) {
			// No need to continue if we were running a new-style dialogue.
			return;
		}

		if (player.nextChat > 0) {
			player.getDH().sendDialogues(player.nextChat, player.dialogueMobId);
		} else {
			player.getDH().sendDialogues(0, -1);
		}

		switch (player.dialogueAction) {
		case 504:
			Bank.open(player);
			break;
		}
	}

}
