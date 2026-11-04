package com.koios.game.content.following;

import com.koios.Constants;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.util.CombatDistance;
import com.koios.game.map.path.SimplePathWalker;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;

public class MobFollowing extends Following {

	private final Mob mob;

	public MobFollowing(Mob mob) {
		super(mob);
		this.mob = mob;
	}

	@Override
	public void process() {
		if (mob.isFamiliar() && mob.summoner != null) {
			Player mobSummoner = mob.summoner.toPlayer();

			if (mobSummoner == null) {
				reset();
				return;
			}

			if (following != null && following.getName().equals(mobSummoner.getName()) && mobSummoner.inBank()) {
				return;
			}

			if (following == null || following.isDead() || Location.getManhattanDistance(mob.getLocation(), mobSummoner.getLocation()) > 10) {

				if (mob.getCombat().getTarget() != null) {
					Combat.resetCombat(mob);
				}

				setFollow(mobSummoner, FollowType.DEFAULT);
			}

		}

		if (following != null) {
			follow();
		}
	}

	@Override
	public boolean pause() {
		if (Constants.withinBlock(following.getX(), following.getY(), following.getSize(), mob.getX(), mob.getY(), mob.getSize())) {
			return false;
		}

		if (type == FollowType.COMBAT) {
			return CombatDistance.inCombatDistance(mob, following, mob.getCombat().getAttackType(), false);
		}

		int x = entity.getLocation().getX();
		int y = entity.getLocation().getY();
		int x2 = following.getLocation().getX();
		int y2 = following.getLocation().getY();

		Location[] a = Constants.getBorder(x, y, entity.getSize());
		Location[] b = Constants.getBorder(x2, y2, following.getSize());

		for (Location i : a) {
			for (Location k : b) {
				if (Math.abs(i.getX() - k.getX()) < 2 && Math.abs(i.getY() - k.getY()) < 2) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean outOfRange() {
		if (ignoreDistance || mob.isFromMinigame()) {
			return false;
		}

		if (mob.getCombat().getTarget() != null && mob.walkingHome) {
			return true;
		}

		return Location.getManhattanDistance(mob.getLocation(), following.getLocation()) > 16;
	}

	@Override
	public void findPath(Location location) {
		if (nextDirection != -1) {
			nextDirection = -1;
		}

		SimplePathWalker.walkToNextTile(mob, location);
	}

	@Override
	public void onCannotReach() {
		if (mob.isFamiliar()) {
			if (mob.summoner == null) {
				reset();
				return;
			}

			Player summoner = mob.summoner.toPlayer();
			Location l = Constants.getClearAdjacentLocation(summoner.getLocation(), mob.getSize());

			if (l != null) {
				mob.teleport(l);
				mob.setFaceIndex(summoner.getIndex(), true);
				mob.getFollowing().setFollow(summoner, FollowType.DEFAULT);
				mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

				if (mob instanceof MobFamiliar) {
					mob.playGraphics(new Graphic(mob.getSize() <= 1 ? 1314 : 1315));
				}
			}
		} else {
			reset();
		}
	}

}
