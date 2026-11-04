package com.koios.game.model.item.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.koios.Constants;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;

/**
 * A container holds a group of items.
 *
 * @author Graham Edgecombe
 */
public class Container {

	/**
	 * The type of container.
	 *
	 * @author Graham Edgecombe
	 */
	public enum Type {

		/**
		 * A standard container such as inventory.
		 */
		STANDARD,

		/**
		 * A container which always stacks, e.g. the bank, regardless of the item.
		 */
		ALWAYS_STACK

	}

	/**
	 * Transfers an item from one container to another.
	 *
	 * @param from The container to transfer from.
	 * @param to The container to transfer to.
	 * @param fromSlot The slot in the original container.
	 * @param id The item id.
	 * @return A flag indicating if the transfer was successful.
	 */
	public static boolean transfer(Container from, Container to, int fromSlot, int id) {
		Item fromItem = from.get(fromSlot);

		if (fromItem == null || fromItem.getId() != id) {
			return false;
		}
		
		if (to.freeSlots() < 1) {
			return false;
		}

		if (to.add(fromItem)) {
			from.set(fromSlot, null);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The capacity of this container.
	 */
	protected int capacity;

	/**
	 * The items in this container.
	 */
	protected Item[] items;

	/**
	 * A list of listeners.
	 */
	protected List<ContainerListener> listeners = new LinkedList<>();

	/**
	 * The container type.
	 */
	protected Type type;

	/**
	 * Firing events flag.
	 */
	protected boolean firingEvents = true;

	/**
	 * The weight of this container.
	 */
	protected double weight;

	/**
	 * Creates the container with the specified capacity.
	 *
	 * @param type The type of this container.
	 * @param capacity The capacity of this container.
	 */
	public Container(Type type, int capacity) {
		this.type = type;
		this.capacity = capacity;
		items = new Item[capacity];
		weight = 0.0;
	}

	/**
	 * Attempts to add an item into the next free slot.
	 *
	 * @param item The item.
	 * @return <code>true</code> if the item was added, <code>false</code> if not.
	 */
	public boolean add(Item item) {
        if(item!=null && item.hasRoll() && freeSlots()<1)return false;
		if (Objects.isNull(item)) {
			return false;
		}

		if (!item.hasRoll() && (ItemDefinition.forId(item.getId()) != null && ItemDefinition.forId(item.getId()).isStackable() || type.equals(Type.ALWAYS_STACK))) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].canStackWith(item)) {
					long totalCount = (long) item.getCount() + items[i].getCount();

					if (totalCount >= Constants.MAX_COUNT) {
						if (getNextFreeSlot() == -1) {
							return false;
						}
						return false;
					}

					item.transform(items[i].getId());
					item.setCount(item.getCount() + items[i].getCount());
					set(i, item.copy());
					return true;
				}
			}

			int slot = getNextFreeSlot();

