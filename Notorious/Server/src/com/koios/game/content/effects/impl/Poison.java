package com.koios.game.content.effects.impl;

import com.koios.game.content.effects.Effect;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.player.Player;

public class Poison implements Effect {

	private final EntityCharacter entity, aggressor;
	private int intervals = 4;
	private int timeLeft;
	private int damage;

	public Poison(EntityCharacter entity, int damage, EntityCharacter aggressor) {
		this.entity = entity;
		this.aggressor = aggressor;
		timeLeft = 17;
		this.damage = damage;
	}

	@Override
	public String getName() {
		return "POISON";
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
	public String toString() {
		return "POISON:" + timeLeft + ":" + damage;
	}

	@Override
	public int getTimeLeft() {
		return timeLeft;
	}

	@Override
	public Walk getWalkEffect() {
		return Walk.WALKABLE;
	}

	@Override
	public void renew() {
	}

	@Override
	public void start() {
		entity.setDamage(new Damage(damage, HitMasks.POISON), aggressor == null ? entity : aggressor);
		if (entity.isPlayer()) {
			entity.toPlayer().sendMessage("You have been poisoned!");
		}
		entity.getEF().setQueueState(false);
	}

	@Override
	public void tick() {

		if (entity == null) {
			timeLeft = 0;
			return;
		}

		if (entity.isDead()) {
			damage = 0;
			timeLeft = 0;
		}

		if (--timeLeft == 1 && --intervals >= 0) {
			entity.setDamage(new Damage(damage, HitMasks.POISON), aggressor == null ? entity : aggressor);
			timeLeft = 17;
		}
	}

	@Override
	public void end() {

		if (entity == null) {
			return;
		}

		if (entity.isPlayer()) {
			entity.toPlayer().sendMessage("Your poison effect fades away.");
		}

		entity.getEF().setQueueState(false);
	}

	@Override
	public void load(Player p, String s) {
		String[] args = s.split(":");

		int time = -1;
		int damage = -1;

		try {
			time = Integer.parseInt(args[1].replace(":", ""));
			damage = Integer.parseInt(args[2].replace(":", ""));
		} catch (Exception e) {
		}

		if (time > 0 && damage > 0) {
			Effect e = new Poison(p, damage, null);
			p.getEF().queueEffect(e);
		}
	}

	@Override
	public String getNonWalkableMessage() {
		return null;
	}

}
