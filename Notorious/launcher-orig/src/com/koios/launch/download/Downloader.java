package com.koios.launch.download;

public abstract class Downloader implements Runnable {

	private final DownloadListener listener;

	public Downloader(DownloadListener listener) {
		this.listener = listener;
	}

	public void notifyProgressedChanged(DownloadChannel channel, float percent) {
		listener.progressChanged(channel, percent);
	}

}