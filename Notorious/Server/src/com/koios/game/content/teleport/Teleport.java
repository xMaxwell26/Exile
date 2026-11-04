package com.koios.game.content.teleport;

import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;

/**
 * Handle the entity's Teleport. Implements Teleport Manager.
 *
 * @author Gabriel
 */
public class Teleport {

	public Teleport() {
		mode = null;
		location = null;
	}

	private final TeleportModes mode;
	private final Location location;

	public Teleport(TeleportModes mode, Location loc) {
		this.mode = mode;
		location = loc;
	}

	public Teleport(Location loc, TeleportModes mode) {
		this.mode = mode;
		location = loc;
	}

	public TeleportModes getTravelMode() {
		return mode;
	}

	public Location getLocation() {
		return location;
	}

	public static enum TeleportModes {

		MODERN_SPELL(3, 2, new Graphic(1576), new Graphic(1577), new Animation(8939), new Animation(8941)), MODERN_SPELL_2(4, 1, new Graphic(1576), new Graphic(1577),
				new Animation(714), new Animation(715)), ANCIENT_SPELL(6, 0, new Graphic(1681), null, new Animation(9599), new Animation(-1)), LUNAR_SPELL(6, 0, new Graphic(1685),
						null, new Animation(9606), new Animation(-1)), CABBAGE(6, 0, new Graphic(1731), new Graphic(1732), new Animation(9606), new Animation(-1)), JEWELRY(8, 0,
								new Graphic(1684), null, new Animation(9603),
								new Animation(-1)), TELEPORT_TAB(2, 1, null, new Graphic(678), new Animation(4069), new Animation(4071));

		private final int timer, endTimer;
		private final Graphic startGraphic, endGraphic;
		private final Animation startAnimation, endAnimation;

		private TeleportModes(int timer, int endTimer, Graphic startGraphic, Graphic endGraphic, Animation startAnimation, Animation endAnimation) {
			this.timer = timer;
			this.endTimer = endTimer;
			this.startGraphic = startGraphic;
			this.endGraphic = endGraphic;
			this.startAnimation = startAnimation;
			this.endAnimation = endAnimation;
		}

		public int getTimer() {
			return timer;
		}

		public int getEndTimer() {
			return endTimer;
		}

		public Animation getStartAnim() {
			return startAnimation;
		}

		public Animation getEndAnim() {
			return endAnimation;
		}

		public Graphic getStartGfx() {
			return startGraphic;
		}

		public Graphic getEndGfx() {
			return endGraphic;
		}

	}

	public enum TeleportPlaces {

		HOME(Location.create(3087, 3502)), RFD(Location.create(1899, 5365, 2)), VARROCK(Location.create(3216, 3424)), LUMBRIDGE(Location.create(3221, 3217)), FALADOR(
				Location.create(2964, 3380)), CAMELOT(Location.create(2756, 3479)), ARDOUGNE(Location.create(2661, 3306)), WATCHTOWER(Location.create(2549, 3113)),;

		private final Location loc;

		private TeleportPlaces(Location loc) {
			this.loc = loc;
		}

		public Location getLocation() {
			return loc;
		}

	}

}
