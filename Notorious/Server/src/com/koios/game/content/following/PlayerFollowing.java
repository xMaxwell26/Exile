package com.koios.game.content.following;

import com.koios.Constants;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.map.path.RS317PathFinding;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;

/**
 * Handles the player following.
 *
 * @author Gabriel | Wolfs Darker
 */
public class PlayerFollowing extends Following {

	private final Player player;

	public PlayerFollowing(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public boolean pause() {
		if (following == null) {
			return true;
		}

		if (type == Following.FollowType.COMBAT) {
			if (Constants.withinBlock(following.getLocation().getX(), following.getLocation().getY(), following.getSize(), player.getLocation().getX(),
					player.getLocation().getY())) {
				return false;
			}

			if (following.isMob()) {
				AttackType c = player.getCombat().getAttackType();

				if (c == AttackType.MAGIC || c == AttackType.RANGE) {
					if (following.toMob() != null && !following.toMob().withinMobWalkDistance(player)) {
						return false;
					}
				}
			}

			if (!player.getLocation().equals(following.getLocation()) && CombatDistance.inCombatDistance(player, following, player.getCombat().getAttackType(), true)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void findPath(Location location) {
		if (type == Following.FollowType.COMBAT) {
			if (player.getCombat().getAttackType() == AttackType.MELEE) {
				RS317PathFinding.findRoute(player, location.getX(), location.getY(), false, 0, 0);
			} else {
				RS317PathFinding.findRoute(player, location.getX(), location.getY(), true, 16, 16);
			}
		} else {
			RS317PathFinding.findRoute(player, location.getX(), location.getY(), true, 16, 16);
		}
	}

	@Override
	public void onCannotReach() {
		reset();

		if (type == Following.FollowType.COMBAT) {
			Combat.resetCombat(player);
		}

		player.sendMessage("I can't reach that!");
	}
}
