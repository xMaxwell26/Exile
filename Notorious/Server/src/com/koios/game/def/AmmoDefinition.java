package com.koios.game.def;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koios.util.XStreamLibrary;

public class AmmoDefinition {

	public static AmmoDefinition getArrow(int id) {
		return projectiles.get(id);
	}

	@SuppressWarnings("unchecked")
	public static void loadProjectiles() throws FileNotFoundException {
		projectiles = new HashMap<>();
		List<AmmoDefinition> loaded = (ArrayList<AmmoDefinition>) XStreamLibrary.load("./data/definitions/ammo_definitions.xml");

		for (AmmoDefinition projectile : loaded) {
			for (int arrowId : projectile.getArrowId()) {
				projectiles.put(arrowId, projectile);
			}
		}
	}

	private int[] arrowId;
	private int startGfx;
	private int startGfx2;
	private int projectile;
	private int rangedStr;
	private int endGfx;

	private static Map<Integer, AmmoDefinition> projectiles;

	public int[] getArrowId() {
		return arrowId;
	}

	public int getEndGfxId() {
		return endGfx;
	}

	public int getProjectileId() {
		return projectile;
	}

	public int getRangedStr() {
		return rangedStr;
	}

	public int getStartGfx2Id() {
		return startGfx2;
	}

	public int getStartGfxId() {
		return startGfx;
	}

}
