package com.koios.game.content.skill.hunter.impl;

import com.koios.game.model.mob.Mob;
import com.koios.task.EntityTask;

/**
 * @author J
 */
@SuppressWarnings("rawtypes")
public abstract class MobAction<E extends Enum<E>> extends EntityTask {

	private E state;

	@SuppressWarnings("unchecked")
	protected MobAction(Mob mob, E state) {
		super(1, true, mob, true);
		this.state = state;
	}

	@SuppressWarnings("unchecked")
	protected MobAction(Mob mob, int delay, E state) {
		super(delay, true, mob, true);
		this.state = state;
	}

	@Override
	public void execute() {
		int code = executeState(state);

		if (code == 0) {
			stop();
		}
	}

	public final int executeCode() {
		return this.executeState(state);
	}

	protected abstract int executeState(E state);

	public void setState(E state) {
		this.state = state;
	}

}