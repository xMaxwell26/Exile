package com.koios.game.model.mob.impl;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobBork extends Mob {

	public MobBork(int index, Location location) {
		super(index, 7133, location);
	}

	private MobBork(Location location) {
		super(7133, location);
	}

	@Override
	public MobBork copy() {
		MobBork mob = new MobBork(homeLocation);
		mob.internalCopy(this);
		return mob;
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

	@Override
	public void dropItems(Player player) {
		drop(ItemIdentifiers.BIG_BONES);
		super.dropItems(player);
		int killCount = player.increaseVariable("kc_bork", 1);
		player.sendMessage("Your Bork kill count is: <col=800000>" + killCount + "</col>.");
	}

}
