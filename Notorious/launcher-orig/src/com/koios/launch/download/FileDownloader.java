package com.koios.launch.download;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileDownloader extends Downloader {

	private static final Logger logger = Logger.getLogger(FileDownloader.class.getName());

	private final FileChannel fileChannel;

	private final DownloadChannel downloadChannel;

	public FileDownloader(URL url, Path root, DownloadListener listener) throws IOException {
		super(listener);
		downloadChannel = new DownloadChannel(this, url);
		fileChannel = FileChannel.open(root, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}

	@Override
	public void run() {
		try {
			fileChannel.transferFrom(downloadChannel, 0, Long.MAX_VALUE);
		} catch (IOException cause) {
			logger.log(Level.SEVERE, "Error occured while downloading", cause);
		}
	}

}