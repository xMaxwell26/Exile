package com.koios.game.content.lottery;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koios.game.content.lottery.LotteryConstants.OfflineRewards;

/**
 * Write the lottery's data and load them
 *
 * @author Gabriel | Wolfs Darker
 */
public class LotteryWriter {

	/**
	 * Load all the list of lottery information from json file.
	 */
	public static void loadLotterys() {
		try (FileReader reader = new FileReader("./data/lottery/lotterys.json")) {
			Gson g = new Gson();
			Lottery[] lotterys = g.fromJson(reader, Lottery[].class);

			if (lotterys != null && lotterys.length > 0) {
				for (Lottery l : lotterys) {
					if (l.getPlayerList().size() > 0) {
						LotteryManager.addLottery(l);
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Load all offline player's rewards from json file.
	 */
	public static void loadRewards() {
		try (FileReader reader = new FileReader("./data/lottery/rewards.json")) {
			Gson gson = new Gson();
			OfflineRewards[] offline_rewards = gson.fromJson(reader, OfflineRewards[].class);

			if (offline_rewards != null && offline_rewards.length > 0) {
				for (OfflineRewards of : offline_rewards) {
					LotteryManager.addReward(of);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Save all the lotterys in a json file.
	 */
	public static void saveLotterys() {
		final Gson format = new GsonBuilder().setPrettyPrinting().create();
		Lottery[] l_l = new Lottery[LotteryManager.getLotteryList().size()];
		int i = 0;

		for (Lottery lottery : LotteryManager.getLotteryList().values()) {
			l_l[i++] = lottery;
		}

		try (BufferedWriter composer = new BufferedWriter(new FileWriter("./data/lottery/lotterys.json"))) {
			composer.write(format.toJson(l_l));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Save all offline player's rewards into a json file.
	 */
	public static void saveRewards() {
		if (LotteryManager.getLotteryList().isEmpty()) {
			return;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		OfflineRewards[] offlineRewards = new OfflineRewards[LotteryManager.getOfflineRewardList().size()];
		int i = 0;

		for (OfflineRewards l : LotteryManager.getOfflineRewardList()) {
			offlineRewards[i++] = l;
		}

		try (BufferedWriter composer = new BufferedWriter(new FileWriter("./data/lottery/rewards.json"))) {
			composer.write(gson.toJson(offlineRewards));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
