package com.koios.game.content.partyroom;

import java.util.Objects;

/**
 * The enumerated type whose elements represent the activities that can be hosted by the Falador party room.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public enum LeverOption {

	DANCING_WOMEN(500_000, new DancingWomenEvent()), DANCING_DRUNKS(250_000, new DancingDrunksEvent()), DROP_PARTY(1_000_000, new DropPartyEvent());

	/**
	 * The amount of coins that must be paid to activate this option.
	 */
	private final int amount;

	/**
	 * The activity executed when this option is activated.
	 */
	private final Runnable activity;

	/**
	 * Creates a new {@link LeverOption}.
	 *
	 * @param amount the amount of coins that must be paid to activate this option.
	 * @param activity the activity executed when this option is activated.
	 */
	private LeverOption(int amount, Runnable activity) {
		this.amount = amount;
		this.activity = Objects.requireNonNull(activity);
	}

	/**
	 * Gets the amount of coins that must be paid to activate this option.
	 *
	 * @return the amount of coins.
	 */
	public final int getAmount() {
		return amount;
	}

	/**
	 * Gets the activity executed when this option is activated.
	 *
	 * @return the activity executed.
	 */
	public final Runnable getActivity() {
		return activity;
	}

}
