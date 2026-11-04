package com.koios.game.model.item;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Entity;
import com.koios.game.model.Location;

/**
 * An entity on the ground that represents a single or stack of item(s) (all of which have the same type).
 */
public class GroundItemEntity extends Entity {

	private final int amount;
 private Item payload;
	int hideTicks;
	private final int id;
	private final long owner;
	int removeTicks;

	public GroundItemEntity(int id, int x, int y, int z, int amount, int hideTicks, long owner) {
		this.id = id;
		getLocation().setAs(new Location(x, y, z));
		this.amount = amount;
		this.hideTicks = hideTicks;
		this.owner = owner;
	}

	public GroundItemEntity(Item item, int x,int y,int z,int hideTicks,long owner){this(item.getId(),x,y,z,item.getCount(),hideTicks,owner);payload=item.copy();}
 public Item getItem(){return payload==null?new Item(id,amount):payload.copy();}

 public int getAmount() {
		return amount;
	}

	public int getId() {
		return id;
	}

	@Override
	public int getIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return ItemDefinition.forId(id).getName();
	}

	public long getOwner() {
		return owner;
	}

}
