package com.koios.launch.common;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;

public final class DesktopUtil {

	private static final OperatingSystem SYSTEM = OperatingSystem.get();

	private static final String[] BROWSERS = new String[] { "xdg-open", "google-chrome", "chromium", "firefox",
			"opera", "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };

	private static final String[] FILE_MANAGERS = new String[] { "xdg-open", "nautilus", "dolphin", "thunar",
			"pcmanfm", "konqueror" };

	private static String findSupportedProgram(String kind, String[] names) throws InterruptedException, IOException {
		for (String name : names) {
			Process process = Runtime.getRuntime().exec(new String[] { "which", name });
			if (process.waitFor() == 0) {
				return name;
			}
		}

		throw new RuntimeException("Unable to find supported " + kind);
	}

	public static void openURL(URL url) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, URISyntaxException {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			desktop.browse(url.toURI());
		} else {
			switch (SYSTEM) {
				case MAC:
					Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", new Class[] { String.class }).invoke(null, new Object[] { url.toString() });
					break;
				case WINDOWS:
					Runtime.getRuntime().exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url.toString() });
					break;
				default:
					String browser = findSupportedProgram("browser", BROWSERS);
					Runtime.getRuntime().exec(new String[] { browser, url.toString() });
					break;
			}
		}
	}

	public static void openFile(File file) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InterruptedException, URISyntaxException {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
			desktop.open(file);
		} else {
			switch (SYSTEM) {
				case MAC:
				case WINDOWS:
					openURL(file.toURI().toURL());
					break;
				default:
					String fileManager = findSupportedProgram("file manager", FILE_MANAGERS);
					Runtime.getRuntime().exec(new String[] { fileManager, file.getAbsolutePath() });
					break;
			}
		}
	}

	private DesktopUtil() {

	}

}