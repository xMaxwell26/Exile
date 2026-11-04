package com.koios.game.content.bankpins;

import java.util.Calendar;
import java.util.LinkedList;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendInterfacePacket;
import com.koios.network.out.impl.SendMoveWidgetComponentPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.NumberUtil;
import com.koios.util.TimeUtil;

/**
 * Handles the bank pin system.
 *
 * @author Ben Stulpin
 */
public class BankPinHandler {

	/**
	 * An enum containing all of the actions.
	 */
	public static enum Action {

		ENTER_BANK(0), ENTER_SETTINGS(0), SET(0), SET_SECOND(1), CHANGE(0), CHANGE_SECOND(1);

		/**
		 * The index.
		 */
		private int index;

		private Action(int index) {
			this.index = index;
		}

		/**
		 * Gets the index.
		 *
		 * @return the index.
		 */
		public int getIndex() {
			return index;
		}
	}

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The bank pin settings.
	 */
	private final Settings settings = new Settings();

	/**
	 * The entered pin string.
	 */
	private String[] enteredPin = new String[2];

	/**
	 * The pin buttons array.
	 */
	private final int[][] pinButtons = { { 58025, -1 }, { 58026, -1 }, { 58027, -1 }, { 58028, -1 }, { 58029, -1 }, { 58030, -1 }, { 58031, -1 }, { 58032, -1 }, { 58033, -1 },
			{ 58034, -1 } };

	/**
	 * The current action.
	 */
	private Action action = Action.ENTER_BANK;

	/**
	 * The current state.
	 */
	private BankPinState state = BankPinState.FIRST;

	/**
	 * Creates a bank pin class for the player.
	 *
	 * @param player the player
	 */
	public BankPinHandler(Player player) {
		this.player = player;
	}

	/**
	 * Deletes the player's bank pin.
	 */
	public void deletePin() {
		getSettings().reset();
		player.sendMessage("@dre@Your PIN has been deleted.");
		openSettings();
	}

	/**
	 * Starts the cancel queue of the pin.
	 */
	public void forgotPin() {

		/**
		 * We send the information dialogue.
		 */
		player.getDH().sendDialogues(496, 494);

		/**
		 * We get the pin cancel date.
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, getSettings().getRecoverDelay());

		/**
		 * We set the pin cancel date.
		 */
		getSettings().setRemoveDate(calendar.getTime());

		/**
		 * We set the calendar to null.
		 */
		calendar = null;
	}

	/**
	 * Gets the entered pin.
	 *
	 * @return the entered pin.
	 */
	public String getEnteredPin(int index) {
		return enteredPin[index];
	}

