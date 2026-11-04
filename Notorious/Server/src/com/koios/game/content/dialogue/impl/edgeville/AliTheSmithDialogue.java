package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.GameService;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class AliTheSmithDialogue extends Dialogue {

	private static final int[][] BROKEN_BARROWS_IDS = { { 4708, 4860 }, { 4710, 4866 }, { 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 }, { 4720, 4896 }, { 4718, 4890 },
			{ 4720, 4896 }, { 4722, 4902 }, { 4732, 4932 }, { 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 }, { 4724, 4908 }, { 4726, 4914 }, { 4728, 4920 }, { 4730, 4926 },
			{ 4745, 4956 }, { 4747, 4926 }, { 4749, 4968 }, { 4751, 4794 }, { 4753, 4980 }, { 4755, 4986 }, { 4757, 4992 }, { 4759, 4998 } };

	public AliTheSmithDialogue(Player player) {
		super(player);
	}

	/*
	 * TODO Using a newly degraded 100 item on Bob on OSRS results in the following single-npc-statement dialogue:
	 * 
	 * "Sorry friend, but I can't do anything with that."
	 */

	private void fixAllBarrows() {
		int totalCost = 0;
		int cashAmount = player.getInventory().getCount(995);

		for (int i = 0; i < player.getInventory().getCapacity(); i++) {
			if (player.getInventory().get(i) == null) {
				continue;
			}
			boolean breakOut = false;

			for (int[] element : BROKEN_BARROWS_IDS) {
				if (player.getInventory().get(i).getId() == element[1]) {
					if (totalCost + 800000 > cashAmount) {
						breakOut = true;
						player.sendMessage("You don't have enough coins.");
						break;
					} else {
						totalCost += 800000;
					}
					player.getInventory().set(i, new Item(element[0] + 1));
				}
			}

			if (breakOut) {
				break;
			}
		}

		if (totalCost > 0) {
			player.getInventory().remove(new Item(995, totalCost));
		}
	}

	@Override
	public void next() {
		if (isPhase(0)) {
			setPhase(50); // next will stop
			mobChat(2820, Expression.HAPPY_TALKING, "Get yer own!");
		} else if (isPhase(1)) {
			setPhase(3); // next will stop
			mobChat(2820, Expression.HAPPY_TALKING, "Yes! I buy and sell axes! Take your pick (or axe)!");
		} else if (isPhase(2)) {
			fixAllBarrows();
			stop();
		} else if (isPhase(3)) {
			stop();
			GameService.getShopManager().open(player, 7);
		} else {
			stop();
		}
	}

	@Override
	public void select(final int index) {
		if (isPhase(0)) {
			if (index == 1) {
				chat(Expression.HAPPY_TALKING, "Give me a quest!");
			} else if (index == 2) {
				setPhase(1);
				chat(Expression.HAPPY_TALKING, "Have you anything to sell?");
			} else if (index == 3) {
				setPhase(2);
				chat(Expression.HAPPY_TALKING, "Can you repair my items for me?");
			}
		}
	}

	@Override
	protected void start() {
		choice("Select an Option", "Give me a quest!", "Have you anything to sell?", "Can you repair my items for me?");
	}

}
