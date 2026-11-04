package com.koios.network.out.impl;

import com.koios.Constants;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

/**
 * Adds a friend to the friend list
 * 
 * @author Timothy Howard <Arithium>
 */
public class AddFriendPacket implements OutboundPacket {

	/**
	 * The username hash of the player
	 */
	private final long usernameHash;

	/**
	 * The world to write the player in
	 */
	private int world;

	/**
	 * Constructs a new {@link OutboundPacket} to add a friend to the friends list
	 * 
	 * @param usernameHash The username hash of the player to add
	 * @param world The world the player is in
	 */
	public AddFriendPacket(long usernameHash, int world) {
		this.usernameHash = usernameHash;
		this.world = world;
	}

	@Override
	public void execute(Session client) {
		if (world != 0) {
			world += 9;
		} else if (!Constants.WORLD_LIST_FIX) {
			world += 1;
		}

		client.getPlayer().write(new GamePacketBuilder(50).writeLong(usernameHash).writeByte(world));
	}

}
