package com.koios.game.content.skill.firemaking;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.map.MapRegion;
import com.koios.game.map.path.RS317PathFinding;
import com.koios.game.model.Animation;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Skills: Firemaking action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class FiremakingAction extends SkillConstructor {

	/**
	 * Checks if the player can make fire.
	 */
	public static boolean canMakeFire(Player player, final int itemUsed, final int usedWith) {
		if (itemUsed != 590 && usedWith != 590) {
			return false;
		}

		Log l = itemUsed == 590 ? Log.forLog(usedWith) : Log.forLog(itemUsed);

		if (l == null) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.FIREMAKING) < l.getLevel()) {
			player.sendMessage("You need a firemaking level of " + l.getLevel() + " to light " + new Item(l.getLogId()).getDefinition().getName() + ".");
			return false;
		}

		if (player.inBank()) {
			player.sendMessage("You cannot light a fire in a bank.");
			return false;
		}

		if (player.atHome()) {
			player.sendMessage("You cannot light a fire at home.");
			return false;
		}

		if (GameService.getObjectManager().doesObjectExist(player.getX(), player.getY())) {
			player.sendMessage("You cannot light a fire here.");
			return false;
		}

		return true;
	}

	private Log l = null;
	private final Player player;

	public FiremakingAction(Player p, int logId) {
		super(1, true);
		player = p;
		l = Log.forLog(logId);
	}

	@Override
	public boolean abort() {
		player.setCurrentSkill(null);
		player.playAnimation(new Animation(65535));
		player.setLastSkillAction(System.currentTimeMillis());
		return true;
	}

	@Override
	public boolean canAbort() {
		return false;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canWalk() {
		return false;
	}

	@Override
	protected void execute() {
		int x = player.getX();
		int y = player.getY();
		player.setLastSkillAction(System.currentTimeMillis());
		int cycle = 2;

		if (player.getLastSkillAction() > 2000) {
			cycle = 3 + NumberUtil.inclusiveRandom(2);
		}

		player.getInventory().remove(new Item(l.getLogId(), 1));
		GroundItemManager.spawnNew(player, l.getLogId(), player.getX(), player.getY(), 1);
		AchievementManager.performed(player, Achievement.FIREMAKING);
		player.sendMessage("You attempt to light a fire.");

		final boolean walk = MapRegion.getRegion(x, y).canMove(x, y, player.getZ(), 3);

		player.playAnimation(new Animation(733));
		RS317PathFinding.findRoute(player, player.getX() + (walk ? -1 : 1), player.getY(), true, 1, 1);

		GameService.getTaskScheduler().schedule(new Task(cycle) {

			@Override
			public void execute() {
				GameService.getGroundItemManager().removeGroundItem(player, l.getLogId(), x, y, player.getZ(), false);
				new GameObject(2732, x, y, 0, 0, 10, -1, 120 + NumberUtil.inclusiveRandom(30), false);
				player.sendMessage("The fire catches and the log beings to burn.");
				player.getSkill().addExperience(Skill.FIREMAKING, l.getExp() * Constants.FIREMAKING_EXPERIENCE);
				player.setFacePosition(walk ? x + 1 : x - 1, y);

				abort();
				stop();
			}

		});

		stop();
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "FIREMAKING";
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
		return false;
	}

	@Override
	public int setAmount(int amount) {
		return 0;
	}

	@Override
	public boolean setMustAbort(boolean abort) {
		return false;
	}

	@Override
	public Object[] setObjects(Object[] objects) {
		return null;
	}

	@Override
	public boolean start() {
		if (l == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		GameService.getTaskScheduler().schedule(this);
		return false;
	}

	@Override
	public boolean hasStarted() {
		return true;
	}

}
