package com.koios.game.model.player.task;

import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

public class SkullTimerTask extends EntityTask<Player> {

	public SkullTimerTask(Player entity) {
		super(entity);
	}

	@Override
	protected void execute() {
		entity.skullTimer--;
		if (entity.skullTimer <= 0) {
			entity.isSkulled = false;
			entity.attackedPlayers.clear();
			entity.skullIcon = -1;
			entity.skullTimer = -1;
			entity.requestUpdates();
			stop();
		}
	}

}
