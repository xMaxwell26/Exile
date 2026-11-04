package com.koios.game.content.skill.runecrafting;

import java.util.Objects;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.SkillConstructor;
import com.koios.game.content.skill.SkillManager;
import com.koios.game.content.skill.runecrafting.RunecraftingData.AltarData;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Skills: Craft runes action.
 *
 * @author Gabriel | Wolfs Darker
 */
public class Runecrafting extends SkillConstructor {

	/**
	 * Checks if the player can craft certain runes.
	 */
	public static boolean canCraftRunes(Player player, int objectId) {
		AltarData def = AltarData.forAltar(objectId);

		if (def == null) {
			return false;
		}

		if (player.getSkill().getLevel(Skill.RUNECRAFTING) < def.getLevelReq()) {
			SimpleDialogues.sendStatement(player, "You need a runecrafting level of " + def.getLevelReq() + " to craft this rune.");
			return false;
		}

		if (!player.getInventory().contains(1436) && !player.getInventory().contains(7936)) {
			player.sendMessage("You need rune or pure essence to make these kinds of runes.");
			return false;
		}

		if (def.getRuneId() >= 560) {
			if (!player.getInventory().contains(7936)) {
				player.sendMessage("You need pure essence to make these kinds of runes.");
				return false;
			}
		}

		return true;
	}

	private final Player player;

	private final AltarData definition;

	public Runecrafting(Player player, int objectId) {
		super(1, true);
		this.player = Objects.requireNonNull(player, "player");
		definition = AltarData.forAltar(objectId);
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
		int runeAmount = player.getInventory().getItemAmount(1436);
		int pureAmount = player.getInventory().getItemAmount(7936);
		Item runeEss = new Item(1436, runeAmount);
		Item pureEss = new Item(7936, pureAmount);
		int realMultiplier = definition.getMultiplier() < 0 ? 1 : (int) Math.floor(player.getSkill().getLevel(20) / definition.getMultiplier()) + 1;

		if (pureEss != null && pureEss.getCount() > 0) {
			player.getInventory().remove(pureEss);
			player.getInventory().add(new Item(definition.getRuneId(), pureAmount * realMultiplier));
			player.getSkill().addExperience(20, definition.getExp() * pureAmount * Constants.RUNECRAFTING_EXPERIENCE);
		}

		if (definition.getRuneId() < 560) {
			if (runeEss != null && runeEss.getCount() > 0) {
				player.getInventory().remove(runeEss);
				player.getInventory().add(new Item(definition.getRuneId(), runeAmount * realMultiplier));
				player.getSkill().addExperience(20, definition.getExp() * runeAmount * Constants.RUNECRAFTING_EXPERIENCE);
			}
		}

		player.playAnimation(new Animation(791));
		player.playGraphics(new Graphic(186));
		player.sendMessage("You bind the temple's power into " + new Item(definition.getRuneId()).getDefinition().getName().toLowerCase() + "s.");
		stop();
	}

	@Override
	public int getActionDelay() {
		return 600;
	}

	@Override
	public String getName() {
		return "RUNECRAFTING";
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
		if (!SkillManager.canSkill(player, this)) {
			abort();
			return false;
		}
		GameService.getTaskScheduler().schedule(this);
		return true;
	}

	@Override
	public boolean hasStarted() {
		return true;
	}

}
