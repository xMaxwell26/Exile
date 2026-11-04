package com.koios.game.content.dialogue;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.koios.game.model.player.Player;

public class DialogueManager {

	private static final Logger logger = LoggerFactory.getLogger(DialogueManager.class);

	private final Player player;

	/**
	 * The current dialogue.
	 */
	private Dialogue dialogue = null;

	public DialogueManager(Player player) {
		this.player = Objects.requireNonNull(player, "player");
	}

	public boolean input(int value) {
		if (dialogue != null) {
			dialogue.input(value);
			return true;
		}

		return false;
	}

	public boolean input(String value) {
		if (dialogue != null) {
			dialogue.input(value);
			return true;
		}

		return false;
	}

	public void interrupt() {
		if (dialogue != null) {
			dialogue.finish();
			dialogue = null;
		}
	}

	public boolean isActive() {
		return dialogue != null;
	}

	public Class<? extends Dialogue> getActiveType() {
		return dialogue == null ? null : dialogue.getClass();
	}

	public boolean next() {
		if (dialogue != null) {
			dialogue.next();
			return true;
		}

		return false;
	}

	public boolean select(int index) {
		if (dialogue != null) {
			dialogue.select(index);
			return true;
		}

		return false;
	}

	/**
	 * @param dialogue The dialogue to run. Not null.
	 */
	public void start(Dialogue dialogue) {
		Objects.requireNonNull(dialogue, "dialogue");
		interrupt();
		this.dialogue = dialogue;
		dialogue.start();
		logger.debug("{} started new dialogue: {}", player.getName(), dialogue.getClass().getName());
	}

}
