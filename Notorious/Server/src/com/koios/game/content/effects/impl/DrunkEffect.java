package com.koios.game.content.effects.impl;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendShakeScreen;

public class DrunkEffect implements Effect {

	private final Player player;
	private int timeLeft;
	private final int type;

	public DrunkEffect(Player p, int type) {
		player = p;
		timeLeft = EffectConstants.DRUNK_TIME * type;
		this.type = type;
	}

	public DrunkEffect(Player p, int time, int type) {
		player = p;
		timeLeft = time;
		this.type = type;
	}

	@Override
	public String getName() {
		return EffectConstants.DRUNK_NAME;
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
		timeLeft = EffectConstants.DRUNK_TIME;
		applyEffect();
		player.getEF().setQueueState(false);
	}

	@Override
	public void start() {
		applyEffect();
		player.getEF().setQueueState(false);
	}

	@Override
	public void tick() {

		timeLeft--;

		if (timeLeft <= 0) {
			return;
		}

		if (player.walkAnim != 2769) {
			player.walkAnim = 2769;
		}
		if (player.standAnim != 3040) {
			player.standAnim = 3040;
		}
	}

	@Override
	public void end() {
		player.walkAnim = -1;
		player.standAnim = -1;

		player.getSession().send(new SendShakeScreen(2, 0, 0, 0));

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	public void load(Player p, String s) {

		String[] args = s.split(":");

		int time = -1;
		int type = -1;

		try {
			time = Integer.parseInt(args[1].replace(":", ""));
			type = Integer.parseInt(args[2].replace(":", ""));
		} catch (Exception e) {
		}

		if (time > 0 && type > 0) {
			Effect e = new DrunkEffect(p, time, type);
			p.getEF().queueEffect(e);
		}
	}

	public void applyEffect() {

		player.getSkill().incrementLevel(Skill.STRENGTH, (int) (player.getSkill().getLevelForExperience(Skill.STRENGTH) * (0.04 * type)));
		if (player.getSkill().getLevel(Skill.STRENGTH) > player.getSkill().getLevelForExperience(Skill.STRENGTH)
				+ player.getSkill().getLevelForExperience(Skill.STRENGTH) * (0.04 * type)) {
			player.getSkill().setLevel(Skill.STRENGTH,
					(int) (player.getSkill().getLevelForExperience(Skill.STRENGTH) + player.getSkill().getLevelForExperience(Skill.STRENGTH) * (0.04 * type)));
		}

		player.getSkill().decrementLevel(Skill.ATTACK, (int) (player.getSkill().getLevelForExperience(Skill.ATTACK) * (0.07 * type)));

		player.getSession().send(new SendShakeScreen(2, 3, 4, 5));

		player.walkAnim = 2769;
		player.standAnim = 3040;

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	public String toString() {
		return EffectConstants.DRUNK_NAME + ":" + timeLeft + ":" + type;
	}

	@Override
	public String getNonWalkableMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
