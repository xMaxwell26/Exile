package com.koios.game.content.lottery;

/**
 * Handle all the lottery's constants
 *
 * @author Gabriel | Wolfs Darker
 */
public class LotteryConstants {

	public enum LotteryTypes {

		LOTTERY_500K(1, 500_000), LOTTERY_1M(1, 1_000_000), LOTTERY_5M(2, 5_000_000), LOTTERY_50M(3, 50_000_000);

		/**
		 * Lottery's duration in hours
		 */
		private final int duration;

		/**
		 * Lottery's ticket to enter cost
		 */
		private final int ticketCost;

		private LotteryTypes(int duration, int ticketCost) {
			this.duration = duration;
			this.ticketCost = ticketCost;
		}

		/**
		 * Get the lottery's duration in hours
		 *
		 * @return the duration
		 */
		public int getDuration() {
			return duration;
		}

		/**
		 * Get the lottery's ticket cost
		 *
		 * @return the cost
		 */
		public int getTicketCost() {
			return ticketCost;
		}

	}

	public static class OfflineRewards {

		private final String winner;
		private final int reward;

		public OfflineRewards(String winner, int reward) {
			this.winner = winner;
			this.reward = reward;
		}

		/**
		 * Get the reward winner
		 *
		 * @return the winner
		 */
		public String getWinner() {
			return winner;
		}

		/**
		 * Get the reward amount
		 *
		 * @return the reward amount
		 */
		public int getReward() {
			return reward;
		}

	}

}
