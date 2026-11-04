package com.koios.game.model.item.container.equip;

import java.util.Objects;

import com.koios.game.content.Bonus;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.ContainerListener;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

/**
 * A listener which updates the weapon tab.
 *
 * @author Graham Edgecombe
 */
public class WeaponContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Creates the listener.
	 *
	 * @param player The player.
	 */
	public WeaponContainerListener(Player player) {
		this.player = Objects.requireNonNull(player, "null player");
	}

	@Override
	public void onSingleItemChange(Container container, int slot) {
		if (slot == Equipment.SLOT_WEAPON) {
			int weaponId = player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1;
			player.getItems().updateCombatStylesTab(weaponId);

			if (weaponId > 0) {
				player.getItems().addSpecialBar(weaponId);
				player.getItems().updateSpecialBar();
			} else {
				if (player.usingSpecial) {
					player.usingSpecial = false;
				}
			}
		}

		sendBonuses();
	}

	@Override
	public void onItemGroupChange(Container container, int[] slots) {
		for (int slot : slots) {
			if (slot == Equipment.SLOT_WEAPON) {
				int weaponId = player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1;
				player.getItems().updateCombatStylesTab(weaponId);

				if (weaponId > 0) {
					player.getItems().addSpecialBar(weaponId);
					player.getItems().updateSpecialBar();
				} else {
					if (player.usingSpecial) {
						player.usingSpecial = false;
					}
				}
			}
		}

		sendBonuses();
	}

	@Override
	public void onFullItemChange(Container container) {
		int weaponId = player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON) ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1;

		player.getItems().updateCombatStylesTab(weaponId);

		if (weaponId > 0) {
			player.getItems().addSpecialBar(weaponId);
			player.getItems().updateSpecialBar();
		} else {
			if (player.usingSpecial) {
				player.usingSpecial = false;
			}
		}

		sendBonuses();
	}

	/**
	 * Sends and recalculates bonuses.
	 */
	public void sendBonuses() {
		// Calculate new bonues.

		if (player == null) {
			return;
		}

		player.resetBonuses();

		for (Item item : player.getEquipment().toArray()) {
			if (item == null) {
				continue;
			}

			ItemDefinition definition = item.getDefinition();

			if (definition != null) {
				double[] bonus = definition.getBonus();

				for (int i = 0; i < item.getDefinition().getBonus().length; i++) {
					player.playerBonus[i] += bonus[i];
				}
			}
		}

		if (player.dfsCount > 0 && player.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
			if (player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11283 || player.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11284) {
				player.playerBonus[5] += player.dfsCount;
				player.playerBonus[6] += player.dfsCount;
				player.playerBonus[7] += player.dfsCount;
				player.playerBonus[9] += player.dfsCount;
			}
		}

		// Send bonus.
		player.getSession().send(new SendStringPacket("Stab: +" + player.playerBonus[Bonus.STAB_ATTACK], 1675));
		player.getSession().send(new SendStringPacket("Stab: +" + player.playerBonus[Bonus.STAB_ATTACK], 1675));
		player.getSession().send(new SendStringPacket("Slash: +" + player.playerBonus[Bonus.SLASH_ATTACK], 1676));
		player.getSession().send(new SendStringPacket("Crush: +" + player.playerBonus[Bonus.CRUSH_ATTACK], 1677));
		player.getSession().send(new SendStringPacket("Magic: +" + player.playerBonus[Bonus.MAGIC_ATTACK], 1678));
		player.getSession().send(new SendStringPacket("Range: +" + player.playerBonus[Bonus.RANGE_ATTACK], 1679));
		player.getSession().send(new SendStringPacket("Stab: +" + player.playerBonus[Bonus.STAB_DEF], 1680));
		player.getSession().send(new SendStringPacket("Slash: +" + player.playerBonus[Bonus.SLASH_DEF], 1681));
		player.getSession().send(new SendStringPacket("Crush: +" + player.playerBonus[Bonus.CRUSH_DEF], 1682));
		player.getSession().send(new SendStringPacket("Magic: +" + player.playerBonus[Bonus.MAGIC_DEF], 1683));
		player.getSession().send(new SendStringPacket("Range: +" + player.playerBonus[Bonus.RANGE_DEF], 1684));
		player.getSession().send(new SendStringPacket("Strength: +" + player.playerBonus[Bonus.STRENGTH_BONUS], 1686));
		player.getSession().send(new SendStringPacket("Prayer: +" + player.playerBonus[Bonus.PRAYER_BONUS], 1687));
	}

}
