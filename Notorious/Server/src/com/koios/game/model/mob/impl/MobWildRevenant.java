package com.koios.game.model.mob.impl;

import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.util.NumberUtil;

public class MobWildRevenant extends Mob {

	public static final int[][] SPAWN_COORDINATIONS = { { 3238, 3710 }, { 3218, 3802 }, { 3246, 3840 }, { 3329, 3873 }, { 3339, 3788 }, { 3362, 3738 }, { 3335, 3721 },
			{ 3018, 3763 }, { 2987, 3823 }, { 2994, 3736 }, { 3007, 3653 }, { 2997, 3887 }, { 3126, 3867 } };

	protected MobWildRevenant(int id, Location location) {
		super(id, location);
	}

	public MobWildRevenant(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobWildRevenant copy() {
		MobWildRevenant mob = new MobWildRevenant(getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void death() {
		homeLocation = new Location(SPAWN_COORDINATIONS[NumberUtil.random(MobWildRevenant.SPAWN_COORDINATIONS.length)][0],
				SPAWN_COORDINATIONS[NumberUtil.random(MobWildRevenant.SPAWN_COORDINATIONS.length)][1]);
		super.death();
	}

}
