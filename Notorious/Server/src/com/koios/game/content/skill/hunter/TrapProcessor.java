package com.koios.game.content.skill.hunter;

import java.util.Iterator;
import java.util.List;

import org.qatide.util.FixedArrayList;
import org.qatide.util.LongHashMap;
import org.qatide.util.LongMap;

import com.koios.game.model.Location;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;

/**
 * @author J
 */
public final class TrapProcessor {

	private static final LongMap<List<Trap>> traps = new LongHashMap<>();

	public static void addTrap(Player player, Trap trap) {
		List<Trap> playerTraps = TrapProcessor.traps.get(player.getUsernameHash());

		if (playerTraps == null) {
			playerTraps = new FixedArrayList<>(5);

			if (TrapProcessor.traps == null) {
				return;
			}

			TrapProcessor.traps.put(player.getUsernameHash(), playerTraps);
		}

		if (trap == null) {
			return;
		}

		playerTraps.add(trap);
	}

	public static Trap getClosest(Location position, Hunter.Capturable capturable) {

		Trap closest = null;
		int closestDistance = 16;

		for (List<Trap> traps2 : TrapProcessor.traps.values()) {
			for (Trap trap : traps2) {
				if (!trap.isValid(capturable)) {
					continue;
				}

				int distance = Location.getManhattanDistance(position.getX(), position.getY(), trap.getPosition().getX(), trap.getPosition().getY());

				if (distance > 5) {
					continue;
				}

				if (distance < closestDistance) {
					closest = trap;
					closestDistance = distance;
				}
			}
		}

		return closest;
	}

	public static Trap getTrap(Player player, WorldObject object) {
		List<Trap> playerTraps = TrapProcessor.traps.get(player.getUsernameHash());

		if (playerTraps == null) {
			return null;
		}

		for (Trap trap : playerTraps) {
			if (trap.equals(object)) {
				return trap;
			}
		}

		return null;
	}

	public static int getTrapCount(Player player) {
		List<Trap> playerTraps = TrapProcessor.traps.get(player.getUsernameHash());

		if (playerTraps == null) {
			return 0;
		}

		return playerTraps.size();
	}

	public static void process() {
		Iterator<LongMap.Entry<List<Trap>>> trap$ = TrapProcessor.traps.entrySet().iterator();

		while (trap$.hasNext()) {
			LongMap.Entry<List<Trap>> entry = trap$.next();
			List<Trap> traps = entry.getValue();
			Iterator<Trap> iterator = traps.iterator();

			while (iterator.hasNext()) {
				Trap trap = iterator.next();
				trap.process();

				if (trap.isRemoved()) {
					iterator.remove();
				}
			}

			if (traps.isEmpty()) {
				trap$.remove();
			}
		}
	}

}