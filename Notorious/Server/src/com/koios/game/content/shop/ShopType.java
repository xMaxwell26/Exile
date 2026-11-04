package com.koios.game.content.shop;

/**
 * Represents the type of a Shop.
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public enum ShopType {

	/**
	 * Indicates that this Shop may only buy items, they are not resold.
	 */
	BUY_ONLY,

	/**
	 * Indicates that this Shop will only sell items from its stock.
	 */
	SELL_ONLY,

	/**
	 * Indicates the this Shop will buy and sell items from its stock, items that are not within the shop cannot be sold.
	 */
	BUY_AND_SELL,

	/**
	 * Indicates that this Shop is a general Shop and will buy and sell items freely as long as they are sellable.
	 */
	GENERAL

}