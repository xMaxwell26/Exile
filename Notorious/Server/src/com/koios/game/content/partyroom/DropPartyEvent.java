package com.koios.game.content.partyroom;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.task.Task;

/**
 * The activity that handles the dancing women party room event.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class DropPartyEvent implements Runnable {

	@Override
	public void run() {
		GameService.getTaskScheduler().schedule(new Task(2, false) {

			@Override
			protected void execute() {
				int slot = 0;

				for (Item item : PartyRoomManager.CONTAINER.toArray()) {
					if (item == null) {
						continue;
					}

					createBalloon(PartyRoomManager.LOCATIONS[(int) (Math.random() * PartyRoomManager.LOCATIONS.length)].random(), item, slot);
					createBalloon(PartyRoomManager.LOCATIONS[(int) (Math.random() * PartyRoomManager.LOCATIONS.length)].random(), null, slot);
					slot++;
				}

				if (PartyRoomManager.CONTAINER.size() == 0) {
					stop();
					PartyRoomManager.cancelEvent();
					return;
				}
			}

		});
	}

	/**
	 * Creates the balloon that will be dropped.
	 *
	 * @param location the location to create it on.
	 * @param item the item to create it with.
	 * @param slot the current slot.
	 */
	private static void createBalloon(Location location, Item item, int slot) {
		if (GameService.getObjectHandler().doesWorldObjectExist(location)) {
			return;
		}

		GameService.getObjectHandler().addObject(new BalloonObject(location, item));

		if (item != null) {
			PartyRoomManager.CONTAINER.set(slot, null);
		}
	}

}
