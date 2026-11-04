package com.koios.game.content.friendschat;

import java.util.Objects;
import java.util.concurrent.Future;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.ResetFriendsChatTab;
import com.koios.network.out.impl.SendFriendRank;
import com.koios.network.out.impl.SendRankInfoPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;
import com.koios.util.StringUtil;

/**
 * This class contains functions to satisfy the engine design ...
 */
public final class FriendschatCommons {

	/**
	 * @param player The player to add a friends chat. Not null.
	 * @param ownerName The name of the owner of the friends chat to add. Not null.
	 * @param createIfAbsent Whether the requested friends chat should be created. Note that this is only available when the supplied player is the owner of the requested friends
	 *            chat channel.
	 */
	public static void attemptJoinFriendschat(Player player, String ownerName, boolean createIfAbsent) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(ownerName, "ownerName");

		if (!GameService.isOnlineMode()) {
			player.sendMessage("Unable to join friends chat in debug mode.");
			return;
		}

		player.sendMessage("Attempting to join channel...");
		player.friendschatName = ownerName;
		Friendschat friendschat = GameService.getFriendschatProvider().get(ownerName);

		if (friendschat != null) {
			// Friends chat is available already; add.
			friendschat.add(player);
		} else {
			// Friends chat must be fetched from remote.
			Future<Friendschat> future = GameService.getFriendschatProvider().loadAndGetFuture(ownerName);

			GameService.getTaskScheduler().schedule(new Task(1) {

				// ticks before we give up on this request.
				private int timeoutCount = 10; // 6 seconds

				@Override
				protected void execute() {
					if (player.isDisconnected()) {
						future.cancel(true);
						stop();
					} else if (future.isDone()) {
						try {
							Friendschat friendschat = future.get();

							if (friendschat == null && ownerName.equalsIgnoreCase(player.getName()) && createIfAbsent) {
								friendschat = GameService.getFriendschatProvider().createNew(ownerName);
							}

							if (friendschat != null) {
								friendschat.add(player);
							} else {
								player.friendschatName = null;
								player.sendMessage("That friends chat channel does not exist.");
							}
						} catch (Exception ex) {
							player.friendschatName = null;
							player.sendMessage("Operation aborted because an unexpected error occured. Please try again later.");
							player.getSession().send(new SendStringPacket("Join chat", 18135));
							player.getSession().send(new ResetFriendsChatTab());
							ex.printStackTrace();
						}

						stop();
					} else if (--timeoutCount == 0) {
						player.friendschatName = null;
						player.sendMessage("Operation aborted because the friends chat server timed out. Please try again later.");
						player.getSession().send(new SendStringPacket("Join chat", 18135));
						player.getSession().send(new ResetFriendsChatTab());
						future.cancel(true);
						stop();
					}
				}

			});
		}
	}

	public static boolean buttonPressed(Player player, int id) {
		switch (id) {
		case 49627: // Leave chat
			if (player.friendschatName == null) {
				player.sendMessage("You are not in a friends chat channel.");
			} else {
				Friendschat friendschat = GameService.getFriendschatProvider().get(player.friendschatName);

				if (friendschat != null) {
					friendschat.remove(player, false);
				}
			}

			return true;

		case 18132: // Clan Setup
			if (player.getName().equalsIgnoreCase(player.friendschatName)) {
				Friendschat friendschat = GameService.getFriendschatProvider().get(player.getName());

				if (friendschat != null) {
					player.getSession().send(new SendStringPacket(friendschat.getTopic(), 47814));
					player.getSession().send(new SendStringPacket(formalize(friendschat.getJoinPermissionLevel()), 47815));
					player.getSession().send(new SendStringPacket(formalize(friendschat.getTalkPermissionLevel()), 47816));
					player.getSession().send(new SendStringPacket(formalize(friendschat.getKickPermissionLevel()), 47817));

					for (long nameHash : player.friends) {
						if (nameHash != 0L) {
							String name = StringUtil.hashToUsername(nameHash);
							player.getSession().send(new SendFriendRank(name, friendschat.getPermission(name).ordinal()));
						}
					}

					player.getSession().send(new SendRankInfoPacket());
					player.getInterfaceState().openInterface(40172);
				} else {
					player.sendMessage("You must be in your friends chat channel to access the Clan Setup interface!");
				}
			} else {
				player.sendMessage("You must be in your friends chat channel to access the Clan Setup interface!");
			}

			return true;

		/* Who can enter chat? */

		case 47255:
			setJoinPermission(player, Permission.ANYONE);
			return true;

		case 48007:
			setJoinPermission(player, Permission.FRIEND);
			return true;

		case 48006:
			setJoinPermission(player, Permission.RECRUIT);
			return true;

		case 48005:
			setJoinPermission(player, Permission.CORPORAL);
			return true;

		case 48004:
			setJoinPermission(player, Permission.SERGEANT);
			return true;

		case 48003:
			setJoinPermission(player, Permission.LIEUTNANT);
			return true;

		case 48002:
			setJoinPermission(player, Permission.CAPTAIN);
			return true;

		case 48001:
			setJoinPermission(player, Permission.GENERAL);
			return true;

		case 48000:
			setJoinPermission(player, Permission.OWNER);
			return true;

		/* Who can talk on chat? */

		case 47258:
			setTalkPermission(player, Permission.ANYONE);
			return true;

		case 48017:
			setTalkPermission(player, Permission.FRIEND);
			return true;

		case 48016:
			setTalkPermission(player, Permission.RECRUIT);
			return true;

		case 48015:
			setTalkPermission(player, Permission.CORPORAL);
			return true;

		case 48014:
			setTalkPermission(player, Permission.SERGEANT);
			return true;

		case 48013:
			setTalkPermission(player, Permission.LIEUTNANT);
			return true;

		case 48012:
			setTalkPermission(player, Permission.CAPTAIN);
			return true;

		case 48011:
			setTalkPermission(player, Permission.GENERAL);
			return true;

		case 48010:
			setTalkPermission(player, Permission.OWNER);
			return true;

		/* Who can kick on chat? */

		case 47261:
			setKickPermission(player, Permission.OWNER);
			return true;

		case 48026:
			setKickPermission(player, Permission.RECRUIT);
			return true;

		case 48025:
			setKickPermission(player, Permission.CORPORAL);
			return true;

		case 48024:
			setKickPermission(player, Permission.SERGEANT);
			return true;

		case 48023:
			setKickPermission(player, Permission.LIEUTNANT);
			return true;

		case 48022:
			setKickPermission(player, Permission.CAPTAIN);
			return true;

		case 48021:
			setKickPermission(player, Permission.GENERAL);
			return true;
		}

		return false;
	}

	private static String formalize(Permission permission) {
		Objects.requireNonNull(permission);

		if (permission == Permission.ANYONE) {
			return "Anyone";
		} else if (permission == Permission.OWNER) {
			return "Only me";
		} else if (permission == Permission.FRIEND) {
			return "Any friends";
		}

		String name = permission.name();
		return name.charAt(0) + name.substring(1, name.length()).toLowerCase() + '+';
	}

	private static void setJoinPermission(Player player, Permission permission) {
		if (player.getName().equalsIgnoreCase(player.friendschatName)) {
			Friendschat friendschat = GameService.getFriendschatProvider().get(player.getName());

			if (friendschat != null) {
				friendschat.setJoinPermissionLevel(permission);
				player.getSession().send(new SendStringPacket(formalize(friendschat.getJoinPermissionLevel()), 47815));
			}
		}
	}

	private static void setKickPermission(Player player, Permission permission) {
		if (player.getName().equalsIgnoreCase(player.friendschatName)) {
			Friendschat friendschat = GameService.getFriendschatProvider().get(player.getName());

			if (friendschat != null) {
				friendschat.setKickPermissionLevel(permission);
				player.getSession().send(new SendStringPacket(formalize(friendschat.getKickPermissionLevel()), 47817));
			}
		}
	}

	private static void setTalkPermission(Player player, Permission permission) {
		if (player.getName().equalsIgnoreCase(player.friendschatName)) {
			Friendschat friendschat = GameService.getFriendschatProvider().get(player.getName());

			if (friendschat != null) {
				friendschat.setTalkPermissionLevel(permission);
				player.getSession().send(new SendStringPacket(formalize(friendschat.getTalkPermissionLevel()), 47816));
			}
		}
	}

	// Suppress default constructor to prevent instantiation.
	private FriendschatCommons() {
		throw new InstantiationError("Cannot instantiate FriendschatCommons.");
	}

}
