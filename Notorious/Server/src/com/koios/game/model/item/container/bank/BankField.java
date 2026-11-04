package com.koios.game.model.item.container.bank;

import java.util.Objects;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.out.impl.SendConfigPacket;

/**
 * Holds all the getters and setters for the variables used in the player's {@linkplain Bank}.
 *
 * @author Michael | Chex
 */
public class BankField {

	/**
	 * Contains the amount of slots used in each tab out of 350.
	 */
	private final int[] tabAmounts = new int[9];

	/**
	 * Checks if the bank tabs were saved out of order.
	 */
	private boolean loadError;

	/**
	 * A check to make sure the player is banking.
	 */
	private boolean isBanking;

	/**
	 * A string holding the unlock text.
	 */
	private String searchText = null;

	/**
	 * A check for withdrawing items from banks as a note.
	 */
	private boolean isWithdrawAsNote;

	/**
	 * A check if the player is using the unlock function.
	 */
	private boolean isSearching;

	/**
	 * A check for whether or not the player is inserting or swapping items.
	 */
	private boolean isInserting;

	/**
	 * The current tab opened.
	 */
	private int tabIndex;

	/**
	 * The current tab opened.
	 */
	private int loadTab;

	/**
	 * The player.
	 */
	private final Player player;

	public BankField(Player player) {
		this.player = Objects.requireNonNull(player, "player");
	}

	/**
	 * Gets the unlock text for searching the bank for items.
	 *
	 * @return The unlock text.
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * Gets the tab amount array.
	 *
	 * @return The tab array.
	 */
	public int[] getTabAmounts() {
		return tabAmounts;
	}

	public void setLoadError(boolean loadError) {
		this.loadError = loadError;
	}

	public boolean isLoadError() {
		return loadError;
	}

	public int getLoadTab() {
		return loadTab;
	}

	/**
	 * Gets the tab index for a slot id in the bank.
	 *
	 * @param slot The slot in the bank.
	 * @return The tab index.
	 */
	public int getTabForSlot(int slot) {
		int offset = 0;

		for (int index = 0; index < getTabAmounts().length; index++) {
			if (slot >= offset && slot < offset + getTabAmounts()[index]) {
				return index;
			} else if (getTabAmounts()[index] > 0) {
				offset += getTabAmounts()[index];
			}
		}

		return 0;
	}

	/**
	 * Gets the amount of used tabs in the bank.
	 *
	 * @return The amount of used tabs.
	 */
	public int getUsedTabs() {
		int tabs = 0;

		for (int amount : getTabAmounts()) {
			if (amount > 0) {
				tabs++;
			}
		}

		return tabs;
	}

	/**
	 * Calculates the used slot offset from zero to the desired tab.
	 *
	 * @param tab The tab to calculate the slot offset to.
	 * @return The slot offset.
	 */
	public int getOffset(int tab) {
		int offset = 0;

		for (int index = 0; index < getTabAmounts().length; index++) {
			if (index == tab) {
				break;
			} else if (getTabAmounts()[index] > 0) {
				offset += getTabAmounts()[index];
			}
		}

		return offset;
	}

	/**
	 * Gets the banking state.
	 *
	 * @return <code>True</code> if the player is banking.
	 */
	public boolean isBanking() {
		return isBanking;
	}

	/**
	 * Checks whether or not the bank is in withdraw as note mode.
	 *
	 * @return <code>true</code> if the bank is withdrawing items as notes.
	 */
	public boolean isWithdrawAsNote() {
		return isWithdrawAsNote;
	}

	/**
	 * Checks if the bank is currently running an item unlock.
	 *
	 * @return <code>True</code> if the bank is currently running an item unlock.
	 */
	public boolean isSearching() {
		return isSearching;
	}

	/**
	 * Gets the check if the bank is inserting or swapping items.
	 *
	 * @return <code>True</code> if the bank is inserting or swapping items.
	 */
	public boolean isInserting() {
		return isInserting;
	}

	/**
	 * Gets the index of the current tab viewed.
	 *
	 * @return The current tab viewed index.
	 */
	public int getTabIndex() {
		return tabIndex;
	}

	/**
	 * Sets the unlock text.
	 *
	 * @param searchText The unlock text.
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	/**
	 * Sets the banking state.
	 *
	 * @param isBanking Whether or not the player is banking.
	 */
	public void setBanking(boolean isBanking) {
		this.isBanking = isBanking;

		if (!isBanking) {
			setSearching(false);
		}
	}

	/**
	 * Sets whether or not the bank is in withdraw as note mode.
	 *
	 * @param isWithdrawAsNote The boolean that enables note withdrawal. <code>True</code> if the bank is withdrawing as a note.
	 */
	public void setWithdrawAsNote(boolean isWithdrawAsNote) {
		this.isWithdrawAsNote = isWithdrawAsNote;
	}

	/**
	 * Checks if the bank is running an item unlock.
	 *
	 * @param isSearching <code>True</code> if the bank is running an item unlock.
	 */
	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;

		if (!isSearching) {
			player.getSession().send(new SendConfigPacket(1010, 0));

			player.write(new GamePacketBuilder(26));

			setSearchText(null);
			player.getBank().fireItemsChanged();
		} else {
			player.getSession().send(new SendConfigPacket(1010, 1));
		}
	}

	/**
	 * Gets the check if the bank is inserting or swapping items.
	 *
	 * @param isInserting <code>True</code> if the bank is inserting, <code>false</code> if it's swapping items.
	 */
	public void setInserting(boolean isInserting) {
		this.isInserting = isInserting;
	}

	/**
	 * Sets the current tab index.
	 *
	 * @param tabIndex The current tab being viewed.
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setLoadTab(int loadTab) {
		this.loadTab = loadTab;
	}

}
