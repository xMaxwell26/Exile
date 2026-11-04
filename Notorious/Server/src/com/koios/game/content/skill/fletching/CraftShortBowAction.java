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
 * SKills: Fletching create shortbow action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CraftShortBowAction extends SkillConstructor {

	/**
	 * Handle item on item to check if you can create a shortbow.
	 */
	public static boolean handleItems(Player p, int itemUsed, int useWith) {
		LogData log = LogData.getLog(itemUsed == 946 ? useWith : itemUsed);

		if (p.getCurrentSkill() != null) {
			p.getCurrentSkill().abort();
		}

		if (log == null || itemUsed != 946 && useWith != 946) {
			return false;
		}

		p.setCurrentSkill(new CraftShortBowAction(p, itemUsed == 946 ? useWith : itemUsed, 0));
		SkillManager.sendFletchingInterface(p, log);
		return false;
	}

	private final Player player;
	private boolean mustAbort, started;
	private int amount;
	private LogData log = null;

	public CraftShortBowAction(Player player, int itemUsed, int amount) {
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
		player.getInventory().add(new Item(log.getItem()[0], 1));
		player.getSkill().addExperience(Skill.FLETCHING, log.getExp()[0] * Constants.FLETCHING_EXPERIENCE);
		player.sendMessage("You carrefully cut the wood into a shortbow.");

		if (--amount > 0) {
			player.playAnimation(new Animation(log.getAnimId()));
		}
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "CRAFTSHORTBOW";
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

		if (player.getSkill().getLevel(Skill.FLETCHING) < log.getLevelReq()[0]) {
			player.sendMessage("You need a Fletching level of " + log.getLevelReq()[0] + " to make this.");
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