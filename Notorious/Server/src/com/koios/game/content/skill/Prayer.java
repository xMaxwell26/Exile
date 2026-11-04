package com.koios.game.content.skill;

import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * @author Tringan
 */
public class Prayer extends SkillHandler {

	public enum DemonHornNecklaceBones {
		SMALL_BONES(5, 526, 528, 530), MEDIUM_BONES(10, 532, 534, 6812), LARGE_BONES(15, 536, 18830, 4834);
		private final int[] ids;
		private final int prayer;

		DemonHornNecklaceBones(int prayer, int... ids) {
			this.prayer = prayer;
			this.ids = ids;
		}

		private static Map<Integer, DemonHornNecklaceBones> values = new HashMap<Integer, DemonHornNecklaceBones>();

		static {
			for (DemonHornNecklaceBones b : values()) {
				for (int i : b.ids) {
					values.put(i, b);
				}
			}
		}

		public int getPrayer() {
			return prayer;
		}

		public static DemonHornNecklaceBones forId(int id) {
			return values.get(id);
		}
	}

	public enum BoneDefinition {

		BABY_DRAGON_BONES(534, 30), BAT_BONES(530, 5), BIG_BONES(532, 15), BONES(526, 5), BURNT_BONES(528, 5), BURNT_JOGRE_BONES(3127, 25), DAGANNOTH_BONES(6729,
				75), DRAGON_BONES(536, 55), FAYGR_BONES(4830, 54), FROST_DRAGON_BONES(18830, 100), JOGRE_BONES(3125, 23), MONKEY_BONES(3179, 5), MONKEY_BONES_2(3180,
						5), MONKEY_BONES_3(3181, 5), MONKEY_BONES_4(3182, 5), MONKEY_BONES_5(3183, 5), MONKEY_BONES_6(3184, 5), MONKEY_BONES_7(3185, 5), MONKEY_BONES_8(3186,
								5), MONKEY_BONES_9(3187, 5), OURG_BONES(4834, 80), PLAYER_BONES(2530, 5), RAURG_BONES(4832, 56), SHAIKAHAN_BONES(3123, 25), WOLF_BONES(2859,
										5), WYVERN_BONES(6812, 35), ZOGRE_BONES(4812, 42);

		public static BoneDefinition forId(int id) {
			return cache.get(Integer.valueOf(id));
		}

		private final int id, experience;

		private static final Map<Integer, BoneDefinition> cache;

		static {
			cache = new HashMap<>(BoneDefinition.values().length);

			for (BoneDefinition definition : BoneDefinition.values()) {
				cache.put(definition.id, definition);
			}
		}

		/**
		 * Constructor for the prayer data.
		 */
		private BoneDefinition(int id, int experience) {
			this.id = id;
			this.experience = experience;
		}

		/**
		 * Gets the ammount of Exp gained per bone/ashe
		 */
		public int getExperience() {
			return experience;
		}

		/**
		 * Gets the itemID(bones/ashes)
		 */
		public int getId() {
			return id;
		}

	}

	public static void buryAltarBone(Player player, int id, int x, int y, int cycles) {
		BoneDefinition definition = BoneDefinition.forId(id);

		if (definition == null) {
			return;
		}

		if (id == definition.getId() && player.getInventory().contains(id) && player.getX() >= x - 1 && player.getX() <= x + 2 && player.getY() >= y - 1
				&& player.getY() <= y + 1) {
			player.playerSkilling[5] = true;

			GameService.getTaskScheduler().schedule(new Task(4, true) {

				private int count = 0;

				@Override
				public void execute() {
					if (player.isDisconnected()) {
						stop();
						return;
					}

					if (count == cycles) {
						stop();
						return;
					}

					if (!player.getInventory().contains(id) || !player.playerSkilling[5]
							|| !(player.getX() >= x - 1 && player.getX() <= x + 2 && player.getY() >= y - 1 && player.getY() <= y + 1)) {
						stop();
						return;
					}

					if (player.playerSkilling[5]) {
						placeBonesOnAltar(player, id, x, y, definition);
						count++;
					}
				}

				@Override
				public void onStop() {
					player.playerSkilling[5] = false;
					player.playAnimation(new Animation(65535));
				}

			});
		}
	}

	public static void buryBone(Player player, int id, int slot) {
		BoneDefinition definition = BoneDefinition.forId(id);

		if (System.currentTimeMillis() - player.buryDelay > 1500) {
			if (BoneDefinition.DRAGON_BONES.getId() == id) {
				AchievementManager.performed(player, Achievement.BURY_DRAGON_BONES);
			}

			DemonHornNecklaceBones b = DemonHornNecklaceBones.forId(id);
			if (b != null && player.getSkill().getLevel(Skill.PRAYER) != player.getSkill().getLevelForExperience(Skill.PRAYER)) {
				int restore = b.getPrayer();
				if (player.getSkill().getLevel(Skill.PRAYER) + restore > player.getSkill().getLevelForExperience(Skill.PRAYER)) {
					player.getSkill().restoreLevel(Skill.PRAYER);
				} else {
					player.getSkill().incrementLevel(Skill.PRAYER, restore);
				}
			}

			player.getInventory().remove(slot, new Item(id, 1));
			player.getSkill().addExperience(Skill.PRAYER, definition.getExperience() * Constants.PRAYER_EXPERIENCE);
			player.playAnimation(new Animation(827));
			player.sendMessage("You bury the " + ItemDefinition.forId(id).getName().toLowerCase() + ".");
			AchievementManager.performed(player, Achievement.BURY_BONES);
			player.buryDelay = System.currentTimeMillis();
		}
	}

	private static void placeBonesOnAltar(Player player, final int itemID, final int altarX, final int altarY, BoneDefinition definition) {
		player.setFacePosition(altarX, altarY);
		player.getInventory().remove(new Item(itemID, 1));
		player.getSkill().addExperience(Skill.PRAYER, definition.getExperience() * 2 * Constants.PRAYER_EXPERIENCE);
		player.playAnimation(new Animation(896));
		Graphic.createGlobalGraphic(624, altarX, altarY, 0, 0);
		player.sendMessage("The gods are pleased with your offerings.");
	}

	public static void sendPrayerInterface(Player player, int item, int altarX, int altarY) {
		if (player.playerSkilling[5]) {
			return;
		}

		BoneDefinition definition = BoneDefinition.forId(item);

		if (definition != null && item == definition.getId()) {
			if (!player.getInventory().contains(item)) {
				return;
			}

			if (player.getInventory().hasItem(new Item(item)) && !player.getInventory().hasItem(new Item(item, 2))) {
				buryAltarBone(player, item, altarX, altarY, 1);
				return;
			}

			player.dialogue().start(new Dialogue(player) {
				@Override
				protected void start() {
					sendMakeItemChat(definition.getId(), 150, ItemDefinition.forId(definition.getId()).getName());
				}

				@Override
				protected void select(int index) {
					stop();

					if (index == 1) {
						buryAltarBone(player, item, altarX, altarY, 1);
					} else if (index == 2) {
						buryAltarBone(player, item, altarX, altarY, 5);
					} else if (index == 3) {
						takeValueInput();
					} else if (index == 4) {
						buryAltarBone(player, item, altarX, altarY, player.getInventory().getCount(item));
					}
				}

				@Override
				protected void input(int value) {
					buryAltarBone(player, item, altarX, altarY, value);
				}
			});
		}
	}

}
