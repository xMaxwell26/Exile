package com.koios.game.model.mob.impl.bosses;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.StaticDropBroadcastStrategy;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobNomad extends Mob {

	private MobNomad(Location location) {
		super(8528, location);
	}

	public MobNomad(int index, Location location) {
		super(index, 8528, location);
	}

	@Override
	public MobNomad copy() {
		MobNomad mob = new MobNomad(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		// Increment kill count for Slayer track records.
		int killCount = player.increaseVariable("kc_nomad", 1);
		player.sendMessage("Your Nomad kill count is: <col=800000>" + killCount + "</col>.");

		// Drop guaraneed bones loot.
		drop(ItemIdentifiers.BONES);

		int num = player.getRandom().nextInt(256);

		if (num < 1) {
			num = player.getRandom().nextInt(5);
			if (num < 1) {
				drop(ItemIdentifiers.LORD_MARSHAL_BOOTS);
			} else if (num < 2) {
				drop(ItemIdentifiers.LORD_MARSHAL_CAP);
			} else if (num < 3) {
				drop(ItemIdentifiers.LORD_MARSHAL_GLOVES);
			} else if (num < 4) {
				drop(ItemIdentifiers.LORD_MARSHAL_TOP);
			} else if (num < 5) {
				num = player.getRandom().nextInt(2);
				if (num < 1) {
					drop(ItemIdentifiers.LORD_MARSHAL_SKIRT);
				} else if (num < 2) {
					drop(ItemIdentifiers.LORD_MARSHAL_TROUSERS);
				}
			}
		} else if (num < 2) {
			num = player.getRandom().nextInt(4);
			if (num < 1) {
				drop(ItemIdentifiers.MYSTERY_BOX);
			} else if (num < 4) {
				drop(ItemIdentifiers.CHALLENGE_SCROLL);
			}
		} else if (num < 5) {
			num = player.getRandom().nextInt(3);
			if (num < 1) {
				drop(ItemIdentifiers.CRYSTAL_KEY);
			} else if (num < 3) {
				drop(ItemIdentifiers.UNCUT_DRAGONSTONE);
			}
		} else if (num < 30) {
			// Grimy herbs
			num = player.getRandom().nextInt(36);
			if (num < 3) {
				drop(ItemIdentifiers.GRIMY_KWUARM_NOTE, 10);
			} else if (num < 9) {
				drop(ItemIdentifiers.GRIMY_AVANTOE_NOTE, 10);
			} else if (num < 15) {
				drop(ItemIdentifiers.GRIMY_IRIT_NOTE, 10);
			} else if (num < 24) {
				drop(ItemIdentifiers.GRIMY_TOADFLAX_NOTE, 10);
			} else if (num < 36) {
				drop(ItemIdentifiers.GRIMY_RANARR_NOTE, 10);
			}
		} else if (num < 55) {
			// Herb seeds
			num = player.getRandom().nextInt(74);
			if (num < 1) {
				drop(ItemIdentifiers.TORSTOL_SEED, 3);
			} else if (num < 3) {
				drop(ItemIdentifiers.SNAPDRAGON_SEED, 3);
			} else if (num < 8) {
				drop(ItemIdentifiers.KWUARM_SEED, 3);
			} else if (num < 20) {
				drop(ItemIdentifiers.AVANTOE_SEED, 3);
			} else if (num < 32) {
				drop(ItemIdentifiers.IRIT_SEED, 3);
			} else if (num < 50) {
				drop(ItemIdentifiers.TOADFLAX_SEED, 3);
			} else if (num < 74) {
				drop(ItemIdentifiers.RANARR_SEED, 3);
			}
		} else if (num < 60) {
			drop(ItemIdentifiers.RUNITE_BOLTS, 250);
		} else if (num < 65) {
			drop(ItemIdentifiers.MYSTIC_ROBE_TOP);
		} else if (num < 70) {
			drop(ItemIdentifiers.MYSTIC_ROBE_BOTTOM);
		} else if (num < 83) {
			drop(ItemIdentifiers.YEW_LOGS_NOTE, 100);
		} else if (num < 89) {
			drop(ItemIdentifiers.MAGIC_LOGS_NOTE, 50);
		} else if (num < 105) {
			drop(ItemIdentifiers.ADAMANTITE_ORE_NOTE, 50);
		} else if (num < 110) {
			drop(ItemIdentifiers.RUNITE_ORE_NOTE, 15);
		} else if (num < 112) {
			num = player.getRandom().nextInt(2);
			if (num < 1) {
				drop(ItemIdentifiers.BLUE_CAPE);
			} else if (num < 2) {
				drop(ItemIdentifiers.RED_CAPE);
			}
		} else if (num < 125) {
			drop(ItemIdentifiers.MUSHROOM_POTATO, 10);
		} else if (num < 127) {
			drop(ItemIdentifiers.UNCUT_DIAMOND);
		} else if (num < 130) {
			drop(ItemIdentifiers.UNCUT_RUBY);
		} else if (num < 134) {
			drop(ItemIdentifiers.UNCUT_EMERALD);
		} else if (num < 139) {
			drop(ItemIdentifiers.UNCUT_SAPPHIRE);
		} else if (num < 150) {
			drop(ItemIdentifiers.ADAMANT_ARROW, 500);
		} else if (num < 155) {
			drop(ItemIdentifiers.FLAX_NOTE, 100);
		} else if (num < 165) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(1, 25) * 1_000);
		} else if (num < 180) {
			drop(ItemIdentifiers.GREEN_DRAGONHIDE_NOTE, 75);
		} else if (num < 200) {
			drop(ItemIdentifiers.REDBERRIES_NOTE, 100);
		} else if (num < 206) {
			drop(ItemIdentifiers.RUNE_HATCHET);
		} else if (num < 220) {
			drop(ItemIdentifiers.RUNITE_BAR_NOTE, 3);
		} else if (num < 235) {
			drop(ItemIdentifiers.RAW_MONKFISH_NOTE, 125);
		} else if (num < 256) {
			drop(ItemIdentifiers.PURE_ESSENCE_NOTE, 500);
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
