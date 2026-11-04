package com.koios.game.content.skill.fletching;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.fletching.FletchingData.BoltData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Fletching's create bolt action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CreateBoltAction extends SkillConstructor {

	/**
	 * Checks if the player can create bolts.
	 */
	public static boolean canCreateBolts(Player player, int itemUsed, int useWith) {
		BoltData def = BoltData.forBolt(itemUsed);

		if (def == null) {
			def = BoltData.forBolt(useWith);

			if (def == null) {
				return false;
			}
		}

		if (itemUsed != def.getFirstItemId() && itemUsed != def.getSecondItemId() || useWith != def.getFirstItemId() && useWith != def.getSecondItemId()) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.FLETCHING) < def.getLevelReq()) {
			SimpleDialogues.sendStatement(player, "You need a Fletching level of " + def.getLevelReq() + " to make this.");
			return false;
		}

		if (!player.getInventory().contains(def.getFirstItemId()) && !player.getInventory().contains(def.getSecondItemId())) {
			SimpleDialogues.sendStatement(player, "You don't have all required items to do this.");
			return false;
		}

		player.setCurrentSkill(new CreateBoltAction(player, def.getFirstItemId()));
		SkillManager.sendInterface(player, def.getBoltId());
		return true;
	}

	private final Player player;
	private boolean mustAbort, started;
	private int amount;
	private BoltData b = null;

	public CreateBoltAction(Player player, int itemUsed) {
		super(3, true);
		this.player = player;
		b = BoltData.forBolt(itemUsed);
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
		if (amount <= 0 || mustAbort || !player.getInventory().contains(b.getFirstItemId()) || !player.getInventory().contains(b.getSecondItemId())
				|| !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		int itemAmount = player.getInventory().getCount(b.getFirstItemId());

		if (player.getInventory().getCount(b.getSecondItemId()) < itemAmount) {
			itemAmount = player.getInventory().getCount(b.getSecondItemId());
		}

		if (itemAmount > 10) {
			itemAmount = 10;
		}

		player.getInventory().remove(new Item(b.getFirstItemId(), itemAmount));
		player.getInventory().remove(new Item(b.getSecondItemId(), itemAmount));
		player.getInventory().add(new Item(b.getBoltId(), itemAmount));
		player.getSkill().addExperience(Skill.FLETCHING, b.getExpReceived() * itemAmount * Constants.FLETCHING_EXPERIENCE);
		amount--;
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "CREATEBOLT";
	}

	@Override
	public Object[] getObjects() {
		return null;
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
		if (b == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
