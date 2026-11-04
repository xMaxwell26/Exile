package com.koios.game.content.skill.herblore;

import java.util.Objects;
import java.util.regex.Pattern;

import com.koios.game.content.combat.Combat;
import com.koios.game.content.effects.impl.Antifire;
import com.koios.game.content.effects.impl.DrunkEffect;
import com.koios.game.content.effects.impl.Overload;
import com.koios.game.content.effects.impl.PrayerRenewal;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;

/**
 * @author Sanity
 */
public class Potions {

	private final Player player;

	private static final Pattern CHARGE_PATTERN = Pattern.compile(" \\([1-4]\\)");

	public Potions(Player player) {
		this.player = Objects.requireNonNull(player);
	}

	private void curePoison(long delay) {
		if (player.getEF().isEffectListed("POISON")) {
			player.getEF().removeEffect("POISON");
		}
	}

	private void doTheBrew(int itemId, int replaceItem, int slot) {
		if (player.getDuel().isRuleActive(DuelingRules.NO_POTIONS)) {
			player.sendMessage("You may not eat in this duel.");
			return;
		}

		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		int[] toDecrease = { 0, 2, 4, 6 };

		for (int skillId : toDecrease) {
			player.getSkill().decrementLevel(skillId, getBrewStat(skillId, 0.10));
		}

		player.getSkill().incrementLevel(1, getBrewStat(1, 0.20));

		if (player.getSkill().getLevel(Skill.DEFENCE) > player.getSkill().getLevelForExperience(Skill.DEFENCE) * 1.2) {
			player.getSkill().setLevel(Skill.DEFENCE, (int) (player.getSkill().getLevelForExperience(Skill.DEFENCE) * 1.2));
		}

		player.getSkill().incrementLevel(3, getBrewStat(3, 0.15));

		if (player.getHitpoints() > player.getMaxHitpoints() * 1.17) {
			player.getSkill().setLevel(Skill.HITPOINTS, (int) (player.getMaxHitpoints() * 1.17));
		}

	}

