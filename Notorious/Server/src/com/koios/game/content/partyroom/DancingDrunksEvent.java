package com.koios.game.content.partyroom;

import java.util.LinkedList;
import java.util.List;

import com.koios.GameService;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * The activity that handles the dancing drunks party room event.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class DancingDrunksEvent implements Runnable {

	/**
	 * The array of text that will be sent during the event.
	 */
	private static final String[] TEXT = { "We like to drink!", "No!", "We LOVE to drink!", "Do you like to drink as well?", "Only time will tell!", "We hope you're feeling well!",
			"Thanks for having us and enjoy the drinks!", "Bye!" };

	@Override
	public void run() {
		List<Mob> mobs = new LinkedList<>();
		Location start = new Location(3049, 3378);

		for (int i = 0; i < 8; i++) {
			Mob mob = MobManager.spawnMob(3222, start.copy().transform(-i, 0));
			mob.setFacePosition(mob.getLocation().transform(0, -1));
			mobs.add(mob);
		}

		GameService.getTaskScheduler().schedule(new Task(4, false) {

			private int countdown;

			@Override
			protected void execute() {
				String text = DancingDrunksEvent.TEXT[countdown];
				Animation animation = countdown == 6 ? new Animation(858) : countdown == 7 ? new Animation(863) : new Animation(866);

				for (Mob npc : mobs) {
					npc.speak(text);
					npc.playAnimation(animation);
				}

				if (++countdown == 8) {
					for (Player player : GameService.getWorld().getPlayers()) {
						if (player == null) {
							continue;
						}

						if (player.getLocation().isViewableFrom(start)) {
							for (Mob npc : mobs) {
								Location p = npc.getLocation().transform(0, 1);
								Location p2 = npc.getLocation().transform(0, -1);
								GroundItemManager.spawnNew(player, 1917, p.getX(), p.getY(), 1, true);
								GroundItemManager.spawnNew(player, 1917, p2.getX(), p2.getY(), 1, true);
							}

							break;
						}
					}

					mobs.forEach(MobManager::remove);
					PartyRoomManager.cancelEvent();
					stop();
				}
			}

		});
	}

}
