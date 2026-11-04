package com.koios.network.in;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;

public interface IncomingPacketDecoder {

	public void handle(Player player, GamePacket packet);

}
