package com.koios.game.content.cluescroll;

import java.util.function.Predicate;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;

public class CoordinateScroll extends ClueScroll {

	private final String lines;

	public CoordinateScroll(int item, String lines, Predicate<Location> emoteLocation, Difficulty difficulty) {
		super(item, emoteLocation, difficulty);
		this.lines = lines;
	}

	@Override
	public void attemptCompletion(Player player) {
		if (player.getAttribute("KILL_AGENT", false)) {
			return;
		}

		player.setAttribute("KILL_AGENT", true);
		Location spawn = Constants.getClearAdjacentLocation(player.getLocation(), 1);
		Mob wizard = MobManager.spawnMob(player, 1264, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 120, 20, 170, 170, true, true);
		wizard.playGraphics(new Graphic(86));

		GameService.getTaskScheduler().schedule(new Task() {

			@Override
			protected void execute() {
				if (player.isDisconnected() || wizard.isDead() || player.getLocation().getDistance(wizard.getLocation()) > 25) {
					wizard.setDead(true);
					stop();
				}
			}

		});
	}

	@Override
	public void open(Player player) {
		player.getSession().send(new SendRemoveInterfacePacket());
		player.getInterfaceState().openInterface(6965);
		String[] lines = this.lines.split("\n");

		for (int i = 6968; i <= 6975; i++) {
			player.getSession().send(new SendStringPacket(lines[i - 6968], i));
		}
	}

}
