package com.koios.game.map;

import java.io.BufferedReader;
import java.io.FileReader;

public class ObjectAlternates {

	private static int[][] alternates = new int[53000][1];

	public static final void loadAlternateIds() {
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/definitions/ObjectAlternates.txt"))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				int id = Integer.parseInt(line.substring(0, line.indexOf(":")));
				line = line.substring(line.indexOf(":") + 1);
				int alt = Integer.parseInt(line.substring(0, line.length()));
				alternates[id][0] = alt;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("Finished loading " + alternates.length + " alternate object definitions.");
	}

	public static int getAlternate(int id) {
		return alternates[id][0];
	}

	public static final void clearAlternates() {
		alternates = null;
	}

}
