package com.koios.game.content.partyroom;

import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.object.WorldObject;
import com.koios.util.NumberUtil;

/**
 * The world object extension that represents a party room balloon.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class BalloonObject extends WorldObject {

	/**
	 * The array of balloon types that will be dropped.
	 */
	private static final int[] BALLOON_TYPES = { 115, 116, 117, 118, 119, 120 };

	/**
	 * The item concealed within this balloon.
	 */
	private final Item item;

	/**
	 * Creates a new {@link BalloonObject}.
	 *
	 * @param location the position of the object.
	 * @param item the item concealed within this balloon. Not null.
	 */
	public BalloonObject(Location location, Item item) {
		super(NumberUtil.random(BALLOON_TYPES), location.getX(), location.getY(), location.getZ(), 0, 10, -1);
		this.item = item;
	}

	/**
	 * Gets the item concealed within this balloon.
	 *
	 * @return the concealed item.
	 */
	public Item getItem() {
		return item;
	}

}
