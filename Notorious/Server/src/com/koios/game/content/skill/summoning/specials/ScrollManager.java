package com.koios.game.content.skill.summoning.specials;

import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.melee.MeleeRequirements;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.impl.ArcticBlastScroll;
import com.koios.game.content.skill.summoning.specials.impl.BoilScroll;
import com.koios.game.content.skill.summoning.specials.impl.BullRushScroll;
import com.koios.game.content.skill.summoning.specials.impl.DreadfowlStrikeScroll;
import com.koios.game.content.skill.summoning.specials.impl.EggSpawnScroll;
import com.koios.game.content.skill.summoning.specials.impl.ElectricLashScroll;
import com.koios.game.content.skill.summoning.specials.impl.EssenceShipmentScroll;
import com.koios.game.content.skill.summoning.specials.impl.FruitfallScroll;
import com.koios.game.content.skill.summoning.specials.impl.HealingAuraScroll;
import com.koios.game.content.skill.summoning.specials.impl.HowlScroll;
import com.koios.game.content.skill.summoning.specials.impl.InfernoScroll;
import com.koios.game.content.skill.summoning.specials.impl.IronWithinScroll;
import com.koios.game.content.skill.summoning.specials.impl.MagicFocusScroll;
import com.koios.game.content.skill.summoning.specials.impl.PesterScroll;
import com.koios.game.content.skill.summoning.specials.impl.SlimeSprayScroll;
import com.koios.game.content.skill.summoning.specials.impl.SteelOfLegendsScroll;
import com.koios.game.content.skill.summoning.specials.impl.StonyShellScroll;
import com.koios.game.content.skill.summoning.specials.impl.SwallowWholeScroll;
import com.koios.game.content.skill.summoning.specials.impl.SwampPlagueScroll;
import com.koios.game.content.skill.summoning.specials.impl.TirelessRunScroll;
import com.koios.game.content.skill.summoning.specials.impl.TitansConstitutionScroll;
import com.koios.game.content.skill.summoning.specials.impl.WinterStorageScroll;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;

