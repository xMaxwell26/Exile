package com.koios.game.content.skill.crafting;

import com.koios.GameService;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.crafting.CraftingData.TanningData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendItemOnInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Handles the tanning interface.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class TanningInterface extends SkillConstructor {

	private final Player player;
	private final int item;
	private int amount;
	private boolean mustAbort;

	public TanningInterface(Player player, int item, int amount) {
		super(1, true);
		this.player = player;
		this.item = item;
		this.amount = amount;
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
		if (amount <= 0 || !SkillManager.canSkill(player, this) || !canTan(item)) {
			abort();
			return false;
		}

		GameService.getTaskScheduler().schedule(this);
		return true;
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
		return 500;
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
		TanningData t = TanningData.get(item);

		if (t == null) {
			return;
		}

		int amount = player.getInventory().getCount(new Item(t.getHideId()));

		if (amount > this.amount) {
			amount = this.amount;
		}

		int price = amount * t.getPrice();
		int coins = player.getInventory().getCount(new Item(995));

		if (price > coins || !player.getInventory().hasItem(new Item(995, price))) {
			player.sendMessage("You do not have enough coins to tan this hide.");
			abort();
			return;
		}

		if (!player.getInventory().hasItem(new Item(t.getHideId(), amount))) {
			player.sendMessage("You do not have any hides to tan.");
			abort();
			return;
		}

		amount = price / t.getPrice();
		player.getInventory().remove(new Item(t.getHideId(), amount));
		player.getInventory().remove(new Item(995, t.getPrice()));
		player.getInventory().add(new Item(t.getLeatherId(), amount));
		player.sendMessage("The tanner tans the hides for you.");
		abort();
	}

	public static void sendTanningInterface(Player player) {
		player.getInterfaceState().openInterface(14670);

		for (TanningData t : TanningData.values()) {
			player.getSession().send(new SendItemOnInterfacePacket(t.getItemFrame(), 250, t.getLeatherId()));
			player.getSession().send(new SendStringPacket(t.getNameFrame(), t.getName()));
			String price = player.getInventory().hasItem(new Item(995, t.getPrice())) ? "@gre@" : "@red@";
			player.getSession().send(new SendStringPacket(price + "Price: " + t.getPrice(), t.getCostFrame()));
		}
	}

	public static boolean canTan(int itemId) {
		TanningData t = TanningData.get(itemId);
		return t != null;
	}

	public static int getHideByButton(int button) {
		switch (button) {
		case 57225:
		case 57217:
		case 57209:
		case 57201:
			return 1741;

		case 57226:
		case 57218:
		case 57210:
		case 57202:
			return 1743;

		case 57227:
		case 57219:
		case 57211:
		case 57203:
		case 57228:
		case 57220:
		case 57212:
		case 57204:
			return 6289;

		case 57229:
		case 57221:
		case 57213:
		case 57205:
			return 1745;

		case 57230:
		case 57222:
		case 57214:
		case 57206:
			return 2505;

		case 57231:
		case 57223:
		case 57215:
		case 57207:
			return 2507;

		case 57232:
		case 57224:
		case 57216:
		case 57208:
			return 2509;

		default:
			return 1741;
		}
	}

	@Override
	public String getName() {
		return "TANNINGINTERFACE";
	}

	@Override
	public boolean hasStarted() {
		return false;
	}

}
