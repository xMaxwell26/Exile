package com.koios.game.content.bountyhunter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class BountyHunterTick extends Task {

	private final List<String> bountyHunters = new CopyOnWriteArrayList<String>();
	private int updateTick = 0;
	private int targetTick = 0;

	public BountyHunterTick() {
		super(1, false);
		updateTick = 25;
		targetTick = 300;
	}

	@Override
	protected void execute() {
		for (Player player : GameService.getWorld().getPlayers()) {
			if (player == null || !GameService.getWorld().isPlayerLoggedIn(player.getName()) || !player.inWild()) {
				continue;
			}

			if (!bountyHunters.contains(player.getName())) {
				bountyHunters.add(player.getName());
			}
		}

		if (bountyHunters.isEmpty() || bountyHunters.size() == 0) {
			return;
		}

		for (Iterator<String> it = new LinkedList<String>(bountyHunters).iterator(); it.hasNext();) {
			String name = it.next();

			if (name == null) {
				it.remove();
				bountyHunters.remove(name);
				continue;
			}

			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(name);
			Player player = maybePlayer.orElse(null);

			if (player == null) {
				it.remove();
				bountyHunters.remove(name);
				continue;
			}

			if (player.getBountyHunter().hasTarget()) {
				if (player.inWild() && player.getBountyHunter().getLeftWildernessTime() != 0) {
					player.getBountyHunter().setLeftWilderness(0);
				}

				if (!player.inWild() && player.getBountyHunter().getLeftWildernessTime() == 0) {
					player.getBountyHunter().setLeftWilderness(System.currentTimeMillis() + 120_000);
					player.sendMessage("@red@You have 2 minutes to return to the Wilderness before you lose your target.");
				}

				if (!player.inWild() && player.getBountyHunter().timeOver(0)) {
					player.getBountyHunter().setLeftWilderness(0);
					player.sendMessage("@red@You have abandoned your target.");
					player.getBountyHunter().avoidTarget();
					it.remove();
					bountyHunters.remove(name);
					continue;
				}

				if (!player.inWild() && player.getBountyHunter().timeOver(60000) && !player.getBountyHunter().timeOver(59000)) {
					player.sendMessage("@red@You have one minute to return to the Wilderness before you lose your target.");
				}

				if (updateTick <= 0) {
					player.getBountyHunter().updateInterface();
				}
			} else {
				if (!player.inWild()) {
					it.remove();
					bountyHunters.remove(name);
					continue;
				}

				if (player.getBountyHunter().getTargetName() != null && !GameService.getWorld().isPlayerLoggedIn(player.getBountyHunter().getTargetName())) {
					player.sendMessage("@red@Your target is no longer avaliable.");
					player.getBountyHunter().reset();
					continue;
				}

				if (targetTick <= 0) {
					player.getBountyHunter().searchNextTarget(bountyHunters);
				}
			}
		}

		if (updateTick <= 0) {
			updateTick = 25;
		}

		if (targetTick <= 0) {
			targetTick = 300;
		}

		targetTick--;
		updateTick--;
	}

}
