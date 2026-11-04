package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobKingBlackDragon extends Mob {

	public MobKingBlackDragon(int index, Location location) {
		super(index, 50, location);
	}

	@Override
	public MobKingBlackDragon copy() {
		MobKingBlackDragon mob = new MobKingBlackDragon(0, homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		int killCount = player.increaseVariable("kc_king_black_dragon", 1);
		player.sendMessage("Your King black dragon kill count is: <col=800000>" + killCount + "</col>.");
		super.dropItems(player);
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.DRAGON_BONES && id != ItemIdentifiers.BLACK_DRAGONHIDE) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
