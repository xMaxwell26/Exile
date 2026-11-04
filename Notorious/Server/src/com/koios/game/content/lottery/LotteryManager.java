package com.koios.game.content.lottery;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.koios.GameService;
import com.koios.game.content.lottery.LotteryConstants.LotteryTypes;
import com.koios.game.content.lottery.LotteryConstants.OfflineRewards;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Manage the lottery list and system
 *
 * @author Gabriel | Wolfs Darker
 */
public class LotteryManager {

	/**
	 * List of all lotterys
	 */
	private static Map<LotteryTypes, Lottery> lottery_list = new HashMap<>();

	/**
	 * Handle rewards for offline players
	 */
	private static List<OfflineRewards> offline_rewards = new CopyOnWriteArrayList<>();

	/**
	 * Class logger.
	 */
	private static Logger logger = Logger.getLogger("Lottery Manager");

	/**
	 * Initialize the lottery system ( load lotterys, offline rewards and starts the tick )
	 */
	public static void init() {
		offline_rewards.clear();
		LotteryWriter.loadLotterys();
		LotteryWriter.loadRewards();
		logger.info("Loaded " + lottery_list.size() + " lotterys and " + offline_rewards.size() + " rewards.");
		GameService.getTaskScheduler().schedule(new LotteryTask());
	}

	/**
	 * Get the lottery list
	 *
	 * @return the list
	 */
	public static Map<LotteryTypes, Lottery> getLotteryList() {
		synchronized (lottery_list) {
			return new LinkedHashMap<>(lottery_list);
		}
	}

	/**
	 * Get the lottery list
	 *
	 * @return the list
	 */
	public static List<OfflineRewards> getOfflineRewardList() {
		synchronized (offline_rewards) {
			return new LinkedList<>(offline_rewards);
		}
	}

	/**
	 * Check if the lottery duration is over.
	 *
	 * @param s
	 * @return if it is over
	 */
	public static boolean isLotteryOver(long time) {
		Calendar calendar = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return today.getTimeInMillis() - calendar.getTimeInMillis() >= 0;
	}

	/**
	 * Finish a lottery and reward the winner
	 *
	 * @param l
	 */
	public static void finishLottery(Lottery l) {
		if (l.getPlayerList().isEmpty()) {
			return;
		}

		String winner = l.getPlayerList().get(NumberUtil.inclusiveRandom(l.getPlayerList().size() - 1));

		if (!GameService.getWorld().isPlayerLoggedIn(winner)) {
			addReward(new OfflineRewards(winner, l.getReward()));
		} else {
			Optional<Player> p = GameService.getWorld().getByUsername(winner);

			if (!p.isPresent()) {
				addReward(new OfflineRewards(winner, l.getReward()));
				return;
			}

			Player player = p.get();
			int reward = l.getReward();

			if (player.getInventory().freeSlots() <= 0) {
				if (Bank.addToBank(player, new Item(995, reward))) {
					player.sendMessage("[ <col=255>Lottery</col> ] You won <col=255>" + NumberUtil.format(reward) + "</col> coins in the lottery!");
					player.sendMessage("[ <col=255>Lottery</col> ] Check your bank for the reward!");
				} else {

					GroundItemManager.spawnNew(player, 995, player.getLocation().getX(), player.getLocation().getY(), reward);
					player.sendMessage("[ <col=255>Lottery</col> ] You won <col=255>" + NumberUtil.format(reward) + "</col> coins in the lottery!");
					player.sendMessage("[ <col=255>Lottery</col> ] You have no space on your inventory or bank, pick your reward from the ground!");
				}
			} else {
				player.getInventory().add(new Item(995, reward));
				player.sendMessage("[ <col=255>Lottery</col> ] You won <col=255>" + reward + "</col> coins in the lottery!");
			}
		}

		lottery_list.remove(l.getLotteryType());
		LotteryWriter.saveLotterys();
		LotteryWriter.saveRewards();
	}

	/**
	 * Check if a player has a reward waiting.
	 */
	public static void checkReward(Player player) {
		for (int i = 0; i < offline_rewards.size(); i++) {
			OfflineRewards of = offline_rewards.get(i);

			if (of == null || of.getWinner() == null || of.getWinner().length() <= 0 || of.getReward() <= 0) {
				offline_rewards.remove(i);
				continue;
			}

			if (of.getWinner().toLowerCase().equals(player.getName().toLowerCase())) {
				int reward = of.getReward();

				if (reward <= 0) {
					return;
				}

				if (Bank.addToBank(player, new Item(995, reward))) {
					player.sendMessage("[ <col=255>Lottery</col> ] You won <col=255>" + NumberUtil.format(reward) + "</col> coins in the lottery!");
					player.sendMessage("[ <col=255>Lottery</col> ] Check your bank for the reward!");
					offline_rewards.remove(i);
				} else {
					if (player.getInventory().add(new Item(995, reward))) {
						player.getInventory().add(new Item(995, reward));
						player.sendMessage("[ <col=255>Lottery</col> ] You won <col=255>" + reward + "</col> coins in the lottery!");
						offline_rewards.remove(i);
					}
				}
			}
		}

		LotteryWriter.saveRewards();
	}

	/**
	 * Add a new lottery to the list by it's type
	 *
	 * @param type lottery type
	 */
	public static void addLottery(LotteryTypes type) {
		if (type == null || lottery_list.containsKey(type)) {
			return;
		}

		lottery_list.put(type, new Lottery(type));
		LotteryWriter.saveLotterys();
	}

	/**
	 * Add a new lottery to the list
	 *
	 * @param l lottery
	 */
	public static void addLottery(Lottery l) {
		if (lottery_list.containsKey(l.getLotteryType())) {
			return;
		}

		lottery_list.put(l.getLotteryType(), l);
	}

	/**
	 * Add a new offline reward to the list
	 *
	 * @param of
	 */
	public static void addReward(OfflineRewards of) {
		int current_prize = 0;

		for (int i = 0; i < offline_rewards.size(); i++) {
			OfflineRewards off = offline_rewards.get(i);

			if (off == null || off.getWinner() == null || off.getWinner().length() <= 0 || of.getReward() <= 0) {
				offline_rewards.remove(i);
				return;
			}

			if (off.getWinner().equals(of.getWinner())) {
				current_prize = off.getReward();
				offline_rewards.remove(i);
			}
		}

		of = new OfflineRewards(of.getWinner(), of.getReward() + current_prize);
		offline_rewards.add(of);
		LotteryWriter.saveRewards();
	}

}