	/**
	 * Gets the bank pin settings.
	 *
	 * @return the bank pin settings.
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Enters a digit to the entered digits array.
	 *
	 * @param button the action button id.
	 */
	public boolean handleButtons(int button) {

		/**
		 * We make as switch of the button ids.
		 */
		switch (button) {
		case 58073: /* PIN Forgotten */
			player.getBankPinHandler().forgotPin();
			return true;

		case 58074: /* Exit */
			player.getSession().send(new SendRemoveInterfacePacket());
			player.sendMessage("Cancelled.");
			return true;

		case 58227: /* Set PIN */
			player.getBankPinHandler().openSetPin(Action.SET);
			return true;

		case 58228:
		case 58229: /* Change Recovery Delay */
			getSettings().setRecoverDelay(getSettings().getRecoverDelay() == 3 ? 7 : 3);
			player.getSession().send(new SendStringPacket(getSettings().getRecoverDelay() + " days", 15107));
			return true;

		case 58230: /* Change PIN */
			player.getDH().sendDialogues(503, 494);
			return true;

		case 58231: /* Delete PIN */
			player.getDH().sendDialogues(502, 494);
			return true;

		case 58234: /* Cancel Pending PIN */
			getSettings().reset();
			openSettings();
			return true;
		}

		/**
		 * We loop through the pin button array.
		 */
		for (int[] pinButton : pinButtons) {

			/**
			 * We check if the button equals the pin button.
			 */
			if (button != pinButton[0]) {
				continue;
			}

			final String entered = enteredPin[action.getIndex()];

			/**
			 * We set the entered pin.
			 */
			enteredPin[action.getIndex()] = (entered == null ? "" : entered) + pinButton[1];

			/**
			 * We send the "*".
			 */
			player.getSession().send(new SendStringPacket("*", 14913 + state.ordinal()));

			/**
			 * We check if the state is the fourth step.
			 */
			if (state == BankPinState.FOURTH) {

				final int attempts = getSettings().getAttempts();

				/**
				 * We clear the interface.
				 */
				reset(false);

				switch (action) {
				case ENTER_BANK:

					/**
					 * We check if the entered pin equals the player's set pin.
					 */
					if (!getEnteredPin(0).equals(getSettings().getPIN())) {
						player.getSession().send(new SendRemoveInterfacePacket());
						player.getDH().sendDialogues(attempts == 0 ? 497 : attempts == 3 ? 499 : 498, 494);
						break;
					}

					player.sendMessage("You have correctly entered your PIN.");
					getSettings().setEntered(true);
					player.getBankField().setBanking(true);
					Bank.open(player);
					break;

				case ENTER_SETTINGS:

					/**
					 * We check if the entered pin equals the player's set pin.
					 */
					if (!getEnteredPin(0).equals(getSettings().getPIN())) {
						player.getSession().send(new SendRemoveInterfacePacket());
						player.getDH().sendDialogues(attempts == 0 ? 497 : attempts == 3 ? 499 : 498, 494);
						break;
					}

					player.sendMessage("You have correctly entered your PIN.");
					getSettings().setEntered(true);
					openSettings();
					break;

				case SET:

					/**
					 * We loot the setting second screen.
					 */
					openSetPin(Action.SET_SECOND);
					break;

				case SET_SECOND:

					/**
					 * We check if the first screen entered pin matches the second.
					 */
					if (getEnteredPin(0).equals(getEnteredPin(1))) {
						player.sendMessage("@dre@Your pin will be active in " + getSettings().getRecoverDelay() + " days.");
						setPin();
						AchievementManager.performed(player, Achievement.SET_BANK_PIN);
					} else {
						player.sendMessage("The pins that you entered do not match.");
						player.getSession().send(new SendRemoveInterfacePacket());
					}
					break;

				case CHANGE:
					openChangePin(Action.CHANGE_SECOND);
					break;

				case CHANGE_SECOND:

					/**
					 * We check if the first screen entered pin matches the second.
					 */
					if (getEnteredPin(0).equals(getEnteredPin(1))) {
						player.sendMessage("@dre@Your pin has been changed.");
						setPin();
					} else {
						player.sendMessage("The pins that you entered do not match.");
					}

					openSettings();
					break;
				}

				break;
			}

			/**
			 * We set the state.
			 */
			state = BankPinState.forIndex(state.ordinal() + 1);

			/**
			 * We refresh the interface and randomize the numbers.
			 */
			refresh();
			return true;
		}

		return false;
	}

	/**
	 * Checks if the player has a pin or not.
	 */
	public boolean hasPin() {
		return !getSettings().getPIN().isEmpty();
	}

	/**
	 * Opens the change pin interface.
	 */
	public void openChangePin(Action action) {

		/**
		 * We clear the values and interface.
		 */
		reset(action == Action.CHANGE);

		/**
		 * We refresh the numbers.
		 */
		player.getBankPinHandler().refresh();

		/**
		 * We set the instruction line.
		 */
		player.getSession()
				.send(new SendStringPacket(action == Action.CHANGE ? "Please choose a new FOUR DIGIT PIN using the buttons below." : "Now please enter that number again.", 14920));

		/**
		 * We set the action.
		 */
		setAction(action);

		/**
		 * We check if the action is to set (first screen).
		 */
		if (action == Action.CHANGE) {

			/**
			 * We move the I don't know it option out of sight.
			 */
			player.getSession().send(new SendMoveWidgetComponentPacket(14922, 0, 10));

			/**
			 * We move the exit option down.
			 */
			player.getSession().send(new SendMoveWidgetComponentPacket(14921, 250, 250));

			/**
			 * We loot the enter pin interface.
			 */
			player.getInterfaceState().setCurrentInterface(7424);
			player.getSession().send(new SendInterfacePacket(7424));
			player.getBankField().setBanking(false);
		}
	}

