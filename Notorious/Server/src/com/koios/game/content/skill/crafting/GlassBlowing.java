package com.koios.game.content.skill.crafting;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.crafting.CraftingData.GlassData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class GlassBlowing extends SkillConstructor {

	public static boolean canCreate(Player player, int buttonId) {
		GlassData definition = GlassData.getByButton(buttonId);

		if (definition == null) {
			return false;
		}

		if (definition.getLevelReq() > player.getSkill().getLevel(Skill.CRAFTING)) {
			player.sendMessage("You need a crafting level of " + definition.getLevelReq() + " to create this.");
			return false;
		}

		return true;
	}

	public static boolean handleItems(Player player, int itemUsed, int useWith) {
		if (itemUsed != 1775 && itemUsed != 1785 && useWith != 1775 && useWith != 1785) {
			return false;
		}
		if (player.getCurrentSkill() != null) {
			player.getCurrentSkill().abort();
		}
		player.getInterfaceState().openInterface(11462);
		player.setCurrentSkill(new GlassBlowing(player, 0, 0));
		return true;
	}

	private final Player player;
	private int button;
	private int amount;
	private boolean mustAbort, started;

	public GlassBlowing(Player player, int button, int amount) {
		super(3, true);
		this.player = player;
		this.button = button;
	}

	@Override
	public boolean abort() {
		stop();
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
		GlassData g = GlassData.getByButton(button);

		if (amount <= 0 || mustAbort || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		if (player.getInventory().contains(1775)) {
			player.getInventory().remove(new Item(1775, 1));
			player.getInventory().add(g.getProduct());
			player.getSkill().addExperience(Skill.CRAFTING, g.getExperience() * Constants.CRAFTING_EXPERIENCE);
			player.sendMessage("You make a " + g.getProduct().getDefinition().getName().toLowerCase() + ".");
		}

		amount--;

		if (amount > 0) {
			player.playAnimation(new Animation(884));
		}
	}

	@Override
	public int getActionDelay() {
		return amount;
	}

	@Override
	public String getName() {
		return "GLASSBLOWING";
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
		button = (int) objects[0];
		return objects;
	}

	@Override
	public boolean start() {
		GlassData definition = GlassData.getByButton(button);

		if (definition == null || amount <= 0 || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		player.playAnimation(new Animation(884));
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
