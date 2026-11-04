package com.koios.game.content;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendFriendServerStatusPacket;
import com.koios.network.out.impl.SendMessageServerPacket;

/**
 * Handles private messaging
 * 
 * @author Timothy Howard <Arithium> TODO: Convert all of this to lists
 */
public class PrivateMessaging {

	public static boolean isLoggedIn(Player player, long nameHash) {
		for (long friend : player.friends) {
			if (friend != 0) {
				if (nameHash == friend) {
					return true;
				}
			}
		}

		return false;
	}

	public static void updatePrivateStatus(Player player, Player friend, int world) {
		if (friend == null || "null".equals(friend.getName())) {
			return;
		}

		long l = friend.getUsernameHash();

		if (friend.privateChat == 0) {
			for (long friend2 : player.friends) {
				if (friend2 != 0) {
					if (l == friend2) {
						player.getSession().send(new SendMessageServerPacket(l, world));
						return;
					}
				}
			}
		} else if (friend.privateChat == 1) {
			for (long friend2 : player.friends) {
				if (friend2 != 0) {
					if (l == friend2) {
						if (isLoggedIn(friend, player.getUsernameHash())) {
							player.getSession().send(new SendMessageServerPacket(l, world));
							return;
						} else {
							player.getSession().send(new SendMessageServerPacket(l, 0));
							return;
						}
					}
				}
			}
		} else if (friend.privateChat == 2) {
			for (long friend2 : player.friends) {
				if (friend2 != 0) {
					if (l == friend2) {
						player.getSession().send(new SendMessageServerPacket(l, 0));
						return;
					}
				}
			}
		}
	}

	public static void logIntoPM(Player player) {
		player.getSession().send(new SendFriendServerStatusPacket(2));

		boolean pmLoaded = false;
		for (long friend : player.friends) {
			boolean ignored = false;
			if (friend != 0) {
				for (int i2 = 0; i2 < GameService.getWorld().getPlayers().length; i2++) {
					Player p = GameService.getWorld().getPlayers()[i2];
					if (p == null) {
						continue;
					}
					if (p.getUsernameHash() == friend) {
						for (long ignore : p.ignores) {
							if (ignore != 0) {
								if (ignore == player.getUsernameHash()) {
									ignored = true;
									break;
								}
							}
						}
						if (p.privateChat == 0 || p.privateChat == 1 && isLoggedIn(p, player.getUsernameHash())) {
							player.getSession().send(new SendMessageServerPacket(friend, ignored ? 2 : 1));
							pmLoaded = true;
						}
						break;
					}
				}
				if (!pmLoaded) {
					player.getSession().send(new SendMessageServerPacket(friend, 0));
				}
				pmLoaded = false;
			}
			for (int i1 = 0; i1 < GameService.getWorld().getPlayers().length; i1++) {
				Player p = GameService.getWorld().getPlayers()[i1];
				if (p != null && p.isActive()) {
					Player o = p;
					if (o != null) {
						updatePrivateStatus(o, player, 1);
					}
				}
			}

		}
	}

}