package com.koios.game.content.effects.impl;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;

public class FreezeEffect implements Effect {

	private final EntityCharacter entity;
	private final String message;
	private int timeLeft;

	public FreezeEffect(EntityCharacter entity, int duration, String message) {
		this.entity = entity;
		this.message = message;
		timeLeft = duration;
	}

	@Override
	public String getName() {
		return EffectConstants.FREEZE_NAME;
	}

	@Override
	public QueuePolicy getQueueType() {
		return QueuePolicy.NEVER;
	}

	@Override
	public Duration getDuration() {
		return Duration.TEMPORARY;
	}

	@Override
	public int getTimeLeft() {
		return timeLeft;
	}

	@Override
	public Walk getWalkEffect() {
		return Walk.NON_WALKABLE;
	}

	@Override
	public void renew() {
	}

	@Override
	public void start() {
		if (entity == null) {
			return;
		}
		entity.getEF().setQueueState(false);
	}

	@Override
	public void tick() {
		if (timeLeft > 0) {
			timeLeft--;
		}

		if (entity == null || entity.isDead()) {
			timeLeft = 0;
		}
	}

	@Override
	public void end() {

		if (entity == null) {
			return;
		}
		entity.getEF().setQueueState(false);
	}

	@Override
	public void load(Player p, String s) {
		String[] args = s.split(":");
		int time = -1;
		String message = "";

		try {
			time = Integer.parseInt(args[1].replace(":", ""));
			message = args[2].replace(":", "");
		} catch (Exception ex) {
		}

		if (time > 0 && !message.isEmpty()) {
			Effect effect = new FreezeEffect(p, time, message);
			p.getEF().queueEffect(effect);
		}
	}

	@Override
	public String getNonWalkableMessage() {
		return message;
	}

	@Override
	public String toString() {
		return EffectConstants.FREEZE_NAME + ":" + timeLeft + ":" + message;
	}

}
