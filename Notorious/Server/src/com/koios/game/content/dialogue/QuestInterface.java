package com.koios.game.content.dialogue;

import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

public class QuestInterface {

	private static final int LABEL_TITLE_ID = 8144;

	private static final int INTERFACE_FRAME_ID = 8134;

	private static final int[] LABEL_ID_TABLE = new int[] { 8145, 8147, 8148, 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161, 8162, 8163, 8164, 8165,
			8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8177, 8178, 8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193,
			8194, 8195, 12174, 12175, 12176, 12177, 12178, 12179, 12180, 12181, 12182, 12183, 12184, 12185, 12186, 12187, 12188, 12189, 12190, 12191, 12192, 12193, 12194, 12195,
			12196, 12197, 12198, 12199, 12200, 12201, 12202, 12203, 12204, 12205, 12206, 12207, 12208, 12209, 12210, 12211, 12212, 12213, 12214, 12215, 12216, 12217, 12218, 12219,
			12220, 12221, 12222, 12223 };

	private String title;
	private final String[] content;
	private int size = 0;

	public QuestInterface() {
		content = new String[LABEL_ID_TABLE.length];
	}

	public boolean isFull() {
		return size >= LABEL_ID_TABLE.length;
	}

	public int size() {
		return size;
	}

	public QuestInterface withTitle(String title) {
		this.title = title;
		return this;
	}

	public QuestInterface addContent(Object obj) {
		if (isFull()) {
			throw new IndexOutOfBoundsException("Full!");
		}

		content[size++] = String.valueOf(obj);
		return this;
	}

	public void show(Player player) {
		for (int i = 0; i < LABEL_ID_TABLE.length; i++) {
			player.getSession().send(new SendStringPacket(content[i], LABEL_ID_TABLE[i]));
		}

		player.getSession().send(new SendStringPacket(title, LABEL_TITLE_ID));
		player.write(new GamePacketBuilder(219));
		player.getSession().send(new SendRemoveInterfacePacket());
		player.getInterfaceState().openInterface(INTERFACE_FRAME_ID);
	}

}
