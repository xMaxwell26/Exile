package com.koios.game.content.shop.dynamic;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.koios.game.content.shop.Shop;
import com.koios.game.content.shop.ShopType;
import com.koios.game.content.shop.currency.CoinsItemCurrency;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.Skillcape;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public final class SkillcapeShop extends Shop {

	public SkillcapeShop() {
		super(81, "Skillcapes", ShopType.SELL_ONLY, new CoinsItemCurrency(), buildStock(), true);
	}

	private static Item[] buildStock() {
		List<Item> items = Lists.newArrayList();
		for (Skillcape skillcape : Skillcape.values()) {
			items.add(skillcape.getTrimmedCape());
		}
		return Iterables.toArray(items, Item.class);
	}

	@Override
	public boolean sellItem(Player player, int id, int amount, int slot) {
		Skillcape skillcape = Skillcape.get(id);
		if (skillcape == null) {
			return false;
		}

		if (player.getInventory().freeSlots() < 2) {
			player.sendMessage("You need at least 2 free slots to purchase a skillcape and hood!");
			return false;
		}

		if (player.getSkill().getLevelForExperience(skillcape.getSkillId()) != 99) {
			player.sendMessage("You have not mastered the " + Skill.SKILL_NAME[skillcape.getSkillId()] + " skill!");
			return false;
		}

		if (!player.getInventory().contains(new Item(995, 99_000 * amount))) {
			player.sendMessage("You do not have enough coins to buy this skillcape.");
			return false;
		}

		boolean moreThanOne99 = player.getSkill().hasMoreThanOne99();
		Item cape = moreThanOne99 ? skillcape.getTrimmedCape() : skillcape.getUntrimmedCape();
		player.getInventory().remove(new Item(995, 99_000 * amount));
		player.getInventory().add(cape);
		player.getInventory().add(skillcape.getHood());

		return true;
	}

	@Override
	public boolean isRegulated() {
		return false;
	}

}