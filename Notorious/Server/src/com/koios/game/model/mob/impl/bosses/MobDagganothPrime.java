package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobDagganothPrime extends Mob {

	private MobDagganothPrime(Location location) {
		super(2882, location);
	}

	public MobDagganothPrime(int index, Location location) {
		super(index, 2882, location);
	}

	@Override
	public MobDagganothPrime copy() {
		MobDagganothPrime mob = new MobDagganothPrime(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		int killCount = player.increaseVariable("kc_dagannoth_prime", 1);
		player.sendMessage("Your Dagannoth Prime kill count is: <col=800000>" + killCount + "</col>.");

		drop(ItemIdentifiers.DAGANNOTH_HIDE);
		super.dropItems(player);
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.DAGANNOTH_BONES && id != ItemIdentifiers.DAGANNOTH_HIDE) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
