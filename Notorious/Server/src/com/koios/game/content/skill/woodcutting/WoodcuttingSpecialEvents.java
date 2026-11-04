package com.koios.game.content.skill.woodcutting;

import com.koios.Constants;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.firemaking.Log;
import com.koios.game.content.skill.woodcutting.WoodcuttingData.TreeData;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handle WoodCutting Special Events and Items.
 *
 * @author Gabbe
 */

public class WoodcuttingSpecialEvents {

	public static void handleAdzeAffect(Player player, int treeId) {
		TreeData t = TreeData.forTree(treeId);

		if (t == null) {
			return;
		}

		Log definition = Log.forLog(t.getLogId());

		if (player.getEquipment().contains(13661) || player.getInventory().contains(13661)) {
			int chance = NumberUtil.random(10);

			if (chance < 3) {
				player.getInventory().remove(new Item(t.getLogId(), 1));
				player.getSkill().addExperience(Skill.FIREMAKING, definition.getExp() * Constants.WOODCUTTING_EXPERIENCE);
				player.sendMessage("The heat of inferno adze incinerates them.");
			}
		}
	}

	private static final double[][] bonusItems = { { 10933, 1.002 }, { 10939, 1.008 }, { 10940, 1.006 }, { 10941, 1.004 } };

	public static double equipmentExperienceBonus(Player p) {
		double bonus = 1;

		for (double[] bonusItem : bonusItems) {
			if (p.getEquipment().contains((int) bonusItem[0])) {
				bonus *= bonusItem[1];
			}
		}

		return bonus;
	}

}
