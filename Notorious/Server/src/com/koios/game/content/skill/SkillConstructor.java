package com.koios.game.content.skill;

import com.koios.task.Task;

/**
 * Construct the skills base.
 *
 * @author Gabriel | Wolfs Darker
 */
public abstract class SkillConstructor extends Task {

	public SkillConstructor(int delay, boolean immediate) {
		super(delay, immediate);
	}

	/**
	 * @return if the player can walk in the skill action.
	 */
	public abstract boolean canWalk();

	/**
	 * @return if the player can teleport in the skill action.
	 */
	public abstract boolean canTeleport();

	/**
	 * @return if the player can abort the skill action.
	 */
	public abstract boolean canAbort();

	/**
	 * @return if the player need to stop the action.
	 */
	public abstract boolean mustAbort();

	/**
	 * @return if the skill action already started.
	 */
	public abstract boolean hasStarted();

	/**
	 * @return the skill's name
	 */
	public abstract String getName();

	/**
	 * Sets if the skill action must be aborted.
	 *
	 * @param abort
	 * @return
	 */
	public abstract boolean setMustAbort(boolean abort);

	/**
	 * @return if the player has the requeriments of that skill.
	 */
	public abstract boolean hasRequeriments(Object[] object);

	/**
	 * Starts the skill action.
	 *
	 * @return
	 */
	public abstract boolean start();

	/**
	 * Abort's the skill action.
	 *
	 * @return if the skill action abort was succefull.
	 */
	public abstract boolean abort();

	/**
	 * Delay to do that action again.
	 *
	 * @return
	 */
	public abstract int getActionDelay();

	/**
	 * Get the skill's variables.
	 *
	 * @return
	 */
	public abstract Object[] getObjects();

	/**
	 * Sets the skill values.
	 *
	 * @return
	 */
	public abstract Object[] setObjects(Object[] objects);

	/**
	 * Sets the amount of items to be made.
	 *
	 * @param amount
	 * @return
	 */
	public abstract int setAmount(int amount);

}
