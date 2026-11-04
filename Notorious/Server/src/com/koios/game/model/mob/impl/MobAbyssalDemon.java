package com.koios.game.model.mob.impl;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobAbyssalDemon extends MobDemon {

	public MobAbyssalDemon(int index, Location location) {
		super(index, 1615, location);
	}

	public MobAbyssalDemon(Location location) {
		super(1615, location);
		setWalkingType(0);
		setMaxHit(10);
		setAttack(100);
	}

	@Override
	public MobAbyssalDemon copy() {
		MobAbyssalDemon mob = new MobAbyssalDemon(homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		int num = player.getRandom().nextInt(125);
		drop(ItemIdentifiers.ASHES);
		player.increaseVariable("kc_abyssal_demon", 1);

		if (num < 1) {
			num = player.getRandom().nextInt(4);

			if (num < 1) {
				drop(ItemIdentifiers.WHIP_VINE);
				player.sendMessage("<col=800000>A whip vine has dropped on the ground.");

				if (player.isNewsfeedEnabled()) {
					StaticDropBroadcastStrategy.broadcast(this, player, 16, ItemIdentifiers.WHIP_VINE, 1, getName());
				}
			} else if (num < 4) {
				drop(ItemIdentifiers.ABYSSAL_WHIP);
				AchievementManager.performed(player, Achievement.OBTAIN_WHIP);
				player.sendMessage("<col=800000>An abyssal whip has dropped on the ground.");

				if (player.isNewsfeedEnabled()) {
					StaticDropBroadcastStrategy.broadcast(this, player, 16, ItemIdentifiers.ABYSSAL_WHIP, 1, getName());
				}
			}
		} else if (num < 2) {
			drop(ItemIdentifiers.CHALLENGE_SCROLL);
		} else if (num < 4) {
			drop(ItemIdentifiers.TOOTH_HALF_KEY);
			player.sendMessage("<col=800000>A tooth half key has dropped on the ground.");
		} else if (num < 6) {
			drop(ItemIdentifiers.LOOP_HALF_KEY);
			player.sendMessage("<col=800000>A loop half key has dropped on the ground.");
		} else if (num < 16) {
			// Uncut gemstones
			num = player.getRandom().nextInt(15);
			if (num < 1) {
				drop(ItemIdentifiers.UNCUT_DRAGONSTONE);
			} else if (num < 3) {
				drop(ItemIdentifiers.UNCUT_DIAMOND);
			} else if (num < 6) {
				drop(ItemIdentifiers.UNCUT_RUBY);
			} else if (num < 10) {
				drop(ItemIdentifiers.UNCUT_EMERALD);
			} else if (num < 15) {
				drop(ItemIdentifiers.UNCUT_SAPPHIRE);
			}
		} else if (num < 20) {
			drop(ItemIdentifiers.RUNE_CHAINBODY);
		} else if (num < 26) {
			drop(ItemIdentifiers.RUNE_MED_HELM);
		} else if (num < 40) {
			drop(ItemIdentifiers.RUNE_DAGGER);
		} else if (num < 50) {
			drop(ItemIdentifiers.ADAMANT_BATTLEAXE);
		} else if (num < 66) {
			drop(ItemIdentifiers.BLACK_AXE);
		} else if (num < 110) {
			num = player.getRandom().nextInt(5);
			if (num < 1) {
				drop(ItemIdentifiers.COINS, 11);
			} else if (num < 2) {
				drop(ItemIdentifiers.COINS, 44);
			} else if (num < 3) {
				drop(ItemIdentifiers.COINS, 132);
			} else if (num < 4) {
				drop(ItemIdentifiers.COINS, 220);
			} else if (num < 5) {
				drop(ItemIdentifiers.COINS, 460);
			}
		} else if (num < 160) {
			// Grimy herbs
			num = player.getRandom().nextInt(114);
			if (num < 3) {
				drop(ItemIdentifiers.GRIMY_KWUARM);
			} else if (num < 9) {
				drop(ItemIdentifiers.GRIMY_AVANTOE);
			} else if (num < 15) {
				drop(ItemIdentifiers.GRIMY_IRIT);
			} else if (num < 24) {
				drop(ItemIdentifiers.GRIMY_TOADFLAX);
			} else if (num < 36) {
				drop(ItemIdentifiers.GRIMY_RANARR);
			} else if (num < 51) {
				drop(ItemIdentifiers.GRIMY_HARRALANDER);
			} else if (num < 69) {
				drop(ItemIdentifiers.GRIMY_MARRENTILL);
			} else if (num < 90) {
				drop(ItemIdentifiers.GRIMY_TARROMIN);
			} else if (num < 114) {
				drop(ItemIdentifiers.GRIMY_GUAM);
			}
		} else if (num < 182) {
			// Herb seeds
			num = player.getRandom().nextInt(115);
			if (num < 1) {
				drop(ItemIdentifiers.SNAPDRAGON_SEED);
			} else if (num < 4) {
				drop(ItemIdentifiers.KWUARM_SEED);
			} else if (num < 10) {
				drop(ItemIdentifiers.AVANTOE_SEED);
			} else if (num < 16) {
				drop(ItemIdentifiers.IRIT_SEED);
			} else if (num < 25) {
				drop(ItemIdentifiers.TOADFLAX_SEED);
			} else if (num < 37) {
				drop(ItemIdentifiers.RANARR_SEED);
			} else if (num < 52) {
				drop(ItemIdentifiers.HARRALANDER_SEED);
			} else if (num < 70) {
				drop(ItemIdentifiers.MARRENTILL_SEED);
			} else if (num < 91) {
				drop(ItemIdentifiers.TARROMIN_SEED);
			} else if (num < 115) {
				drop(ItemIdentifiers.GUAM_SEED);
			}
		} else if (num < 200) {
			drop(ItemIdentifiers.LOBSTER);
		}
	}

}
