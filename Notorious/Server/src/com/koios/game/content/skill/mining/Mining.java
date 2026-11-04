package com.koios.game.content.skill.mining;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillCycleCalculator;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.mining.MiningData.PickaxeData;
import com.koios.game.content.skill.mining.MiningData.RocksData;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Skills: Full mining skill class.
 *
 * @author Gabriel | Wolfs Darker
 */
public class Mining extends SkillConstructor {

	/**
	 * Checks if the player can mine or if the object is a mining ore.
	 */
	public static boolean canMine(Player p, int objectId) {
		RocksData def = RocksData.forRock(objectId);

		if (def == null) {
			return false;
		}

		int pickaxe = getPlayerPickaxe(p);

		if (pickaxe <= 0) {
			p.sendMessage("You need a pickaxe to mine this ore.");
			return false;
		}

		if (def.getLevelReq() > p.getSkill().getLevel(Skill.MINING)) {
			p.sendMessage("You need a Mining level of " + def.getLevelReq() + " to mine this ore.");
			return false;
		}

		PickaxeData pick = PickaxeData.forPickaxe(pickaxe);

		if (pick.getLevelRequired() > p.getSkill().getLevel(Skill.MINING)) {
			p.sendMessage("You need a mining level of " + pick.getLevelRequired() + " to use this pickaxe.");
			return false;
		}

		if (p.getInventory().freeSlots() < 1) {
			p.sendMessage("You do not have enough inventory slots to do that.");
			return false;
		}

		return true;
	}

	public static int getPlayerPickaxe(Player p) {

		int equipment = -1;
		int inventory = -1;

		for (PickaxeData pick : PickaxeData.values()) {
			if (p.getEquipment().contains(pick.getPickId())) {
				equipment = pick.getPickId();
			}
			if (p.getInventory().contains(pick.getPickId())) {
				inventory = pick.getPickId();
			}
		}
		return equipment > 0 ? equipment : inventory;
	}

	private final Player player;
	private final WorldObject rock;
	private RocksData r = null;
	private PickaxeData pick = null;

	private boolean mustAbort;

	private int cycles = 0, animCycles = 0, anim;

	public Mining(Player player, WorldObject rock) {
		super(1, false);
		this.player = player;
		this.rock = rock;
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
		if (mustAbort || player.getInventory().freeSlots() < 1 || !SkillManager.canContinue(player)) {
			abort();
			return;
		}

		if (cycles == 0) {
			if (NumberUtil.inclusiveRandom(5) >= 2) {
				respawnRock(r.getTimer());
				player.getInventory().add(new Item(r.getOreId(), 1));
				player.getSkill().addExperience(14, r.getExperienceGained() * Constants.MINING_EXPERIENCE);
				randomGems();
				player.sendMessage("You manage to mine some " + ItemDefinition.forId(r.getOreId()).getName().toLowerCase() + ".");

				if (RocksData.forRock(rock.getID()) == RocksData.RUNITE) {
					AchievementManager.performed(player, Achievement.MINE_RUNITE);
				}

				AchievementManager.performed(player, Achievement.MINE_ORE);
				abort();
				return;
			}

			cycles = SkillCycleCalculator.miningCycles(r.getLevelReq(), pick.getPickDelay(), player.getSkill().getLevel(Skill.MINING));
		}

		if (animCycles == 0) {
			player.playAnimation(new Animation(anim));
			animCycles = 14;
		}

		animCycles--;
		cycles--;
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "Mining";
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

	public void randomGems() {
		int[] gems = { 1623, 1621, 1619, 1617, 1631 };

		if (NumberUtil.inclusiveRandom(10) >= 9) {
			int i = gems[NumberUtil.inclusiveRandom(4)];
			player.getInventory().add(new Item(i, 1));
			player.sendMessage("You find a gem!");
		}
	}

	public void respawnRock(final int timer) {

		new GameObject(452, rock.getX(), rock.getY(), rock.getZ(), 1, 10, 452, timer, false);

		GameService.getTaskScheduler().schedule(new Task(timer) {

			@Override
			protected void execute() {
				new GameObject(rock.getID(), rock.getX(), rock.getY(), rock.getZ(), 1, 10, rock.getID(), timer, false);
				stop();
			}

		});
	}

	@Override
	public int setAmount(int amount) {
		return 0;
	}

	@Override
	public boolean setMustAbort(boolean abort) {
		mustAbort = abort;
		return mustAbort;
	}

	@Override
	public Object[] setObjects(Object[] objects) {
		return null;
	}

	@Override
	public boolean start() {
		if (!SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		r = RocksData.forRock(rock.getID());
		pick = PickaxeData.forPickaxe(getPlayerPickaxe(player));
		anim = pick.getPickAnimation();
		cycles = SkillCycleCalculator.miningCycles(r.getLevelReq(), pick.getPickDelay(), player.getSkill().getLevel(Skill.MINING));
		animCycles = 14;
		player.playAnimation(new Animation(anim));
		player.sendMessage("You swing your pick at the rock.");
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public boolean hasStarted() {
		return true;
	}

}