	/**
	 * We loot the enter pin interface.
	 */
	public void openEnterPin(Action action) {

		if (settings.getRemoveDate() != null) {
			if (settings.getRemoveDate().before(Calendar.getInstance().getTime())) {
				settings.reset();
			}
		}

		if (settings.getActiveDate() != null) {
			if (settings.getActiveDate().before(Calendar.getInstance().getTime())) {
				settings.setActiveDate(null);
			}
		}

		/**
		 * We check if the player does not have a bank pin and is trying to loot their pin settings.
		 */
		if ((hasPin() && getSettings().getActiveDate() != null || !hasPin() && getSettings().getActiveDate() == null) && action == Action.ENTER_SETTINGS) {
			openSettings();
			return;
		}

		/**
		 * We clear the values and interface.
		 */
		player.getBankPinHandler().reset(true);

		/**
		 * We refresh the numbers.
		 */
		player.getBankPinHandler().refresh();

		/**
		 * We set the action.
		 */
		player.getBankPinHandler().setAction(action);

		/**
		 * We loot the interface.
		 */
		player.getInterfaceState().setCurrentInterface(7424);
		player.getBankField().setBanking(false);
		player.getSession().send(new SendInterfacePacket(7424));
	}

	/**
	 * Opens the set pin interface.
	 *
	 * @param action the action.
	 */
	private void openSetPin(Action action) {
		/**
		 * We clear the values and interface.
		 */
		reset(action == Action.SET);

		/**
		 * We refresh the numbers.
		 */
		player.getBankPinHandler().refresh();

		/**
		 * We set the instruction line.
		 */
		player.getSession()
				.send(new SendStringPacket(action == Action.SET ? "Please choose a new FOUR DIGIT PIN using the buttons below." : "Now please enter that number again.", 14920));

		/**
		 * We set the action.
		 */
		player.getBankPinHandler().setAction(action);

		/**
		 * We check if the action is to set (first screen).
		 */
		if (action == Action.SET) {
			/**
			 * We move the I don't know it option out of sight.
			 */
			player.getSession().send(new SendMoveWidgetComponentPacket(14922, 0, 10));

			/**
			 * We move the exit option down.
			 */
			player.getSession().send(new SendMoveWidgetComponentPacket(14921, 250, 250));

			/**
			 * We loot the enter pin interface.
			 */
			player.getInterfaceState().setCurrentInterface(7424);
			player.getBankField().setBanking(false);
			player.getSession().send(new SendInterfacePacket(7424));
		}
	}

