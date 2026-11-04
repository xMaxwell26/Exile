package com.koios.launch.loader.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

public final class LoaderAppletContext implements AppletContext {

	private final Logger logger = Logger.getLogger(LoaderAppletContext.class);

	private final Map<String, InputStream> streams;

	private final Vector<Applet> applets;

	public LoaderAppletContext() {
		streams = new HashMap<>();
		applets = new Vector<>();
	}

	@Override
	public AudioClip getAudioClip(URL url) {
		return Applet.newAudioClip(url);
	}

	@Override
	public Image getImage(URL url) {
		Toolkit toolKit = Toolkit.getDefaultToolkit();
		return toolKit.getImage(url);
	}

	@Override
	public Applet getApplet(String name) {
		throw new RuntimeException();
	}

	@Override
	public Enumeration<Applet> getApplets() {
		return applets.elements();
	}

	@Override
	public void showDocument(URL url) {
		logger.info("Showing document: [url=" + url.toString() + "]");
	}

	@Override
	public void showDocument(URL url, String target) {
		try {
			showDocument(new URL(url + target));
		} catch (MalformedURLException cause) {
			logger.error("Malformed URL", cause);
		}
	}

	@Override
	public void showStatus(String status) {
		logger.info("Current status: [status=" + status + "]");
	}

	@Override
	public void setStream(String key, InputStream stream) {
		streams.put(key, stream);
	}

	@Override
	public InputStream getStream(String key) {
		return streams.get(key);
	}

	@Override
	public Iterator<String> getStreamKeys() {
		return streams.keySet().iterator();
	}

}