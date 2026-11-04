package com.koios.game.content.friendschat;

import java.util.Collection;
import java.util.concurrent.Future;

public interface FriendschatProvider {

	public Friendschat createNew(String ownerName);

	// Note: does not perform IO operations to load new friendschats.
	public Friendschat get(String ownerName);

	public int getCacheSize();

	public boolean isLoaded(String ownerName);

	public Future<Friendschat> loadAndGetFuture(String ownerName);

	public void save(Friendschat friendschat);

	public void saveAll();

	public boolean unload(Friendschat friendschat);

	public Collection<Friendschat> all();

}
