package com.koios.game.model.mob.impl;

import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;

public class MobBanker extends Mob {

	public MobBanker(int index, int id, Location location) {
		super(index, id, location);
		super.setInterationDistance(2);
	}

}
