package com.koios.game.model.mob.impl.bosses.godwars;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobArmadylBoss extends Mob {

	public MobArmadylBoss(int index, Location location) {
		super(index, 6222, location);
	}

	@Override
	public MobArmadylBoss copy() {
		MobArmadylBoss mob = new MobArmadylBoss(0, homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_gwd_boss_armadyl", 1);
		player.sendMessage("Your Kree'arra kill count is: <col=800000>" + NumberUtil.format(killCount) + "</col>.");

		// Guaranteed ourg bones drop
		drop(ItemIdentifiers.BIG_BONES);
		drop(ItemIdentifiers.FEATHER, 1 + player.getRandom().nextInt(15)); // 1-15

		int rn = player.getRandom().nextInt(256);

		if (rn < 1) {
			rn = player.getRandom().nextInt(2);

			if (rn < 1) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed Kree'arra, and received an armadyl hilt drop.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.ARMADYL_HILT); // 1:512
			} else if (rn < 2) {
				drop(ItemIdentifiers.DRAGON_LONGSWORD); // 1:512
			}
		} else if (rn < 2) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed Kree'arra, and received an armadyl helmet drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ARMADYL_HELMET); // 1:256
		} else if (rn < 3) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed Kree'arra, and received an armadyl chestplate drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ARMADYL_CHESTPLATE); // 1:256
		} else if (rn < 4) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed Kree'arra, and received an armadyl chainskirt drop.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.ARMADYL_CHAINSKIRT); // 1:256
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
			drop(ItemIdentifiers.RUNE_CROSSBOW); // 1:16
		} else if (rn < 70) {
			drop(ItemIdentifiers.RUNITE_BOLTS, 25); // 1:16
		} else if (rn < 86) {
			drop(ItemIdentifiers.RUNE_ARROW, 100); // 1:16
		} else if (rn < 102) {
			drop(ItemIdentifiers.RUNE_LONGSWORD); // 1:16
		} else if (rn < 118) {
			drop(ItemIdentifiers.DRAGON_BOLTS_E, NumberUtil.random(2, 15)); // 1:16
		} else if (rn < 134) {
			drop(ItemIdentifiers.BLACK_DHIDE_BODY); // 1:16
		} else if (rn < 150) {
			drop(ItemIdentifiers.RANGING_POTION_3, 3); // 1:16
		} else if (rn < 166) {
			drop(ItemIdentifiers.SUPER_DEFENCE_3, 3); // 1:16
		} else if (rn < 182) {
			drop(ItemIdentifiers.GRIMY_DWARF_WEED_NOTE, NumberUtil.random(5, 22)); // 1:16
		} else if (rn < 198) {
			drop(ItemIdentifiers.DWARF_WEED_SEED, 3); // 1:16
		} else if (rn < 214) {
			drop(ItemIdentifiers.YEW_SEED, 1); // 1:16
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

		if (id != ItemIdentifiers.BIG_BONES && id != ItemIdentifiers.FEATHER) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
