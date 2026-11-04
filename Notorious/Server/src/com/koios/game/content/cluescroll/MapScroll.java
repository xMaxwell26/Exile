package com.koios.game.content.cluescroll;

import java.util.function.Predicate;

import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * A clue scroll showing a map for the location of the treasure.
 *
 * @author Michael | Chex
 */
public class MapScroll extends ClueScroll {

	private final int interfaceId;

	/**
	 * Constructs a new Map clue scroll.
	 *
	 * @param item - The clue scroll item.
	 * @param interfaceId - The interface id for the clue scroll.
	 * @param digLocation - The dig location to complete the clue scroll.
	 * @param difficulty - The clue scroll difficulty.
	 */
	public MapScroll(int item, int interfaceId, Predicate<Location> digLocation, Difficulty difficulty) {
		super(item, digLocation, difficulty);
		this.interfaceId = interfaceId;
	}

	@Override
	public void attemptCompletion(Player player) {
		if (player.getInventory().contains(getItem())) {
			reward(player, "You've found");
		}
	}

	@Override
	public void open(Player player) {
		player.getSession().send(new SendRemoveInterfacePacket());
		player.getInterfaceState().openInterface(interfaceId);
	}

}
