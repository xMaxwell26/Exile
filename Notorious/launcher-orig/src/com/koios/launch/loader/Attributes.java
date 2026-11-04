package com.koios.launch.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class Attributes {

	private static final Properties properties = new Properties();

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static String getName() {
		return getProperty("name");
	}

	public static boolean showConsole() {
		return Boolean.parseBoolean(getProperty("show-console"));
	}

	public static boolean showTools() {
		return Boolean.parseBoolean(getProperty("show-tools"));
	}

	public static String getScreenshotFolder() {
		return getProperty("screenshot-folder");
	}

	public static String getRoot() {
		return getProperty("root");
	}

	public static String getArchive() {
		return getProperty("archive");
	}

	public static void load(File file) throws IOException {
		if (!file.exists()) {
			writeDefaultSettings();
		}
		properties.load(new FileReader(file));
	}

	private static void writeDefaultSettings() throws IOException {
		File file = new File("./", "settings.ini");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write("name = Koios");
			writer.newLine();
			writer.write("show-console = false");
			writer.newLine();
			writer.write("show-tools = false");
			writer.newLine();
			writer.write("screenshot-folder = captures");
			writer.newLine();
			writer.write("archive = Koios.jar");
			writer.newLine();
			writer.write("root = assets");
		}
	}

	private Attributes() {

	}

}