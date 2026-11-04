package com.koios.game.content.partyroom;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.koios.GameService;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.task.Task;

/**
 * The activity that handles the dancing women party room event.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class DancingWomenEvent implements Runnable {

	/**
	 * The array of text that will be sent during the event.
	 */
	private static final String[] TEXT = { "We've got spirit yes we do!", "We've got spirit how about you?", "P-A-R-T-Y OH YEAH!", "Crank up the music bring out the food!",
			"We are the dancing mistresses!", "We can satisfy your desires...", "Thanks for having us!", "Bye!" };

	@Override
	public void run() {
		Random random = new Random();
		List<Mob> mobs = new LinkedList<>();
		Location start = new Location(3049, 3378);

		for (int i = 0; i < 8; i++) {
			Mob mob = MobManager.spawnMob(536, start.copy().transform(-i, 0));
			mob.setFacePosition(mob.getLocation().transform(0, -1));
			mobs.add(mob);
		}

		GameService.getTaskScheduler().schedule(new Task(4, false) {

			private int countdown;

			@Override
			protected void execute() {
				String text = DancingWomenEvent.TEXT[countdown];
				Animation animation = countdown == 6 ? new Animation(858) : countdown == 7 ? new Animation(863) : random.nextBoolean() ? new Animation(2109) : new Animation(866);

				for (Mob mob : mobs) {
					mob.speak(text);
					mob.playAnimation(animation);
				}

				if (++countdown == 8) {
					mobs.forEach(MobManager::remove);
					PartyRoomManager.cancelEvent();
					stop();
				}
			}

		});
	}
}
