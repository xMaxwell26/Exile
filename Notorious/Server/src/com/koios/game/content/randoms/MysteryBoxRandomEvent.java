package com.koios.game.content.randoms;

import com.koios.GameService;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Mystery box random event
 *
 * @author Mikey`
 */
public class MysteryBoxRandomEvent extends RandomEvent {

	public static boolean openMysteryBox(Player player, int id) {
		if (id == RANDOM_MYSTERY_BOX) {
			player.getInventory().remove(new Item(RANDOM_MYSTERY_BOX, 1));

			if (player.getInventory().getCount(RANDOM_MYSTERY_BOX) > 0) {
				player.sendMessage("You dispose of a mystery box (random event).");
				return true;
			}

			int item = -1;

			for (int i : RANDOM_EVENT_GIFTS) {
				if (!player.getInventory().contains(i) && !player.getEquipment().contains(id) && !player.getBank().contains(i)) {
					item = i;
				}
			}

			if (item > 0) {
				player.getInventory().add(new Item(item, 1));
			}
			return true;
		}

		return false;
	}

	/**
	 * Random event rewards TODO: make sure these equip correctly
	 */
	public static final int[] RANDOM_EVENT_GIFTS = { 6180, 6181, 6182, 6188, 6184, 6185, 6186, 6187, 6654, 6655, 6654, 3057, 3058, 3059, 3060, 3061, 7592, 7593, 7594, 7595, 7596 };

	public static final int RANDOM_MYSTERY_BOX = 6183;

	/**
	 * NPC ids
	 */
	public static final int[] RANDOM_NPCS = { 409, 3118, 2458 };

	@Override
	public void init(Player player) {
		int mobId = NumberUtil.random(MysteryBoxRandomEvent.RANDOM_NPCS);
		Mob mob = Mob.newGenericMob(mobId, new Location(player.getX(), player.getY(), player.getZ()));
		MobManager.spawn(mob);
		mob.summoner = player;

		GameService.getTaskScheduler().schedule(new Task(2) {

			byte stage = 0;

			@Override
			protected void execute() {
				if (stage == 0) {
					mob.speak("Greetings, " + player.getName() + "! Please have this gift.");
					mob.playAnimation(new Animation(863));
					mob.setFaceIndex(player.index(), true);
					mob.getFollowing().setFollow(player, FollowType.DEFAULT);
					player.getInventory().add(new Item(6183));

					GameService.getTaskScheduler().schedule(new Task(30) {

						@Override
						protected void execute() {
							if (player.isDisconnected() || !player.getInventory().contains(RANDOM_MYSTERY_BOX)) {
								stop();
								return;
							}

							if (player.getInventory().freeSlots() == 0) {
								return;
							}

							player.sendMessage("The random event mystery box duplicates.");
							player.getInventory().add(new Item(RANDOM_MYSTERY_BOX));
						}

					});
				} else if (stage == 1) {
					mob.playGraphics(new Graphic(86));
					mob.setDead(true);
					stop();
				}

				stage++;
			}

		});
	}

}
