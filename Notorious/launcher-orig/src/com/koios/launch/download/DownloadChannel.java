package com.koios.launch.download;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.koios.launch.common.HttpUtil;

public final class DownloadChannel implements ReadableByteChannel {

	private final Downloader downloader;

	private final ReadableByteChannel channel;

	private final long expectedLength;

	private long readLength;

	private float lastPercentage = 0F;

	public DownloadChannel(Downloader downloader, ReadableByteChannel channel, long expectedLength) {
		this.downloader = downloader;
		this.channel = channel;
		this.expectedLength = expectedLength;
	}

	public DownloadChannel(Downloader downloader, URL url, long expectedLength) throws IOException {
		this(downloader, Channels.newChannel(url.openStream()), expectedLength);
	}

	public DownloadChannel(Downloader downloader, URL url) throws IOException {
		this(downloader, url, HttpUtil.getContentLength(url));
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		int read = 0;
		float percent = 0F;

		if ((read = channel.read(dst)) > 0) {
			readLength += read;

			if (expectedLength > 0) {
				percent = readLength * 100 / expectedLength;

				if (lastPercentage != percent) {
					lastPercentage = percent;
					downloader.notifyProgressedChanged(this, percent);
				}
			}
		}

		return read;
	}

	public long getReadLength() {
		return readLength;
	}

}