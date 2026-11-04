package com.koios.game.model.item.container;

/**
 * Listens to events from a container.
 *
 * @author Graham Edgecombe
 */
public interface ContainerListener {

	/**
	 * Called when an item is changed.
	 *
	 * @param container The container.
	 * @param slot The slot that was changed.
	 */
	public void onSingleItemChange(Container container, int slot);

	/**
	 * Called when a group of items are changed.
	 *
	 * @param container The container.
	 * @param slots The slots that were changed.
	 */
	public void onItemGroupChange(Container container, int[] slots);

	/**
	 * Called when all the items change.
	 *
	 * @param container The container.
	 */
	public void onFullItemChange(Container container);

}