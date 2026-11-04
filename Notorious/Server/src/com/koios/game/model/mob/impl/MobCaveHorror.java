package com.koios.game.model.mob.impl;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;

public class MobCaveHorror extends Mob {

	public MobCaveHorror(int id, Location location) {
		super(id, location);
	}

	public MobCaveHorror(int index, int id, Location location) {
		super(index, id, location);
	}

	@Override
	public MobCaveHorror copy() {
		MobCaveHorror mob = new MobCaveHorror(getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		AchievementManager.performed(player, Achievement.KILL_CAVE_HORRORS);

		int anum = player.getRandom().nextInt(400); // 0-399
		int bnum = anum / 10; // 0-39
		int cnum = bnum / 10; // 0-3

		if (player.hasRingOfWealth()) {
			/*
			 * Note: If the hit 'anum' is NOT 0-9 (slots in which rare drops are located) then there is a 1/5 chance the player can have a second spin on the dropExtraordinary
			 * table.
			 *
			 * This effectively increases the chance of a rare dropExtraordinary.
			 */

			if (bnum > 0 && player.getRandom().nextInt(5) < 1) {
				anum = player.getRandom().nextInt(400);
				bnum = anum / 10;
				cnum = bnum / 10;

				// Let the player know their ring just let them have a second
				// spin on the wheel. This does not warrant a rare drop like a black mask.
				player.sendMessage("<col=FF7000>Your ring of wealth shines brightly!");
			}
		}

		// Guaranteed big bones drop
		drop(ItemIdentifiers.BIG_BONES);
		player.increaseVariable("kc_cave_horror", 1);

		if (anum == 0) {
			player.sendMessage("<col=800000>A charged black mask has dropped on the ground!");
			drop(ItemIdentifiers.BLACK_MASK_10);
		} else if (bnum == 0) {
			// 2 slots available - anum 0 is a black mask drop above.
			if (anum < 3) {
				drop(ItemIdentifiers.CHALLENGE_SCROLL);
			} else if (anum < 4) {
				drop(ItemIdentifiers.UNCUT_DRAGONSTONE);
			} else if (anum < 7) {
				drop(ItemIdentifiers.TOOTH_HALF_KEY);
			} else if (anum < 10) {
				drop(ItemIdentifiers.LOOP_HALF_KEY);
			}
		} else if (cnum == 0) {
			int rn = player.getRandom().nextInt(7);
			if (rn < 3) {
				drop(ItemIdentifiers.TOADFLAX_SEED);
			} else if (rn < 5) {
				drop(ItemIdentifiers.IRIT_SEED);
			} else if (rn < 6) {
				drop(ItemIdentifiers.AVANTOE_SEED);
			} else if (rn < 7) {
				drop(ItemIdentifiers.KWUARM_SEED);
			}
		} else if (cnum == 1) {
			if (anum < 144) {
				drop(ItemIdentifiers.POTATO_SEED, 1 + player.getRandom().nextInt(4));
			} else if (anum < 188) {
				drop(ItemIdentifiers.ONION_SEED, 1 + player.getRandom().nextInt(3));
			} else if (anum < 194) {
				drop(ItemIdentifiers.UNCUT_SAPPHIRE);
			} else {
				drop(ItemIdentifiers.UNCUT_EMERALD);
			}
		} else if (cnum == 2) {
			if (bnum < 235) {
				drop(ItemIdentifiers.MAHOGANY_LOGS_NOTE, 2);
			} else {
				drop(ItemIdentifiers.TEAK_LOGS_NOTE, 4);
			}
		} else if (cnum == 3) {
			if (bnum < 333) {
				drop(ItemIdentifiers.NATURE_RUNE, 3 + player.getRandom().nextInt(4));
			} else if (bnum < 360) {
				drop(ItemIdentifiers.LIMPWURT_ROOT);
			} else if (bnum < 365) {
				drop(ItemIdentifiers.ADAMANT_KITESHIELD);
			} else if (bnum < 375) {
				drop(ItemIdentifiers.MITHRIL_AXE);
			} else if (bnum < 390) {
				drop(ItemIdentifiers.MITHRIL_KITESHIELD);
			} else {
				drop(ItemIdentifiers.RUNE_DAGGER);
			}
		}
	}

}
