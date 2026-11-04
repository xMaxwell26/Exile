package com.koios.game.content.wilderness;

import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public final class DeathTauntMessages {

	public static void sendTaunt(Player player, Player target) {
		String name = target.getName();

		switch (NumberUtil.inclusiveRandom(10)) {
		case 0:
			player.sendMessage("You have defeated " + name + ".");
			return;

		case 1:
			player.sendMessage("You were clearly a better fighter than " + name + ".");
			break;

		case 2:
			player.sendMessage(name + " was no match for you.");
			break;

		case 3:
			player.sendMessage("With a crushing blow you finish " + name + ".");
			break;

		case 4:
			player.sendMessage(name + " didn't stand a chance against you.");
			break;

		case 5:
			player.sendMessage("Can anyone defeat you? Certainly not " + name + ".");
			break;

		case 6:
			player.sendMessage(name + " falls before your might.");
			break;

		case 7:
			player.sendMessage("A humiliating defeat for " + name + ".");
			break;

		case 8:
			player.sendMessage(name + " has won a free ticket to Edgeville.");
			break;

		case 9:
			player.sendMessage("It's all over for " + name + ".");
			break;

		default:
			player.sendMessage(name + " regrets the day " + (target.getAppearance().isMale() ? "he" : "she") + " met you in combat.");
		}
	}

	private DeathTauntMessages() {
		throw new InternalError();
	}

}
