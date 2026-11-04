package com.koios.game.content.skill.hunter.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;
import com.koios.task.impl.MobDeathTask;

/**
 * @author J
 */
@SuppressWarnings("rawtypes")
public final class CatchButterfly extends EntityTask {

	// 6601 - fly up
	// 6603 - swimming
	// 6608 - star opening jar
	// 603 - big player swinging

	private final Player player;
	private final Mob mob;
	private final Hunter.Butterfly butterfly;
	private final int hunterLevelRequired;
	private final boolean hasNet;
	private final int animation;
	private int delay;

	@SuppressWarnings("unchecked")
	public CatchButterfly(Player player, Mob mob, Hunter.Butterfly butterfly) {
		super(1, player);
		this.player = player;
		this.mob = mob;
		this.butterfly = butterfly;

		Container equipment = player.getEquipment();
		Item weapon = equipment.get(Equipment.SLOT_WEAPON);
		int weaponId = weapon == null ? -1 : weapon.getId();

		// init local values
		hasNet = weaponId == 10010 || weaponId == 11259;
		animation = hasNet ? 6606 : 5083;
		hunterLevelRequired = hasNet ? butterfly.getLevelRequired() : butterfly.getBarehandedHunterLevel();
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

		if (hasNet) {
			if (player.getSkill().getLevel(Skill.HUNTER) < hunterLevelRequired) {
				player.sendMessage("You need a Hunter level of " + hunterLevelRequired + " to catch this butterfly.");
				player.setStopPacket(false);
				return 0;
			}

			if (player.getInventory().getCount(10012) == 0) {
				player.sendMessage("You don't have a butterfly jar in which to keep a butterfly.");
				player.setStopPacket(false);
				return 0;
			}
		} else {
			if (player.getSkill().getLevel(Skill.HUNTER) < hunterLevelRequired) {
				player.sendMessage("You need a Hunter level of " + hunterLevelRequired + " to catch this butterfly barehanded.");
				player.setStopPacket(false);
				return 0;
			}

			if (player.getSkill().getLevel(Skill.AGILITY) < butterfly.getBarehandedAgilityLevel()) {
				player.sendMessage("You need an Agility level of " + butterfly.getBarehandedAgilityLevel() + " to catch this butterfly barehanded.");
				player.setStopPacket(false);
				return 0;
			}

			player.setStopPacket(false);
		}

		player.playAnimation(new Animation(animation));

		if (!Hunter.successfulCatch(hunterLevelRequired, player.getSkill().getLevel(Skill.HUNTER), hasNet)) {
			delay = 4;
			player.setStopPacket(false);
			return 0;
		}

		if (hasNet) {
			player.getSkill().addExperience(Skill.HUNTER, butterfly.getExperience() * Constants.HUNTER_EXPERIENCE);
			player.getInventory().remove(new Item(10012, 1));

			if (!player.getInventory().add(new Item(butterfly.getJar(), 1))) {
				player.setStopPacket(false);
				return 0;
			}

			player.sendMessage("You manage to catch the bufferly and place it in a jar.");
		} else {
			player.getSkill().addExperience(Skill.HUNTER, butterfly.getBarehandedAgilityExperience() * Constants.AGILITY_EXPERIENCE / 11);
			player.getSkill().addExperience(Skill.HUNTER, butterfly.getBarehandedHunterExperience() * Constants.HUNTER_EXPERIENCE);
			player.sendMessage("You manage to catch the bufferly and release it back into the wild.");
		}

		player.setStopPacket(false);
		mob.actionTimer = butterfly.getLevelRequired() * 3;
		GameService.getTaskScheduler().schedule(new MobDeathTask(mob, 2));
		return 0;
	}

}
