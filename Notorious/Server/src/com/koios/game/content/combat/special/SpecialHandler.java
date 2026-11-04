package com.koios.game.content.combat.special;

import java.util.HashMap;
import java.util.Map;

import com.koios.GameService;
import com.koios.game.content.combat.special.impl.DefaultSpecials;
import com.koios.game.content.combat.special.impl.DragonClawsSpecial;
import com.koios.game.content.combat.special.impl.HandCannonSpecial;
import com.koios.game.content.combat.special.impl.KorasiSwordSpecial;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handle the special attack list and activation.
 *
 * @author Gabriel | Wolfs Darker
 */
public class SpecialHandler {

	private static final Map<Integer, SpecialAttack> specialAttacks = new HashMap<Integer, SpecialAttack>();

	static {
		specialAttacks.put(859, new DefaultSpecials());
		specialAttacks.put(861, new DefaultSpecials());
		specialAttacks.put(1215, new DefaultSpecials());
		specialAttacks.put(1231, new DefaultSpecials());
		specialAttacks.put(1249, new DefaultSpecials());
		specialAttacks.put(1305, new DefaultSpecials());
		specialAttacks.put(1377, new DefaultSpecials());
		specialAttacks.put(1434, new DefaultSpecials());
		specialAttacks.put(3204, new DefaultSpecials());
		specialAttacks.put(4151, new DefaultSpecials());
		specialAttacks.put(4153, new DefaultSpecials());
		specialAttacks.put(4587, new DefaultSpecials());
		specialAttacks.put(5680, new DefaultSpecials());
		specialAttacks.put(5698, new DefaultSpecials());
		specialAttacks.put(10887, new DefaultSpecials());
		specialAttacks.put(11235, new DefaultSpecials());
		specialAttacks.put(11730, new DefaultSpecials());
		specialAttacks.put(11694, new DefaultSpecials());
		specialAttacks.put(11696, new DefaultSpecials());
		specialAttacks.put(11698, new DefaultSpecials());
		specialAttacks.put(11700, new DefaultSpecials());
		specialAttacks.put(13899, new DefaultSpecials());
		specialAttacks.put(13901, new DefaultSpecials());
		specialAttacks.put(13902, new DefaultSpecials());
		specialAttacks.put(13904, new DefaultSpecials());
		specialAttacks.put(13905, new DefaultSpecials());
		specialAttacks.put(13907, new DefaultSpecials());
		specialAttacks.put(13976, new DefaultSpecials());
		specialAttacks.put(13979, new DefaultSpecials());
		specialAttacks.put(13988, new DefaultSpecials());
		specialAttacks.put(13985, new DefaultSpecials());
		specialAttacks.put(15441, new DefaultSpecials());
		specialAttacks.put(15442, new DefaultSpecials());
		specialAttacks.put(15443, new DefaultSpecials());
		specialAttacks.put(15444, new DefaultSpecials());
		specialAttacks.put(15486, new DefaultSpecials());
		specialAttacks.put(19143, new DefaultSpecials());
		specialAttacks.put(19146, new DefaultSpecials());
		specialAttacks.put(19149, new DefaultSpecials());
		specialAttacks.put(21371, new DefaultSpecials());

		specialAttacks.put(14484, new DragonClawsSpecial());
		specialAttacks.put(15241, new HandCannonSpecial());
		specialAttacks.put(19780, new KorasiSwordSpecial());
	}

	/**
	 * Activates the special attack.
	 *
	 * @param attacker
	 * @param target
	 * @return if it was successfull.
	 */
	public static boolean activateSpecial(Player attacker, EntityCharacter target) {

		if (attacker == null || target == null || attacker.isDead() || target.isDead()) {
			return false;
		}

		if (!attacker.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
			reset(attacker);
			return false;
		}

		int weapon_id = attacker.getEquipment().get(Equipment.SLOT_WEAPON).getId();

		SpecialAttack special = specialAttacks.get(weapon_id);
		
		if (special == null) {
			attacker.sendMessage("This special is unavaliable.");
			reset(attacker);
			return false;
		}

		if (attacker.specialEnergy < special.getSpecialRequired(weapon_id)) {
			attacker.sendMessage("You don't have the required special energy to use this attack.");
			reset(attacker);
			return false;
		}

		attacker.getCombat().setOldTarget(target);
		
		if (special.canExecute(attacker, target, weapon_id) && special.execute(attacker, target, weapon_id)) {
			attacker.specialEnergy -= special.getSpecialRequired(weapon_id);
			attacker.getCombat().setLastWeapon(weapon_id);
			reset(attacker);
		}

		if (!attacker.specialEvent && attacker.specialEnergy < 100) {
			attacker.specialEvent = true;
			GameService.getTaskScheduler().schedule(new SpecialRestoreTask(attacker));
		}

		return true;
	}

	/**
	 * Handles restoring the players special attack energy once they've activated a special
	 *
	 * @author Timothy Howard <Arithium>
	 */
	public static class SpecialRestoreTask extends Task {

		private final Player attacker;

		public SpecialRestoreTask(Player player) {
			attacker = player;
		}

		private int restoreTimer = 30;

		@Override
		public void execute() {
			if (attacker.isDisconnected()) {
				stop();
				return;
			}

			if (attacker.specialEnergy >= 100) {
				attacker.specialEnergy = 100;
				stop();
				return;
			}

			if (restoreTimer > 0) {
				restoreTimer--;
			}

			if (restoreTimer == 0) {
				if (attacker.specialEnergy < 100) {
					restoreTimer = 30;
					attacker.specialEnergy += 5;

					if (attacker.specialEnergy > 100) {
						attacker.specialEnergy = 100;
					}
					if (attacker.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
						attacker.getItems().addSpecialBar(attacker.getEquipment().get(Equipment.SLOT_WEAPON).getId());
						attacker.getItems().updateSpecialBar();
					}
				} else {
					stop();
					return;
				}
			}

		}

		@Override
		public void onStop() {
			attacker.specialEvent = false;
		}

	};

	/**
	 * Checks if the player has enough special energy.
	 *
	 * @param player
	 * @param weapon_id
	 * @return
	 */
	public static boolean checkSpecial(Player player, int weapon_id) {
		
		if (player == null || player.isDead()) {
			return false;
		}

		if (!player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
			reset(player);
			return false;
		}

		if (!player.getController().canUseSpecialAttack(player)) {
			return false;
		}

		SpecialAttack special = specialAttacks.get(weapon_id);
		
		if (special == null) {
			player.sendMessage("This special is unavaliable.");
			reset(player);
			return false;
		}

		if (player.specialEnergy < special.getSpecialRequired(weapon_id)) {
			player.sendMessage("You don't have the required special energy to use this attack.");
			reset(player);
			return false;
		}

		if (player.specialEnergy < 0) {
			player.specialEnergy = 0;
		}

		player.specialEnergy -= special.getSpecialRequired(weapon_id);

		return true;
	}

	/**
	 * Resets the special bar and attack.
	 *
	 * @param player
	 */
	public static void reset(Player player) {
		player.usingSpecial = false;
		player.getItems().updateSpecialBar();
		player.getItems().addSpecialBar(player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1);
	}

}
