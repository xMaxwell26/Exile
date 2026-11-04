package com.koios.game.content.skill.hunter.impl;

import java.util.function.Consumer;

import com.koios.GameService;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.Trap;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendObjectPacket;
import com.koios.task.impl.MobDeathTask;

/**
 * @author Omicron
 */
public final class NetTrap extends MobAction<NetTrap.State> {

	private static class CapturedTrapFilterExecutable implements Consumer<Player> {

		private final Location position;
		private final Hunter.NetTrapped netTrapped;

		public CapturedTrapFilterExecutable(Location position, Hunter.NetTrapped netTrapped) {
			this.position = position;
			this.netTrapped = netTrapped;
		}

		@Override
		public void accept(Player player) {
			if (player.distanceToPoint(position.getX(), position.getY()) <= 60) {
				player.getSession().send(new SendObjectPacket(netTrapped.getCapturedObject(), position.getX(), position.getY(), position.getZ(), 0, 10));
			}
		}

	}

	private static class FailedTrapFilterExecutable implements Consumer<Player> {

		private final Location position;

		public FailedTrapFilterExecutable(Location position) {
			this.position = position;
		}

		@Override
		public void accept(Player player) {
			if (player.distanceToPoint(position.getX(), position.getY()) <= 60) {
				player.getSession().send(new SendObjectPacket(19661, position.getX(), position.getY(), position.getZ(), 0, 10));
			}
		}

	}

	private static class PreCapturedFilterExecutable implements Consumer<Player> {

		private final Location position;
		private final Hunter.NetTrapped snareTrapped;

		public PreCapturedFilterExecutable(Location position, Hunter.NetTrapped snareTrapped) {
			this.position = position;
			this.snareTrapped = snareTrapped;
		}

		@Override
		public void accept(Player player) {
			if (player.distanceToPoint(position.getX(), position.getY()) <= 60) {
				player.getSession().send(new SendObjectPacket(snareTrapped.getSuccessObject(), position.getX(), position.getY(), position.getZ(), 0, 10));
			}
		}

	}

	public static enum State {

		START, ANIMATION, ATTEMPT, CAPTURE

	}

	private final Trap trap;

	private final Mob mob;

	public NetTrap(Mob mob, Trap trap) {
		super(mob, State.START);
		this.trap = trap;
		this.mob = mob;
		this.mob.inTask = true;
	}

	@Override
	protected int executeState(State state) {
		switch (state) {
		case START: {
			if (!trap.isBound()) {
				return 0;
			}

			mob.setFacePosition(mob.getX(), mob.getY() + 1);
			trap.setCapturing();
			setState(State.ANIMATION);
			return 2;
		}

		case ANIMATION: {
			if (!trap.isCapturing()) {
				return 0;
			}

			// mob.performAnimation(6775);//animation
			setState(State.ATTEMPT);
			return 1;
		}

		case ATTEMPT: {
			if (!trap.isCapturing()) {
				return 0;
			}

			Location position = trap.getPosition();

			if (Hunter.successfulTrap(mob.capturable.getLevelRequired(), trap.getLevel())) {
				Hunter.NetTrapped snareTrapped = (Hunter.NetTrapped) mob.capturable;
				GameService.getWorld().execute(new PreCapturedFilterExecutable(position, snareTrapped));
				mob.actionTimer = 50;
				GameService.getTaskScheduler().schedule(new MobDeathTask(mob, 2));
				setState(State.CAPTURE);
				return 2;
			} else {
				trap.removeObject();
				WorldObject object = GameService.getObjectHandler().createAnObject(19661, position.getX(), position.getY(), 0);
				GameService.getWorld().execute(new FailedTrapFilterExecutable(position));
				trap.setFailed();
				trap.setObject(object);
				return 0;
			}
		}

		case CAPTURE: {
			trap.removeObject();
			Hunter.NetTrapped netTrapped = (Hunter.NetTrapped) mob.capturable;
			Location position = trap.getPosition();
			WorldObject object = GameService.getObjectHandler().createAnObject(netTrapped.getCapturedObject(), position.getX(), position.getY(), 0);
			GameService.getWorld().execute(new CapturedTrapFilterExecutable(position, netTrapped));
			trap.setObject(object);
			trap.setCaptured(netTrapped);
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
