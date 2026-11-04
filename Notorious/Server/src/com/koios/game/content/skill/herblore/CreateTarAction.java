package com.koios.game.content.skill.herblore;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.herblore.HerbloreData.TarData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Herblore's create tar action.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class CreateTarAction extends SkillConstructor {

	/**
	 * Checks if the player can create a tar.
	 */
	public static boolean canCreateTar(Player player, int itemUsed, int useWith) {
		TarData t = TarData.forItemId(itemUsed == 1939 ? useWith : itemUsed);

		if (t == null) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.HERBLORE) < t.getLevelReq()) {
			SimpleDialogues.sendStatement(player, "You need a Herblore level of " + t.getLevelReq() + "  to make this.");
			return false;
		}

		if (!player.getInventory().hasItem(new Item(t.getWeedId())) && !player.getInventory().hasItem(new Item(1939))) {
			SimpleDialogues.sendStatement(player, "You don't have all required items to do this.");
			return false;
		}

		if (!player.getInventory().hasItem(new Item(233))) {
			player.sendMessage("You need a pestle and mortar to mix " + t.getWeedName() + " with swamp tar.");
			return false;
		}

		player.setCurrentSkill(new CreateTarAction(player, t));
		SkillManager.sendInterface(player, t.getTarId());
		return true;
	}

	private int amount;
	private boolean mustAbort, started;
	private final Player player;
	private TarData t = null;

	public CreateTarAction(Player player, TarData t) {
		super(2, true);
		this.player = player;
		this.t = t;
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
		if (amount <= 0 || mustAbort || !player.getInventory().hasItem(new Item(t.getWeedId(), 1)) || !player.getInventory().hasItem(new Item(1939, 1))
				|| !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		player.playAnimation(new Animation(364));
		int amount = player.getInventory().getItemAmount(1939);
		player.getInventory().remove(new Item(t.getWeedId(), 1));
		player.getInventory().remove(new Item(1939, amount > 15 ? 15 : amount));
		player.getSkill().addExperience(Skill.HERBLORE, t.getExperience() * Constants.HERBLORE_EXPERIENCE);
		player.getInventory().add(new Item(t.getTarId(), amount > 15 ? 15 : amount));
		player.sendMessage("You mix the " + t.getWeedName() + " into the swamp tar.");
		amount--;
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "TARCREATION";
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
		if (amount <= 0 || t == null || !SkillManager.canSkill(player, this)) {
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
