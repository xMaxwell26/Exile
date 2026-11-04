package com.koios.game.content.skill.summoning;

import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class SummoningTask extends Task {

	private final Player player;
	private int tick;

	public SummoningTask(Player player) {
		super(50);
		this.player = player;
	}

	@Override
	protected void execute() {
		tick++;

		if (tick == 3) {
			player.getSkill().setLevel(Skill.SUMMONING, player.getSkill().getLevel(Skill.SUMMONING) - 1);
			tick = 0;
		}

		if (player.getSummoning().getFamiliar() == null) {
			stop();
			return;
		}

		player.getSummoning().setSpecialPoints(player.getSummoning().getSpecialPoints() + 10);
		player.getSummoning().setRemainingTicks(player.getSummoning().getRemainingTicks() - 1);

		if (player.getSummoning().getRemainingTicks() == 1 || player.getSummoning().getRemainingTicks() == 2) {
			player.sendMessage("You have " + (player.getSummoning().getRemainingTicks() == 1 ? "30 seconds" : "1 minute") + " before your familiar vanishes.");
		}

		if (player.getSummoning().getRemainingTicks() <= 0) {
			player.getSummoning().handleAction(SummoningData.DURATION_OVER, new Object[] { true });
			return;
		}

		if (player.getSkill().getLevel(Skill.SUMMONING) <= 0) {
			player.getSummoning().handleAction(SummoningData.DURATION_OVER, new Object[] { false });
			return;
		}

		player.getSummoning().sendTab(false);
	}

}