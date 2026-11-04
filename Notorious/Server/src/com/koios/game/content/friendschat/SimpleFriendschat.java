package com.koios.game.content.friendschat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.koios.game.model.player.Player;
import com.koios.network.in.impl.PacketReportAbuse;
import com.koios.network.out.impl.ResetFriendsChatTab;
import com.koios.network.out.impl.SendFriendChatIcon;
import com.koios.network.out.impl.SendFriendschatMessagePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.StringUtil;

/**
 * A representation of a rather simple {@link Friendschat}.
 */
public class SimpleFriendschat implements Friendschat {

	/**
	 * The maximum number of attendees in a friends chat channel.
	 */
	private static final int CHANNEL_CAPACITY = 100;

	/**
	 * The maximum number of channel friends.
	 */
	private static final int PERMISSION_CAPACITY = 200;

	/**
	 * A list holding references to {@link Player players} attending this friends chat.
	 */
	private final List<Player> attendees;

	/**
	 * The minimum {@link Permission} required to add this friends chat.
	 */
	private Permission joinPermissionLevel;

	/**
	 * The minimum {@link Permission} required to kick others from this friends chat.
	 */
	private Permission kickPermissionLevel;

	/**
	 * An indicator to whether this friends chat has been modified by it's owner.
	 */
	private boolean modified = false;

	/**
	 * The username of the owner of this friends chat.
	 */
	private final String ownerName;

	/**
	 * A map containing all friend permissions.
	 */
	private final Map<String, Permission> permissions;

	/**
	 * A reference to the {@link FriendschatProvider} that provided this friends chat.
	 */
	private final FriendschatProvider provider;

	/**
	 * The minimum {@link Permission} required to talk in this friends chat.
	 */
	private Permission talkPermissionLevel;

	/**
	 * The current friends chat topic.
	 */
	private String topic;

	public SimpleFriendschat(FriendschatProvider provider, String ownerName, String topic, Map<String, Permission> permissions, Permission joinPermissionLevel,
			Permission talkPermissionLevel, Permission kickPermissionLevel) {
		this.provider = Objects.requireNonNull(provider, "provider");
		this.ownerName = Objects.requireNonNull(ownerName, "ownerName");
		this.topic = Objects.requireNonNull(topic, "topic");
		this.joinPermissionLevel = Objects.requireNonNull(joinPermissionLevel, "joinPermissionLevel");
		this.talkPermissionLevel = Objects.requireNonNull(talkPermissionLevel, "talkPermissionLevel");
		this.kickPermissionLevel = Objects.requireNonNull(kickPermissionLevel, "kickPermissionLevel");
		this.permissions = Objects.requireNonNull(permissions, "permissions");
		attendees = new ArrayList<>();
	}

	@Override
	public boolean canJoin(Player player) {
		if (player.isSuperAdministrator()) {
			// super administrators can add regardless of permissions
			return true;
		}

		return getPermission(player).ordinal() >= getJoinPermissionLevel().ordinal();
	}

	@Override
	public boolean canKick(Player player, Player target) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(target, "target");

		if (player.isSuperAdministrator()) {
			// super administrators can kick anyone regardless of permissions
			return true;
		} else if (target.isSuperAdministrator()) {
			// the friendschat owner does not have permission to kick super administrators
			return false;
		}

