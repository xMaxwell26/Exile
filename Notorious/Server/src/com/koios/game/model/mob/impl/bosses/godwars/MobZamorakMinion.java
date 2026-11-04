package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobZamorakMinion extends Mob {

	public MobZamorakMinion(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobZamorakMinion copy() {
		MobZamorakMinion mob = new MobZamorakMinion(0, getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		player.increaseVariable("kc_gwd_minion_zamorak", 1);

		// Guaranteed ashes drop
		drop(ItemIdentifiers.ASHES);

		int rn = player.getRandom().nextInt(768);

		if (rn < 3) {
			rn = player.getRandom().nextInt(2);

			if (rn < 1) {
				drop(ItemIdentifiers.STEAM_BATTLESTAFF);
			} else if (rn < 2) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received a zamorakian spear drop.",
						p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.ZAMORAKIAN_SPEAR);
			}

			return;
		} else if (rn < 5) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.GODSWORD_SHARD_1);
			} else if (rn < 2) {
				drop(ItemIdentifiers.GODSWORD_SHARD_2);
			} else if (rn < 3) {
				drop(ItemIdentifiers.GODSWORD_SHARD_3);
			}
			return;
		}

		rn = player.getRandom().nextInt(7);

		if (rn < 1) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.TUNA_POTATO, 2);
			} else if (rn < 3) {
				drop(ItemIdentifiers.SHARK, 2);
			}
		} else if (rn < 2) {
			drop(ItemIdentifiers.STEEL_DART, NumberUtil.random(95, 102));
		} else if (rn < 3) {
			drop(ItemIdentifiers.STEEL_ARROW, NumberUtil.random(95, 102));
		} else if (rn < 4) {
			drop(ItemIdentifiers.BLOOD_RUNE, NumberUtil.random(5, 10));
		} else if (rn < 5) {
			drop(ItemIdentifiers.DEATH_RUNE, NumberUtil.random(5, 10));
		} else if (rn < 6) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(1250, 2500));
		} else if (rn < 7) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.WINE_OF_ZAMORAK, NumberUtil.random(5, 10));
			} else if (rn < 3) {
				drop(ItemIdentifiers.SUPER_STRENGTH_3);
			}
		}
	}

	@Override
	public void dropCharms(Player player) {
		// No charm drops!
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
