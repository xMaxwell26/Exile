package com.koios.game.content.skill.crafting;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.crafting.CraftingData.CutGemData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Handles the gem cutting action of crafting.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CutGem extends SkillConstructor {

	public static boolean handleItems(Player player, int itemUsed, int useWith) {
		CutGemData c = CutGemData.forGem(itemUsed == 1755 ? useWith : itemUsed);

		if (c == null) {
			return false;
		}

		if (!player.getInventory().contains(1755)) {
			player.sendMessage("You need a chisel to make bolt tips.");
			return false;
		}

		if (player.getSkill().getLevel(Skill.CRAFTING) < c.getLevelReq()) {
			player.sendMessage("You need a Crafting level of " + c.getLevelReq() + " to cut this gem.");
			return false;
		}

		if (player.getCurrentSkill() != null) {
			player.getCurrentSkill().abort();
		}

		SkillManager.sendInterface(player, c.getCutGem());
		player.setCurrentSkill(new CutGem(player, c));
		return true;
	}

	private final Player player;
	private final CutGemData c;
	private int amount;
	private int anim;
	private boolean mustAbort, started;

	public CutGem(Player player, CutGemData c) {
		super(2, true);
		this.player = player;
		this.c = c;
	}

	@Override
	public boolean abort() {
		player.playAnimation(new Animation(65535));
		player.setCurrentSkill(null);
		player.setLastSkillAction(System.currentTimeMillis());
		super.stop();
		return true;
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
		if (mustAbort || amount <= 0 || c == null || !player.getInventory().contains(c.getUncutGem()) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		player.getInventory().remove(new Item(c.getUncutGem(), 1));
		player.getInventory().add(new Item(c.getCutGem(), 1));
		player.getSkill().addExperience(Skill.CRAFTING, c.getExperience() * Constants.CRAFTING_EXPERIENCE);
		player.sendMessage("You cut the " + new Item(c.getUncutGem()).getDefinition().getName() + ".");

		if (amount > 0) {
			player.playAnimation(new Animation(anim));
		}

		AchievementManager.performed(player, Achievement.CUT_GEMS);

		if (c.getUncutGem() == CutGemData.ONYX.getUncutGem()) {
			AchievementManager.performed(player, Achievement.CUT_ONYX);
		}

		amount--;
	}

	@Override
	public int getActionDelay() {
		return 500;
	}

	@Override
	public String getName() {
		return "CUTGEM";
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
		if (c == null || amount <= 0 || !SkillManager.canSkill(player, this)) {
			return false;
		}

		anim = c.getAnimation();
		player.playAnimation(new Animation(anim));
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
