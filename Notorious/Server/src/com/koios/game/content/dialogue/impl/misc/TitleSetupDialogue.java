package com.koios.game.content.dialogue.impl.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.titles.Title;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.player.Player;

public class TitleSetupDialogue extends Dialogue {

	private static class Option {

		String text;
		Consumer<Player> action;

		public Option(String text, Consumer<Player> action) {
			this.text = Objects.requireNonNull(text);
			this.action = Objects.requireNonNull(action);
		}

	}

	private final Title current;
	private List<Title> available;
	private final List<Option> options = new ArrayList<>();

	public TitleSetupDialogue(Player player) {
		super(player);
		current = player.getTitle();

		if (current == null) {
			available = player.unlockedTitles;
		} else {
			// we don't want the current title to be in the list of available titles.
			available = player.unlockedTitles.stream().filter(t -> !t.equals(current)).collect(Collectors.toList());
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(-1)) {
			if (index == 1) {
				// De-activate current title
				setAndUpdateTitle(null);
				player.sendMessage("Title de-activated: <col=" + Integer.toHexString(current.getColor()) + ">" + current.getName() + "</col>.");
				stop();
			} else if (index == 2) {
				if (available.isEmpty()) {
					// Cancel
					stop();
				} else {
					// Use another title
					setPhase(0);
					pollNextBatch();
				}
			} else {
				// Cancel
				stop();
			}
		} else if (getPhase() != 0xBABE) {
			Option chosen = options.get(index - 1);

			if (chosen == null) {
				stop();
			} else {
				chosen.action.accept(player);
			}
		} else {
			stop();
		}
	}

	@Override
	protected void start() {
		if (current == null) {
			if (available.isEmpty()) {
				setPhase(0xBABE);
				sendStatement("You have not unlocked any titles.");
			} else {
				pollNextBatch();
			}
		} else if (available.isEmpty()) {
			setPhase(-1);
			choice("Select an Option", "De-activate current title", "Cancel");
		} else {
			setPhase(-1);
			choice("Select an Option", "De-activate current title", "Use another title", "Cancel");
		}
	}

	private void pollNextBatch() {
		options.clear();
		int startIndex = getPhase();
		int endIndex = Math.min(available.size(), startIndex + 4);
		setPhase(endIndex);

		for (int i = startIndex; i < endIndex; i++) {
			Title title = available.get(i);

			options.add(new Option("Set title: " + title.getName(), p -> {
				setAndUpdateTitle(title);
				player.sendMessage("Title activated: <col=" + Integer.toHexString(title.getColor()) + ">" + title.getName() + "</col>.");
				stop();
			}));
		}

		if (available.size() == 1) {
			options.add(new Option("Is it possible to unlock more titles?", p -> sendStatement("Of course!", "Visit the forum tutorials for more information!")));
		} else if (options.size() == 1) {
			setPhase(0);
			options.add(new Option("Back to Start", p -> pollNextBatch()));
		} else if (available.size() > endIndex) {
			options.add(new Option("More Titles", p -> pollNextBatch()));
		}

		choice(options.stream().map(option -> option.text).collect(Collectors.toList()));
	}

	private void choice(List<String> text) {
		String header = "(" + getPhase() + "/" + available.size() + ")";

		switch (text.size()) {
		case 2:
			choice(header, text.get(0), text.get(1));
			break;

		case 3:
			choice(header, text.get(0), text.get(1), text.get(2));
			break;

		case 4:
			choice(header, text.get(0), text.get(1), text.get(2), text.get(3));
			break;

		case 5:
			choice(header, text.get(0), text.get(1), text.get(2), text.get(3), text.get(4));
			break;

		default:
			throw new RuntimeException("invalid text.size(): " + text.size());
		}
	}

	private void setAndUpdateTitle(Title title) {
		player.setTitle(title);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

}
