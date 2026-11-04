package com.koios.network;

import com.koios.GameEngine;
import com.koios.game.model.player.Player;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.in.impl.PacketBank10;
import com.koios.network.in.impl.PacketBank5;
import com.koios.network.in.impl.PacketBankAll;
import com.koios.network.in.impl.PacketBankAllButOne;
import com.koios.network.in.impl.PacketBankModifiableX;
import com.koios.network.in.impl.PacketBankSearch;
import com.koios.network.in.impl.PacketBankX1;
import com.koios.network.in.impl.PacketChangeAppearance;
import com.koios.network.in.impl.PacketClickButton;
import com.koios.network.in.impl.PacketClickMob;
import com.koios.network.in.impl.PacketClickingInGame;
import com.koios.network.in.impl.PacketClickingStuff;
import com.koios.network.in.impl.PacketCommand;
import com.koios.network.in.impl.PacketFriendsAction;
import com.koios.network.in.impl.PacketGroundItemOption1;
import com.koios.network.in.impl.PacketGroundItemOption2;
import com.koios.network.in.impl.PacketIdleIndication;
import com.koios.network.in.impl.PacketItemOption1;
import com.koios.network.in.impl.PacketItemOption2;
import com.koios.network.in.impl.PacketItemOption3;
import com.koios.network.in.impl.PacketItemOption4;
import com.koios.network.in.impl.PacketItemOption5;
import com.koios.network.in.impl.PacketItemWithGroundItem;
import com.koios.network.in.impl.PacketItemWithItem;
import com.koios.network.in.impl.PacketItemWithMob;
import com.koios.network.in.impl.PacketItemWithObject;
import com.koios.network.in.impl.PacketItemWithPlayer;
import com.koios.network.in.impl.PacketMoveInventoryItem;
import com.koios.network.in.impl.PacketNameInput;
import com.koios.network.in.impl.PacketNextDialogue;
import com.koios.network.in.impl.PacketObjectOption;
import com.koios.network.in.impl.PacketPublicChat;
import com.koios.network.in.impl.PacketRegionChanged;
import com.koios.network.in.impl.PacketReportAbuse;
import com.koios.network.in.impl.PacketSelectPlayerOption;
import com.koios.network.in.impl.PacketSkillGoal;
import com.koios.network.in.impl.PacketSpellWithGroundItem;
import com.koios.network.in.impl.PacketSpellWithItem;
import com.koios.network.in.impl.PacketSpellWithPlayer;
import com.koios.network.in.impl.PacketUnequipItem;
import com.koios.network.in.impl.PacketValueInput;
import com.koios.network.in.impl.PacketWalk;
import com.koios.network.in.impl.PacketWindowFocusChanged;

public final class ReceivedPacketExecutor {

	private static final IncomingPacketDecoder[] DECODERS = new IncomingPacketDecoder[256];

	static {
		PacketObjectOption co = new PacketObjectOption();
		PacketWalk walking = new PacketWalk();
		PacketClickMob cn = new PacketClickMob();
		PacketRegionChanged changeRegions = new PacketRegionChanged();
		PacketSelectPlayerOption selectPlayerOption = new PacketSelectPlayerOption();
		PacketFriendsAction pm = new PacketFriendsAction();

		DECODERS[3] = new PacketWindowFocusChanged();
		DECODERS[218] = new PacketReportAbuse();
		DECODERS[14] = new PacketItemWithPlayer();
		DECODERS[40] = new PacketNextDialogue();
		DECODERS[132] = co;
		DECODERS[252] = co;
		DECODERS[70] = co;
		DECODERS[57] = new PacketItemWithMob();
		DECODERS[72] = cn;
		DECODERS[131] = cn;
		DECODERS[155] = cn;
		DECODERS[17] = cn;
		DECODERS[21] = cn;
		DECODERS[16] = new PacketItemOption3();
		DECODERS[75] = new PacketItemOption4();
		DECODERS[122] = new PacketItemOption1();
		DECODERS[241] = new PacketClickingInGame();
		DECODERS[253] = new PacketGroundItemOption2();
		DECODERS[4] = new PacketPublicChat();
		DECODERS[236] = new PacketGroundItemOption1();
		DECODERS[87] = new PacketItemOption5();
		DECODERS[185] = new PacketClickButton();
		DECODERS[130] = new PacketClickingStuff();
		DECODERS[103] = new PacketCommand();
		DECODERS[214] = new PacketMoveInventoryItem();
		DECODERS[237] = new PacketSpellWithItem();
		DECODERS[181] = new PacketSpellWithGroundItem();
		DECODERS[202] = new PacketIdleIndication();
		DECODERS[73] = selectPlayerOption;
		DECODERS[249] = new PacketSpellWithPlayer();
		DECODERS[128] = selectPlayerOption;
		DECODERS[139] = selectPlayerOption;
		DECODERS[39] = selectPlayerOption;
		DECODERS[41] = new PacketItemOption2();
		DECODERS[145] = new PacketUnequipItem();
		DECODERS[117] = new PacketBank5();
		DECODERS[43] = new PacketBank10();
		DECODERS[129] = new PacketBankAll();
		DECODERS[101] = new PacketChangeAppearance();
		DECODERS[188] = pm;
		DECODERS[126] = pm;
		DECODERS[215] = pm;
		DECODERS[59] = pm;
		DECODERS[95] = pm;
		DECODERS[133] = pm;
		DECODERS[135] = new PacketBankX1();
		DECODERS[208] = new PacketValueInput();
		DECODERS[98] = walking;
		DECODERS[164] = walking;
		DECODERS[248] = walking;
		DECODERS[53] = new PacketItemWithItem();
		DECODERS[192] = new PacketItemWithObject();
		DECODERS[25] = new PacketItemWithGroundItem();
		DECODERS[121] = changeRegions;
		DECODERS[210] = changeRegions;
		DECODERS[60] = new PacketNameInput();
		DECODERS[61] = new PacketBankSearch();
		DECODERS[62] = new PacketSkillGoal();
		DECODERS[63] = new PacketSkillGoal();
		DECODERS[140] = new PacketBankAllButOne();
		DECODERS[141] = new PacketBankModifiableX();
	}

	public static void processPacket(Player player, GamePacket packet) {

		try {
			if (packet.getOpcode() < 0 || packet.getOpcode() >= DECODERS.length) {
				System.err.println("Invalid packet.getOpcode(): ['" + player.getName() + "', " + packet.getOpcode() + ", " + packet.getSize() + "]");
				return;
			}

            com.koios.game.arpg.Tutorial.releaseLegacyRestrictions(player);

			if (DECODERS[packet.getOpcode()] != null) {
				DECODERS[packet.getOpcode()].handle(player, packet);
			}
		} catch (Throwable t) {
			System.err.println("Error occurred during packet read: ['" + player.getName() + "', " + packet.getOpcode() + ", " + packet.getSize() + "]");

			t.printStackTrace();
			GameEngine.queueLogout(player);
		}
	}

}
