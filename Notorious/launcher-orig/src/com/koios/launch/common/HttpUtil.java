package com.koios.launch.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class HttpUtil {

	public static HttpURLConnection getHttpURLConnection(URL url, String requestMethod) throws IOException {
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		connection.setConnectTimeout(30_000);
		connection.setReadTimeout(10_000);
		connection.setRequestMethod(requestMethod);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.0.13) Gecko/2009073021 Firefox/3.0.13");
		connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
		connection.setRequestProperty("Expires", "0");
		connection.setRequestProperty("Pragma", "no-cache");
		return connection;
	}

	public static long getContentLength(URL url) throws IOException {
		HttpURLConnection connection = getHttpURLConnection(url, "HEAD");
		return connection.getContentLengthLong();
	}

	private HttpUtil() {

	}

}