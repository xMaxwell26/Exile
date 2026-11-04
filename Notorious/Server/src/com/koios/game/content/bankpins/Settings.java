package com.koios.game.content.bankpins;

import java.util.Calendar;
import java.util.Date;

/**
 * The bank pin settings.
 *
 * @author Ben Stulpin
 */
public class Settings {

	// TODO Make use of java.time from Java 8 over deprecated Calendar/Date.

	/**
	 * The pin.
	 */
	private String pin = "";

	/**
	 * The recover delay.
	 */
	private int recoverDelay = 7;

	/**
	 * The active date.
	 */
	private Date activeDate;

	/**
	 * The remove date.
	 */
	private Date removeDate;

	/**
	 * The entered flag.
	 */
	private boolean entered;

	/**
	 * The attempts count.
	 */
	private int attempts;

	/**
	 * The disable time.
	 */
	private int disableTime;

	/**
	 * Gets the active date.
	 *
	 * @return the active date.
	 */
	public Date getActiveDate() {
		if (activeDate != null) {
			if (activeDate.before(Calendar.getInstance().getTime())) {
				setActiveDate(null);
			}
		}

		return activeDate;
	}

	/**
	 * Gets the attempts count.
	 *
	 * @return the attempts count.
	 */
	public int getAttempts() {
		return attempts;
	}

	/**
	 * Gets the disable time.
	 *
	 * @return the disable time.
	 */
	public int getDisableTime() {
		return disableTime;
	}

	/**
	 * Gets the pin.
	 *
	 * @return the pin.
	 */
	public String getPIN() {
		return pin;
	}

	/**
	 * Gets the recover delay.
	 *
	 * @return the recover delay.
	 */
	public int getRecoverDelay() {
		return recoverDelay;
	}

	/**
	 * Gets the remove date.
	 *
	 * @return the remove date.
	 */
	public Date getRemoveDate() {
		if (removeDate != null) {
			if (removeDate.before(Calendar.getInstance().getTime())) {
				setRemoveDate(null);
			}
		}

		return removeDate;
	}

	/**
	 * Gets the entered flag.
	 *
	 * @return the entered flag.
	 */
	public boolean hasEntered() {
		return entered;
	}

	/**
	 * Resets the pin settings.
	 */
	public void reset() {
		activeDate = null;
		removeDate = null;
		pin = "";
	}

	/**
	 * Sets he active date.
	 *
	 * @param activeDate the active date.
	 */
	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	/**
	 * Sets the attempts count.
	 *
	 * @param attempts the attempts count.
	 * @param disableTime the disable time.
	 */
	public void setAttempts(int attempts, int disableTime) {
		this.attempts = attempts;
		this.disableTime = disableTime;
	}

	/**
	 * Sets the disable time.
	 *
	 * @param disableTime the disable time.
	 */
	public void setDisableTime(int disableTime) {
		this.disableTime = disableTime;
	}

	/**
	 * Sets the entered flag.
	 *
	 * @param entered the entered flag.
	 */
	public void setEntered(boolean entered) {
		this.entered = entered;
		setAttempts(0, 0);
	}

	/**
	 * Sets the pin.
	 *
	 * @param pin the pin.
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * Sets the recovery delay.
	 *
	 * @param recoverDelay the recovery delay.
	 */
	public void setRecoverDelay(int recoverDelay) {
		this.recoverDelay = recoverDelay;
	}

	/**
	 * Gets the remove date.
	 *
	 * @param removeDate the remove date.
	 */
	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}

}
