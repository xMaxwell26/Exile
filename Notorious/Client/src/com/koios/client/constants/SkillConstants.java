package com.koios.client.constants;


public final class SkillConstants
{

	public static final int SKILL_COUNT = 24;

	public static final String[] SKILL_NAMES_ORDERED = { "Attack", "Defence", "Strength", "Hitpoints", "Range",
			"Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing",
			"Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Construction", "Hunter",
			"Summoning" };

	public static final int[] MORE_DETAILS_PANEL_ID = { 60024, 60066, 60045, 60031, 60088, 60109, 60130,
			60102, 60144, 60137, 60081, 60123, 60116, 60059, 60038, 60073, 60052, 60095, 60158, 60165, 60151, 60216,
			60226, 60236 };

	public static final String[] SKILL_NAMES = { "Attack", "Constitution", "Mining", "Strength", "Agility", "Smithing",
			"Defence", "Herblore", "Fishing", "Range", "Thieving", "Cooking", "Prayer", "Crafting", "Firemaking",
			"Magic", "Fletching", "Woodcutting", "Runecrafting", "Slayer", "Farming", "Construction", "Hunter",
			"Summoning" };

	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
			MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20,
			CONSTRUCTION = 21, HUNTER = 22, SUMMONING = 23;

	public static final boolean[] SKILL_ENABLED;

	static
	{
		SKILL_ENABLED = new boolean[SKILL_COUNT];
		SKILL_ENABLED[ATTACK] = true;
		SKILL_ENABLED[DEFENCE] = true;
		SKILL_ENABLED[STRENGTH] = true;
		SKILL_ENABLED[HITPOINTS] = true;
		SKILL_ENABLED[RANGED] = true;
		SKILL_ENABLED[PRAYER] = true;
		SKILL_ENABLED[MAGIC] = true;
		SKILL_ENABLED[COOKING] = true;
		SKILL_ENABLED[WOODCUTTING] = true;
		SKILL_ENABLED[FLETCHING] = true;
		SKILL_ENABLED[FISHING] = true;
		SKILL_ENABLED[FIREMAKING] = true;
		SKILL_ENABLED[CRAFTING] = true;
		SKILL_ENABLED[SMITHING] = true;
		SKILL_ENABLED[MINING] = true;
		SKILL_ENABLED[HERBLORE] = true;
		SKILL_ENABLED[AGILITY] = true;
		SKILL_ENABLED[THIEVING] = true;
		SKILL_ENABLED[SLAYER] = true;
		SKILL_ENABLED[FARMING] = true;
		SKILL_ENABLED[RUNECRAFTING] = true;
		SKILL_ENABLED[CONSTRUCTION] = false;
		SKILL_ENABLED[HUNTER] = true;
		SKILL_ENABLED[SUMMONING] = true;
	}

}
