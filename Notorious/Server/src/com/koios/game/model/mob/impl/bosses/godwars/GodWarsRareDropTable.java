package com.koios.game.model.mob.impl.bosses.godwars;

import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

/*
 * TODO more balance - this is a placeholder
 */

public final class GodWarsRareDropTable {

	public static void runDropTable(Player player, Mob mob) {
		boolean rowEffect = player.hasRingOfWealth();
		int modifiedChance = rowEffect ? 64 : 80;
		int rn = player.getRandom().nextInt(modifiedChance);

		if (rn < 8) {
			int rx = player.getRandom().nextInt(8);
			if (rx < 3) {
				mob.drop(ItemIdentifiers.UNCUT_SAPPHIRE);
			} else if (rx < 5) {
				mob.drop(ItemIdentifiers.UNCUT_EMERALD);
			} else if (rx < 6) {
				mob.drop(ItemIdentifiers.UNCUT_RUBY);
			} else if (rx < 7) {
				mob.drop(ItemIdentifiers.UNCUT_DIAMOND);
			} else if (rx < 8) {
				mob.drop(ItemIdentifiers.UNCUT_DRAGONSTONE);
			}
		} else if (rn < 12) {
			mob.drop(ItemIdentifiers.RUNE_JAVELIN, 5);
		} else if (rn < 16) {
			mob.drop(ItemIdentifiers.RUNE_SPEAR);
		} else if (rn < 20) {
			mob.drop(ItemIdentifiers.ADAMANT_JAVELIN, 20);
		} else if (rn < 24) {
			mob.drop(ItemIdentifiers.DRAGON_SPEAR);
		} else if (rn < 28) {
			mob.drop(ItemIdentifiers.DRAGON_DAGGER);
		} else if (rn < 32) {
			mob.drop(ItemIdentifiers.PURE_ESSENCE_NOTE, NumberUtil.random(10, 15) * 100);
		} else if (rn < 36) {
			mob.drop(ItemIdentifiers.RUNITE_ORE_NOTE, 100);
		} else if (rn < 40) {
			mob.drop(ItemIdentifiers.BIG_BONES_NOTE, 150);
		} else if (rn < 44) {
			mob.drop(ItemIdentifiers.SILVER_ORE_NOTE, 100);
		} else if (rn < 48) {
			mob.drop(ItemIdentifiers.CLEAN_TORSTOL_NOTE, NumberUtil.random(16, 34));
			// all remaining slots are "empty". Ring of wealth users have less "empty" slots.
		}

		if (rowEffect && rn < 48) {
			player.sendMessage("<col=FF7000>Your ring of wealth shines brightly!");
		}
	}

	private GodWarsRareDropTable() {
		throw new InstantiationError();
	}

}