			if (slot == -1) {
				return false;
			} else {
				set(slot, item.copy());
				return true;
			}
		} else {
			int slots = freeSlots();
			
			if (item.getCount() > slots) {
				//TODO: Find out if this will cause problems
				item.setCount(slots);
			}
			
			if (item.getCount() <= 0) {
				return false;
			}

			if (slots >= item.getCount()) {
				boolean b = firingEvents;
				firingEvents = false;

				try {
					int temp = item.getCount();
					item.setCount(1);

					for (int i = 0; i < temp; i++) {
						set(getNextFreeSlot(), item.copy());
					}

					return true;
				} finally {
					firingEvents = b;

					if (firingEvents) {
						fireItemsChanged();
					}
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * Adds a listener.
	 *
	 * @param listener The listener to add.
	 */
	public void addListener(ContainerListener listener) {
		listeners.add(listener);
		listener.onFullItemChange(this);
	}

	/**
	 * Clears this container.
	 */
	public void clear() {
		items = new Item[items.length];

		if (firingEvents) {
			fireItemsChanged();
		}
	}

	/**
	 * Checks if the container contains the specified item.
	 *
	 * @param id The item id.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean contains(int id) {
		return getSlotById(id) != -1;
	}

	public boolean contains(int... id) {
		return Arrays.stream(id).allMatch(i -> contains(i));
	}

	public boolean contains(Item... items) {
		for (Item item : items) {
			if (!contains(item)) {
				return false;
			}
		}

		return true;
	}

	public boolean contains(Item given) {
		int totalAmount = 0;

		for (Item item : items) {
			if (item != null && item.getId() == given.getId()) {
				totalAmount += item.getCount();
			}
		}

		return totalAmount >= given.getCount();
	}

	/**
	 * Checks if the container contains an item, based on the requirements of the given filter.
	 *
	 * @param filter The {@link Predicate} used for validation.
	 * @return Whether or not any of the items pass validation.
	 */
	public boolean contains(Predicate<Item> filter) {
		for (Item item : items) {
			if (Objects.nonNull(item) && filter.test(item)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Fires an item changed event.
	 *
	 * @param slot The slot that changed.
	 */
	public void fireItemChanged(int slot) {
		for (ContainerListener listener : listeners) {
			listener.onSingleItemChange(this, slot);
		}
	}

	/**
	 * Fires an items changed event.
	 */
	public void fireItemsChanged() {
		for (ContainerListener listener : listeners) {
			listener.onFullItemChange(this);
		}
	}

	/**
	 * Fires an items changed event.
	 *
	 * @param slots The slots that changed.
	 */
	public void fireItemsChanged(int[] slots) {
		for (ContainerListener listener : listeners) {
			listener.onItemGroupChange(this, slots);
		}
	}

	/**
	 * Gets the number of free slots.
	 *
	 * @return The number of free slots.
	 */
	public int freeSlots() {
		return capacity - size();
	}

	/**
	 * Gets an item.
	 *
	 * @param index The position in the container.
	 * @return The item.
	 */
	public Item get(int index) {
		if (index >= items.length || index < 0) {
			return null;
		}

		return items[index];
	}

	/**
	 * Gets an item by id.
	 *
	 * @param id The id.
	 * @return The item, or <code>null</code> if it could not be found.
	 */
	public Item getById(int id) {
		for (Item item : items) {
			if (item == null) {
				continue;
			}

			if (item.getId() == id) {
				return item;
			}
		}

		return null;
	}

	/**
	 * Gets the capacity of this container.
	 *
	 * @return The capacity of this container.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Gets the total amount of an item, including the items in stacks.
	 *
	 * @param id The id.
	 * @return The amount.
	 */
	public int getCount(int id) {
		int total = 0;

		for (Item item : items) {
			if (item != null) {
				if (item.getId() == id && total + item.getCount() <= Constants.MAX_COUNT) {
					total += item.getCount();
				}
			}
		}

		if (total < 0 || total >= Constants.MAX_COUNT) {
			return Constants.MAX_COUNT;
		} else {
			return total;
		}
	}

	/**
	 * Gets the total amount of an item, including the items in stacks.
	 *
	 * @param id The id.
	 * @return The amount.
	 */
	public int getCount(int id, int slot) {
		return items[slot].getCount();
	}

	/**
	 * Gets the total amount of an item, including the items in stacks.
	 *
	 * @return The amount.
	 */
	public int getCount(Item i2) {

		if (i2 == null) {
			return 0;
		}

		int total = 0;

		for (Item item : items) {
			if (item != null) {
				if (item.getId() == i2.getId() && total + item.getCount() <= Constants.MAX_COUNT) {
					total += item.getCount();
				}
			}
		}

		if (total < 0 || total >= Constants.MAX_COUNT) {
			return Constants.MAX_COUNT;
		} else {
			return total;
		}
	}

	/**
	 * Gets the total amount of an item, including the items in stacks.
	 *
	 * @return The amount.
	 */
	public int getCount(Item i2, int slot) {
		int total = 0;

		if (ItemDefinition.forId(i2.getId()).isStackable()) {
			total = items[slot].getCount();
		} else {
			total = getCount(i2);
		}

		return total;
	}

	public int getItemAmount(int itemId) {
		int amount = 0;

		for (Item item : items) {
			if (item != null) {
				if (item.getId() == itemId) {
					amount += item.getCount();
				}
			}
		}

		return amount;
	}

	/**
	 * Gets the listeners of this container.
	 *
	 * @return The listeners of this container.
	 */
	public Collection<ContainerListener> getListeners() {
		return Collections.unmodifiableCollection(listeners);
	}

	/**
	 * Gets the next free slot.
	 *
	 * @return The slot, or <code>-1</code> if there are no available slots.
	 */
	public int getNextFreeSlot() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null || items[i].getId() == -1) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Gets a slot by id.
	 *
	 * @param id The id.
	 * @return The slot, or <code>-1</code> if it could not be found.
	 */
	public int getSlotById(int id) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				continue;
			}

			if (items[i].getId() == id) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Gets a slot by id.
	 *
	 * @param id The id.
	 * @return The slot, or <code>-1</code> if it could not be found.
	 */
	public int getSlotById(Item id) {
		for(int i=0;i<items.length;i++)if(items[i]!=null && items[i].sameIdentity(id))return i;
        return -1;
	}

	/**
	 * Gets the valid items in the container,
	 *
	 * @return items in a list format.
	 */
	public List<Item> getValidItems() {
		List<Item> items = new ArrayList<>();

		for (Item item : this.items) {
			if (item != null && item.getId() > 0) {
				items.add(item);
			}
		}

		return items;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return (int) Math.floor(weight);
	}

	public boolean hasItem(Item item) {
		int amount = getItemAmount(item.getId());

		if (item.getCount() <= 0 && contains(item.getId())) {
			return true;
		}

		if (contains(item.getId()) && amount >= item.getCount()) {
			return true;
		}

		return false;
	}

	public boolean itemsEqual(Item[] items) {
		if (items.length != this.items.length) {
			return false;
		}

		return Arrays.equals(this.items, items);
	}

	public boolean hasItems(Item[] items) {

		boolean[] doesHave = new boolean[items.length];

		for (int i = 0; i < items.length; i++) {
			int amount = getItemAmount(items[i].getId());

			if (items[i].getCount() <= 0 && contains(items[i].getId())) {
				doesHave[i] = true;
			}

			if (contains(items[i].getId()) && amount >= items[i].getCount()) {
				doesHave[i] = true;
			}
		}

		for (int i = 0; i < doesHave.length; i++) {
			if (!doesHave[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if there is room in the inventory for an item.
	 *
	 * @param item The item.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasRoomFor(Item item) {
		if (!item.hasRoll() && (ItemDefinition.forId(item.getId()).isStackable() || type.equals(Type.ALWAYS_STACK))) {
			for (Item item2 : items) {
				if (item2 != null && item2.canStackWith(item)) {
					int totalCount = item.getCount() + item2.getCount();

					if (totalCount >= Constants.MAX_COUNT || totalCount < 1) {
						return false;
					}

					return true;
				}
			}

			int slot = getNextFreeSlot();
			return slot != -1;
		} else {
			int slots = freeSlots();
			return slots >= item.getCount();
		}
	}

	/**
	 * Inserts an item.
	 *
	 * @param fromSlot The old slot.
	 * @param toSlot The new slot.
	 */
	public void insert(int fromSlot, int toSlot) {
		// we clear the item in the from slot
		Item from = items[fromSlot];

		if (from == null) {
			return;
		}

		items[fromSlot] = null;

		// find which direction to shift in
		if (fromSlot > toSlot) {
			int shiftFrom = toSlot;
			int shiftTo = fromSlot;

			for (int i = toSlot + 1; i < fromSlot; i++) {
				if (items[i] == null) {
					shiftTo = i;
					break;
				}
			}

			Item[] slice = new Item[shiftTo - shiftFrom];
			System.arraycopy(items, shiftFrom, slice, 0, slice.length);
			System.arraycopy(slice, 0, items, shiftFrom + 1, slice.length);
		} else {
			int sliceStart = fromSlot + 1;

			for (int i = toSlot - 1; i >= sliceStart; i--) {
				if (items[i] == null) {
					sliceStart = i;
					break;
				}
			}

			Item[] slice = new Item[toSlot - sliceStart + 1];
			System.arraycopy(items, sliceStart, slice, 0, slice.length);
			System.arraycopy(slice, 0, items, sliceStart - 1, slice.length);
		}

		// now fill in the target slot
		items[toSlot] = from;

		if (firingEvents) {
			fireItemsChanged();
		}
	}

	/**
	 * Checks the firing events flag.
	 *
	 * @return <code>true</code> if events are fired, <code>false</code> if not.
	 */
	public boolean isFiringEvents() {
		return firingEvents;
	}

	/**
	 * Checks if a slot is free.
	 *
	 * @param slot The slot.
	 * @return <code>true</code> if an item is not present, <code>false</code> otherwise.
	 */
	public boolean isSlotFree(int slot) {
		return items[slot] == null;
	}

	/**
	 * Checks if a slot is used.
	 *
	 * @param slot The slot.
	 * @return <code>true</code> if an item is present, <code>false</code> otherwise.
	 */
	public boolean isSlotUsed(int slot) {
		return items[slot] != null;
	}

	public void remove(int itemId, int amount) {
		remove(new Item(itemId, amount));
	}

	/**
	 * Removes an item.
	 *
	 * @param preferredSlot The preferred slot.
	 * @param item The item to remove.
	 * @return The number of items removed.
	 */
	public int remove(int preferredSlot, Item item) {
		if (Objects.isNull(item)) {
			return 0;
		}

		int removed = 0;

		if (!item.hasRoll() && (ItemDefinition.forId(item.getId()).isStackable() || type.equals(Type.ALWAYS_STACK))) {
			int slot = getSlotById(item);

			if (slot == -1) {
				return removed;
			}

			Item stack = get(slot);

			if (stack.getCount() > item.getCount()) {
				removed = item.getCount();
				item.transform(stack.getId());
				item.setCount(stack.getCount() - item.getCount());
				set(slot, item);
			} else {
				removed = stack.getCount();
				set(slot, null);
			}
		} else {
			for (int i = 0; i < item.getCount(); i++) {
				int slot = getSlotById(item);

				if (slot == -1) {
					continue;
				}

				if (i == 0 && preferredSlot != -1) {
					Item inSlot = get(preferredSlot);

					if (inSlot != null && inSlot.sameIdentity(item)) {
						slot = preferredSlot;
					}
				}

				removed++;
				set(slot, null);
			}
		}

		return removed;
	}

	/**
	 * Removes an item.
	 *
	 * @param item The item to remove.
	 * @return The number of items removed.
	 */
	public int remove(Item item) {
		return remove(-1, item);
	}

	public void remove(Item[] items) {
		for (Item item : items) {
			remove(-1, item);
		}
	}

	/**
	 * Removes all listeners.
	 */
	public void removeAllListeners() {
		listeners.clear();
	}

	public boolean removeItem(Item given, int slot) {
		if (given.getId() < 0 || given.getId() > Constants.MAX_ITEMS) {
			return false;
		}

		Item toModify = get(slot);

		if (Objects.nonNull(toModify) && given.getId() == toModify.getId()) {
			int amountToRemove = given.getCount();

			if (amountToRemove >= toModify.getCount()) {
				items[slot] = null;
			} else {
				toModify.reduceCount(amountToRemove);
			}

			if (isFiringEvents()) {
				fireItemChanged(slot);
			}
		}

		return false;
	}

	/**
	 * Removes a listener.
	 *
	 * @param listener The listener to remove.
	 */
	public void removeListener(ContainerListener listener) {
		listeners.remove(listener);
	}

	public boolean replace(int original, int newItem) {
		return replace(getSlotById(original), original, newItem);
	}

	public boolean replace(int slot, int original, int newItem) {
		if (slot == -1) {
			return false;
		}

		if (newItem == -1) {
			items[slot] = null;
			fireItemsChanged();
		}

		if (items[slot] != null) {
			items[slot].transform(newItem);
			items[slot].setCount(1);
			fireItemsChanged();
		}

		return true;
	}

	/**
	 * Sets an item.
	 *
	 * @param index The position in the container.
	 * @param item The item.
	 */
	public void set(int index, Item item) {
		items[index] = item;

		if (firingEvents) {
			fireItemChanged(index);
		}
	}

	/**
	 * Sets the firing events flag.
	 *
	 * @param firingEvents The flag.
	 */
	public void setFiringEvents(boolean firingEvents) {
		this.firingEvents = firingEvents;
	}

	public void setItems(Item[] items) {
		this.items = items;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Shifts all items to the top left of the container leaving no gaps.
	 */
	public void shift() {
		Item[] old = items;
		items = new Item[capacity];
		int newIndex = 0;

		for (int i = 0; i < items.length; i++) {
			if (old[i] != null) {
				items[newIndex] = old[i];
				newIndex++;
			}
		}

		if (firingEvents) {
			fireItemsChanged();
		}
	}

	/**
	 * Gets the size of this container.
	 *
	 * @return The size of this container.
	 */
	public int size() {
		int size = 0;

		for (Item item : items) {
			if (item != null && item.getId() >= 0) {
				size++;
			}
		}

		return size;
	}

	/**
	 * Swaps two items.
	 *
	 * @param fromSlot From slot.
	 * @param toSlot To slot.
	 */
	public void swap(int fromSlot, int toSlot) {
		Item temp = get(fromSlot);
		boolean b = firingEvents;
		firingEvents = false;

		try {
			set(fromSlot, get(toSlot));
			set(toSlot, temp);

			if (b) {
				fireItemsChanged(new int[] { fromSlot, toSlot });
			}
		} finally {
			firingEvents = b;
		}
	}

	/**
	 * Returns an array representing this container.
	 *
	 * @return The array.
	 */
	public Item[] toArray() {
		return items;
	}

}