		Permission playerPermission = getPermission(player);
		Permission targetPermission = getPermission(target);
		return playerPermission.ordinal() > targetPermission.ordinal();
	}

	@Override
	public boolean canTalk(Player player) {
		Objects.requireNonNull(player);
		Permission talkPermissionLevel = getTalkPermissionLevel();

		if (player.isMuted()) {
			return false;
		}

		if (talkPermissionLevel == Permission.ANYONE) {
			// in this case we don't need to check any player states (except mutes, of course).
			return true;
		}

		return player.isSuperAdministrator() || getPermission(player).ordinal() >= talkPermissionLevel.ordinal();

	}

	@Override
	public List<Player> getAttendees() {
		// safe copy that does not share references with the internal attendees.
		return Collections.unmodifiableList(new ArrayList<>(attendees));
	}

	@Override
	public Permission getJoinPermissionLevel() {
		return joinPermissionLevel;
	}

	@Override
	public Permission getKickPermissionLevel() {
		return kickPermissionLevel;
	}

	@Override
	public String getOwnerName() {
		return ownerName;
	}

	@Override
	public Permission getPermission(String username) {
		Permission permission = permissions.get(username);
		return permission == null ? Permission.ANYONE : permission;
	}

	@Override
	public Map<String, Permission> getPermissions() {
		return permissions;
	}

	/**
	 * Returns the friendschat permission icon that is more fitting in the situation.
	 *
	 * @param player The player to evaluate. Not null.
	 */
	private int getRankIcon(Player player) {
		if (player.getName().equals(getOwnerName())) {
			return 8;
		} else if (player.isSuperAdministrator()) {
			return 11;
		}

		return getPermission(player).ordinal();
	}

	@Override
	public Permission getTalkPermissionLevel() {
		return talkPermissionLevel;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public boolean isEmpty() {
		return attendees.isEmpty();
	}

	@Override
	public boolean isFull() {
		return attendees.size() >= CHANNEL_CAPACITY;
	}

	@Override
	public boolean isModified() {
		return modified;
	}

	@Override
	public void add(Player player) {
		Objects.requireNonNull(player); // fail fast

		if (joinPermissionLevel != Permission.ANYONE && !player.isSuperAdministrator() && getJoinPermissionLevel().ordinal() > getPermission(player).ordinal()
				&& !player.getName().equals(ownerName)) {
			player.friendschatName = null;
			player.sendMessage("You don't have a high enough rank to enter this friends chat!");

			if (isEmpty()) {
				provider.unload(this);
			}
		} else if (isFull()) {
			// TODO kick lower ranked players when a higher ranked member joins.
			player.friendschatName = null;
			player.sendMessage("The friends chat you are trying to joim is currently full.");
		} else if (attendees.add(player)) {
			if (getOwnerName().equals(player.getName())) {
				for (long nameHash : player.friends) {
					if (nameHash != 0L) {
						String name = StringUtil.hashToUsername(nameHash);

						if (!permissions.containsKey(name)) {
							permissions.put(name, Permission.FRIEND);
						}
					}
				}
			}

			player.sendMessage("Now talking in friends channel: " + getTopic());
			player.sendMessage("To talk, start each line of chat with the / symbol.");
			player.getSession().send(new SendStringPacket("Leave chat", 18135));
			update();
		} else {
			// attendees#add should always be <tt>true</tt>
			throw new AssertionError();
		}
	}

	@Override
	public void remove(Player player, boolean logout) {
		Objects.requireNonNull(player);

		if (attendees.remove(player)) {
			if (!logout) {
				player.friendschatName = null;
				player.getSession().send(new SendStringPacket("Join chat", 18135));
				player.getSession().send(new ResetFriendsChatTab());
				player.sendMessage("You have left the friends chat channel.");
			}

			if (getOwnerName().equals(player.getName())) {
				provider.save(this);
			}

			update();
		}

		if (attendees.isEmpty()) {
			provider.unload(this);
		}
	}

	@Override
	public void sendMessage(String message, Predicate<Player> filter) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(filter, "filter");
		attendees.stream().filter(filter).forEach(p -> p.sendMessage(message));
	}

	@Override
	public void setJoinPermissionLevel(Permission permission) {
		joinPermissionLevel = Objects.requireNonNull(permission, "permission");
		modified = true;
	}

	@Override
	public void setKickPermissionLevel(Permission permission) {
		kickPermissionLevel = Objects.requireNonNull(permission, "permission");
		modified = true;
	}

	@Override
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	@Override
	public boolean setPermission(String username, Permission permission) {
		Objects.requireNonNull(username, "username");
		Objects.requireNonNull(permission, "permission");

		if (permission == Permission.OWNER) {
			throw new IllegalArgumentException();
		}

		// Ranked players must be friends with the friendschat owner, and the
		// friendslist is capped at 200.

		if (permission == Permission.ANYONE) {
			permissions.remove(username);
		} else if (permissions.size() >= PERMISSION_CAPACITY) {
			return false;
		} else {
			permissions.put(username, permission);
		}

		modified = true;
		update();
		return true;
	}

	@Override
	public void setTalkPermissionLevel(Permission permission) {
		talkPermissionLevel = Objects.requireNonNull(permission, "permission");
		modified = true;
	}

	@Override
	public void setTopic(String topic) {
		this.topic = Objects.requireNonNull(topic, "topic");
		update();
		modified = true;
	}

	@Override
	public int size() {
		return attendees.size();
	}

	@Override
	public void talk(Player player, String message) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(message, "message");

		if (canTalk(player)) {
			String msg = message; // effectively final
			PacketReportAbuse.createChatSnapshot(player.getName(), "[Friends Chat -> '" + getOwnerName() + "']: " + msg);
			attendees.forEach(p -> p.getSession().send(new SendFriendschatMessagePacket(player.getName(), msg, getTopic(), player.getChatIconId())));
		} else {
			player.sendMessage("You don't have a high enough rank to talk in this channel.");
		}
	}

	@Override
	public String toString() {
		return String.format("%s [ownerName='%s', topic='%s', add=%s, talk=%s, kick=%s]", getClass().getSimpleName(), getOwnerName(), getTopic(), getJoinPermissionLevel(),
				getTalkPermissionLevel(), getKickPermissionLevel());
	}

	/**
	 * Called when someone left or joined.
	 */
	private void update() {
		// Temporary test to see if this actually occurs:
		// TODO Figure out if this occurs, and if yes, then figure how it occurs.

		for (Iterator<Player> it = attendees.iterator(); it.hasNext();) {
			Player player = it.next();

			if (player.isDisconnected()) {
				it.remove();
			}
		}

		// End of temporary test code.

		Collections.sort(attendees, Player.PLAYER_COMPARATOR_BY_USERNAME);

		attendees.forEach(player -> {
			player.getSession().send(new ResetFriendsChatTab());
			player.getSession().send(new SendStringPacket("Talking in: " + getTopic(), 18139));
			player.getSession().send(new SendStringPacket("Owner: " + getOwnerName(), 18140));

			int index = 18144;

			for (Player tmp : attendees) {
				player.getSession().send(new SendFriendChatIcon(getRankIcon(tmp), index + 20000));
				player.getSession().send(new SendStringPacket(tmp.getName(), index++));
			}
		});
	}

}
