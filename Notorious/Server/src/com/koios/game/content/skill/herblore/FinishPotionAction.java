package com.koios.game.content.skill.herblore;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.herblore.HerbloreData.IngredientData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Finish potions action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class FinishPotionAction extends SkillConstructor {

	/**
	 * Checks if the player can create a herblore potion.
	 */
	public static boolean canCreatePotion(Player p, int itemUsed, int usedWith) {
		IngredientData i = IngredientData.forPot(itemUsed, usedWith);

		if (i == null) {
			return false;
		}

		if (p.getSkill().getLevel(Skill.HERBLORE) < i.getLevelReq()) {
			SimpleDialogues.sendStatement(p, "You need a herblore level of " + i.getLevelReq() + " to make this potion.");
			return false;
		}

		for (Item items : i.getRequiredItems()) {
			if (!p.getInventory().hasItem(items)) {
				SimpleDialogues.sendStatement(p, "You don't have all required items to do this.");
				return false;
			}
		}

		p.setCurrentSkill(new FinishPotionAction(p, i));
		SkillManager.sendInterface(p, i.getFinishedPotion().getId());
		return true;
	}

	private final Player player;
	private IngredientData i = null;
	private boolean mustAbort, started;
	private int amount;

	public FinishPotionAction(Player p, IngredientData i) {
		super(2, true);
		player = p;
		this.i = i;
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

		if (amount <= 0 || mustAbort || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		for (Item items : i.getRequiredItems()) {
			if (!player.getInventory().hasItem(items)) {
				SimpleDialogues.sendStatement(player, "You don't have all required items to do this amount!");
				abort();
				return;
			}

		}

		for (Item item : i.getRequiredItems()) {
			player.getInventory().remove(new Item(item.getId(), item.getCount()));
		}

		player.playAnimation(new Animation(363));
		player.getSkill().addExperience(Skill.HERBLORE, i.getExpGained() * Constants.HERBLORE_EXPERIENCE);
		player.getInventory().add(i.getFinishedPotion());
		player.sendMessage("You mix the ingredients and make a " + i.getFinishedPotion().getDefinition().getName().toLowerCase() + ".");
		AchievementManager.performed(player, Achievement.MAKE_POTION);
		amount--;
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "FINISHPOTION";
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
		if (amount <= 0 || i == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		started = true;
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
