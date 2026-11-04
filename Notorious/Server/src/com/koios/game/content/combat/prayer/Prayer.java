package com.koios.game.content.combat.prayer;

import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;

public interface Prayer {

	static final int NONE = 0x0;
	static final int ATTACK = 0x01;
	static final int STRENGTH = 0x02;
	static final int DEFENCE = 0x04;
	static final int RANGE = 0x08;
	static final int MAGE = 0x10;
	static final int OVERHEAD = 0x20;
	static final int HEALING = 0x40;
	static final int SPIRIT = 0x80;

	default void onActivate(Player player) {

	}

	default void onDeactivate(Player player) {

	}

	default boolean canActivate(Player player) {
		return true;
	}

	default boolean tryActivate(Player player) {
		if (player.isDead() || player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			return false;
		}

		if (player.getSkill().getLevelForExperience(Skill.PRAYER) < getLevelRequired()) {
			SimpleDialogues.sendStatement(player, "You need a @blu@Prayer level of " + getLevelRequired() + " to use " + getName() + ".");
			return false;
		}

		if (player.getSkill().getLevel(Skill.PRAYER) == 0) {
			SimpleDialogues.sendStatement(player, "You need to recharge your Prayer at an altar.");
			return false;
		}

		if (player.getDuel().isRuleActive(DuelingRules.NO_PRAYER)) {
			player.sendMessage("Prayer has been disabled in this duel!");
			return false;
		}

		if (player.getMinigame().inMinigame() && !player.getMinigame().getCurrentMinigame().canUsePrayer()) {
			player.sendMessage("You cannot use Prayer inside this minigame!");
			return false;
		}

		if (player.inChallenge()) {
			player.sendMessage("You cannot use Prayer inside the Champion's arena!");
			return false;
		}

		if (player.inBarbDef()) {
			player.sendMessage("You cannot use Prayer inside Barbarian assault!");
			return false;
		}

		// prevent prayer swapping/cheating
		if (getBook() == PrayerBook.CURSED && player.altarPrayed != 1) {
			return false;
		}

		if (getBook() == PrayerBook.NORMAL && player.altarPrayed != 0) {
			return false;
		}

		return canActivate(player);
	}

	int getId();

	int getGlow();

	int getButtonId();

	int getQuickPrayerButton();

	int getLevelRequired();

	int getFlags();

	double getDrain();

	String getName();

	PrayerBook getBook();

	default void effect(Player player) {

	}

	default int getHeadIcon() {
		return -1;
	}

	default int getActivateAnimation() {
		return -1;
	}

	default int getActivateGraphic() {
		return -1;
	}

}