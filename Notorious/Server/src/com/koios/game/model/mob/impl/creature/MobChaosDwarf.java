package com.koios.game.model.mob.impl.creature;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;

public class MobChaosDwarf extends Mob {

	public MobChaosDwarf(int id, Location location) {
		super(119, location);
	}

	public MobChaosDwarf(int index, int id, Location location) {
		super(index, 119, location);
	}

	@Override
	public MobChaosDwarf copy() {
		MobChaosDwarf mob = new MobChaosDwarf(0, getId(), homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	@Override
	public void dropItems(Player player) {
		drop(ItemIdentifiers.BONES);

		// Evaluate the 1:768 dragon pickaxe drop first.
		int rn = player.getRandom().nextInt(768);

		if (rn < 1) {
			GameService.getWorld().broadcast("<img=9> @blu@" + player.getName() + " killed a chaos dwarf, and received a dragon pickaxe.", p -> !p.disabledGlobalChat);
			drop(ItemIdentifiers.DRAGON_PICKAXE); // 1:768
			return;
		}

		rn %= 16;

		if (rn < 1) {
			drop(ItemIdentifiers.STEEL_FULL_HELM);
		} else if (rn < 2) {
			drop(ItemIdentifiers.MITHRIL_LONGSWORD);
		} else if (rn < 3) {
			drop(ItemIdentifiers.MITHRIL_SQ_SHIELD);
		} else if (rn < 4) {
			drop(ItemIdentifiers.BLACK_FULL_HELM);
		} else if (rn < 5) {
			drop(ItemIdentifiers.COAL);
		} else if (rn < 6) {
			drop(ItemIdentifiers.MITHRIL_BAR);
		} else if (rn < 9) {
			drop(ItemIdentifiers.CHAOS_RUNE, NumberUtil.random(10, 50));
		} else if (rn < 13) {
			drop(ItemIdentifiers.COINS, NumberUtil.random(25, 150));
		} else if (rn < 14) {
			drop(ItemIdentifiers.DEATH_RUNE, NumberUtil.random(3, 6));
		} else if (rn < 15) {
			drop(ItemIdentifiers.GRIMY_RANARR);
		}
	}

	@Override
	public void dropCharms(Player player) {
		int rn = player.getRandom().nextInt(100);
		if (rn < 2) {
			drop(ItemIdentifiers.BLUE_CHARM); // 2%
		} else if (rn < 5) {
			drop(ItemIdentifiers.CRIMSON_CHARM); // 3%
		} else if (rn < 10) {
			drop(ItemIdentifiers.GREEN_CHARM); // 5%
		} else if (rn < 20) {
			drop(ItemIdentifiers.GOLD_CHARM); // 10%
			// 80% chance of no charm drop
		}
	}

	@Override
	public void drop(int id, int amount) {
		super.drop(id, amount);
	}

}
