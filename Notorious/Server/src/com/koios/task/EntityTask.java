package com.koios.task;

import com.koios.game.model.EntityCharacter;

public abstract class EntityTask<T extends EntityCharacter> extends Task {

	protected final boolean walkable;
	protected T entity;

	public EntityTask(int delay, int maximumIterations, T entity) {
		this(delay, maximumIterations, entity, true);
	}

	public EntityTask(int delay, int maximumIterations, T entity, boolean walkable) {
		super(delay, maximumIterations);
		this.entity = entity;
		this.walkable = walkable;
	}

	public EntityTask(T entity) {
		this(1, false, entity, true);
	}

	public EntityTask(int delay, T entity, boolean walkable) {
		this(delay, false, entity, walkable);
	}

	public EntityTask(int delay, T entity) {
		this(delay, false, entity, true);
	}

	public EntityTask(int delay, boolean immediate, T entity, boolean walkable) {
		super(delay, immediate);
		this.entity = entity;
		this.walkable = walkable;
	}

	@Override
	public boolean pulse() {
		boolean running = super.pulse();
		if (running) {
			if (!walkable && entity.isMoving()) {
				stop();
				return false;
			}
		}

		return running;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

}