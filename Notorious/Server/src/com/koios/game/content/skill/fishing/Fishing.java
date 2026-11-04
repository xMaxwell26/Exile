package com.koios.game.content.skill.fishing;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillCycleCalculator;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.fishing.FishingData.Fishable;
import com.koios.game.content.skill.fishing.FishingData.Spots;
import com.koios.game.content.skill.fishing.FishingData.Tools;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

/**
 * Skills: Full fishing skill class.
 *
 * @author Gabriel | Wolfs Darker.
 */
public class Fishing extends SkillConstructor {

	/**
	 * Gets a random fish from the spot.
	 */
	public static int getFish(Player p, int fishId, int fishId2) {
		return NumberUtil.inclusiveRandom(5) < 2 && hasFishingLevelFor(p, fishId2) ? fishId2 : fishId;
	}

	/**
	 * Checks if player has enough fishing level for the other fish.
	 */
	public static boolean hasFishingLevelFor(Player p, int fish) {
		final Fishable f = Fishable.forId(fish);
		return f != null && p.getSkill().getLevel(Skill.FISHING) >= f.getRequiredLevel();
	}

	/**
	 * Determine if player can catch that fish.
	 */
	public static boolean isAbleToFish(Player p, int spotId, boolean firstClick) {
		Spots s = Spots.forSpot(spotId);

		if (s == null) {
			return false;
		}

		int fish1 = firstClick ? s.getFirstFishId() : s.getThirdFishId();

		Fishable f = Fishable.forId(fish1);

		if (f == null) {
			return false;
		}

		if (p.getSkill().getLevel(Skill.FISHING) < f.getRequiredLevel()) {
			SimpleDialogues.sendStatement(p, "You need a Fishing level of " + f.getRequiredLevel() + " to catch this fish.");
			return false;
		}

		if (!p.getInventory().contains(f.getToolId())) {
			p.sendMessage("You need a " + new Item(f.getToolId()).getDefinition().getName().toLowerCase() + " to catch this fish.");
			return false;
		}

		if (f.getBaitRequired() != -1) {
			if (!p.getInventory().contains(f.getBaitRequired())) {
				p.sendMessage("You need at least a " + new Item(f.getBaitRequired()).getDefinition().getName().toLowerCase() + " to catch this fish.");
				return false;
			}
		}

		if (p.getInventory().freeSlots() < 1) {
			p.sendMessage("You don't have enough space in your inventory.");
			return false;
		}

		return true;
	}

	private int animCycles;
	private int cycles = 0;
	private Fishable f = null;
	private boolean mustAbort;
	private final boolean firstClick;
	private final Player player;
	private Spots s = null;
	private Tools t = null;

	public Fishing(Player p, int spotId, boolean firstClick) {
		super(1, true);
		player = p;
		this.firstClick = firstClick;
		s = Spots.forSpot(spotId);
		f = Fishable.forId(firstClick ? s.getFirstFishId() : s.getThirdFishId());
		t = Tools.forId(f.getToolId());
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
		if (player == null || mustAbort || player.getInventory().freeSlots() < 1 || !SkillManager.canContinue(player) || player.getCurrentSkill() == null) {
			abort();
			return;
		}

		if (cycles == 0) {
			final int finalFish = getFish(player, firstClick ? s.getFirstFishId() : s.getThirdFishId(), firstClick ? s.getSecondFishId() : s.getFourthFishId());
			final Fishable f = Fishable.forId(finalFish);
			int factor = NumberUtil.inclusiveRandom(player.getSkill().getLevel(Skill.FISHING));
			int fluke = NumberUtil.inclusiveRandom(f.getRequiredLevel() == 1 ? 2 : f.getRequiredLevel());

			if (factor > fluke) {
				player.getInventory().add(new Item(f.getRawFishId(), 1));

				if (f.getBaitRequired() > 0) {
					player.getInventory().remove(new Item(f.getBaitRequired(), 1));
				}

				player.sendMessage(getSuccessfulInteractionMessage(finalFish));
				player.getSkill().addExperience(Skill.FISHING, f.getExperience() * Constants.FISHING_EXPERIENCE);
				AchievementManager.performed(player, Achievement.CATCH_FISH);
				if (finalFish == Fishable.SHARK.getRawFishId()) {
					AchievementManager.performed(player, Achievement.CATCH_SHARK);
				}
			}

			cycles = SkillCycleCalculator.fishingCycles(player, firstClick, s);
		}

		if (animCycles == 0) {
			player.playAnimation(new Animation(t.getAnimationId() == 622 ? t.getAnimationId() + 1 : t.getAnimationId()));
			animCycles = t.getAnimCycles();
		}

		animCycles--;
		cycles--;
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	/**
	 * Gets the message upon spot click.
	 */
	public String getInteractionMessage(int fish) {
		return "You attempt to catch a " + new Item(fish).getDefinition().getName().toLowerCase().replace("raw ", "") + ".";
	}

	@Override
	public Object[] getObjects() {
		return null;
	}

	/**
	 * Get successful catch message.
	 */
	public String getSuccessfulInteractionMessage(int fish) {
		Item rawFish = new Item(fish, 1);
		String prefix = rawFish.getDefinition().getName().endsWith("s") ? "some" : StringUtil.getArticle(rawFish.getDefinition().getName());
		return "You catch " + prefix + " " + rawFish.getDefinition().getName().toLowerCase().replace("raw ", "") + ".";
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
		if (s == null || t == null || f == null || !SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}

		player.playAnimation(new Animation(t.getAnimationId()));
		player.sendMessage(getInteractionMessage(firstClick ? s.getFirstFishId() : s.getThirdFishId()));
		cycles = SkillCycleCalculator.fishingCycles(player, firstClick, s);
		animCycles = t.getAnimCycles();
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public String getName() {
		return "FISHING";
	}

	@Override
	public boolean hasStarted() {
		return true;
	}

}
