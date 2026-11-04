package com.koios.game.content.skill;

import java.util.Objects;

import com.google.common.primitives.Ints;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.impl.edgeville.MysteriousCapeDialogue;
import com.koios.game.model.Graphic;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendSkillLevelPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.StringUtil;

/**
 * Represents a player's skill and experience levels.
 *
 * @author Graham Edgecombe
 */
public class Skill {

	/**
	 * The number of skills.
	 */
	public static final int SKILL_COUNT = 24;

	/**
	 * The levels array.
	 */
	private final int[] levels = new int[SKILL_COUNT];

	/**
	 * The experience array.
	 */
	private final int[] experience = new int[SKILL_COUNT];

	/**
	 * The goals array.
	 */
	private final int[][] skillGoals = new int[SKILL_COUNT][3];

	/**
	 * The skill names.
	 */
	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing",
			"Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Construction", "Hunter", "Summoning",
			"Dungeoneering" };

	/**
	 * Constants for the skill numbers.
	 */
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10,
			FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20, CONSTRUCTION = 21,
			HUNTER = 22, SUMMONING = 23, DUNGEONEERING = 24;

	/**
	 * The largest allowed experience.
	 */
	// Representation is ten times the actual maximum
	private static final int MAX_EXPERIENCE = 2_000_000_000;
	private final Player player;

	/**
	 * Creates a skills object.
	 *
	 * @param player The player whose skills this object represents.
	 */
	public Skill(Player player) {
		this.player = Objects.requireNonNull(player);

		for (int i = 0; i < 7; i++) {
			levels[i] = 1;
		}

		levels[Skill.HITPOINTS] = 10;
		experience[Skill.HITPOINTS] = 11540;
	}

	public Skill(Mob mob, int attack, int defence, int hitpoints) {
		player = null;

		for (int i = 0; i < 7; i++) {
			if (i == 5) {
				levels[i] = 1;
				experience[i] = 0;
				skillGoals[i][0] = -1;
				continue;
			}

			levels[i] = i == 1 ? defence : i == 3 ? hitpoints : attack;
			experience[i] = getXPForLevel(i == 1 ? defence : i == 3 ? hitpoints : attack);
			skillGoals[i][0] = -1;
		}
	}

	/**
	 * Checks to see if the player has more than one 99.
	 *
	 * @return True if the player has more than one 99, false otherwise.
	 */
	public boolean hasMoreThanOne99() {
		int count = 0;

		for (int i = 0; i < SKILL_COUNT; i++) {
			if (getLevelForExperience(i) >= 99) {
				if (++count == 2) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasA99() {
		for (int i = 0; i < SKILL_COUNT; i++) {
			if (getLevelForExperience(i) >= 99) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds experience.
	 *
	 * @param skill The skill id.
	 * @param exp The experience to add.
	 */
	public int addExperience(final int skill, double exp) {
        if(com.koios.game.arpg.CharacterProgress.combatSkill(skill))return 0;
		if (player.enabledXPLock || exp < 0) {
			// You cannot add negative experience.
			return 0;
		}

		int iexp = (int) (exp * 10.0);

		if (experience[skill] == MAX_EXPERIENCE) {
			// Short-circuit: already have maximum experience.
			return 0;
		}

		int newExperience = experience[skill] + iexp;

		if (iexp < 0 || newExperience < 0 || newExperience > MAX_EXPERIENCE) {
			iexp = MAX_EXPERIENCE - experience[skill];
		}

		int oldLevel = getLevelForExperience(skill);
		experience[skill] += iexp;

		if (experience[skill] > MAX_EXPERIENCE) {
			experience[skill] = MAX_EXPERIENCE;
		}

		int newLevel = Math.min(99, getLevelForExperience(skill));
		int levelDifference = newLevel - oldLevel;

		if (levelDifference > 0) {
			levels[skill] += levelDifference;
			player.playGraphics(new Graphic(1634));
			levelIncreased(skill, newLevel, levelDifference);
		}

		if (experience[skill] == MAX_EXPERIENCE) {
			GameService.getWorld().broadcast(String.format("<col=800000>News: %s just reached 200,000,000 XP in the %s skill.", player.getName(), Skill.SKILL_NAME[skill]),
					Player::isNewsfeedEnabled);

			boolean completedAll = true;

			// Iterate over the players XP table to test if they have achieved 200,000,000 XP in all
			// stats.
			for (int i = 0; i < 23; i++) {
				if (experience[i] != MAX_EXPERIENCE) {
					if (i == Skill.CONSTRUCTION) {
						continue;
					}

					completedAll = false;
					break;
				}
			}

			if (completedAll) {
				// A small greeting to those who manage to obtain 200,000,000 XP in all skills.
				player.sendMessage("<col=800000>You have reached the highest possible XP total!");
			}
		}

		int realExperienceAdded = iexp / 10;
		player.getSession().send(new SendSkillLevelPacket(skill));
		// player.getSession().send(new SendCounterExpPacket(skill, realExperienceAdded));
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		AchievementManager.performed(player, Achievement.GAIN_100_MILLION, realExperienceAdded);
		return realExperienceAdded;
	}

	/**
	 * Decrements a level.
	 *
	 * @param skill The skill to decrement.
	 */
	public void decrementLevel(int skill) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(player.getHitpoints()-1);return;}
		if (levels[skill] - 1 >= 0) {
			levels[skill] = levels[skill] - 1;
		}

		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	/**
	 * Decrements a level.
	 *
	 * @param skill The skill to decrement.
	 */
	public void decrementLevel(int skill, int amount) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(player.getHitpoints()-Math.max(0,amount));return;}
		if (amount < 0) {
			amount = 0;
		}

		if (levels[skill] - amount < 0) {
			amount = levels[skill];
		}

		levels[skill] = levels[skill] - amount;
		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	/**
	 * Detracts a given level a given amount.
	 *
	 * @param skill The level to detract.
	 * @param amount The amount to detract from the level.
	 */
	public void detractLevel(int skill, int amount) {
		if (levels[skill] == 0 || amount < 0) {
			amount = 0;
		}

		if (amount > levels[skill]) {
			amount = levels[skill];
		}

		levels[skill] = levels[skill] - amount;
		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	/**
	 * Gets the combat level.
	 *
	 * @return The combat level.
	 */
	public int getCombatLevel(boolean summon) {
        if(player!=null)return player.getArpgProgress().level();
		int attack = getLevelForExperience(0);
		int defence = getLevelForExperience(1);
		int strength = getLevelForExperience(2);
		int hp = getLevelForExperience(3);
		int prayer = getLevelForExperience(5);
		int ranged = getLevelForExperience(4);
		int magic = getLevelForExperience(6);

		double prayerBonus = Math.floor(prayer * 0.5);
		double summoningBonus = getSummoningCombat();

		double base = 0.25 * (1.3 * Ints.max(attack + strength, magic * 2, ranged * 2) + defence + hp + prayerBonus + summoningBonus);
		int combatLevel = (int) (summon ? base : base - summoningBonus);

		return combatLevel;
	}

	public double getSummoningCombat() {
		int summoning = getLevelForExperience(23);
		return Math.floor(summoning * 0.5);
	}

	/**
	 * Gets experience.
	 *
	 * @param skill The skill id.
	 * @return The experience.
	 */
	public int getExperience(int skill) {
		return experience[skill] / 10;
	}

	/**
	 * Gets the goal data for a specific skill.
	 *
	 * @param skill The skill id.
	 * @return The goal data for the skill.
	 */
	public int[] getGoalData(int skill) {
		return skillGoals[skill];
	}

	/**
	 * Gets a level.
	 *
	 * @param skill The skill id.
	 * @return The level.
	 */
	public int getLevel(int skill) {
        if(player!=null&&skill==HITPOINTS)return player.getHitpoints();
		return levels[skill];
	}

	/**
	 * Gets a level by experience.
	 *
	 * @param skill The skill id.
	 * @return The level.
	 */
	public int getLevelForExperience(int skill) {
		double exp = getExperience(skill);
		int points = 0;
		int output;

		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4) - 1;

			if (output >= exp) {
				return lvl;
			}
		}

		return 99;
	}

	/**
	 * Gets the total level.
	 *
	 * @return The total level.
	 */
	public int getTotalLevel() {
		int total = 0;

		for (int i = 0; i < levels.length; i++) {
			total += getLevelForExperience(i);
		}

		return total;
	}

	/**
	 * Gets the total experience of all skills
	 * 
	 * @return The total experience of all skills
	 */
	public int getTotalExperience() {
		int total = 0;

		for (int i = 0; i < experience.length; i++) {
			total += experience[i];
		}
		return total;
	}

	/**
	 * Gets a experience from the level.
	 *
	 * @param level
	 *            The level.
	 * @return The experience.
	 */
	public int getXPForLevel(int level) {
		if (level >= 99) {
			return 13_034_431;
		}

		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));

			if (lvl >= level) {
				return output;
			}

			output = (int) Math.floor(points / 4);
		}

		return 0;
	}

	/**
	 * Increments a level.
	 *
	 * @param skill The skill to increment.
	 */
	public void incrementLevel(int skill) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(player.getHitpoints()+1);return;}
		levels[skill]++;
		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	/**
	 * Increments a level.
	 *
	 * @param skill The skill to increment.
	 */
	public void incrementLevel(int skill, int amount) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(player.getHitpoints()+amount);return;}
		levels[skill] += amount;
		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	private void levelIncreased(int skill, int level, int difference) {
		if (level == 99) {
			GameService.getWorld().broadcast("@red@News: " + player.getName() + " has just achieved level 99 " + Skill.SKILL_NAME[skill] + ".", Player::isNewsfeedEnabled);

			if (MysteriousCapeDialogue.canWearCompletionistCape(player, false)) {
				GameService.getWorld().broadcast("@red@News: " + player.getName() + " has just unlocked the completionist cape!", Player::isNewsfeedEnabled);
			} else if (MysteriousCapeDialogue.canWearMaxCape(player, false)) {
				GameService.getWorld().broadcast("@red@News: " + player.getName() + " has just unlocked the max cape!", Player::isNewsfeedEnabled);
			}
		}

		if (difference > 1) {
			player.sendMessage(String.format("You've just advanced %d %s levels! You have reached level %d.", difference, Skill.SKILL_NAME[skill], level));
		} else {
			String article = StringUtil.getArticle(SKILL_NAME[skill]);
			player.sendMessage(String.format("You've just advanced %s %s level! You have reached level %d.", article, Skill.SKILL_NAME[skill], level));
		}

		switch (skill) {
		case Skill.ATTACK:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced an Attack level!", 4268));
			player.getSession().send(new SendStringPacket("Your Attack level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6247));
			break;

		case Skill.DEFENCE:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Defence level!", 4268));
			player.getSession().send(new SendStringPacket("Your Defence level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6253));
			break;

		case Skill.STRENGTH:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Strength level!", 4268));
			player.getSession().send(new SendStringPacket("Your Strength level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6206));
			break;

		case Skill.HITPOINTS:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Hitpoints level!", 4268));
			player.getSession().send(new SendStringPacket("Your Hitpoints level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6216));
			break;

		case Skill.RANGED:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Ranged level!", 4268));
			player.getSession().send(new SendStringPacket("Your Ranged level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4443));
			break;

		case Skill.PRAYER:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Prayer level!", 4268));
			player.getSession().send(new SendStringPacket("Your Prayer level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6242));
			break;

		case Skill.MAGIC:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Magic level!", 4268));
			player.getSession().send(new SendStringPacket("Your Magic level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6211));
			break;

		case Skill.COOKING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Cooking level!", 4268));
			player.getSession().send(new SendStringPacket("Your Cooking level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6226));
			break;

		case Skill.WOODCUTTING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Woodcutting level!", 4268));
			player.getSession().send(new SendStringPacket("Your Woodcutting level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4272));
			break;

		case Skill.FLETCHING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Fletching level!", 4268));
			player.getSession().send(new SendStringPacket("Your Fletching level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6231));
			break;

		case Skill.FISHING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Fishing level!", 4268));
			player.getSession().send(new SendStringPacket("Your Fishing level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6258));
			break;

		case Skill.FIREMAKING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Firemaking level!", 4268));
			player.getSession().send(new SendStringPacket("Your Firemaking level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4282));
			break;

		case Skill.CRAFTING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Crafting level!", 4268));
			player.getSession().send(new SendStringPacket("Your Crafting level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6263));
			break;

		case Skill.SMITHING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Smithing level!", 4268));
			player.getSession().send(new SendStringPacket("Your Smithing level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6221));
			break;

		case Skill.MINING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Mining level!", 4268));
			player.getSession().send(new SendStringPacket("Your Mining level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4416));
			break;

		case Skill.HERBLORE:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Herblore level!", 4268));
			player.getSession().send(new SendStringPacket("Your Herblore level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(6237));
			break;

		case Skill.AGILITY:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced an Agility level!", 4268));
			player.getSession().send(new SendStringPacket("Your Agility level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4277));
			break;

		case Skill.THIEVING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Thieving level!", 4268));
			player.getSession().send(new SendStringPacket("Your Theiving level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4261));
			break;

		case Skill.SLAYER:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Slayer level!", 4268));
			player.getSession().send(new SendStringPacket("Your Slayer level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(12122));
			break;

		case Skill.RUNECRAFTING:
			player.getSession().send(new SendStringPacket("Congratulations, you just advanced a Runecrafting level!", 4268));
			player.getSession().send(new SendStringPacket("Your Runecrafting level is now " + level + ".", 4269));
			player.getSession().send(new SendChatInterfacePacket(4267));
		}

		player.dialogueAction = 0;
		player.nextChat = 0;
		player.dialogue().interrupt();
	}

	/**
	 * Normalizes a level (adjusts it until it is at its normal value).
	 *
	 * @param skill The skill to normalize.
	 */
	public void normalizeLevel(int skill) {
        if(skill==HITPOINTS)return;
		int norm = getLevelForExperience(skill);

		if (levels[skill] > norm && skill != 3) {
			levels[skill]--;
			player.getSession().send(new SendSkillLevelPacket(skill));
		} else if (levels[skill] < norm) {
			levels[skill]++;
			player.getSession().send(new SendSkillLevelPacket(skill));
		}
	}

	public void refresh() {
		for (int i = 0; i < levels.length; i++) {
			player.getSession().send(new SendSkillLevelPacket(i));
		}

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void restoreAll() {
        if(player!=null){player.getArpgVitals().refill(player.getArpgStats());player.getArpgSkills().refill(player.getArpgStats().get(com.koios.game.arpg.Stat.MANA));}
		for (int i = 0; i < levels.length; i++) {
			levels[i] = getLevelForExperience(i);
		}

		refresh();
	}

	/**
	 * Restores the skill to the xp level.
	 */
	public void restoreLevel(int skill) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(player.getMaxHitpoints());return;}
		int newLevel;

		if (skill == Skill.HITPOINTS) {
			newLevel = player.getMaxHitpoints();
		} else {
			newLevel = getLevelForExperience(skill);
		}

		levels[skill] = newLevel;
		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	/**
	 * Restores the skill to a level.
	 */
	public void restoreLevel(int skill, int level) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(level);return;}
		if (level > getLevelForExperience(skill)) {
			level = getLevelForExperience(skill);
		}

		levels[skill] = level;
		player.getSession().send(new SendSkillLevelPacket(skill));
	}

	/**
	 * Sets experience.
	 *
	 * @param skill The skill id.
	 * @param exp The experience.
	 */
	public void setExperience(int skill, int exp) {
		int oldLvl = getLevelForExperience(skill);
		experience[skill] = exp;
		player.getSession().send(new SendSkillLevelPacket(skill));
		int newLvl = getLevelForExperience(skill);

		if (oldLvl != newLvl) {
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	/**
	 * Sets the goal data for a skill.
	 *
	 * @param skill The skill id.
	 * @param goal The goal.
	 * @param goalType The goal type (0 is level, 1 is experience)
	 * @param goalInit The initial experience.
	 */
	public void setGoalData(int skill, int goal, int goalType, int goalInit) {
		skillGoals[skill][0] = goal;
		skillGoals[skill][1] = goalType;
		skillGoals[skill][2] = goalInit;
	}

	/**
	 * Sets a level.
	 *
	 * @param skill The skill id.
	 * @param level The level.
	 */
	public void setLevel(int skill, int level) {
        if(player!=null&&skill==HITPOINTS){player.setHitpoints(level);return;}
		levels[skill] = level;

		if (player != null) {
			player.getSession().send(new SendSkillLevelPacket(skill));
		}
	}

	/**
	 * Sets a skill.
	 *
	 * @param skill The skill id.
	 * @param level The level.
	 * @param exp The experience.
	 */
	public void setSkill(int skill, int level, int exp) {
		levels[skill] = level;
		experience[skill] = exp * 10;

		if (player != null) {
			player.getSession().send(new SendSkillLevelPacket(skill));
		}
	}

}
