package com.koios.game.content.skill.fletching;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.def.FletchingData.StringData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class StringWeaponAction extends SkillConstructor {

	/**
	 * Checks if the player can finish the weapon.
	 */
	public static boolean canFinishWeapon(Player player, int itemUsed, int useWith) {
		StringData string = StringData.getItem(itemUsed == 1777 ? useWith : itemUsed);

		if (string == null || itemUsed != 1777 && useWith != 1777) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.FLETCHING) < string.getLevelReq()) {
			SimpleDialogues.sendStatement(player, "You need a Fletching level of " + string.getLevelReq() + " to make this.");
			return false;
		}

		if (!player.getInventory().hasItem(new Item(string.getUnfItem(), 1)) && !player.getInventory().hasItem(new Item(1777, 1))) {
			player.sendMessage("You don't have all required items to do this.");
			return false;
		}

		player.setCurrentSkill(new StringWeaponAction(player, string.getUnfItem()));
		SkillManager.sendInterface(player, string.getFinalItem());
		return false;
	}

	private final Player player;
	private boolean mustAbort, started;
	private int amount;
	private StringData string = null;

	public StringWeaponAction(Player player, int itemUsed) {
		super(3, true);
		this.player = player;
		string = StringData.getItem(itemUsed);
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
		if (amount <= 0 || mustAbort || !player.getInventory().contains(string.getUnfItem()) || !player.getInventory().contains(1777) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		player.getInventory().remove(new Item(string.getUnfItem(), 1));
		player.getInventory().remove(new Item(1777, 1));
		player.getInventory().add(new Item(string.getFinalItem(), 1));
		player.getSkill().addExperience(Skill.FLETCHING, string.getExp() * Constants.FLETCHING_EXPERIENCE);
		String name = new Item(string.getUnfItem()).getDefinition().getName().toLowerCase().contains("crossbow")
				|| new Item(string.getUnfItem()).getDefinition().getName().toLowerCase().contains("c'bow") ? "crossbow" : "bow";
		player.sendMessage("You add a string to the " + name + ".");

		if (--amount > 0) {
			player.playAnimation(new Animation(string.getAnimId()));
		}
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "STRINGWEAPON";
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
		if (string == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		player.playAnimation(new Animation(string.getAnimId()));
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}