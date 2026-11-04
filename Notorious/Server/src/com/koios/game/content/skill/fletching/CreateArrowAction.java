package com.koios.game.content.skill.fletching;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.fletching.FletchingData.ArrowData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Fletching create arrow action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CreateArrowAction extends SkillConstructor {

	/**
	 * Checks if the player can create bolts.
	 */
	public static boolean canCreateBolts(Player player, int itemUsed, int useWith) {

		ArrowData def = ArrowData.forArrow(itemUsed == 314 || itemUsed == 53 ? useWith : itemUsed);

		if (def == null) {
			return false;
		}

		if (itemUsed != def.getHeadId() && itemUsed != def.getShaftId() && useWith != def.getHeadId() && useWith != def.getShaftId()) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.FLETCHING) < def.getLevelReq()) {
			SimpleDialogues.sendStatement(player, "You need a Fletching level of " + def.getLevelReq() + " to make this.");
			return false;
		}

		if (!player.getInventory().contains(def.getHeadId()) && !player.getInventory().contains(def.getShaftId())) {
			SimpleDialogues.sendStatement(player, "You don't have all required items to do this.");
			return false;
		}

		player.setCurrentSkill(new CreateArrowAction(player, def.getHeadId()));
		SkillManager.sendInterface(player, def.getArrowId());
		return true;
	}

	private final Player player;
	private boolean mustAbort, started;
	private int amount;
	private ArrowData a = null;

	public CreateArrowAction(Player player, int itemUsed) {
		super(3, true);
		this.player = player;
		a = ArrowData.forArrow(itemUsed);
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
		if (amount <= 0 || mustAbort || !player.getInventory().contains(a.getHeadId()) || !player.getInventory().contains(a.getShaftId()) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		int itemAmount = player.getInventory().getCount(a.getShaftId());

		if (itemAmount > 15) {
			itemAmount = 15;
		}

		if (player.getInventory().getCount(a.getShaftId()) < itemAmount) {
			itemAmount = player.getInventory().getCount(a.getShaftId());
		}

		player.getInventory().remove(new Item(a.getHeadId(), itemAmount));
		player.getInventory().remove(new Item(a.getShaftId(), itemAmount));
		player.getInventory().add(new Item(a.getArrowId(), itemAmount));
		player.getSkill().addExperience(Skill.FLETCHING, a.getExpReceived() * Constants.FLETCHING_EXPERIENCE);
		amount--;
		if (amount <= 0) {
			abort();
		}
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "CREATEARROW";
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
		if (a == null || !SkillManager.canSkill(player, this)) {
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