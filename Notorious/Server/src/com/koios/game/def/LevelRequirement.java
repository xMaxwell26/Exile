package com.koios.game.def;

import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class LevelRequirement {

	private static LevelRequirement[] definitions;

	private static final String DIR = "./data/definitions/level_requirements.json";

	public static LevelRequirement forId(int id) {
		for (LevelRequirement reqs : definitions) {
			for (int item : reqs.getId()) {
				if (id == item) {
					return reqs;
				}
			}
		}

		if (id < 0 || id > definitions.length || definitions[id] == null) {
			return null;
		}

		return null;
	}

	public static LevelRequirement[] getDefinitions() {
		return definitions;
	}

	public static void init() {
		Gson gson = new Gson();

		try (FileReader reader = new FileReader(DIR)) {
			definitions = gson.fromJson(reader, LevelRequirement[].class);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("Finished reading " + definitions.length + " level requirements.");
	}

	private int[] id;
	private int[] levelId;
	private int[] requiredLevel;

	public int[] getId() {
		return id;
	}

	public int[] getLevelId() {
		return levelId;
	}

	public int[] getRequiredLevel() {
		return requiredLevel;
	}

}
