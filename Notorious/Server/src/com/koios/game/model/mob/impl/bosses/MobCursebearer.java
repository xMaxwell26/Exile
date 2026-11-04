package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobCursebearer extends Mob {

	private MobCursebearer(Location location) {
		super(10127, location);
	}

	public MobCursebearer(int index, Location location) {
		super(index, 10127, location);
	}

	@Override
	public MobCursebearer copy() {
		MobCursebearer mob = new MobCursebearer(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_cursebearer", 1);
		player.sendMessage("Your Unholy Cursebearer kill count is: <col=800000>" + killCount + "</col>.");

		// Drop guaraneed bones loot.
		drop(ItemIdentifiers.BIG_BONES);

		int num = player.getRandom().nextInt(128);

		if (num < 1) {
			// Necklace dropExtraordinary
			num = player.getRandom().nextInt(3);
			if (num < 1) {
				drop(ItemIdentifiers.BLOOD_NECKLACE);
			} else if (num < 2) {
				drop(ItemIdentifiers.DEMON_HORN_NECKLACE);
			} else if (num < 3) {
				// Soul wars red and blue cape dropExtraordinary
				num = player.getRandom().nextInt(2);
				if (num < 1) {
					drop(ItemIdentifiers.BLUE_CAPE);
				} else if (num < 2) {
					drop(ItemIdentifiers.RED_CAPE);
				}
			}
		} else if (num < 2) {
			// Gravite weaponry
			num = player.getRandom().nextInt(5);
			if (num < 1) {
				drop(ItemIdentifiers.GRAVITE_LONGSWORD);
			} else if (num < 2) {
				drop(ItemIdentifiers.GRAVITE_2H_SWORD);
			} else if (num < 3) {
				drop(ItemIdentifiers.GRAVITE_RAPIER);
			} else if (num < 4) {
				drop(ItemIdentifiers.GRAVITE_STAFF);
			} else if (num < 5) {
				drop(ItemIdentifiers.GRAVITE_SHORTBOW);
			}
		} else if (num < 4) {
			// Novite equipment
			num = player.getRandom().nextInt(4);
			if (num < 1) {
				drop(ItemIdentifiers.NOVITE_FULL_HELM);
			} else if (num < 2) {
				drop(ItemIdentifiers.NOVITE_PLATEBODY);
			} else if (num < 3) {
				drop(ItemIdentifiers.NOVITE_KITESHIELD);
			} else if (num < 4) {
				num = player.getRandom().nextInt(2);
				if (num < 1) {
					drop(ItemIdentifiers.NOVITE_PLATELEGS);
				} else if (num < 2) {
					drop(ItemIdentifiers.NOVITE_PLATESKIRT);
				}
			}
		} else if (num < 10) {
			// Challenge scroll, tooth and loop half of a key
			num = player.getRandom().nextInt(9);
			if (num < 1) {
				drop(ItemIdentifiers.CHALLENGE_SCROLL);
			} else if (num < 5) {
				drop(ItemIdentifiers.TOOTH_HALF_KEY);
			} else if (num < 9) {
				drop(ItemIdentifiers.LOOP_HALF_KEY);
			}
		}
		// 1K - 25K coins dropExtraordinary
		else if (num < 28) {
			drop(ItemIdentifiers.COINS, 1_000 * (1 + player.getRandom().nextInt(25)));
		} else if (num < 40) {
			drop(ItemIdentifiers.PURE_ESSENCE_NOTE, 125);
		} else if (num < 60) {
			drop(ItemIdentifiers.RUNE_ESSENCE_NOTE, 125);
		} else if (num < 65) {
			drop(ItemIdentifiers.RUNE_PLATEBODY);
		} else if (num < 75) {
			drop(ItemIdentifiers.ADAMANT_PLATEBODY);
		} else if (num < 80) {
			// Magic runes
			num = random.nextInt(26);
			if (num < 1) {
				drop(ItemIdentifiers.BLOOD_RUNE, 125);
			} else if (num < 2) {
				drop(ItemIdentifiers.DEATH_RUNE, 125);
			} else if (num < 3) {
				drop(ItemIdentifiers.NATURE_RUNE, 125);
			} else if (num < 4) {
				drop(ItemIdentifiers.ASTRAL_RUNE, 125);
			} else if (num < 6) {
				drop(ItemIdentifiers.CHAOS_RUNE, 250);
			} else if (num < 11) {
				drop(ItemIdentifiers.AIR_RUNE, 500);
			} else if (num < 16) {
				drop(ItemIdentifiers.WATER_RUNE, 500);
			} else if (num < 21) {
				drop(ItemIdentifiers.EARTH_RUNE, 500);
			} else if (num < 26) {
				drop(ItemIdentifiers.FIRE_RUNE, 500);
			}
		} else if (num < 85) {
			drop(ItemIdentifiers.RUNE_DAGGER);
		} else if (num < 95) {
			drop(ItemIdentifiers.ADAMANT_LONGSWORD);
		} else if (num < 110) {
			drop(ItemIdentifiers.RUM);
		} else if (num < 125) {
			drop(ItemIdentifiers.VODKA);
		}
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);

		if (id != ItemIdentifiers.BIG_BONES) {
			Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (maybePlayer.isPresent()) {
				StaticDropBroadcastStrategy.broadcast(this, maybePlayer.get(), 16, id, amount, getName());
			}
		}
	}

}
