package com.koios.game.content.skill.slayer.assignments;

import com.koios.game.def.MobDefinition;

/**
 * @author Tringan
 */
public class Assignment {

	public static Assignment of(int mobId) {
		return new Assignment(mobId);
	}

	private final int mobId;
	private final int levelRequired;
	private final String mobName;
	private final String slayerKey;

	private Assignment(int mobId) {
		this.mobId = mobId;
		MobDefinition def = MobDefinition.get(mobId);
		mobName = def.getName();
		slayerKey = def.getSlayerKey();
		levelRequired = def.getSlayerRequirement();
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public int getMobId() {
		return mobId;
	}

	public String getMobName() {
		return mobName;
	}

	public String getSlayerKey() {
		return slayerKey;
	}

}