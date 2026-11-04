package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobBandosMinion extends Mob {

	public MobBandosMinion(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobBandosMinion copy() {
		MobBandosMinion mob = new MobBandosMinion(0, getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		player.increaseVariable("kc_gwd_minion_bandos", 1);

		// Guaranteed bones drop
		drop(ItemIdentifiers.BONES);

		int rn = player.getRandom().nextInt(768);

		if (rn < 1) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received a bandos chestplate drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.BANDOS_CHESTPLATE);
			return;
		} else if (rn < 2) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received some bandos tassets drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.BANDOS_TASSETS);
			return;
		} else if (rn < 3) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed " + getName() + ", and received aome bandos boots drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.BANDOS_BOOTS);
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

		rn = player.getRandom().nextInt(8);

		if (rn < 1) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.CHILLI_POTATO, 3);
			} else if (rn < 3) {
				drop(ItemIdentifiers.SHARK, 2);
			}
		} else if (rn < 2) {
			drop(ItemIdentifiers.STEEL_DART, NumberUtil.random(95, 102));
		} else if (rn < 3) {
			drop(ItemIdentifiers.STEEL_ARROW, NumberUtil.random(95, 102));
		} else if (rn < 4) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(1250, 2500));
		} else if (rn < 5) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.COAL_NOTE, NumberUtil.random(15, 20));
			} else if (rn < 3) {
				drop(ItemIdentifiers.GRIMY_SNAPDRAGON_NOTE);
			}
		} else if (rn < 6) {
			drop(ItemIdentifiers.EYE_PATCH);
		} else if (rn < 7) {
			drop(ItemIdentifiers.COSMIC_RUNE, NumberUtil.random(2, 9));
		} else if (rn < 8) {
			drop(ItemIdentifiers.KEBAB);
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
