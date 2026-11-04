package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobCorporealBeast extends Mob {

	private MobCorporealBeast(Location location) {
		super(8133, location);
	}

	public MobCorporealBeast(int index, Location location) {
		super(index, 8133, location);
	}

	@Override
	public MobCorporealBeast copy() {
		MobCorporealBeast mob = new MobCorporealBeast(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_corporeal_beast", 1);
		player.sendMessage("Your Corporeal beast kill count is: <col=800000>" + NumberUtil.format(killCount) + "</col>.");

		// Guaranteed ash drop
		drop(ItemIdentifiers.ASHES);
		runAdditionalDropEvaluations(player);

		int rn = player.getRandom().nextInt(128);

		if (rn < 1) {
			rn = player.getRandom().nextInt(8);

			if (rn < 1) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed the Corporeal Beast, and it dropped a divine sigil.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.DIVINE_SIGIL);
			} else if (rn < 2) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed the Corporeal Beast, and it dropped an elysian sigil.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.ELYSIAN_SIGIL);
			} else if (rn < 5) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed the Corporeal Beast, and it dropped an arcane sigil.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.ARCANE_SIGIL);
			} else if (rn < 8) {
				GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed the Corporeal Beast, and it dropped a spectral sigil.", p -> !p.disabledGlobalChat);
				drop(ItemIdentifiers.SPECTRAL_SIGIL);
			}
		} else if (rn < 2) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed the Corporeal Beast, and it dropped an holy elixir.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.HOLY_ELIXIR);
		} else if (rn < 4) {
			drop(ItemIdentifiers.SPIRIT_SHIELD);
		} else if (rn < 5) {
			// The infamous "Gem drop" mass
			rn = player.getRandom().nextInt(3);
			drop(ItemIdentifiers.COINS, NumberUtil.random(5000, 35000));
			drop(ItemIdentifiers.UNCUT_SAPPHIRE, NumberUtil.random(1, 6));
			drop(ItemIdentifiers.UNCUT_EMERALD, NumberUtil.random(1, 6));
			if (rn < 1) {
				drop(ItemIdentifiers.UNCUT_RUBY, NumberUtil.random(1, 3));
			}
			if (rn < 2) {
				drop(ItemIdentifiers.UNCUT_DIAMOND, NumberUtil.random(1, 2));
			}
			if (rn < 3) {
				drop(ItemIdentifiers.NATURE_TALISMAN, NumberUtil.random(1, 2));
			}
		}

		// Equipment
		else if (rn < 8) {
			drop(ItemIdentifiers.ADAMANT_ARROW, 750);
		} else if (rn < 11) {
			drop(ItemIdentifiers.RUNITE_BOLTS, 250);
		} else if (rn < 14) {
			drop(ItemIdentifiers.CANNONBALL, 2000);
		} else if (rn < 17) {
			drop(ItemIdentifiers.MYSTIC_AIR_STAFF);
		} else if (rn < 20) {
			drop(ItemIdentifiers.MYSTIC_WATER_STAFF);
		} else if (rn < 23) {
			drop(ItemIdentifiers.MYSTIC_EARTH_STAFF);
		} else if (rn < 26) {
			drop(ItemIdentifiers.MYSTIC_FIRE_STAFF);
		} else if (rn < 29) {
			drop(ItemIdentifiers.MYSTIC_ROBE_TOP);
		} else if (rn < 32) {
			drop(ItemIdentifiers.MYSTIC_ROBE_BOTTOM);
		} else if (rn < 35) {
			drop(ItemIdentifiers.ONYX_BOLTS_E, 175);
		} else if (rn < 40) {
			drop(ItemIdentifiers.PURE_ESSENCE_NOTE, 2500);
		} else if (rn < 45) {
			drop(ItemIdentifiers.LAW_RUNE, 100);
		} else if (rn < 50) {
			drop(ItemIdentifiers.COSMIC_RUNE, 500);
		} else if (rn < 55) {
			drop(ItemIdentifiers.DEATH_RUNE, 300);
		} else if (rn < 60) {
			drop(ItemIdentifiers.SOUL_RUNE, 250);
		} else if (rn < 65) {
			drop(ItemIdentifiers.ADAMANTITE_ORE_NOTE, 125);
		} else if (rn < 70) {
			drop(ItemIdentifiers.RUNITE_ORE_NOTE, 20);
		} else if (rn < 75) {
			drop(ItemIdentifiers.ADAMANTITE_BAR_NOTE, 35);
		} else if (rn < 80) {
			drop(ItemIdentifiers.TEAK_PLANK_NOTE, 100);
		} else if (rn < 85) {
			drop(ItemIdentifiers.MAHOGANY_LOGS_NOTE, 150);
		} else if (rn < 90) {
			drop(ItemIdentifiers.MAGIC_LOGS_NOTE, 75);
		} else if (rn < 95) {
			drop(ItemIdentifiers.GREEN_DRAGONHIDE_NOTE, 100);
		} else if (rn < 100) {
			drop(ItemIdentifiers.RAW_SHARK_NOTE, 70);
		} else if (rn < 105) {
			drop(ItemIdentifiers.WHITE_BERRIES_NOTE, 120);
		} else if (rn < 110) {
			drop(ItemIdentifiers.DESERT_GOAT_HORN_NOTE, 120);
		} else if (rn < 112) {
			drop(ItemIdentifiers.RANARR_SEED, NumberUtil.random(10, 15));
		} else if (rn < 115) {
			drop(ItemIdentifiers.WATERMELON_SEED, 24);
		} else if (rn < 128) {
			drop(ItemIdentifiers.TUNA_POTATO, 30);
		}
	}

	private void dropAdditionalItem(Player player) {
		int rn = player.getRandom().nextInt(2);
		if (rn < 1) {
			drop(ItemIdentifiers.CRYSTAL_KEY);
		} else if (rn < 2) {
			drop(ItemIdentifiers.CHALLENGE_SCROLL);
		}
	}

	private void runAdditionalDropEvaluations(Player player) {
		boolean hasRingOfWealth = player.hasRingOfWealth();
		int chanceModified = hasRingOfWealth ? 24 : 10;
		int randomChoice = player.getRandom().nextInt(320);

		if (randomChoice < chanceModified) {
			dropAdditionalItem(player);

			if (hasRingOfWealth && randomChoice > 1) {
				// Would've been a "miss" on the table had the user not been using a ROW.
				player.sendMessage("<col=FF7000>Your ring of wealth shines brightly!");
			}
		}

		// A drop table cannot exist without one of these easter egg drops!
		if (player.getRandom().nextInt(4096) < 1) {
			drop(ItemIdentifiers.BLUE_PARTYHAT_FAKE);
			player.sendMessage("<col=800000>Congratulations! Now that you've hit that 1:4096 drop, the question is: real or not?");
		}
	}

	@Override
	public void dropCharms(Player player) {
		int rn = player.getRandom().nextInt(16);
		if (rn < 3) {
			drop(ItemIdentifiers.BLUE_CHARM, 13); // 3:8
		} else if (rn < 5) {
			drop(ItemIdentifiers.CRIMSON_CHARM, 13); // 2:8
		} else if (rn < 7) {
			drop(ItemIdentifiers.GREEN_CHARM, 13); // 2:8
		} else if (rn < 8) {
			drop(ItemIdentifiers.GOLD_CHARM, 13); // 1:8
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
