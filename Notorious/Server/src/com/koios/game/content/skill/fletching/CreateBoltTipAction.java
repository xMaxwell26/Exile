package com.koios.game.content.skill.fletching;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.fletching.FletchingData.BoltTip;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Fletching create bolt tip action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CreateBoltTipAction extends SkillConstructor {

	/**
	 * Checks if player can make a bolt tip.
	 */
	public static boolean canMakeTip(Player player, int itemUsed, int useWith) {
		BoltTip def = BoltTip.forGem(itemUsed == 1755 ? useWith : itemUsed);

		if (def == null) {
			return false;
		}

		if (!player.getInventory().contains(1755)) {
			player.sendMessage("You need a chisel to make bolt tips.");
			return false;
		}

		if (player.getSkill().getLevel(Skill.FLETCHING) < def.getLevelReq()) {
			player.sendMessage("You need a Fletching level of " + def.getLevelReq() + " to make this.");
			return false;
		}

		player.setCurrentSkill(new CreateBoltTipAction(player, itemUsed == 1755 ? useWith : itemUsed));
		SkillManager.sendInterface(player, def.getTipId());
		return true;
	}

	private final Player player;
	private boolean mustAbort, started;
	private int amount;
	private BoltTip b = null;

	public CreateBoltTipAction(Player player, int itemUsed) {
		super(3, true);
		this.player = player;
		b = BoltTip.forGem(itemUsed);
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
		if (amount <= 0 || mustAbort || !player.getInventory().contains(b.getGemId()) || !player.getInventory().contains(1755) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		player.getInventory().remove(new Item(b.getGemId(), 1));
		player.getInventory().add(new Item(b.getTipId(), 12));
		player.getSkill().addExperience(Skill.FLETCHING, b.getXpAmount() * Constants.FLETCHING_EXPERIENCE);
		player.sendMessage("You use your chisel to make small bolt tips.");

		if (--amount > 0) {
			player.playAnimation(new Animation(b.getAnimationId()));
		}
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "CREATEBOLTTIP";
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

		player.playAnimation(new Animation(b.getAnimationId()));
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
