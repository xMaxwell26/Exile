package com.koios.game.model.movement;

import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.Walking;

public class MobMovementHandler extends MovementHandler {

	private final Mob mob;

	public MobMovementHandler(Mob mob) {
		super(mob);
		this.mob = mob;
	}

	@Override
	public void process() {
	}

	@Override
	public boolean moving() {
		return mob.getFollowing().isFollowing();
	}

	@Override
	public boolean canMoveTo(int direction) {
		if (mob.isDead()) {
			return false;
		}
		return Walking.canMoveTo(mob, direction);
	}

	@Override
	public boolean canMoveTo(int x, int y, int size, int direction) {
		if (mob.isDead()) {
			return false;
		}
		return Walking.canMoveTo(mob, x, y, direction, size);
	}

}
