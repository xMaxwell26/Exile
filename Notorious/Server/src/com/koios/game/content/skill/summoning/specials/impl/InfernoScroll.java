package com.koios.game.content.skill.summoning.specials.impl;

import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CalculateHit;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.specials.FamiliarScroll;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

/**
 * Handles the forge regent's scroll ability.
 *
 * @author Gabriel | Wolfs Darker
 */
public class InfernoScroll implements FamiliarScroll {

	@Override
	public boolean isTargetRequired() {
		return true;
	}

	@Override
	public boolean canCastScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {
		return true;
	}

	@Override
	public boolean castScroll(Player player, ScrollsData scroll, EntityCharacter target, Object[] extra_data) {

		MobFamiliar familiar = player.getSummoning().getFamiliar();

		player.playGraphics(new Graphic(1316));
		player.playAnimation(new Animation(7660));
		familiar.playAnimation(new Animation(7871));
		familiar.playGraphics(new Graphic(1394));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			public void execute() {
				if (target == null || familiar == null) {
					stop();
					return;
				}
				target.playGraphics(new Graphic(1395));
				target.setDamage(new Damage(CalculateHit.execute(familiar, target, AttackType.MAGIC), HitMasks.REGULAR_HIT, HitIcons.MAGIC), player);
				if (!target.isDead() && target.isPlayer()) {
					Player t = target.toPlayer();
					int misc = NumberUtil.inclusiveRandom(2);
					int slot = misc == 0 ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD;
					if (t.getEquipment().get(slot) == null) {
						slot = slot == Equipment.SLOT_WEAPON ? Equipment.SLOT_SHIELD : Equipment.SLOT_WEAPON;
					}
					if (t.getEquipment().get(slot) != null) {
						if (t.getInventory().add(new Item(t.getEquipment().get(slot).getId(), 1))) {
							t.getEquipment().set(slot, null);
						}
					}
				}
				stop();
			}
		});

		return true;
	}

}
