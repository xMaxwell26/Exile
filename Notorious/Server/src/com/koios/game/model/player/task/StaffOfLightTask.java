package com.koios.game.model.player.task;

import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

public class StaffOfLightTask extends EntityTask<Player> {

	public StaffOfLightTask(Player entity) {
		super(1, entity);
	}

	@Override
	protected void execute() {
		if (entity.staffOfLightProtection > 0) {
			if (entity.getEquipment().get(Equipment.SLOT_WEAPON) != null && entity.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 15486) {
				entity.staffOfLightProtection = 0;
				stop();
			} else {
				--entity.staffOfLightProtection;
			}
		}

	}

}
