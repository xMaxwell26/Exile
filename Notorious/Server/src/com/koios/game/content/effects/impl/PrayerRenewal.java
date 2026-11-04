package com.koios.game.content.effects.impl;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;

/**
 * Handle the prayer renewal effects in the player
 *
 * @author Gabriel | Wolfs Darker
 */
public class PrayerRenewal implements Effect {

	private double increase;
	private Player player;
	private int timeLeft = -1;

	public PrayerRenewal() {
	}

	public PrayerRenewal(Player player) {
		this.player = player;
	}

	public PrayerRenewal(Player player, int timer) {
		this.player = player;
		timeLeft = timer;
	}

	@Override
	public void end() {
	}

	@Override
	public Duration getDuration() {
		return Duration.TEMPORARY;
	}

	@Override
	public String toString() {
		return EffectConstants.PRAYER_RENEWAL_NAME + ":" + timeLeft;
	}

	@Override
	public String getName() {
		return EffectConstants.PRAYER_RENEWAL_NAME;
	}

	@Override
	public QueuePolicy getQueueType() {
		return QueuePolicy.ALWAYS;
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
		timeLeft = EffectConstants.PRAYER_RENEWAL_TIME;
		player.getEF().setQueueState(false);
	}

	@Override
	public void start() {
		if (timeLeft <= 0) {
			timeLeft = EffectConstants.PRAYER_RENEWAL_TIME;
		}

		player.getEF().setQueueState(false);
	}

	@Override
	public void tick() {
		timeLeft--;

		if (timeLeft <= 0 || player.getSkill().getLevel(Skill.PRAYER) == player.getSkill().getLevelForExperience(Skill.PRAYER)) {
			return;
		}

		increase += (player.getSkill().getLevelForExperience(Skill.PRAYER) * 4 + 120) / 450;

		if (increase <= 0) {
			increase = 0.5;
		}

		if (increase < 10) {
			return;
		}

		player.getSkill().incrementLevel(Skill.PRAYER);

		if (player.getSkill().getLevel(Skill.PRAYER) > player.getSkill().getLevelForExperience(Skill.PRAYER)) {
			player.getSkill().restoreLevel(Skill.PRAYER);
		}

		increase = 0;
	}

	@Override
	public void load(Player p, String s) {
		int time = Integer.parseInt(s.split(":")[1].replace(":", ""));

		if (time > 0) {
			Effect e = new PrayerRenewal(p, time);
			p.getEF().queueEffect(e);
		}
	}

	@Override
	public String getNonWalkableMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
