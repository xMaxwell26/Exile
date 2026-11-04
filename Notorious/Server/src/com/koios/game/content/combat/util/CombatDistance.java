package com.koios.game.content.combat.util;

import com.koios.Constants;
import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.ranged.RangedData;
import com.koios.game.map.path.StraightPathFinder;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * Handles the combat distance checks.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CombatDistance {

	/**
	 * Checks if the attacker is in distance to attack.
	 *
	 * @param attacker the one attacking
	 * @param target the one who will receive damage
	 * @param type the attack type
	 * @return if is in distance to attack
	 */
	public static boolean inCombatDistance(EntityCharacter attacker, EntityCharacter target, AttackType type, boolean noMovement) {

		if (target == null || attacker == null || type == null) {
			return false;
		}

		if (attacker.getLocation().equals(target.getLocation())) {
			return false;
		}

		int dist = getDistance(attacker, target, type);

		if (!noMovement && !attacker.isMob() && !target.isMob() && attacker.getMovementHandler().moving()) {
			dist += 3;
		}

		if (!attacker.inDistance(target, dist)) {
			return false;
		}

		boolean b = false;

		for (Location i : Constants.getEdges(attacker.getLocation().getX(), attacker.getLocation().getY(), attacker.getSize())) {

			if (type == AttackType.MAGIC || type == AttackType.RANGE) {
				if (StraightPathFinder.isProjectilePathClear(i, target.getLocation()) || StraightPathFinder.isProjectilePathClear(target.getLocation(), i)) {
					b = true;
					break;
				}
			} else {
				if (StraightPathFinder.isInteractionPathClear(i, target.getLocation()) || StraightPathFinder.isInteractionPathClear(target.getLocation(), i)) {
					b = true;
					break;
				}
			}
		}
		if (!b) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the attacker is in a certain distance from the target
	 *
	 * @param attacker
	 * @param target
	 * @param req
	 * @return
	 */
	public static boolean isWithinDistance(EntityCharacter attacker, EntityCharacter target, int req) {
		if (attacker == null || target == null) {
			return false;
		}
		if (!attacker.isMob() && !target.isMob() && Location.getManhattanDistance(target.getLocation(), attacker.getLocation()) == 0) {
			return false;
		}

		int x = attacker.getLocation().getX();
		int y = attacker.getLocation().getY();
		int x2 = target.getLocation().getX();
		int y2 = target.getLocation().getY();

		if (attacker.getLocation().withinBlock(target.getLocation(), target.getSize())) {
			return true;
		}

		if (Location.getManhattanDistance(x, y, x2, y2) <= req) {
			return true;
		}

		Location[] a = Constants.getBorder(attacker.getLocation(), attacker.getSize());
		Location[] b = Constants.getBorder(target.getLocation(), target.getSize());

		for (Location i : a) {
			for (Location k : b) {
				if (Location.getManhattanDistance(i, k) <= req) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the attacker is in distance to make damage to the target
	 *
	 * @param attacker
	 * @param target target of the check
	 * @param type attack type ( melee, magic, range, etc )
	 * @return
	 */
	public static int getDistance(EntityCharacter attacker, EntityCharacter target, AttackType type) {

		int distance_required = 0;

		if (attacker.isMob() && type != null) {
			switch (type) {
			case RANGE:
			case MAGIC:
			case DRAGON_FIRE:
			case DRAGON_FIRE_BALL:
				distance_required = 11;
				break;
			default:
				break;
			}
		} else {
			Player player = attacker.toPlayer();
			if (type == null) {
				distance_required = 1;
			} else {
				switch (type) {
				case RANGE:
					distance_required = (RangedData.canBeThrow(player) ? 4 : 6) + (player.getCombat().getAttackStyle() == AttackStyle.CONTROLLED ? 2 : 0);
					break;
				case MAGIC:
					distance_required = 11;
					break;
				default:
					distance_required = 1;
					break;
				}
			}
		}

		distance_required = distance_required <= 0 ? 1 : distance_required;

		return distance_required;
	}

	/**
	 * Calculates the delay to apply the entity's damage
	 *
	 * @param entity
	 * @param victim
	 * @return the delay
	 */
	public static int calculateDamageDelay(Location entity, Location victim) {
		switch (Location.getDistance(entity, victim)) {
		case 0:
		case 1:
		case 2:
			return 2;
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			return 3;
		default:
			return 4;
		}
	}
}