	/**
	 * We loot the settings interface.
	 */
	public void openSettings() {

		/**
		 * The frames to be cleared.
		 */
		int[] frameIds = { 15171, 15176, 15075, 15076, 15078, 15079, 15080, 15082 };

		/**
		 * We loop through the frame ids.
		 */
		for (int id : frameIds) {
			/**
			 * We move the components.
			 */
			player.getSession().send(new SendMoveWidgetComponentPacket(id, 0, 0));
		}

		/**
		 * We check if the player has a pin.
		 */
		if (hasPin()) {
			if (getSettings().getActiveDate() != null) {
				frameIds = new int[] { 15171, 15176, 15075, 15076, 15078, 15079, 15080 };
			} else {
				frameIds = new int[] { 15171, 15176, 15075, 15076, 15082 };
			}
		} else {
			frameIds = new int[] { 15171, 15176, 15078, 15079, 15080, 15082 };
		}

		/**
		 * We loop through the frame ids.
		 */
		for (int id : frameIds) {
			/**
			 * We move the components.
			 */
			player.getSession().send(new SendMoveWidgetComponentPacket(id, 250, 250));
		}

		/**
		 * The PIN status message.
		 */
		String status = "No PIN set";

		/**
		 * We check if they have set a pin.
		 */
		if (!getSettings().getPIN().isEmpty()) {
			/**
			 * We set the status message depending on if the pin is active or not.
			 */
			status = getSettings().getActiveDate() != null ? "PIN pending" : "PIN set";
		}

		/**
		 * We set the PIN status line.
		 */
		player.getSession().send(new SendStringPacket(status, 15105));

		/**
		 * We set the recovery delay line.
		 */
		player.getSession().send(new SendStringPacket(getSettings().getRecoverDelay() + " days", 15107));

		/**
		 * We loot the interface.
		 */
		player.getInterfaceState().setCurrentInterface(14924);
		player.getBankField().setBanking(false);
		player.getSession().send(new SendInterfacePacket(14924));
	}

	/**
	 * Refreshes the interface.
	 */
	public void refresh() {
		/**
		 * The numbers arraylist.
		 */
		final LinkedList<Integer> numbers = new LinkedList<>();

		/**
		 * Sets the instruction message.
		 */
		player.getSession().send(new SendStringPacket(state.getMessage(), 15313));

		if (getSettings().getRemoveDate() != null) {
			int time = TimeUtil.daysBetween(Calendar.getInstance().getTime(), getSettings().getRemoveDate());

			if (time == 0) {
				time = 1;
			}

			player.getSession().send(new SendStringPacket("Your BANK PIN will be deleted in another " + time + " days.", 14923));
		}

		/**
		 * We loop ten times.
		 */
		for (int i = 0; i < 10; i++) {
			/**
			 * We add the number to the arraylist.
			 */
			numbers.add(i);
		}

		/**
		 * We loop through the pin button array.
		 */
		for (int i = 0; i < pinButtons.length; i++) {
			/**
			 * We get a random index.
			 */
			final int index = NumberUtil.inclusiveRandom(numbers.size() - 1);

			/**
			 * We set the pin button's number.
			 */
			pinButtons[i][1] = numbers.get(index);

			/**
			 * We send the number button string.
			 */
			player.getSession().send(new SendStringPacket(Integer.toString(pinButtons[i][1]), 14883 + i));

			/**
			 * We remove the number from the arraylist.
			 */
			numbers.remove(index);
		}
	}

	/**
	 * We clear the values and interface.
	 */
	public void reset(boolean reset) {
		state = BankPinState.FIRST;

		for (int i = 0; i < 5; i++) {
			player.getSession().send(new SendStringPacket("?", 14913 + i));
		}

		player.getSession().send(new SendStringPacket("Bank of Exile", 14923));

		if (reset) {
			enteredPin = new String[2];
			player.getSession().send(new SendStringPacket("Please enter your FOUR DIGIT PIN using the buttons below.", 14920));
			player.getSession().send(new SendMoveWidgetComponentPacket(14921, 0, 0));
			player.getSession().send(new SendMoveWidgetComponentPacket(14922, 0, 0));
		}
	}

	/**
	 * Sets the action.
	 *
	 * @param action the action.
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * Sets the pin.
	 */
	private void setPin() {
		/**
		 * We get the pin cancel date.
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, getSettings().getRecoverDelay());

		/**
		 * We set the pin cancel date.
		 */
		getSettings().setActiveDate(calendar.getTime());

		/**
		 * We set the pin.
		 */
		getSettings().setPin(getEnteredPin(0));
		AchievementManager.performed(player, Achievement.SET_BANK_PIN);

		/**
		 * We set the entered flag to true.
		 */
		getSettings().setEntered(true);

		/**
		 * We loot the settings interface.
		 */
		openSettings();
	}

}
