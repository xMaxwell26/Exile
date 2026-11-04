package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobChaosElemental extends Mob {

	public MobChaosElemental(int index, Location location) {
		super(index, 3200, location);
	}

	private MobChaosElemental(Location location) {
		super(3200, location);
	}

	@Override
	public MobChaosElemental copy() {
		MobChaosElemental mob = new MobChaosElemental(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_chaos_elemental", 1);
		player.sendMessage("Your Chaos Elemental kill count is: <col=800000>" + NumberUtil.format(killCount) + "</col>.");
		super.dropItems(player);
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.ASHES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
