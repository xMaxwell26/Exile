package com.koios.game.model.player.controller.impl;

import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingStatus;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.Controller;
import com.koios.network.out.impl.SendOptionPacket;
import com.koios.network.out.impl.SendWalkableWidget;

public class DuelArenaController extends Controller {

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
		return true;
	}

	@Override
	public boolean canAttackNPC(Player player) {
		return false;
	}

	@Override
	public boolean canAttackPlayer(Player attacker, Player victim) {

		return true;
	}

	@Override
	public boolean allowMultiSpells(Player player) {

		return false;
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

		return null;
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
		player.getSession().send(new SendWalkableWidget(201));

		if (player.getDuel().getStatus() == DuelingStatus.DUELING) {
			player.getSession().send(new SendOptionPacket(3, "Attack", false));
		} else {
			player.getSession().send(new SendOptionPacket(3, "Challenge", false));
		}
	}

	@Override
	public boolean canTeleport(Player player) {
		if (player.inArena()) {
			player.sendMessage("You cannot teleport out of the arena.");
			return false;
		}
		return true;
	}

	@Override
	public void onTeleport(Player player) {

	}

	@Override
	public boolean canTrade(Player player) {
		return !player.inArena();
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

	}

	@Override
	public String toString() {

		return "Duel Arena Controller";
	}

}
