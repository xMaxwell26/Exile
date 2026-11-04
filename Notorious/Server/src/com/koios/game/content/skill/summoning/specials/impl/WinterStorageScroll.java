package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;

/**
 * Handles the pack yak's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class WinterStorageScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return false;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		if (extra_data == null || extra_data.length < 2) {
			return false;

		}

		int slot = (int) extra_data[0];
		int id = (int) extra_data[1];

		Item item = player.getInventory().get(slot);

		if (item == null || item.getCount() <= 0 || item.getId() != id) {
			return false;
		}

		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		int slot = (int) extra_data[0];

		Item item = player.getInventory().get(slot);

		if (!Bank.addToBank(player, new Item(item.getId(), 1))) {
			player.sendMessage("Error while banking: " + item.getDefinition().getName() + ".");
			return false;
		} else {
			player.getInventory().remove(slot, new Item(item.getId(), 1));
		}

		player.playGraphics(new Graphic(1316));
		player.playAnimation(new Animation(7660));

		return true;
	}

}
