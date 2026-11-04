package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobSaradominBoss extends Mob {

	public MobSaradominBoss(int index, Location location) {
		super(index, 6247, location);
	}

	@Override
	public MobSaradominBoss copy() {
		MobSaradominBoss mob = new MobSaradominBoss(0, homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_gwd_boss_saradomin", 1);
		player.sendMessage("Your Commander Zilyana kill count is: <col=800000>" + NumberUtil.format(killCount) + "</col>.");

		// Guaranteed bones drop
		drop(ItemIdentifiers.BONES);

		int rn = player.getRandom().nextInt(256);

		if (rn < 1) {
			rn = player.getRandom().nextInt(2);

			if (rn < 1) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed Commander Zilyana, and received a saradomin hilt drop.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.SARADOMIN_HILT); // 1:512
			} else if (rn < 2) {
				drop(ItemIdentifiers.DRAGON_LONGSWORD); // 1:512
			}
		} else if (rn < 4) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed Commander Zilyana, and received a saradomin sword drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.SARADOMIN_SWORD); // 3:256
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
			drop(ItemIdentifiers.BATTLESTAFF_NOTE, 2); // 1:16
		} else if (rn < 70) {
			drop(ItemIdentifiers.RUNE_DART, 40); // 1:16
		} else if (rn < 86) {
			drop(ItemIdentifiers.ADAMANT_PLATEBODY); // 1:16
		} else if (rn < 102) {
			drop(ItemIdentifiers.RUNE_PLATESKIRT); // 1:16
		} else if (rn < 118) {
			drop(ItemIdentifiers.RUNE_KITESHIELD, 2); // 1:16
		} else if (rn < 134) {
			drop(ItemIdentifiers.GRIMY_RANARR_NOTE, 5); // 1:16
		} else if (rn < 150) {
			drop(ItemIdentifiers.RANARR_SEED, 2); // 1:16
		} else if (rn < 166) {
			rn = player.getRandom().nextInt(2);
			if (rn < 1) {
				drop(ItemIdentifiers.MAGIC_POTION_3, 3); // 1:32
			} else if (rn < 2) {
				drop(ItemIdentifiers.SUPER_DEFENCE_4, 3); // 1:32
			}
		} else if (rn < 182) {
			drop(ItemIdentifiers.PRAYER_POTION_4, 3); // 1:16
		} else if (rn < 198) {
			rn = player.getRandom().nextInt(2);
			if (rn < 1) {
				drop(ItemIdentifiers.SARADOMIN_BREW_3, 3); // 1:32
			} else if (rn < 2) {
				drop(ItemIdentifiers.SUPER_RESTORE_4, 3); // 1:32
			}
		} else if (rn < 214) {
			drop(ItemIdentifiers.MAGIC_SEED); // 1:16
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

		if (id != ItemIdentifiers.BONES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
