package com.koios.game.content;

import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public class HealingContent {

	public static void produceHealing(Player player) {
		if (player.isBusy()) {
			return;
		}

		player.setBusy(true);

		GameService.getTaskScheduler().schedule(new Task(2) {

			@Override
			public void execute() {
				if (player.isDisconnected()) {
					stop();
					return;
				}

				player.getSkill().restoreLevel(Skill.HITPOINTS);
				player.sendMessage("You have been healed.");
				player.playAnimation(new Animation(1500));

				GameService.getTaskScheduler().schedule(new Task(2) {

					@Override
					public void execute() {
						player.playAnimation(new Animation(1914));
						stop();
					}

				});

				player.playGraphics(new Graphic(444));
				player.getSkill().restoreLevel(Skill.PRAYER);
				player.specialEnergy = 100;
				player.getItems().updateSpecialBar();
				player.getItems().addSpecialBar(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
				player.sendMessage("You've been healed.");
				player.setBusy(false);
				stop();
			}

		});
	}

}
