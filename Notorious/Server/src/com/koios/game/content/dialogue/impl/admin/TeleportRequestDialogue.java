package com.koios.game.content.dialogue.impl.admin;

import java.util.Objects;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.model.player.Player;

public class TeleportRequestDialogue extends Dialogue {

	private final Player target;
	private final String targetName;

	public TeleportRequestDialogue(Player player, int defaultPhase, Player target) {
		super(player, defaultPhase);
		Objects.requireNonNull(target, "target");
		this.target = target;
		targetName = target.getName();
	}

	@Override
	public void next() {
		if (isPhase(0) || isPhase(1)) {
			choice("Proceed?", "Yes", "No");
		} else {
			stop();
		}
	}

	@Override
	public void select(int index) {
		if (isPhase(0)) {
			if (index == 1) {

				if (target != null) {
					player.getMovementHandler().reset();
					player.setPosition(target.getX(), target.getY(), target.getZ());
					player.sendMessage("You've been teleported to " + target.getName());
					target.sendMessage("You've teleported " + player.getName() + " to yourself.");
				} else {
					player.sendMessage("Unable to find " + targetName + ".");
				}

				stop();
			} else if (index == 2) {
				stop();
			}
		} else if (isPhase(1)) {
			if (index == 1) {

				if (target != null) {
					target.getMovementHandler().reset();
					target.setPosition(player.getX(), player.getY(), player.getZ());
					target.sendMessage("You've teleported to " + player.getName() + ".");
				} else {
					player.sendMessage("Unable to find " + targetName + ".");
				}

				stop();
			} else if (index == 2) {
				stop();
			}
		}
	}

	@Override
	protected void start() {

		if (target != null) {
			if (isPhase(0)) {
				sendStatement(target.getName() + " wishes to teleport you to them.", "Proceed?");
			} else if (isPhase(1)) {
				sendStatement(target.getName() + " wishes to teleport to you.", "Proceed?");
			} else {
				player.sendMessage(TeleportRequestDialogue.class.getName() + ": invalid dialogue setPhase(" + getPhase());
				stop();
			}
		} else {
			// this should never happen
			player.sendMessage("Unable to find " + targetName + ".");
			stop();
		}
	}

}
