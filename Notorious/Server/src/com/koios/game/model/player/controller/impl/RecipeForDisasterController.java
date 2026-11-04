package com.koios.game.model.player.controller.impl;

import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.controller.Controller;

public class RecipeForDisasterController extends Controller {

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
	
		return true;
	}

	@Override
	public boolean canAttackPlayer(Player attacker, Player victim) {
	
		return false;
	}

	@Override
	public boolean allowMultiSpells(Player player) {
	
		return false;
	}

	@Override
	public boolean allowPvPCombat(Player player) {
	
		return false;
	}

	@Override
	public void onDeath(Player player) {
	
		player.setAttribute("culi", 0);
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
	
		return false;
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
	
		return null;
	}

}
