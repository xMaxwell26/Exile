package com.koios.game.content.combat.util;

import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.LevelRequirement;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Combat accuracy calculator
 *
 * @author Gabriel | Wolfs Darker
 */
public class CalculateAccuracy {

	/**
	 * Calculates the accuracy between the attacker and the target.
	 *
	 * @param attacker
	 * @param target
	 * @param att_type
	 * @return
	 */

	public static boolean execute(EntityCharacter attacker, EntityCharacter target, AttackType att_type) {
        if(attacker.isPlayer() && target.isMob())return java.util.concurrent.ThreadLocalRandom.current().nextDouble()<com.koios.game.arpg.Defences.hitChance(attacker.toPlayer().getArpgStats().get(com.koios.game.arpg.Stat.ACCURACY),Math.max(0,target.getDefence())*2);
        if(attacker.isMob() && target.isPlayer())return true;

		int att_curr_attack = attacker.getAttack();
		int att_curr_range = attacker.getRanged();
		int att_curr_magic = attacker.getMagicLevel();

		int tar_curr_magic = target.isMob() ? 1 : target.getMagicLevel();
		int tar_curr_defence = target.getDefence();

		int att_base_attack = attacker.isMob() ? attacker.getAttack() : attacker.getSkill().getLevelForExperience(Skill.ATTACK);
		int att_base_range = attacker.isMob() ? attacker.getRanged() : attacker.getSkill().getLevelForExperience(Skill.RANGED);
		int att_base_magic = attacker.isMob() ? attacker.getMagicLevel() : attacker.getSkill().getLevelForExperience(Skill.MAGIC);

		double att_prayer_bonus = 1.0;
		double att_style_bonus = 0;
		double att_spec_bonus = 1;

		double att_spell_bonus = 0;
		double att_weapon_bonus = 0;
		double att_additional_bonus = 1;
		double att_effective_attack = 0;
		double att_equipment_bonus = 1;
		double augmented_attack = 0;
		double att_hit_chance = 0;
		double att_void_bonus = 1;

		double tar_prayer_bonus = 1.0;
		double tar_style_bonus = 0;

		double tar_equipment_bonus = 1;
		double augmented_defence = 0;
		double tar_block_chance = 0;

		double tar_effective_defence = 0;

		if (attacker.isPlayer()) {
			Player p = attacker.toPlayer();

			switch (att_type) {
			case MELEE:
				if (p.getPrayers().isActive(NormalPrayer.CLARITY_OF_THOUGHT)) {
					att_prayer_bonus += 0.05;
				}

				if (p.getPrayers().isActive(NormalPrayer.IMPROVED_REFLEXES)) {
					att_prayer_bonus += 0.1;
				}

				if (p.getPrayers().isActive(NormalPrayer.INCREDIBLE_REFLEXES)) {
					att_prayer_bonus += 0.15;
				}

				if (p.getPrayers().isActive(NormalPrayer.CHIVALRY)) {
					att_prayer_bonus += 0.15;
				}

				if (p.getPrayers().isActive(NormalPrayer.PIETY)) {
					att_prayer_bonus += 0.2;
				}

				if (p.getPrayers().isActive(CursedPrayer.LEECH_ATTACK)) {
					att_prayer_bonus += 0.1;
				}

				if (p.getPrayers().isActive(CursedPrayer.TURMOIL)) {
					att_prayer_bonus += 0.15;
				}
				att_equipment_bonus = p.playerBonus[p.getCombat().getAttackStyle().ordinal() <= 1 ? p.getCombat().getAttackStyle().ordinal() : 1];
				att_void_bonus = CombatData.wearingFullVoid(p, att_type) ? 1.1 : 1;
				break;
			case RANGE:
				if (p.getPrayers().isActive(NormalPrayer.SHARP_EYE)) {
					att_prayer_bonus += 0.05;
				}

				else if (p.getPrayers().isActive(NormalPrayer.HAWK_EYE)) {
					att_prayer_bonus += 0.1;
				}

				else if (p.getPrayers().isActive(NormalPrayer.EAGLE_EYE)) {
					att_prayer_bonus += 0.15;
				}

				if (p.getPrayers().isActive(CursedPrayer.LEECH_RANGED)) {
					att_prayer_bonus += 0.1;
				}
				att_equipment_bonus = p.playerBonus[4];
				att_void_bonus = CombatData.wearingFullVoid(p, att_type) ? 1.1 : 1;
				break;
			case MAGIC:
				if (p.getPrayers().isActive(NormalPrayer.MYSTIC_WILL)) {
					att_prayer_bonus += 0.05;
				}

				else if (p.getPrayers().isActive(NormalPrayer.MYSTIC_LORE)) {
					att_prayer_bonus += 0.1;
				}

				else if (p.getPrayers().isActive(NormalPrayer.MYSTIC_MIGHT)) {
					att_prayer_bonus += 0.15;
				}
				if (p.getPrayers().isActive(CursedPrayer.LEECH_MAGIC)) {
					att_prayer_bonus += 0.1;
				}
				att_spell_bonus += (att_base_magic - MagicData.MAGIC_SPELLS[p.oldSpellId][1]) * 0.3;

				att_equipment_bonus = p.playerBonus[3];
				att_void_bonus = CombatData.wearingFullVoid(p, att_type) ? 1.3 : 1;
				break;
			default:
				break;
			}

			if ((p.getCombat().getLastWeapon() > 0 || p.getEquipment().get(Equipment.SLOT_WEAPON) != null) && att_type != AttackType.MAGIC) {
				int weap = p.getCombat().getLastWeapon() > 0 ? p.getCombat().getLastWeapon() : p.getEquipment().get(Equipment.SLOT_WEAPON).getId();
				LevelRequirement l = LevelRequirement.forId(weap);
				int skill = att_type == AttackType.MELEE ? 0 : 4;
				int req = 0;
				if (l != null) {

					for (int i = 0; i < l.getLevelId().length; i++) {
						if (skill == l.getLevelId()[i]) {
							req = l.getRequiredLevel()[i];
						}
					}
					att_weapon_bonus += ((att_type == AttackType.MELEE ? att_base_attack : att_base_range) - req) * 0.3;
				}
			}

			if (att_type != AttackType.MAGIC) {
				att_style_bonus = p.getCombat().getAttackStyle() == AttackStyle.CONTROLLED ? 1 : p.getCombat().getAttackStyle() == AttackStyle.ACCURATE ? 3 : 0;

				if (p.usingSpecial && p.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					switch (p.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
					case 1215:
					case 4587:
					case 3101:
					case 859:
						att_spec_bonus += 0.15;
						break;
					case 19780:
						att_spec_bonus += 0.85;
						break;
					case 11694:
					case 11696:
					case 11698:
					case 11700:
						att_spec_bonus += 2.60;
						break;
					case 13899:
						att_spec_bonus += 0.2;
						break;
					case 15241:
						att_spec_bonus += 0.75;
						break;
					case 20671:
						att_spec_bonus += 1;
						break;
					case 1434:
					case 861:
						att_spec_bonus -= 0.15;
						break;
					case 1377:
						att_spec_bonus -= 0.1;
						break;
					}
				}
			}

		}

		switch (att_type) {
		case MELEE:
			att_effective_attack = Math.floor(att_curr_attack * att_prayer_bonus * att_additional_bonus + att_style_bonus + att_weapon_bonus);
			break;
		case RANGE:
			att_effective_attack = Math.floor(att_curr_range * att_prayer_bonus * att_additional_bonus + att_style_bonus + att_weapon_bonus);
			break;
		case MAGIC:
			att_effective_attack = Math.floor(att_curr_magic * att_prayer_bonus * att_additional_bonus + att_spell_bonus);
			break;
		default:
			break;
		}

		if (target.isPlayer()) {

			Player t = target.toPlayer();

			if (t.getPrayers().isActive(NormalPrayer.THICK_SKIN)) {
				tar_prayer_bonus += 0.05;
			}

			if (t.getPrayers().isActive(NormalPrayer.ROCK_SKIN)) {
				tar_prayer_bonus += 0.1;
			}

			if (t.getPrayers().isActive(NormalPrayer.STEEL_SKIN)) {
				tar_prayer_bonus += 0.15;
			}

			if (t.getPrayers().isActive(NormalPrayer.CHIVALRY)) {
				tar_prayer_bonus += 0.2;
			}

			if (t.getPrayers().isActive(NormalPrayer.PIETY)) {
				tar_prayer_bonus += 0.25;
			}

			if (t.getPrayers().isActive(CursedPrayer.LEECH_DEFENCE)) {
				tar_prayer_bonus += 0.1;
			}

			if (t.getPrayers().isActive(CursedPrayer.TURMOIL)) {
				tar_prayer_bonus += 0.15;
			}

			tar_style_bonus = t.getCombat().getAttackStyle() == AttackStyle.CONTROLLED ? 1 : t.getCombat().getAttackStyle() == AttackStyle.DEFENSIVE ? 3 : 0;

			switch (att_type) {
			case MELEE:
				tar_equipment_bonus = t.playerBonus[attacker.getCombat().getAttackStyle().ordinal() <= 1 ? attacker.getCombat().getAttackStyle().ordinal() + 5 : 6];
				break;
			case RANGE:
				tar_equipment_bonus = t.playerBonus[9];
				break;
			case MAGIC:
				tar_equipment_bonus = t.playerBonus[8];
				break;
			default:
				break;
			}
		}

		switch (att_type) {
		case MELEE:
			tar_effective_defence = Math.floor(tar_curr_defence * tar_prayer_bonus + tar_style_bonus);
			break;
		case RANGE:
			tar_effective_defence = Math.floor(tar_curr_defence * tar_prayer_bonus + tar_style_bonus);
			break;
		case MAGIC:
			tar_effective_defence = Math.floor(tar_curr_defence * tar_prayer_bonus * 0.3) + Math.floor(tar_curr_magic * 0.7);
			break;
		default:
			break;
		}

		augmented_attack = Math.floor((att_effective_attack + 8) * (att_equipment_bonus + 64) / 10);

		augmented_defence = Math.floor((tar_effective_defence + 8) * (tar_equipment_bonus + 64) / 10);

		double hit_chance = 0;

		if (augmented_attack < augmented_defence) {
			hit_chance = (augmented_attack - 1) / (augmented_defence * 2);
		} else {
			hit_chance = 1 - (augmented_defence + 1) / (augmented_attack * 2);
		}

		switch (att_type) {
		case MELEE:
			if (target.isPlayer() && target.toPlayer().getPrayers().isActive(NormalPrayer.PROTECT_FROM_MELEE)) {
				att_hit_chance = Math.floor(hit_chance * att_spec_bonus * att_void_bonus * 0.6 * 100);
				tar_block_chance = Math.floor(101 - hit_chance * att_spec_bonus * att_void_bonus * 0.6 * 100);
			} else {
				att_hit_chance = Math.floor(hit_chance * att_spec_bonus * att_void_bonus * 100);
				tar_block_chance = Math.floor(101 - hit_chance * att_spec_bonus * att_void_bonus * 100);
			}
			break;
		case RANGE:
			if (target.isPlayer() && target.toPlayer().getPrayers().isActive(NormalPrayer.PROTECT_FROM_MISSILES)) {
				att_hit_chance = Math.floor(hit_chance * att_spec_bonus * att_void_bonus * 0.6 * 100);
				tar_block_chance = Math.floor(101 - hit_chance * att_spec_bonus * att_void_bonus * 0.6 * 100);
			} else {
				att_hit_chance = Math.floor(hit_chance * att_spec_bonus * att_void_bonus * 100);
				tar_block_chance = Math.floor(101 - hit_chance * att_spec_bonus * att_void_bonus * 100);
			}
			break;
		case MAGIC:
			if (target.isPlayer() && target.toPlayer().getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC)) {
				att_hit_chance = Math.floor(hit_chance * att_void_bonus * 0.6 * 100);
				tar_block_chance = Math.floor(101 - hit_chance * att_spec_bonus * att_void_bonus * 0.6 * 100);
			} else {
				att_hit_chance = Math.floor(hit_chance * att_void_bonus * 100);
				tar_block_chance = Math.floor(101 - hit_chance * att_spec_bonus * att_void_bonus * 100);
			}
			break;
		default:
			break;
		}

		att_hit_chance = NumberUtil.inclusiveRandom((int) att_hit_chance + 20 + (attacker.isPlayer() ? attacker.toPlayer().usingSpecial ? 20 : 0 : 0));
		tar_block_chance = NumberUtil.inclusiveRandom((int) tar_block_chance);

		return (int) att_hit_chance > (int) tar_block_chance;
	}
}
