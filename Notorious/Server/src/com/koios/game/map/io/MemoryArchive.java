package com.koios.game.map.io;

public class MemoryArchive {

	private static final int INDEX_DATA_CHUNK_SIZE = 12;

	private final ByteStream cache;
	private final ByteStream index;

	public MemoryArchive(ByteStream cache, ByteStream index) {
		this.cache = cache;
		this.index = index;
	}

	public int getContentSize() {
		return index.length() / 12;
	}

	public byte[] get(int dataIndex) {
		try {
			if (index.length() < dataIndex * INDEX_DATA_CHUNK_SIZE) {
				return null;
			}

			index.setOffset(dataIndex * INDEX_DATA_CHUNK_SIZE);
			long fileOffset = index.getLong();
			int fileSize = index.getInt();
			cache.setOffset(fileOffset);
			byte[] buffer = cache.read(fileSize);
			return buffer;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
