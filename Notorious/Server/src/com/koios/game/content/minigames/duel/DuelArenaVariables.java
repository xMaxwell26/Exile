package com.koios.game.content.minigames.duel;

import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingStatus;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;

/**
 * Handle the player's variables for the duel arena minigame.
 *
 * @author Gabriel | Wolfs Darker
 */
public class DuelArenaVariables {

	private final Player player;

	private String dueling_with;

	private DuelingStatus status = DuelingStatus.NONE;

	private final Container stake_inventory = new Container(Container.Type.STANDARD, DuelArenaConstants.STAKE_SIZE);

	private boolean[] rules = new boolean[22];

	private int spaceReq;

	private final int[] duel_data = new int[2];

	private boolean duelRequested;

	public DuelArenaVariables(Player player) {
		this.player = player;
	}

	/**
	 * Returns the dueler's name;
	 *
	 * @return the name
	 */
	public String getDuelingWith() {
		return dueling_with;
	}

	/**
	 * Returns the current duel status.
	 *
	 * @return the status
	 */
	public DuelingStatus getStatus() {
		return status;
	}

	/**
	 * Return the flagged and unflagged rules for the duel.
	 *
	 * @return the rules
	 */
	public boolean[] getRules() {
		return rules;
	}

	/**
	 * Returns the items that will be staked.
	 *
	 * @return the items
	 */
	public Container getStakeInventory() {
		return stake_inventory;
	}

	/**
	 * Returns the space necessary in inventory for the duel.
	 *
	 * @return the space
	 */
	public int getSpaceReq() {
		return spaceReq;
	}

	/**
	 * Clear all the flagged rules.
	 */
	public void clearRules() {
		rules = new boolean[22];
	}

	/**
	 * Sets the next dueler.
	 *
	 * @param d
	 */
	public void setDuelingWith(String d) {
		dueling_with = d;
	}

	/**
	 * Sets the new duel status.
	 *
	 * @param s
	 */
	public void setStatus(DuelingStatus s) {
		status = s;
	}

	/**
	 * Sets the flagged and unflagged rules.
	 *
	 * @param b
	 */
	public void setRules(boolean[] b) {
		rules = b;
	}

	/**
	 * Increases the space required.
	 *
	 * @param i
	 */
	public void increaseSpaceReq(int i) {
		setSpaceReq(spaceReq + i);
	}

	/**
	 * Sets the space required.
	 *
	 * @param i
	 */
	public void setSpaceReq(int i) {
		spaceReq = i;
	}

	public int[] getDuelData() {
		return duel_data;
	}

	public void setDuelRequested(boolean duelRequested) {
		this.duelRequested = duelRequested;
	}

	/**
	 * Checks if the player requested a duel to a certain player.
	 *
	 * @param name
	 * @return
	 */
	public boolean requestedDuel(String name) {
		return dueling_with == name && duelRequested;
	}

	/**
	 * Flag or unflag a certain rule.
	 *
	 * @param r
	 * @return if it was succefull.
	 */
	public boolean applyRule(DuelingRules r) {
		rules[r.ordinal()] = !rules[r.ordinal()];
		return rules[r.ordinal()];
	}

	/**
	 * Checks if a rule is flagged.
	 *
	 * @param r
	 * @return if is flagged
	 */
	public boolean isRuleActive(DuelingRules r) {
		if (r == null) {
			return false;
		}

		return rules[r.ordinal()];
	}

	/**
	 * Checks if the player is dueling.
	 *
	 * @return if is dueling
	 */
	public boolean isDueling() {
		return dueling_with != null && status != null && status != DuelingStatus.NONE;
	}

	/**
	 * Resets all the dueling variables.
	 */
	public void reset() {
		status = DuelingStatus.NONE;
		dueling_with = null;
		duelRequested = false;
		spaceReq = 0;
		rules = new boolean[22];
		stake_inventory.clear();
		player.getSession().send(new SendConfigPacket(286, 0));
	}

}
