package com.koios.game.content.combat.special.impl;

import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.melee.MeleeData;
import com.koios.game.content.combat.ranged.RangedData;
import com.koios.game.content.combat.special.SpecialAttack;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;

public class DefaultSpecials implements SpecialAttack {

	@Override
	public int getSpecialRequired(int weaponId) {
		switch (weaponId) {
		case 1249:
		case 1215:
		case 1231:
		case 5680:
		case 5698:
		case 1305:
		case 1434:
		case 13899:
		case 13901:
		case 13976:
		case 13982:
		case 13985:
		case 13923:
		case 13925:
			return 25;

		case 3204:
			return 30;

		case 13902:
		case 13904:
			return 35;

		case 4151:
		case 21371:
		case 15441:
		case 15442:
		case 15443:
		case 15444:
		case 11694:
		case 14484:
		case 11698:
		case 4153:
		case 13905:
		case 13929:
		case 13931:
		case 15016:
		case 15015:
		case 10887:
		case 15241:
			return 50;

		case 4587:
		case 859:
		case 861:
		case 11235:
		case 15701:
		case 15702:
		case 15703:
		case 15704:
		case 13979:

		case 19143: // Saradomin bow
		case 19146: // guthix bow
		case 19149: // zamorak bow
			return 55;

		case 11700:
		case 19780:
			return 60;

		case 1377:
		case 11696:
		case 11730:
		case 13973:
		case 15486:
			return 100;
		default:
			return 0;
		}
	}

