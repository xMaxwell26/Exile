package com.koios.game.model.player;

import java.util.List;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.Palette;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.Palette.PaletteTile;
import com.koios.game.model.item.Item;
import com.koios.network.GamePacketBuilder;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.GamePacketBuilder.AccessType;
import com.koios.network.out.impl.SendLocalPosition;

/**
 * Send player actions (packets) to the client
 *
 * @author Gabriel | Wolfs Darker
 */
public class ActionSender {

	public static void sendObjectTransformation(Player player, int objectId, int face) {
		for (Player p : GameService.getWorld().getPlayers()) {
			if (p == null) {
				continue;
			}

			if (Location.getDistance(player.getLocation(), p.getLocation()) <= 20 || p.getIndex() == player.getIndex()) {
				sendObjectTransformation(p, player.getIndex(), objectId, face);
			}
		}
	}

	public static void createPlayersObjectAnim(Player player, int X, int Y, int animationID, int tileObjectType, int orientation) {
		player.getSession().send(new SendLocalPosition(X, Y, false));
		player.write(new GamePacketBuilder(160).writeByteS(((0 & 7) << 4) + (0 & 7)).writeByteS((tileObjectType << 2) + (orientation & 3)).writeShortA(animationID));
	}

	public static void createProjectile(Player player, int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time,
			int slope) {
		player.getSession().send(new SendLocalPosition(x, y, true));
		GamePacketBuilder bldr = new GamePacketBuilder(117);
		bldr.writeByte(angle);
		bldr.writeByte(offY);
		bldr.writeByte(offX);
		bldr.writeShort(lockon);
		bldr.writeShort(gfxMoving);
		bldr.writeByte(startHeight);
		bldr.writeByte(endHeight);
		bldr.writeShort(time);
		bldr.writeShort(speed);
		bldr.writeByte(slope);
		bldr.writeByte(64);
		player.write(bldr);
	}

