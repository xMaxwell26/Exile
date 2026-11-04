package com.koios.launch;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.koios.launch.loader.Loader;

public final class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		try {
			Loader.start();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | InterruptedException | ExecutionException cause) {
			logger.log(Level.SEVERE, "Exception occured while starting up", cause);
		}
	}

}