package com.koios.game.model.player.task;

import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

@SuppressWarnings("rawtypes")
public class MiasmicEffectTask extends EntityTask {

	private final Player player;

	@SuppressWarnings("unchecked")
	public MiasmicEffectTask(Player player) {
		super(1, player);
		this.player = player;
	}

	@Override
	protected void execute() {
		if (player.miasmicCloud > 0) {
			player.miasmicCloud--;
		} else if (player.miasmicCloud == 0) {
			player.sendMessage("The miasmic cloud has faded away.");
			player.miasmicCloud = -1;
			stop();
		}
	}

}
