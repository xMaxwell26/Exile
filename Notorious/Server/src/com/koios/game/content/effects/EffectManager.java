package com.koios.game.content.effects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.game.content.effects.Effect.Duration;
import com.koios.game.content.effects.Effect.QueuePolicy;
import com.koios.game.content.effects.Effect.Walk;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;

/**
 * Manage the entity's effects list
 *
 * @author Gabriel | Wolfs Darker
 */
public class EffectManager {

	/**
	 * List of effects
	 */
	private final List<Effect> effects;

	/**
	 * Effects to be added queue
	 */

	private final Queue<Effect> queuedEffects;

	/**
	 * If busy, a new effect is starting
	 */
	private boolean queueBusy;

	/**
	 * Construct the entity's effect manager
	 */
	public EffectManager() {
		effects = new CopyOnWriteArrayList<>();
		queuedEffects = new LinkedList<>();
	}

	/**
	 * Check if the player already has that effect
	 *
	 * @throws NullPointerException if {@code name == null}.
	 */
	public boolean isEffectListed(String name) {
		return effects.stream().map(Effect::getName).anyMatch(name::equals);
	}

	/**
	 * Check if the effect's duration is over
	 *
	 * @param effect The effect
	 * @return if it is over
	 */
	public boolean isEffectOver(Effect effect) {
		return effect.getTimeLeft() <= 0;
	}

	/**
	 * Add new effects to the queue list
	 */
	public void queueEffect(Effect effect) {
		if (renewEffect(effect.getName())) {
			return;
		}

		if (effect.getQueueType() == QueuePolicy.NEVER && !queueBusy) {
			effects.add(effect);
			setQueueState(true);
			effect.start();
		} else {
			queuedEffects.add(effect);
			processNextEffect();
		}
	}

	/**
	 * Starts the next queued effect
	 */
	public void processNextEffect() {

		if (queuedEffects.isEmpty() || queueBusy) {
			return;
		}

		Effect effect = queuedEffects.poll();
		setQueueState(true);
		effects.add(effect);
		effect.start();
	}

	/**
	 * Checks if the effect exist and renew it
	 */
	public boolean renewEffect(String s) {
		for (Effect e : new LinkedList<>(effects)) {
			if (e.getName().equals(s)) {
				e.renew();
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if the queue is busy.
	 *
	 * @return if is busy
	 */
	public boolean isQueueBusy() {
		return queueBusy;
	}

	/**
	 * Set the queue state
	 *
	 * @param b new state
	 */
	public void setQueueState(boolean b) {
		queueBusy = b;
	}

	/**
	 * Get the entity's effect list
	 *
	 * @return the effects
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * Update all the entity's effects
	 */
	public void updateEffects() {
		if (effects.isEmpty() || effects.size() == 0) {
			return;
		}

		for (Iterator<Effect> it = new LinkedList<>(effects).iterator(); it.hasNext();) {
			try {
				Effect effect = it.next();

				/**
				 * Complete the effects
				 */
				if (effect.getDuration() == Duration.TEMPORARY && isEffectOver(effect)) {
					effect.end();
					it.remove();
					effects.remove(effect);
					continue;
				}
				effect.tick();
			} catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}

		processNextEffect();
	}

	/**
	 * Deserialize the player's info
	 */
	public void deserialize(Player player, String s) {
		try {
			String name = s.split(":")[0].replace(":", "");
			Effect effect = EffectList.getEffectByName(name);
			if (effect != null) {
				effect.load(player, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove all effects on death.
	 */
	public void handleDeath() {
		for (Iterator<Effect> it = new LinkedList<Effect>(effects).iterator(); it.hasNext();) {
			try {
				Effect effect = it.next();

				if (effect == null) {
					it.remove();
					effects.remove(effect);
					continue;
				}

				if (effect.getDuration() != Duration.PERMANENT) {
					effect.end();
					it.remove();
					effects.remove(effect);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}
	}

	/**
	 * Checks if any effect can stop the player from moving.
	 *
	 * @param player the player to be checked
	 * @return if the player can walk
	 */
	public boolean canWalk(EntityCharacter e, boolean message) {

		if (effects == null || effects.size() == 0) {
			return true;
		}

		for (Iterator<Effect> it = new LinkedList<>(effects).iterator(); it.hasNext();) {
			try {
				Effect effect = it.next();

				if (effect == null) {
					it.remove();
					effects.remove(effect);
					continue;
				}

				if (effect.getWalkEffect() == Walk.NON_WALKABLE) {
					if (e.isPlayer() && message) {
						e.toPlayer().sendMessage(effect.getNonWalkableMessage());
					}
					return false;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}
		return true;
	}

	/**
	 * Removes a effect from the list.
	 *
	 * @param name effect to be removed's name
	 * @return if it was successful
	 */
	public boolean removeEffect(String name) {

		if (name == null) {
			return false;
		}

		for (Effect effect : effects) {
			try {
				if (effect == null) {
					effects.remove(effect);
					continue;
				}

				if (name.equals(effect.getName()) && effect.getDuration() != Duration.PERMANENT) {
					effect.end();
					effects.remove(effect);
					return true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

}