	@Override
	public boolean canExecute(Player attacker, EntityCharacter target, int weapon_id) {
		if (attacker == null || target == null || attacker.isDead() || target.isDead()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean execute(Player attacker, EntityCharacter target, int weapon_id) {
		switch (weapon_id) {
		case 10887:
			attacker.playGraphics(new Graphic(1027));
			attacker.playAnimation(new Animation(5870));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 1305: // dragon long
		case 13982:
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 248));
			attacker.playAnimation(new Animation(1058));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 1215: // dragon daggers
		case 1231:
		case 5680:
		case 5698:
		case 13976:
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 252));
			attacker.playAnimation(new Animation(1062));
			attacker.doubleHit = true;
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 11730: // Saradomin sword
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 1224));
			attacker.playAnimation(new Animation(7072));
			attacker.doubleHit = true;
			attacker.ssSpec = true;
			attacker.specEffect = 6;
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 4151: // Abyssal whip
		case 21371:
		case 15441:
		case 15442:
		case 15443:
		case 15444:
			target.playGraphics(new Graphic(GraphicHeight.LOW, 341));
			attacker.playAnimation(new Animation(1658));
			attacker.specEffect = 5;
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 13899: // Vesta's longsword
		case 13901: // Vesta's longsword (deg)
			attacker.playAnimation(new Animation(10502));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 13905: // Vesta spear
		case 13907: // Vesta spear (deg)
			attacker.playAnimation(new Animation(10499));
			attacker.playGraphics(new Graphic(1835));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 13902: // Statius's warhammer
		case 13904: // Statius's warhammer (deg)
			attacker.playAnimation(new Animation(10505));
			attacker.playGraphics(new Graphic(1840));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			attacker.specEffect = 7;
			return true;

		case 11694: // Armadyl godsword
			attacker.playAnimation(new Animation(7074));
			attacker.playGraphics(new Graphic(1222));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 11700: // Zamorak godsword
			attacker.playAnimation(new Animation(7070));
			attacker.playGraphics(new Graphic(1221));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			attacker.specEffect = 2;
			return true;

		case 11696: // Bandos godsword
			attacker.playAnimation(new Animation(7073));
			attacker.playGraphics(new Graphic(1223));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			attacker.specEffect = 3;
			return true;

		case 11698:// Saradomin godsword
			attacker.playAnimation(new Animation(7071));
			attacker.playGraphics(new Graphic(1220));
			attacker.specEffect = 4;
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 1249: // Dragon spear
		case 13988:
			attacker.playAnimation(new Animation(1872));
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 253));
			if (target.isPlayer()) {
				getSpeared(target.toPlayer(), attacker.getX(), attacker.getY());
			}
			return true;

		case 3204: // Dragon halberd
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 282));
			attacker.playAnimation(new Animation(1203));

			if (attacker.getCombat().getTarget() != null) {
				if (!attacker.goodDistance(attacker.getX(), attacker.getY(), attacker.getCombat().getTarget().getX(), attacker.getCombat().getTarget().getY(), 1)) {
					attacker.doubleHit = true;
				}
			}

			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 4587: // Dragon scimitar
		case 13979:
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 347));
			attacker.playAnimation(new Animation(1872));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			attacker.specEffect = 1;
			return true;

		case 1434: // Dragon mace
		case 13985:
			attacker.playAnimation(new Animation(1060));
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 251));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			return true;

		case 859: // Magic longbow
			attacker.bowSpecShot = 3;
			attacker.rangeItemUsed = attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId();
			attacker.getItems().deleteArrow();
			attacker.playAnimation(new Animation(426));
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, 250));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));
			attacker.projectileStage = 1;

			if (attacker.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE) {
				attacker.getCombat().setAttackTimer(attacker.getCombat().getAttackTime() - 1);
			}
			return true;

		case 861: // Magic shortbow
			attacker.bowSpecShot = 1;
			attacker.rangeItemUsed = attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId();
			attacker.getItems().deleteArrow();
			attacker.getItems().deleteArrow();
			attacker.playAnimation(new Animation(1074));
			attacker.getCombat().setHitDelay(3);
			attacker.projectileStage = 1;
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));

			if (attacker.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE) {
				attacker.getCombat().setAttackTimer(attacker.getCombat().getAttackTime() - 1);
			}

			RangedData.fireProjectile(attacker);
			return true;

		case 19143: // Saradomin bow
		case 19146:
		case 19149:
			attacker.bowSpecShot = 1;
			attacker.rangeItemUsed = attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId();
			attacker.getItems().deleteArrow();
			attacker.getItems().deleteArrow();
			attacker.playAnimation(new Animation(1074));
			attacker.projectileStage = 1;
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));

			if (attacker.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE) {
				attacker.getCombat().setAttackTimer(attacker.getCombat().getAttackTime() - 1);
			}

			RangedData.fireProjectile(attacker);
			return true;

		case 11235: // Dark bow
		case 15701: // Dark bow
		case 15702: // Dark bow
		case 15703: // Dark bow
		case 15704: // Dark bow
			attacker.dbowSpec = true;
			attacker.rangeItemUsed = attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId();
			attacker.getItems().deleteArrow();
			attacker.getItems().deleteArrow();
			attacker.getItems().dropArrow();
			attacker.getCombat().setHitDelay(3);
			attacker.playAnimation(new Animation(426));
			attacker.projectileStage = 1;
			attacker.playGraphics(new Graphic(GraphicHeight.HIGH, RangedData.getRangeStartGFX(attacker)));
			attacker.getCombat().setHitDelay(MeleeData.getHitDelay(attacker));

			if (attacker.getCombat().getAttackStyle() == AttackStyle.AGGRESSIVE) {
				attacker.getCombat().setAttackTimer(attacker.getCombat().getAttackTime() - 1);
			}

			RangedData.fireProjectile(attacker);
			return true;
		}

		return false;
	}

	private void getSpeared(Player player, int otherX, int otherY) {

		if (player.getDuel().isRuleActive(DuelingRules.NO_MOVEMENT)) {
			player.sendMessage("Melee has been disabled in this duel!");
			return;
		}

		int x = player.getLocation().getX() - otherX;
		int y = player.getLocation().getY() - otherY;

		if (x > 0) {
			x = 1;
		} else if (x < 0) {
			x = -1;
		}

		if (y > 0) {
			y = 1;
		} else if (y < 0) {
			y = -1;
		}

		moveCheck(player, x, y);
		player.lastSpear = System.currentTimeMillis();
	}

	private void moveCheck(Player player, final int horizontal, final int vertical) {
		player.setPosition(player.getLocation().getX() + horizontal, player.getLocation().getY() + vertical, player.getLocation().getZ());
	}

}
