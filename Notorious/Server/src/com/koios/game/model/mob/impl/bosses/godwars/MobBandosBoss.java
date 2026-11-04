package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobBandosBoss extends Mob {

	public MobBandosBoss(int index, Location location) {
		super(index, 6260, location);
	}

	@Override
	public MobBandosBoss copy() {
		MobBandosBoss mob = new MobBandosBoss(0, homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_gwd_boss_bandos", 1);
		player.sendMessage("Your General Graardor kill count is: <col=800000>" + NumberUtil.format(killCount) + "</col>.");

		// Guaranteed ourg bones drop
		drop(ItemIdentifiers.OURG_BONES);

		int rn = player.getRandom().nextInt(256);

		if (rn < 1) {
			rn = player.getRandom().nextInt(2);

			if (rn < 1) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed General Graardor, and received a bandos hilt drop.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.BANDOS_HILT); // 1:512
			} else if (rn < 2) {
				drop(ItemIdentifiers.DRAGON_LONGSWORD); // 1:512
			}
		} else if (rn < 2) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed General Graardor, and received a bandos chestplate drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.BANDOS_CHESTPLATE); // 1:256
		} else if (rn < 3) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed General Graardor, and received a bandos tassets drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.BANDOS_TASSETS); // 1:256
		} else if (rn < 4) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed General Graardor, and received a bandos boots drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.BANDOS_BOOTS); // 1:256
		} else if (rn < 6) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.GODSWORD_SHARD_1); // 1:384
			} else if (rn < 2) {
				drop(ItemIdentifiers.GODSWORD_SHARD_2); // 1:384
			} else if (rn < 3) {
				drop(ItemIdentifiers.GODSWORD_SHARD_3); // 1:384
			}
		} else if (rn < 12) {
			drop(ItemIdentifiers.CHALLENGE_SCROLL); // 1:16
		} else if (rn < 14) {
			drop(ItemIdentifiers.LOOP_HALF_KEY); // 1:16
		} else if (rn < 18) {
			drop(ItemIdentifiers.TOOTH_HALF_KEY); // 1:16
		} else if (rn < 22) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(19900, 21000)); // 1:16
		} else if (rn < 54) {
			drop(ItemIdentifiers.RUNE_LONGSWORD); // 1:16
		} else if (rn < 70) {
			drop(ItemIdentifiers.RUNE_2H_SWORD); // 1:16
		} else if (rn < 86) {
			drop(ItemIdentifiers.RUNE_PICKAXE); // 1:16
		} else if (rn < 102) {
			drop(ItemIdentifiers.RUNE_PLATEBODY); // 1:16
		} else if (rn < 118) {
			drop(ItemIdentifiers.RUNITE_BAR_NOTE, 2); // 1:16
		} else if (rn < 134) {
			drop(ItemIdentifiers.ADAMANTITE_ORE_NOTE, 20); // 1:16
		} else if (rn < 150) {
			drop(ItemIdentifiers.COAL_NOTE, 120); // 1:16
		} else if (rn < 166) {
			drop(ItemIdentifiers.MAGIC_LOGS_NOTE, 20); // 1:16
		} else if (rn < 182) {
			drop(ItemIdentifiers.SUPER_RESTORE_4, 3); // 1:16
		} else if (rn < 198) {
			drop(ItemIdentifiers.SNAPDRAGON_SEED); // 1:16
		} else if (rn < 214) {
			drop(ItemIdentifiers.GRIMY_SNAPDRAGON_NOTE, 3); // 1:16
		} else if (rn < 256) {
			GodWarsRareDropTable.runDropTable(player, this); // 10:256 (approx. 1:26)
		}
	}

	@Override
	public void dropCharms(Player player) {
		int rn = player.getRandom().nextInt(100);
		if (rn < 20) {
			drop(ItemIdentifiers.GOLD_CHARM, 3); // 20%
		} else if (rn < 35) {
			drop(ItemIdentifiers.GREEN_CHARM, 3); // 15%
		} else if (rn < 50) {
			drop(ItemIdentifiers.CRIMSON_CHARM, 3); // 15%
		} else if (rn < 60) {
			drop(ItemIdentifiers.BLUE_CHARM, 3); // 10%
			// 40% chance of no charm drop
		}
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.OURG_BONES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
