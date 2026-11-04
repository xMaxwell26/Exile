package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobSaradominMinion extends Mob {

	public MobSaradominMinion(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobSaradominMinion copy() {
		MobSaradominMinion mob = new MobSaradominMinion(0, getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		player.increaseVariable("kc_gwd_minion_saradomin", 1);

		// Guaranteed bones drop
		drop(ItemIdentifiers.BONES);

		int rn = player.getRandom().nextInt(768);

		if (rn < 3) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received a saradomin sword drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.SARADOMIN_SWORD);
			return;
		} else if (rn < 5) {
			int rx = player.getRandom().nextInt(3);
			if (rx < 1) {
				drop(ItemIdentifiers.GODSWORD_SHARD_1);
			} else if (rx < 2) {
				drop(ItemIdentifiers.GODSWORD_SHARD_2);
			} else if (rx < 3) {
				drop(ItemIdentifiers.GODSWORD_SHARD_3);
			}
			return;
		}

		rn = player.getRandom().nextInt(6);

		if (rn < 1) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.MUSHROOM_POTATO, 3);
			} else if (rn < 3) {
				drop(ItemIdentifiers.MANTA_RAY, 2);
			}
		} else if (rn < 2) {
			rn = player.getRandom().nextInt(2);
			if (rn < 1) {
				drop(ItemIdentifiers.STEEL_DART, NumberUtil.random(95, 102));
			} else if (rn < 2) {
				drop(ItemIdentifiers.STEEL_ARROW, NumberUtil.random(95, 102));
			}
		} else if (rn < 3) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(1250, 2500));
		} else if (rn < 5) {
			drop(ItemIdentifiers.UNICORN_HORN_NOTE, 2);
		} else if (rn < 6) {
			drop(ItemIdentifiers.SNAPE_GRASS_NOTE, 5);
		} else if (rn < 7) {
			drop(ItemIdentifiers.ASHES);
		}
	}

	@Override
	public void dropCharms(Player player) {
		// No charm drops!
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.BONES && id != ItemIdentifiers.ASHES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
