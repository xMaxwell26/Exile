package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendSidebarInterfacePacket;

public class ChaosAltarDialogue extends Dialogue {

	public ChaosAltarDialogue(Player player) {
		super(player);
	}

	@Override
	protected void select(int index) {
		if (index == 1) {
			player.playAnimation(new Animation(645));

			if (player.altarPrayed == 0) {
				player.altarPrayed = 1;
				player.getSession().send(new SendSidebarInterfacePacket(5, 21356));
				sendStatement("The altar fills you head with dark thoughts, purging the", "prayers from your memory and leaving only curses in their place.");
			} else {
				player.altarPrayed = 0;
				player.getSession().send(new SendSidebarInterfacePacket(5, 5608));
				sendStatement("The curses slip from your memory", "and you recall the prayers you used to know.");
			}

			player.getPrayers().clear();
		} else {
			stop();
		}
	}

	@Override
	protected void start() {
		if (player.altarPrayed == 1) {
			choice("Select an Option", "De-activate Curses", "No. I want to keep ancient Curses active.");
		} else if (player.getSkill().getLevelForExperience(Skill.PRAYER) < 50) {
			player.sendMessage("You need to have at least 50 Prayer to activate ancient Curses.");
			stop();
		} else {
			choice("Select an Option", "Activate Curses", "No. I want to keep normal Prayers active.");
		}
	}

}
