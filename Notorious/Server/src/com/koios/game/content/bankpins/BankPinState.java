package com.koios.game.content.bankpins;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum containing all of the states.
 *
 * @author Ben Stulpin
 */
public enum BankPinState {

	FIRST("First click the FIRST digit."), SECOND("Now click the SECOND digit."), THIRD("Time for the THIRD digit."), FOURTH("Finally, the FOURTH digit.");

	/**
	 * Gets the state from the ordinal.
	 *
	 * @param index the state ordinal.
	 */
	public static BankPinState forIndex(int index) {
		return states.get(index);
	}

	/**
	 * The message.
	 */
	private String message;

	/**
	 * A map containing all of the states.
	 */
	private static final Map<Integer, BankPinState> states = new HashMap<>();

	static {
		for (BankPinState state : BankPinState.values()) {
			states.put(state.ordinal(), state);
		}
	}

	private BankPinState(String message) {
		this.message = message;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
