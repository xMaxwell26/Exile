package com.koios.game.model.player.task;

import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

@SuppressWarnings("rawtypes")
public class PlaygroundAltarTask extends EntityTask {

	private final Player player;

	@SuppressWarnings("unchecked")
	public PlaygroundAltarTask(Player player) {
		super(1, player);
		this.player = player;
	}

	@Override
	protected void execute() {
		if (player.playgroundAltarCooldown > 0) {
			player.playgroundAltarCooldown--;
		} else {
			stop();
		}
	}

}
