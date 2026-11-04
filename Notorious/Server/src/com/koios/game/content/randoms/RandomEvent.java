package com.koios.game.content.randoms;

import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Random event
 *
 * @author Mikey`
 */
public abstract class RandomEvent {

	public static final long MIN_RANDOM_WAIT_TIME = 60_000;

	public static void tryRandom(Player player) {
		if (System.currentTimeMillis() - player.randomAttemptDelay >= MIN_RANDOM_WAIT_TIME) {
			if (NumberUtil.random(0, 700) < 1) {
				RandomEvent randomEvent = new MysteryBoxRandomEvent();
				randomEvent.init(player);
				player.randomAttemptDelay = System.currentTimeMillis();
			}
		}
	}

	public abstract void init(Player player);

}
