package com.koios.game.content.effects.impl;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.player.Player;

/**
 * Handle the overload potion effects
 *
 * @author Gabriel | Wolfs Darker
 */
public class Overload implements Effect {

	private int timeLeft;
	private int restoreTick;
	private int damage;
	private boolean inWildy;
	private Player player;

	public Overload() {
	}

	public Overload(Player player) {
		this.player = player;
	}

	public Overload(Player player, int time) {
		this.player = player;
		timeLeft = time;
	}

	@Override
	public void end() {
		for (int i = 0; i <= 6; i++) {
			if (i == 5 || i == 3) {
				continue;
			}

			player.getSkill().restoreLevel(i);
		}

		player.getEF().setQueueState(false);
	}

	@Override
	public Duration getDuration() {
		return Duration.TEMPORARY;
	}

	@Override
	public String getName() {
		return EffectConstants.OVERLOAD_NAME;
	}

	@Override
	public QueuePolicy getQueueType() {
		return QueuePolicy.NEVER;
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
	public void load(Player p, String s) {
		int time = Integer.parseInt(s.split(":")[1].replace(":", ""));

		if (time > 0) {
			Effect e = new Overload(p, time);
			p.getEF().queueEffect(e);
		}
	}

	@Override
	public void renew() {
		timeLeft = EffectConstants.OVERLOAD_TIME;
		damage = 6;
		updateStats();
		player.getEF().setQueueState(false);
	}

	@Override
	public void start() {
		if (timeLeft <= 0) {
			timeLeft = EffectConstants.OVERLOAD_TIME;
			damage = 6;
		}

		updateStats();
		player.getEF().setQueueState(false);
	}

	@Override
	public void tick() {
		timeLeft--;

		if (player.inWild() && !inWildy) {
			inWildy = !inWildy;
			updateStats();
		}

		if (damage > 0) {
			player.playAnimation(new Animation(3170));

			if (player.getSkill().getLevel(3) > 10) {
				player.setDamage(new Damage(10, HitIcons.NONE), player);
			}

			damage--;
		}

		if (timeLeft <= 0) {
			return;
		}

		restoreTick++;

		if (restoreTick < 25) {
			return;
		}

		restoreTick = 0;
		updateStats();
	}

	@Override
	public String toString() {
		return EffectConstants.OVERLOAD_NAME + ":" + timeLeft;
	}

	/**
	 * Update the player's stats
	 */
	public void updateStats() {
		for (int i = 0; i <= 6; i++) {
			if (i == 5 || i == 3) {
				continue;
			}

			int boost = (int) ((i == 4 || i == 6 ? 3 : 5) + player.getSkill().getLevelForExperience(i) * .22);

			if (player.inWild()) {
				boost = (int) (i == 4 || i == 6 ? player.getSkill().getLevelForExperience(i) * .13 + 1 : player.getSkill().getLevelForExperience(i) * .20);
			}

			player.getSkill().incrementLevel(i, boost);

			if (player.getSkill().getLevel(i) > player.getSkill().getLevelForExperience(i) + boost) {

				player.getSkill().setLevel(i, player.getSkill().getLevelForExperience(i) + boost);
			}
		}
	}

	@Override
	public String getNonWalkableMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
