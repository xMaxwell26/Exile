package com.koios.game.model.player.controller.impl;

import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.Controller;
import com.koios.network.out.impl.SendOptionPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.network.out.impl.SendWalkableWidget;

public class WildernessController extends Controller {

	@Override
	public void tick(Player player) {

	}

	@Override
	public boolean canTalk(Player player) {
		return true;
	}

	@Override
	public boolean canMove(Player player) {
		return true;
	}

	@Override
	public boolean canSave() {
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
	public boolean canAttackPlayer(Player attacker, Player victim) {
		return true;
	}

	@Override
	public boolean allowMultiSpells(Player player) {
		return true;
	}

	@Override
	public boolean allowPvPCombat(Player player) {
		return true;
	}

	@Override
	public void onDeath(Player player) {

	}

	@Override
	public Location getRespawnLocation(Player player) {
		return DefaultController.DEFAULT_LOCATION;
	}

	@Override
	public boolean canLogOut(Player player) {
		return true;
	}

	@Override
	public void onDisconnect(Player player) {

	}

	@Override
	public void onControllerInit(Player player) {
		int modY = player.getY() > 6400 ? player.getY() - 6400 : player.getY();
		player.wildLevel = (modY - 3520) / 8;

		if (player.wildLevel < 10) {
			player.wildLevel = 10;
		}

		player.getSession().send(new SendStringPacket("@yel@Level: " + player.wildLevel, 199));
		player.getSession().send(new SendWalkableWidget(23300));
		player.getSession().send(new SendOptionPacket(3, "Attack", false));
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public void onTeleport(Player player) {

	}

	@Override
	public boolean canTrade(Player player) {
		return false;
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return true;
	}

	@Override
	public boolean canUsePrayer(Player player) {
		return true;
	}

	@Override
	public boolean canEat(Player player) {
		return true;
	}

	@Override
	public boolean canDrink(Player player) {
		return true;
	}

	@Override
	public boolean canUseSpecialAttack(Player player) {
		return true;
	}

	@Override
	public boolean transitionOnWalk(Player player) {
		return true;
	}

	@Override
	public void onWalk(Player player) {
		int modY = player.getY() > 6400 ? player.getY() - 6400 : player.getY();
		player.wildLevel = (modY - 3520) / 8;

		if (player.wildLevel < 10) {
			player.wildLevel = 10;
		}

		player.getSession().send(new SendStringPacket("@yel@Level: " + player.wildLevel, 199));
		player.getSession().send(new SendWalkableWidget(23300));
	}

	@Override
	public String toString() {
		return "Wilderness Controller";
	}

}
