package com.koios.game.content.skill.smithing;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.smithing.SmithingData.SmeltingData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendItemOnInterfacePacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * Skills: Smithing's smelting action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class SmeltingAction extends SkillConstructor {

	private final Player player;
	private final SmeltingData s;
	private final int itemUsed;
	private int amount;
	private boolean mustAbort, started;

	public SmeltingAction(Player player, int itemUsed, int amount) {
		super(3, true);
		this.player = player;
		this.itemUsed = itemUsed;
		this.amount = amount;
		s = SmeltingData.forBar(itemUsed);
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
		if (s == null || !hasOres(player, itemUsed) || !SkillManager.canSkill(player, this)) {
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
		return this.amount = amount;
	}

	@Override
	protected void execute() {
		if (amount <= 0 || mustAbort || !player.getInventory().hasItem(s.getMainOre()) || s.getSecondaryItems() != null && !player.getInventory().hasItem(s.getSecondaryItems()[0])
				|| !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		player.playAnimation(new Animation(899));
		player.sendMessage("You retrieve an " + new Item(s.getBarId()).getDefinition().getName().toLowerCase() + ".");
		player.getSkill().addExperience(Skill.SMITHING, s.getExperience() * Constants.SMITHING_EXPERIENCE);
		player.getInventory().remove(s.getMainOre());

		if (s.getSecondaryItems() != null) {
			player.getInventory().remove(s.getSecondaryItems()[0]);
		}

		player.getInventory().add(new Item(s.getBarId(), 1));

		if (s.getBarId() == SmeltingData.GOLD.getBarId()) {
			AchievementManager.performed(player, Achievement.SMITH_GOLD);
		}

	}

	/**
	 * Checks if the player can smelt a bar.
	 */
	public static boolean canSmelt(Player player, int barId) {
		SmeltingData s = SmeltingData.forBar(barId);

		if (s == null) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.SMITHING) < s.getLevelRequired()) {
			player.getSession().send(new SendRemoveInterfacePacket());
			player.sendMessage("You need a smithing level of " + s.getLevelRequired() + " to smelt this bar.");
			return false;
		}

		if (!player.getInventory().hasItem(s.getMainOre())
				|| s.getSecondaryItems() != null && s.getSecondaryItems()[0] != null && !player.getInventory().hasItem(s.getSecondaryItems()[0])) {
			player.getSession().send(new SendRemoveInterfacePacket());
			player.sendMessage("You don't have the required ores to smelt this bar.");
			return false;
		}

		return true;
	}

	/**
	 * Sends the smelting interface.
	 */
	public static void sendSmeltingInterface(Player player) {
		for (SmeltingData s : SmeltingData.values()) {
			player.getSession().send(new SendItemOnInterfacePacket(s.getFrame(), 150, s.getBarId()));
		}
		player.getSession().send(new SendChatInterfacePacket(2400));
	}

	/**
	 * Checks if player has the ores to create a bar.
	 */
	public static boolean hasOres(Player player, int barId) {

		SmeltingData s = SmeltingData.forBar(barId);

		if (s == null) {
			return false;
		}

		if (!player.getInventory().hasItem(s.getMainOre()) || s.getSecondaryItems() != null && !player.getInventory().hasItem(s.getSecondaryItems()[0])) {
			player.getSession().send(new SendRemoveInterfacePacket());
			player.sendMessage("You don't have enough ores to continue smelting!");
			return false;
		}

		return true;
	}

	@Override
	public String getName() {
		return "SMELTING";
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
