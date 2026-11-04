package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Handles the abyssal titan's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class EssenceShipmentScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return false;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		int essenceCount = player.getInventory().getCount(1436);
		int pureEssenceCount = player.getInventory().getCount(7936);
		if (essenceCount <= 0 && pureEssenceCount <= 0) {
			player.sendMessage("You need to have rune or pure essences to cast this scroll.");

			return false;
		}

		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		int essenceCount = player.getInventory().getCount(1436);
		int pureEssenceCount = player.getInventory().getCount(7936);

		player.playAnimation(new Animation(7660));
		player.playGraphics(new Graphic(1308));
		player.getSummoning().getFamiliar().playAnimation(new Animation(7698));
		player.getSummoning().getFamiliar().playGraphics(new Graphic(1457));
		if (essenceCount > 0) {
			player.getBank().add(new Item(1436, essenceCount));
			player.getInventory().remove(new Item(1436, essenceCount));
		}
		if (pureEssenceCount > 0) {
			player.getBank().add(new Item(7936, pureEssenceCount));
			player.getInventory().remove(new Item(7936, pureEssenceCount));
		}
		return true;
	}

}
