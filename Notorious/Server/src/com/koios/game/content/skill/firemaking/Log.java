package com.koios.game.content.skill.firemaking;

import java.util.HashMap;
import java.util.Map;

public enum Log {

	LOG(1, 40, 1511), ACHEY(1, 40, 2862), OAK(15, 60, 1521), WILLOW(30, 90, 1519), TEAK(35, 105, 6333), ARCTIC_PINE(42, 125, 10810), MAPLE(45, 135, 1517), MAHOGANY(50, 157.5,
			6332), EUCALYPTUS(58, 193.5, 12581), YEW(60, 202.5, 1515), MAGIC(75, 303.8, 1513);

	public static Log forLog(int logId) {
		return values.get(logId);
	}

	private final int levelRequired, logId;
	private final double experienceGained;

	private static final Map<Integer, Log> values = new HashMap<>();

	static {
		for (Log logs : values()) {
			values.put(logs.getLogId(), logs);
		}
	}

	private Log(int levelRequired, double experienceGained, int logId) {
		this.levelRequired = levelRequired;
		this.experienceGained = experienceGained;
		this.logId = logId;
	}

	public int getLevel() {
		return levelRequired;
	}

	public int getLogId() {
		return logId;
	}

	public double getExp() {
		return experienceGained;
	}

}