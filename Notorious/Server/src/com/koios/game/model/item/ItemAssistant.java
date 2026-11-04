package com.koios.game.model.item;

import java.util.Objects;
import java.util.Optional;

import com.koios.GameService;
import com.koios.game.def.ItemDefinition;
import com.koios.game.def.LevelRequirement;
import com.koios.game.def.WeaponDefinitions;
import com.koios.game.def.WeaponDefinitions.RangeTypes;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.item.container.equip.Equipment.EquipmentType;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.out.impl.SendItemOnInterfacePacket;
import com.koios.network.out.impl.SendLocalPosition;
import com.koios.network.out.impl.SendMoveWidgetComponentPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.network.out.impl.ToggleWidgetLayer;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

public class ItemAssistant {

	/**
	 * The skill names.
	 */
	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Hitpoints", "Range", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing",
			"Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Construction", "Hunter", "Summoning",
			"Dungeoneering" };

	public static final int[][] SKILLCAPE_IDS = { { 9747, 9748 }, // attack
			{ 9753, 9754 }, // defence
			{ 9750, 9751 }, // strength
			{ 9768, 9769 }, // hitpoints
			{ 9756, 9757 }, // range
			{ 9759, 9760 }, // prayer
			{ 9762, 9763 }, // magic
			{ 9801, 9802 }, // cooking
			{ 9807, 9808 }, // woodcutting
			{ 9783, 9784 }, // fletching
			{ 9798, 9799 }, // fishing
			{ 9804, 9805 }, // firemaking
			{ 9780, 9781 }, // crafting
			{ 9795, 9796 }, // smithing
			{ 9792, 9793 }, // mining
			{ 9774, 9775 }, // herblore
			{ 9771, 9772 }, // agility
			{ 9777, 9778 }, // thieving
			{ 9786, 9787 }, // slayer
			{ 9810, 9811 }, // farming
			{ 9765, 9766 } // runecraft
	};

	/**
	 * Full hat items that cover your face go here. Names go here.
	 */
	private static final String[] CANT_HAVE_BEARD = { "Initiate sallet", "full helm", "mask", "Verac's helm", "Guthan's helm", "Karil's coif", "mask", "Torag's helm", "sallet",
			"veracs", "slayer", "Slayer helmet", "Virtus mask", "Pernix cowl" };

	/**
	 * Full hat items that cover your head go here. Names go here.
	 */
	private static final String[] CANT_HAVE_HAIR = { "med helm", "coif", "Dharok's helm", "hood", "Initiate helm", "Coif", "Armadyl helmet", "Berserker helm", "Archer helm",
			"Farseer helm", "Warrior helm", "void", "h'ween", "full helm", "veracs", "Helm of neitiznot", "Slayer helmet", "Virtus mask", "Pernix cowl" };

	/**
	 * Full body items that cover your arms. Names go here.
	 */
	private static final String[] FULL_BODY = { "top", "shirt", "platebody", "Ahrims robetop", "Karils leathertop", "brassard", "Robe top", "robetop", "platebody (t)",
			"platebody (g)", "chestplate", "torso", "hauberk", "Dragon chainbody", "h'ween", "ghostly robe", "Varrock armour", "Pernix body" };

	private final Player player;

	public ItemAssistant(final Player player) {
		this.player = Objects.requireNonNull(player);
	}

	/**
	 * Calls if an item is a full helm item.
	 */
	public static boolean cantHaveBeard(Item item) {
		if (item == null || item.getDefinition() == null) {
			return false;
		}

		String itemName = item.getDefinition().getName();

		switch (item.getId()) {
		case 7594:
		case 5574:
		case 9672:
		case 4753:
			return true;
		}

		for (String items : CANT_HAVE_BEARD) {
			if (itemName.contains(items)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Calls if an item is a full helm item.
	 */
	public static boolean cantHaveHair(Item item) {
		if (item == null || item.getDefinition() == null) {
			return false;
		}

		switch (item.getId()) {
		case 6918:
		case 2581:
			return false;

		case 3751:
		case 10045:
		case 11664:
		case 11665:
		case 11663:
		case 7594:
		case 9920:
		case 6326:
		case 5574:
		case 9672:
		case 11200:
		case 10342:
		case 15492:
		case 4753:
		case 4724:
			return true;
		}

		String itemName = item.getDefinition().getName();

		for (String element : CANT_HAVE_HAIR) {
			if (itemName.contains(element)) {
				return true;
			}
		}

		return false;
	}

	public static boolean canWear(Player player, int itemId) {
		LevelRequirement l = LevelRequirement.forId(itemId);

		if (itemId == 830) {
			player.sendMessage("This item is currently disabled until the next client update.");
			return false;
		}

		if (l == null) {
			return true;
		}

		for (int i = 0; i < l.getLevelId().length; i++) {
			int skill = l.getLevelId()[i];

			if ((com.koios.game.arpg.CharacterProgress.combatSkill(skill)?player.getArpgProgress().level():player.getSkill().getLevelForExperience(skill)) < l.getRequiredLevel()[i]) {
				player.sendMessage("You need a " + (com.koios.game.arpg.CharacterProgress.combatSkill(skill)?"character":SKILL_NAME[skill]) + " level of " + l.getRequiredLevel()[i] + " to wear this item.");
				return false;
			}
		}

		return true;
	}

	/* TODO this function needs to be eliminated */
	public static int getItemID(String name) {
		for (int i = 0; i < ItemDefinition.toArray().length; i++) {
			if (name.equalsIgnoreCase(ItemDefinition.forId(i).getName())) {
				return i;
			}
		}

		return -1;
	}

	public static int getUntradePrice(int id) {
		switch (id) {
		case 13889:
		case 13895:
		case 13901:
		case 13907:
		case 13913:
		case 13919:
		case 13925:
		case 13931:
		case 13860:
		case 13863:
		case 13866:
		case 13869:
		case 13934:
		case 13937:
		case 13940:
		case 13943:
		case 13872:
		case 13875:
		case 13878:
		case 13946:
		case 13949:
		case 13952:
		case 13886:
		case 13892:
		case 13898:
		case 13904:
		case 13910:
		case 13916:
		case 13922:
			return 5_000_000;
		}

		return 0;
	}

	/**
	 * Calls if an item is a full body item.
	 */
	public static boolean isFullBody(Item item) {
		if (item == null) {
			return false;
		}

		int id = item.getDefinition().getId();
        if(id==22424||id==22426||id==22433||id==22434||id==22438||id==22439)return true;
        if(id==22425)return false;

		// Items that are full bodies go here
		switch (id) {
		case 19453:
		case 19461:
		case 10386:
		case 10370:
		case 10378:
		case 10564:
		case 13887:
		case 13911:
		case 20151:
		case 11758:
		case 6129:
		case 6184:
		case 13870:
		case 13944:
		case 3140:
		case 544:
			return true;

		case 7592:
			return false;
		}

		if (item.getDefinition().getEquipType().equals(EquipmentType.PLATEBODY)) {
			return true;
		}

		String itemName = item.getDefinition().getName();

		for (String element : FULL_BODY) {
			if (itemName.toLowerCase().contains(element) || itemName.toLowerCase().equals(element)) {
				return true;
			}
		}

		return false;
	}

	public static boolean specialCase(int id) {
		switch (id) {
		// PvP armour & weapons
		case 13889:
		case 13895:
		case 13901:
		case 13907:
		case 13913:
		case 13919:
		case 13925:
		case 13931:
		case 13860:
		case 13863:
		case 13866:
		case 13869:
		case 13934:
		case 13937:
		case 13940:
		case 13943:
		case 13872:
		case 13875:
		case 13878:
		case 13946:
		case 13949:
		case 13952:
		case 13886:
		case 13892:
		case 13898:
		case 13904:
		case 13910:
		case 13916:
		case 13922:
			return true;
		}

		return false;
	}

	public void addSpecialBar(final int id) {
		switch (id) {
		case 14484: // Dragon claws
			player.getSession().send(new ToggleWidgetLayer(true, 7800));
			specialAmount(id, player.specialEnergy, 7812);
			break;

		case 15441: // whip
		case 15442: // whip
		case 15443: // whip
		case 15444: // whip
		case 4151: // whip
		case 21371: // vine whip
			player.getSession().send(new ToggleWidgetLayer(true, 12323));
			specialAmount(id, player.specialEnergy, 12335);
			break;

		case 859: // Magic bows
		case 861:
		case 11235: // Dark bow
		case 15701: // dark bow
		case 15702: // dark bow
		case 15703: // dark bow
		case 15704: // dark bow
		case 15241: // dark bow
		case 19143:
		case 19146:
		case 19149:
			player.getSession().send(new ToggleWidgetLayer(true, 7549));
			specialAmount(id, player.specialEnergy, 7561);
			break;

		case 4587: // d scimmy
		case 10887:
		case 11694:
		case 11698:
		case 11700:
		case 11696:
		case 13979: // corrupt scimmy
			player.getSession().send(new ToggleWidgetLayer(true, 7599));
			specialAmount(id, player.specialEnergy, 7611);
			break;

		case 19780:
			player.getSession().send(new ToggleWidgetLayer(true, 7574));
			specialAmount(id, player.specialEnergy, 7586);
			break;

		case 3204: // d hally
			player.getSession().send(new ToggleWidgetLayer(true, 8493));
			specialAmount(id, player.specialEnergy, 8505);
			break;

		case 1377: // d battleaxe
		case 13973:
			player.getSession().send(new ToggleWidgetLayer(true, 7499));
			specialAmount(id, player.specialEnergy, 7511);
			break;

		case 4153: // gmaul
		case 13902: // statius hammer
		case 13904: // statius hammer (deg)
			player.getSession().send(new ToggleWidgetLayer(true, 7474));
			specialAmount(id, player.specialEnergy, 7486);
			break;

		case 1249: // dspear
		case 13905: // vesta spear
		case 13931: // deg vesta spear
			player.getSession().send(new ToggleWidgetLayer(true, 7674));
			specialAmount(id, player.specialEnergy, 7686);
			break;

		case 15486: // SOL
		case 1215:// dragon dagger
		case 1231:
		case 5680:
		case 5698:
		case 1305: // dragon long
		case 11730:
		case 13976: // corrupt dd
		case 13982: // corrupt d long
		case 13899: // vls
		case 13901: // vls (deg)
			player.getSession().send(new ToggleWidgetLayer(true, 7574));
			specialAmount(id, player.specialEnergy, 7586);
			break;

		case 1434: // dragon mace
		case 13985: // corrupt d mace
			player.getSession().send(new ToggleWidgetLayer(true, 7624));
			specialAmount(id, player.specialEnergy, 7636);
			break;

		default:
			player.getSession().send(new ToggleWidgetLayer(false, 7624)); // mace interface
			player.getSession().send(new ToggleWidgetLayer(false, 7474)); // hammer, gmaul
			player.getSession().send(new ToggleWidgetLayer(false, 7499)); // axe
			player.getSession().send(new ToggleWidgetLayer(false, 7549)); // bow interface
			player.getSession().send(new ToggleWidgetLayer(false, 7574)); // sword interface
			player.getSession().send(new ToggleWidgetLayer(false, 7599)); // scimmy sword interface
			player.getSession().send(new ToggleWidgetLayer(false, 8493));
			player.getSession().send(new ToggleWidgetLayer(false, 12323)); // whip interface
		}
	}

	private boolean avasEffect() {
		if (player.getEquipment().get(Equipment.SLOT_CAPE) == null) {
			return false;
		}

		if (player.getEquipment().isSlotUsed(Equipment.SLOT_CHEST)) {
			if (ItemData.doesNegateAvasDeviceEffect(player.getEquipment().get(Equipment.SLOT_CHEST).getId())) {
				return false;
			}
		}

		if (player.getEquipment().get(Equipment.SLOT_CAPE).getId() != 10498 && player.getEquipment().get(Equipment.SLOT_CAPE).getId() != 10499) {
			return false;
		}

		int random = NumberUtil
				.random(player.getEquipment().get(Equipment.SLOT_CAPE).getId() == 10498 ? 3 : player.getEquipment().get(Equipment.SLOT_CAPE).getId() == 10499 ? 4 : 0);

		return random != 1;
	}

	// public void createGroundItem(int itemID, int itemX, int itemY, int itemAmount)
	public void createGroundItem(Item item, Location location) {
		GamePacketBuilder bldr = new GamePacketBuilder(85);

		bldr.writeByteC(location.getY() - 8 * player.getCurrentRegion().getRegionY());
		bldr.writeByteC(location.getX() - 8 * player.getCurrentRegion().getRegionX());
		player.write(bldr.toPacket());
		bldr = new GamePacketBuilder(44);
		bldr.writeLEShortA(item.getId());
		bldr.writeShort(item.getCount());
		bldr.writeByte(0);
		player.write(bldr.toPacket());
	}

	public void deleteArrow() {
		if (player.isSuperAdministrator() || player.getEquipment().get(Equipment.SLOT_ARROWS) == null) {
			return;
		}

		player.avasDeviceEffect = avasEffect();

		if (player.avasDeviceEffect) {
			return;
		}

		if (player.getEquipment().get(Equipment.SLOT_ARROWS).getCount() == 1) {
			player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(-1));
		}

		if (player.getEquipment().get(Equipment.SLOT_ARROWS).getCount() != 0) {

			GamePacketBuilder bldr = new GamePacketBuilder(34, PacketType.SHORT);
			bldr.writeShort(1688);
			bldr.writeByte(Equipment.SLOT_ARROWS);
			bldr.writeShort(player.getEquipment().get(Equipment.SLOT_ARROWS).getId() + 1);

			if (player.getEquipment().get(Equipment.SLOT_ARROWS).getCount() - 1 > 254) {
				bldr.writeByte(255);
				bldr.writeLEInt(player.getEquipment().get(Equipment.SLOT_ARROWS).getCount() - 1);
			} else {
				bldr.writeByte(player.getEquipment().get(Equipment.SLOT_ARROWS).getCount() - 1);
			}

			player.getEquipment().remove(Equipment.SLOT_ARROWS, new Item(player.getEquipment().get(Equipment.SLOT_ARROWS).getId(), 1));
			player.write(bldr);
		}

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void deleteEquipment() {
		
		if (WeaponDefinitions.getWeapon(player.getEquipment().get(3).getId()).getRangeType() == RangeTypes.NEED_WEAPON) {
			player.avasDeviceEffect = avasEffect();

			if (player.avasDeviceEffect) {
				return;
			}
		}
		
		if (player.getEquipment().get(Equipment.SLOT_WEAPON).getCount() == 1) {
			player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(-1));
		}

		if (player.getEquipment().get(Equipment.SLOT_WEAPON).getCount() != 0) {
			GamePacketBuilder bldr = new GamePacketBuilder(34, PacketType.SHORT);
			bldr.writeShort(1688);
			bldr.writeByte(Equipment.SLOT_WEAPON);
			bldr.writeShort(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() + 1);

			if (player.getEquipment().get(Equipment.SLOT_WEAPON).getCount() - 1 > 254) {
				bldr.writeByte(255);
				bldr.writeLEInt(player.getEquipment().get(Equipment.SLOT_WEAPON).getCount() - 1);
			} else {
				bldr.writeByte(player.getEquipment().get(Equipment.SLOT_WEAPON).getCount() - 1);
			}

			player.write(bldr);
			player.getEquipment().remove(Equipment.SLOT_WEAPON, new Item(player.getEquipment().get(Equipment.SLOT_WEAPON).getId(), 1));
		}

		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void dropArrow() {
		if (player.getCombat().getWeaponUsed() != null) {
			if (player.getCombat().getWeaponUsed().getRangeType() == RangeTypes.NO_AMMO) {
				return;
			}
		}

		if (player.avasDeviceEffect) {
			return;
		}

		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		Item arrows = player.getEquipment().get(Equipment.SLOT_ARROWS);

		if (weapon != null && (weapon.getId() == 10033 || weapon.getId() == 10034)) {
			return;
		}

		if (arrows != null && arrows.getId() == 15243) {
			return;
		}

		int enemyX = player.getCombat().getPreviousTarget().getX();
		int enemyY = player.getCombat().getPreviousTarget().getY();
		int enemyZ = player.getCombat().getPreviousTarget().getZ();

		if (NumberUtil.inclusiveRandom(10) >= 4) {
			if (GameService.getGroundItemManager().getAmountOf(player, player.rangeItemUsed, enemyX, enemyY, enemyZ) == 0) {
				GroundItemManager.spawnNew(player, player.rangeItemUsed, enemyX, enemyY, 1);
			} else if (GameService.getGroundItemManager().getAmountOf(player, player.rangeItemUsed, enemyX, enemyY, enemyZ) != 0) {
				int amount = GameService.getGroundItemManager().getAmountOf(player, player.rangeItemUsed, enemyX, enemyY, enemyZ);
				GameService.getGroundItemManager().removeGroundItem(player, player.rangeItemUsed, enemyX, enemyY, enemyZ, false);
				GroundItemManager.spawnNew(player, player.rangeItemUsed, enemyX, enemyY, amount + 1);
			}
		}
	}

	void makeBlade() {
		player.getInventory().remove(new Item(11710));
		player.getInventory().remove(new Item(11712));
		player.getInventory().remove(new Item(11714));
		player.getInventory().add(new Item(11690));
		player.sendMessage("You combine the shards to make a blade.");
	}

	void makeGodsword(int id) {
		if (player.getInventory().contains(11690) && player.getInventory().contains(id)) {
			player.getInventory().remove(new Item(11690));
			player.getInventory().remove(new Item(id));
			player.getInventory().add(new Item(id - 8));
			player.sendMessage("You combine the hilt and the blade to make a godsword.");
		}
	}

	void removeGroundItem(int id, int x, int y, int amount) {
		player.getSession().send(new SendLocalPosition(x, y, false));
		player.write(new GamePacketBuilder(156).writeByteS(0).writeShort(id));
	}

	private void specialAmount(int id, double energy, int barId) {
		player.specBarId = barId;
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 100 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 90 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 80 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 70 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 60 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 50 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 40 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 30 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 20 ? 500 : 0, 0));
		player.getSession().send(new SendMoveWidgetComponentPacket(--barId, energy >= 10 ? 500 : 0, 0));
		updateSpecialBar();
		updateCombatStylesTab(id);
	}

	public void updateCombatStylesTab(int weaponId) {
		Optional<ItemDefinition> definition = ItemDefinition.getOptional(weaponId);
		String weaponName = definition.isPresent() ? definition.get().getName().toLowerCase() : "Unarmed";
		String name = weaponName.replaceAll("bronze", "");
		name = name.replaceAll("iron", "");
		name = name.replaceAll("steel", "");
		name = name.replaceAll("black", "");
		name = name.replaceAll("mithril", "");
		name = name.replaceAll("adamant", "");
		name = name.replaceAll("rune", "");
		name = name.replaceAll("granite", "");
		name = name.replaceAll("dragon", "");
		name = name.replaceAll("drag", "");
		name = name.replaceAll("crystal", "");
		name = name.trim();

		if (weaponName.equals("Unarmed")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 5855)); // punch, kick, block
			player.getSession().send(new SendStringPacket(weaponName, 5857));
		} else if (weaponName.endsWith("whip")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 12290)); // flick, lash, deflect
			player.getSession().send(new SendItemOnInterfacePacket(12291, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 12293));
		} else if (weaponName.endsWith("bow") || weaponName.endsWith("chinchompa") || weaponName.endsWith("10") || weaponName.endsWith("full") || weaponName.startsWith("seercull")
				|| weaponName.contains("cannon")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 1764)); // accurate, rapid, longrange
			player.getSession().send(new SendItemOnInterfacePacket(1765, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 1767));
		} else if (!name.endsWith("of light") && weaponName.startsWith("staff") || weaponName.endsWith("staff") || weaponName.endsWith("wand")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 328)); // spike, impale, smash, block
			player.getSession().send(new SendItemOnInterfacePacket(329, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 330));
		} else if (name.startsWith("dart") || name.startsWith("knife") || name.startsWith("javelin") || weaponName.equalsIgnoreCase("toktz-xil-ul")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 4446)); // accurate, rapid, longrange
			player.getSession().send(new SendItemOnInterfacePacket(4447, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 4449));
		} else if (name.contains("dagger") || name.contains("sword") && !name.contains("godsword") || name.contains("staff of light")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 2276)); // stab, lunge, slash, block
			player.getSession().send(new SendItemOnInterfacePacket(2277, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 2279));
		} else if (name.startsWith("pickaxe")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 5570)); // spike, impale, smash, block
			player.getSession().send(new SendItemOnInterfacePacket(5571, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 5573));
		} else if (name.contains("axe") || name.contains("hatchet")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 1698)); // chop, hack, smash, block
			player.getSession().send(new SendItemOnInterfacePacket(1699, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 1701));
		} else if (name.contains("claws")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 7762));
			player.getSession().send(new SendItemOnInterfacePacket(7763, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 7765));
		} else if (name.startsWith("halberd")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 8460)); // jab, swipe, fend
			player.getSession().send(new SendItemOnInterfacePacket(8461, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 8463));
		} else if (name.startsWith("scythe")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 8460)); // jab, swipe, fend
			player.getSession().send(new SendItemOnInterfacePacket(8461, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 8463));
		} else if (name.contains("spear")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 4679)); // lunge, swipe, pound, block
			player.getSession().send(new SendItemOnInterfacePacket(4680, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 4682));
		} else if (name.toLowerCase().contains("mace")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 3796));
			player.getSession().send(new SendItemOnInterfacePacket(3797, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 3799));
		} else if (name.contains("maul") || name.contains("hammer")) {
			player.getSession().send(new SendSidebarInterfacePacket(0, 425)); // war hamer equip.
			player.getSession().send(new SendItemOnInterfacePacket(426, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(definition.get().getName()), 428));
		} else {
			player.getSession().send(new SendSidebarInterfacePacket(0, 2423)); // chop, slash, lunge, block
			player.getSession().send(new SendItemOnInterfacePacket(2424, 200, weaponId));
			player.getSession().send(new SendStringPacket(StringUtil.capitalize(weaponName), 2426));
		}
	}

	public void updateSpecialBar() {
		if (!player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
			return;
		}

		if (player.usingSpecial && player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 15050) {
			player.getSession().send(new SendStringPacket("@yel@ Special Attack (" + player.specialEnergy + "%)", player.specBarId));
		} else {
			player.getSession().send(new SendStringPacket("@bla@ Special Attack (" + player.specialEnergy + "%)", player.specBarId));
		}
	}

}