	/**
	 * Sends the packet to construct a map region.
	 *
	 * @param palette The palette of map regions.
	 * @return The action sender instance, for chaining.
	 */
	public static void sendConstructMapRegion(Player player, Palette palette) {
		player.getCurrentRegion().setAs(player.getLocation());
		GamePacketBuilder bldr = new GamePacketBuilder(241, PacketType.SHORT);
		bldr.writeShortA(player.getLocation().getRegionY() + 6);
		bldr.initializeAccess(AccessType.BIT);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					bldr.writeBits(1, tile != null ? 1 : 0);
					if (tile != null) {
						bldr.writeBits(26, tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1);
					}
				}
			}
		}
		bldr.initializeAccess(AccessType.BYTE);
		bldr.writeShort(player.getLocation().getRegionX() + 6);
		player.write(bldr);
	}

	private static void sendObjectTransformation(Player player, int playerId, int objectId, int face) {

		int tileBits = ((player.getLocation().getLocalX() & 0x7) << 4) + (player.getLocation().getLocalY() & 0x7);
		int objectBits = 10 << 2 | face & 3;
		int object = objectId;

		player.write(new GamePacketBuilder(147).writeByteS(tileBits).writeShort(playerId).writeByteS(objectBits).writeShort(object));
	}

	/**
	 * Displays a interface to the player
	 */
	public static void sendInterface(Player player, int interfaceId) {

		if (player.getInterfaceState().getCurrentInterface() != interfaceId) {
			return;
		}

		player.write(new GamePacketBuilder(97).writeShort(interfaceId));
	}

	/**
	 * Close all interfaces for the player
	 */
	public static void closeInterface(Player player) {
		player.write(new GamePacketBuilder(219));
	}

	/**
	 * Send a array of items to display in the interface
	 */
	public static void sendInterfaceItems(Player player, int interfaceId, Item[] items) {
        com.koios.game.arpg.ItemTooltip.clear(player,interfaceId);
        for(int i=0;i<items.length;i++)if(com.koios.game.arpg.ItemTooltip.supports(items[i]))com.koios.game.arpg.ItemTooltip.send(player,interfaceId,i,items[i]);
		GamePacketBuilder bldr = new GamePacketBuilder(53, PacketType.SHORT);

		bldr.writeShort(interfaceId).writeShort(items.length);

		for (Item item : items) {
			if (item != null) {
				if (item.getCount() > 254) {
					bldr.writeByte(255);
					bldr.writeDoubleInt(item.getCount());
				} else {
					bldr.writeByte(item.getCount());
				}

				bldr.writeLEShortA(item.getId() + 1);
			} else {
				bldr.writeByte(0);
				bldr.writeLEShortA(0);
			}
		}

		player.write(bldr);
	}

	/**
	 * Send a array of items to display in the interface
	 */
	public static void sendInterfaceItems(Player player, int interfaceId, List<Item> items) {
		sendInterfaceItems(player, interfaceId, items.stream().toArray(Item[]::new));
	}

	/**
	 * Sends a packet to update a single item.
	 *
	 * @param interfaceId The interface id.
	 * @param slot The slot.
	 * @param item The item.
	 */
	public static void sendUpdateItem34(Player player, int interfaceId, int slot, Item item) {
        com.koios.game.arpg.ItemTooltip.send(player,interfaceId,slot,item);

		GamePacketBuilder bldr = new GamePacketBuilder(34, PacketType.SHORT);

		bldr.writeShort(interfaceId).writeByte(slot);

		if (item != null) {
			int count = item.getCount();
			bldr.writeShort(item.getId() + 1);

			if (count > 254) {
				bldr.writeByte(255);
				bldr.writeInt(count);
			} else {
				bldr.writeByte(count);
			}
		} else {
			bldr.writeShort(0);
			bldr.writeByte(0);
		}

		player.write(bldr);
	}

	/**
	 * Sends weight on the interface.
	 */
	public static void sendWeight(Player player, int weight) {

		if (weight < 0) {
			return;
		}

		player.write(new GamePacketBuilder(240).writeShort(weight));
	}

	/**
	 * Sends a packet to update multiple (but not all) items.
	 *
	 * @param interfaceId The interface id.
	 * @param slots The slots.
	 * @param items The item array.
	 */
	public static void sendUpdateItems(Player player, int interfaceId, int[] slots, Item[] items) {
        for(int slot:slots)com.koios.game.arpg.ItemTooltip.send(player,interfaceId,slot,items[slot]);
		GamePacketBuilder bldr = new GamePacketBuilder(34, PacketType.SHORT);

		bldr.writeShort(interfaceId);

		for (int slot : slots) {
			Item item = items[slot];
			bldr.writeByte(slot);

			if (item != null) {
				bldr.writeShort(item.getId() + 1);
				int count = item.getCount();

				if (count > 254) {
					bldr.writeByte(255);
					bldr.writeInt(count);
				} else {
					bldr.writeByte(count);
				}
			} else {
				bldr.writeShort(0);
				bldr.writeByte(0);
			}
		}

		player.write(bldr);
	}

	/**
	 * Sends a specific skill.
	 *
	 * @param skill The skill to send.
	 */
	public static void sendSkill(Player player, int skill) {
		player.write(new GamePacketBuilder(134).writeByte(skill).writeSingleInt(player.getSkill().getExperience(skill)).writeByte(player.getSkill().getLevel(skill)));
	}

	/**
	 * Sends a model in an interface.
	 *
	 * @param id The interface id.
	 * @param zoom The zoom.
	 * @param model The model id.
	 */
	public static void sendInterfaceModel(Player player, int id, int zoom, int model) {
		player.write(new GamePacketBuilder(246).writeLEShort(id).writeShort(zoom).writeShort(model));
	}

	public static void sendStillGfx(Player player, int id, Location loc, GraphicHeight height, int time) {
		for (Player p : GameService.getWorld().getPlayers()) {
			if (p == null) {
				continue;
			}

			if (Location.getDistance(player.getLocation(), p.getLocation()) <= 20 || p.getIndex() == player.getIndex()) {
				sendGfx(p, id, loc, height, time);
			}
		}
	}

	public static void sendGfx(Player player, int id, Location loc, GraphicHeight height, int time) {
		GamePacketBuilder bldr = new GamePacketBuilder(85);
		bldr.writeByteC(loc.getY() - (player.getCurrentRegion().getRegionY() << 3));
		bldr.writeByteC(loc.getX() - (player.getCurrentRegion().getRegionX() << 3));
		player.write(bldr);
		bldr = new GamePacketBuilder(4);
		bldr.writeByte(0);
		bldr.writeShort(id);
		bldr.writeByte(height.getHeight());
		bldr.writeShort(time);
		player.write(bldr);
	}

}
