package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class SinSeerDialogue extends Dialogue {

	private static enum StatuetteDefinition {

		BROKEN_STATUE_HEADDRESS(14892, 5000), THIRD_AGE_CARAFE(14891, 10000), BRONZED_DRAGON_CLAW(14890, 20000), ANCIENT_PSALTERY_BRIDGE(14889, 30000), SARADOMIN_AMPHORA(14888,
				40000), BANDOS_SCRIMSHAW(14887, 50000), SARADOMIN_CARVING(14886, 75000), ZAMORAK_MEDALLION(14885, 100000), ARMADYL_TOTEM(14884, 150000), GUTHIXIAN_BRAZIER(14883,
						200000), RUBY_CHALICE(14882, 250000), BANDOS_STATUETTE(14881, 300000), SARADOMIN_STATUETTE(14880, 400000), ZAMORAK_STATUETTE(14879,
								500000), ARMADYL_STATUETTE(14878, 750000), SEREN_STATUETTE(14877, 1000000), ANCIENT_STATUETTE(14876, 5000000);

		private final int id;
		private final int cost;

		private StatuetteDefinition(final int id, final int cost) {
			this.id = id;
			this.cost = cost << 1; // All prices are double the RuneScape value.
		}

	}

	private final int[] counts = new int[StatuetteDefinition.values().length];
	private final boolean[] slots = new boolean[28];
	private int totalCost = 0;

	public SinSeerDialogue(Player player) {
		super(player);
	}

	@Override
	protected void next() {
		if (isPhase(0) || isPhase(2)) {
			choice("Select an Option", "Yes!", "Sorry, but no.");
		} else {
			stop();
		}
	}

	@Override
	protected void select(final int index) {
		if (isPhase(0)) {
			if (index == 1) {
				if (totalCost == 0) {
					setPhase(1);
					chat(Expression.HAPPY_TALKING, "I am not carrying any PvP statuettes.");
				} else {
					setPhase(2);
					mobChat(5571, Expression.HAPPY_TALKING, "You are carrying " + NumberUtil.format(totalCost) + " coins worth", "in PvP artefacts. Would you like to sell them?");
				}
			} else {
				stop();
			}
		} else if (isPhase(2)) {
			if (index == 1) {
				int coins = player.getInventory().getCount(995);

				if (coins + totalCost < 0) // overflow
				{
					setPhase(1);
					chat(Expression.HAPPY_TALKING, "I do not have enough inventory space to accept this.");
				} else {
					if (player.getInventory().contains(StatuetteDefinition.ANCIENT_STATUETTE.id)) {
						AchievementManager.performed(player, Achievement.EXCHANGE_ANCIENT_ARTIFACT);
					}

					for (int i = 0; i < slots.length; i++) {
						if (slots[i]) {
							player.getInventory().set(i, new Item(-1));
						}
					}

					player.getInventory().add(new Item(995, totalCost));
					player.sendMessage("Sold PvP artefacts worth " + NumberUtil.format(totalCost) + " coins.");
					stop();
				}
			} else if (index == 2) {
				stop();
			}
		}
	}

	@Override
	protected void start() {
		for (int n = 0; n < StatuetteDefinition.values().length; n++) {
			StatuetteDefinition definition = StatuetteDefinition.values()[n];

			for (int i = 0; i < 28; i++) {

				if (player.getInventory().get(i) == null) {
					continue;
				}

				final int id = player.getInventory().get(i).getId();

				if (id == definition.id) {
					counts[n] += player.getInventory().get(i).getCount();
					slots[i] = true;
				} else if (id == ItemDefinition.forId(definition.id).getNoteId()) {
					counts[n] += player.getInventory().get(i).getCount();
					slots[i] = true;
				}
			}

			if (counts[n] > 0) {
				try {
					// Overflow concious code
					int collectiveCost = Math.multiplyExact(counts[n], definition.cost);
					totalCost = Math.addExact(totalCost, collectiveCost);
				} catch (ArithmeticException ex) {
					setPhase(0xBABE);
					mobChat(5571, Expression.HAPPY_TALKING, "Hiya!", "You look to be carrying far too many PvP statuettes.", "Please come back with smaller quantities at a time!");
					return;
				}
			}
		}

		mobChat(5571, Expression.HAPPY_TALKING, "Hello, " + player.getName() + ".", "Do you happen to carry any PvP artefacts with you?");
	}

}
