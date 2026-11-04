package com.koios.network.mysql;

import org.sql2o.Connection;
import org.sql2o.Query;

import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;

/**
 * Inserts the players skills into the hiscores
 * 
 * @author Arithium
 *
 */
public class HiscoresDatabaseQuery implements SQLJob {

	/**
	 * The player to insert into the hiscores
	 */
	private final Player player;

	/**
	 * Constructs a new {@link HiscoresDatabaseQuery}
	 * 
	 * @param player
	 *            {@link Player} to insert into the hiscores
	 */
	public HiscoresDatabaseQuery(Player player) {
		this.player = player;
	}

	@Override
	public void executeQuery() {
		
		final String username = player.getName().toLowerCase();
		try (Connection connection = GameService.getConnection()) {
			
			/*
			 * Here we delete the player from the database
			 */
			Query delete = connection.createQuery("DELETE FROM hs_skill WHERE username = :username;");
			delete.addParameter("username", username);
			delete.executeUpdate();
			
			if (player.getPrivilege().superiorTo(RegularPrivilege.ADMINISTRATOR)) {
				return;
			}
			
			/*
			 * Next we insert them back into the database
			 */
			Query insert = connection.createQuery(
					"INSERT INTO `hs_users` (`username`, `game_mode`, `rights`, `overall_level`, `overall_xp`, `attack_xp`, `defence_xp`, `strength_xp`, `hitpoints_xp`, `ranged_xp`, `prayer_xp`, `magic_xp`, `cooking_xp`, `woodcutting_xp`, `fletching_xp`, `fishing_xp`, `firemaking_xp`, `crafting_xp`, `smithing_xp`, `mining_xp`, `herblore_xp`, `agility_xp`, `thieving_xp`, `slayer_xp`, `farming_xp`, `runecrafting_xp`, `hunter_xp`, `attack_level`, `defence_level`, `strength_level`, `hitpoints_level`, `ranged_level`, `prayer_level`, `magic_level`, `cooking_level`, `woodcutting_level`, `fletching_level`, `fishing_level`, `firemaking_level`, `crafting_level`, `smithing_level`, `mining_level`, `herblore_level`, `agility_level`, `thieving_level`, `slayer_level`, `farming_level`, `runecrafting_level`, `hunter_level`) "
								+ "VALUES (:username, :iron, :rights, :overall_level, :overall_xp, :attack_xp, :defence_xp, :strength_xp, :hitpoints_xp, :ranged_xp, :prayer_xp, :magic_xp, :cooking_xp, :woodcutting_xp, :fletching_xp, :fishing_xp, :firemaking_xp, :crafting_xp, :smithing_xp, :mining_xp, :herblore_xp, :agility_xp, :thieving_xp, :slayer_xp, :farming_xp, :runecrafting_xp, :hunter_xp, :attack_level, :defence_level, :strength_level, :hitpoints_level, :ranged_level, :prayer_level, :magic_level, :cooking_level, :woodcutting_level, :fletching_level, :fishing_level, :firemaking_level, :crafting_level, :smithing_level, :mining_level, :herblore_level, :agility_level, :thieving_level, :slayer_level, :farming_level, :runecrafting_level, :hunter_level);");
			
			
			insert.addParameter("username", player.getName());
			insert.addParameter("game_mode", false);
			insert.addParameter("rights", player.getPrivilege().getCrownId());
			insert.addParameter("overall_level", player.getSkill().getTotalLevel());
			insert.addParameter("overall_xp", player.getSkill().getTotalExperience());
			insert.addParameter("attack_xp", (int) player.getSkill().getExperience(Skill.ATTACK));
			insert.addParameter("attack_level", player.getSkill().getLevelForExperience(Skill.ATTACK));
			insert.addParameter("defence_xp", (int) player.getSkill().getExperience(Skill.DEFENCE));
			insert.addParameter("defence_level", player.getSkill().getLevelForExperience(Skill.DEFENCE));
			insert.addParameter("strength_xp", (int) player.getSkill().getExperience(Skill.STRENGTH));
			insert.addParameter("strength_level", player.getSkill().getLevelForExperience(Skill.STRENGTH));
			insert.addParameter("hitpoints_xp", (int) player.getSkill().getExperience(Skill.HITPOINTS));
			insert.addParameter("hitpoints_level", player.getSkill().getLevelForExperience(Skill.HITPOINTS));
			insert.addParameter("ranged_xp", (int) player.getSkill().getExperience(Skill.RANGED));
			insert.addParameter("ranged_level", player.getSkill().getLevelForExperience(Skill.RANGED));
			insert.addParameter("prayer_xp", (int) player.getSkill().getExperience(Skill.PRAYER));
			insert.addParameter("prayer_level", player.getSkill().getLevelForExperience(Skill.PRAYER));
			insert.addParameter("magic_xp", (int) player.getSkill().getExperience(Skill.MAGIC));
			insert.addParameter("magic_level", player.getSkill().getLevelForExperience(Skill.MAGIC));
			insert.addParameter("cooking_xp", (int) player.getSkill().getExperience(Skill.COOKING));
			insert.addParameter("cooking_level", player.getSkill().getLevelForExperience(Skill.COOKING));
			insert.addParameter("woodcutting_xp", (int) player.getSkill().getExperience(Skill.WOODCUTTING));
			insert.addParameter("woodcutting_level", player.getSkill().getLevelForExperience(Skill.WOODCUTTING));
			insert.addParameter("fletching_xp", (int) player.getSkill().getExperience(Skill.FLETCHING));
			insert.addParameter("fletching_level", player.getSkill().getLevelForExperience(Skill.FLETCHING));
			insert.addParameter("fishing_xp", (int) player.getSkill().getExperience(Skill.FISHING));
			insert.addParameter("fishing_level", player.getSkill().getLevelForExperience(Skill.FISHING));
			insert.addParameter("firemaking_xp", (int) player.getSkill().getExperience(Skill.FIREMAKING));
			insert.addParameter("firemaking_level", player.getSkill().getLevelForExperience(Skill.FIREMAKING));
			insert.addParameter("crafting_xp", (int) player.getSkill().getExperience(Skill.CRAFTING));
			insert.addParameter("crafting_level", player.getSkill().getLevelForExperience(Skill.CRAFTING));
			insert.addParameter("smithing_xp", (int) player.getSkill().getExperience(Skill.SMITHING));
			insert.addParameter("smithing_level", player.getSkill().getLevelForExperience(Skill.SMITHING));
			insert.addParameter("mining_xp", (int) player.getSkill().getExperience(Skill.MINING));
			insert.addParameter("mining_level", player.getSkill().getLevelForExperience(Skill.MINING));
			insert.addParameter("herblore_xp", (int) player.getSkill().getExperience(Skill.HERBLORE));
			insert.addParameter("herblore_level", player.getSkill().getLevelForExperience(Skill.HERBLORE));
			insert.addParameter("agility_xp", (int) player.getSkill().getExperience(Skill.AGILITY));
			insert.addParameter("agility_level", player.getSkill().getLevelForExperience(Skill.AGILITY));
			insert.addParameter("thieving_xp", (int) player.getSkill().getExperience(Skill.THIEVING));
			insert.addParameter("thieving_level", player.getSkill().getLevelForExperience(Skill.THIEVING));
			insert.addParameter("slayer_xp", (int) player.getSkill().getExperience(Skill.SLAYER));
			insert.addParameter("slayer_level", player.getSkill().getLevelForExperience(Skill.SLAYER));
			insert.addParameter("farming_xp", (int) player.getSkill().getExperience(Skill.FARMING));
			insert.addParameter("farming_level", player.getSkill().getLevelForExperience(Skill.FARMING));
			insert.addParameter("runecrafting_xp", (int) player.getSkill().getExperience(Skill.RUNECRAFTING));
			insert.addParameter("runecrafting_level", player.getSkill().getLevelForExperience(Skill.RUNECRAFTING));
			insert.addParameter("hunter_xp", (int) player.getSkill().getExperience(Skill.HUNTER));
			insert.addParameter("hunter_level", player.getSkill().getLevelForExperience(Skill.HUNTER));
			
			insert.executeUpdate();
		} catch (Exception e) {
			System.out.println("Failed to update database for user: " + player.getName());
			e.printStackTrace();
		}
	}

}
