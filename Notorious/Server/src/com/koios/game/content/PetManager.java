package com.koios.game.content;

import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.def.MobDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.mob.impl.MobPet;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.network.out.impl.SendMobHeadOnInterfacePacket;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

/**
 * @author DF
 */
public final class PetManager {

	public static enum PetDefinition {

		KITTEN_A(1555, 761, 1561), KITTEN_B(1556, 762, 1562), KITTEN_C(1557, 763, 1563), KITTEN_D(1558, 764, 1564), KITTEN_E(1559, 765, 1565), KITTEN_F(1560, 766, 1566), CAT_A(
				1561, 768, 1567), CAT_B(1562, 769, 1568), CAT_C(1563, 770, 1569), CAT_D(1564, 771, 1570), CAT_E(1565, 772, 1571), CAT_F(1566, 773, 1573), OVERGROWN_CAT_A(1567, 774,
						-1), OVERGROWN_CAT_B(1568, 775, -1), OVERGROWN_CAT_C(1569, 776, -1), OVERGROWN_CAT_D(1570, 777, -1), OVERGROWN_CAT_E(1571, 778, -1), OVERGROWN_CAT_F(1572,
								779, -1), CLOCKWORK_CAT(7771, 3598, -1), HELL_CAT(7582, 3504, -1), RED_HATCHLING_BABY_DRAGON(12469, 6900, 12470), RED_BABY_DRAGON(12470, 6901,
										-1), BLUE_HATCHLING_BABY_DRAGON(12471, 6902, 12472), BLUE_BABY_DRAGON(12472, 6903, -1), GREEN_HATCHLING_BABY_DRAGON(12473, 6904,
												12474), GREEN_BABY_DRAGON(12474, 6905, -1), BLACK_HATCHLING_BABY_DRAGON(12475, 6906, 12476), BLACK_BABY_DRAGON(12476, 6907,
														-1), BABY_PENGUIN(12481, 6908, 12482), PENGUIN(12482, 6909, -1), RAVEN_CHICK(12484, 6911, 12485), RAVEN(12485, 6912,
																-1), BABY_RACCOON(12486, 6913, 12487), RACCOON(12487, 6914, -1), BABY_GECKO(12488, 6915, 12489), GECKO(12489, 6916,
																		-1), BABY_SQUIRREL(12490, 6919, 12491), SQUIRREL(12491, 6920, -1), BABY_CHAMELEON(12492, 6922,
																				12493), CHAMELEON(12493, 6923, -1), BABY_MONKEY(12496, 6942, 12497), MONKEY(12497, 6943,
																						-1), VULTURE_CHICK(12498, 6945, 12499), VULTURE(12499, 6946, -1), BABY_GIANT_CRAB(12500,
																								6947, 12501), GIANT_CRAB(12501, 6948, -1), SARADOMIN_CHICK(12503, 6949,
																										12504), SARADOMIN_BIRD(12504, 6950, -1), SARADOMIN_OWL(12505, 6951,
																												-1), ZAMORAK_CHICK(12506, 6952, 12507), ZAMORAK_BIRD(12507, 6953,
																														-1), ZAMORAK_HAWK(12508, 6954, -1), GUTHIX_CHICK(12509,
																																6955,
																																12510), GUTHIX_BIRD(12510, 6956, -1), GUTHIX_RAPTOR(
																																		12511, 6957, -1), TERRIER_PUPPY(12512, 6958,
																																				12513), TERRIER(12513, 6959,
																																						-1), GREYHOUND_PUPPY(12514,
																																								6960,
																																								12515), GREYHOUND(
																																										12515, 6961,
																																										-1), LABRADOR_PUPPY(
																																												12516,
																																												6962,
																																												12517), LABRADOR(
																																														12517,
																																														6963,
																																														-1), DALMATIAN_PUPPY(
																																																12518,
																																																6964,
																																																12519), DALMATIAN(
																																																		12519,
																																																		6965,
																																																		-1), SHEEPDOG_PUPPY(
																																																				12520,
																																																				6966,
																																																				12521), SHEEPDOG(
																																																						12521,
																																																						6967,
																																																						-1), BULLDOG_PUPPY(
																																																								12522,
																																																								6969,
																																																								12523), BULLDOG(
																																																										12523,
																																																										6968,
																																																										-1);

