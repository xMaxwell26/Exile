package com.koios.game.content.combat.mob;

import com.koios.game.content.combat.prayer.CombatCurse;
import com.koios.game.content.combat.prayer.CursedPrayer;
import com.koios.game.content.combat.prayer.NormalPrayer;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handles the dragon fire damage.
 *
 * @author Gabriel | Wolfs Darker
 */
public class ApplyDragonFireDamage {

	private static int getAntifireLevel(Player player) {

		int toReturn = 0;

		if (player.getEF().isEffectListed(EffectConstants.ANTIFIRE_NAME)) {
			toReturn++;
		}

		if (player.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
			if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 1540 || player.getPrayers().isActive(NormalPrayer.PROTECT_FROM_MAGIC, CursedPrayer.DEFLECT_MAGIC)
					|| player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11283 || player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11284) {
				toReturn++;
			}
		}

		return toReturn;
	}

	/**
	 * Executes the damage applier and starts the combat again.
	 *
	 * @param attacker
	 * @param target
	 */
	public static void execute(EntityCharacter attacker, EntityCharacter target) {
		if (target == null || attacker.isDead() || target.isDead() || attacker.getZ() != target.getZ() || target.isTeleporting() || attacker.isTeleporting()) {
			return;
		}

		Mob mob = attacker.toMob();
		int anti = target.isMob() ? 0 : getAntifireLevel(target.toPlayer());
		int damage = anti == 2 ? 0 : NumberUtil.inclusiveRandom(anti == 0 ? 40 : 10);

		if (target.isPlayer()) {
			Player p = target.toPlayer();

			switch (anti) {
			case 1:
				if (p.getEquipment().get(Equipment.SLOT_SHIELD) != null && p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 1540) {
					p.sendMessage("Your anti-dragon shield protects you from the dragon fire.");
				}
				break;

			case 2:
				p.sendMessage("Your " + (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 1540 ? "anti-dragon shield" : "dragonfire shield")
						+ " and antifire effect protects you from the dragon fire.");
				break;

			default:
				p.sendMessage("You are badly burnt by the dragon fire!");
			}

			if (p.getEquipment().get(Equipment.SLOT_SHIELD) != null && p.dfsCount <= 49) {
				if (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11284) {
					p.dfsCount++;
					p.playGraphics(new Graphic(1164));
					p.playAnimation(new Animation(6695));
					p.sendMessage("Your dragonfire shield absorbs the dragon breath.");
				}

				if (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11283) {
					p.dfsCount++;
					p.playAnimation(new Animation(6696));
					p.playGraphics(new Graphic(1164));
					p.sendMessage("Your dragonfire shield absorbs the dragon breath.");
				}
			}
		}

		if (target.getHitpoints() - damage < 0) {
			damage = target.getHitpoints();
		}

		target.playGraphics(new Graphic(GraphicHeight.HIGH, mob.endGfx));

		if (target.isPlayer()) {
			MobManager.handleSpecialEffects(target.toPlayer(), mob.getIndex(), damage);

			if (target.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME)) {
				MobManager.appendMobVengeance(target.toPlayer(), mob, damage);
			}

		}

		target.getCombat().setAggressor(mob);
		if(damage > 0) target.getCombat().setLastDamageReceived(System.currentTimeMillis());
		target.getCombat().setLastHitDealt(System.currentTimeMillis());
		mob.getCombat().setLastDamageApplied(System.currentTimeMillis());
		EntityCharacter t = mob.isFamiliar() && mob.summoner != null ? mob.summoner : attacker;
		target.setDamage(new Damage(damage, HitIcons.NONE), t);
		CombatCurse.handleCurses(mob, target, damage, mob.getCombat().getAttackType());
	}

}
