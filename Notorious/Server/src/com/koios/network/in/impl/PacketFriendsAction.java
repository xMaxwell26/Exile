package com.koios.network.in.impl;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.PrivateMessaging;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.AddFriendPacket;
import com.koios.network.out.impl.SendPrivateMessage;
import com.koios.util.StringUtil;

public class PacketFriendsAction implements IncomingPacketDecoder {

	private static final int ADD_FRIEND = 188;
	private static final int ADD_IGNORE = 133;
	private static final int CHANGE_PM_STATUS = 95;
	private static final int REMOVE_FRIEND = 215;
	private static final int REMOVE_IGNORE = 59;
	private static final int SEND_PM = 126;

	@Override
	public void handle(Player player, GamePacket packet) {
		switch (packet.getOpcode()) {
		case ADD_FRIEND:
			long friendHash = packet.readLong();

			if (!StringUtil.checkValid(friendHash)) {
				return;
			}

			for (long friend : player.friends) {
				if (friend != 0 && friend == friendHash) {
					player.sendMessage(friendHash + " is already on your friends list.");
					return;
				}
			}

			for (int i1 = 0; i1 < player.friends.length; i1++) {
				if (player.friends[i1] == 0) {
					player.friends[i1] = friendHash;

					for (Player o : GameService.getWorld().getPlayers()) {
						if (o.getUsernameHash() == friendHash) {
							if (o.privateChat == 0 || o.privateChat == 1 && PrivateMessaging.isLoggedIn(o, player.getUsernameHash())) {
								player.getSession().send(new AddFriendPacket(friendHash, 1));
								break;
							}
						}
					}
					break;
				}
			}
			break;

		case SEND_PM:
			friendHash = packet.readLong();

			if (!StringUtil.checkValid(friendHash)) {
				return;
			}

			if (player.isMuted()) {
				return;
			}

			byte[] text = packet.readBytes(packet.getSize());

			for (long friend : player.friends) {
				if (friend != friendHash) {
					continue;
				}

				Optional<Player> optional = GameService.getWorld().getByUsernameHash(friendHash);
				if (!optional.isPresent()) {
					player.sendMessage("That player is currently offline.");
					return;
				}

				Player o = optional.get();
				if (o.privateChat == 0 || o.privateChat == 1 && PrivateMessaging.isLoggedIn(o, player.getUsernameHash())) {
					o.getSession().send(new SendPrivateMessage(player.getUsernameHash(), player.getChatIconId(), text));
				}
			}

			PacketReportAbuse.createChatSnapshot(player.getName(), "[PM -> " + StringUtil.hashToUsername(friendHash) + "]: " + StringUtil.decode(text, text.length, true));
			break;

		case REMOVE_FRIEND:
			friendHash = packet.readLong();

			if (!StringUtil.checkValid(friendHash)) {
				return;
			}

			for (int index = 0; index < player.friends.length; index++) {
				long friend = player.friends[index];
				if (friend != friendHash) {
					continue;
				}

				Optional<Player> optional = GameService.getWorld().getByUsernameHash(friend);
				optional.ifPresent(p -> PrivateMessaging.updatePrivateStatus(p, player, 0));

				player.friends[index] = 0;
			}
			break;

		case REMOVE_IGNORE:
			long ignore = packet.readLong();

			if (!StringUtil.checkValid(ignore)) {
				return;
			}

			for (int i = 0; i < player.ignores.length; i++) {
				if (player.ignores[i] == ignore) {
					player.ignores[i] = 0;
					break;
				}
			}
			break;

		case CHANGE_PM_STATUS:
			packet.readByte();
			player.privateChat = packet.readByte();
			packet.readByte();
			GameService.getWorld().execute(p -> PrivateMessaging.updatePrivateStatus(p, player, 1));
			break;

		case ADD_IGNORE:
			long ignoreAdd = packet.readLong();
			if (!StringUtil.checkValid(ignoreAdd)) {
				return;
			}

			for (int i = 0; i < player.ignores.length; i++) {
				if (player.ignores[i] == 0) {
					player.ignores[i] = ignoreAdd;
					break;
				}
			}
		}
	}

}