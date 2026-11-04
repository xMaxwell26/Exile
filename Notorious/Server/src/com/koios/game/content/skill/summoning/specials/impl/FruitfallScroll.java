package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.GameService;
import com.koios.game.content.skill.summoning.SummoningData;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Handles the fruitbat's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class FruitfallScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return false;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		MobFamiliar familiar = player.getSummoning().getFamiliar();

		player.playGraphics(new Graphic(1316));
		player.playAnimation(new Animation(7660));
		familiar.playAnimation(new Animation(8320));

		GameService.getTaskScheduler().schedule(new Task(2) {

			@Override
			public void execute() {
				Item[] items = new Item[3 + NumberUtil.inclusiveRandom(5)];
				for (int i = 0; i < items.length; i++) {
					items[i] = SummoningData.FRUITFALL_ITEMS[NumberUtil.inclusiveRandom(SummoningData.FRUITFALL_ITEMS.length - 1)];
				}
				for (Item item : items) {
					if (item == null) {
						continue;
					}
					int x = NumberUtil.inclusiveRandom(2);
					int y = NumberUtil.inclusiveRandom(2);
					Location loc = familiar.getLocation().transform(x == 2 ? -1 : x, y == 2 ? -1 : y, 0);
					Graphic.createGlobalGraphic(1331, loc.getX(), loc.getY(), 0, 0);
					GroundItemManager.spawnNew(player, item.getId(), loc.getX(), loc.getY(), item.getCount(), true);
				}
				stop();
			}

		});

		return true;
	}

}
