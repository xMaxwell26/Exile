package com.koios.game.content.skill;

import com.koios.game.def.FletchingData.LogData;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendItemOnInterfacePacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Handle the skill actions.
 *
 * @author Gabriel
 */
public class SkillManager {

	/**
	 * Checks if the player can start the skill.
	 */
	public static boolean canSkill(Player player, SkillConstructor skill) {
		if (skill == null) {
			return false;
		}

		player.getSession().send(new SendRemoveInterfacePacket());

		if (player.isDead() || player.getHitpoints() <= 0) {
			return false;
		}

		if (player.isSkilling(skill.getActionDelay())) {
			return false;
		}

		if (player.getLastSkillAction() < skill.getActionDelay()) {
			player.sendMessage("You must wait a few seconds before trying this again.");
			return false;
		}

		if (player.getCurrentSkill() != null
				&& (!player.getCurrentSkill().getName().equals(skill.getName()) || !player.getCurrentSkill().mustAbort() && player.getCurrentSkill().hasStarted())) {
			player.getCurrentSkill().abort();
			return false;
		}

		player.setCurrentSkill(skill);
		player.setLastSkillAction(System.currentTimeMillis());
		return true;
	}

	/**
	 * Checks if the player can continue the skill.
	 *
	 * @param player
	 * @return
	 */
	public static boolean canContinue(Player player) {

		if (player.getCombat().getTarget() != null) {
			return false;
		}

		if (player.getCombat().getAggressor() != null) {
			return false;
		}

		if (player.getFollowing().isFollowing()) {
			return false;
		}

		if (player.getCurrentSkill() == null) {
			return false;
		}

		return true;

	}

	public static void sendFletchingInterface(Player player, LogData log) {
		player.getSession().send(new SendChatInterfacePacket(log.getInterface()));

		for (int i = 0; i < log.getItemInterface().length; i++) {
			if (log.getLevelReq()[i] > 0) {
				player.getSession().send(new SendItemOnInterfacePacket(log.getItemInterface()[i], 190, log.getItem()[i]));
			}

			player.getSession().send(new SendStringPacket("\\n\\n\\n\\n\\n" + (new Item(log.getItem()[i]).getDefinition().getName().toLowerCase().contains("stock")
					? "Crossbow stock" : new Item(log.getItem()[i]).getDefinition().getName()) + "", log.getTextInterface()[i]));
		}
	}

	public static void sendInterface(Player player, int itemId) {
		player.getSession().send(new SendChatInterfacePacket(4429));
		player.getSession().send(new SendItemOnInterfacePacket(1746, 190, itemId));
		player.getSession().send(new SendStringPacket("\\n\\n\\n\\n\\n" + new Item(itemId).getDefinition().getName(), 2799));
	}

}