		private final int itemId;
		private final int mobId;
		private final int nextStage;

		static Map<Integer, PetDefinition> pet_data = new HashMap<>();

		static {
			for (PetDefinition p : values()) {
				pet_data.put(p.getItemId(), p);
			}
		}

		private PetDefinition(int itemId, int mobId, int nextStage) {
			this.itemId = itemId;
			this.mobId = mobId;
			this.nextStage = nextStage;
		}

		public static PetDefinition get(int item_id) {
			return pet_data.get(item_id);
		}

		public int getItemId() {
			return itemId;
		}

		public int getMobId() {
			return mobId;
		}

		public int getNextStage() {
			return nextStage;
		}

	}

	/**
	 * Picks the player's pet and put it in his inventory.
	 *
	 * @param player
	 * @param mob
	 * @return
	 */
	public static boolean pickUpPet(Player player, Mob mob) {
		if (player.getSummoning().getFamiliarItem() <= 0 || player.getSummoning().getPet() == null) {
			return false;
		}

		PetDefinition pets = PetDefinition.get(player.getSummoning().getFamiliarItem());

		if (pets == null) {
			return false;
		}

		if (mob.summoner == null || mob.getIndex() != player.getSummoning().getPet().getIndex() || mob.summoner != null && mob.summoner.getIndex() != player.index()) {
			player.sendMessage("This is not your pet.");
			return false;
		}

		if (player.getInventory().freeSlots() > 0) {
			player.temp_stage = player.getSummoning().getPet().getStage();
			player.temp_hunger = player.getSummoning().getPet().getHunger();

			mob.remove();
			player.playAnimation(new Animation(827));
			player.getInventory().add(new Item(pets.itemId));
			player.getSummoning().reset();
			player.getSession().send(new SendSidebarInterfacePacket(16, -1));
			player.getSession().send(new SendOpenSideTabPacket(4));
			player.sendMessage("You pick up your pet.");
		} else {
			player.sendMessage("You do not have enough inventory space to do this.");
			return false;
		}

		return true;
	}

	/**
	 * Deploys the pet when dropped or login.
	 *
	 * @param player
	 * @param itemId
	 * @param slot
	 * @param login
	 * @return
	 */
	public static boolean spawnPet(Player player, int itemId, int slot, boolean login) {
		PetDefinition definition = PetDefinition.get(itemId);

		if (definition == null) {
			return false;
		}

		int mobId = definition.mobId;

		if (player.getSummoning().getPet() != null || player.getSummoning().getFamiliar() != null) {
			player.sendMessage("You already have a follower!");
			return false;
		}

		Location l = Constants.getClearAdjacentLocation(player.getLocation(), MobDefinition.get(mobId).getSize());

		if (l == null) {

			player.sendMessage("Not enough room to place your pet.");
			return false;
		}

		MobPet pet = new MobPet(mobId, l, definition);

		MobManager.spawnPet(player, pet);

		pet.update(player.temp_stage, player.temp_hunger);

		pet.getFollowing().setFollow(player, FollowType.DEFAULT);
		player.getSummoning().setFamiliarItem(itemId);

		player.getSession().send(new SendSidebarInterfacePacket(16, 19017));
		player.getSession().send(new SendStringPacket(pet.getDefinition().getName(), 19021));
		player.getSession().send(new SendMobHeadOnInterfacePacket(mobId, 19019));

		if (!login) {
			player.getSession().send(new SendOpenSideTabPacket(16));
		}

		if (!login) {
			player.getInventory().remove(slot, new Item(itemId));
			PlayerSaver.save(player);
		}

		return true;
	}

	/**
	 * Grows the player's pet to the next stage.
	 *
	 * @param player
	 */
	public static void growPet(Player player) {
		if (player.getSummoning().getPet() == null) {
			return;
		}

		PetDefinition def = PetDefinition.get(player.getSummoning().getPet().getPetDef().getNextStage());

		if (def == null) {
			return;
		}

		player.getSummoning().setFamiliarItem(def.getItemId());
		player.getSummoning().getPet().grow(def);
		player.getSummoning().sendTab(false);
	}

}