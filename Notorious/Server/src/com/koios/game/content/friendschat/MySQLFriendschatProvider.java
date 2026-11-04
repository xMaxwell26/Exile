package com.koios.game.content.friendschat;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.data.Row;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.util.StringUtil;

public class MySQLFriendschatProvider implements FriendschatProvider {

	/**
	 * A {@link Type} instance for parsing serialized JSON data.
	 */
	private static final Type HASHMAP_TYPE_1 = new TypeToken<Map<String, Permission>>() {

	}.getType();

	/**
	 * A {@link Map} holding friend chats currently in use.
	 */
	private final Map<String, Friendschat> cache;

	/**
	 * A {@link Gson} instance to encode and decode JSON strings for storage.
	 */
	private final Gson gson;

	public MySQLFriendschatProvider() {
		cache = new ConcurrentHashMap<>();
		gson = new Gson();
	}

	@Override
	public Collection<Friendschat> all() {
		return Collections.unmodifiableCollection(cache.values());
	}

	@Override
	public Friendschat createNew(String ownerName) {
		Objects.requireNonNull(ownerName);
		ownerName = StringUtil.formatUsername(ownerName);
		Friendschat friendschat = cache.get(ownerName);

		if (friendschat == null) {
			Friendschat newFriendschat = new SimpleFriendschat(this, ownerName, ownerName, new HashMap<>(), Permission.ANYONE, Permission.ANYONE, Permission.OWNER);
			friendschat = cache.putIfAbsent(ownerName, newFriendschat);

			if (friendschat == null) {
				friendschat = newFriendschat;
			}
		}

		friendschat.setModified(true);
		save(friendschat);
		return friendschat;
	}

	@Override
	public Friendschat get(String ownerName) {
		Objects.requireNonNull(ownerName, "null ownerName");
		return cache.get(StringUtil.formatUsername(ownerName));
	}

	@Override
	public int getCacheSize() {
		return cache.size();
	}

	@Override
	public boolean isLoaded(String ownerName) {
		Objects.requireNonNull(ownerName, "ownerName");
		return cache.containsKey(StringUtil.formatUsername(ownerName));
	}

	@Override
	public Future<Friendschat> loadAndGetFuture(String ownerName) {
		Objects.requireNonNull(ownerName, "ownerName");
		String name = StringUtil.formatUsername(ownerName);
		FutureTask<Friendschat> task = new FutureTask<>(() -> {
			Friendschat friendschat = cache.get(name);
			if (friendschat != null) {
				return friendschat;
			}

			try (Connection connection = GameService.getConnection(); Query query = connection.createQuery("SELECT * FROM `friendschat` WHERE `name` = :name LIMIT 1")) {
				query.addParameter("name", name);

				List<Row> results = query.executeAndFetchTable().rows();
				if (results.isEmpty()) {
					return null;
				}

				for (Row result : results) {
					String topic = result.getString("topic");
					Permission join = Permission.values()[result.getByte("enter")];
					Permission talk = Permission.values()[result.getByte("talk")];
					Permission kick = Permission.values()[result.getByte("kick")];
					Map<String, Permission> permissions = gson.fromJson(result.getString("permissions"), HASHMAP_TYPE_1);
					Friendschat newFriendschat = new SimpleFriendschat(MySQLFriendschatProvider.this, name, topic, permissions, join, talk, kick);
					cache.putIfAbsent(name, newFriendschat);
					friendschat = newFriendschat;
				}

				return friendschat;
			}
		});

		GameService.execute(task);
		return task;
	}

	@Override
	public void save(Friendschat friendschat) {
		if (!friendschat.isModified()) {
			// Nothing changed, so we do not need to update!
			return;
		}

		// avoid multiple calls to save the same friendschat
		friendschat.setModified(false);

		// asynchronously update (or insert) the friendschat settings
		GameService.execute(() -> {
			String name = friendschat.getOwnerName();
			String topic = friendschat.getTopic();
			String perms = "{}";
			int enter = friendschat.getJoinPermissionLevel().ordinal();
			int talk = friendschat.getTalkPermissionLevel().ordinal();
			int kick = friendschat.getKickPermissionLevel().ordinal();

			String sql = "INSERT INTO `friendschat` " + "(`name`, `topic`, `enter`, `talk`, `kick`, `permissions`) VALUES(" + "'" + name + "', " + "'" + topic + "', " + "'" + enter
					+ "', " + "'" + talk + "', " + "'" + kick + "', " + "'" + perms + "') ON DUPLICATE KEY UPDATE "
					+ "`topic` = VALUES(`topic`), `enter` = VALUES(`enter`), `talk` = VALUES(`talk`), " + "`kick` = VALUES(`kick`), `permissions` = VALUES(`permissions`);";

			try (Connection connection = GameService.getConnection(); Query query = connection.createQuery(sql)) {
				query.executeUpdate();
			}
		});
	}

	@Override
	public void saveAll() {
		cache.values().forEach(this::save);
	}

	@Override
	public boolean unload(Friendschat friendschat) {
		// have all players remove the friendschat first.
		if (!friendschat.isEmpty()) {
			for (Player player : friendschat.getAttendees()) {
				friendschat.remove(player, false);
			}

			// Note: recursive function
			// When the last player in a friends chat leaves; this function is called to unload
			// the friends chat from the system.
			return true;
		} else {
			save(friendschat);
			return cache.remove(friendschat.getOwnerName(), friendschat);
		}
	}

}
