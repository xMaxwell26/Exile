package com.koios.game.model.movement;

import com.koios.Constants;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.map.MapRegion;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.ControllerManager;
import com.koios.network.out.impl.SendMapRegionPacket;
import com.koios.network.out.impl.SendMessagePacket;
import com.koios.network.out.impl.SendPlanePacket;

public class PlayerMovementHandler extends MovementHandler {

	private final Player player;

	public PlayerMovementHandler(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public boolean canMoveTo(int direction) {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		int z = player.getLocation().getZ();
		if (player.isSuperAdministrator()) {
			return true;
		}
		return MapRegion.getRegion(x, y).canMove(x, y, z, direction);
	}

	@Override
	public boolean canMoveTo(int x, int y, int size, int direction) {
		int z = player.getLocation().getZ();
		if (player.isSuperAdministrator()) {
			return true;
		}
		return MapRegion.getRegion(x, y).canMove(x, y, z, direction);
	}

	@Override
	public void process() {
		if (player.isDead() || !player.getEF().canWalk(player, false) || player.isTeleportActive()
				|| player.getDuel().isDueling() && player.getDuel().isRuleActive(DuelingRules.NO_MOVEMENT)) {
			reset();
		} else {
			Point walkPoint = waypoints.poll();

			Point runPoint = null;

			if (walkPoint != null && walkPoint.getDirection() != -1) {

				// if (forceMove || canMoveTo(walkPoint.getDirection())) {
				if (player.getRunEnergy().isResting()) {
					player.getRunEnergy().toggleResting();
				}

				player.getMovementHandler().getLastLocation().setAs(player.getLocation());
				player.getLocation().move(Constants.DIR[walkPoint.getDirection()][0], Constants.DIR[walkPoint.getDirection()][1]);
				player.setDirection1(walkPoint.getDirection());
				ControllerManager.setControllerOnWalk(player);
				ControllerManager.updateControllerOnWalk(player);
				// }
			}

			if (player.getRunEnergy().isRunning()) {
				runPoint = waypoints.poll();
			}

			if (runPoint != null) {
				// if (forceMove || canMoveTo(runPoint.getDirection())) {
				if (player.getRunEnergy().canRun()) {
					player.getMovementHandler().getLastLocation().setAs(player.getLocation());
					player.getLocation().move(Constants.DIR[runPoint.getDirection()][0], Constants.DIR[runPoint.getDirection()][1]);
					player.setDirection2(runPoint.getDirection());
					player.getRunEnergy().onRun();
				} else {
					player.getSession().send(new SendMessagePacket("You don't have enough run energy to do that."));
					player.getRunEnergy().reset();
				}
				ControllerManager.setControllerOnWalk(player);
				ControllerManager.updateControllerOnWalk(player);
				// }
			}

			int deltaX = player.getLocation().getX() - player.getCurrentRegion().getRegionX() * 8;
			int deltaY = player.getLocation().getY() - player.getCurrentRegion().getRegionY() * 8;

			if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
				player.getSession().send(new SendPlanePacket());
				player.getSession().send(new SendMapRegionPacket(player));
			}

			if (forceMove && waypoints.size() == 0) {
				forceMove = false;
			}
		}
	}

}
