package com.koios.game.content.dialogue.impl.util;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.player.Player;

public final class SimpleDialogues {

	public static void sendStatement(Player player, String... lines) {
		player.dialogue().start(new Dialogue(player) {
			@Override
			protected void start() {
				sendStatement(lines);
			}

			@Override
			protected void next() {
				stop();
			}
		});
	}

	public static void sendMobStatement(Player player, int id, String... lines) {
		player.dialogue().start(new Dialogue(player) {
			@Override
			protected void start() {
				mobChat(id, Expression.CALM_TALK, lines);
			}

			@Override
			protected void next() {
				stop();
			}
		});
	}

	private SimpleDialogues() {

	}

}