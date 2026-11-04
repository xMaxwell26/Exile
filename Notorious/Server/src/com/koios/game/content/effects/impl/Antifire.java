package com.koios.game.content.effects.impl;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.model.player.Player;

public class Antifire implements Effect {

	private final Player player;
	private int timeLeft;

	public Antifire(Player player) {
		this.player = player;
		timeLeft = EffectConstants.ANTIFIRE_TIME;
	}

	public Antifire(Player player, int time) {
		this.player = player;
		timeLeft = time;
	}

	@Override
	public String getName() {
		return EffectConstants.ANTIFIRE_NAME;
	}

	@Override
	public QueuePolicy getQueueType() {
		return QueuePolicy.ALWAYS;
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
		return Walk.WALKABLE;
	}

	@Override
	public void renew() {
		timeLeft = EffectConstants.ANTIFIRE_TIME;
		player.getEF().setQueueState(false);
	}

	@Override
	public void start() {

		if (timeLeft <= 0) {
			timeLeft = EffectConstants.ANTIFIRE_TIME;
		}

		player.getEF().setQueueState(false);
	}

	@Override
	public void tick() {
		timeLeft--;

		if (timeLeft == 25) {
			player.sendMessage("Your resistance to dragonfire is about to run out.");
		}

	}

	@Override
	public void end() {

		player.sendMessage("Your resistance to dragonfire has run out.");

		player.getEF().setQueueState(false);

	}

	@Override
	public void load(Player p, String s) {
		int time = Integer.parseInt(s.split(":")[1].replace(":", ""));

		if (time > 0) {
			Effect e = new Antifire(p, time);
			p.getEF().queueEffect(e);
		}
	}

	@Override
	public String getNonWalkableMessage() {
		return null;
	}

	@Override
	public String toString() {
		return EffectConstants.ANTIFIRE_NAME + ":" + timeLeft;
	}

}
