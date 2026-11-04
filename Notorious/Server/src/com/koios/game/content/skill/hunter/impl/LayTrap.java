package com.koios.game.content.skill.hunter.impl;

import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.Trap;
import com.koios.game.content.skill.hunter.TrapProcessor;
import com.koios.game.map.MapRegion;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

/**
 * @author J
 */
@SuppressWarnings("rawtypes")
public final class LayTrap extends EntityTask {

	public static enum State {

		START, FINISH

	}

	private final int slot;
	private final Hunter.TrapType trapType;
	private final Player player;
	private State state = State.START;

	@SuppressWarnings("unchecked")
	public LayTrap(Player player, int slot, Hunter.TrapType trapType) {
		super(1, player, false);
		this.slot = slot;
		this.trapType = trapType;
		this.player = player;
	}

	@Override
	protected void execute() {
		int code = executeState();

		if (code == 0) {
			stop();
			player.setStopPacket(false);
		}
	}

	public int executeState() {
		switch (state) {
		case START: {
			player.getMovementHandler().reset();
			player.setStopPacket(true);

			if (player.getInventory().get(slot).getId() != trapType.getItem().getId()) {
				return 0;
			}

			int trapCount = TrapProcessor.getTrapCount(player);

			if (trapCount >= player.getSkill().getLevelForExperience(Skill.HUNTER) / 20 + 1) {
				player.sendMessage("You can only lay a maximum of " + trapCount + " with your hunter level.");
				return 0;
			}

			Location position = player.getLocation();
			boolean object = GameService.getObjectHandler().doesWorldObjectExist(position);

			if (object) {
				player.sendMessage("You cannot lay a trap here.");
				return 0;
			}

			player.sendMessage("You begin setting up the trap.");
			player.getInventory().remove(trapType.getItem());
			GroundItemManager.spawnNew(player, trapType.getItem().getId(), player.getX(), player.getY(), trapType.getItem().getCount());
			player.playAnimation(new Animation(trapType.getLayAnimation()));
			setState(State.FINISH);
			return 3;
		}

		case FINISH: {
			Location position = player.getLocation();
			boolean object = GameService.getObjectHandler().doesWorldObjectExist(position);

			if (object) {
				return 0;
			}

			GameService.getGroundItemManager().removeGroundItem(player, trapType.getItem().getId(), position.getX(), position.getY(), position.getZ(), false);
			WorldObject wo = GameService.getObjectHandler().createAnObject(trapType.getObject(), position.getX(), position.getY(), 0);

			TrapProcessor.addTrap(player, new Trap(player, trapType, wo));
			final boolean walk;

			if (MapRegion.getRegion(position.getX(), position.getY()).canMove(position.getX(), position.getY(), player.getZ(), 3)) {
				walk = true;
			} else {
				walk = false;
			}

			player.getMovementHandler().walkTo(walk == true ? -1 : 1, 0);
			return 0;
		}
		}

		throw new AssertionError();
	}

	public void setState(State state) {
		this.state = state;
	}

}
