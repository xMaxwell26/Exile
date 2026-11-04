package com.koios.game.model.item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handles global drops which respawn after set amount of time when taken
 *
 * @author Stuart <RogueX>
 */
public class GlobalDropsHandler {

	/**
	 * Holds each drops data
	 *
	 * @author Stuart
	 */
	private static class GlobalDrop {

		/**
		 * item amount
		 */
		private final int amount;

		/**
		 * item id
		 */
		private final int id;

		/**
		 * has the item been taken
		 */
		private boolean taken = false;

		/**
		 * Time it was taken at
		 */
		private long takenAt;

		/**
		 * cord x
		 */
		private final int x;

		/**
		 * cord y
		 */
		private final int y;

		/**
		 * Sets the dropExtraordinary arguments
		 *
		 * @param id item id
		 * @param amount item amount
		 * @param x cord x
		 * @param y cord y
		 */
		private GlobalDrop(int id, int amount, int x, int y) {
			this.id = id;
			this.amount = amount;
			this.x = x;
			this.y = y;
		}

		/**
		 * get the item amount
		 *
		 * @return
		 */
		public int getAmount() {
			return amount;
		}

		/**
		 * get the item id
		 *
		 * @return
		 */
		public int getId() {
			return id;
		}

		/**
		 * get the time it was taken at
		 *
		 * @return
		 */
		public long getTakenAt() {
			return takenAt;
		}

		/**
		 * get cord x
		 *
		 * @return
		 */
		public int getX() {
			return x;
		}

		/**
		 * get cord x
		 *
		 * @return
		 */
		public int getY() {
			return y;
		}

		/**
		 * has the dropExtraordinary already been taken?
		 *
		 * @return
		 */
		public boolean isTaken() {
			return taken;
		}

		/**
		 * set if or not the dropExtraordinary has been taken
		 *
		 * @param taken true yes false no
		 */
		public void setTaken(boolean taken) {
			this.taken = taken;
		}

		/**
		 * set the time it was picked up
		 *
		 * @param takenAt
		 */
		public void setTakenAt(long takenAt) {
			this.takenAt = takenAt;
		}

	}

	/**
	 * holds all the objects
	 */
	private static List<GlobalDrop> globalDrops = new ArrayList<>();

	/**
	 * time in seconds it takes for the item to respawn
	 */
	private static final int TIME_TO_RESPAWN = 60;

	/**
	 * See if a dropExtraordinary exists at the given place
	 *
	 * @param id item id
	 * @param x x cord
	 * @param y y cord
	 * @return
	 */
	private static GlobalDrop get(int id, int x, int y) {
		for (GlobalDrop drop : globalDrops) {
			if (drop.getId() == id && drop.getX() == x && drop.getY() == y) {
				return drop;
			}
		}

		return null;
	}

	/**
	 * loads the items
	 */
	public static void init() {
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/cfg/globaldrops.txt"))) {
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}

				String[] args = line.split(":");
				globalDrops.add(new GlobalDrop(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("Loaded " + globalDrops.size() + " global drops.");

		GameService.getTaskScheduler().schedule(new Task() {

			@Override
			public void execute() {
				for (GlobalDrop drop : globalDrops) {
					if (drop.isTaken()) {
						if (System.currentTimeMillis() - drop.getTakenAt() >= TIME_TO_RESPAWN * 1000) {
							drop.setTaken(false);

							for (Player player : GameService.getWorld().getPlayers()) {
								if (player.distanceToPoint(drop.getX(), drop.getY()) <= 60) {
									player.getItems().createGroundItem(Item.of(drop.getId(), drop.getAmount()), Location.create(drop.getX(), drop.getY()));
								}
							}
						}
					}
				}
			}

		});
	}

	/**
	 * Loads all the items when a player changes region
	 */
	public static void load(Player player) {
		globalDrops.stream().filter(drop -> !drop.isTaken()).forEach(drop -> {
			if (player.distanceToPoint(drop.getX(), drop.getY()) <= 60) {
				player.getItems().createGroundItem(Item.of(drop.getId(), drop.getAmount()), Location.create(drop.getX(), drop.getY()));
			}
		});
	}

	/**
	 * Pick up an item at the given location
	 *
	 * @param id item id
	 * @param x cord x
	 * @param y cord y
	 */
	public static void take(Player player, int id, int x, int y) {
		GlobalDrop drop = get(id, x, y);

		if (drop == null) {
			return;
		}

		if (drop.isTaken()) {
			return;
		}

		drop.setTakenAt(System.currentTimeMillis());
		drop.setTaken(true);

		if (player.getInventory().freeSlots() > 0) {
			player.getInventory().add(new Item(drop.getId(), drop.getAmount()));
		}

		for (Player p : GameService.getWorld().getPlayers()) {
			if (p.distanceToPoint(drop.getX(), drop.getY()) <= 60) {
				p.getItems().removeGroundItem(drop.getId(), drop.getX(), drop.getY(), drop.getAmount());
			}
		}
	}

}
