package com.koios.game.content.combat.util;

import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.combat.ranged.RangedData;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Max hit calculator
 *
 * @author Gabriel | Wolfs Darker
 */
public class CalculateMaxHit {

	/**
	 * Gets the entity's max hit by the attack type ( Magic, Melee or Range )
	 *
	 * @param attacker
	 * @param type
	 * @return
	 */
	public static int execute(EntityCharacter attacker, AttackType type) {

		if (attacker.isMob()) {

			return attacker.toMob().getMaxHit() <= 0 ? 2 : attacker.toMob().getMaxHit();
		}

		Player p = attacker.toPlayer();

		switch (type) {
		case MELEE:
            if(com.koios.game.arpg.ArpgMelee.enabled(p))return (int)Math.min(Integer.MAX_VALUE,p.getArpgStats().get(com.koios.game.arpg.Stat.PHYSICAL_MAX));

			int curr_strength = p.getSkill().getLevel(Skill.STRENGTH);
			double prayer_bonus = 1.0;
			int style_bonus = 0;

			if (p.getPrayers().isActive(NormalPrayer.BURST_OF_STRENGTH)) {
				prayer_bonus *= 1.05;
			}

			if (p.getPrayers().isActive(NormalPrayer.SUPERHUMAN_STRENGTH)) {
				prayer_bonus *= 1.10;
			}

			if (p.getPrayers().isActive(NormalPrayer.ULTIMATE_STRENGTH)) {
				prayer_bonus *= 1.15;
			}

			if (p.getPrayers().isActive(NormalPrayer.CHIVALRY)) {
				prayer_bonus *= 1.18;
			}

			if (p.getPrayers().isActive(NormalPrayer.PIETY)) {
				prayer_bonus *= 1.23;
			}

			if (p.getPrayers().isActive(CursedPrayer.LEECH_STRENGTH)) {
				prayer_bonus *= 1.10;
			}

			if (p.getPrayers().isActive(CursedPrayer.TURMOIL)) {
				prayer_bonus *= 1.23;
			}

			style_bonus = attacker.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE ? 3 : attacker.getCombat().getAttackStyle() == AttackStyle.CONTROLLED ? 1 : 0;

			double effective_strength = curr_strength * prayer_bonus + style_bonus;
			double special_bonus = 1;
			double strengthBonus = p.playerBonus[14];

			if (p.usingSpecial && (p.getCombat().getLastWeapon() > 0 || p.getEquipment().get(Equipment.SLOT_WEAPON) != null)) {
				int weap = p.getCombat().getLastWeapon() > 0 ? p.getCombat().getLastWeapon() : p.getEquipment().get(Equipment.SLOT_WEAPON).getId();
				switch (weap) {
				case 5680:
				case 1231:
				case 1215:
				case 5698:
				case 3204:
				case 11698:
				case 11700:
					special_bonus = 1.1;
					break;
				case 11696:
					special_bonus = 1.2;
					break;
				case 13899:
				case 13901:
					special_bonus = 1.28;
					break;
				case 1305:
					special_bonus = 1.241;
					break;
				case 11694:
					special_bonus = 1.475;
					break;
				case 1434:
					special_bonus = 1.45;
					break;
				case 13902:
				case 13904:
					special_bonus = 1.50;
					break;
				}

			}

			double base = (13 + effective_strength + strengthBonus / 8 + effective_strength * strengthBonus / 64) / 10;

			base *= special_bonus;

			/**
			 * Obby weapons + berserker necklace (obsidian)
			 */
			if (CombatData.hasObsidianEffect(p)) {
				base *= 1.2;
			}

			/**
			 * Void bonus
			 */
			if (CombatData.wearingFullVoid(p, AttackType.MELEE)) {
				base *= 1.1;
			}

			/**
			 * Dharok's bonus
			 */
			if (CombatData.wearingFullDharoks(p)) {
				base *= CombatData.getDharoksMultiplier(p);
			}

			if (p.usingSpecial && p.getEquipment().get(3).getId() == 19780) {
				base = NumberUtil.random((int) (base * 0.50), (int) (base * 1.50));
			}

			return (int) Math.floor(base);

		case MAGIC:
			double damage = MagicData.MAGIC_SPELLS[p.oldSpellId][6];
			double damageMultiplier = 1;

			if (p.getSkill().getLevel(Skill.MAGIC) > p.getSkill().getLevelForExperience(Skill.MAGIC)) {
				damageMultiplier += .03 * (p.getSkill().getLevel(Skill.MAGIC) - p.getSkill().getLevelForExperience(Skill.MAGIC));
			}

			int weap = p.getCombat().getLastWeapon() > 0 ? p.getCombat().getLastWeapon()
					: p.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? p.getEquipment().get(Equipment.SLOT_WEAPON).getId() : 0;

			switch (weap) {
			case 18371: // Gravite Staff
				damageMultiplier += 0.05;
				break;
			case 4675: // Ancient Staff
			case 4710: // Ahrim's Staff
			case 4862: // Ahrim's Staff
			case 4864: // Ahrim's Staff
			case 4865: // Ahrim's Staff
			case 6914: // Master Wand
			case 8841: // Void Knight Mace
			case 13867: // Zuriel's Staff
			case 13869: // Zuriel's Staff (Deg)
				damageMultiplier += 0.10;
				break;
			case 15486: // Staff of Light
				damageMultiplier += 0.15;
				break;
			case 18355: // Chaotic Staff
				damageMultiplier += 0.20;
				break;
			}
			if (p.getEquipment().isSlotUsed(Equipment.SLOT_AMULET)) {
				switch (p.getEquipment().get(Equipment.SLOT_AMULET).getId()) {
				case 18333: // Arcane Pulse
					damageMultiplier += 0.05;
					break;
				case 18334:// Arcane Blast
					damageMultiplier += 0.10;
					break;
				case 18335:// Arcane Stream
					damageMultiplier += 0.15;
					break;
				}
			}
			damage *= damageMultiplier;
			return (int) damage;
		case RANGE:
			int a = attacker.getRanged();

			double d = RangedData.getRangedStrength(p);

			double b = 1.0;

			if (p.getPrayers().isActive(NormalPrayer.SHARP_EYE)) {
				b += 0.05;
			}
			if (p.getPrayers().isActive(NormalPrayer.EAGLE_EYE)) {
				b += 0.10;
			}
			if (p.getPrayers().isActive(NormalPrayer.HAWK_EYE)) {
				b += 0.12;
			}
			if (p.getPrayers().isActive(CursedPrayer.LEECH_RANGED)) {
				b += 0.10;
			}

			if (CombatData.wearingFullVoid(p, AttackType.RANGE)) {
				b *= 1.20;
			}

			double e = Math.floor(a * b);

			if (attacker.getCombat().getAttackStyle() == AttackStyle.ACCURATE) {
				e += 3;
			}

			double c = 1.0;

			if (p.getEquipment().get(Equipment.SLOT_WEAPON) != null && p.usingSpecial) {
				switch (p.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 11235:
				case 15701:
				case 15702:
				case 15703:
				case 15704:
					if (p.lastArrowUsed == 11212) {
						c *= 1.5;
					} else {
						c *= 1.3;
					}
					break;
				}
			}

			double max = (1.3 + e / 10 + d / 80 + e * d / 640) * c;
			return (int) Math.floor(max);
		default:
			return 0;
		}

	}
}
