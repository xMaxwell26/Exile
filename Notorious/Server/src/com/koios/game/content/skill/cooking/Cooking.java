package com.koios.game.content.skill.cooking;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.cooking.CookingData.CookItemData;
import com.koios.game.map.ObjectDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Skills: Cooking on range/fire cooking class.
 *
 * @author Gabriel | Wolfs Darker
 */
public class Cooking extends SkillConstructor {

	public static boolean canCook(Player player, int item) {
		final CookItemData c = CookItemData.forFood(item);

		if (c == null) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.COOKING) < c.getLevelReq()) {
			player.sendMessage("You need a cooking level of " + c.getLevelReq() + " to cook this.");
			return false;
		}

		if (!player.getInventory().contains(c.getUncookedFoodId())) {
			return false;
		}

		return true;
	}

	public static int getAnimation(String name) {
		if (name.contains("range") || name.equals("range") || name.contains("stove") || name.equals("stove")) {
			return 896;
		}

		return 897;
	}

	public static boolean handleItemOnObject(Player p, int itemId, int objectId) {
		if (!isCookingSpot(objectId) || objectId <= 0) {
			return false;
		}

		CookItemData c = CookItemData.forFood(itemId);

		if (c == null) {
			return false;
		}

		if (p.getCurrentSkill() != null) {
			p.getCurrentSkill().abort();
		}

		if (!canCook(p, itemId)) {
			return false;
		}

		p.setCurrentSkill(new Cooking(p, new Item(itemId), objectId, 0));

		if (p.getCurrentSkill() != null) {
			SkillManager.sendInterface(p, c.getCookedFoodId());
		}

		return true;
	}

	public static boolean isCookingSpot(int objectId) {
		return objectId == 2732 || objectId == 114 || objectId >= 2728 && objectId <= 2731 || objectId == 2859 || objectId == 12269;
	}

	private final Player player;
	private final Item food;
	private final int objectId;
	private int amount;
	private final int anim;
	private boolean mustAbort = false, started = false;

	public Cooking(Player player, Item food, int objectId, int amount) {
		super(3, true);
		this.player = player;
		this.food = food;
		this.objectId = objectId;
		this.amount = amount;
		mustAbort = false;
		anim = getAnimation(ObjectDefinition.getById(objectId).name.toLowerCase());
	}

	@Override
	public boolean abort() {
		player.playAnimation(new Animation(65535));
		player.setCurrentSkill(null);
		player.setLastSkillAction(System.currentTimeMillis());
		super.stop();
		return false;
	}

	public void burnFood(CookItemData c) {
		player.getInventory().remove(new Item(c.getUncookedFoodId(), 1));
		player.getInventory().add(new Item(c.getBurntFoodId(), 1));
		player.sendMessage("Oops! You accidentally burnt the " + new Item(c.getUncookedFoodId()).getDefinition().getName().toLowerCase() + "!");
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

	public void cookFood(CookItemData c) {
		player.getInventory().remove(new Item(c.getUncookedFoodId(), 1));
		player.getInventory().add(new Item(c.getCookedFoodId(), 1));
		player.sendMessage("You successfully cook the " + new Item(c.getUncookedFoodId()).getDefinition().getName().toLowerCase() + ".");
		player.getSkill().addExperience(Skill.COOKING, c.getExp() * Constants.COOKING_EXPERIENCE);
		AchievementManager.performed(player, Achievement.COOKING);
	}

	@Override
	protected void execute() {
		final CookItemData c = CookItemData.forFood(food.getId());

		if (amount <= 0 || !player.getInventory().contains(c.getUncookedFoodId()) || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		final int cookLevel = player.getSkill().getLevel(7);
		final int cookingGloves = player.getEquipment().contains(775) ? 8 : 0;
		int chance = 4 + cookLevel - c.getLevelReq();
		boolean burn = cookLevel - (c.getLevelToStopBurning() + cookingGloves) > 0 ? false : true;
		amount--;

		if (food.getId() == 1942 && objectId == 2732) {
			burnFood(c);
		} else {
			if (burn) {
				if (NumberUtil.inclusiveRandom(chance) >= 3) {
					cookFood(c);
				} else {
					burnFood(c);
				}
			} else {
				cookFood(c);
			}
		}

		if (amount > 0) {
			player.playAnimation(new Animation(anim));
		}

		player.setLastSkillAction(System.currentTimeMillis());
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "COOKING";
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
		if (amount <= 0 || !SkillManager.canSkill(player, this) || !canCook(player, food.getId())) {
			abort();
			return false;
		}

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
