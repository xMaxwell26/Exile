package com.koios.game.content.skill.summoning.actions;

import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Recharges the player's summoning points.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ObeliskRecharge {

	public static void execute(Player player) {
		if (player.getSkill().getLevel(Skill.SUMMONING) >= player.getSkill().getLevelForExperience(Skill.SUMMONING)) {
			player.sendMessage("You already have full summoning points.");
			return;
		}
		player.playGraphics(new Graphic(1517));
		player.playAnimation(new Animation(8502));
		player.getSkill().setLevel(Skill.SUMMONING, player.getSkill().getLevelForExperience(Skill.SUMMONING));
		player.getSession().send(new SendStringPacket(player.getSkill().getLevel(Skill.SUMMONING) + "/" + player.getSkill().getLevelForExperience(Skill.SUMMONING), 18045));
		player.sendMessage("You recharge your summoning points.");
	}

}
