package com.koios.game.model.player.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class StoreUtils {

	public static Path getDirectory(String path) throws IOException {
		String userHome = System.getProperty("user.home") + File.separator + "koios-store";
		Path directory = Paths.get(userHome, path);
		return Files.createDirectories(directory);
	}

	public static Path getCharacterFile(String username) throws IOException {
		Path directory = getDirectory("characters" + File.separator + String.valueOf(username.charAt(0)));
		return directory.resolve(username + ".txt");
	}

	private StoreUtils() {
		throw new InstantiationError();
	}

}
