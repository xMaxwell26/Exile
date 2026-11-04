package com.koios.game.content.skill.thieving;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class ThievingManager {

	private enum MobData {

		ALKHARID_WARRIOR(new int[] { 18 }, 25, new int[] { 995 }, 100, 150), ARDOUGNE_KNIGHT(new int[] { 26, 23 }, 55, new int[] { 995 }, NumberUtil.inclusiveRandom(100),
				250), BANDIT(new int[] { 1880 }, 55, new int[] { 995 }, NumberUtil.inclusiveRandom(5000), 200), FARMER(new int[] { 7 }, 10, new int[] { 995, 313, 314 }, 15,
						100), FREMENNIK_PERSON(new int[] { 1307, 1305, 1306, 1311, 1310, 1308, 1314 }, 45, new int[] { 995 }, NumberUtil.inclusiveRandom(200), 200), GUARD(
								new int[] { 9, 32 }, 40, new int[] { 995 }, 150, 200), HAM_FEMALE(new int[] { 1715 }, 15, new int[] { 4298, 4300, 4302, 4304, 4308, 4310 }, 1,
										50), HAM_MALE(new int[] { 1714 }, 20, new int[] { 4298, 4300, 4302, 4304, 4308, 4310 }, 1, 100), HERO(new int[] { 21 }, 80,
												new int[] { 995 }, NumberUtil.inclusiveRandom(800),
												400), MAN(new int[] { 1, 2, 3, 4 }, 1, new int[] { 995 }, 30, 50), MASTER_FARMER(new int[] { 2234 }, 38,
														new int[] { 5291, 5291, 5291, 5293, 5293, 5294, 5294, 5294, 5294, 5295, 5296, 5297, 5298, 5300, 5303, 5304, 5291, 5291,
																5291, 5291, 5293, 5294, 5294, 5291, 5291, 5292, 5292, 5292 },
														NumberUtil.inclusiveRandom(5), 150), PALADIN(new int[] { 20 }, 70, new int[] { 995 }, NumberUtil.inclusiveRandom(500),
																325), VILLAGER(new int[] { 1888 }, 45, new int[] { 995 }, NumberUtil.inclusiveRandom(2000),
																		100), WATCHMAN(new int[] { 34 }, 65, new int[] { 995 }, NumberUtil.inclusiveRandom(50), 300);

		private final int[] items;
		private final int[] npcId;
		private final int requirements;
		private final int amount;
		private final int xp;

		private MobData(int[] npcId, int requirements, int[] items, int amount, int xp) {
			this.npcId = npcId;
			this.requirements = requirements;
			this.items = items;
			this.amount = amount;
			this.xp = xp;
		}

		public int getAmount() {
			return amount;
		}

		public int[] getItems() {
			return items;
		}

		public int[] getNpcId() {
			return npcId;
		}

		public int getRequirements() {
			return requirements;
		}

		public int getXP() {
			return xp;
		}
	}

	public static MobData forMobData(int mobId) {
		for (MobData mobData : MobData.values()) {
			for (int id : mobData.getNpcId()) {
				if (id == mobId) {
					if (mobData != null) {
						return mobData;
					}
				}
			}
		}

		return null;
	}

	public static void initMobData(Player player, MobData data) {
		if (player.getInventory().freeSlots() == 0) {
			player.getDH().sendStatement("You do not have enough space in your inventory.");
			return;
		}

		if (player.getSkill().getLevel(Skill.THIEVING) < data.getRequirements()) {
			player.getDH().sendStatement("You need a Thieving level of " + data.getRequirements() + ".");
			return;
		}

		if (player.getSkill().getLevel(Skill.THIEVING) >= data.getRequirements()) {
			if (player.isBusy()) {
				return;
			}

			player.setBusy(true);

			GameService.getTaskScheduler().schedule(new Task() {

				int cycle = 0;

				@Override
				public void execute() {
					if (player.isDisconnected()) {
						stop();
						return;
					}

					int chance = 2 * player.getSkill().getLevel(Skill.THIEVING) - data.getRequirements();

					if (NumberUtil.inclusiveRandom(chance) == 1) {
						cycle++;
						player.sendMessage("You have been stunned.");
						player.playGraphics(new Graphic(80));
						player.playAnimation(new Animation(65535));
						stop();
						return;
					} else if (cycle == 0) {
						player.getSkill().addExperience(Skill.THIEVING, data.getXP());
						player.playAnimation(new Animation(881));
						player.getInventory().add(new Item(data.getItems()[NumberUtil.inclusiveRandom(data.getItems().length - 1)], NumberUtil.inclusiveRandom(data.getAmount())));
						AchievementManager.performed(player, Achievement.PICKPOCKET);
						stop();
					}
				}

				@Override
				public void onStop() {
					player.setBusy(false);
				}

			});
		}
	}

}
