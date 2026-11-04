package com.koios.game.content.skill.summoning.actions;

import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;

/**
 * Handle the dismiss of the familiar.
 *
 * @author Gabriel | Wolfs Darker
 */
public class DismissFamiliar {
	public static void execute(Player player) {

		Mob familiar = player.getSummoning().getFamiliar() == null ? player.getSummoning().getPet() : player.getSummoning().getFamiliar();

		if (familiar == null) {
			return;
		}

		player.sendMessage("You dismiss your " + (familiar instanceof MobFamiliar ? "familiar" : "pet") + ".");

		familiar.remove();

		player.getSummoning().reset();

		player.getSession().send(new SendSidebarInterfacePacket(16, -1));
		player.getSession().send(new SendOpenSideTabPacket(4));
	}

}