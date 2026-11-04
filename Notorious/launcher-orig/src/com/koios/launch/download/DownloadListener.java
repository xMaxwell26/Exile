package com.koios.launch.download;

public interface DownloadListener {

	void progressChanged(DownloadChannel downloadChannel, float percent);

}