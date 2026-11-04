package com.koios.game.content.lottery;

import java.util.Iterator;
import java.util.LinkedHashMap;

import com.koios.task.Task;

/**
 * Lottery update task
 *
 * @author Gabriel | Wolfs Darker
 */
public class LotteryTask extends Task {

	public LotteryTask() {
		super(1);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected void execute() {
		for (Iterator<Lottery> it = new LinkedHashMap<>(LotteryManager.getLotteryList()).values().iterator(); it.hasNext();) {
			Lottery lottery = it.next();

			try {

				if (LotteryManager.isLotteryOver(lottery.getExpireDate())) {
					if (lottery.getPlayerList().size() > 0) {
						LotteryManager.finishLottery(lottery);
					}

					LotteryManager.getLotteryList().remove(lottery);
					it.remove();
				}
			} catch (Exception ex) {
				System.err.println("[ Error ] Lottery Type: " + lottery.getLotteryType().toString());
				ex.printStackTrace();
			}
		}
	}

}
