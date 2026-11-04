package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobArmadylMinion extends Mob {

	public MobArmadylMinion(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobArmadylMinion copy() {
		MobArmadylMinion mob = new MobArmadylMinion(0, getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		player.increaseVariable("kc_gwd_minion_armadyl", 1);

		// Guaranteed bones drop
		drop(ItemIdentifiers.BONES);

		int rn = player.getRandom().nextInt(768);

		if (rn < 1) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received an armadyl helmet drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ARMADYL_HELMET);
			return;
		} else if (rn < 2) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received an armadyl chestplate drop.",
					p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ARMADYL_CHESTPLATE);
			return;
		} else if (rn < 3) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received an armadyl chainskirt drop.",
					p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ARMADYL_CHAINSKIRT);
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
			drop(ItemIdentifiers.STEEL_DART, NumberUtil.random(95, 102));
		} else if (rn < 3) {
			drop(ItemIdentifiers.STEEL_ARROW, NumberUtil.random(95, 102));
		} else if (rn < 4) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(1250, 2500));
		} else if (rn < 5) {
			drop(ItemIdentifiers.CRUSHED_NEST_NOTE, NumberUtil.random(2, 5));
		} else if (rn < 6) {
			drop(ItemIdentifiers.DIAMOND_NOTE, NumberUtil.random(1, 3));
		}
	}

	@Override
	public void dropCharms(Player player) {
		// No charm drops!
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.BONES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
