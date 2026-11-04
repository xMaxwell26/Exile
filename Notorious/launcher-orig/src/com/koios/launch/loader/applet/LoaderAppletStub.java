package com.koios.launch.loader.applet;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

public final class LoaderAppletStub implements AppletStub {

	private boolean active;

	private final AppletContext appletContext;

	public LoaderAppletStub(AppletContext appletContext) {
		this.appletContext = appletContext;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public URL getDocumentBase() {
		throw new UnsupportedOperationException();
	}

	@Override
	public URL getCodeBase() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParameter(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AppletContext getAppletContext() {
		return appletContext;
	}

	@Override
	public void appletResize(int width, int height) {
		throw new UnsupportedOperationException();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}