	private void drinkAntiPoison(int itemId, int replaceItem, int slot, long delay) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		curePoison(delay);
	}

	private void drainkAntiFire(int itemId, int replaceItem, int slot) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		player.getEF().queueEffect(new Antifire(player));
	}

	private void drinkEnergyPotion(int itemId, int replaceItem, int slot, long delay, boolean isSuper) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);

		player.getRunEnergy().setEnergy(player.getRunEnergy().getEnergy() + (isSuper ? 20 : 10));

		if (player.getRunEnergy().getEnergy() > 100) {
			player.getRunEnergy().setEnergy(100);
		}
	}

	private void drinkExtremePotion(int itemId, int replaceItem, int slot, int stat, boolean sup) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		extremeEnhanceState(stat, sup);
	}

	private void drinkExtremePrayer(int itemId, int replaceItem, int slot, boolean rest) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);

		player.getSkill().incrementLevel(5, (int) (player.getSkill().getLevelForExperience(5) * .38));

		if (rest) {
			player.getSkill().incrementLevel(5);
		}

		if (player.getSkill().getLevel(5) > player.getSkill().getLevelForExperience(5)) {
			player.getSkill().restoreLevel(5);
		}

		if (rest) {
			restoreStats();
		}
	}

	private void drinkPrayerPot(int itemId, int replaceItem, int slot, boolean rest) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);

		player.getSkill().incrementLevel(5, (int) (player.getSkill().getLevelForExperience(5) * .33));

		if (rest) {
			player.getSkill().incrementLevel(5);
		}

		if (player.getSkill().getLevel(5) > player.getSkill().getLevelForExperience(5)) {
			player.getSkill().restoreLevel(5);
		}

		if (rest) {
			restoreStats();
		}
	}

	private void drinkSummoningPot(int itemId, int replaceItem, int slot, boolean rest) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);

		player.getSkill().incrementLevel(23, (int) (player.getSkill().getLevelForExperience(23) * .25 + 7));
		if (rest) {
			player.getSkill().incrementLevel(23);
		}

		if (player.getSkill().getLevel(23) > player.getSkill().getLevelForExperience(23)) {
			player.getSkill().restoreLevel(23);
		}

		if (rest) {
			restoreStats();
		}
	}

	private void drinkStatPotion(int itemId, int replaceItem, int slot, int stat, boolean sup) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		enchanceStat(stat, sup);
	}

	private void enchanceStat(int skillID, boolean sup) {
		player.getSkill().incrementLevel(skillID, getBoostedStat(skillID, sup));
	}

	private void extremeEnhanceState(int skillID, boolean sup) {
		player.getSkill().incrementLevel(skillID, getExtremeStat(skillID, sup));
	}

	private int getBoostedStat(int skill, boolean sup) {
		int increaseBy;

		if (sup) {
			increaseBy = (int) (player.getSkill().getLevelForExperience(skill) * .20);
		} else {
			increaseBy = (int) (player.getSkill().getLevelForExperience(skill) * .13) + 1;
		}

		if (player.getSkill().getLevel(skill) + increaseBy > player.getSkill().getLevelForExperience(skill) + increaseBy + 1) {
			return player.getSkill().getLevelForExperience(skill) + increaseBy - player.getSkill().getLevel(skill);
		}

		return increaseBy;
	}

	private int getBrewStat(int skill, double amount) {
		return (int) (player.getSkill().getLevelForExperience(skill) * amount);
	}

	private int getExtremeStat(int skill, boolean sup) {
		int increaseBy;

		if (sup) {
			increaseBy = (int) (player.getSkill().getLevelForExperience(skill) * .25);
		} else {
			increaseBy = (int) (player.getSkill().getLevelForExperience(skill) * .25) + 1;
		}

		if (player.getSkill().getLevel(skill) + increaseBy > player.getSkill().getLevelForExperience(skill) + increaseBy + 1) {
			return player.getSkill().getLevelForExperience(skill) + increaseBy - player.getSkill().getLevel(skill);
		}

		return increaseBy;
	}

	public void handlePotion(int id, int slot) {
		if (player.isDead() || player.getHitpoints() <= 0) {
			return;
		}

		if (player.getDuel().isRuleActive(DuelingRules.NO_POTIONS)) {
			player.sendMessage("You may not drink potions in this duel.");
			return;
		}

		if (System.currentTimeMillis() - player.potDelay >= 1400) {
			player.potDelay = System.currentTimeMillis();
			Combat.resetCombat(player);

			String name = ItemDefinition.forId(id).getName();
			player.sendMessage("You drink some of your " + CHARGE_PATTERN.matcher(name).replaceAll("").toLowerCase() + ".");

			if (name.endsWith("(4)")) {
				player.sendMessage("You have 3 doses of the potion left.");
			} else if (name.endsWith("(3)")) {
				player.sendMessage("You have 2 doses of the potion left.");
			} else if (name.endsWith("(2)")) {
				player.sendMessage("You have 1 dose of the potion left.");
			} else if (name.endsWith("(1)")) {
				player.sendMessage("You have finished your potion.");
			}

			switch (id) {
			// Summoning potions
			case 12140:
				drinkSummoningPot(id, 12142, slot, true);
				break;

			case 12142:
				drinkSummoningPot(id, 12144, slot, true);
				break;

			case 12144:
				drinkSummoningPot(id, 12146, slot, true);
				break;

			case 12146:
				drinkSummoningPot(id, 229, slot, true);
				break;

			case 6685: // brews
				doTheBrew(id, 6687, slot);
				break;

			case 6687:
				doTheBrew(id, 6689, slot);
				break;

			case 6689:
				doTheBrew(id, 6691, slot);
				break;

			case 6691:
				doTheBrew(id, 229, slot);
				break;

			case 2436:
				drinkStatPotion(id, 145, slot, 0, true); // sup attack
				break;

			case 145:
				drinkStatPotion(id, 147, slot, 0, true);
				break;

			case 147:
				drinkStatPotion(id, 149, slot, 0, true);
				break;

			case 149:
				drinkStatPotion(id, 229, slot, 0, true);
				break;

			case 2440:
				drinkStatPotion(id, 157, slot, 2, true); // sup str
				break;

			case 157:
				drinkStatPotion(id, 159, slot, 2, true);
				break;

			case 159:
				drinkStatPotion(id, 161, slot, 2, true);
				break;

			case 161:
				drinkStatPotion(id, 229, slot, 2, true);
				break;

			case 2444:
				drinkStatPotion(id, 169, slot, 4, false); // range pot
				break;

			case 169:
				drinkStatPotion(id, 171, slot, 4, false);
				break;

			case 171:
				drinkStatPotion(id, 173, slot, 4, false);
				break;

			case 173:
				drinkStatPotion(id, 229, slot, 4, false);
				break;

			case 2432:
				drinkStatPotion(id, 133, slot, 1, false); // def pot
				break;

			case 133:
				drinkStatPotion(id, 135, slot, 1, false);
				break;

			case 135:
				drinkStatPotion(id, 137, slot, 1, false);
				break;

			case 137:
				drinkStatPotion(id, 229, slot, 1, false);
				break;

			case 113:
				drinkStatPotion(id, 115, slot, 2, false); // str pot
				break;

			case 115:
				drinkStatPotion(id, 117, slot, 2, false);
				break;

			case 117:
				drinkStatPotion(id, 119, slot, 2, false);
				break;

			case 119:
				drinkStatPotion(id, 229, slot, 2, false);
				break;

			case 2428:
				drinkStatPotion(id, 121, slot, 0, false); // attack pot
				break;

			case 121:
				drinkStatPotion(id, 123, slot, 0, false);
				break;

			case 123:
				drinkStatPotion(id, 125, slot, 0, false);
				break;

			case 125:
				drinkStatPotion(id, 229, slot, 0, false);
				break;

			case 2442:
				drinkStatPotion(id, 163, slot, 1, true); // super def pot
				break;

			case 163:
				drinkStatPotion(id, 165, slot, 1, true);
				break;

			case 165:
				drinkStatPotion(id, 167, slot, 1, true);
				break;

			case 167:
				drinkStatPotion(id, 229, slot, 1, true);
				break;

			case 3040: // magic pot
				drinkStatPotion(id, 3042, slot, 6, false);
				break;

			case 3042:
				drinkStatPotion(id, 3044, slot, 6, false);
				break;

			case 3044:
				drinkStatPotion(id, 3046, slot, 6, false);
				break;

			case 3046:
				drinkStatPotion(id, 229, slot, 6, false);
				break;

			case 3024:
				drinkPrayerPot(id, 3026, slot, true); // sup restore
				break;

			case 3026:
				drinkPrayerPot(id, 3028, slot, true);
				break;

			case 3028:
				drinkPrayerPot(id, 3030, slot, true);
				break;

			case 3030:
				drinkPrayerPot(id, 229, slot, true);
				break;

			case 10925:
				drinkPrayerPot(id, 10927, slot, true); // sanfew serums
				curePoison(300000);
				break;

			case 10927:
				drinkPrayerPot(id, 10929, slot, true);
				curePoison(300000);
				break;

			case 10929:
				drinkPrayerPot(id, 10931, slot, true);
				curePoison(300000);
				break;

			case 10931:
				drinkPrayerPot(id, 229, slot, true);
				curePoison(300000);
				break;

			case 2434:
				drinkPrayerPot(id, 139, slot, false); // pray pot
				break;

			case 139:
				drinkPrayerPot(id, 141, slot, false);
				break;

			case 141:
				drinkPrayerPot(id, 143, slot, false);
				break;

			case 143:
				drinkPrayerPot(id, 229, slot, false);
				break;

			case 3008:
				drinkEnergyPotion(id, 3010, slot, 30000, false);
				break;

			case 3010:
				drinkEnergyPotion(id, 3012, slot, 30000, false);
				break;

			case 3012:
				drinkEnergyPotion(id, 3014, slot, 30000, false);
				break;

			case 3014:
				drinkEnergyPotion(id, 229, slot, 30000, false);
				break;

			case 3016:
				drinkEnergyPotion(id, 3018, slot, 30000, true);
				break;

			case 3018:
				drinkEnergyPotion(id, 3020, slot, 30000, true);
				break;

			case 3020:
				drinkEnergyPotion(id, 3022, slot, 30000, true);
				break;

			case 3022:
				drinkEnergyPotion(id, 229, slot, 30000, true);
				break;

			case 2446:
				drinkAntiPoison(id, 175, slot, 30000); // anti poisons
				break;

			case 175:
				drinkAntiPoison(id, 177, slot, 30000);
				break;

			case 177:
				drinkAntiPoison(id, 179, slot, 30000);
				break;

			case 179:
				drinkAntiPoison(id, 229, slot, 30000);
				break;

			case 2448:
				drinkAntiPoison(id, 181, slot, 300000); // anti poisons
				break;

			case 181:
				drinkAntiPoison(id, 183, slot, 300000);
				break;

			case 183:
				drinkAntiPoison(id, 185, slot, 300000);
				break;

			case 2452:
				drainkAntiFire(id, 2454, slot);
				break;

			case 2454:
				drainkAntiFire(id, 2456, slot);
				break;

			case 2456:
				drainkAntiFire(id, 2458, slot);
				break;

			case 2458:
				drainkAntiFire(id, 229, slot);
				break;

			case 15312: // Extreme Strength
				drinkExtremePotion(id, 15313, slot, 2, false);
				break;

			case 15313: // Extreme Strength
				drinkExtremePotion(id, 15314, slot, 2, false);
				break;

			case 15314: // Extreme Strength
				drinkExtremePotion(id, 15315, slot, 2, false);
				break;

			case 15315: // Extreme Strength
				drinkExtremePotion(id, 229, slot, 2, false);
				break;

			case 15308: // Extreme Attack
				drinkExtremePotion(id, 15309, slot, 0, false);
				break;

			case 15309: // Extreme Attack
				drinkExtremePotion(id, 15310, slot, 0, false);
				break;

			case 15310: // Extreme Attack
				drinkExtremePotion(id, 15311, slot, 0, false);
				break;

			case 15311: // Extreme Attack
				drinkExtremePotion(id, 229, slot, 0, false);
				break;

			case 15316: // Extreme Defense
				drinkExtremePotion(id, 15317, slot, 1, false);
				break;

			case 15317: // Extreme Defense
				drinkExtremePotion(id, 15318, slot, 1, false);
				break;

			case 15318: // Extreme Defense
				drinkExtremePotion(id, 15319, slot, 1, false);
				break;

			case 15319: // Extreme Defense
				drinkExtremePotion(id, 229, slot, 1, false);
				break;

			case 15324: // Extreme Ranging
				drinkExtremePotion(id, 15325, slot, 4, false);
				break;

			case 15325: // Extreme Ranging
				drinkExtremePotion(id, 15326, slot, 4, false);
				break;

			case 15326: // Extreme Ranging
				drinkExtremePotion(id, 15327, slot, 4, false);
				break;

			case 15327: // Extreme Ranging
				drinkExtremePotion(id, 229, slot, 4, false);
				break;

			case 15320: // Extreme Magic
				drinkExtremePotion(id, 15321, slot, 6, false);
				break;

			case 15321: // Extreme Magic
				drinkExtremePotion(id, 15322, slot, 6, false);
				break;

			case 15322: // Extreme Magic
				drinkExtremePotion(id, 15323, slot, 6, false);
				break;

			case 15323: // Extreme Magic
				drinkExtremePotion(id, 229, slot, 6, false);
				break;

			case 15328: // Super Prayer
				drinkExtremePrayer(id, 15329, slot, true);
				break;

			case 15329: // Super Prayer
				drinkExtremePrayer(id, 15330, slot, true);
				break;

			case 15330: // Super Prayer
				drinkExtremePrayer(id, 15331, slot, true);
				break;

			case 15331: // Super Prayer
				drinkExtremePrayer(id, 229, slot, true);
				break;

			case 15300: // Recover Special
				recoverSpecial(id, 15301, slot);
				break;

			case 15301: // Recover Special
				recoverSpecial(id, 15302, slot);
				break;

			case 15302: // Recover Special
				recoverSpecial(id, 15303, slot);
				break;

			case 15303: // Recover Special
				recoverSpecial(id, 229, slot);
				break;

			case 21630:
				prayerRenewal(id, 21632, slot);
				break;
			case 21632:
				prayerRenewal(id, 21634, slot);
				break;
			case 21634:
				prayerRenewal(id, 21636, slot);
				break;
			case 21636:
				prayerRenewal(id, 229, slot);
				break;
			case 15332:
				overload(id, 15333, slot);
				break;

			case 15333:
				overload(id, 15334, slot);
				break;

			case 15334:
				overload(id, 15335, slot);
				break;

			case 15335:
				overload(id, 229, slot);
				break;

			case 1917:
			case 1909:
				handleBeer(id, 1919, slot);
				break;
			case 1989:
				handleBeer(id, 1935, slot);
				break;
			case 7919:
				handleBeer(id, 7921, slot);
				break;

			case 2015:
			case 2017:
			case 2019:
			case 2021:
				handleVodka(id, -1, slot);
				break;

			case 8940:
			case 8941:
				handleRum(id, -1, slot);
				break;
				
			case 2450:	//zamy brew by hontiris1
				doTheBrewzam(id, 189, slot);
				break;
				case 189:
				doTheBrewzam(id, 191, slot);
				break;
				case 191:
				doTheBrewzam(id, 193, slot);
				break;
				case 193:
				doTheBrewzam(id, 229, slot);
				break;

			}
		}
	}
	
	public void doTheBrewzam(int itemId, int replaceItem, int slot) { //by hontiris1
		if (player.getDuel().isRuleActive(DuelingRules.NO_POTIONS)) {
			player.sendMessage("You may not eat in this duel.");
			return;
		}
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		
		int[] toDecrease = {1,3};

		for (int tD : toDecrease) {
			player.getSkill().decrementLevel(tD, getBrewStat(tD, 0.10) + 2);
		}
		
		player.getSkill().incrementLevel(0, getBrewStat(0, 0.20) + 2);

		if (player.getSkill().getLevel(Skill.ATTACK) > player.getSkill().getLevelForExperience(Skill.ATTACK) * 1.2) {
			player.getSkill().setLevel(Skill.ATTACK, (int) (player.getSkill().getLevelForExperience(Skill.ATTACK) * 1.2));
		}

		player.getSkill().incrementLevel(2, getBrewStat(2, 0.12) + 2);

		if (player.getSkill().getLevel(Skill.STRENGTH) > player.getSkill().getLevelForExperience(Skill.STRENGTH) * 1.12) {
			player.getSkill().setLevel(Skill.STRENGTH, (int) (player.getSkill().getLevelForExperience(Skill.STRENGTH) * 1.12));
		}
		
		player.getSkill().incrementLevel(5, getBrewStat(5, 0.10));

		if (player.getSkill().getLevel(Skill.PRAYER) > player.getSkill().getLevelForExperience(Skill.PRAYER) * 1.12) {
			player.getSkill().setLevel(Skill.PRAYER, (int) (player.getSkill().getLevelForExperience(Skill.PRAYER) * 1.12));
		}
	}

	public boolean isPotion(int itemId) {
		String name = ItemDefinition.forId(itemId).getName();
		return name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)") || itemId >= 2015 && itemId <= 2021 || itemId == 1909 || itemId == 1917
				|| itemId == 1989 || itemId == 7919 || itemId == 8940 || itemId == 8941;
	}

	/**
	 * Check used for potion emptying
	 */
	public boolean isPotionCheck(int itemId) {
		String name = ItemDefinition.forId(itemId).getName();
		return !name.contains("Amulet") && !name.contains("amulet") && !name.contains("necklace") && !name.contains("Necklace")
				&& (name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)") || name.contains("(unf)") || name.contains("vial"));
	}

	public void prayerRenewal(int itemId, int replaceItem, int slot) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		player.getEF().queueEffect(new PrayerRenewal(player));
	}

	public void overload(int itemId, int replaceItem, int slot) {

		if (player.getHitpoints() < 60) {
			player.sendMessage("You don't have enough hitpoints to use this.");
			return;
		}
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		player.getEF().queueEffect(new Overload(player));

	}

	public void handleBeer(int itemId, int replaceItem, int slot) {

		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		player.getEF().queueEffect(new DrunkEffect(player, 1));
	}

	public void handleVodka(int itemId, int replaceItem, int slot) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		player.getEF().queueEffect(new DrunkEffect(player, 3));
	}

	public void handleRum(int itemId, int replaceItem, int slot) {
		player.playAnimation(new Animation(829));
		player.getInventory().replace(slot, replaceItem, replaceItem);
		player.getEF().queueEffect(new DrunkEffect(player, 2));
	}

	private void recoverSpecial(int itemId, int replaceItem, int slot) {
		if (player.inWild()) {
			player.sendMessage("You are unable to restore special in the wilderness.");
			return;
		} else if (player.specialEnergy >= 75) {
			player.sendMessage("You are unable to drink the potion as your special is above 75%.");
		} else if (System.currentTimeMillis() - player.restoreDelay >= 30000) {
			player.specialEnergy += 25;
			player.getItems().addSpecialBar(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
			player.playAnimation(new Animation(829));
			player.sendMessage("As you drink the potion you feel your special attack slightly regenerate.");
			player.getInventory().replace(slot, replaceItem, replaceItem);
			player.getItems().updateSpecialBar();
			player.restoreDelay = System.currentTimeMillis();
		} else {
			player.sendMessage("You can only restore your special once every 30 seconds.");
		}
	}

	private void restoreStats() {
		for (int j = 0; j <= 6; j++) {
			if (j == 5 || j == 3) {
				continue;
			}

			if (player.getSkill().getLevel(j) < player.getSkill().getLevelForExperience(j)) {

				player.getSkill().incrementLevel(j, (int) (player.getSkill().getLevelForExperience(j) * 0.33));

				if (player.getSkill().getLevel(j) > player.getSkill().getLevelForExperience(j)) {
					player.getSkill().restoreLevel(j);
				}
			}
		}
	}

}
