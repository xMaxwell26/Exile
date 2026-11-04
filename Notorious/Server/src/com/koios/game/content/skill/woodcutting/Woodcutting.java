package com.koios.game.content.skill.woodcutting;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillCycleCalculator;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.woodcutting.WoodcuttingData.HatchetData;
import com.koios.game.content.skill.woodcutting.WoodcuttingData.TreeData;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Skills: Full woodcutting skill class.
 *
 * @author Gabriel | Wolfs Darker
 */
public class Woodcutting extends SkillConstructor {

	private final Player player;
	private final WorldObject tree;
	private boolean mustAbort = false;
	private HatchetData a = null;
	private int cycles = 0, anim;

	public Woodcutting(Player player, WorldObject tree) {
		super(1, true);
		this.player = player;
		this.tree = tree;
		a = HatchetData.forAxe(Woodcutting.getPlayerAxe(player));
	}

	public static boolean canWoodcut(Player player, int objectId) {
		final TreeData t = TreeData.forTree(objectId);

		if (t == null) {
			return false;
		}

		int axe = getPlayerAxe(player);

		if (axe <= 0) {
			player.sendMessage("You need hatchet hatchet to cut this tree.");
			return false;
		}

		if (t.getLevelReq() > player.getSkill().getLevel(Skill.WOODCUTTING)) {
			player.sendMessage("You need hatchet woodcutting level of " + t.getLevelReq() + " to cut this tree.");
			return false;
		}

		HatchetData hatchet = HatchetData.forAxe(axe);

		if (hatchet.getLevelRequired() > player.getSkill().getLevel(Skill.WOODCUTTING)) {
			player.sendMessage("You need hatchet woodcutting level of " + hatchet.getLevelRequired() + " to use this hatchet.");
			return false;
		}

		return true;
	}

	public static int getPlayerAxe(Player p) {
		for (HatchetData axe : HatchetData.values()) {
			if (p.getInventory().contains(axe.getAxeId()) || p.getEquipment().contains(axe.getAxeId())) {
				return axe.getAxeId();
			}
		}

		return -1;
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
		mustAbort = abort;
		return mustAbort;
	}

	@Override
	public boolean hasRequeriments(Object[] object) {
		return false;
	}

	@Override
	public boolean start() {
		if (!SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		anim = a.getAxeAnimation();
		player.playAnimation(new Animation(anim));
		cycles = SkillCycleCalculator.woodcuttingCycles(a.getAxeId(), tree.getID(), player.getSkill().getLevel(Skill.WOODCUTTING));
		player.sendMessage("You swing your hatchet at the tree.");
		GameService.getTaskScheduler().schedule(this);
		return false;
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
		if (mustAbort || player.getInventory().freeSlots() < 1 || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		if (cycles == 0) {
			final TreeData t = TreeData.forTree(tree.getID());
			player.getInventory().add(new Item(t.getLogId(), 1));
			player.getSkill().addExperience(Skill.WOODCUTTING,
					t.getExperienceGained() * Constants.WOODCUTTING_EXPERIENCE * WoodcuttingSpecialEvents.equipmentExperienceBonus(player));
			WoodcuttingSpecialEvents.handleAdzeAffect(player, tree.getID());
			player.sendMessage("You cut down some logs.");

			if (t.getLogId() == TreeData.MAGIC.getLogId()) {
				AchievementManager.performed(player, Achievement.CHOP_MAGIC_TREE);
			}

			AchievementManager.performed(player, Achievement.WOODCUTTER);
			BirdNests.birdNests(player);

			if (NumberUtil.inclusiveRandom(100) <= t.getMisc()) {
				cutDownTree(t);
				abort();
				return;
			} else {
				cycles = SkillCycleCalculator.woodcuttingCycles(a.getAxeId(), tree.getID(), player.getSkill().getLevel(Skill.WOODCUTTING));
			}
		}

		player.playAnimation(new Animation(anim));
		cycles--;
	}

	public void cutDownTree(TreeData t) {
		new GameObject(t.getStumpId(), tree.getX(), tree.getY(), tree.getZ(), 1, 10, t.getStumpId(), t.getTimer(), false);

		GameService.getTaskScheduler().schedule(new Task(t.getTimer()) {

			@Override
			protected void execute() {
				new GameObject(tree.getID(), tree.getX(), tree.getY(), tree.getZ(), 1, 10, tree.getID(), t.getTimer(), false);
				stop();
			}

		});
	}

	@Override
	public String getName() {
		return "Woodcutting";
	}

	@Override
	public boolean hasStarted() {
		return true;
	}

}
