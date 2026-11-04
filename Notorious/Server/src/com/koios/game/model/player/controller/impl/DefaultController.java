package com.koios.game.model.player.controller.impl;

import com.koios.Constants;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.Controller;
import com.koios.network.out.impl.SendOptionPacket;
import com.koios.network.out.impl.SendWalkableWidget;

public class DefaultController extends Controller {

	/**
	 * The default location to respawn too
	 */
	public static final Location DEFAULT_LOCATION = new Location(Constants.RESPAWN_X, Constants.RESPAWN_Y);

	@Override
	public boolean canTalk(Player player) {
		return true;
	}

	@Override
	public boolean canMove(Player p) {
		return true;
	}

	@Override
	public boolean canClick(Player player) {
		return true;
	}

	@Override
	public boolean isSafe() {
		return false;
	}

	@Override
	public boolean canAttackNPC(Player player) {
		return true;
	}

	@Override
	public boolean canAttackPlayer(Player p, Player p2) {
		return false;
	}

	@Override
	public void onDisconnect(Player p) {
	}

	@Override
	public String toString() {
		return "DEFAULT";
	}

	@Override
	public boolean canLogOut(Player player) {
		return true;
	}

	@Override
	public void tick(Player player) {
		if (player.getEquipment().get(3) != null && player.getEquipment().get(3).getId() == 4566) {
			player.getSession().send(new SendOptionPacket(3, "Whack", false));
		} else {
			player.getSession().send(new SendOptionPacket(3, "null", false));
		}
	}

	@Override
	public void onControllerInit(Player player) {
		if (player.getEquipment().get(3) != null && player.getEquipment().get(3).getId() == 4566) {
			player.getSession().send(new SendOptionPacket(3, "Whack", false));
		} else {
			player.getSession().send(new SendOptionPacket(3, "null", false));
		}
		player.getSession().send(new SendOptionPacket(4, "Trade With", false));
		player.getSession().send(new SendOptionPacket(5, "Follow", false));
		player.getSession().send(new SendWalkableWidget(-1));
	}

	@Override
	public void onDeath(Player p) {
	}

	@Override
	public Location getRespawnLocation(Player player) {
		return DEFAULT_LOCATION;
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public boolean canTrade(Player player) {
		return true;
	}

	@Override
	public boolean canEquip(Player p, int id, int slot) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player p) {
		return true;
	}

	@Override
	public boolean canEat(Player p) {
		return true;
	}

	@Override
	public boolean canDrink(Player p) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player p) {
		return true;
	}

	@Override
	public boolean transitionOnWalk(Player p) {
		return true;
	}

	@Override
	public void onTeleport(Player p) {
	}

	@Override
	public boolean allowMultiSpells(Player player) {
		return true;
	}

	@Override
	public boolean allowPvPCombat(Player player) {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public void onWalk(Player player) {
		if (player.getEquipment().get(3) != null && player.getEquipment().get(3).getId() == 4566) {
			player.getSession().send(new SendOptionPacket(3, "Whack", false));
		} else {
			player.getSession().send(new SendOptionPacket(3, "null", false));
		}
	}
}
