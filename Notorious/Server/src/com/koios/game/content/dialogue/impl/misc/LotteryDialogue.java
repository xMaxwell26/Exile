package com.koios.game.content.dialogue.impl.misc;

import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.content.lottery.LotteryManager;
import com.koios.game.content.lottery.LotteryConstants.LotteryTypes;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class LotteryDialogue extends Dialogue {

	public LotteryDialogue(Player player) {
		super(player);
	}

	@Override
	protected void start() {
		setPhase(0);
		mobChat(2169, Expression.HAPPY_TALKING, "Hello " + player.getName() + "! My name is Dromund, and", "I am the lotteries supervisor!", "What can I do for you?");
	}

	@Override
	protected void next() {
		if (getPhase() < 0) {
			stop();
			return;
		}

		switch (getPhase()) {
		case 0:
			choice("Select an Option", "Join the lottery", "How long does a lottery last?", "If I log out and win, will I lose my reward?", "Never mind, I have to go.");
			break;

		default:
			stop();
		}
	}

	@Override
	protected void select(int index) {

		if (isPhase(1)) {
			switch (index) {
			case 1:
				if (player.getInventory().hasItem(new Item(995, 500000))) {
					if (!LotteryManager.getLotteryList().containsKey(LotteryTypes.LOTTERY_500K)) {
						LotteryManager.addLottery(LotteryTypes.LOTTERY_500K);
					}

					if (LotteryManager.getLotteryList().get(LotteryTypes.LOTTERY_500K).addPlayer(player.getName())) {
						player.getInventory().remove(new Item(995, 500000));
						mobChat(2169, Expression.HAPPY_TALKING, "Thank you for joining the lottery!", "The winner will be rewarded in:",
								LotteryManager.getLotteryList().get(LotteryTypes.LOTTERY_500K).getExpireDateToString());
					} else {
						mobChat(2169, Expression.HAPPY_TALKING, "You can only add the same lottery round 3 times!", "Wait untill the next round starts or",
								"bet in another lottery!");
						setPhase(-1);
					}
				} else {
					mobChat(2169, Expression.HAPPY_TALKING, "You don't have the amount of cash needed!", "Return to me when you do.");
					setPhase(-1);
				}
				break;

			case 2:
				if (player.getInventory().hasItem(new Item(995, 5000000))) {
					if (!LotteryManager.getLotteryList().containsKey(LotteryTypes.LOTTERY_5M)) {
						LotteryManager.addLottery(LotteryTypes.LOTTERY_5M);
					}

					if (LotteryManager.getLotteryList().get(LotteryTypes.LOTTERY_5M).addPlayer(player.getName())) {
						player.getInventory().remove(new Item(995, 5000000));
						mobChat(2169, Expression.HAPPY_TALKING, "Thank you for joining the lottery!", "The winner will be rewarded in:",
								LotteryManager.getLotteryList().get(LotteryTypes.LOTTERY_5M).getExpireDateToString());
					} else {
						mobChat(2169, Expression.HAPPY_TALKING, "You can only add the same lottery round 3 times!", "Wait untill the next round starts or",
								"bet in another lottery!");
						setPhase(-1);
					}
				} else {
					mobChat(2169, Expression.HAPPY_TALKING, "You don't have the amount of cash needed!", "Return to me when you do.");
					setPhase(-1);
				}
				break;

			case 3:
				if (player.getInventory().hasItem(new Item(995, 50000000))) {
					if (!LotteryManager.getLotteryList().containsKey(LotteryTypes.LOTTERY_50M)) {
						LotteryManager.addLottery(LotteryTypes.LOTTERY_50M);
					}

					if (LotteryManager.getLotteryList().get(LotteryTypes.LOTTERY_50M).addPlayer(player.getName())) {
						player.getInventory().remove(new Item(995, 50000000));
						mobChat(2169, Expression.HAPPY_TALKING, "Thank you for joining the lottery!", "The winner will be rewarded in:",
								LotteryManager.getLotteryList().get(LotteryTypes.LOTTERY_50M).getExpireDateToString());
					} else {
						mobChat(2169, Expression.HAPPY_TALKING, "You can only add the same lottery round 3 times!", "Wait untill the next round starts or",
								"bet in another lottery!");
						setPhase(-1);
					}
				} else {
					mobChat(2169, Expression.HAPPY_TALKING, "You don't have the amount of cash needed!", "Return to me when you do.");
					setPhase(-1);
				}
				break;
			}
		}

		if (isPhase(0)) {
			switch (index) {
			case 1:
				choice("Select an Option", "Cheap lottery ( 500K per ticket )", "Intermediate lottery ( 5M per ticket )", "Expensive lottery ( 50M per ticket )",
						"Never mind, I have to go.");
				setPhase(1);
				break;

			case 2:
				mobChat(2169, Expression.HAPPY_TALKING, "Well it depends on the lottery you enter,", " the cheap lottery is 1 hour, the intermediate is 2",
						"and the expensive is 3 hours.");
				setPhase(0);
				break;

			case 3:
				mobChat(2169, Expression.HAPPY_TALKING, "Absolutely not! It will be waiting for you", "when you log in again, and will", "be placed in your bank account.");
				setPhase(0);
				break;

			case 4:
				stop();
				break;
			}
		}
	}

}
