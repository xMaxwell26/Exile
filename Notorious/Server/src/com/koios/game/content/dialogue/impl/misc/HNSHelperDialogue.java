package com.koios.game.content.dialogue.impl.misc;

import java.util.Objects;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.mob.impl.event.hideandseek.MobHideAndSeekHelper;
import com.koios.game.model.player.Player;

public final class HNSHelperDialogue extends Dialogue {

	private final MobHideAndSeekHelper helper;

	public HNSHelperDialogue(Player player, MobHideAndSeekHelper helper) {
		super(player);
		this.helper = Objects.requireNonNull(helper, "helper cannot be null");
	}

	@Override
	protected void select(int index) {
		if (index == 1) {
			helper.setClaimable(true);
			helper.speak("Go! Go! Go!");
		} else if (index == 2) {
			helper.setDead(true);
		} else if (index == 3) {
			helper.claim(player);
			helper.speak(null); // reset default by #claim
			helper.setDead(true);
		}

		stop();
	}

	@Override
	protected void start() {
		choice("Select an Option", "Set claimable", "Destroy Helper & Contents", "Destroy Helper");
	}

}
