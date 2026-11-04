package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.Location;
import com.koios.game.model.mob.impl.MobDemon;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobTormentedDemon extends MobDemon {

	public MobTormentedDemon(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobTormentedDemon copy() {
		MobTormentedDemon mob = new MobTormentedDemon(0, getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_tormented_demon", 1);
		player.sendMessage("Your Tormented demon kill count is: <col=800000>" + killCount + "</col>.");

		// Guaranteed ashes dropExtraordinary
		drop(ItemIdentifiers.ASHES);

		int num = player.getRandom().nextInt(256);

		if (num < 1) {
			num = player.getRandom().nextInt(2);
			if (num < 1) {
				drop(ItemIdentifiers.DRAGON_CLAWS);
			} else if (num < 2) {
				num = player.getRandom().nextInt(3);
				if (num < 1) {
					drop(ItemIdentifiers.RUINED_DRAGON_ARMOUR_LUMP);
				} else if (num < 2) {
					drop(ItemIdentifiers.RUINED_DRAGON_ARMOUR_SHARD);
				} else if (num < 3) {
					drop(ItemIdentifiers.RUINED_DRAGON_ARMOUR_SLICE);
				}
			}
		} else if (num < 2) {
			drop(ItemIdentifiers.CHALLENGE_SCROLL);
		} else if (num < 6) {
			num = player.getRandom().nextInt(2);
			if (num < 1) {
				drop(ItemIdentifiers.TOOTH_HALF_KEY);
			} else if (num < 2) {
				drop(ItemIdentifiers.LOOP_HALF_KEY);
			}
		} else if (num < 20) {
			drop(ItemIdentifiers.UNCUT_DIAMOND_NOTE, 4);
		} else if (num < 34) {
			drop(ItemIdentifiers.ADAMANT_BATTLEAXE);
		} else if (num < 48) {
			drop(ItemIdentifiers.RUNE_MACE);
		} else if (num < 62) {
			drop(ItemIdentifiers.RUNE_SWORD);
		} else if (num < 76) {
			drop(ItemIdentifiers.RUNE_SQ_SHIELD);
		} else if (num < 90) {
			drop(ItemIdentifiers.RUNE_CHAINBODY);
		} else if (num < 104) {
			drop(ItemIdentifiers.RUNE_FULL_HELM);
		} else if (num < 118) {
			drop(ItemIdentifiers.DEATH_RUNE, NumberUtil.random(20, 40));
		} else if (num < 132) {
			drop(ItemIdentifiers.LAW_RUNE, NumberUtil.random(20, 45));
		} else if (num < 146) {
			drop(ItemIdentifiers.FIRE_TALISMAN, 4);
		} else if (num < 160) {
			num = player.getRandom().nextInt(3);
			if (num < 1) {
				drop(ItemIdentifiers.BLOOD_RUNE, 43);
			} else if (num < 3) {
				drop(ItemIdentifiers.BLOOD_RUNE, NumberUtil.random(10, 20));
			}
		} else if (num < 174) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(3_000, 15_000));
		} else if (num < 176) {
			drop(ItemIdentifiers.SNAPDRAGON_SEED, 3);
		} else if (num < 184) {
			drop(ItemIdentifiers.KWUARM_SEED, 3);
		} else if (num < 194) {
			drop(ItemIdentifiers.AVANTOE_SEED, 3);
		} else if (num < 206) {
			drop(ItemIdentifiers.IRIT_SEED, 3);
		} else if (num < 220) {
			drop(ItemIdentifiers.TOADFLAX_SEED, 3);
		} else if (num < 238) {
			drop(ItemIdentifiers.RANARR_SEED, 3);
		} else if (num < 252) {
			drop(ItemIdentifiers.ADAMANTITE_BAR_NOTE, 4);
		} else if (num < 256) {
			num = player.getRandom().nextInt(8);
			if (num < 1) {
				drop(ItemIdentifiers.DRAGON_LONGSWORD);
			} else if (num < 2) {
				drop(ItemIdentifiers.DRAGON_SCIMITAR);
			} else if (num < 5) {
				drop(ItemIdentifiers.DRAGON_DAGGER);
			} else if (num < 8) {
				drop(ItemIdentifiers.DRAGON_MACE);
			}
		}
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.ASHES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				Player player = maybePlayer.get();
				StaticDropBroadcastStrategy.broadcast(this, player, 16, id, amount, getName());

				if (id == ItemIdentifiers.DRAGON_CLAWS) {
					AchievementManager.performed(player, Achievement.OBTAIN_DRAGON_CLAWS);
				}
			}
		}
	}

}
