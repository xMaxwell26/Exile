package com.koios.game.content.degrading;

import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

/**
 * Handles the armour degrading system.
 *
 * @author Ben Stulpin
 */
public class ArmourDegrading {

	/**
	 * The degradeable equipment slots.
	 */
	private static final int[] EQUIPMENT_SLOTS = { 0, 3, 4, 7 };

	/**
	 * The chest plate's health.
	 */
	private int chestHealth;

	/**
	 * The helmet's health.
	 */
	private int helmetHealth;

	/**
	 * The legging's health.
	 */
	private int leggingHealth;

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The weapon's health.
	 */
	private int weaponHealth;

	/**
	 * Creates a new instance for the player.
	 *
	 * @param player the player.
	 */
	public ArmourDegrading(Player player) {
		this.player = player;
	}

	/**
	 * We get the chest plate's health.
	 *
	 * @return the chest plate's health.
	 */
	public int getChestHealth() {
		return chestHealth;
	}

	/**
	 * We get the helmet's health.
	 *
	 * @return the helmet's health.
	 */
	public int getHelmetHealth() {
		return helmetHealth;
	}

	/**
	 * We get the legging's health.
	 *
	 * @return the legging's health.
	 */
	public int getLeggingHealth() {
		return leggingHealth;
	}

	/**
	 * We get the weapon's health.
	 *
	 * @return the weapon's health.
	 */
	public int getWeaponHealth() {
		return weaponHealth;
	}

	/**
	 * We handle the equipment health refreshing.
	 */
	public void handleHealthRefresh(int slot) {

		/**
		 * The degradeable armour.
		 */
		Armour armour = Armour.get(player.getEquipment().get(slot).getId());

		/**
		 * We set the equipment's health value.
		 */
		handleHealthValue(slot, true, armour == null ? 0 : armour.getHealth());
	}

	/**
	 * Sets the equipment health according to the slot.
	 *
	 * @param slot the equipment slot.
	 * @param set the set to specific value flag.
	 * @param setTo the health to set for a specific value.
	 */
	private void handleHealthValue(int slot, boolean set, int setTo) {
		/**
		 * We deduct equipment health according to the slot.
		 */
		switch (slot) {
		case 0: /* Helmet Slot */
			/**
			 * We deduct one from the helmet health.
			 */
			setHelmetHealth(set ? setTo : helmetHealth - 1);
			break;

		case 3: /* Weapon Slot */
			/**
			 * We deduct one from the weapon health.
			 */
			setWeaponHealth(set ? setTo : weaponHealth - 1);
			break;

		case 4: /* Chest Slot */
			/**
			 * We deduct one from the chest health.
			 */
			setChestHealth(set ? setTo : chestHealth - 1);
			break;

		case 7: /* Leggings Slot */
			/**
			 * We deduct one from the legging health.
			 */
			setLeggingHealth(set ? setTo : leggingHealth - 1);
		}
	}

	/**
	 * We handle the reduction for the equipment that is being worn.
	 *
	 * @param weaponDegrade the weapon degrade flag.
	 */
	public void handleReduction(boolean weaponDegrade) {
		/**
		 * We loop through the slots.
		 */
		for (int element : EQUIPMENT_SLOTS) {
			/**
			 * The degradeable armour.
			 */
			final Armour armour = Armour.get(player.getEquipment().get(element) == null ? -1 : player.getEquipment().get(element).getId());

			/**
			 * We check if the armour is an active degradable.
			 */
			if (armour == null || weaponDegrade && armour.getSlot() != 3 || !weaponDegrade && armour.getSlot() == 3) {
				continue;
			}

			/**
			 * We reduce the equipment's health.
			 */
			reduceEquipmentHealth(armour);
		}
	}

	/**
	 * Reduces the health of the equipment.
	 *
	 * @param armour the degradeable armour.
	 */
	private void reduceEquipmentHealth(final Armour armour) {
		/**
		 * We check if the player is wearing the equipment.
		 */
		if (player.getEquipment().get(armour.getSlot()).getId() != armour.getId() && player.getEquipment().get(armour.getSlot()).getId() != armour.getDegradedId()) {
			return;
		}

		/**
		 * We deduct from the equipment's health.
		 */
		handleHealthValue(armour.getSlot(), false, 0);

		/**
		 * We check if the armour has not yet degraded.
		 */
		if (player.getEquipment().get(armour.getSlot()).getId() == armour.getId()) {

			/**
			 * We send the degrade message.
			 */
			player.sendMessage("Your " + ItemDefinition.forId(armour.getId()).getName() + " has degraded slightly upon use!");

			player.getEquipment().set(armour.getSlot(), new Item(armour.getDegradedId()));

			/**
			 * We refresh the weapon's health.
			 */
			handleHealthRefresh(armour.getSlot());

			/**
			 * We check if the equipment's health is 0.
			 */
		} else if (player.getEquipment().get(armour.getSlot()).getId() == armour.getDegradedId() && (weaponHealth <= 0 && armour.getSlot() == 3
				|| helmetHealth <= 0 && armour.getSlot() == 0 || chestHealth <= 0 && armour.getSlot() == 4 || leggingHealth <= 0 && armour.getSlot() == 7)) {
			/**
			 * We send the degrade message.
			 */
			player.sendMessage("Your " + ItemDefinition.forId(armour.getId()).getName() + " has degraded fully!");
			player.getEquipment().set(armour.getSlot(), new Item(-1));
		}
	}

	/**
	 * We set the chest plate's health.
	 */
	public void setChestHealth(int chestHealth) {
		this.chestHealth = chestHealth;
	}

	/**
	 * We set the helmet's health.
	 */
	public void setHelmetHealth(int helmetHealth) {
		this.helmetHealth = helmetHealth;
	}

	/**
	 * We set the legging's health.
	 */
	public void setLeggingHealth(int leggingHealth) {
		this.leggingHealth = leggingHealth;
	}

	/**
	 * We set the weapon's health.
	 *
	 * @param weaponHealth the weapon's health.
	 */
	public void setWeaponHealth(int weaponHealth) {
		this.weaponHealth = weaponHealth;
	}

}
