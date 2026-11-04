package com.koios.launch.loader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.google.common.hash.Hashing;
import com.koios.launch.common.FormatUtil;
import com.koios.launch.common.HttpUtil;
import com.koios.launch.download.DownloadChannel;
import com.koios.launch.download.DownloadListener;
import com.koios.launch.download.Downloader;
import com.koios.launch.download.FileDownloader;

public final class VersionChecker extends JFrame {
	private static final long serialVersionUID = -4660145968726536143L;

	private static final Logger logger = Logger.getLogger(VersionChecker.class);

	private final Path root = Paths.get(Attributes.getRoot());

	private final Path archive = root.resolve(Attributes.getArchive());

	private final JProgressBar progressBar = new JProgressBar();

	public VersionChecker() {
		super("Koios Updater");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		Container content = getContentPane();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder("Checking for updates...");
		progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		setSize(300, 100);
		requestFocus();
		toFront();
		setResizable(false);
		setVisible(true);
	}

	private boolean archiveExists() throws IOException {
		if (!Files.exists(root)) {
			Files.createDirectories(root);
			return false;
		}

		return Files.exists(archive);
	}

	public void start() {
		try {

			/* If the archive doesn't exist; we need to update. */
			boolean archiveExists = archiveExists();

			String localHash = null;

			if (archiveExists) {
				localHash = Hashing.md5().hashBytes(Files.readAllBytes(archive)).toString();
			}

			URL url = new URL("http://cdn.Koiosps.com/digest.php" + (archiveExists ? "?md5sum=" + localHash : ""));

			HttpURLConnection connection = HttpUtil.getHttpURLConnection(url, "GET");
			connection.connect();

			progressBar.setString("Web connection established!");

			int responseCode = connection.getResponseCode() / 100;
			if (responseCode == 4) {
				progressBar.setString("Webserver unable to find archive!");
				return;
			}

			if (responseCode == 3) {
				progressBar.setString("You're awesome -- no update is required.");
				return;
			}

			if (responseCode == 2) {
				Downloader downloader = new FileDownloader(url, archive, new DownloadListener() {
					@Override
					public void progressChanged(DownloadChannel downloadChannel, float percent) {
						progressBar.setValue((int) percent);
						progressBar.setString(String.format("Progress [bytes=%s, percent=%.02f%%]", FormatUtil.humanReadableByteCount(downloadChannel.getReadLength(), true), percent));
					}
				});

				downloader.run();
			}

		} catch (IOException cause) {
			progressBar.setString("Error occured, check logs file");
			logger.error("Error occured while downloading", cause);
		}
	}

}