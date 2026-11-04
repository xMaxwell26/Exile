package com.koios.game.content.skill.summoning.actions;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.summoning.SummoningData.PouchData;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class CreatePouch extends SkillConstructor {

	private final Player player;
	private final int buttonId;
	private final int amount;
	private PouchData pouchData = null;
	private boolean started;

	public CreatePouch(Player player, int buttonId, int amount) {
		super(1, true);
		this.player = player;
		this.buttonId = buttonId;
		pouchData = PouchData.getByButton(buttonId);
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
		if (amount <= 0 || pouchData == null || !SkillManager.canSkill(player, this)) {
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
		int newAmount = 0;
		for (int i = 1; i <= amount; i++) {
			if (hasRequirements(player, buttonId, false, 1, pouchData)) {
				player.getInventory().remove(new Item(POUCH_ID, 1));
				player.getInventory().remove(new Item(SHARD_ID, 1));
				player.getInventory().remove(new Item(pouchData.getCharmId(), 1));
				player.getInventory().remove(new Item(pouchData.getSecondIngredientId(), 1));
				player.getInventory().add(new Item(pouchData.getPouchId(), 1));
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
		player.getSkill().addExperience(Skill.SUMMONING, pouchData.getExp() * newAmount * Constants.SUMMONING_EXPERIENCE);
		player.sendMessage("You infuse " + newAmount + " " + new Item(pouchData.getPouchId()).getDefinition().getName() + (newAmount > 1 ? "s" : "") + ".");
		abort();
	}

	/**
	 * Constants
	 */

	/**
	 * The id of the shard.
	 */
	private static final int SHARD_ID = 18016;

	/**
	 * The id of the default pouch.
	 */
	private static final int POUCH_ID = 12155;

	/**
	 * Process interface functions
	 */

	public static boolean processInterfaceFunctions(Player c, int buttonId) {
		if (buttonId < 205231 || buttonId > 207109) {
			return false;
		}
		PouchData pouch = PouchData.getByButton(buttonId);
		if (pouch == null) {
			return false;
		}
		switch (buttonId - pouch.getButtonId()) {
		case 1:
			hasRequirements(c, pouch.getButtonId(), true, 5, pouch);
			return true;
		case 2:
			hasRequirements(c, pouch.getButtonId(), true, 10, pouch);
			return true;
		case 3:
			hasRequirements(c, pouch.getButtonId(), true, 28, pouch);
			return true;
		case 4:
			c.getInterfaceState().openValueInput(52700, pouch.getButtonId(), pouch.getPouchId());
			return true;
		default:
			hasRequirements(c, pouch.getButtonId(), true, 1, pouch);
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

	public static boolean hasRequirements(final Player player, final int buttonId, boolean inform, int amount, PouchData pouch) {
		if (inform) {
			if (player.getSkill().getLevelForExperience(Skill.SUMMONING) < pouch.getLevelRequired()) {
				player.sendMessage("You need a summoning level of " + pouch.getLevelRequired() + " to create this pouch");
				return false;
			}
		}
		if (!player.getInventory().contains(POUCH_ID)) {
			if (inform) {
				player.sendMessage("You dont have a pouch to create this item.");
			}
			return false;
		}
		if (!player.getInventory().hasItem(new Item(SHARD_ID, pouch.getShardsRequired()))) {
			if (inform) {
				player.sendMessage("You need " + pouch.getShardsRequired() + " shards to create this pouch.");
			}
			return false;
		}

		if (!player.getInventory().contains(pouch.getCharmId())) {
			if (inform) {
				player.sendMessage("You need a " + new Item(pouch.getCharmId()).getDefinition().getName() + " to create this pouch.");
			}
			return false;
		}
		if (!player.getInventory().contains(pouch.getSecondIngredientId())) {
			if (inform) {
				player.sendMessage("You need " + new Item(pouch.getSecondIngredientId()).getDefinition().getName() + " to create this pouch.");
			}
			return false;
		}
		player.setCurrentSkill(new CreatePouch(player, buttonId, amount));
		player.getCurrentSkill().start();
		return true;

	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public String getName() {
		return "POUCH CREATION";
	}

}
