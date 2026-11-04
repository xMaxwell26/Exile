package com.koios.game.content.skill;

import com.koios.game.content.skill.fishing.Fishing;
import com.koios.game.content.skill.fishing.FishingData.Fishable;
import com.koios.game.content.skill.fishing.FishingData.Spots;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class SkillCycleCalculator {

	/**
	 * @return The amount of cycles
	 */
	public static int fishingCycles(Player p, boolean firstClick, Spots s) {

		Fishable f = Fishable.forId(Fishing.getFish(p, firstClick ? s.getFirstFishId() : s.getThirdFishId(), firstClick ? s.getSecondFishId() : s.getFourthFishId()));

		int summon_boost = 0;

		if (p.getSummoning().getFamiliar() != null && p.getSummoning().getFamiliar().getAbility() != null) {
			if (p.getSummoning().getFamiliar().getAbility().getSkillToBoost() == Skill.FISHING) {
				summon_boost = p.getSummoning().getFamiliar().getAbility().getBoostAmount();
			}
		}

		int level = f.getRequiredLevel();

		double var1 = Math.ceil(level * 50 - (p.getSkill().getLevel(Skill.FISHING) + summon_boost) * 10);

		double val2 = level * 0.0625 * 3;
		double val3 = level <= 2 ? 1 : NumberUtil.inclusiveRandom((int) (level * 0.0625));
		double val41 = level - (7 + NumberUtil.inclusiveRandom(4));
		double val4 = NumberUtil.inclusiveRandom((int) (val41 < 0 ? 2 : val41));

		double cycle = var1 / val2 * val3 + val4;
		cycle = cycle < 4 ? 4 : cycle > 15 ? 7 : cycle;
		return (int) Math.round(cycle);
	}

	public static int woodcuttingCycles(int axe, int tree, int level) {
		double timer = axe * 2 + 20 + NumberUtil.inclusiveRandom(20) - (tree * (tree * 0.75) + level);

		if (timer < 3.0) {
			return 3;
		} else {
			return (int) timer;
		}
	}

	public static int miningCycles(int rockReq, double pickDelay, int level) {
		double timer = rockReq * 2 + 20 + NumberUtil.inclusiveRandom(20) - (pickDelay * (pickDelay * 0.75) + level);
		timer = timer < 5 ? 7 : timer > 20 ? 20 : timer;
		return (int) timer;
	}

}