package com.koios.game.content.skill.summoning.actions;

import com.koios.Constants;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

/**
 * Calls the familiar ( teleports near the summoner )
 *
 * @author Gabriel | Wolfs Darker
 */
public class CallFamiliar {

	public static void execute(Player player) {
		Mob familar = player.getSummoning().getFamiliar();
		boolean pet = false;
		if (familar == null) {
			familar = player.getSummoning().getPet();
			if (familar == null) {
				return;
			}
			pet = true;
		}

		if (player.inBank()) {
			player.sendMessage("You are in a restricted area, your familar will wait for you outside.");
			return;
		}

		if (familar.getLocation().distanceTo(player.getLocation()) < 2) {
			player.sendMessage("Your familiar is already near you!");
			return;
		}

		Location l = Constants.getClearAdjacentLocation(player.getLocation(), familar.getSize());

		if (l == null) {
			player.sendMessage("There is no room for your familar here!");
			return;
		}

		familar.setVisible(false);
		familar.teleport(l);
		familar.setFaceIndex(player.getIndex(), true);
		familar.getFollowing().setFollow(player, FollowType.DEFAULT);
		familar.setVisible(true);

		if (!pet) {
			player.getSummoning().getFamiliar().playGraphics(new Graphic(player.getSummoning().getFamiliar().getSize() <= 1 ? 1314 : 1315));
		}
	}

}
