package com.koios.game.content.combat.mob;

import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public class Godwars {

	// 0 = Armadyl
	// 1 = Bandos
	// 2 = Saradomin
	// 3 = Zamorak

	public static final int[][] GODWARS_MOBS = { { 6222, 6223, 6225, 6227, 6229, 6230, 6231, 6232, 6233, 6234, 6238, 6239, 6240, 6241, 6242, 6243, 6244, 6245, 6246 },
			{ 6260, 6261, 6263, 6265, 6268, 6269, 6270, 6271, 6272, 6273, 6274, 6275, 6276, 6277, 6278, 6279, 6280, 6281, 6282, 6283 },
			{ 6247, 6248, 6250, 6252, 6254, 6255, 6256, 6257, 6258, 6259 }, { 6203, 6204, 6206, 6208, 6211, 6212, 6213, 6214, 6215, 6216, 6217, 6218, 6219, 6220 } };

	public static void updateInterface(Player player) {
		if (player.inGodwars()) {
			for (int i = 0; i < 4; i++) {
				if (player.getGodwardsKc()[i] < 0) {
					player.getGodwardsKc()[i] = 0;
				}

				player.getSession().send(new SendStringPacket(String.valueOf(player.getGodwardsKc()[i]), 14506 + i));
			}
		}
	}

	public static void increaseKillCount(Player player, int minion) {
		int god = -1;

		for (int i = 0; i < GODWARS_MOBS.length; i++) {
			for (int mob : GODWARS_MOBS[i]) {
				if (mob == minion) {
					god = i;
				}
			}
		}

		if (god == -1) {
			return;
		}

		player.getGodwardsKc()[god]++;
		updateInterface(player);
	}

}
