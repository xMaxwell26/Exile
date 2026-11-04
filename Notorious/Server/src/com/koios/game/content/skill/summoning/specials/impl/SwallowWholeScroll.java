package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.game.content.Food.FoodDefinition;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.cooking.CookingData.CookItemData;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Handles the bunyip's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class SwallowWholeScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return false;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		if (extra_data.length < 2) {
			return false;
		}

		int inv_slot = (int) extra_data[0];
		int item_id = (int) extra_data[1];

		Item inv_item = player.getInventory().get(inv_slot);

		if (inv_item == null || inv_item.getCount() <= 0 || inv_item.getId() != item_id) {
			return false;
		}

		CookItemData cook = CookItemData.forFood(inv_item.getId());

		if (cook == null) {
			player.sendMessage("You must use it in a raw food!");
			return false;
		}

		FoodDefinition food = FoodDefinition.get(cook.getCookedFoodId());

		if (food == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		int inv_slot = (int) extra_data[0];

		Item inv_item = player.getInventory().get(inv_slot);

		FoodDefinition food = FoodDefinition.get(CookItemData.forFood(inv_item.getId()).getCookedFoodId());

		player.getSkill().incrementLevel(Skill.HITPOINTS, food.getHeal());

		if (player.getSkill().getLevel(Skill.HITPOINTS) > player.getMaxHitpoints()) {
			player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints());
		}

		player.sendMessage("Your familiar eats the " + ItemDefinition.forId(inv_item.getId()).getName().toLowerCase() + " and heals you.");
		player.getInventory().remove(inv_slot, new Item(inv_item.getId(), 1));
		player.playGraphics(new Graphic(1316));
		player.playAnimation(new Animation(7660));

		return true;
	}

}
