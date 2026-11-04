package com.koios.game.content.skill.smithing;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.smithing.SmithingData.AnvilData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Smithing anvil action.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class SmithingAction extends SkillConstructor {

	private final Player player;
	private final Item item;
	private final Item bar;
	private final int amount;
	private boolean mustAbort, started;

	public SmithingAction(Player player, Item item, int amount) {
		super(3, false);
		this.player = player;
		this.item = item;
		this.amount = amount;
		bar = AnvilData.forItem(item.getId()).getReqItem();
	}

	@Override
	public boolean canWalk() {
		return false;
	}

	@Override
	public boolean canTeleport() {
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean start() {
		if (bar == null || item == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		player.playAnimation(new Animation(898));
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
		return true;
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
		return 0;
	}

	@Override
	protected void execute() {
		AnvilData a = AnvilData.forItem(item.getId());

		if (amount <= 0 || mustAbort || a == null || !player.getInventory().hasItem(bar) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		int amount = SmithingData.getFinishedAmount(a.toString().toLowerCase());
		player.sendMessage(amount == 1 ? "You make a " + item.getDefinition().getName() + "." : "You make " + amount + " " + item.getDefinition().getName() + ".");
		player.getInventory().remove(bar);
		player.getInventory().add(new Item(item.getId(), amount));
		player.getSkill().addExperience(Skill.SMITHING, a.getExperience() * Constants.SMITHING_EXPERIENCE);
		if (item.getId() == AnvilData.RUNE_SCIMITAR.getItem()) {
			AchievementManager.performed(player, Achievement.SMITH_SCIMITAR);
		}
		if (item.getId() == AnvilData.RUNE_ARROWTIPS.getItem()) {
			AchievementManager.performed(player, Achievement.CREATE_RUNE_ARROWS, amount);
		}
		player.playAnimation(new Animation(898));

	}

	/**
	 * Reads the input for smithing interface.
	 */
	public static void readInput(Player player, int itemId, int amount) {
		AnvilData a = AnvilData.forItem(itemId);

		if (a == null) {
			return;
		}

		if (!player.getInventory().contains(2347)) {
			player.sendMessage("You need a hammer to do this.");
			return;
		}

		if (a.getLevelReq() > player.getSkill().getLevel(Skill.SMITHING)) {
			player.sendMessage("You need a smithing level of " + a.getLevelReq() + " to make this.");
			return;
		}

		if (a.getReqItem().getCount() > player.getInventory().getCount(a.getReqItem().getId())) {
			player.sendMessage("You need more bars to create this!");
			return;
		}

		new SmithingAction(player, new Item(itemId), amount).start();
	}

	@Override
	public String getName() {
		return "SMITHING";
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
