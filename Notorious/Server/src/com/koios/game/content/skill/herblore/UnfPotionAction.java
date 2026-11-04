package com.koios.game.content.skill.herblore;

import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.herblore.HerbloreData.UnfData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Create unfinished potion action.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class UnfPotionAction extends SkillConstructor {

	private final Player player;
	private UnfData u = null;
	private boolean mustAbort, started;
	private int amount;

	public UnfPotionAction(Player p, UnfData u) {
		super(2, true);
		player = p;
		this.u = u;
	}

	@Override
	public boolean canWalk() {
		return true;
	}

	@Override
	public boolean canTeleport() {
		return true;
	}

	@Override
	public boolean canAbort() {
		return true;
	}

	@Override
	public boolean mustAbort() {
		return mustAbort;
	}

	@Override
	public boolean setMustAbort(boolean abort) {
		return mustAbort = abort;
	}

	@Override
	public boolean hasRequeriments(Object[] object) {
		return false;
	}

	@Override
	public boolean start() {
		if (amount <= 0 || u == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return false;
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
	public int getActionDelay() {
		return 600;
	}

	@Override
	public Object[] getObjects() {
		return null;
	}

	@Override
	public Object[] setObjects(Object[] objects) {
		return null;
	}

	@Override
	public int setAmount(int amount) {
		return this.amount = amount;
	}

	@Override
	protected void execute() {
		if (amount <= 0 || mustAbort || !player.getInventory().hasItem(new Item(u.getWeedId(), 1)) || !player.getInventory().hasItem(new Item(227, 1))
				|| !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		player.playAnimation(new Animation(363));
		player.getInventory().remove(new Item(u.getWeedId(), 1));
		player.getInventory().remove(new Item(227, 1));
		player.getInventory().add(new Item(u.getUnfId(), 1));
		player.sendMessage("You mix the ingredients and make a " + new Item(u.getUnfId()).getDefinition().getName().toLowerCase() + ".");
		amount--;
	}

	/**
	 * Checks if the player can create a herblore potion.
	 */
	public static boolean canCreatePotion(Player player, int itemUsed, int usedWith) {
		UnfData def = UnfData.forItemId(itemUsed != 227 ? itemUsed : usedWith);

		if (def == null) {
			return false;
		}

		if (itemUsed != def.getWeedId() && itemUsed != 227 && usedWith != def.getWeedId() && usedWith != 227) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.HERBLORE) < def.getLevelReq()) {
			SimpleDialogues.sendStatement(player, "You need a herblore level of " + def.getLevelReq() + " to make this potion.");
			return false;
		}

		if (!player.getInventory().hasItem(new Item(def.getWeedId(), 1)) && !player.getInventory().hasItem(new Item(227, 1))) {
			SimpleDialogues.sendStatement(player, "You don't have all required items to do this.");
			return false;
		}

		player.setCurrentSkill(new UnfPotionAction(player, def));
		SkillManager.sendInterface(player, def.getUnfId());
		return true;
	}

	@Override
	public String getName() {
		return "UNFPOTION";
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
