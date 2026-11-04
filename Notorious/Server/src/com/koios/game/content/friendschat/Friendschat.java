package com.koios.game.content.friendschat;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.koios.game.model.player.Player;

/*
 * This interface exists because I want to allow future implementation of "help chats" that are not directly friends
 * chats. Ie. to have permissions decided purely by account status (all player moderators => channel admins etc)
 */

public interface Friendschat {

	public boolean canJoin(Player player);

	public boolean canKick(Player player, Player target);

	public boolean canTalk(Player player);

	public List<Player> getAttendees();

	public Permission getJoinPermissionLevel();

	public Permission getKickPermissionLevel();

	public String getOwnerName();

	public default Permission getPermission(Player player) {
		Objects.requireNonNull(player, "null player");
		return getPermission(player.getName());
	}

	public Permission getPermission(String username);

	public Map<String, Permission> getPermissions();

	public Permission getTalkPermissionLevel();

	public String getTopic();

	public boolean isEmpty();

	public boolean isFull();

	public boolean isModified();

	public void add(Player player);

	public void remove(Player player, boolean logout);

	public default void sendMessage(String message) {
		sendMessage(message, p -> true); // send to all attendees
	}

	public void sendMessage(String message, Predicate<Player> filter);

	public void setJoinPermissionLevel(Permission permission);

	public void setKickPermissionLevel(Permission permission);

	public void setModified(boolean modified);

	public boolean setPermission(String username, Permission permission);

	public void setTalkPermissionLevel(Permission permission);

	public void setTopic(String topic);

	public int size();

	public void talk(Player player, String message);

}
