package com.koios.game.content.skill.hunter.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.Hunter.Impling;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;
import com.koios.task.impl.MobDeathTask;

/**
 * @author Omicron
 */
@SuppressWarnings("rawtypes")
public final class CatchImpling extends EntityTask {

	private final Player player;
	private final Mob mob;
	private final Hunter.Impling impling;
	private final boolean hasNet;
	private int delay;
	private final int animation;

	@SuppressWarnings("unchecked")
	public CatchImpling(Player player, Mob mob, Hunter.Impling impling) {
		super(1, player);
		this.player = player;
		this.mob = mob;
		hasNet = player.getEquipment().get(Equipment.SLOT_WEAPON) != null
				&& (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 10010 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11259);
		this.impling = impling;
		// this.catchRate = (impling.getLevelRequired() * 2) /
		// player.getSkill().getLevel(Skill.HUNTER);
		animation = hasNet ? 6606 : 5083;
	}

	@Override
	protected void execute() {
		int code = executeCode();

		if (code == 0) {
			stop();
		}
	}

	public int executeCode() {
		if (mob == null || mob.isDead()) {
			player.setStopPacket(false);
			return 0;
		}

		if (delay > 0) {
			delay--;
			player.setStopPacket(false);
			return 0;
		}

		if (player.getSkill().getLevel(Skill.HUNTER) < impling.getLevelRequired()) {
			player.sendMessage("You need a Hunter level of " + impling.getLevelRequired() + " to catch this impling.");
			player.setStopPacket(false);
			return 0;
		}

		player.playAnimation(new Animation(animation));

		if (!Hunter.successfulCatch(impling.getLevelRequired(), player.getSkill().getLevel(Skill.HUNTER), hasNet)) {
			delay = 4;
			player.setStopPacket(false);
			return 0;
		}

		if (hasNet && player.getInventory().getCount(11260) != 0) {
			player.getInventory().remove(new Item(11260, 1));
			player.getInventory().add(new Item(impling.getJar()));
			player.sendMessage("You manage to catch the impling and place it in the jar.");
			player.setStopPacket(false);

			if (impling.getMobId() == Impling.KINGLY.getMobId()) {
				AchievementManager.performed(player, Achievement.CATCH_KINGLY_IMPLING);
			} else if (impling.getMobId() == Impling.BABY.getMobId()) {
				AchievementManager.performed(player, Achievement.CATCH_BABY_IMPLYING);
			}
		} else {
			Item item = impling.getRandomReward();

			if (!player.getInventory().add(new Item(item.getId(), item.getCount()))) {
				player.setStopPacket(false);
				return 0;
			}

			if (impling.getMobId() == Impling.KINGLY.getMobId()) {
				AchievementManager.performed(player, Achievement.CATCH_KINGLY_IMPLING);
			} else if (impling.getMobId() == Impling.BABY.getMobId()) {
				AchievementManager.performed(player, Achievement.CATCH_BABY_IMPLYING);
			}

			player.sendMessage("You manage to catch the impling and loot the reward.");
		}

		player.getSkill().addExperience(Skill.HUNTER, impling.getExperience() * Constants.HUNTER_EXPERIENCE);
		mob.actionTimer = impling.getLevelRequired() * 3;
		GameService.getTaskScheduler().schedule(new MobDeathTask(mob, 2));
		player.setStopPacket(false);
		return 0;
	}

}