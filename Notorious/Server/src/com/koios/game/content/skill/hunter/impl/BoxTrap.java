package com.koios.game.content.skill.hunter.impl;

import com.koios.GameService;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.Trap;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.task.impl.MobDeathTask;

/**
 * @author Omicron
 */
public final class BoxTrap extends MobAction<BoxTrap.State> {

	public static enum State {

		START, ATTEMPT, CAPTURE

	}

	private static WorldObject showNewObject(Location trapPos, int objectId) {

		// for(Player player : GameService.getWorld().getPlayers()) {
		// if(player == null) continue;
		// Position pos = player.getPosition();
		// if(pos.distanceTo(trapPos) <= 60)

		return GameService.getObjectHandler().placeObject(new WorldObject(objectId, trapPos.getX(), trapPos.getY(), 0, 0, 10, 0, true));
		// }
		// return null;
	}

	private final Trap trap;
	private final Mob mob;

	public BoxTrap(Mob npc, Trap trap) {
		super(npc, State.START);
		this.trap = trap;
		mob = npc;
		mob.inTask = true;
		mob.getMovementHandler().setStopMovement(true);
		mob.getMovementHandler().reset();
	}

	@Override
	protected int executeState(State state) {

		switch (state) {
		case START: {
			if (!trap.isBound()) {
				return 0;
			}

			mob.playAnimation(new Animation(5185));// animation
			trap.setCapturing();
			setState(State.ATTEMPT);
			return 1;
		}

		case ATTEMPT: {
			if (!trap.isCapturing()) {
				return 0;
			}

			Location position = new Location(mob.getX(), mob.getY(), mob.getZ());

			if (Hunter.successfulTrap(mob.capturable.getLevelRequired(), trap.getLevel())) {
				showNewObject(position, 19195);
				mob.actionTimer = 25;
				GameService.getTaskScheduler().schedule(new MobDeathTask(mob, 2));
				setState(State.CAPTURE);
				return 2;
			} else {
				trap.removeObject();
				trap.setFailed();
				trap.setObject(showNewObject(position, 19192));
				return 0;
			}
		}

		case CAPTURE: {
			trap.removeObject();
			trap.setObject(showNewObject(Location.create(mob.getX(), mob.getY(), mob.getZ()), 19189));
			trap.setCaptured(mob.capturable);
			return 0;
		}
		}

		throw new AssertionError();
	}

	@Override
	public void onStop() {
		mob.trap = null;
		mob.inTask = false;
	}

}