package com.koios.game.content.dialogue.impl.edgeville;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class MandrithDialogue extends Dialogue {

	public MandrithDialogue(Player player) {
		super(player);
	}

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

	@Override
	protected void next() {
		if (isPhase(0)) {
			setPhase(1);
			mobChat(8725, Expression.HAPPY_TALKING, "Mandrith is the name.");
		} else if (isPhase(1)) {
			setPhase(2);
			chat(Expression.HAPPY_TALKING, "Errhh... What are you doing here?");
		} else if (isPhase(2)) {
			setPhase(3);
			mobChat(8725, Expression.HAPPY_TALKING, "Offering high-level equipment to those victorious in PvP.", "This business is brutal!");
		} else if (isPhase(3)) {
			setPhase(4);
			mobChat(8725, Expression.HAPPY_TALKING, "Now, what do you want?");
		} else if (isPhase(4)) {
			setPhase(5);
			choice("Select an Option", "I'd like to see what you have to offer", "I'm interested in my PvP statistics", "Exchange PvP Artefacts", "You're scary!");
		} else if (isPhase(6)) {
			setPhase(0xBABE);
			chat(Expression.HAPPY_TALKING, "Eek!");
		} else if (isPhase(7)) {
			choice("Select an Option", "Yes!", "Sorry, but no.");
		} else {
			stop();
		}
	}

	@Override
	protected void select(int index) {
		if (isPhase(5)) {
			if (index == 1) {
				stop();
				GameService.getShopManager().open(player, 1);
			} else if (index == 2) {
				setPhase(0xBABE);
				sendStatement("@dre@" + player.getName() + "'s PvP Statistics", "", "Kills: @dre@" + player.killCount, "Deaths: @dre@" + player.deathCount,
						"PvP Points: @dre@" + player.pvpPoints);
			} else if (index == 3) {

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
							mobChat(8725, Expression.HAPPY_TALKING, "You look to be carrying far too many PvP statuettes.", "Please come back with smaller quantities at a time!");
							return;
						}
						mobChat(8725, Expression.HAPPY_TALKING, "You are carrying " + NumberUtil.format(totalCost) + " coins worth",
								"in PvP artefacts. Would you like to sell them?");
						setPhase(7);
					} else {
						mobChat(8725, Expression.HAPPY_TALKING, "You do not have any artefacts.");
						setPhase(4);
					}
				}
			} else if (index == 4) {
				setPhase(6);
				mobChat(8725, Expression.HAPPY_TALKING, "Boo!");
			}
		} else if (isPhase(7)) {
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
	}

	@Override
	protected void start() {
		chat(Expression.HAPPY_TALKING, "Who are you?");
	}

}
