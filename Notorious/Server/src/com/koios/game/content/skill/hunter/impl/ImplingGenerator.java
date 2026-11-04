package com.koios.game.content.skill.hunter.impl;

import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.util.NumberUtil;

public final class ImplingGenerator {

	private static int cycleCount = 1;

	public static final Location[] POSITIONS = new Location[] { Location.create(0, 3179, 3361), Location.create(0, 3299, 3466), Location.create(0, 2976, 3505),
			Location.create(0, 2935, 3396), Location.create(0, 2864, 3349), Location.create(0, 2753, 3457), Location.create(0, 2688, 3421), Location.create(0, 2568, 3421),
			Location.create(0, 2437, 3438), Location.create(0, 2448, 3329), Location.create(0, 2476, 3107), Location.create(0, 2511, 2977), Location.create(0, 2625, 2962),
			Location.create(0, 2754, 2758), Location.create(0, 2741, 3308), Location.create(0, 2904, 2981), Location.create(0, 2893, 3132), Location.create(0, 3081, 3209),
			Location.create(0, 3328, 3283), Location.create(0, 2983, 3204), Location.create(0, 2983, 3204), Location.create(0, 2711, 3626), Location.create(0, 3225, 2963), };

	private static final Mob[] mobs = new Mob[ImplingGenerator.POSITIONS.length];

	public static void init() {
		for (int i = 0; i < ImplingGenerator.mobs.length; i++) {
			ImplingGenerator.mobs[i] = MobManager.spawnMob(ImplingGenerator.newImpling(), ImplingGenerator.POSITIONS[i], 1, -1, -1, -1, -1);

		}
	}

	private static int newImpling() {
		int value = NumberUtil.random(5000);

		if (value < 750) {
			return Hunter.Impling.BABY.getMobId();
		}

		if (value < 1450) {
			return Hunter.Impling.YOUNG.getMobId();
		}

		if (value < 2100) {
			return Hunter.Impling.GOURMET.getMobId();
		}

		if (value < 2700) {
			return Hunter.Impling.EARTH.getMobId();
		}

		if (value < 3250) {
			return Hunter.Impling.ESSENCE.getMobId();
		}

		if (value < 3750) {
			return Hunter.Impling.ECLECTIC.getMobId();
		}

		if (value < 4100) {
			return Hunter.Impling.SPIRIT.getMobId();
		}

		if (value < 4400) {
			return Hunter.Impling.NATURE.getMobId();
		}

		if (value < 4650) {
			return Hunter.Impling.MAGPIE.getMobId();
		}

		if (value < 4800) {
			return Hunter.Impling.NINJA.getMobId();
		}

		if (value < 4900) {
			return Hunter.Impling.PIRATE.getMobId();
		}

		if (value < 4925) {
			return Hunter.Impling.DRAGON.getMobId();
		}

		if (value < 4975) {
			return Hunter.Impling.ZOMBIE.getMobId();
		}

		return Hunter.Impling.KINGLY.getMobId();
	}

	public static void process() {
		if (ImplingGenerator.cycleCount / 500 == 1) {
			for (Mob npc : ImplingGenerator.mobs) {
				npc.remove();
			}

			for (int i = 0; i < ImplingGenerator.mobs.length; i++) {
				ImplingGenerator.mobs[i] = MobManager.spawnMob(ImplingGenerator.newImpling(), ImplingGenerator.POSITIONS[i], 1, -1, -1, -1, -1);
			}
			ImplingGenerator.cycleCount = 0;
		}

		ImplingGenerator.cycleCount++;
	}

}