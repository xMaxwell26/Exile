package com.koios.game.model.item.container.bank;

import com.koios.game.model.item.Item;

/**
 * Represents an item deposited in the bank.
 *
 * @author Michael | Chex
 */
public class BankItem extends Item {

	/**
	 * The item's tab slot index in the bank.
	 */
	private int tabIndex;

	/**
	 * Initializes a new bank item.
	 *
	 * @param tabSlot The tab index the item is stored in the bank.
	 * @param itemId The item id to add.
	 * @param amount The item amount.
	 */
	public BankItem(int tabSlot, int itemId, int amount) {
		super(itemId, amount);
		setTabSlot(tabSlot);
	}

	/**
	 * Gets the tab the item is located in.
	 *
	 * @return The tab.
	 */
	public BankItem(int tab, Item item){this(tab,item.getId(),item.getCount());setRoll(item.getRoll());}

	public int getTabIndex() {
		return tabIndex;
	}

	/**
	 * Creates a copy of an existing bank object.
	 */
	@Override
	public BankItem copy() {
		BankItem result=new BankItem(tabIndex,getId(),getCount());result.setRoll(getRoll());return result;
	}

	/**
	 * Sets the item id.
	 *
	 * @param itemID The new item id.
	 */
	public void setID(int itemID) {
		transform(itemID);
	}

	/**
	 * Sets the tab the item is stored in.
	 *
	 * @param tabSlot The tab to store the item in.
	 */
	public void setTabSlot(int tabSlot) {
		tabIndex = tabSlot;
	}

	@Override
	public String toString() {
		return "BankItem[" + getId() + ", " + getCount() + ", " + tabIndex + "]";
	}

}