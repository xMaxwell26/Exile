package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobDagganothRex extends Mob {

	private MobDagganothRex(Location location) {
		super(2883, location);
	}

	public MobDagganothRex(int index, Location location) {
		super(index, 2883, location);
	}

	@Override
	public MobDagganothRex copy() {
		MobDagganothRex mob = new MobDagganothRex(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		int killCount = player.increaseVariable("kc_dagannoth_rex", 1);
		player.sendMessage("Your Dagannoth Rex kill count is: <col=800000>" + killCount + "</col>.");

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
