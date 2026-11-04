package com.koios.game.content.skill.hunter;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;

/**
 * @author J
 */
public final class Trap {

	private static enum State {

		REGISTERING, ACTIVE, BOUND, CAPTURING, CAPTURED, FAILED, DISMANTLING, REMOVED

	}

	private final long encodedName;
	private final int level;
	private final Hunter.TrapType trapType;
	private WorldObject object;
	private State state;
	private int ticks;
	private Hunter.Capturable captured;

	public Trap(Player player, Hunter.TrapType trapType, WorldObject object) {
		encodedName = player.getUsernameHash();
		level = player.getSkill().getLevel(Skill.HUNTER);
		this.trapType = trapType;
		this.object = object;
		state = State.REGISTERING;
	}

	public boolean equals(WorldObject object) {
		return this.object.equals(object);
	}

	public Hunter.Capturable getCaptured() {
		return captured;
	}

	public int getLevel() {
		return level;
	}

	public Location getPosition() {
		return object.getPosition();
	}

	public int getRequiredSpace(Player player) {
		if (captured == null) {
			return 1;
		}

		switch (captured.identifier()) {
		case Hunter.Capturable.SNARE: {
			if (player.getInventory().hasItem(((Hunter.SnareTrapped) captured).getFeathers())) {
				return 2;
			}

			return 3;
		}

		case Hunter.Capturable.NET: {
			return 3;
		}

		case Hunter.Capturable.BOX: {
			Hunter.BoxTrapped boxTrapped = (Hunter.BoxTrapped) captured;

			if (ItemDefinition.forId(boxTrapped.getItem().getId()).isStackable() && player.getInventory().hasItem(boxTrapped.getItem())) {
				return 1;
			}

			return 2;
		}
		}

		throw new AssertionError();
	}

	public Hunter.TrapType getTrapType() {
		return trapType;
	}

	public boolean isActive() {
		return state == State.ACTIVE;
	}

	public boolean isBound() {
		return state == State.BOUND;
	}

	public boolean isCapturing() {
		return state == State.CAPTURING;
	}

	public boolean isDismantling() {
		return state == State.DISMANTLING;
	}

	public boolean isRemoved() {
		return state == State.REMOVED;
	}

	public boolean isValid(Hunter.Capturable capturable) {
		if (state != State.ACTIVE) {
			return false;
		}

		if (trapType != capturable.getTrapType()) {
			return false;
		}

		if (level < capturable.getLevelRequired()) {
			return false;
		}

		return true;
	}

	public void process() {
		if (state == State.REMOVED) {
			return;
		}

		ticks++;

		switch (ticks) {
		case 20: {
			if (state == State.REGISTERING) {
				state = State.ACTIVE;
			}
			return;
		}

		case 95: {
			state = State.DISMANTLING;
			return;
		}

		case 100: {
			state = State.REMOVED;
			removeObject();
			Optional<Player> optionalPlayer = GameService.getWorld().getByUsernameHash(encodedName);

			if (optionalPlayer.isPresent()) {
				Player player = optionalPlayer.get();
				GroundItemManager.spawnNew(player, trapType.getItem().getId(), object.getPosition().getX(), object.getPosition().getY(), trapType.getItem().getCount());
				player.sendMessage("The trap you set has been dismantled.");
			}
		}
		}
	}

	public void removeObject() {
		GameService.getObjectHandler().createAnObject(-1, object.getX(), object.getY(), object.getFace());
	}

	public void setBound() {
		state = State.BOUND;
	}

	public void setCaptured(Hunter.Capturable captured) {
		state = State.CAPTURED;
		this.captured = captured;
	}

	public void setCapturing() {
		state = State.CAPTURING;
	}

	public void setDismantling() {
		state = State.DISMANTLING;
	}

	public void setFailed() {
		state = State.FAILED;
	}

	public void setObject(WorldObject object) {
		this.object = object;
	}

	public void setRemoved() {
		state = State.REMOVED;
	}

}
