package com.koios.game.content.skill.crafting;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.crafting.CraftingData.LeatherData;
import com.koios.game.content.skill.crafting.CraftingData.LeatherDialogueData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendItemOnInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

public class CraftLeather extends SkillConstructor {

	public static boolean canCraft(Player player, int product, boolean start) {
		LeatherData l = LeatherData.forProduct(product);

		if (l == null) {
			return false;
		}

		if (start) {
			if (player.getSkill().getLevel(Skill.CRAFTING) < l.getLevel()) {
				player.sendMessage("You need a Crafting level of " + l.getLevel() + " to craft this.");
				return false;
			}
		}

		if (!player.getInventory().contains(1734)) {
			player.sendMessage(start ? "You need some thread to make this." : "You ran out of threads!");
			return false;
		}

		if (!player.getInventory().hasItem(new Item(l.getLeather(), l.getHideAmount()))) {
			player.sendMessage(start ? "You need " + l.getHideAmount() + " " + new Item(l.getLeather()).getDefinition().getName().toLowerCase() + " to make "
					+ new Item(product).getDefinition().getName().toLowerCase() + "." : "You don't have enough to make more!");
			return false;
		}

		return true;
	}

	public static int getProductByButton(int button) {
		if (button >= 33185 && button <= 33187) {
			return 1129;
		}

		if (button >= 33188 && button <= 33190) {
			return 1059;
		}

		if (button >= 33191 && button <= 33193) {
			return 1061;
		}

		if (button >= 33194 && button <= 33196) {
			return 1063;
		}

		if (button >= 33197 && button <= 33199) {
			return 1095;
		}

		if (button >= 33200 && button <= 33202) {
			return 1169;
		}

		if (button >= 33203 && button <= 33205) {
			return 1167;
		}

		return -1;
	}

	public static boolean handleItems(Player player, int itemUsed, int useWith) {
		if (itemUsed != 1733 && useWith != 1733 || !isHide(itemUsed) && !isHide(useWith)) {
			return false;
		}

		if (!player.getInventory().contains(1734)) {
			return false;
		}

		if (player.getCurrentSkill() != null) {
			player.getCurrentSkill().abort();
		}

		int leather = itemUsed == 1733 ? useWith : itemUsed;

		switch (leather) {
		case 1741:
			player.getInterfaceState().openInterface(2311);
			break;

		case 1743:
			SkillManager.sendInterface(player, 1131);
			player.setCurrentSkill(new CraftLeather(player, new Item(1131), 0));
			return true;

		case 6289:
			String[] name = { "Body", "Chaps", "Bandana", "Boots", "Vamb", };
			player.getSession().send(new SendChatInterfacePacket(8938));
			player.getSession().send(new SendItemOnInterfacePacket(8941, 180, 6322));
			player.getSession().send(new SendItemOnInterfacePacket(8942, 180, 6324));
			player.getSession().send(new SendItemOnInterfacePacket(8943, 180, 6326));
			player.getSession().send(new SendItemOnInterfacePacket(8944, 180, 6328));
			player.getSession().send(new SendItemOnInterfacePacket(8945, 180, 6330));

			for (int i = 0; i < name.length; i++) {
				player.getSession().send(new SendStringPacket("\\n\\n\\n\\n\\n" + name[i], 8949 + i * 4));
			}
			break;

		default:
			LeatherDialogueData l = LeatherDialogueData.forLeather(leather);
			String[] names = { "Vamb", "Chaps", "Body", };
			player.getSession().send(new SendChatInterfacePacket(8880));
			player.getSession().send(new SendItemOnInterfacePacket(8883, 180, l.getVamb()));
			player.getSession().send(new SendItemOnInterfacePacket(8884, 180, l.getChaps()));
			player.getSession().send(new SendItemOnInterfacePacket(8885, 180, l.getBody()));

			for (int i = 0; i < names.length; i++) {
				player.getSession().send(new SendStringPacket("\\n\\n\\n\\n\\n" + names[i], 8889 + i * 4));
			}
			break;
		}

		player.setCurrentSkill(new CraftLeather(player, new Item(leather), 0));
		return true;
	}

	public static boolean isHide(int item) {
		return item == 1741 || item == 1743 || item == 6289 || item == 1745 || item == 2505 || item == 2507 || item == 2509;
	}

	private int amount;
	private Item item;
	private boolean mustAbort = false, started = false;
	private final Player player;

	public CraftLeather(Player player, Item item, int amount) {
		super(3, true);
		this.player = player;
		this.item = item;
		this.amount = amount;
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
		if (amount <= 0 || mustAbort || !canCraft(player, item.getId(), false) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		LeatherData definition = LeatherData.forProduct(item.getId());

		if (player.getInventory().hasItem(new Item(definition.getLeather(), definition.getHideAmount())) && player.getInventory().hasItem(new Item(1734, 1))) {
			player.getInventory().remove(new Item(1734, 1));
			player.getInventory().remove(new Item(definition.getLeather(), definition.getHideAmount()));
			player.getInventory().add(new Item(definition.getProduct(), 1));
			player.sendMessage("You make " + (new Item(definition.getProduct()).getDefinition().getName().contains("body") ? "a" : "some") + " "
					+ new Item(definition.getProduct()).getDefinition().getName() + ".");
			player.getSkill().addExperience(Skill.CRAFTING, definition.getXP() * Constants.CRAFTING_EXPERIENCE);
			AchievementManager.performed(player, Achievement.CRAFT_LEATHER);
		} else {
			abort();
			return;
		}

		if (amount > 0) {
			player.playAnimation(new Animation(1249));
		}

		amount--;
	}

	@Override
	public int getActionDelay() {
		return 500;
	}

	@Override
	public String getName() {
		return "CRAFTLEATHER";
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { LeatherDialogueData.forLeather(item.getId()) };
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
		this.amount = amount;
		return amount;
	}

	@Override
	public boolean setMustAbort(boolean abort) {
		return mustAbort = abort;
	}

	@Override
	public Object[] setObjects(Object[] objects) {
		item = (Item) objects[0];
		return objects;
	}

	@Override
	public boolean start() {
		if (amount <= 0 || !canCraft(player, item.getId(), true) || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		player.playAnimation(new Animation(1249));
		started = true;
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

}
