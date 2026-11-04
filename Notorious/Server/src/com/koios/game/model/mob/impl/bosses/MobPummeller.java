package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobPummeller extends Mob {

	private MobPummeller(Location location) {
		super(10141, location);
	}

	public MobPummeller(int index, Location location) {
		super(index, 10141, location);
	}

	@Override
	public MobPummeller copy() {
		MobPummeller mob = new MobPummeller(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_ballak_pummeller", 1);
		player.sendMessage("Your Bal'lak the Pummeller kill count is: <col=800000>" + killCount + "</col>.");

		// Drop loot
		super.dropItems(player);
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.BIG_BONES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
