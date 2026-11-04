package com.koios.game.model.mob;

import com.koios.GameService;
import com.koios.game.map.path.SimplePathWalker;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class MobWalkTask extends Task {

	private final Mob mob;
	private final Location location;
	private byte wait = 0;
	private final boolean shouldWait;

	public MobWalkTask(Mob mob, Location location, boolean shouldWait) {
		super(1);
		this.mob = mob;
		this.location = location;
		this.shouldWait = shouldWait;
	}

	@Override
	public void execute() {
		if (mob.isDead()) {
			stop();
			return;
		}

		if (Location.getManhattanDistance(location, mob.getLocation()) <= 0) {
			stop();
		} else {
			SimplePathWalker.walkToNextTile(mob, location);

			if (mob.getDirection1() == -1) {
				if (shouldWait) {
					if (wait != 0) {
						wait = (byte) (wait - 1);
						return;
					}

					for (Player p : GameService.getWorld().getPlayers()) {
						if (p != null && mob.getLocation().isViewableFrom(p.getLocation())) {
							wait = 15;
							return;
						}
					}

					mob.teleport(location);
				}

				stop();
			}
		}
	}

	@Override
	public void onStop() {
		mob.inTask = false;
		mob.walkingHome = false;
	}

}
