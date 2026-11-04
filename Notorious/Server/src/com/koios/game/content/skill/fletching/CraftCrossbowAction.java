package com.koios.game.content.skill.fletching;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.def.FletchingData.LogData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Fletchin craft crossbow action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CraftCrossbowAction extends SkillConstructor {

	private final Player player;
	private boolean mustAbort, started;
	private int amount;
	private LogData log = null;

	public CraftCrossbowAction(Player player, int itemUsed, int amount) {
		super(3, true);
		this.player = player;
		log = LogData.getLog(itemUsed);
		this.amount = amount;
	}

	@Override
	public boolean abort() {
		player.playAnimation(new Animation(65535));
		player.setCurrentSkill(null);
		player.setLastSkillAction(System.currentTimeMillis());
		super.stop();
		return false;
	}

	@Override
	public boolean canAbort() {
		return true;
	}

	@Override
	public boolean canTeleport() {
		return true;
	}

	@Override
	public boolean canWalk() {
		return true;
	}

	@Override
	protected void execute() {
		if (amount <= 0 || mustAbort || !player.getInventory().contains(log.getLogId()) || !player.getInventory().contains(946) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}
		player.getInventory().remove(new Item(log.getLogId(), 1));
		player.getInventory().add(new Item(log.getItem()[2], 1));
		player.getSkill().addExperience(Skill.FLETCHING, log.getExp()[2] * Constants.FLETCHING_EXPERIENCE);
		player.sendMessage("You carrefully cut the wood into a crossbow stock.");
		amount--;
		if (amount > 0) {
			player.playAnimation(new Animation(log.getAnimId()));
		}
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "CROSSBOWCRAFT";
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { log.getLogId() };
	}

	@Override
	public boolean hasRequeriments(Object[] object) {
		return false;
	}

	@Override
	public boolean mustAbort() {
		return mustAbort;
	}

	@Override
	public int setAmount(int amount) {
		return this.amount = amount;
	}

	@Override
	public boolean setMustAbort(boolean abort) {
		return mustAbort = abort;
	}

	@Override
	public Object[] setObjects(Object[] objects) {
		return null;
	}

	@Override
	public boolean start() {
		player.setCurrentSkill(null);

		if (amount <= 0 || log == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		if (player.getSkill().getLevel(Skill.FLETCHING) < log.getLevelReq()[2]) {
			player.sendMessage("You need a fletching level of " + log.getLevelReq()[2] + " to make this.");
			abort();
			return false;
		}

		player.playAnimation(new Animation(log.getAnimId()));
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