/**
 * Manage the casting of familiar's scrolls.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ScrollManager {

	/**
	 * Handles the scroll list.
	 */
	private static Map<Integer, FamiliarScroll> scroll_list = new HashMap<Integer, FamiliarScroll>();

	static {
		scroll_list.put(ScrollsData.HOWL.getScrollId(), new HowlScroll());
		scroll_list.put(ScrollsData.DREADFOWL_STRIKE.getScrollId(), new DreadfowlStrikeScroll());
		scroll_list.put(ScrollsData.EGG_SPAWN.getScrollId(), new EggSpawnScroll());
		scroll_list.put(ScrollsData.SLIME_SPRAY.getScrollId(), new SlimeSprayScroll());
		scroll_list.put(ScrollsData.STONY_SHELL.getScrollId(), new StonyShellScroll());
		scroll_list.put(ScrollsData.PESTER.getScrollId(), new PesterScroll());
		scroll_list.put(ScrollsData.ELECTRIC_LASH.getScrollId(), new ElectricLashScroll());
		scroll_list.put(ScrollsData.BRONZE_BULL_RUSH.getScrollId(), new BullRushScroll());
		scroll_list.put(ScrollsData.IRON_BULL_RUSH.getScrollId(), new BullRushScroll());
		scroll_list.put(ScrollsData.TIRELESS_RUN.getScrollId(), new TirelessRunScroll());
		scroll_list.put(ScrollsData.STEEL_BULL_RUSH.getScrollId(), new BullRushScroll());
		scroll_list.put(ScrollsData.MITHRIL_BULL_RUSH.getScrollId(), new BullRushScroll());
		scroll_list.put(ScrollsData.SWALLOW_WHOLE.getScrollId(), new SwallowWholeScroll());
		scroll_list.put(ScrollsData.FRUITFALL.getScrollId(), new FruitfallScroll());
		scroll_list.put(ScrollsData.ARCTIC_BLAST.getScrollId(), new ArcticBlastScroll());
		scroll_list.put(ScrollsData.INFERNO.getScrollId(), new InfernoScroll());
		scroll_list.put(ScrollsData.ADAMANT_BULL_RUSH.getScrollId(), new BullRushScroll());
		scroll_list.put(ScrollsData.TITANS_CONSTITUTION.getScrollId(), new TitansConstitutionScroll());
		scroll_list.put(ScrollsData.TITANS_CONSTITUTION_2.getScrollId(), new TitansConstitutionScroll());
		scroll_list.put(ScrollsData.TITANS_CONSTITUTION_3.getScrollId(), new TitansConstitutionScroll());
		scroll_list.put(ScrollsData.SWAMP_PLAGUE.getScrollId(), new SwampPlagueScroll());
		scroll_list.put(ScrollsData.RUNE_BULL_RUSH.getScrollId(), new BullRushScroll());
		scroll_list.put(ScrollsData.HEALING_AURA.getScrollId(), new HealingAuraScroll());
		scroll_list.put(ScrollsData.BOIL.getScrollId(), new BoilScroll());
		scroll_list.put(ScrollsData.MAGIC_FOCUS.getScrollId(), new MagicFocusScroll());
		scroll_list.put(ScrollsData.ESSENCE_SHIPMENT.getScrollId(), new EssenceShipmentScroll());
		scroll_list.put(ScrollsData.IRON_WITHIN.getScrollId(), new IronWithinScroll());
		scroll_list.put(ScrollsData.WINTER_STORAGE.getScrollId(), new WinterStorageScroll());
		scroll_list.put(ScrollsData.STEEL_OF_LEGENDS.getScrollId(), new SteelOfLegendsScroll());
	}

	/**
	 * Cast the familiar's scroll, if avalaible in the list.
	 *
	 * @param player
	 * @param scroll
	 */
	public static void castScroll(Player player, ScrollsData scroll, Object[] extra_data) {

		MobFamiliar familiar = player.getSummoning().getFamiliar();

		if (familiar == null || familiar.isDead() || scroll == null) {
			return;
		}

		FamiliarScroll scroll_ability = scroll_list.get(scroll.getScrollId());

		if (scroll_ability == null) {
			player.getSummoning().setSpecialDelay(0);
			player.sendMessage("This familiar's special move is currently unavaliable.");
			return;
		}

		final EntityCharacter target = familiar.getCombat().getTarget() != null ? familiar.getCombat().getTarget() : player.getCombat().getTarget();

		if (scroll_ability.isTargetRequired() && target == null) {
			player.getSummoning().setSpecialDelay(0);
			player.sendMessage("You need to be in combat to cast this scroll.");
			return;
		}

		if (scroll_ability.isTargetRequired() && !player.inMulti()) {
			player.getSummoning().setSpecialDelay(0);
			player.sendMessage("You must be in multi combat areas to cast this.");
			return;
		}

		if (scroll_ability.isTargetRequired() && target.isPlayer()) {
			Player t = (Player) target;
			if (!t.inWild()) {
				player.getSummoning().setSpecialDelay(0);
				player.sendMessage("The player is not in the wilderness.");
				return;
			} else if (t.inWild()) {
				int combatDif1 = MeleeRequirements.getCombatDifference(t.getSkill().getCombatLevel(false), player.getSkill().getCombatLevel(false));

				if (combatDif1 > t.wildLevel || combatDif1 > player.wildLevel) {
					player.getSummoning().setSpecialDelay(0);
					return;
				}
			}
		}

		if (Location.getManhattanDistance(familiar.getLocation(), player.getLocation()) > 6) {
			player.getSummoning().setSpecialDelay(0);
			player.sendMessage("Your familiar is too far for you to cast this.");
			return;
		}

		if (!scroll_ability.canCastScroll(player, scroll, target, extra_data)) {
			return;
		}

		if (scroll_ability.castScroll(player, scroll, target, extra_data)) {

			player.getSkill().addExperience(Skill.SUMMONING, scroll.getCastExperience() * Constants.SUMMONING_EXPERIENCE);
			player.getInventory().remove(new Item(scroll.getScrollId(), 1));
			player.getSummoning().setSpecialPoints(player.getSummoning().getSpecialPoints() - scroll.getSpecRequired());
			player.getSummoning().sendTab(false);
			player.getSummoning().setSpecialDelay(System.currentTimeMillis());

			if (player.getEquipment().get(Equipment.SLOT_CAPE) != null && player.getEquipment().get(Equipment.SLOT_CAPE).getId() == 19893) {
				player.getSummoning().setSpecialPoints(player.getSummoning().getSpecialPoints() + 2);
			}
		} else {
			player.getSummoning().setSpecialDelay(0);
		}
	}

	/**
	 * Sends the projectile from the familiar to the target.
	 *
	 * @param player
	 * @param target
	 * @param id
	 * @param time
	 * @param speed
	 * @param startHeigth
	 * @param endHeigth
	 * @param curve
	 */
	public static void sendSummoningProjectile(MobFamiliar familiar, EntityCharacter target, int id, int time, int speed, int startHeigth, int endHeigth, int curve) {
		if (target == null) {
			return;
		}
		int pX = familiar.getX();
		int pY = familiar.getY();
		int nX = target.getX();
		int nY = target.getY();
		int offX = (pY - nY) * -1;
		int offY = (pX - nX) * -1;

		CombatExtras.createPlayersProjectile(pX, pY, familiar.getZ(), offX, offY, 50, speed, id, startHeigth, endHeigth, target.getProjetileIndex(), curve, 16);
	}

	/**
	 * Boost's the summoner skill level.
	 *
	 * @param player
	 * @param skill
	 * @param amount
	 */
	public static void boostSkill(Player player, int skill, int amount) {

		int boost = amount + player.getSkill().getLevel(skill);
		int max = skill == 3 ? player.getMaxHitpoints() : player.getSkill().getLevelForExperience(skill);
		if (boost > max + amount) {
			boost = max + amount;
		}
		player.getSkill().setLevel(skill, boost);
	}
}
