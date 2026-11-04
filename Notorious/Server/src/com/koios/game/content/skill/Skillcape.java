package com.koios.game.content.skill;

import com.koios.game.model.item.Item;

public enum Skillcape {

	DEFENCE(9753), ATTACK(9747), THIEVING(9777), MAGIC(9762), STRENGTH(9750), HITPOINTS(9768), RANGED(9756), FISHING(9798), WOODCUTTING(9807), AGILITY(9771), COOKING(
			9801), CRAFTING(9780), FARMING(9810), FLETCHING(9783), HUNTER(9948), RUNECRAFTING(9765), SLAYER(9786), SMITHING(9795), SUMMONING(12169), PRAYER(9759), MINING(
					9792), HERBLORE(9774), CONSTRUCTION(9789), FIREMAKING(9804);

	private final int untrimmedCapeId;

	private Skillcape(int untrimmedCapeId) {
		this.untrimmedCapeId = untrimmedCapeId;
	}

	public static Skillcape get(int id) {
		for (Skillcape cape : values()) {
			if (cape.getTrimmedCape().getId() == id || cape.getUntrimmedCape().getId() == id) {
				return cape;
			}
		}
		return null;
	}

	public int getSkillId() {
		for (int i = 0; i < Skill.SKILL_NAME.length; i++) {
			if (name().toLowerCase().equals(Skill.SKILL_NAME[i].toLowerCase())) {
				return i;
			}
		}
		return -1;
	}

	public Item getUntrimmedCape() {
		return new Item(untrimmedCapeId);
	}

	public Item getTrimmedCape() {
		return new Item(untrimmedCapeId + 1);
	}

	public Item getHood() {
		return new Item(untrimmedCapeId + 2);
	}

}