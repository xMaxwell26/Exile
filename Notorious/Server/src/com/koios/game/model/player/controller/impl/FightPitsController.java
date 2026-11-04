package com.koios.game.model.player.controller.impl;

import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.Controller;
import com.koios.network.out.impl.SendOptionPacket;

public class FightPitsController extends Controller {

	private static final Location RESPAWN_LOCATION = new Location(2399, 5177, 0);

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

		return true;
	}

	@Override
	public boolean allowPvPCombat(Player player) {

		return true;
	}

	@Override
	public void onDeath(Player player) {
		FightPits.getInstance().PARTICIPANTS.remove(player);
		player.sendMessage("You have been rewarded a fight point for your participation.");
	}

	@Override
	public Location getRespawnLocation(Player player) {

		return RESPAWN_LOCATION;
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
		player.getSession().send(new SendOptionPacket(3, "Attack", false));
	}

	@Override
	public boolean canTeleport(Player player) {

		return false;
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

	}

	@Override
	public String toString() {

		return "Fight Pits Controller";
	}

}
