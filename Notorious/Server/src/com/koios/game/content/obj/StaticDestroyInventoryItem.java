package com.koios.game.content.obj;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

public class StaticDestroyInventoryItem {

	public static void destroy(Player player, int id, int slot) {
		String firstLine = getFirstLine(id);
		String secondLine = getSecondLine(id);
		player.awaitResponseDestroyItemId = id;
		player.awaitResponseDestroyItemSlot = slot;
		sendInterfaceItem(player, id, 0, 14171, 1);
		player.getSession().send(new SendStringPacket(firstLine, 14182));
		player.getSession().send(new SendStringPacket(secondLine, 14183));
		player.getSession().send(new SendStringPacket(ItemDefinition.forId(id).getName(), 14184));
		player.getSession().send(new SendChatInterfacePacket(14170));
	}

	private static String getFirstLine(int id) {
		switch (id) {
		case 5733:
		case 20432:
			return "You can only obtain another " + ItemDefinition.forId(id).getName().toLowerCase() + " with";

		case 20748:
			return "You can buy a new completionist cape in Edgeville.";
		}

		return "";
	}

	private static String getSecondLine(int id) {
		switch (id) {
		case 5733:
		case 20432:
			return "administrative commands.";

		case 20748:
			return "It will cost 50,000,000 gp.";
		}

		return "";
	}

	private static void sendInterfaceItem(Player player, int id, int slot, int interfaceId, int amount) {
		GamePacketBuilder bldr = new GamePacketBuilder(34, PacketType.SHORT);
		bldr.writeShort(interfaceId);
		bldr.writeByte(4);
		bldr.writeLEInt(slot);
		bldr.writeShort(id + 1);
		bldr.writeByte(amount);
		player.write(bldr);
	}

	private StaticDestroyInventoryItem() {
	}

}
