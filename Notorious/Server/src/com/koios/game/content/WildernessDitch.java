package com.koios.game.content;

import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

public class WildernessDitch {

	public static void startMovement(Player player, Location location) {
		boolean south = player.getY() > location.getY();

		final boolean running = player.getRunEnergy().isRunning();
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		final int walkAnimation = player.getWalkAnim(weapon);

		final int yModifier = (south ? -3 : 3);
		final Location endLocation = Location.create(player.getX(), yModifier + player.getY());

		player.getRunEnergy().setRunning(false);
		player.getMovementHandler().setForceMove(true);
		player.walkAnim = 6132;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		player.getMovementHandler().walkTo(0, yModifier);

		player.getTaskScheduler().schedule(new EntityTask<Player>(1, player) {

			@Override
			protected void execute() {
				if (player.getLocation().equals(endLocation)) {
					stop();
				}
			}

			@Override
			protected void onStop() {
				player.getRunEnergy().setRunning(running);
				player.walkAnim = walkAnimation;
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getMovementHandler().setForceMove(false);
			}

		});

	}

}
