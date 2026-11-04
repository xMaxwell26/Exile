package com.koios.game.model;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendLocationGraphic;

/**
 * Represents a single graphic request.
 *
 * @author Graham Edgecombe
 */
public class Graphic {

	public enum GraphicHeight {

		LOW(65536), MIDDLE(3276800), HIGH(6553600), ULTRA_HIGH(32768000);

		private final int height;

		private GraphicHeight(int height) {
			this.height = height;
		}

		public int getHeight() {
			return height;
		}

	}

	/**
	 * Creates an graphic with no delay.
	 *
	 * @param id The id.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id) {
		return create(id, 0);
	}

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 * @param delay The delay.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id, int delay) {
		return new Graphic(id, delay);
	}

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 * @param delay The delay.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id, int height, int delay) {
		return new Graphic(id, delay + height);
	}

	public static void createGlobalGraphic(int id, int x, int y, int height, int time) {
		for (Player p : GameService.getWorld().getPlayers()) {
			if (p.distanceToPoint(x, y) <= 25) {
				p.getSession().send(new SendLocationGraphic(id, x, y, height, time));
			}
		}
	}

	/**
	 * The id.
	 */
	private final int id;

	/**
	 * The delay.
	 */
	private final int delay;

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 */
	public Graphic(GraphicHeight height, int id) {
		this.id = id;
		delay = height.getHeight();
	}

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 */
	public Graphic(int id) {
		this.id = id;
		delay = 0;
	}

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 */
	public Graphic(int id, GraphicHeight height) {
		this.id = id;
		delay = height.getHeight();
	}

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 */
	public Graphic(int id, int height) {
		this.id = id;
		delay = height;
	}

	/**
	 * Creates a graphic.
	 *
	 * @param id The id.
	 * @param delay The delay.
	 */
	public Graphic(int id, int delay, GraphicHeight height) {
		this.id = id;
		this.delay = delay + height.getHeight();
	}

	/**
	 * Gets the delay.
	 *
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the id.
	 *
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

}
