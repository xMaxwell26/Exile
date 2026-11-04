package com.koios.network.in.impl;

import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * A decoder for the player appearance change packet.
 */
public class PacketChangeAppearance implements IncomingPacketDecoder {

	public static final int[][] ALLOWED_COLORS = { { 0, 11 }, // hair color
			{ 0, 15 }, // torso color
			{ 0, 15 }, // legs color
			{ 0, 5 }, // feet color
			{ 0, 7 } // skin color
	};

	public static final int[][] FEMALE_VALUES = { { 45, 54 }, // head
			{ -1, -1 }, // jaw
			{ 56, 60 }, // torso
			{ 61, 65 }, // arms
			{ 67, 68 }, // hands
			{ 70, 77 }, // legs
			{ 79, 80 }, // feet
	};

	public static final int[][] MALE_VALUES = { { 0, 8 }, // head
			{ 10, 17 }, // jaw
			{ 18, 25 }, // torso
			{ 26, 31 }, // arms
			{ 33, 34 }, // hands
			{ 36, 40 }, // legs
			{ 42, 43 }, // feet
	};

	@Override
	public void handle(Player player, GamePacket packet) {
		if (!player.hasFocus()) {
			return;
		}

		int gender = packet.readByte();

		if (gender != 0 && gender != 1) {
			return;
		}

		final int[] apperances = new int[MALE_VALUES.length];

		for (int i = 0; i < apperances.length; i++) {
			int value = packet.readByte();
			if (value < (gender == 0 ? MALE_VALUES[i][0] : FEMALE_VALUES[i][0]) || value > (gender == 0 ? MALE_VALUES[i][1] : FEMALE_VALUES[i][1])) {
				value = gender == 0 ? MALE_VALUES[i][0] : FEMALE_VALUES[i][0];
			}
			apperances[i] = value;
		}

		final int[] colors = new int[ALLOWED_COLORS.length];

		for (int i = 0; i < colors.length; i++) {
			int value = packet.readByte();
			if (value < ALLOWED_COLORS[i][0] || value > ALLOWED_COLORS[i][1]) {
				value = ALLOWED_COLORS[i][0];
			}
			colors[i] = value;
		}

		final int[] look = new int[13];

		final int appear = apperances.length;
		final int color = colors.length;

		look[0] = gender;

		for (int i = 1; i <= appear; i++) {
			look[i] = apperances[i - 1];
		}

		for (int i = appear + 1; i < (appear + color); i++) {
			look[i] = colors[i - appear - 1];
		}

		if (player.canChangeAppearance) {
			player.getAppearance().setLook(look);
			player.getSession().send(new SendRemoveInterfacePacket());
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.canChangeAppearance = false;
		}
	}

}
