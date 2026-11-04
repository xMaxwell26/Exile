package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobZamorakBoss extends Mob {

	public MobZamorakBoss(int index, Location location) {
		super(index, 6203, location);
	}

	@Override
	public MobZamorakBoss copy() {
		MobZamorakBoss mob = new MobZamorakBoss(0, homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_gwd_boss_zamorak", 1);
		player.sendMessage("Your K'ril Tsutsaroth kill count is: <col=800000>" + NumberUtil.format(killCount) + "</col>.");

		// Guaranteed ashes drop
		drop(ItemIdentifiers.ASHES);

		int rn = player.getRandom().nextInt(256);

		if (rn < 1) {
			rn = player.getRandom().nextInt(2);

			if (rn < 1) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed K'ril Tsutsaroth, and received a zamorak hilt drop.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.ZAMORAK_HILT); // 1:512
			} else if (rn < 2) {
				drop(ItemIdentifiers.DRAGON_LONGSWORD); // 1:512
			}
		} else if (rn < 4) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed K'ril Tsutsaroth, and received a zamorakian spear drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ZAMORAKIAN_SPEAR); // 3:256
		} else if (rn < 6) {
			rn = player.getRandom().nextInt(3);
			if (rn < 1) {
				drop(ItemIdentifiers.GODSWORD_SHARD_1); // 1:384
			} else if (rn < 2) {
				drop(ItemIdentifiers.GODSWORD_SHARD_2); // 1:384
			} else if (rn < 3) {
				drop(ItemIdentifiers.GODSWORD_SHARD_3); // 1:384
			}
		} else if (rn < 7) {
			drop(ItemIdentifiers.STEAM_BATTLESTAFF); // 1:256
		} else if (rn < 12) {
			drop(ItemIdentifiers.CHALLENGE_SCROLL); // 1:16
		} else if (rn < 14) {
			drop(ItemIdentifiers.LOOP_HALF_KEY); // 1:16
		} else if (rn < 18) {
			drop(ItemIdentifiers.TOOTH_HALF_KEY); // 1:16
		} else if (rn < 22) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(19900, 21000)); // approx. 1:16 (15:256)
		} else if (rn < 54) {
			drop(ItemIdentifiers.RUNE_SCIMITAR); // 1:16
		} else if (rn < 70) {
			drop(ItemIdentifiers.ADAMANT_PLATEBODY); // 1:16
		} else if (rn < 86) {
			drop(ItemIdentifiers.RUNE_PLATELEGS); // 1:16
		} else if (rn < 102) {
			drop(ItemIdentifiers.SUPER_ATTACK_3, 3); // 1:16
		} else if (rn < 118) {
			drop(ItemIdentifiers.SUPER_STRENGTH_3, 3); // 1:16
		} else if (rn < 134) {
			drop(ItemIdentifiers.SUPER_RESTORE_3, 3); // 1:16
		} else if (rn < 150) {
			drop(ItemIdentifiers.ZAMORAK_BREW_3, 3); // 1:16
		} else if (rn < 166) {
			drop(ItemIdentifiers.LANTADYME_SEED, 3); // 1:16
		} else if (rn < 182) {
			drop(ItemIdentifiers.GRIMY_LANTADYME_NOTE, 10); // 1:16
		} else if (rn < 198) {
			drop(ItemIdentifiers.RUNITE_ORE_NOTE, 2); // 1:16
		} else if (rn < 214) {
			drop(ItemIdentifiers.INFERNAL_ASHES_NOTE, 5); // 1:16
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

		if (id != ItemIdentifiers.ASHES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
