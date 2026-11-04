package com.koios.game.content.skill.summoning.actions;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningTask;
import com.koios.game.content.skill.summoning.SummoningData.SummonPouch;
import com.koios.game.def.MobDefinition;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;

/**
 * Summons the familiar using the pouch.
 *
 * @author Gabriel | Wolfs Darker
 */
public class SummonFamiliar {

	public static void execute(Player player, int pouchId, boolean login) {
		try {
			SummonPouch pouch = SummonPouch.get(pouchId);

			if (pouch == null || player.getSummoning().getRemainingTicks() == 0 && login) {
				return;
			}

			Location l = Constants.getClearAdjacentLocation(player.getLocation(), MobDefinition.get(pouch.getFamiliarId()).getSize());

			if (l == null) {
				player.sendMessage("Not enough room to summon your familiar.");
				return;
			}

			MobFamiliar familiar = new MobFamiliar(pouch.getFamiliarId(), l);

			MobManager.spawnFamiliar(player, familiar);

			familiar.getFollowing().setFollow(player, FollowType.DEFAULT);

			familiar.playGraphics(new Graphic(familiar.getSize() <= 1 ? 1314 : 1315));

			if (pouch.getStorageSlots() > 0) {
				player.getSummoning().setFamiliarInventory(new Container(Container.Type.STANDARD, pouch.getStorageSlots()));
			}
			player.getSummoning().setFamiliarItem(pouchId);
			player.getSummoning().setFamiliar(familiar);
			player.getSummoning().setSpecialPoints(100);

			if (login && player.temporaryFamiliarInventory != null && player.temporaryFamiliarInventory.freeSlots() != player.temporaryFamiliarInventory.getCapacity()) {
				for (Item item : player.temporaryFamiliarInventory.toArray()) {
					if (item == null || item.getCount() <= 0) {
						continue;
					}
					player.getSummoning().getFamiliarInventory().add(item);
				}
			}

			if (!login) {
				player.getInventory().remove(new Item(pouch.getPouchId(), 1));
				player.getSummoning().setRemainingTicks(pouch.getDuration());
				player.getSkill().setLevel(Skill.SUMMONING, player.getSkill().getLevel(Skill.SUMMONING) - pouch.getPointsReq());
			}

			// player.getSession().send(new SendClientScriptPacket(1, "true-" + new
			// Item(familiar.getPouchDefinitions().getScrollId()).getDefinition().getName() + "-" +
			// SummoningData.castOnInventory(familiar)));
			player.getSummoning().sendTab(true);
			GameService.getTaskScheduler().schedule(new SummoningTask(player));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}