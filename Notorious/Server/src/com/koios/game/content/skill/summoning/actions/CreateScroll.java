package com.koios.game.content.skill.summoning.actions;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Create summoning scroll action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class CreateScroll extends SkillConstructor {

	private final Player player;
	private final int buttonId;
	private ScrollsData scroll = null;
	private final int amount;
	private boolean started = false;

	public CreateScroll(Player player, int buttonId, int amount) {
		super(1, true);
		this.player = player;
		this.buttonId = buttonId;
		scroll = ScrollsData.getByButtton(buttonId);
		this.amount = amount;
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
		return false;
	}

	@Override
	public boolean mustAbort() {
		return true;
	}

	@Override
	public boolean setMustAbort(boolean abort) {
		return false;
	}

	@Override
	public boolean hasRequeriments(Object[] object) {
		return false;
	}

	@Override
	public boolean start() {
		if (amount <= 0 || scroll == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		started = true;
		GameService.getTaskScheduler().schedule(this);
		return false;
	}

	@Override
	public boolean abort() {
		player.setCurrentSkill(null);
		player.setLastSkillAction(System.currentTimeMillis());
		stop();
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
		int newAmount = 0;
		for (int i = 0; i < amount; i++) {
			if (hasRequirements(player, buttonId, false, 1, scroll)) {
				player.getInventory().remove(new Item(scroll.getPouchReq(), 1));
				player.getInventory().add(new Item(scroll.getScrollId(), 10));
				newAmount++;
			} else {
				break;
			}
		}
		if (newAmount == 0) {
			abort();
			return;
		}
		player.playAnimation(new Animation(725));
		player.playGraphics(new Graphic(1207));
		player.sendMessage("You infuse " + newAmount * 10 + " " + new Item(scroll.getScrollId()).getDefinition().getName() + (newAmount * 10 > 1 ? "s" : "") + ".");
		player.getSkill().addExperience(Skill.SUMMONING, scroll.getCreateExp() * newAmount * Constants.SUMMONING_EXPERIENCE);
		abort();
	}

	/**
	 * Process interface functions
	 */

	public static boolean processInterfaceFunctions(Player player, int buttonId) {
		if (buttonId < 201255 || buttonId > 203132) {
			return false;
		}
		ScrollsData scroll = ScrollsData.getByButtton(buttonId);
		if (scroll == null) {
			player.sendMessage("This familiar's special move is currently unavaliable.");
			return false;
		}
		switch (buttonId - scroll.getButtonId()) {
		case 1:
			hasRequirements(player, scroll.getButtonId(), true, 5, scroll);
			return true;
		case 2:
			hasRequirements(player, scroll.getButtonId(), true, 10, scroll);
			return true;
		case 3:
			hasRequirements(player, scroll.getButtonId(), true, 28, scroll);
			return true;
		case 4:
			player.getInterfaceState().openValueInput(51700, scroll.getButtonId(), scroll.getScrollId());
			return true;
		default:
			hasRequirements(player, scroll.getButtonId(), true, 1, scroll);
			return true;
		}
	}

	/**
	 * Checks whether or not that player has requirements to make the pouch.
	 *
	 * @param c The Player.
	 * @param itemId The item id.
	 * @param slot The slot of the item.
	 */

	public static boolean hasRequirements(final Player player, final int buttonId, boolean inform, int amount, ScrollsData scroll) {
		if (inform) {
			if (player.getSkill().getLevelForExperience(Skill.SUMMONING) < scroll.getLevelReq()) {
				player.sendMessage("You need a summoning level of " + scroll.getLevelReq() + " to create this scroll");
				return false;
			}
		}
		if (!player.getInventory().contains(scroll.getPouchReq())) {
			if (inform) {
				player.sendMessage("You need " + new Item(scroll.getPouchReq()).getDefinition().getName() + " to create this scroll.");
			}
			return false;
		}
		player.setCurrentSkill(new CreateScroll(player, buttonId, amount));
		player.getCurrentSkill().start();
		return true;

	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public String getName() {
		return "SCROLL CREATION";
	}

}
