package com.koios.game.content.lottery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.game.content.lottery.LotteryConstants.LotteryTypes;

/**
 * Lottery interface to create new lotterys
 *
 * @author Gabriel | Wolfs Darker
 */
public class Lottery {

	private long expireDate;

	private final List<String> player_list = new CopyOnWriteArrayList<>();

	private LotteryTypes type = LotteryTypes.LOTTERY_500K;

	public Lottery(LotteryTypes type) {
		this.type = type;

		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MMM/dd/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat(":mm:ss MMM/dd/yyyy");
			String date = calendar.get(Calendar.HOUR_OF_DAY) + type.getDuration() + sdf2.format(today);
			calendar.setTime(sdf.parse(date));
			expireDate = calendar.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a player to the lottery
	 *
	 * @param s player's name
	 */
	public boolean addPlayer(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}

		int count = 0;

		for (String name : player_list) {
			if (name == s) {
				count++;
			}
		}

		if (count > 3) {
			return false;
		}

		player_list.add(s);
		return true;
	}

	/**
	 * Lottery's expire date ( rewards the winner )
	 *
	 * @return the date
	 */
	public long getExpireDate() {
		return expireDate;
	}

	/**
	 * Get the expire time to string ( x hours and y minutes )
	 *
	 * @return the time
	 */
	public String getExpireDateToString() {
		String s = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(expireDate);
		long delta = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		long seconds = delta / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;

		if (hours > 0 && minutes <= 60) {
			hours = 0;
		}

		if (hours > 0 && minutes > 60) {
			minutes -= hours * 60;
		}

		s += hours > 0 ? hours + (hours > 1 ? " hours" : " hour") : "";
		s += minutes > 0 ? (s.length() > 0 ? " and " : "") + minutes + (minutes > 1 ? " minutes" : " minute") : "";

		if (hours <= 0 && minutes <= 0) {
			s = seconds > 0 ? seconds + (seconds > 1 ? " seconds" : " second") : "";
		}

		return s;
	}

	/**
	 * Get the lottery's type
	 *
	 * @return the type
	 */
	public LotteryTypes getLotteryType() {
		return type;
	}

	/**
	 * Get the players that joined the lottery
	 *
	 * @return the players
	 */
	public List<String> getPlayerList() {
		return player_list;
	}

	/**
	 * Get the reward amount ( player_list size * ticket value )
	 *
	 * @return the current bet
	 */
	public int getReward() {
		return (int) (player_list.isEmpty() ? 0 : player_list.size() * type.getTicketCost() * 0.8);
	}

	/**
	 * Set the expire date
	 *
	 * @param l the new date
	 */
	public void setExpireDate(long l) {
		expireDate = l;
	}

}
