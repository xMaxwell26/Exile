package com.koios.network.in.impl;

import java.util.regex.Pattern;

import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.util.StringUtil;

/**
 * Public chat
 */
public class PacketPublicChat implements IncomingPacketDecoder {

	/**
	 * Formatting used to remove text formatting (ex.: "@red@" and "<col=FF0000>")
	 */
	private static final Pattern INTERNAL_FORMATTING_PATTERNS = Pattern.compile("<.+>|@.+@|:.+:|#.+#");

	@Override
	public void handle(Player player, GamePacket packet) {
		if (player.isMuted() || !player.hasFocus() || player.isSinging()) {
			return;
		}

		// read the packet contents
		final int effect = packet.readByteS();
		final int color = packet.readByteS();
		final int size = packet.getSize();
		final byte[] bytes = packet.readReversedBytesA(size);
		String unmodifiedMessage = StringUtil.decode(bytes, bytes.length, false).toLowerCase();

		String messageContent = INTERNAL_FORMATTING_PATTERNS.matcher(unmodifiedMessage).replaceAll("");

		if (messageContent.isEmpty()) {
			return;
		}

		// perform message evaluations here - ie. detect spam messages
		if (!player.isSuperAdministrator()) {
			if (messageContent.contains(".com") || messageContent.contains(".org")) {
				return;
			}
		}

		if (!player.getController().canTalk(player)) {
			return;
		}

		// flag the player for a chat update.
		player.setChatTextEffects(effect);
		player.setChatTextColor(color);
		player.setChatTextSize((byte) messageContent.length());
		player.setChatText(StringUtil.encode(messageContent));
		player.getUpdateFlags().flag(UpdateFlag.CHAT);
		PacketReportAbuse.createChatSnapshot(player.getName(), unmodifiedMessage);
	}

}
