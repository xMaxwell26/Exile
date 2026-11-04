package com.koios.game.model.item;

import com.koios.game.def.ItemDefinition;

/**
 * Represents a single item.
 *
 * @author Graham Edgecombe
 */
public class Item {

	public static Item of(int id) {
		return new Item(id);
	}

	public static Item of(int id, int amount) {
		return new Item(id, amount);
	}

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The number of items.
	 */
	private int count;
 private com.koios.game.arpg.ItemRoll roll;
 public boolean hasRoll(){return roll!=null;}
 public com.koios.game.arpg.ItemRoll getRoll(){return roll;}
 public void setRoll(com.koios.game.arpg.ItemRoll roll){
  if(roll!=null){roll.validate();if(count!=1)throw new IllegalArgumentException("Rolled items must have quantity one");}
  this.roll=roll;
 }
 public Item copyWithCount(int amount){Item result=copy();result.setCount(amount);return result;}
 public boolean sameIdentity(Item other){
  return other!=null && id==other.id && (roll==null?other.roll==null:other.roll!=null && roll.getInstanceId().equals(other.roll.getInstanceId()));
 }
 public boolean canStackWith(Item other){return other!=null && !hasRoll() && !other.hasRoll() && id==other.id;}


	/**
	 * Creates a single item.
	 *
	 * @param id The id.
	 */
	public Item(int id) {
		this(id, 1);
	}

	/**
	 * Creates a stacked item.
	 *
	 * @param id The id.
	 * @param count The number of items.
	 * @throws IllegalArgumentException if count is negative.
	 */
	public Item(int id, int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Count cannot be negative.");
		}

		this.id = id;
		if(roll!=null && count!=1)throw new IllegalArgumentException("Rolled quantity must be one");
        this.count = count;
	}

	public Item copy() {
		Item result=new Item(id,count);result.roll=roll;return result;
	}

	@Override
	public int hashCode() {
		return 31*id+(roll==null?0:roll.getInstanceId().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Item) {
			Item that = (Item) obj;
			return sameIdentity(that);
		}

		return false;
	}

	/**
	 * Gets the count.
	 *
	 * @return The count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the definition of this item.
	 *
	 * @return The definition.
	 */
	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}

	/**
	 * Gets the item id.
	 *
	 * @return The item id.
	 */
	public int getId() {
		return id;
	}

	public void increaseCount(int addition) {
		setCount(Math.addExact(count, addition));
	}

	public void reduceCount(int reduction) {
		setCount(Math.max(count-reduction,0));
	}

	public void setCount(int count) {
        if(count<0)throw new IllegalArgumentException("Negative item quantity");
		if(roll!=null && count!=1)throw new IllegalArgumentException("Rolled quantity must be one");
        this.count = count;
	}

	@Override
	public String toString() {
		return Item.class.getName() + " [name='" + getDefinition().getName() + "', id=" + id + ", count=" + count + "]";
	}

	/**
	 * Changes the items id.
	 */
	public void transform(int id) {
		this.id = id;
	}

}
